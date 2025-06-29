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
    }
}