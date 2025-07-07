package com.weather.android.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weather.android.presentation.WeatherViewModelBridge
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme
import com.weather.android.util.WeatherUtils
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import com.weather.presentation.state.WeatherUiEvent
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedWeatherListScreen(
    onWeatherClick: (Weather) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModelBridge = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    
    // Handle pull to refresh
    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing) {
            viewModel.onEvent(WeatherUiEvent.RefreshWeather)
        }
    }
    
    LaunchedEffect(uiState.isRefreshing) {
        if (!uiState.isRefreshing) {
            pullToRefreshState.endRefresh()
        }
    }
    
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Weather Forecast",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = AtomicDesignSystem.colors.OnBackground
                    )
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = AtomicDesignSystem.colors.OnBackground
                ),
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = AtomicDesignSystem.colors.Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AtomicDesignSystem.colors.Background,
                            AtomicDesignSystem.colors.SurfaceContainer
                        )
                    )
                )
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            when {
                uiState.isLoading && uiState.weatherList.isEmpty() -> {
                    LoadingStateView(modifier = Modifier.fillMaxSize())
                }
                
                uiState.error != null && uiState.weatherList.isEmpty() -> {
                    ErrorStateView(
                        error = uiState.error ?: "Unknown error",
                        onRetry = { viewModel.onEvent(WeatherUiEvent.RetryLoad) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                else -> {
                    WeatherListContent(
                        weatherList = uiState.weatherList,
                        onWeatherClick = onWeatherClick,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }
            }
            
            PullToRefreshContainer(
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun WeatherListContent(
    weatherList: List<Weather>,
    onWeatherClick: (Weather) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            horizontal = AtomicDesignSystem.spacing.ScreenPadding,
            vertical = AtomicDesignSystem.spacing.SM
        ),
        verticalArrangement = Arrangement.spacedBy(AtomicDesignSystem.spacing.WeatherCardSpacing)
    ) {
        items(
            items = weatherList,
            key = { weather -> "${weather.date.year}-${weather.date.monthNumber}-${weather.date.dayOfMonth}" }
        ) { weather ->
            EnhancedWeatherRowCard(
                weather = weather,
                onClick = { onWeatherClick(weather) }
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(AtomicDesignSystem.spacing.LG))
        }
    }
}

@Composable
private fun EnhancedWeatherRowCard(
    weather: Weather,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = AtomicDesignSystem.spacing.SM,
                shape = AtomicDesignSystem.shapes.WeatherCard,
                ambientColor = Color.Black.copy(alpha = 0.05f)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
        shape = AtomicDesignSystem.shapes.WeatherCard,
        colors = CardDefaults.cardColors(
            containerColor = AtomicDesignSystem.colors.WeatherCardBackground
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = AtomicDesignSystem.spacing.BorderWidthThin,
            color = AtomicDesignSystem.colors.Outline.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = AtomicDesignSystem.spacing.XL,
                    vertical = AtomicDesignSystem.spacing.MD
                ),
            horizontalArrangement = Arrangement.spacedBy(AtomicDesignSystem.spacing.MD),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date section
            Column(
                modifier = Modifier.width(AtomicDesignSystem.spacing.WeatherCardDateWidth),
                verticalArrangement = Arrangement.spacedBy(AtomicDesignSystem.spacing.XS)
            ) {
                Text(
                    text = getDayName(weather.date),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AtomicDesignSystem.colors.OnSurface
                )
                Text(
                    text = formatDate(weather.date),
                    style = MaterialTheme.typography.labelMedium,
                    color = AtomicDesignSystem.colors.OnSurfaceVariant
                )
            }
            
            // Weather icon and condition
            Column(
                modifier = Modifier.width(AtomicDesignSystem.spacing.WeatherCardIconWidth),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AtomicDesignSystem.spacing.XS)
            ) {
                Text(
                    text = WeatherUtils.getWeatherEmoji(weather.condition),
                    fontSize = 32.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = weather.description,
                    style = MaterialTheme.typography.labelSmall,
                    color = AtomicDesignSystem.colors.OnSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    lineHeight = AtomicDesignSystem.typography.BodySmall.lineHeight
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Temperature and details
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(AtomicDesignSystem.spacing.XS)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AtomicDesignSystem.spacing.SM),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // High temperature
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AtomicDesignSystem.spacing.XXS)
                    ) {
                        Text(
                            text = "HIGH",
                            style = MaterialTheme.typography.labelSmall,
                            color = AtomicDesignSystem.colors.OnSurfaceVariant,
                            letterSpacing = AtomicDesignSystem.typography.LabelSmall.letterSpacing
                        )
                        Text(
                            text = "${weather.temperatureHigh.toInt()}°",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = AtomicDesignSystem.colors.OnSurface
                        )
                    }
                    
                    // Low temperature
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AtomicDesignSystem.spacing.XXS)
                    ) {
                        Text(
                            text = "LOW",
                            style = MaterialTheme.typography.labelSmall,
                            color = AtomicDesignSystem.colors.OnSurfaceVariant,
                            letterSpacing = AtomicDesignSystem.typography.LabelSmall.letterSpacing
                        )
                        Text(
                            text = "${weather.temperatureLow.toInt()}°",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = AtomicDesignSystem.colors.OnSurfaceVariant
                        )
                    }
                }
                
                // Humidity with icon
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AtomicDesignSystem.spacing.XS),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💧",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "${weather.humidity}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = AtomicDesignSystem.colors.OnSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingStateView(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = AtomicDesignSystem.colors.Primary,
            strokeWidth = 4.dp
        )
        
        Spacer(modifier = Modifier.height(AtomicDesignSystem.spacing.LG))
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AtomicDesignSystem.spacing.SM)
        ) {
            Text(
                text = "Loading Weather",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = AtomicDesignSystem.colors.OnBackground
            )
            Text(
                text = "Fetching the latest forecast...",
                style = MaterialTheme.typography.bodyMedium,
                color = AtomicDesignSystem.colors.OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorStateView(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(AtomicDesignSystem.spacing.XXL),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AtomicDesignSystem.spacing.MD)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                modifier = Modifier.size(60.dp),
                tint = AtomicDesignSystem.colors.Error
            )
            
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AtomicDesignSystem.colors.OnBackground
            )
            
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = AtomicDesignSystem.colors.OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(AtomicDesignSystem.spacing.LG))
        
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = AtomicDesignSystem.colors.Primary
            ),
            shape = AtomicDesignSystem.shapes.Button
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(AtomicDesignSystem.spacing.SM),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retry",
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Try Again",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Utility functions
private fun getDayName(date: LocalDate): String {
    val calendar = Calendar.getInstance()
    calendar.set(date.year, date.monthNumber - 1, date.dayOfMonth)
    
    val formatter = SimpleDateFormat("EEEE", Locale.getDefault())
    return formatter.format(calendar.time)
}

