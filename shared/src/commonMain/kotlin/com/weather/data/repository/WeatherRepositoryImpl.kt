package com.weather.data.repository

import com.weather.data.local.LocalWeatherDataSource
import com.weather.data.local.preferences.AppPreferences
import com.weather.data.remote.RemoteWeatherDataSource
import com.weather.domain.model.Weather
import com.weather.domain.model.LocationData
import com.weather.domain.repository.WeatherRepository
import com.weather.domain.common.Result
import com.weather.domain.common.DomainException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepositoryImpl(
    private val localDataSource: LocalWeatherDataSource,
    private val remoteDataSource: RemoteWeatherDataSource,
    private val appPreferences: AppPreferences
) : WeatherRepository {

    override fun getWeatherForecast(location: LocationData): Flow<Result<List<Weather>>> = flow {
        try {
            println("WeatherRepository: Getting weather forecast for location: ${location.displayName}")
            val cacheConfig = appPreferences.getCacheConfig()
            
            // 1. First, emit cached data if available
            val cachedData = localDataSource.getWeatherForecasts()
            if (cachedData.isNotEmpty()) {
                println("WeatherRepository: Found cached data with ${cachedData.size} items")
                emit(Result.Success(cachedData))
            }
            
            // 2. Check if cache is valid based on 60-second expiry
            val isCacheValid = localDataSource.isCacheValid(cacheConfig.cacheExpiryHours)
            println("WeatherRepository: Cache valid: $isCacheValid, expiry hours: ${cacheConfig.cacheExpiryHours}")
            
            // 3. Fetch fresh data from remote using current location
            try {
                val locationString = "${location.latitude},${location.longitude}"
                println("WeatherRepository: Fetching fresh data for coordinates: $locationString")
                
                val freshData = remoteDataSource.getWeatherForecast(locationString)
                
                // 4. Save fresh data to cache
                localDataSource.saveWeatherForecasts(freshData)
                
                // 5. Emit updated data from the database
                val updatedCache = localDataSource.getWeatherForecasts()
                if (!isCacheValid || updatedCache != cachedData) {
                    emit(Result.Success(updatedCache))
                }
            } catch (networkException: Exception) {
                // 6. If network fails and we have no cached data, emit error
                if (cachedData.isEmpty()) {
                    emit(Result.Error(DomainException.Network.Generic(networkException.message ?: "Network error")))
                }
                // If we have cached data, we already emitted it above, so we don't emit error
            }
        } catch (exception: Exception) {
            emit(Result.Error(DomainException.Unknown(exception.message ?: "Unknown error")))
        }
    }

    override suspend fun refreshWeatherForecast(location: LocationData): Result<List<Weather>> {
        return try {
            val locationString = "${location.latitude},${location.longitude}"
            println("WeatherRepository: Refreshing weather forecast for coordinates: $locationString")
            
            val freshData = remoteDataSource.getWeatherForecast(locationString)
            localDataSource.saveWeatherForecasts(freshData)

            // Return refreshed data from the local source
            Result.Success(localDataSource.getWeatherForecasts())
        } catch (exception: Exception) {
            println("WeatherRepository: Refresh failed: ${exception.message}")
            Result.Error(DomainException.Network.Generic(exception.message ?: "Refresh failed"))
        }
    }

    override suspend fun clearCache() {
        localDataSource.clearAllCache()
    }
}