package com.weather.presentation.viewmodel

import com.weather.di.KoinHelper
import com.weather.presentation.state.WeatherUiEvent
import com.weather.presentation.state.WeatherUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow

class IOSWeatherViewModel {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val weatherViewModel = WeatherViewModel(
        KoinHelper.get(),
        KoinHelper.get()
    )
    
    val uiState: StateFlow<WeatherUiState> = weatherViewModel.uiState
    
    fun onEvent(event: WeatherUiEvent) {
        weatherViewModel.onEvent(event)
    }
    
    // Convenience methods for Swift
    fun loadWeather() {
        weatherViewModel.onEvent(WeatherUiEvent.LoadWeather)
    }
    
    fun refreshWeather() {
        weatherViewModel.onEvent(WeatherUiEvent.RefreshWeather)
    }
    
    fun retryLoad() {
        weatherViewModel.onEvent(WeatherUiEvent.RetryLoad)
    }
    
    fun dispose() {
        weatherViewModel.onCleared()
        viewModelScope.cancel()
    }
}