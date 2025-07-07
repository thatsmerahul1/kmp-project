package com.weather.di

import com.weather.data.service.GeocodingServiceImpl
import com.weather.data.service.LocationServiceImpl
import com.weather.domain.service.GeocodingService
import com.weather.domain.service.LocationService
import org.koin.dsl.module

val serviceModule = module {
    single<GeocodingService> { GeocodingServiceImpl(get()) }
    single<LocationService> { LocationServiceImpl(get(), get(), get()) }
}