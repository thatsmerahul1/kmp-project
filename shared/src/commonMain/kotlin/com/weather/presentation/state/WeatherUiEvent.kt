package com.weather.presentation.state

sealed class WeatherUiEvent {
    object LoadWeather : WeatherUiEvent()
    object RefreshWeather : WeatherUiEvent()
    object RetryLoad : WeatherUiEvent()
    object ClearError : WeatherUiEvent()
}