package com.weather.domain.location

import com.weather.domain.model.LocationData
import kotlinx.coroutines.flow.Flow

/**
 * Platform-agnostic location manager interface
 */
interface LocationManager {
    /**
     * Request location permissions from the user
     * @return true if permissions are granted, false otherwise
     */
    suspend fun requestLocationPermission(): Boolean
    
    /**
     * Check if location permissions are granted
     */
    suspend fun hasLocationPermission(): Boolean
    
    /**
     * Get the current location
     * @return Current location or null if unavailable
     */
    suspend fun getCurrentLocation(): Location?
    
    /**
     * Get current location as LocationData with city information
     * @return LocationData with geocoded information or null if unavailable
     */
    suspend fun getCurrentLocationData(): LocationData?
    
    /**
     * Observe location updates
     * @return Flow of location updates
     */
    fun observeLocationUpdates(): Flow<Location>
    
    /**
     * Check if location services are enabled on the device
     */
    suspend fun isLocationEnabled(): Boolean
    
    /**
     * Open location settings for the user to enable location services
     */
    suspend fun openLocationSettings()
}

/**
 * Represents a geographic location
 */
data class Location(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float? = null,
    val timestamp: Long = 0L,
    val provider: String? = null,
    val cityName: String? = null,
    val countryName: String? = null
)

/**
 * Location-related exceptions
 */
sealed class LocationException : Exception() {
    object PermissionDenied : LocationException() {
        override val message = "Location permission denied"
    }
    
    object LocationDisabled : LocationException() {
        override val message = "Location services are disabled"
    }
    
    object Timeout : LocationException() {
        override val message = "Location request timed out"
    }
    
    data class Unknown(override val message: String) : LocationException()
}