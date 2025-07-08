package com.weather.presentation.viewmodel

import com.weather.domain.usecase.GetWeatherForecastUseCase
import com.weather.domain.usecase.RefreshWeatherUseCase
import com.weather.domain.common.Result
import com.weather.domain.service.LocationService
import com.weather.domain.permission.PermissionManager
import com.weather.domain.permission.Permission
import com.weather.domain.permission.PermissionStatus
import com.weather.presentation.state.WeatherUiEvent
import com.weather.presentation.state.WeatherUiState
import com.weather.presentation.state.ConnectionStatus
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
    private val refreshWeatherUseCase: RefreshWeatherUseCase,
    private val locationService: LocationService,
    private val permissionManager: PermissionManager
) {
    
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        // Request permissions first, then load location and weather
        requestInitialPermissions()
    }

    fun onEvent(event: WeatherUiEvent) {
        when (event) {
            is WeatherUiEvent.LoadWeather -> loadWeather()
            is WeatherUiEvent.RefreshWeather -> refreshWeather()
            is WeatherUiEvent.RetryLoad -> loadWeather()
            is WeatherUiEvent.ClearError -> clearError()
            is WeatherUiEvent.ClearLocationError -> clearLocationError()
            is WeatherUiEvent.ShowLocationPicker -> showLocationPicker()
            is WeatherUiEvent.HideLocationPicker -> hideLocationPicker()
            is WeatherUiEvent.RequestCurrentLocation -> requestCurrentLocation()
            is WeatherUiEvent.SelectLocation -> selectLocation(event.location)
            is WeatherUiEvent.SearchLocations -> searchLocations(event.query)
        }
    }

    private fun loadWeather() {
        viewModelScope.launch {
            println("WeatherViewModel: Loading weather...")
            val currentLocation = _uiState.value.currentLocation
            if (currentLocation == null) {
                println("WeatherViewModel: No current location available, loading location first...")
                loadCurrentLocation()
                return@launch
            }
            
            println("WeatherViewModel: Loading weather for location: ${currentLocation.displayName}")
            getWeatherForecastUseCase(currentLocation)
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
                        connectionStatus = ConnectionStatus.OFFLINE
                    )
                }
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.value = _uiState.value.copy(
                                weatherList = result.data,
                                isLoading = false,
                                error = null,
                                lastUpdated = Clock.System.now(),
                                connectionStatus = ConnectionStatus.CONNECTED
                            )
                        }
                        is Result.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = result.exception.message ?: "Unknown error occurred",
                                connectionStatus = ConnectionStatus.OFFLINE
                            )
                        }
                        is Result.Loading -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = true,
                                error = null
                            )
                        }
                    }
                }
        }
    }

    private fun refreshWeather() {
        viewModelScope.launch {
            println("WeatherViewModel: Refreshing weather...")
            val currentLocation = _uiState.value.currentLocation
            if (currentLocation == null) {
                println("WeatherViewModel: No current location available for refresh")
                return@launch
            }
            
            _uiState.value = _uiState.value.copy(
                isRefreshing = true,
                error = null
            )
            
            println("WeatherViewModel: Refreshing weather for location: ${currentLocation.displayName}")
            val result = refreshWeatherUseCase(currentLocation)
            when (result) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        weatherList = result.data,
                        isRefreshing = false,
                        lastUpdated = Clock.System.now(),
                        connectionStatus = ConnectionStatus.CONNECTED
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        error = result.exception.message ?: "Failed to refresh weather data",
                        connectionStatus = ConnectionStatus.OFFLINE
                    )
                }
                is Result.Loading -> {
                    // Already in refreshing state, no additional action needed
                }
            }
        }
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    private fun clearLocationError() {
        _uiState.value = _uiState.value.copy(locationError = null)
    }
    
    private fun showLocationPicker() {
        _uiState.value = _uiState.value.copy(showLocationPicker = true)
    }
    
    private fun hideLocationPicker() {
        _uiState.value = _uiState.value.copy(showLocationPicker = false)
    }
    
    private fun loadCurrentLocation() {
        viewModelScope.launch {
            try {
                println("WeatherViewModel: Loading current location...")
                _uiState.value = _uiState.value.copy(isLocationLoading = true, locationError = null)
                val location = locationService.getCurrentLocation()
                println("WeatherViewModel: Current location loaded: ${location.displayName} (${location.latitude}, ${location.longitude})")
                _uiState.value = _uiState.value.copy(
                    currentLocation = location,
                    isLocationLoading = false
                )
                
                // Auto-load weather data for the new location
                loadWeather()
            } catch (e: Exception) {
                println("WeatherViewModel: Failed to load location: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLocationLoading = false,
                    locationError = e.message ?: "Failed to get location"
                )
            }
        }
    }
    
    private fun requestCurrentLocation() {
        viewModelScope.launch {
            try {
                println("WeatherViewModel: Requesting current location...")
                _uiState.value = _uiState.value.copy(isLocationLoading = true, locationError = null)
                
                // Try to get GPS location first
                println("WeatherViewModel: Trying to get GPS location...")
                val gpsLocation = locationService.getLocationBySource(
                    com.weather.domain.model.LocationSource.GPS
                )
                
                val location = gpsLocation ?: locationService.getDefaultLocation()
                println("WeatherViewModel: Final location selected: ${location.displayName} (GPS: ${gpsLocation != null})")
                
                // Update current location and refresh weather data
                locationService.setUserSelectedLocation(location)
                _uiState.value = _uiState.value.copy(
                    currentLocation = location,
                    isLocationLoading = false,
                    showLocationPicker = false
                )
                
                // Refresh weather data for new location
                println("WeatherViewModel: Refreshing weather for new location...")
                loadWeather()
            } catch (e: Exception) {
                println("WeatherViewModel: Failed to request current location: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLocationLoading = false,
                    locationError = e.message ?: "Failed to get current location"
                )
            }
        }
    }
    
    private fun selectLocation(location: com.weather.domain.model.LocationData) {
        viewModelScope.launch {
            try {
                locationService.setUserSelectedLocation(location)
                _uiState.value = _uiState.value.copy(
                    currentLocation = location,
                    showLocationPicker = false,
                    locationError = null
                )
                
                // Refresh weather data for new location
                loadWeather()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    locationError = e.message ?: "Failed to select location"
                )
            }
        }
    }
    
    private fun searchLocations(query: String) {
        viewModelScope.launch {
            try {
                val results = locationService.searchLocations(query)
                // Results would be handled in the UI layer
                // This is just a placeholder for search functionality
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    locationError = e.message ?: "Failed to search locations"
                )
            }
        }
    }

    private fun requestInitialPermissions() {
        viewModelScope.launch {
            try {
                println("WeatherViewModel: Requesting initial permissions...")
                
                // Request all necessary permissions
                val requiredPermissions = listOf(
                    Permission.LOCATION_FINE,
                    Permission.LOCATION_COARSE,
                    Permission.INTERNET,
                    Permission.NETWORK_STATE
                )
                
                val permissionResults = permissionManager.requestPermissions(requiredPermissions)
                
                permissionResults.forEach { (permission, status) ->
                    println("WeatherViewModel: Permission $permission status: $status")
                }
                
                // Check if we have location permissions
                val hasLocationPermission = permissionManager.hasLocationPermission()
                println("WeatherViewModel: Has location permission: $hasLocationPermission")
                
                // Load location regardless of permission status (will use fallback if needed)
                loadCurrentLocation()
                
            } catch (e: Exception) {
                println("WeatherViewModel: Failed to request permissions: ${e.message}")
                // Continue loading even if permission request fails
                loadCurrentLocation()
            }
        }
    }

    fun onCleared() {
        viewModelScope.cancel()
    }
}