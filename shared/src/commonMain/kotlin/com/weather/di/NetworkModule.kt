package com.weather.di

import com.weather.data.remote.HttpClientFactory
import com.weather.data.remote.api.WeatherApi
import com.weather.data.remote.api.WeatherApiImpl
import org.koin.dsl.module

val networkModule = module {
    single { HttpClientFactory.create() }
    
    single<WeatherApi> { 
        WeatherApiImpl(
            httpClient = get(),
            baseUrl = "https://api.openweathermap.org/data/2.5"
        )
    }
}