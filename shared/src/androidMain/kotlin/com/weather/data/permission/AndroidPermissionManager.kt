package com.weather.data.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import com.weather.domain.permission.Permission
import com.weather.domain.permission.PermissionManager
import com.weather.domain.permission.PermissionStatus
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidPermissionManager(
    private val context: Context
) : PermissionManager {

    companion object {
        private fun Permission.toAndroidPermission(): String = when (this) {
            Permission.LOCATION_FINE -> Manifest.permission.ACCESS_FINE_LOCATION
            Permission.LOCATION_COARSE -> Manifest.permission.ACCESS_COARSE_LOCATION
            Permission.INTERNET -> Manifest.permission.INTERNET
            Permission.NETWORK_STATE -> Manifest.permission.ACCESS_NETWORK_STATE
        }
    }

    override suspend fun checkPermission(permission: Permission): PermissionStatus {
        val androidPermission = permission.toAndroidPermission()
        return when (ContextCompat.checkSelfPermission(context, androidPermission)) {
            PackageManager.PERMISSION_GRANTED -> {
                println("AndroidPermissionManager: Permission $permission is GRANTED")
                PermissionStatus.GRANTED
            }
            PackageManager.PERMISSION_DENIED -> {
                println("AndroidPermissionManager: Permission $permission is DENIED")
                PermissionStatus.DENIED
            }
            else -> {
                println("AndroidPermissionManager: Permission $permission is NOT_DETERMINED")
                PermissionStatus.NOT_DETERMINED
            }
        }
    }

    override suspend fun requestPermission(permission: Permission): PermissionStatus {
        // For now, just return current status since actual permission requests 
        // need to be handled by the Activity using ActivityResultContracts
        println("AndroidPermissionManager: Requesting permission $permission")
        return checkPermission(permission)
    }

    override suspend fun requestPermissions(permissions: List<Permission>): Map<Permission, PermissionStatus> {
        println("AndroidPermissionManager: Requesting multiple permissions: $permissions")
        return permissions.associateWith { checkPermission(it) }
    }

    override suspend fun hasLocationPermission(): Boolean {
        val fineLocationStatus = checkPermission(Permission.LOCATION_FINE)
        val coarseLocationStatus = checkPermission(Permission.LOCATION_COARSE)
        val hasPermission = fineLocationStatus == PermissionStatus.GRANTED || 
                           coarseLocationStatus == PermissionStatus.GRANTED
        
        println("AndroidPermissionManager: Has location permission: $hasPermission")
        return hasPermission
    }

    override suspend fun requestLocationPermission(): Boolean {
        // In a real implementation, this would trigger the permission request
        // For now, just check current status
        return hasLocationPermission()
    }

    override suspend fun openAppSettings() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            println("AndroidPermissionManager: Failed to open app settings: ${e.message}")
        }
    }

    override suspend fun shouldShowRationale(permission: Permission): Boolean {
        if (context !is ComponentActivity) return false
        
        val androidPermission = permission.toAndroidPermission()
        return ActivityCompat.shouldShowRequestPermissionRationale(context, androidPermission)
    }
}