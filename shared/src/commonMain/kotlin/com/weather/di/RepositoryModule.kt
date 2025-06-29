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
    
    single<RemoteWeatherDataSource> { RemoteWeatherDataSourceImpl(get()) }
    
    single<WeatherRepository> { 
        WeatherRepositoryImpl(
            localDataSource = get(),
            remoteDataSource = get(),
            appPreferences = get()
        )
    }
}