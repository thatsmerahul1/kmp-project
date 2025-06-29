package com.weather.android.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weather.presentation.state.WeatherUiEvent
import com.weather.presentation.state.WeatherUiState
import com.weather.presentation.viewmodel.WeatherViewModel as SharedWeatherViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModelBridge(
    private val sharedViewModel: SharedWeatherViewModel
) : ViewModel() {
    
    val uiState: StateFlow<WeatherUiState> = sharedViewModel.uiState
    
    fun onEvent(event: WeatherUiEvent) {
        sharedViewModel.onEvent(event)
    }
    
    override fun onCleared() {
        super.onCleared()
        sharedViewModel.onCleared()
    }
}