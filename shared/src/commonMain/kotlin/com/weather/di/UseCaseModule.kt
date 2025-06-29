package com.weather.di

import com.weather.domain.usecase.GetCacheConfigUseCase
import com.weather.domain.usecase.GetWeatherForecastUseCase
import com.weather.domain.usecase.RefreshWeatherUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetWeatherForecastUseCase(get()) }
    factory { RefreshWeatherUseCase(get()) }
    factory { GetCacheConfigUseCase(get()) }
}