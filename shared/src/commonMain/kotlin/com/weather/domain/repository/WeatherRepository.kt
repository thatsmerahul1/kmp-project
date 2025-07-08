package com.weather.domain.repository

import com.weather.domain.model.Weather
import com.weather.domain.model.LocationData
import com.weather.domain.common.Result
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getWeatherForecast(location: LocationData): Flow<Result<List<Weather>>>
    suspend fun refreshWeatherForecast(location: LocationData): Result<List<Weather>>
    suspend fun clearCache()
}