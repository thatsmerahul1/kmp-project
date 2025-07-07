package com.weather.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.util.WeatherUtils
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import kotlinx.datetime.LocalDate
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailScreen(
    weather: Weather,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Weather Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Location and date header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Bengaluru, India",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = formatDetailDate(weather.date),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Enhanced main weather card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = Color.Black.copy(alpha = 0.1f)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = getWeatherGradient(weather.condition)
                        )
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Weather icon
                        Text(
                            text = WeatherUtils.getWeatherEmoji(weather.condition),
                            fontSize = 100.sp,
                            textAlign = TextAlign.Center
                        )

                        // Weather description
                        Text(
                            text = weather.description,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )

                        // Temperature section
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Current temperature (using high temp as current)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "CURRENT",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.8f),
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "${weather.temperatureHigh.toInt()}°",
                                    fontSize = 64.sp,
                                    fontWeight = FontWeight.Thin,
                                    color = Color.White
                                )
                            }

                            // High/Low temperatures
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(60.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // High temperature
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = "High",
                                        tint = Color.Red.copy(alpha = 0.8f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "HIGH",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.8f),
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "${weather.temperatureHigh.toInt()}°",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }

                                // Vertical divider
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(50.dp)
                                        .background(Color.White.copy(alpha = 0.3f))
                                )

                                // Low temperature
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Low",
                                        tint = Color.Blue.copy(alpha = 0.8f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "LOW",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.8f),
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "${weather.temperatureLow.toInt()}°",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Enhanced detail cards grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.height(260.dp)
            ) {
                items(getDetailCards(weather)) { card ->
                    EnhancedDetailCard(
                        icon = card.icon,
                        title = card.title,
                        value = card.value,
                        subtitle = card.subtitle
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun EnhancedDetailCard(
    icon: String,
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.05f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AtomicDesignSystem.colors.WeatherCardBackground
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineMedium
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )

                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// Helper functions
private fun getWeatherGradient(condition: WeatherCondition): Brush {
    return when (condition) {
        WeatherCondition.CLEAR -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFFF9A56).copy(alpha = 0.9f),
                Color(0xFFFFD93D).copy(alpha = 0.8f)
            )
        )
        WeatherCondition.CLOUDS -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF9E9E9E).copy(alpha = 0.9f),
                Color(0xFF64B5F6).copy(alpha = 0.8f)
            )
        )
        WeatherCondition.RAIN -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF42A5F5).copy(alpha = 0.9f),
                Color(0xFF3F51B5).copy(alpha = 0.8f)
            )
        )
        WeatherCondition.SNOW -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFE3F2FD).copy(alpha = 0.9f),
                Color(0xFFBBDEFB).copy(alpha = 0.8f)
            )
        )
        else -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF64B5F6).copy(alpha = 0.9f),
                Color(0xFF9C27B0).copy(alpha = 0.8f)
            )
        )
    }
}

private data class DetailCardData(
    val icon: String,
    val title: String,
    val value: String,
    val subtitle: String
)

private fun getDetailCards(weather: Weather): List<DetailCardData> {
    return listOf(
        DetailCardData(
            icon = "💧",
            title = "Humidity",
            value = "${weather.humidity}%",
            subtitle = getHumidityDescription(weather.humidity.toInt())
        ),
        DetailCardData(
            icon = WeatherUtils.getWeatherEmoji(weather.condition),
            title = "Condition",
            value = weather.condition.name.lowercase()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
            subtitle = "Today"
        ),
        DetailCardData(
            icon = "🌡️",
            title = "Feels like",
            value = "${weather.temperatureHigh.toInt()}°",
            subtitle = "Actual temp"
        ),
        DetailCardData(
            icon = "📊",
            title = "Weather Index",
            value = getWeatherIndex(weather),
            subtitle = "Overall rating"
        )
    )
}

private fun getHumidityDescription(humidity: Int): String {
    return when (humidity) {
        in 0..30 -> "Dry"
        in 31..60 -> "Comfortable"
        in 61..80 -> "Humid"
        else -> "Very Humid"
    }
}

private fun getWeatherIndex(weather: Weather): String {
    val tempScore = if (weather.temperatureHigh > 20 && weather.temperatureHigh < 30) 1 else 0
    val humidityScore = if (weather.humidity > 30 && weather.humidity < 70) 1 else 0
    val total = tempScore + humidityScore

    return when (total) {
        2 -> "Excellent"
        1 -> "Good"
        else -> "Fair"
    }
}

private fun formatDetailDate(date: LocalDate): String {
    return "${date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }}, ${
        date.month.name.lowercase().replaceFirstChar { it.uppercase() }
    } ${date.dayOfMonth}"
}

@Preview
@Composable
fun WeatherDetailScreenPreview() {
    val sampleWeather = Weather(
        date = LocalDate(2024, 1, 15),
        condition = WeatherCondition.CLEAR,
        temperatureHigh = 25.0,
        temperatureLow = 15.0,
        humidity = 65,
        icon = "01d",
        description = "Clear sky with pleasant weather"
    )

    WeatherDetailScreen(
        weather = sampleWeather,
        onBackClick = {}
    )
}