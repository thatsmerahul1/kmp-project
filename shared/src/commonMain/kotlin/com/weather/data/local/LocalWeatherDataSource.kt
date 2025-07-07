package com.weather.data.local

import com.weather.data.local.database.WeatherDao
import com.weather.data.mapper.WeatherMapper.toDomain
import com.weather.data.mapper.WeatherMapper.toEntity
import com.weather.domain.model.Weather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

interface LocalWeatherDataSource {
    suspend fun saveWeatherForecasts(forecasts: List<Weather>)
    suspend fun getWeatherForecasts(): List<Weather>
    fun getWeatherForecastsFlow(): Flow<List<Weather>>
    suspend fun isCacheValid(cacheExpiryHours: Double): Boolean
    suspend fun clearOldCache(cacheExpiryHours: Double)
    suspend fun clearAllCache()
}

class LocalWeatherDataSourceImpl(
    private val weatherDao: WeatherDao
) : LocalWeatherDataSource {

    override suspend fun saveWeatherForecasts(forecasts: List<Weather>) {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val entities = forecasts.map { it.toEntity(currentTime) }
        // Use a single transaction to clear and insert atomically
        weatherDao.replaceAllWeatherForecasts(entities)
    }

    override suspend fun getWeatherForecasts(): List<Weather> {
        val entities = weatherDao.getAllWeatherForecasts()
        return entities.map { it.toDomain() }
    }

    override fun getWeatherForecastsFlow(): Flow<List<Weather>> {
        return weatherDao.getAllWeatherForecastsFlow().map { forecasts ->
            forecasts.map { it.toDomain() }
        }
    }

    override suspend fun isCacheValid(cacheExpiryHours: Double): Boolean {
        val expiryTime = Clock.System.now().toEpochMilliseconds() - (cacheExpiryHours * 60 * 60 * 1000).toLong()
        println("LocalWeatherDataSource: Checking cache validity - expiry hours: $cacheExpiryHours, expiry time: $expiryTime")
        val isValid = weatherDao.isCacheValid(expiryTime)
        println("LocalWeatherDataSource: Cache valid: $isValid")
        return isValid
    }

    override suspend fun clearOldCache(cacheExpiryHours: Double) {
        val expiryTime = Clock.System.now().toEpochMilliseconds() - (cacheExpiryHours * 60 * 60 * 1000).toLong()
        weatherDao.deleteOlderThan(expiryTime)
    }

    override suspend fun clearAllCache() {
        weatherDao.deleteAll()
    }
}