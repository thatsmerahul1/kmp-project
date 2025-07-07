package com.weather.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.weather.database.WeatherDatabase
import com.weather.data.location.AndroidLocationManager
import com.weather.domain.location.LocationManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single<SqlDriver> {
        AndroidSqliteDriver(
            WeatherDatabase.Schema,
            androidContext(),
            "weather.db"
        )
    }
    
    single<WeatherDatabase> {
        WeatherDatabase(get())
    }
    
    single<LocationManager> {
        AndroidLocationManager(androidContext(), get())
    }
}