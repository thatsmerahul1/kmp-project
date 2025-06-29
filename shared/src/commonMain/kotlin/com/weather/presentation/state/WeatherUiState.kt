package com.weather.presentation.state

import com.weather.domain.model.Weather
import kotlinx.datetime.Instant

data class WeatherUiState(
    val weatherList: List<Weather> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val lastUpdated: Instant? = null,
    val isOffline: Boolean = false
)