package com.weather.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class WeatherConditionTest {

    @Test
    fun `fromString should return correct weather condition for valid inputs`() {
        assertEquals(WeatherCondition.CLEAR, WeatherCondition.fromString("clear"))
        assertEquals(WeatherCondition.CLEAR, WeatherCondition.fromString("Clear"))
        assertEquals(WeatherCondition.CLEAR, WeatherCondition.fromString("CLEAR"))
        
        assertEquals(WeatherCondition.CLOUDS, WeatherCondition.fromString("clouds"))
        assertEquals(WeatherCondition.RAIN, WeatherCondition.fromString("rain"))
        assertEquals(WeatherCondition.THUNDERSTORM, WeatherCondition.fromString("thunderstorm"))
        assertEquals(WeatherCondition.SNOW, WeatherCondition.fromString("snow"))
    }

    @Test
    fun `fromString should return UNKNOWN for invalid inputs`() {
        assertEquals(WeatherCondition.UNKNOWN, WeatherCondition.fromString("invalid"))
        assertEquals(WeatherCondition.UNKNOWN, WeatherCondition.fromString(""))
        assertEquals(WeatherCondition.UNKNOWN, WeatherCondition.fromString("random"))
    }
}