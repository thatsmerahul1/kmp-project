package com.weather.data.local.preferences

import com.weather.domain.model.CacheConfig

interface AppPreferences {
    suspend fun getCacheConfig(): CacheConfig
    suspend fun setCacheConfig(config: CacheConfig)
    suspend fun getDefaultLocation(): String
    suspend fun setDefaultLocation(location: String)
}

class AppPreferencesImpl : AppPreferences {
    
    // For now, using hardcoded values. In a real implementation, 
    // you would use platform-specific storage (SharedPreferences on Android, UserDefaults on iOS)
    private var _cacheConfig = CacheConfig()
    private var _defaultLocation = "Bengaluru,India"

    override suspend fun getCacheConfig(): CacheConfig {
        return _cacheConfig
    }

    override suspend fun setCacheConfig(config: CacheConfig) {
        _cacheConfig = config
    }

    override suspend fun getDefaultLocation(): String {
        return _defaultLocation
    }

    override suspend fun setDefaultLocation(location: String) {
        _defaultLocation = location
    }
}