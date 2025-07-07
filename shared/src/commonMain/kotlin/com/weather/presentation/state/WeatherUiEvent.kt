package com.weather.presentation.state

import com.weather.domain.model.LocationData

sealed class WeatherUiEvent {
    object LoadWeather : WeatherUiEvent()
    object RefreshWeather : WeatherUiEvent()
    object RetryLoad : WeatherUiEvent()
    object ClearError : WeatherUiEvent()
    object ClearLocationError : WeatherUiEvent()
    object ShowLocationPicker : WeatherUiEvent()
    object HideLocationPicker : WeatherUiEvent()
    object RequestCurrentLocation : WeatherUiEvent()
    data class SelectLocation(val location: LocationData) : WeatherUiEvent()
    data class SearchLocations(val query: String) : WeatherUiEvent()
}