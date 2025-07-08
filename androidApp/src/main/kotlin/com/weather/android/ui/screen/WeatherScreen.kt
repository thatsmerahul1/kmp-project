package com.weather.android.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weather.android.ui.component.LoadingIndicator
import com.weather.android.ui.component.WeatherItem
import com.weather.android.ui.molecules.LocationHeader
import com.weather.android.ui.molecules.LocationPickerBottomSheet
import com.weather.android.ui.molecules.ErrorStateView
import com.weather.android.ui.molecules.NoDataOfflineView
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import com.weather.presentation.state.WeatherUiEvent
import com.weather.android.presentation.WeatherViewModelBridge
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModelBridge = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState()
    var showLocationBottomSheet by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf(emptyList<com.weather.domain.model.LocationSearchResult>()) }
    var isSearching by remember { mutableStateOf(false) }

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

    LaunchedEffect(uiState.showLocationPicker) {
        showLocationBottomSheet = uiState.showLocationPicker
    }

    WeatherScreenContent(
        uiState = uiState,
        pullToRefreshState = pullToRefreshState,
        onEvent = viewModel::onEvent,
        onLocationClick = { 
            showLocationBottomSheet = true
            viewModel.onEvent(WeatherUiEvent.ShowLocationPicker)
        }
    )
    
    if (showLocationBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { 
                showLocationBottomSheet = false
                viewModel.onEvent(WeatherUiEvent.HideLocationPicker)
            }
        ) {
            LocationPickerBottomSheet(
                onDismiss = { 
                    showLocationBottomSheet = false
                    viewModel.onEvent(WeatherUiEvent.HideLocationPicker)
                },
                onLocationSelected = { location ->
                    viewModel.onEvent(WeatherUiEvent.SelectLocation(location))
                    showLocationBottomSheet = false
                },
                onRequestCurrentLocation = {
                    viewModel.onEvent(WeatherUiEvent.RequestCurrentLocation)
                },
                searchResults = searchResults,
                onSearchQueryChanged = { query ->
                    if (query.isNotBlank()) {
                        isSearching = true
                        viewModel.onEvent(WeatherUiEvent.SearchLocations(query))
                    } else {
                        searchResults = emptyList()
                        isSearching = false
                    }
                },
                isSearching = isSearching,
                isLocationLoading = uiState.isLocationLoading,
                currentLocation = uiState.currentLocation
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeatherScreenContent(
    uiState: com.weather.presentation.state.WeatherUiState,
    pullToRefreshState: androidx.compose.material3.pulltorefresh.PullToRefreshState,
    onEvent: (WeatherUiEvent) -> Unit,
    onLocationClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Weather Forecast",
                            fontWeight = FontWeight.Bold
                        )
                        LocationHeader(
                            currentLocation = uiState.currentLocation,
                            onLocationClick = onLocationClick,
                            isLocationLoading = uiState.isLocationLoading
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            when {
                uiState.isLoading && uiState.weatherList.isEmpty() -> {
                    LoadingIndicator()
                }
                
                uiState.error != null && uiState.weatherList.isEmpty() -> {
                    if (uiState.connectionStatus == com.weather.presentation.state.ConnectionStatus.OFFLINE) {
                        NoDataOfflineView(
                            onRetry = { onEvent(WeatherUiEvent.RetryLoad) }
                        )
                    } else {
                        ErrorStateView(
                            error = uiState.error!!,
                            connectionStatus = uiState.connectionStatus,
                            onRetry = { onEvent(WeatherUiEvent.RetryLoad) }
                        )
                    }
                }
                
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (uiState.weatherList.isNotEmpty()) {
                            items(uiState.weatherList) { weather ->
                                WeatherItem(weather = weather)
                            }
                        }
                        
                        if (uiState.error != null) {
                            item {
                                ErrorCard(
                                    error = uiState.error!!,
                                    onDismiss = { onEvent(WeatherUiEvent.ClearError) }
                                )
                            }
                        }
                    }
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
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Oops! Something went wrong",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Button(onClick = onRetry) {
            Text("Try Again")
        }
    }
}

@Composable
private fun ErrorCard(
    error: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Button(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Dismiss")
            }
        }
    }
}

@Preview
@Composable
fun WeatherScreenPreview() {
    val sampleWeatherList = listOf(
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
            condition = WeatherCondition.CLOUDS,
            temperatureHigh = 18.0,
            temperatureLow = 12.0,
            humidity = 75,
            icon = "02d",
            description = "Few clouds"
        )
    )
    
    LazyColumn {
        items(sampleWeatherList) { weather ->
            WeatherItem(weather = weather)
        }
    }
}