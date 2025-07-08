package com.weather.data.permission

import com.weather.domain.permission.Permission
import com.weather.domain.permission.PermissionManager
import com.weather.domain.permission.PermissionStatus
import platform.CoreLocation.*
import platform.Foundation.*
import platform.UIKit.*

class IOSPermissionManager : PermissionManager {

    override suspend fun checkPermission(permission: Permission): PermissionStatus {
        return when (permission) {
            Permission.LOCATION_FINE, Permission.LOCATION_COARSE -> {
                val status = when (CLLocationManager.authorizationStatus()) {
                    kCLAuthorizationStatusAuthorizedWhenInUse,
                    kCLAuthorizationStatusAuthorizedAlways -> {
                        println("IOSPermissionManager: Location permission is GRANTED")
                        PermissionStatus.GRANTED
                    }
                    kCLAuthorizationStatusDenied -> {
                        println("IOSPermissionManager: Location permission is DENIED")
                        PermissionStatus.DENIED
                    }
                    kCLAuthorizationStatusRestricted -> {
                        println("IOSPermissionManager: Location permission is PERMANENTLY_DENIED")
                        PermissionStatus.PERMANENTLY_DENIED
                    }
                    kCLAuthorizationStatusNotDetermined -> {
                        println("IOSPermissionManager: Location permission is NOT_DETERMINED")
                        PermissionStatus.NOT_DETERMINED
                    }
                    else -> PermissionStatus.NOT_DETERMINED
                }
                status
            }
            Permission.INTERNET, Permission.NETWORK_STATE -> {
                // On iOS, internet access doesn't require explicit permission
                println("IOSPermissionManager: Internet/Network permission is auto-granted")
                PermissionStatus.GRANTED
            }
        }
    }

    override suspend fun requestPermission(permission: Permission): PermissionStatus {
        return when (permission) {
            Permission.LOCATION_FINE, Permission.LOCATION_COARSE -> {
                println("IOSPermissionManager: Requesting location permission")
                val locationManager = CLLocationManager()
                
                // Request permission
                locationManager.requestWhenInUseAuthorization()
                
                // Return current status (in real implementation, this would wait for callback)
                checkPermission(permission)
            }
            Permission.INTERNET, Permission.NETWORK_STATE -> {
                // Auto-granted on iOS
                PermissionStatus.GRANTED
            }
        }
    }

    override suspend fun requestPermissions(permissions: List<Permission>): Map<Permission, PermissionStatus> {
        println("IOSPermissionManager: Requesting multiple permissions: $permissions")
        return permissions.associateWith { requestPermission(it) }
    }

    override suspend fun hasLocationPermission(): Boolean {
        val status = checkPermission(Permission.LOCATION_FINE)
        val hasPermission = status == PermissionStatus.GRANTED
        println("IOSPermissionManager: Has location permission: $hasPermission")
        return hasPermission
    }

    override suspend fun requestLocationPermission(): Boolean {
        val status = requestPermission(Permission.LOCATION_FINE)
        return status == PermissionStatus.GRANTED
    }

    override suspend fun openAppSettings() {
        try {
            val settingsUrl = NSURL.URLWithString("app-settings:")
            if (settingsUrl != null && UIApplication.sharedApplication.canOpenURL(settingsUrl)) {
                UIApplication.sharedApplication.openURL(settingsUrl)
            } else {
                // Fallback to iOS Settings app
                val fallbackUrl = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
                if (fallbackUrl != null) {
                    UIApplication.sharedApplication.openURL(fallbackUrl)
                }
            }
        } catch (e: Exception) {
            println("IOSPermissionManager: Failed to open app settings: ${e.message}")
        }
    }

    override suspend fun shouldShowRationale(permission: Permission): Boolean {
        // iOS doesn't have a direct equivalent to shouldShowRequestPermissionRationale
        // We can infer based on the current status
        val status = checkPermission(permission)
        return status == PermissionStatus.DENIED
    }
}