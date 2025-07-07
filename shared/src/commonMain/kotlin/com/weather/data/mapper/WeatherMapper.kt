package com.weather.data.mapper

import com.weather.data.remote.dto.WeatherItemDto
import com.weather.database.WeatherForecast
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import kotlinx.datetime.LocalDate

object WeatherMapper {

    fun WeatherItemDto.toDomain(): Weather {
        val parsedDate = LocalDate.parse(date)
        
        return Weather(
            date = parsedDate,
            condition = WeatherCondition.fromWeatherCode(weatherCode),
            temperatureHigh = temperatureMax,
            temperatureLow = temperatureMin,
            temperatureCurrent = temperatureCurrent,
            humidity = humidity,
            icon = getIconFromWeatherCode(weatherCode),
            description = getDescriptionFromWeatherCode(weatherCode),
            // Enhanced weather details
            pressure = pressure,
            windSpeed = windSpeed,
            windDirection = windDirection,
            visibility = visibility,
            uvIndex = uvIndex,
            precipitationChance = precipitationChance,
            precipitationAmount = precipitationAmount,
            cloudCover = cloudCover,
            feelsLike = feelsLike,
            dewPoint = dewPoint,
            sunrise = sunrise,
            sunset = sunset,
            moonPhase = getMoonPhase(parsedDate), // Generate moon phase
            airQuality = null // Will be populated from a separate API call in future
        )
    }

    fun Weather.toEntity(cachedAt: Long): WeatherForecast {
        val epochDays = date.toEpochDays().toLong()
        
        return WeatherForecast(
            id = epochDays,
            date = epochDays,
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
    
    private fun getIconFromWeatherCode(code: Int): String {
        return when (code) {
            0 -> "01d" // Clear sky
            1, 2, 3 -> "02d" // Partly cloudy
            45, 48 -> "50d" // Fog
            51, 53, 55, 56, 57 -> "09d" // Drizzle
            61, 63, 65, 66, 67 -> "10d" // Rain
            71, 73, 75, 77 -> "13d" // Snow
            80, 81, 82 -> "09d" // Showers
            85, 86 -> "13d" // Snow showers
            95, 96, 99 -> "11d" // Thunderstorm
            else -> "01d" // Default to clear
        }
    }
    
    private fun getDescriptionFromWeatherCode(code: Int): String {
        return when (code) {
            0 -> "Clear sky"
            1 -> "Mainly clear"
            2 -> "Partly cloudy"
            3 -> "Overcast"
            45 -> "Fog"
            48 -> "Depositing rime fog"
            51 -> "Light drizzle"
            53 -> "Moderate drizzle"
            55 -> "Dense drizzle"
            56 -> "Light freezing drizzle"
            57 -> "Dense freezing drizzle"
            61 -> "Slight rain"
            63 -> "Moderate rain"
            65 -> "Heavy rain"
            66 -> "Light freezing rain"
            67 -> "Heavy freezing rain"
            71 -> "Slight snow fall"
            73 -> "Moderate snow fall"
            75 -> "Heavy snow fall"
            77 -> "Snow grains"
            80 -> "Slight rain showers"
            81 -> "Moderate rain showers"
            82 -> "Violent rain showers"
            85 -> "Slight snow showers"
            86 -> "Heavy snow showers"
            95 -> "Thunderstorm"
            96 -> "Thunderstorm with slight hail"
            99 -> "Thunderstorm with heavy hail"
            else -> "Unknown"
        }
    }
    
    private fun getMoonPhase(date: LocalDate): String {
        // Simple moon phase calculation based on date
        // This is a simplified calculation - real apps would use astronomy libraries
        val dayOfYear = date.dayOfYear
        val moonCycle = 29.53 // Average lunar month in days
        val phase = (dayOfYear % moonCycle) / moonCycle
        
        return when {
            phase < 0.125 -> "New Moon"
            phase < 0.25 -> "Waxing Crescent"
            phase < 0.375 -> "First Quarter"
            phase < 0.5 -> "Waxing Gibbous"
            phase < 0.625 -> "Full Moon"
            phase < 0.75 -> "Waning Gibbous"
            phase < 0.875 -> "Last Quarter"
            else -> "Waning Crescent"
        }
    }
}