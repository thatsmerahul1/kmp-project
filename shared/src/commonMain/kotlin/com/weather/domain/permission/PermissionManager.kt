package com.weather.domain.permission

enum class Permission {
    LOCATION_FINE,
    LOCATION_COARSE,
    INTERNET,
    NETWORK_STATE
}

enum class PermissionStatus {
    GRANTED,
    DENIED,
    NOT_DETERMINED,
    PERMANENTLY_DENIED
}

interface PermissionManager {
    suspend fun checkPermission(permission: Permission): PermissionStatus
    suspend fun requestPermission(permission: Permission): PermissionStatus
    suspend fun requestPermissions(permissions: List<Permission>): Map<Permission, PermissionStatus>
    suspend fun hasLocationPermission(): Boolean
    suspend fun requestLocationPermission(): Boolean
    suspend fun openAppSettings()
    suspend fun shouldShowRationale(permission: Permission): Boolean
}