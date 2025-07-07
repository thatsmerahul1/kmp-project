package com.weather.di

import com.weather.domain.usecase.GetCacheConfigUseCase
import com.weather.domain.usecase.GetWeatherForecastUseCase
import com.weather.domain.usecase.RefreshWeatherUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetWeatherForecastUseCase(get()) }
    factory { RefreshWeatherUseCase(get()) }
    factory { GetCacheConfigUseCase(get()) }
    
    // UseCase configuration for 2025 standards
    single(org.koin.core.qualifier.named("usecase_config")) {
        mapOf(
            "default_timeout_seconds" to 10,
            "enable_caching" to true,
            "enable_offline_fallback" to true,
            "enable_performance_tracking" to true
        )
    }
    
    // Performance monitoring for UseCases
    single(org.koin.core.qualifier.named("usecase_performance_tracker")) {
        mutableMapOf<String, MutableList<Long>>()
    }
}