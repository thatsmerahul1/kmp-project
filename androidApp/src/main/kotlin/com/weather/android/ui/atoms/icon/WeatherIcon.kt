package com.weather.android.ui.atoms.icon

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.weather.android.R
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
        painter = painterResource(id = iconData.iconRes),
        contentDescription = iconData.contentDescription,
        tint = if (tint == Color.Unspecified) iconData.defaultTint else tint,
        modifier = modifier.size(size)
    )
}

private data class WeatherIconData(
    val iconRes: Int,
    val contentDescription: String,
    val defaultTint: Color
)

@Composable
private fun getWeatherIconData(condition: WeatherCondition): WeatherIconData {
    return when (condition) {
        WeatherCondition.CLEAR -> WeatherIconData(
            iconRes = R.drawable.ic_weather_sunny,
            contentDescription = "Clear sky",
            defaultTint = AtomicDesignSystem.colors.Sunny
        )
        WeatherCondition.CLOUDS -> WeatherIconData(
            iconRes = R.drawable.ic_weather_cloudy,
            contentDescription = "Cloudy",
            defaultTint = AtomicDesignSystem.colors.Cloudy
        )
        WeatherCondition.RAIN -> WeatherIconData(
            iconRes = R.drawable.ic_weather_rainy,
            contentDescription = "Rain",
            defaultTint = AtomicDesignSystem.colors.Rainy
        )
        WeatherCondition.DRIZZLE -> WeatherIconData(
            iconRes = R.drawable.ic_weather_drizzle,
            contentDescription = "Drizzle",
            defaultTint = AtomicDesignSystem.colors.Rainy
        )
        WeatherCondition.SNOW -> WeatherIconData(
            iconRes = R.drawable.ic_weather_snow,
            contentDescription = "Snow",
            defaultTint = AtomicDesignSystem.colors.Snowy
        )
        WeatherCondition.THUNDERSTORM -> WeatherIconData(
            iconRes = R.drawable.ic_weather_thunderstorm,
            contentDescription = "Thunderstorm",
            defaultTint = AtomicDesignSystem.colors.Stormy
        )
        WeatherCondition.MIST, WeatherCondition.FOG -> WeatherIconData(
            iconRes = R.drawable.ic_weather_fog,
            contentDescription = "Foggy",
            defaultTint = AtomicDesignSystem.colors.Cloudy
        )
        WeatherCondition.SMOKE, WeatherCondition.HAZE, WeatherCondition.DUST, 
        WeatherCondition.SAND, WeatherCondition.ASH -> WeatherIconData(
            iconRes = R.drawable.ic_weather_fog,
            contentDescription = "Atmospheric conditions",
            defaultTint = AtomicDesignSystem.colors.Cloudy
        )
        WeatherCondition.SQUALL, WeatherCondition.TORNADO -> WeatherIconData(
            iconRes = R.drawable.ic_weather_thunderstorm,
            contentDescription = "Severe weather",
            defaultTint = AtomicDesignSystem.colors.Stormy
        )
        WeatherCondition.UNKNOWN -> WeatherIconData(
            iconRes = R.drawable.ic_weather_cloudy,
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