package com.weather.data.mapper

import com.weather.data.mapper.WeatherMapper.toDomain
import com.weather.data.remote.dto.WeatherItemDto
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class WeatherMapperTest {

    @Test
    fun `WeatherItemDto toDomain should map correctly`() {
        val dto = WeatherItemDto(
            date = "2025-01-15",
            temperatureMax = 25.0,
            temperatureMin = 15.0,
            humidity = 65,
            weatherCode = 0, // Clear sky
            windSpeed = 5.5
        )

        val result = dto.toDomain()

        assertEquals(LocalDate(2025, 1, 15), result.date)
        assertEquals(WeatherCondition.CLEAR, result.condition)
        assertEquals(25.0, result.temperatureHigh)
        assertEquals(15.0, result.temperatureLow)
        assertEquals(65, result.humidity)
        assertEquals("01d", result.icon)
        assertEquals("Clear sky", result.description)
    }

    @Test
    fun `WeatherItemDto toDomain should map cloudy weather correctly`() {
        val dto = WeatherItemDto(
            date = "2025-01-16",
            temperatureMax = 22.0,
            temperatureMin = 12.0,
            humidity = 70,
            weatherCode = 2, // Partly cloudy
            windSpeed = 7.0
        )

        val result = dto.toDomain()

        assertEquals(LocalDate(2025, 1, 16), result.date)
        assertEquals(WeatherCondition.CLOUDS, result.condition)
        assertEquals(22.0, result.temperatureHigh)
        assertEquals(12.0, result.temperatureLow)
        assertEquals(70, result.humidity)
        assertEquals("02d", result.icon)
        assertEquals("Partly cloudy", result.description)
    }

    @Test
    fun `WeatherItemDto toDomain should map rainy weather correctly`() {
        val dto = WeatherItemDto(
            date = "2025-01-17",
            temperatureMax = 18.0,
            temperatureMin = 10.0,
            humidity = 85,
            weatherCode = 61, // Light rain
            windSpeed = 12.0
        )

        val result = dto.toDomain()

        assertEquals(LocalDate(2025, 1, 17), result.date)
        assertEquals(WeatherCondition.RAIN, result.condition)
        assertEquals(18.0, result.temperatureHigh)
        assertEquals(10.0, result.temperatureLow)
        assertEquals(85, result.humidity)
        assertEquals("10d", result.icon)
        assertEquals("Light rain", result.description)
    }

    @Test
    fun `Weather toDomain should handle identity mapping`() {
        val weather = Weather(
            date = LocalDate(2025, 1, 15),
            condition = WeatherCondition.CLEAR,
            temperatureHigh = 25.0,
            temperatureLow = 15.0,
            humidity = 65,
            icon = "01d",
            description = "Clear sky"
        )

        val result = weather.toDomain()

        assertEquals(weather, result)
    }

    @Test
    fun `WeatherItemDto should handle extreme weather codes`() {
        val dto = WeatherItemDto(
            date = "2025-01-18",
            temperatureMax = -5.0,
            temperatureMin = -15.0,
            humidity = 90,
            weatherCode = 85, // Heavy snow
            windSpeed = 25.0
        )

        val result = dto.toDomain()

        assertEquals(LocalDate(2025, 1, 18), result.date)
        assertEquals(WeatherCondition.SNOW, result.condition)
        assertEquals(-5.0, result.temperatureHigh)
        assertEquals(-15.0, result.temperatureLow)
        assertEquals(90, result.humidity)
        assertEquals("13d", result.icon)
        assertEquals("Heavy snow", result.description)
    }
}