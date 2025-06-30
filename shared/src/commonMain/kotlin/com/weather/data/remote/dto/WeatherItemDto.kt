package com.weather.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class WeatherItemDto(
    val date: String,
    val temperatureMax: Double,
    val temperatureMin: Double,
    val humidity: Int,
    val weatherCode: Int,
    val windSpeed: Double
)