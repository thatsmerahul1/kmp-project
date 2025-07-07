package com.weather.di

import com.weather.presentation.viewmodel.WeatherViewModel
import org.koin.dsl.module

val viewModelModule = module {
    factory { WeatherViewModel(get(), get(), get()) }
}