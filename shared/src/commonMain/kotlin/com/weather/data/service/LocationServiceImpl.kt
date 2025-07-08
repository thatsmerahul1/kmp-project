package com.weather.data.service

import com.weather.data.local.preferences.AppPreferences
import com.weather.domain.location.LocationManager
import com.weather.domain.model.LocationData
import com.weather.domain.model.LocationSearchResult
import com.weather.domain.model.LocationSource
import com.weather.domain.service.GeocodingService
import com.weather.domain.service.LocationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class LocationServiceImpl(
    private val locationManager: LocationManager,
    private val geocodingService: GeocodingService,
    private val appPreferences: AppPreferences,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : LocationService {

    companion object {
        private const val USER_LOCATION_KEY = "user_selected_location"
        
        // Default fallback location - Bengaluru
        private val DEFAULT_LOCATION = LocationData(
            latitude = 12.9716,
            longitude = 77.5946,
            cityName = "Bengaluru",
            countryName = "India",
            state = "Karnataka",
            displayName = "Bengaluru, Karnataka, India"
        )
    }

    override suspend fun getCurrentLocation(): LocationData {
        // Priority: User Selected -> GPS -> Default (Bengaluru)
        
        println("LocationService: Getting current location...")
        
        // 1. Check if user has selected a location
        getUserSelectedLocation()?.let { 
            println("LocationService: Using user selected location: ${it.displayName}")
            return it 
        }
        
        // 2. Try to get GPS location
        try {
            println("LocationService: Checking GPS availability...")
            if (locationManager.hasLocationPermission() && locationManager.isLocationEnabled()) {
                println("LocationService: GPS available, getting current location...")
                locationManager.getCurrentLocationData()?.let { 
                    println("LocationService: GPS location found: ${it.displayName} (${it.latitude}, ${it.longitude})")
                    return it 
                }
            } else {
                println("LocationService: GPS not available - permission: ${locationManager.hasLocationPermission()}, enabled: ${locationManager.isLocationEnabled()}")
            }
        } catch (e: Exception) {
            println("LocationService: GPS failed with error: ${e.message}")
        }
        
        // 3. Return default location (Bengaluru)
        val defaultLocation = getDefaultLocation()
        println("LocationService: Using default location: ${defaultLocation.displayName}")
        return defaultLocation
    }

    override suspend fun getLocationBySource(source: LocationSource): LocationData? {
        return when (source) {
            is LocationSource.GPS -> {
                try {
                    println("LocationService: Getting GPS location by source...")
                    if (locationManager.hasLocationPermission() && locationManager.isLocationEnabled()) {
                        val location = locationManager.getCurrentLocationData()
                        println("LocationService: GPS by source result: ${location?.displayName ?: "null"}")
                        location
                    } else {
                        println("LocationService: GPS by source - no permission or not enabled")
                        null
                    }
                } catch (e: Exception) {
                    println("LocationService: GPS by source failed: ${e.message}")
                    null
                }
            }
            is LocationSource.UserSelected -> source.location
            is LocationSource.Search -> {
                val results = geocodingService.searchLocations(source.query, 1)
                results.firstOrNull()?.locationData
            }
            is LocationSource.Default -> getDefaultLocation()
        }
    }

    override suspend fun searchLocations(query: String): List<LocationSearchResult> {
        return geocodingService.searchLocations(query.trim(), 10)
    }

    override suspend fun setUserSelectedLocation(location: LocationData) {
        try {
            val locationJson = json.encodeToString(location)
            // Store in preferences - for now using default location as fallback
            // In real implementation, this would use platform-specific storage
            appPreferences.setDefaultLocation("${location.latitude},${location.longitude}")
            
            // TODO: Add proper user preferences storage
        } catch (e: Exception) {
            // Handle serialization error
        }
    }

    override suspend fun getUserSelectedLocation(): LocationData? {
        return try {
            // TODO: Implement proper user preferences storage
            // For now, check if default location is not Bengaluru
            val defaultLocationStr = appPreferences.getDefaultLocation()
            if (defaultLocationStr != "Bengaluru,India" && defaultLocationStr.contains(",")) {
                val coords = defaultLocationStr.split(",")
                if (coords.size >= 2) {
                    val lat = coords[0].toDoubleOrNull()
                    val lng = coords[1].toDoubleOrNull()
                    if (lat != null && lng != null) {
                        geocodingService.reverseGeocode(lat, lng)
                    } else null
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun clearUserSelectedLocation() {
        appPreferences.setDefaultLocation("Bengaluru,India")
    }

    override fun observeCurrentLocation(): Flow<LocationData> = flow {
        emit(getCurrentLocation())
        
        // TODO: Implement proper location observation
        // This would observe changes in user preferences and GPS location
    }

    override suspend fun getDefaultLocation(): LocationData {
        return DEFAULT_LOCATION
    }
}