private fun formatDate(date: LocalDate): String {
    val calendar = Calendar.getInstance()
    calendar.set(date.year, date.monthNumber - 1, date.dayOfMonth)
    
    val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
    return formatter.format(calendar.time)
}

@Preview
@Composable
fun EnhancedWeatherListScreenPreview() {
    AtomicTheme {
        val sampleWeatherList = listOf(
            Weather(
                date = LocalDate(2024, 1, 15),
                condition = WeatherCondition.CLEAR,
                temperatureHigh = 25.0,
                temperatureLow = 15.0,
                humidity = 65,
                icon = "01d",
                description = "Clear sky"
            ),
            Weather(
                date = LocalDate(2024, 1, 16),
                condition = WeatherCondition.RAIN,
                temperatureHigh = 22.0,
                temperatureLow = 12.0,
                humidity = 80,
                icon = "10d",
                description = "Light rain"
            ),
            Weather(
                date = LocalDate(2024, 1, 17),
                condition = WeatherCondition.CLOUDS,
                temperatureHigh = 20.0,
                temperatureLow = 10.0,
                humidity = 70,
                icon = "04d",
                description = "Cloudy"
            )
        )
        
        WeatherListContent(
            weatherList = sampleWeatherList,
            onWeatherClick = { },
            modifier = Modifier.fillMaxSize()
        )
    }
}