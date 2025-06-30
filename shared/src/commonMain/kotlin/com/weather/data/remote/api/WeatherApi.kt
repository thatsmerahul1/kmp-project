package com.weather.data.remote.api

import com.weather.data.remote.dto.WeatherResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

interface WeatherApi {
    suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        days: Int = 7
    ): WeatherResponseDto
}

class WeatherApiImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://api.open-meteo.com/v1"
) : WeatherApi {

    override suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        days: Int
    ): WeatherResponseDto {
        return httpClient.get("$baseUrl/forecast") {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            parameter("daily", "weather_code,temperature_2m_max,temperature_2m_min,relative_humidity_2m_mean,wind_speed_10m_max")
            parameter("forecast_days", days)
            parameter("timezone", "auto")
        }.body()
    }
}