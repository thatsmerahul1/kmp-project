package com.weather.domain.model

import kotlinx.datetime.LocalDate

data class Weather(
    val date: LocalDate,
    val condition: WeatherCondition,
    val temperatureHigh: Double,
    val temperatureLow: Double,
    val humidity: Int,
    val icon: String,
    val description: String
)