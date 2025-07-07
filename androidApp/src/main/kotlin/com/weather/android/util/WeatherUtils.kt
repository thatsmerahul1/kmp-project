package com.weather.android.util

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.weather.android.ui.theme.AtomicColors
import com.weather.domain.model.WeatherCondition

/**
 * Utility functions for weather-related operations
 */
object WeatherUtils {
    
    /**
     * Gets the appropriate emoji representation for a weather condition
     * @param condition The weather condition
     * @return The emoji string representing the condition
     */
    fun getWeatherEmoji(condition: WeatherCondition): String {
        return when (condition) {
            WeatherCondition.CLEAR -> "☀️"
            WeatherCondition.CLOUDS -> "☁️"
            WeatherCondition.RAIN -> "🌧️"
            WeatherCondition.DRIZZLE -> "🌦️"
            WeatherCondition.THUNDERSTORM -> "⛈️"
            WeatherCondition.SNOW -> "❄️"
            WeatherCondition.MIST, WeatherCondition.FOG -> "🌫️"
            else -> "🌤️"
        }
    }
    
    /**
     * Gets the appropriate gradient brush for a weather condition
     * @param condition The weather condition
     * @return A Brush with gradient colors matching the weather
     */
    fun getWeatherGradient(condition: WeatherCondition): Brush {
        return when (condition) {
            WeatherCondition.CLEAR -> Brush.verticalGradient(
                colors = listOf(
                    AtomicColors.SunnyGradientStart,
                    AtomicColors.SunnyGradientEnd
                )
            )
            WeatherCondition.CLOUDS -> Brush.verticalGradient(
                colors = listOf(
                    AtomicColors.CloudyGradientStart,
                    AtomicColors.CloudyGradientEnd
                )
            )
            WeatherCondition.RAIN, WeatherCondition.DRIZZLE -> Brush.verticalGradient(
                colors = listOf(
                    AtomicColors.RainyGradientStart,
                    AtomicColors.RainyGradientEnd
                )
            )
            WeatherCondition.THUNDERSTORM -> Brush.verticalGradient(
                colors = listOf(
                    AtomicColors.StormyGradientStart,
                    AtomicColors.StormyGradientEnd
                )
            )
            WeatherCondition.SNOW -> Brush.verticalGradient(
                colors = listOf(
                    AtomicColors.SnowyGradientStart,
                    AtomicColors.SnowyGradientEnd
                )
            )
            WeatherCondition.MIST, WeatherCondition.FOG -> Brush.verticalGradient(
                colors = listOf(
                    AtomicColors.CloudyGradientStart,
                    AtomicColors.CloudyGradientEnd
                )
            )
            else -> Brush.verticalGradient(
                colors = listOf(
                    AtomicColors.CloudyGradientStart,
                    AtomicColors.CloudyGradientEnd
                )
            )
        }
    }
    
    /**
     * Gets the primary color for a weather condition
     * @param condition The weather condition
     * @return The primary color representing the weather
     */
    fun getWeatherColor(condition: WeatherCondition): Color {
        return when (condition) {
            WeatherCondition.CLEAR -> AtomicColors.Sunny
            WeatherCondition.CLOUDS -> AtomicColors.Cloudy
            WeatherCondition.RAIN, WeatherCondition.DRIZZLE -> AtomicColors.Rainy
            WeatherCondition.THUNDERSTORM -> AtomicColors.Stormy
            WeatherCondition.SNOW -> AtomicColors.Snowy
            WeatherCondition.MIST, WeatherCondition.FOG -> AtomicColors.Cloudy
            else -> AtomicColors.Cloudy
        }
    }
}