package com.weather.presentation.viewmodel

import com.weather.domain.usecase.GetWeatherForecastUseCase
import com.weather.domain.usecase.RefreshWeatherUseCase
import com.weather.presentation.state.WeatherUiEvent
import com.weather.presentation.state.WeatherUiState
import com.weather.utils.FlowWrapper
import com.weather.utils.wrap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class IOSWeatherViewModel(
    getWeatherForecastUseCase: GetWeatherForecastUseCase,
    refreshWeatherUseCase: RefreshWeatherUseCase
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val weatherViewModel = WeatherViewModel(getWeatherForecastUseCase, refreshWeatherUseCase)
    
    val uiStateFlow: FlowWrapper<WeatherUiState> = weatherViewModel.uiState.wrap()
    
    fun onEvent(event: WeatherUiEvent) {
        weatherViewModel.onEvent(event)
    }
    
    fun dispose() {
        uiStateFlow.unsubscribe()
        weatherViewModel.onCleared()
        viewModelScope.cancel()
    }
}