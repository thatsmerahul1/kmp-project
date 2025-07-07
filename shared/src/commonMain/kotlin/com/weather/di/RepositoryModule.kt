package com.weather.di

import com.weather.data.local.LocalWeatherDataSource
import com.weather.data.local.LocalWeatherDataSourceImpl
import com.weather.data.local.preferences.AppPreferences
import com.weather.data.local.preferences.AppPreferencesImpl
import com.weather.data.remote.RemoteWeatherDataSource
import com.weather.data.remote.RemoteWeatherDataSourceImpl
import com.weather.data.repository.WeatherRepositoryImpl
import com.weather.domain.repository.WeatherRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AppPreferences> { AppPreferencesImpl() }
    
    single<LocalWeatherDataSource> { LocalWeatherDataSourceImpl(get()) }
    
    single<RemoteWeatherDataSource> { 
        RemoteWeatherDataSourceImpl(
            weatherApi = get(),
            httpClient = get()
        )
    }
    
    single<WeatherRepository> { 
        WeatherRepositoryImpl(
            localDataSource = get(),
            remoteDataSource = get(),
            appPreferences = get()
        )
    }
    
    // Repository configuration for 2025 standards
    single(org.koin.core.qualifier.named("repository_config")) {
        mapOf(
            "cache_duration_minutes" to 30,
            "offline_mode_enabled" to true,
            "auto_refresh_enabled" to true,
            "max_retry_attempts" to 3,
            "enable_background_sync" to true
        )
    }
    
    // Repository performance metrics
    single(org.koin.core.qualifier.named("repository_metrics")) {
        mutableMapOf<String, Any>(
            "cache_hit_count" to 0L,
            "cache_miss_count" to 0L,
            "network_request_count" to 0L,
            "average_response_time_ms" to 0.0
        )
    }
}