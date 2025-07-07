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
        // For now, return null (simplified implementation)
        // Full implementation would use FusedLocationProviderClient and geocoding
        return null
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