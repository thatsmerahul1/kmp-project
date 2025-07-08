package com.weather.domain.model

data class CacheConfig(
    val cacheExpiryHours: Double = 1.0, // 1 hour cache expiry
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS
)

enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT;
    
    fun convert(celsius: Double): Double {
        return when (this) {
            CELSIUS -> celsius
            FAHRENHEIT -> (celsius * 9/5) + 32
        }
    }
    
    fun getSymbol(): String {
        return when (this) {
            CELSIUS -> "°C"
            FAHRENHEIT -> "°F"
        }
    }
}