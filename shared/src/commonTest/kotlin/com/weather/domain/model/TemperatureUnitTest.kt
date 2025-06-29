package com.weather.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class TemperatureUnitTest {

    @Test
    fun `celsius convert should return same value`() {
        val celsius = TemperatureUnit.CELSIUS
        assertEquals(25.0, celsius.convert(25.0))
        assertEquals(0.0, celsius.convert(0.0))
        assertEquals(-10.0, celsius.convert(-10.0))
    }

    @Test
    fun `fahrenheit convert should return correct fahrenheit value`() {
        val fahrenheit = TemperatureUnit.FAHRENHEIT
        assertEquals(77.0, fahrenheit.convert(25.0)) // 25°C = 77°F
        assertEquals(32.0, fahrenheit.convert(0.0))  // 0°C = 32°F
        assertEquals(14.0, fahrenheit.convert(-10.0)) // -10°C = 14°F
    }

    @Test
    fun `getSymbol should return correct symbols`() {
        assertEquals("°C", TemperatureUnit.CELSIUS.getSymbol())
        assertEquals("°F", TemperatureUnit.FAHRENHEIT.getSymbol())
    }
}