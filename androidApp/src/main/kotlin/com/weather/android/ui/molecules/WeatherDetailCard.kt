package com.weather.android.ui.molecules

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.weather.domain.model.Weather
import com.weather.domain.model.AirQualityCategory

@Composable
fun WeatherDetailCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            content()
        }
    }
}

@Composable
fun TemperatureDetailCard(
    weather: Weather,
    modifier: Modifier = Modifier
) {
    WeatherDetailCard(
        title = "Temperature",
        icon = Icons.Default.Add,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            weather.temperatureCurrent?.let { current ->
                DetailRow("Current", "${current.toInt()}°C", isHighlight = true)
            }
            weather.feelsLike?.let { feelsLike ->
                DetailRow("Feels like", "${feelsLike.toInt()}°C")
            }
            DetailRow("High", "${weather.temperatureHigh.toInt()}°C")
            DetailRow("Low", "${weather.temperatureLow.toInt()}°C")
            weather.dewPoint?.let { dewPoint ->
                DetailRow("Dew point", "${dewPoint.toInt()}°C")
            }
        }
    }
}

@Composable
fun WindDetailCard(
    weather: Weather,
    modifier: Modifier = Modifier
) {
    WeatherDetailCard(
        title = "Wind",
        icon = Icons.Default.ArrowForward,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            weather.windSpeed?.let { speed ->
                DetailRow("Speed", "${speed.toInt()} km/h", isHighlight = true)
            }
            weather.windDirection?.let { direction ->
                DetailRow("Direction", "${direction}° ${getWindDirection(direction)}")
            }
        }
    }
}

@Composable
fun AtmosphericDetailCard(
    weather: Weather,
    modifier: Modifier = Modifier
) {
    WeatherDetailCard(
        title = "Atmospheric",
        icon = Icons.Default.Build,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            weather.pressure?.let { pressure ->
                DetailRow("Pressure", "${pressure.toInt()} hPa", isHighlight = true)
            }
            DetailRow("Humidity", "${weather.humidity}%")
            weather.visibility?.let { visibility ->
                DetailRow("Visibility", "${visibility.toInt()} km")
            }
            weather.cloudCover?.let { cloudCover ->
                DetailRow("Cloud cover", "${cloudCover}%")
            }
        }
    }
}

@Composable
fun PrecipitationDetailCard(
    weather: Weather,
    modifier: Modifier = Modifier
) {
    WeatherDetailCard(
        title = "Precipitation",
        icon = Icons.Default.Clear,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            weather.precipitationChance?.let { chance ->
                DetailRow("Chance", "${chance}%", isHighlight = true)
            }
            weather.precipitationAmount?.let { amount ->
                DetailRow("Amount", "${amount} mm")
            }
        }
    }
}

@Composable
fun UVIndexDetailCard(
    weather: Weather,
    modifier: Modifier = Modifier
) {
    WeatherDetailCard(
        title = "UV Index",
        icon = Icons.Default.Star,
        modifier = modifier
    ) {
        weather.uvIndex?.let { uvIndex ->
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DetailRow("UV Index", uvIndex.toString(), isHighlight = true)
                DetailRow("Category", getUVCategory(uvIndex))
                Text(
                    text = getUVAdvice(uvIndex),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun AirQualityDetailCard(
    weather: Weather,
    modifier: Modifier = Modifier
) {
    weather.airQuality?.let { airQuality ->
        WeatherDetailCard(
            title = "Air Quality",
            icon = Icons.Default.ArrowForward,
            modifier = modifier
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DetailRow("AQI", airQuality.aqi.toString(), isHighlight = true)
                DetailRow("Category", airQuality.category.name.replace("_", " "))
                airQuality.pm25?.let { pm25 ->
                    DetailRow("PM2.5", "${pm25} μg/m³")
                }
                airQuality.pm10?.let { pm10 ->
                    DetailRow("PM10", "${pm10} μg/m³")
                }
            }
        }
    }
}

@Composable
fun SunMoonDetailCard(
    weather: Weather,
    modifier: Modifier = Modifier
) {
    WeatherDetailCard(
        title = "Sun & Moon",
        icon = Icons.Default.Home,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            weather.sunrise?.let { sunrise ->
                DetailRow("Sunrise", sunrise, isHighlight = true)
            }
            weather.sunset?.let { sunset ->
                DetailRow("Sunset", sunset)
            }
            weather.moonPhase?.let { moonPhase ->
                DetailRow("Moon phase", moonPhase)
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    isHighlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            style = if (isHighlight) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isHighlight) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun getWindDirection(degrees: Int): String {
    return when (((degrees + 22.5) / 45).toInt()) {
        0, 8 -> "N"
        1 -> "NE"
        2 -> "E"
        3 -> "SE"
        4 -> "S"
        5 -> "SW"
        6 -> "W"
        7 -> "NW"
        else -> "N"
    }
}

private fun getUVCategory(uvIndex: Int): String {
    return when (uvIndex) {
        in 0..2 -> "Low"
        in 3..5 -> "Moderate"
        in 6..7 -> "High"
        in 8..10 -> "Very High"
        else -> "Extreme"
    }
}

private fun getUVAdvice(uvIndex: Int): String {
    return when (uvIndex) {
        in 0..2 -> "Minimal sun protection required"
        in 3..5 -> "Seek shade during midday hours"
        in 6..7 -> "Sun protection essential"
        in 8..10 -> "Extra protection needed"
        else -> "Take all precautions"
    }
}