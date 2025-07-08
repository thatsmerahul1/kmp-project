package com.weather.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class WeatherItemDto(
    val date: String,
    val temperatureMax: Double,
    val temperatureMin: Double,
    val temperatureCurrent: Double? = null,
    val humidity: Int,
    val weatherCode: Int,
    val windSpeed: Double,
    // Enhanced weather fields
    val pressure: Double? = null,
    val windDirection: Int? = null,
    val visibility: Double? = null,
    val uvIndex: Int? = null,
    val precipitationChance: Int? = null,
    val precipitationAmount: Double? = null,
    val cloudCover: Int? = null,
    val feelsLike: Double? = null,
    val dewPoint: Double? = null,
    val sunrise: String? = null,
    val sunset: String? = null
)