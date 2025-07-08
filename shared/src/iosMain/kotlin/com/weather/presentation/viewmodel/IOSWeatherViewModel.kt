package com.weather.presentation.viewmodel

import com.weather.di.KoinHelper
import com.weather.presentation.state.WeatherUiEvent
import com.weather.presentation.state.WeatherUiState
import com.weather.utils.StateFlowWrapper
import com.weather.utils.wrapForIOS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow

class IOSWeatherViewModel {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val weatherViewModel = WeatherViewModel(
        KoinHelper.get(),
        KoinHelper.get(),
        KoinHelper.get(),
        KoinHelper.get()
    )
    
    val uiState: StateFlow<WeatherUiState> = weatherViewModel.uiState
    
    // StateFlow wrapper for easy iOS/SwiftUI integration
    val uiStateWrapper: StateFlowWrapper<WeatherUiState> = weatherViewModel.uiState.wrapForIOS(viewModelScope)
    
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
    
    fun clearError() {
        weatherViewModel.onEvent(WeatherUiEvent.ClearError)
    }
    
    // Get current state value synchronously
    fun getCurrentState(): WeatherUiState {
        return uiState.value
    }
    
    fun dispose() {
        uiStateWrapper.unsubscribe()
        weatherViewModel.onCleared()
        viewModelScope.cancel()
    }
}