package com.weather.data.remote.api

import com.weather.data.remote.dto.WeatherResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

interface WeatherApi {
    suspend fun getWeatherForecast(
        location: String,
        apiKey: String,
        days: Int = 7,
        units: String = "metric"
    ): WeatherResponseDto
}

class WeatherApiImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : WeatherApi {

    override suspend fun getWeatherForecast(
        location: String,
        apiKey: String,
        days: Int,
        units: String
    ): WeatherResponseDto {
        return httpClient.get("$baseUrl/forecast") {
            parameter("q", location)
            parameter("appid", apiKey)
            parameter("cnt", days * 8) // 8 forecasts per day (every 3 hours)
            parameter("units", units)
        }.body()
    }
}