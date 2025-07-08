package com.weather.presentation.state

import com.weather.domain.model.Weather
import com.weather.domain.model.LocationData
import kotlinx.datetime.Instant

data class WeatherUiState(
    val weatherList: List<Weather> = emptyList(),
    val currentLocation: LocationData? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLocationLoading: Boolean = false,
    val error: String? = null,
    val locationError: String? = null,
    val lastUpdated: Instant? = null,
    val isOffline: Boolean = false,
    val showLocationPicker: Boolean = false,
    val connectionStatus: ConnectionStatus = ConnectionStatus.UNKNOWN
)

enum class ConnectionStatus {
    CONNECTED,
    OFFLINE,
    UNKNOWN
}