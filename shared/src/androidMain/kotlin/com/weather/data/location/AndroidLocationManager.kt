package com.weather.data.location

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager as SystemLocationManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.weather.domain.location.Location
import com.weather.domain.location.LocationException
import com.weather.domain.location.LocationManager
import com.weather.domain.model.LocationData
import com.weather.domain.service.GeocodingService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AndroidLocationManager(
    private val context: Context,
    private val geocodingService: GeocodingService
) : LocationManager {
    
    override suspend fun requestLocationPermission(): Boolean {
        // In Android, permission requests must be handled by the Activity
        // This method returns the current permission status
        return hasLocationPermission()
    }
    
    override suspend fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    override suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) {
            throw LocationException.PermissionDenied
        }
        
        if (!isLocationEnabled()) {
            throw LocationException.LocationDisabled
        }
        
        // For now, return null (simplified implementation)
        // Full implementation would use FusedLocationProviderClient
        return null
    }
    
    override suspend fun getCurrentLocationData(): LocationData? {
        println("AndroidLocationManager: Getting current location data...")
        
        val hasPermission = hasLocationPermission()
        println("AndroidLocationManager: Has location permission: $hasPermission")
        
        if (!hasPermission) {
            println("AndroidLocationManager: No location permission - need to request at runtime")
            // For now, we'll continue with simulated location even without permission for testing
            // In production, this should return null or request permissions
        }
        
        val locationEnabled = isLocationEnabled()
        println("AndroidLocationManager: Location services enabled: $locationEnabled")
        
        if (!locationEnabled) {
            println("AndroidLocationManager: Location services disabled")
            // For testing, we'll continue even if location services are disabled
        }
        
        println("AndroidLocationManager: Simulating GPS location detection...")
        
        // TODO: Replace with actual FusedLocationProviderClient implementation
        // For now, simulate getting GPS coordinates and reverse geocoding them
        try {
            // Simulate getting GPS coordinates (in a real app, this would come from FusedLocationProviderClient)
            // Using Varanasi coordinates for testing (you can change these to any location)
            val simulatedLatitude = 25.3176 // Varanasi, Uttar Pradesh, India
            val simulatedLongitude = 82.9739
            
            println("AndroidLocationManager: Simulated GPS coordinates: $simulatedLatitude, $simulatedLongitude")
            
            // Try to reverse geocode the coordinates to get city information
            val geocodedLocation = geocodingService.reverseGeocode(simulatedLatitude, simulatedLongitude)
            
            if (geocodedLocation != null) {
                println("AndroidLocationManager: Geocoded location: ${geocodedLocation.displayName}")
                return geocodedLocation.copy(
                    isCurrentLocation = true,
                    accuracy = 10.0f, // Simulated accuracy
                    timestamp = System.currentTimeMillis()
                )
            } else {
                println("AndroidLocationManager: Geocoding failed, creating basic location data")
                // If geocoding fails, create basic location data
                return LocationData(
                    latitude = simulatedLatitude,
                    longitude = simulatedLongitude,
                    cityName = "Current Location",
                    isCurrentLocation = true,
                    accuracy = 10.0f,
                    timestamp = System.currentTimeMillis()
                )
            }
        } catch (e: Exception) {
            println("AndroidLocationManager: Error getting location: ${e.message}")
            return null
        }
    }
    
    override fun observeLocationUpdates(): Flow<Location> = flow {
        // For now, empty flow (simplified implementation)
        // Full implementation would use FusedLocationProviderClient
    }
    
    override suspend fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as SystemLocationManager
        return locationManager.isProviderEnabled(SystemLocationManager.GPS_PROVIDER) ||
               locationManager.isProviderEnabled(SystemLocationManager.NETWORK_PROVIDER)
    }
    
    override suspend fun openLocationSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}