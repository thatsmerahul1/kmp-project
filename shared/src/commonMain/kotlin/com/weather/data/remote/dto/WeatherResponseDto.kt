package com.weather.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponseDto(
    @SerialName("list")
    val list: List<WeatherItemDto>,
    @SerialName("city")
    val city: CityDto
)

@Serializable
data class CityDto(
    @SerialName("name")
    val name: String,
    @SerialName("country")
    val country: String
)