package com.weather.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.weather.database.WeatherDatabase
import com.weather.data.location.IOSLocationManager
import com.weather.data.permission.IOSPermissionManager
import com.weather.domain.location.LocationManager
import com.weather.domain.permission.PermissionManager
import org.koin.dsl.module

actual val platformModule = module {
    single<SqlDriver> {
        NativeSqliteDriver(
            WeatherDatabase.Schema,
            "weather.db"
        )
    }
    
    single<WeatherDatabase> {
        WeatherDatabase(get())
    }
    
    single<LocationManager> {
        IOSLocationManager(get())
    }
    
    single<PermissionManager> {
        IOSPermissionManager()
    }
    
    // Include iOS-specific ViewModel
    includes(iosViewModelModule)
}