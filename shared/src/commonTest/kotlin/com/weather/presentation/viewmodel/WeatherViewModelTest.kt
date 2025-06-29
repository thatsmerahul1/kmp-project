package com.weather.presentation.viewmodel

import app.cash.turbine.test
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import com.weather.domain.repository.WeatherRepository
import com.weather.domain.usecase.GetWeatherForecastUseCase
import com.weather.domain.usecase.RefreshWeatherUseCase
import com.weather.presentation.state.WeatherUiEvent
import com.weather.presentation.state.WeatherUiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mockRepository = mockk<WeatherRepository>()
    private lateinit var getWeatherUseCase: GetWeatherForecastUseCase
    private lateinit var refreshWeatherUseCase: RefreshWeatherUseCase
    private lateinit var viewModel: WeatherViewModel

    private val sampleWeatherList = listOf(
        Weather(
            date = LocalDate(2024, 1, 15),
            condition = WeatherCondition.CLEAR,
            temperatureHigh = 25.0,
            temperatureLow = 15.0,
            humidity = 60,
            icon = "01d",
            description = "Clear sky"
        ),
        Weather(
            date = LocalDate(2024, 1, 16),
            condition = WeatherCondition.CLOUDS,
            temperatureHigh = 22.0,
            temperatureLow = 12.0,
            humidity = 70,
            icon = "02d",
            description = "Few clouds"
        )
    )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getWeatherUseCase = GetWeatherForecastUseCase(mockRepository)
        refreshWeatherUseCase = RefreshWeatherUseCase(mockRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be empty`() {
        // Given
        coEvery { mockRepository.getWeatherForecast() } returns flowOf(Result.success(emptyList()))
        
        // When
        viewModel = WeatherViewModel(getWeatherUseCase, refreshWeatherUseCase)
        
        // Then
        val initialState = viewModel.uiState.value
        assertTrue(initialState.weatherList.isEmpty())
        assertFalse(initialState.isLoading)
        assertNull(initialState.error)
    }

    @Test
    fun `loading weather should emit loading state then success`() = runTest {
        // Given
        coEvery { mockRepository.getWeatherForecast() } returns flowOf(Result.success(sampleWeatherList))
        
        // When
        viewModel = WeatherViewModel(getWeatherUseCase, refreshWeatherUseCase)
        
        // Then
        viewModel.uiState.test {
            // Initial state (loading starts automatically in init)
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertTrue(loadingState.weatherList.isEmpty())
            
            // Success state
            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertEquals(sampleWeatherList, successState.weatherList)
            assertNull(successState.error)
        }
    }

    @Test
    fun `loading weather should emit error state when repository fails`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { mockRepository.getWeatherForecast() } returns flowOf(Result.failure(Exception(errorMessage)))
        
        // When
        viewModel = WeatherViewModel(getWeatherUseCase, refreshWeatherUseCase)
        
        // Then
        viewModel.uiState.test {
            // Initial state (loading starts automatically in init)
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            
            // Error state
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals(errorMessage, errorState.error)
            assertTrue(errorState.weatherList.isEmpty())
        }
    }

    @Test
    fun `refresh weather should emit refreshing state then success`() = runTest {
        // Given
        coEvery { mockRepository.getWeatherForecast() } returns flowOf(Result.success(sampleWeatherList))
        coEvery { mockRepository.refreshWeatherForecast() } returns Result.success(sampleWeatherList)
        
        viewModel = WeatherViewModel(getWeatherUseCase, refreshWeatherUseCase)
        
        // When
        viewModel.onEvent(WeatherUiEvent.RefreshWeather)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val currentState = awaitItem()
            assertFalse(currentState.isRefreshing)
            assertEquals(sampleWeatherList, currentState.weatherList)
        }
    }

    @Test
    fun `clear error should remove error from state`() = runTest {
        // Given
        coEvery { mockRepository.getWeatherForecast() } returns flowOf(Result.failure(Exception("Error")))
        
        viewModel = WeatherViewModel(getWeatherUseCase, refreshWeatherUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.onEvent(WeatherUiEvent.ClearError)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val currentState = awaitItem()
            assertNull(currentState.error)
        }
    }

    @Test
    fun `retry load should trigger weather loading again`() = runTest {
        // Given
        coEvery { mockRepository.getWeatherForecast() } returns flowOf(Result.success(sampleWeatherList))
        
        viewModel = WeatherViewModel(getWeatherUseCase, refreshWeatherUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.onEvent(WeatherUiEvent.RetryLoad)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        viewModel.uiState.test {
            val currentState = awaitItem()
            assertEquals(sampleWeatherList, currentState.weatherList)
            assertFalse(currentState.isLoading)
        }
    }
}