package com.weather.domain.usecase

import com.weather.data.local.preferences.AppPreferences
import com.weather.domain.model.CacheConfig

class GetCacheConfigUseCase(
    private val appPreferences: AppPreferences
) {
    suspend operator fun invoke(): CacheConfig {
        return appPreferences.getCacheConfig()
    }
}