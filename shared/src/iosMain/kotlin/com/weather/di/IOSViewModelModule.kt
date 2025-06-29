package com.weather.di

import com.weather.presentation.viewmodel.IOSWeatherViewModel
import org.koin.dsl.module

val iosViewModelModule = module {
    factory { IOSWeatherViewModel(get(), get()) }
}