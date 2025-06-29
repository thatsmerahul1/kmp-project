package com.weather.android.di

import com.weather.android.presentation.WeatherViewModelBridge
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val androidModule = module {
    viewModel { WeatherViewModelBridge(get()) }
}