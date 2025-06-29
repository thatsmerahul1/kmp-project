package com.weather.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherItemDto(
    @SerialName("dt")
    val timestamp: Long,
    @SerialName("main")
    val main: MainDto,
    @SerialName("weather")
    val weather: List<WeatherInfoDto>,
    @SerialName("dt_txt")
    val dateText: String
)

@Serializable
data class MainDto(
    @SerialName("temp")
    val temperature: Double,
    @SerialName("temp_min")
    val temperatureMin: Double,
    @SerialName("temp_max")
    val temperatureMax: Double,
    @SerialName("humidity")
    val humidity: Int
)

@Serializable
data class WeatherInfoDto(
    @SerialName("main")
    val main: String,
    @SerialName("description")
    val description: String,
    @SerialName("icon")
    val icon: String
)