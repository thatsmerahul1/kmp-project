package com.weather.android.ui.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import com.weather.android.ui.atoms.loading.ShimmerBox
import com.weather.android.ui.theme.AtomicDesignSystem
import com.weather.android.ui.theme.AtomicTheme
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherListOrganism(
    weatherList: List<Weather>,
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onWeatherClick: ((Weather) -> Unit)? = null
) {
    val pullRefreshState = rememberPullToRefreshState()

    LaunchedEffect(pullRefreshState.isRefreshing) {
        if (pullRefreshState.isRefreshing) {
            onRefresh()
        }
    }

    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) {
            pullRefreshState.endRefresh()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(pullRefreshState.nestedScrollConnection)
    ) {
        when {
            isLoading && weatherList.isEmpty() -> {
                WeatherListSkeleton()
            }
            
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(AtomicDesignSystem.spacing.ScreenPadding),
                    verticalArrangement = Arrangement.spacedBy(AtomicDesignSystem.spacing.WeatherCardSpacing),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = weatherList,
                        key = { it.date.toEpochDays() }
                    ) { weather ->
                        WeatherCard(
                            weather = weather,
                            onClick = onWeatherClick?.let { { it(weather) } }
                        )
                    }
                }
            }
        }

        PullToRefreshContainer(
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun WeatherListSkeleton() {
    LazyColumn(
        contentPadding = PaddingValues(AtomicDesignSystem.spacing.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(AtomicDesignSystem.spacing.WeatherCardSpacing)
    ) {
        items(7) {
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AtomicDesignSystem.spacing.WeatherCardPadding)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WeatherListOrganismPreview() {
    AtomicTheme {
        WeatherListOrganism(
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

@Preview(showBackground = true)
@Composable
private fun WeatherListSkeletonPreview() {
    AtomicTheme {
        WeatherListSkeleton()
    }
}