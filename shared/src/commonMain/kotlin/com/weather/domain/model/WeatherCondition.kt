package com.weather.domain.model

enum class WeatherCondition {
    CLEAR,
    CLOUDS,
    RAIN,
    DRIZZLE,
    THUNDERSTORM,
    SNOW,
    MIST,
    SMOKE,
    HAZE,
    DUST,
    FOG,
    SAND,
    ASH,
    SQUALL,
    TORNADO,
    UNKNOWN;

    companion object {
        fun fromString(condition: String): WeatherCondition {
            return when (condition.lowercase()) {
                "clear" -> CLEAR
                "clouds" -> CLOUDS
                "rain" -> RAIN
                "drizzle" -> DRIZZLE
                "thunderstorm" -> THUNDERSTORM
                "snow" -> SNOW
                "mist" -> MIST
                "smoke" -> SMOKE
                "haze" -> HAZE
                "dust" -> DUST
                "fog" -> FOG
                "sand" -> SAND
                "ash" -> ASH
                "squall" -> SQUALL
                "tornado" -> TORNADO
                else -> UNKNOWN
            }
        }
        
        fun fromWeatherCode(code: Int): WeatherCondition {
            return when (code) {
                0 -> CLEAR // Clear sky
                1, 2, 3 -> CLOUDS // Mainly clear, partly cloudy, and overcast
                45, 48 -> FOG // Fog and depositing rime fog
                51, 53, 55, 56, 57 -> DRIZZLE // Drizzle variations
                61, 63, 65, 66, 67 -> RAIN // Rain variations
                71, 73, 75, 77 -> SNOW // Snow fall variations
                80, 81, 82 -> RAIN // Rain showers
                85, 86 -> SNOW // Snow showers
                95, 96, 99 -> THUNDERSTORM // Thunderstorm variations
                else -> UNKNOWN
            }
        }
    }
}