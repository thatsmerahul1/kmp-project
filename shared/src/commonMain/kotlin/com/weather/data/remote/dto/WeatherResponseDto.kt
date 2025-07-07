package com.weather.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponseDto(
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("timezone")
    val timezone: String,
    @SerialName("daily")
    val daily: DailyDto
)

@Serializable
data class DailyDto(
    @SerialName("time")
    val time: List<String>,
    @SerialName("weather_code")
    val weatherCode: List<Int>,
    @SerialName("temperature_2m_max")
    val temperatureMax: List<Double>,
    @SerialName("temperature_2m_min")
    val temperatureMin: List<Double>,
    @SerialName("temperature_2m_mean")
    val temperatureCurrent: List<Double>? = null,
    @SerialName("relative_humidity_2m_mean")
    val humidity: List<Int>,
    @SerialName("surface_pressure_mean")
    val pressure: List<Double>? = null,
    @SerialName("wind_speed_10m_max")
    val windSpeed: List<Double>,
    @SerialName("wind_direction_10m_dominant")
    val windDirection: List<Int>? = null,
    @SerialName("visibility_mean")
    val visibility: List<Double>? = null,
    @SerialName("uv_index_max")
    val uvIndex: List<Int>? = null,
    @SerialName("precipitation_probability_max")
    val precipitationChance: List<Int>? = null,
    @SerialName("precipitation_sum")
    val precipitationAmount: List<Double>? = null,
    @SerialName("cloud_cover_mean")
    val cloudCover: List<Int>? = null,
    @SerialName("apparent_temperature_max")
    val feelsLike: List<Double>? = null,
    @SerialName("dew_point_2m_mean")
    val dewPoint: List<Double>? = null,
    @SerialName("sunrise")
    val sunrise: List<String>? = null,
    @SerialName("sunset")
    val sunset: List<String>? = null
)