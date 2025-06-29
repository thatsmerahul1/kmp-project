package com.weather.di

import com.weather.data.local.database.WeatherDao
import com.weather.data.local.database.WeatherDaoImpl
import org.koin.dsl.module

val databaseModule = module {
    single<WeatherDao> { WeatherDaoImpl(get()) }
}