package com.weather.di

import org.koin.core.Koin
import org.koin.core.context.startKoin

object KoinHelper {
    private var _koin: Koin? = null
    
    val koin: Koin
        get() = _koin ?: error("Koin not initialized. Call initKoin() first.")
    
    fun initKoin() {
        if (_koin == null) {
            val koinApp = startKoin {
                modules(sharedKoinModules + platformModule)
            }
            _koin = koinApp.koin
        }
    }
    
    inline fun <reified T> get(): T {
        return koin.get()
    }
}