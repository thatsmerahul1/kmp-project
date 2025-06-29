package com.weather.android.ui.atoms.icon

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
            icon = Icons.Default.Star, // Using star as sun placeholder
            contentDescription = "Clear sky",
            defaultTint = AtomicDesignSystem.colors.Sunny
        )
        WeatherCondition.CLOUDS -> WeatherIconData(
            icon = Icons.Default.Info, // Using info as cloud placeholder
            contentDescription = "Cloudy",
            defaultTint = AtomicDesignSystem.colors.Cloudy
        )
        WeatherCondition.RAIN, WeatherCondition.DRIZZLE -> WeatherIconData(
            icon = Icons.Default.AccountBox, // Using available icon as placeholder
            contentDescription = "Rain",
            defaultTint = AtomicDesignSystem.colors.Rainy
        )
        WeatherCondition.SNOW -> WeatherIconData(
            icon = Icons.Default.Star, // Using available icon as placeholder
            contentDescription = "Snow",
            defaultTint = AtomicDesignSystem.colors.Snowy
        )
        WeatherCondition.THUNDERSTORM -> WeatherIconData(
            icon = Icons.Default.Warning, // Using available icon as placeholder
            contentDescription = "Thunderstorm",
            defaultTint = AtomicDesignSystem.colors.Stormy
        )
        WeatherCondition.MIST, WeatherCondition.FOG, WeatherCondition.SMOKE, 
        WeatherCondition.HAZE, WeatherCondition.DUST, WeatherCondition.SAND, 
        WeatherCondition.ASH -> WeatherIconData(
            icon = Icons.Default.Info, // Using info as cloud placeholder
            contentDescription = "Foggy",
            defaultTint = AtomicDesignSystem.colors.Cloudy
        )
        WeatherCondition.SQUALL, WeatherCondition.TORNADO -> WeatherIconData(
            icon = Icons.Default.Warning, // Using available icon as placeholder
            contentDescription = "Severe weather",
            defaultTint = AtomicDesignSystem.colors.Stormy
        )
        WeatherCondition.UNKNOWN -> WeatherIconData(
            icon = Icons.Default.Info, // Using info as help placeholder
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