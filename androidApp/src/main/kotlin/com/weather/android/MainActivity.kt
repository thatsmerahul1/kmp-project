package com.weather.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.weather.android.presentation.WeatherViewModelBridge
import com.weather.android.ui.pages.WeatherDashboard
import com.weather.android.ui.screen.WeatherDetailScreen
import com.weather.android.ui.theme.AtomicTheme
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AtomicTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherApp()
                }
            }
        }
    }
}

@Composable
fun WeatherApp(
    navController: NavHostController = rememberNavController(),
    viewModel: WeatherViewModelBridge = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    NavHost(
        navController = navController,
        startDestination = "weather_list"
    ) {
        composable("weather_list") {
            WeatherDashboard(
                onWeatherClick = { weather ->
                    navController.navigate("weather_detail/${weather.date.toEpochDays()}")
                }
            )
        }
        
        composable("weather_detail/{dateEpochDays}") { backStackEntry ->
            val dateEpochDays = backStackEntry.arguments?.getString("dateEpochDays")?.toLongOrNull()
            val weather = dateEpochDays?.let { epochDays ->
                uiState.weatherList.find { it.date.toEpochDays().toLong() == epochDays }
            }
            
            weather?.let {
                WeatherDetailScreen(
                    weather = it,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}