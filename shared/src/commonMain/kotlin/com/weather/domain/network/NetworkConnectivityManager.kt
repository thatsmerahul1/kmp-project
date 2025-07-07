package com.weather.domain.network

import kotlinx.coroutines.flow.Flow

/**
 * Platform-agnostic network connectivity manager
 */
interface NetworkConnectivityManager {
    /**
     * Check if network is currently available
     */
    suspend fun isNetworkAvailable(): Boolean
    
    /**
     * Observe network connectivity changes
     * @return Flow emitting true when connected, false when disconnected
     */
    fun observeNetworkConnectivity(): Flow<NetworkStatus>
    
    /**
     * Get current network type
     */
    suspend fun getCurrentNetworkType(): NetworkType
}

/**
 * Network status information
 */
data class NetworkStatus(
    val isConnected: Boolean,
    val networkType: NetworkType,
    val isMetered: Boolean = false
)

/**
 * Types of network connections
 */
enum class NetworkType {
    WIFI,
    CELLULAR,
    ETHERNET,
    UNKNOWN,
    NONE
}

/**
 * Network-related exceptions
 */
sealed class NetworkException : Exception() {
    object NoConnection : NetworkException() {
        override val message = "No network connection available"
    }
    
    object Timeout : NetworkException() {
        override val message = "Network request timed out"
    }
    
    data class HttpError(val code: Int, override val message: String) : NetworkException()
    
    data class Unknown(override val message: String) : NetworkException()
}