package com.weather.android.ui.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.weather.android.ui.atoms.text.CaptionText
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme

enum class TemperatureUnit(val symbol: String) {
    CELSIUS("C"),
    FAHRENHEIT("F")
}

@Composable
fun TemperatureDisplay(
    high: Double,
    low: Double,
    unit: TemperatureUnit = TemperatureUnit.CELSIUS,
    modifier: Modifier = Modifier,
    showLowTemperature: Boolean = true
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AtomicDesignSystem.spacing.XS)
    ) {
        Text(
            text = "${high.toInt()}°${unit.symbol}",
            style = AtomicDesignSystem.typography.TemperatureMedium,
            color = AtomicDesignSystem.colors.OnSurface
        )
        
        if (showLowTemperature) {
            CaptionText(
                text = "Low ${low.toInt()}°${unit.symbol}",
                color = AtomicDesignSystem.colors.OnSurfaceVariant
            )
        }
    }
}

@Composable
fun LargeTemperatureDisplay(
    temperature: Double,
    unit: TemperatureUnit = TemperatureUnit.CELSIUS,
    modifier: Modifier = Modifier
) {
    Text(
        text = "${temperature.toInt()}°",
        style = AtomicDesignSystem.typography.TemperatureLarge,
        color = AtomicDesignSystem.colors.OnSurface,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun TemperatureDisplayPreview() {
    AtomicTheme {
        TemperatureDisplay(
            high = 22.0,
            low = 15.0
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LargeTemperatureDisplayPreview() {
    AtomicTheme {
        LargeTemperatureDisplay(temperature = 22.0)
    }
}