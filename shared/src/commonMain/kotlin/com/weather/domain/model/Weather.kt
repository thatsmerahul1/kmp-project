package com.weather.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Weather(
    val date: LocalDate,
    val condition: WeatherCondition,
    val temperatureHigh: Double,
    val temperatureLow: Double,
    val temperatureCurrent: Double? = null,
    val humidity: Int,
    val icon: String,
    val description: String,
    // Enhanced weather details
    val pressure: Double? = null, // in hPa
    val windSpeed: Double? = null, // in km/h
    val windDirection: Int? = null, // in degrees
    val visibility: Double? = null, // in km
    val uvIndex: Int? = null, // UV index 0-11+
    val precipitationChance: Int? = null, // percentage
    val precipitationAmount: Double? = null, // in mm
    val cloudCover: Int? = null, // percentage
    val feelsLike: Double? = null, // feels like temperature
    val dewPoint: Double? = null, // dew point temperature
    val sunrise: String? = null, // sunrise time
    val sunset: String? = null, // sunset time
    val moonPhase: String? = null, // moon phase
    val airQuality: AirQuality? = null
)

@Serializable
data class AirQuality(
    val aqi: Int, // Air Quality Index
    val category: AirQualityCategory,
    val pm25: Double? = null, // PM2.5 concentration
    val pm10: Double? = null, // PM10 concentration
    val co: Double? = null, // Carbon monoxide
    val no2: Double? = null, // Nitrogen dioxide
    val so2: Double? = null, // Sulfur dioxide
    val o3: Double? = null // Ozone
)

@Serializable
enum class AirQualityCategory {
    GOOD,
    MODERATE,
    UNHEALTHY_FOR_SENSITIVE,
    UNHEALTHY,
    VERY_UNHEALTHY,
    HAZARDOUS
}