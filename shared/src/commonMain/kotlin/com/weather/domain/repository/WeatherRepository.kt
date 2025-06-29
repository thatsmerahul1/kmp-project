package com.weather.domain.repository

import com.weather.domain.model.Weather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getWeatherForecast(): Flow<Result<List<Weather>>>
    suspend fun refreshWeatherForecast(): Result<List<Weather>>
    suspend fun clearCache()
}