package com.weather.android.ui.templates

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.weather.android.ui.atoms.text.HeadlineText
import com.weather.android.ui.organisms.WeatherListOrganism
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTemplate(
    title: String,
    weatherList: List<Weather>,
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onWeatherClick: ((Weather) -> Unit)? = null,
    topBarActions: @Composable (() -> Unit)? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    HeadlineText(
                        text = title,
                        color = AtomicDesignSystem.colors.OnPrimary
                    )
                },
                actions = {
                    topBarActions?.invoke()
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AtomicDesignSystem.colors.Primary,
                    titleContentColor = AtomicDesignSystem.colors.OnPrimary,
                    actionIconContentColor = AtomicDesignSystem.colors.OnPrimary
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            WeatherListOrganism(
                weatherList = weatherList,
                onRefresh = onRefresh,
                isRefreshing = isRefreshing,
                isLoading = isLoading,
                onWeatherClick = onWeatherClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardTemplatePreview() {
    AtomicTheme {
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
            isRefreshing = false
        )
    }
}