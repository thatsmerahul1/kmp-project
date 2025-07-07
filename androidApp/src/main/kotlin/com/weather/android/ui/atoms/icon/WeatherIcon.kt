package com.weather.android.ui.atoms.icon

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme
import com.weather.domain.model.WeatherCondition

@Composable
fun WeatherIcon(
    condition: WeatherCondition,
    modifier: Modifier = Modifier,
    size: Dp = AtomicDesignSystem.spacing.IconSizeLarge,
    tint: Color = Color.Unspecified
) {
    val iconData = getWeatherIconData(condition)
    
    Icon(
        imageVector = iconData.icon,
        contentDescription = iconData.contentDescription,
        tint = if (tint == Color.Unspecified) iconData.defaultTint else tint,
        modifier = modifier.size(size)
    )
}

private data class WeatherIconData(
    val icon: ImageVector,
    val contentDescription: String,
    val defaultTint: Color
)

@Composable
private fun getWeatherIconData(condition: WeatherCondition): WeatherIconData {
    return when (condition) {
        WeatherCondition.CLEAR -> WeatherIconData(
            icon = Icons.Default.Star, // Star for sunny
            contentDescription = "Clear sky",
            defaultTint = AtomicDesignSystem.colors.Sunny
        )
        WeatherCondition.CLOUDS -> WeatherIconData(
            icon = Icons.Default.Info, // Info icon for clouds
            contentDescription = "Cloudy",
            defaultTint = AtomicDesignSystem.colors.Cloudy
        )
        WeatherCondition.RAIN -> WeatherIconData(
            icon = Icons.Default.AccountBox, // Box icon for rain
            contentDescription = "Rain",
            defaultTint = AtomicDesignSystem.colors.Rainy
        )
        WeatherCondition.DRIZZLE -> WeatherIconData(
            icon = Icons.Default.Circle, // Circle for drizzle
            contentDescription = "Drizzle",
            defaultTint = AtomicDesignSystem.colors.Rainy
        )
        WeatherCondition.SNOW -> WeatherIconData(
            icon = Icons.Default.Circle, // Circle for snow
            contentDescription = "Snow",
            defaultTint = AtomicDesignSystem.colors.Snowy
        )
        WeatherCondition.THUNDERSTORM -> WeatherIconData(
            icon = Icons.Default.Warning, // Warning for storms
            contentDescription = "Thunderstorm",
            defaultTint = AtomicDesignSystem.colors.Stormy
        )
        WeatherCondition.MIST, WeatherCondition.FOG -> WeatherIconData(
            icon = Icons.Default.Info, // Info for fog
            contentDescription = "Foggy",
            defaultTint = AtomicDesignSystem.colors.Cloudy
        )
        WeatherCondition.SMOKE, WeatherCondition.HAZE, WeatherCondition.DUST, 
        WeatherCondition.SAND, WeatherCondition.ASH -> WeatherIconData(
            icon = Icons.Default.Info, // Info for atmospheric
            contentDescription = "Atmospheric conditions",
            defaultTint = AtomicDesignSystem.colors.Cloudy
        )
        WeatherCondition.SQUALL, WeatherCondition.TORNADO -> WeatherIconData(
            icon = Icons.Default.Warning, // Warning for severe weather
            contentDescription = "Severe weather",
            defaultTint = AtomicDesignSystem.colors.Stormy
        )
        WeatherCondition.UNKNOWN -> WeatherIconData(
            icon = Icons.Default.Info, // Info for unknown
            contentDescription = "Unknown weather",
            defaultTint = AtomicDesignSystem.colors.OnSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WeatherIconPreview() {
    AtomicTheme {
        WeatherIcon(condition = WeatherCondition.CLEAR)
    }
}