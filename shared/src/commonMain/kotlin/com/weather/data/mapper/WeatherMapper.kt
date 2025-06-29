package com.weather.data.mapper

import com.weather.data.remote.dto.WeatherItemDto
import com.weather.database.WeatherForecast
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object WeatherMapper {

    fun WeatherItemDto.toDomain(): Weather {
        val date = Instant.fromEpochSeconds(timestamp)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
        
        val weatherInfo = weather.firstOrNull()
        
        return Weather(
            date = date,
            condition = WeatherCondition.fromString(weatherInfo?.main ?: ""),
            temperatureHigh = main.temperatureMax - 273.15, // Convert from Kelvin to Celsius
            temperatureLow = main.temperatureMin - 273.15,
            humidity = main.humidity,
            icon = weatherInfo?.icon ?: "",
            description = weatherInfo?.description ?: ""
        )
    }

    fun Weather.toEntity(cachedAt: Long): WeatherForecast {
        return WeatherForecast(
            id = 0L, // Will be auto-generated
            date = date.toEpochDays().toLong(),
            condition = condition.name,
            temperature_high = temperatureHigh,
            temperature_low = temperatureLow,
            humidity = humidity.toLong(),
            icon = icon,
            description = description,
            cached_at = cachedAt
        )
    }

    fun WeatherForecast.toDomain(): Weather {
        return Weather(
            date = LocalDate.fromEpochDays(date.toInt()),
            condition = WeatherCondition.fromString(condition),
            temperatureHigh = temperature_high,
            temperatureLow = temperature_low,
            humidity = humidity.toInt(),
            icon = icon,
            description = description
        )
    }
}