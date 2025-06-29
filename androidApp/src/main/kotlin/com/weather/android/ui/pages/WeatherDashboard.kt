package com.weather.android.ui.pages

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.weather.android.presentation.WeatherViewModelBridge
import com.weather.android.ui.atoms.button.AtomicIconButton
import com.weather.android.ui.organisms.ErrorCard
import com.weather.android.ui.organisms.ErrorType
import com.weather.android.ui.templates.DashboardTemplate
import com.weather.android.ui.templates.ErrorTemplate
import com.weather.android.ui.theme.AtomicTheme
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import com.weather.presentation.state.WeatherUiEvent
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.koinViewModel

@Composable
fun WeatherDashboard(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModelBridge = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        // Show error template if no data and has error
        uiState.error != null && uiState.weatherList.isEmpty() -> {
            ErrorTemplate(
                title = "Weather Forecast",
                errorMessage = uiState.error!!,
                onRetry = { viewModel.onEvent(WeatherUiEvent.RetryLoad) },
                errorType = if (uiState.isOffline) ErrorType.NETWORK else ErrorType.SERVER,
                modifier = modifier
            )
        }
        
        // Show dashboard with data (may include error card for partial failures)
        else -> {
            DashboardTemplate(
                title = "Weather Forecast",
                weatherList = uiState.weatherList,
                onRefresh = { viewModel.onEvent(WeatherUiEvent.RefreshWeather) },
                isRefreshing = uiState.isRefreshing,
                isLoading = uiState.isLoading,
                onWeatherClick = { weather ->
                    // TODO: Navigate to weather detail
                },
                topBarActions = {
                    AtomicIconButton(
                        icon = Icons.Default.Refresh,
                        contentDescription = "Refresh weather",
                        onClick = { viewModel.onEvent(WeatherUiEvent.RefreshWeather) }
                    )
                },
                modifier = modifier
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WeatherDashboardPreview() {
    AtomicTheme {
        // Mock dashboard with sample data
        DashboardTemplate(
            title = "Weather Forecast",
            weatherList = listOf(
                Weather(
                    date = LocalDate(2024, 1, 15),
                    condition = WeatherCondition.CLEAR,
                    temperatureHigh = 22.0,
                    temperatureLow = 15.0,
                    humidity = 65,
                    icon = "01d",
                    description = "Clear sky"
                ),
                Weather(
                    date = LocalDate(2024, 1, 16),
                    condition = WeatherCondition.RAIN,
                    temperatureHigh = 18.0,
                    temperatureLow = 12.0,
                    humidity = 85,
                    icon = "10d",
                    description = "Light rain"
                )
            ),
            onRefresh = {},
            isRefreshing = false,
            topBarActions = {
                AtomicIconButton(
                    icon = Icons.Default.Refresh,
                    contentDescription = "Refresh weather",
                    onClick = {}
                )
            }
        )
    }
}