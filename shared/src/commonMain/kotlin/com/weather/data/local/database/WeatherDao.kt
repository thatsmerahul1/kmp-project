package com.weather.data.local.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.weather.database.WeatherDatabase
import com.weather.database.WeatherForecast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface WeatherDao {
    suspend fun insertWeatherForecast(forecast: WeatherForecast)
    suspend fun insertWeatherForecasts(forecasts: List<WeatherForecast>)
    suspend fun getAllWeatherForecasts(): List<WeatherForecast>
    fun getAllWeatherForecastsFlow(): Flow<List<WeatherForecast>>
    suspend fun getWeatherForecastsByDateRange(startDate: Long, endDate: Long): List<WeatherForecast>
    suspend fun deleteOlderThan(timestamp: Long)
    suspend fun deleteAll()
    suspend fun isCacheValid(timestamp: Long): Boolean
    suspend fun getCount(): Long
}

class WeatherDaoImpl(
    private val database: WeatherDatabase
) : WeatherDao {

    private val queries = database.weatherDatabaseQueries

    override suspend fun insertWeatherForecast(forecast: WeatherForecast) {
        withContext(Dispatchers.Default) {
            queries.insertOrReplace(
                id = forecast.id,
                date = forecast.date,
                condition = forecast.condition,
                temperature_high = forecast.temperature_high,
                temperature_low = forecast.temperature_low,
                humidity = forecast.humidity,
                icon = forecast.icon,
                description = forecast.description,
                cached_at = forecast.cached_at
            )
        }
    }

    override suspend fun insertWeatherForecasts(forecasts: List<WeatherForecast>) {
        withContext(Dispatchers.Default) {
            database.transaction {
                forecasts.forEach { forecast ->
                    queries.insertOrReplace(
                        id = forecast.id,
                        date = forecast.date,
                        condition = forecast.condition,
                        temperature_high = forecast.temperature_high,
                        temperature_low = forecast.temperature_low,
                        humidity = forecast.humidity,
                        icon = forecast.icon,
                        description = forecast.description,
                        cached_at = forecast.cached_at
                    )
                }
            }
        }
    }

    override suspend fun getAllWeatherForecasts(): List<WeatherForecast> {
        return withContext(Dispatchers.Default) {
            queries.selectAll().executeAsList()
        }
    }

    override fun getAllWeatherForecastsFlow(): Flow<List<WeatherForecast>> {
        return queries.selectAll().asFlow().mapToList(Dispatchers.Default)
    }

    override suspend fun getWeatherForecastsByDateRange(
        startDate: Long,
        endDate: Long
    ): List<WeatherForecast> {
        return withContext(Dispatchers.Default) {
            queries.selectByDateRange(startDate, endDate).executeAsList()
        }
    }

    override suspend fun deleteOlderThan(timestamp: Long) {
        withContext(Dispatchers.Default) {
            queries.deleteOlderThan(timestamp)
        }
    }

    override suspend fun deleteAll() {
        withContext(Dispatchers.Default) {
            queries.deleteAll()
        }
    }

    override suspend fun isCacheValid(timestamp: Long): Boolean {
        return withContext(Dispatchers.Default) {
            queries.isCacheValid(timestamp).executeAsOne()
        }
    }

    override suspend fun getCount(): Long {
        return withContext(Dispatchers.Default) {
            queries.countRecords().executeAsOne()
        }
    }
}