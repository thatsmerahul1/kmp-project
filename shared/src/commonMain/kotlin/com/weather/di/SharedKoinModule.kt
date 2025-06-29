package com.weather.di

import org.koin.core.module.Module
import org.koin.dsl.module

val sharedKoinModules = listOf(
    databaseModule,
    networkModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)

// Platform-specific modules will be added in androidMain and iosMain
expect val platformModule: Module