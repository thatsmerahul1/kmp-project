package com.weather.data.remote

import com.weather.data.mapper.WeatherMapper.toDomain
import com.weather.data.remote.api.WeatherApi
import com.weather.domain.model.Weather
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface RemoteWeatherDataSource {
    suspend fun getWeatherForecast(
        location: String,
        apiKey: String
    ): List<Weather>
}

class RemoteWeatherDataSourceImpl(
    private val weatherApi: WeatherApi
) : RemoteWeatherDataSource {

    override suspend fun getWeatherForecast(
        location: String,
        apiKey: String
    ): List<Weather> {
        val response = weatherApi.getWeatherForecast(
            location = location,
            apiKey = apiKey,
            days = 7,
            units = "metric"
        )
        
        // Group by date and take the first forecast for each day
        // OpenWeatherMap 5-day forecast gives data every 3 hours
        val dailyForecasts = response.list
            .groupBy { forecast ->
                // Extract date from timestamp
                val instant = Instant.fromEpochSeconds(forecast.timestamp)
                instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
            }
            .map { (_, forecasts) ->
                // Take the first forecast for each day (usually around noon)
                forecasts.first().toDomain()
            }
            .sortedBy { it.date }
            .take(7) // Ensure we only take 7 days
        
        return dailyForecasts
    }
}