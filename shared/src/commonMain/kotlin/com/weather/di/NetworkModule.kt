package com.weather.di

import com.weather.data.remote.HttpClientFactory
import com.weather.data.remote.api.WeatherApi
import com.weather.data.remote.api.WeatherApiImpl
import org.koin.dsl.module

val networkModule = module {
    single { HttpClientFactory.create() }
    
    single<WeatherApi> { 
        WeatherApiImpl(httpClient = get())
    }
    
    // Network configuration
    single(org.koin.core.qualifier.named("network_config")) {
        mapOf(
            "timeout_seconds" to 30,
            "retry_attempts" to 3,
            "cache_size_mb" to 10
        )
    }
}