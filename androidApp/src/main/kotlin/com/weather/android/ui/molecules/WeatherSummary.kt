package com.weather.android.ui.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.weather.android.ui.atoms.icon.WeatherIcon
import com.weather.android.ui.atoms.text.BodyText
import com.weather.android.ui.atoms.text.CaptionText
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import kotlinx.datetime.LocalDate
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun WeatherSummary(
    weather: Weather,
    modifier: Modifier = Modifier,
    showDate: Boolean = true,
    showHumidity: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Date and description
        Column(
            modifier = Modifier.weight(1f)
        ) {
            if (showDate) {
                BodyText(
                    text = formatDate(weather.date),
                    color = AtomicDesignSystem.colors.OnSurface
                )
            }
            CaptionText(
                text = weather.description.replaceFirstChar { 
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
                },
                color = AtomicDesignSystem.colors.OnSurface.copy(alpha = 0.8f)
            )
            if (showHumidity) {
                CaptionText(
                    text = "Humidity ${weather.humidity}%",
                    color = AtomicDesignSystem.colors.OnSurface.copy(alpha = 0.7f)
                )
            }
        }
        
        // Weather icon
        WeatherIcon(
            condition = weather.condition,
            size = AtomicDesignSystem.spacing.IconSizeMedium
        )
        
        // Temperature
        TemperatureDisplay(
            high = weather.temperatureHigh,
            low = weather.temperatureLow,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun CompactWeatherSummary(
    weather: Weather,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AtomicDesignSystem.spacing.SM),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WeatherIcon(
            condition = weather.condition,
            size = AtomicDesignSystem.spacing.IconSize
        )
        
        BodyText(
            text = "${weather.temperatureHigh.toInt()}°/${weather.temperatureLow.toInt()}°",
            textAlign = TextAlign.End
        )
    }
}

private fun formatDate(date: LocalDate): String {
    return try {
        val dayOfWeek = when (date.dayOfWeek.name) {
            "MONDAY" -> "Monday"
            "TUESDAY" -> "Tuesday" 
            "WEDNESDAY" -> "Wednesday"
            "THURSDAY" -> "Thursday"
            "FRIDAY" -> "Friday"
            "SATURDAY" -> "Saturday"
            "SUNDAY" -> "Sunday"
            else -> "Unknown"
        }
        dayOfWeek
    } catch (e: Exception) {
        "Today"
    }
}

@Preview(showBackground = true)
@Composable
private fun WeatherSummaryPreview() {
    AtomicTheme {
        WeatherSummary(
            weather = Weather(
                date = LocalDate(2024, 1, 15),
                condition = WeatherCondition.CLEAR,
                temperatureHigh = 22.0,
                temperatureLow = 15.0,
                humidity = 65,
                icon = "01d",
                description = "Clear sky"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CompactWeatherSummaryPreview() {
    AtomicTheme {
        CompactWeatherSummary(
            weather = Weather(
                date = LocalDate(2024, 1, 15),
                condition = WeatherCondition.CLOUDS,
                temperatureHigh = 18.0,
                temperatureLow = 12.0,
                humidity = 75,
                icon = "02d",
                description = "Few clouds"
            )
        )
    }
}