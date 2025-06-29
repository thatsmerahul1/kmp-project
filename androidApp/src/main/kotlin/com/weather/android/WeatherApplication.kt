package com.weather.android

import android.app.Application
import com.weather.android.di.androidModule
import com.weather.di.platformModule
import com.weather.di.sharedKoinModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WeatherApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@WeatherApplication)
            modules(sharedKoinModules + platformModule + androidModule)
        }
    }
}