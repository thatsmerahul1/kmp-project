package com.weather.domain.model

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for the Weather domain model
 */
class WeatherTest {
    
    @Test
    fun testWeatherCreation() {
        val weather = Weather(
            date = LocalDate(2025, 1, 1),
            condition = WeatherCondition.CLEAR,
            temperatureHigh = 25.0,
            temperatureLow = 15.0,
            humidity = 65,
            icon = "sunny",
            description = "Sunny day"
        )
        
        assertEquals(LocalDate(2025, 1, 1), weather.date)
        assertEquals(WeatherCondition.CLEAR, weather.condition)
        assertEquals(25.0, weather.temperatureHigh)
        assertEquals(15.0, weather.temperatureLow)
        assertEquals(65, weather.humidity)
        assertEquals("sunny", weather.icon)
        assertEquals("Sunny day", weather.description)
    }
    
    @Test
    fun testWeatherCopy() {
        val originalWeather = Weather(
            date = LocalDate(2025, 1, 1),
            condition = WeatherCondition.CLEAR,
            temperatureHigh = 25.0,
            temperatureLow = 15.0,
            humidity = 65,
            icon = "sunny",
            description = "Sunny day"
        )
        
        val modifiedWeather = originalWeather.copy(
            temperatureHigh = 30.0,
            description = "Very sunny day"
        )
        
        assertEquals(30.0, modifiedWeather.temperatureHigh)
        assertEquals("Very sunny day", modifiedWeather.description)
        // Other properties should remain the same
        assertEquals(originalWeather.date, modifiedWeather.date)
        assertEquals(originalWeather.condition, modifiedWeather.condition)
        assertEquals(originalWeather.temperatureLow, modifiedWeather.temperatureLow)
    }
    
    @Test
    fun testWeatherEquality() {
        val weather1 = Weather(
            date = LocalDate(2025, 1, 1),
            condition = WeatherCondition.CLEAR,
            temperatureHigh = 25.0,
            temperatureLow = 15.0,
            humidity = 65,
            icon = "sunny",
            description = "Sunny day"
        )
        
        val weather2 = Weather(
            date = LocalDate(2025, 1, 1),
            condition = WeatherCondition.CLEAR,
            temperatureHigh = 25.0,
            temperatureLow = 15.0,
            humidity = 65,
            icon = "sunny",
            description = "Sunny day"
        )
        
        assertEquals(weather1, weather2)
        assertEquals(weather1.hashCode(), weather2.hashCode())
    }
}