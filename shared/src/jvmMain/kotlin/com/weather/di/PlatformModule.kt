package com.weather.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.weather.database.WeatherDatabase
import com.weather.security.PlatformEncryptionProvider
import com.weather.security.PlatformSecureStorage
import org.koin.dsl.module

/**
 * JVM platform-specific Koin module
 */
actual val platformModule = module {
    single<SqlDriver> {
        JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
            .also { WeatherDatabase.Schema.create(it) }
    }
    
    single { PlatformEncryptionProvider() }
    single { PlatformSecureStorage() }
}