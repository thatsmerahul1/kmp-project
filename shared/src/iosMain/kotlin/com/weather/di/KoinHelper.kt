package com.weather.di

import org.koin.core.Koin
import org.koin.core.context.startKoin

class KoinHelper {
    companion object {
        val shared = KoinHelper()
    }
    
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
}