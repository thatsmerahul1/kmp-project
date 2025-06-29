package com.weather.android.ui.organisms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.weather.android.ui.molecules.WeatherSummary
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import kotlinx.datetime.LocalDate

@Composable
fun WeatherCard(
    weather: Weather,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    showDate: Boolean = true,
    showHumidity: Boolean = true
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        shape = AtomicDesignSystem.shapes.WeatherCard,
        colors = CardDefaults.cardColors(
            containerColor = AtomicDesignSystem.colors.Surface,
            contentColor = AtomicDesignSystem.colors.OnSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = AtomicDesignSystem.spacing.XS
        )
    ) {
        WeatherSummary(
            weather = weather,
            showDate = showDate,
            showHumidity = showHumidity,
            modifier = Modifier.padding(AtomicDesignSystem.spacing.WeatherCardPadding)
        )
    }
}

@Composable
fun CompactWeatherCard(
    weather: Weather,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        shape = AtomicDesignSystem.shapes.WeatherCard,
        colors = CardDefaults.cardColors(
            containerColor = AtomicDesignSystem.colors.SurfaceVariant,
            contentColor = AtomicDesignSystem.colors.OnSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = AtomicDesignSystem.spacing.XS / 2
        )
    ) {
        WeatherSummary(
            weather = weather,
            showDate = false,
            showHumidity = false,
            modifier = Modifier.padding(AtomicDesignSystem.spacing.SM)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WeatherCardPreview() {
    AtomicTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(AtomicDesignSystem.spacing.SM),
            modifier = Modifier.padding(AtomicDesignSystem.spacing.MD)
        ) {
            WeatherCard(
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
            
            CompactWeatherCard(
                weather = Weather(
                    date = LocalDate(2024, 1, 16),
                    condition = WeatherCondition.RAIN,
                    temperatureHigh = 18.0,
                    temperatureLow = 12.0,
                    humidity = 85,
                    icon = "10d",
                    description = "Light rain"
                )
            )
        }
    }
}