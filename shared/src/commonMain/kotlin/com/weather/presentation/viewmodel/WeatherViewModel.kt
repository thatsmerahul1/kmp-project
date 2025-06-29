package com.weather.presentation.viewmodel

import com.weather.domain.usecase.GetWeatherForecastUseCase
import com.weather.domain.usecase.RefreshWeatherUseCase
import com.weather.presentation.state.WeatherUiEvent
import com.weather.presentation.state.WeatherUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class WeatherViewModel(
    private val getWeatherForecastUseCase: GetWeatherForecastUseCase,
    private val refreshWeatherUseCase: RefreshWeatherUseCase
) {
    
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        loadWeather()
    }

    fun onEvent(event: WeatherUiEvent) {
        when (event) {
            is WeatherUiEvent.LoadWeather -> loadWeather()
            is WeatherUiEvent.RefreshWeather -> refreshWeather()
            is WeatherUiEvent.RetryLoad -> loadWeather()
            is WeatherUiEvent.ClearError -> clearError()
        }
    }

    private fun loadWeather() {
        viewModelScope.launch {
            getWeatherForecastUseCase()
                .onStart {
                    _uiState.value = _uiState.value.copy(
                        isLoading = _uiState.value.weatherList.isEmpty(),
                        error = null
                    )
                }
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error occurred",
                        isOffline = true
                    )
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { weatherList ->
                            _uiState.value = _uiState.value.copy(
                                weatherList = weatherList,
                                isLoading = false,
                                error = null,
                                lastUpdated = Clock.System.now(),
                                isOffline = false
                            )
                        },
                        onFailure = { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = exception.message ?: "Unknown error occurred",
                                isOffline = true
                            )
                        }
                    )
                }
        }
    }

    private fun refreshWeather() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isRefreshing = true,
                error = null
            )
            
            val result = refreshWeatherUseCase()
            result.fold(
                onSuccess = { weatherList ->
                    _uiState.value = _uiState.value.copy(
                        weatherList = weatherList,
                        isRefreshing = false,
                        lastUpdated = Clock.System.now(),
                        isOffline = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        error = exception.message ?: "Failed to refresh weather data",
                        isOffline = true
                    )
                }
            )
        }
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun onCleared() {
        viewModelScope.cancel()
    }
}