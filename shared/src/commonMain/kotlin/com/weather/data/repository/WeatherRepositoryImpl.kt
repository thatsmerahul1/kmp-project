package com.weather.data.repository

import com.weather.data.local.LocalWeatherDataSource
import com.weather.data.local.preferences.AppPreferences
import com.weather.data.remote.RemoteWeatherDataSource
import com.weather.domain.model.Weather
import com.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepositoryImpl(
    private val localDataSource: LocalWeatherDataSource,
    private val remoteDataSource: RemoteWeatherDataSource,
    private val appPreferences: AppPreferences
) : WeatherRepository {

    override fun getWeatherForecast(): Flow<Result<List<Weather>>> = flow {
        try {
            val cacheConfig = appPreferences.getCacheConfig()
            
            // 1. First, emit cached data if available
            val cachedData = localDataSource.getWeatherForecasts()
            if (cachedData.isNotEmpty()) {
                emit(Result.success(cachedData))
            }
            
            // 2. Check if cache is valid, but always try to fetch fresh data
            val isCacheValid = localDataSource.isCacheValid(cacheConfig.cacheExpiryHours)
            
            // 3. Fetch fresh data from remote (always attempt for offline-first strategy)
            try {
                val location = appPreferences.getDefaultLocation()
                
                val freshData = remoteDataSource.getWeatherForecast(location)
                
                // 4. Save fresh data to cache
                localDataSource.saveWeatherForecasts(freshData)
                
                // 5. Emit updated data from the database
                val updatedCache = localDataSource.getWeatherForecasts()
                if (!isCacheValid || updatedCache != cachedData) {
                    emit(Result.success(updatedCache))
                }
            } catch (networkException: Exception) {
                // 6. If network fails and we have no cached data, emit error
                if (cachedData.isEmpty()) {
                    emit(Result.failure(networkException))
                }
                // If we have cached data, we already emitted it above, so we don't emit error
            }
        } catch (exception: Exception) {
            emit(Result.failure(exception))
        }
    }

    override suspend fun refreshWeatherForecast(): Result<List<Weather>> {
        return try {
            val location = appPreferences.getDefaultLocation()
            
            val freshData = remoteDataSource.getWeatherForecast(location)
            localDataSource.saveWeatherForecasts(freshData)

            // Return refreshed data from the local source
            Result.success(localDataSource.getWeatherForecasts())
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun clearCache() {
        localDataSource.clearAllCache()
    }
}