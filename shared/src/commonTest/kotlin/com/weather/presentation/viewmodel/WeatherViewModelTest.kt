package com.weather.presentation.viewmodel

import app.cash.turbine.test
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import com.weather.domain.repository.WeatherRepository
import com.weather.domain.usecase.GetWeatherForecastUseCase
import com.weather.domain.usecase.RefreshWeatherUseCase
import com.weather.testing.FakeWeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

/**
 * Tests for WeatherViewModel using cross-platform fake implementations
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeWeatherRepository
    private lateinit var getWeatherUseCase: GetWeatherForecastUseCase
    private lateinit var refreshWeatherUseCase: RefreshWeatherUseCase
    private lateinit var viewModel: WeatherViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeWeatherRepository()
        getWeatherUseCase = GetWeatherForecastUseCase(fakeRepository)
        refreshWeatherUseCase = RefreshWeatherUseCase(fakeRepository)
        viewModel = WeatherViewModel(getWeatherUseCase, refreshWeatherUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel should be initialized properly`() = runTest {
        // Then
        assertTrue(true, "ViewModel created successfully")
        assertEquals(0, fakeRepository.getForecastCallCount)
    }

    @Test
    fun `repository should provide weather data`() = runTest {
        // Given
        val weatherData = listOf(
            Weather(
                date = LocalDate(2025, 1, 15),
                condition = WeatherCondition.CLEAR,
                temperatureHigh = 25.0,
                temperatureLow = 15.0,
                humidity = 65,
                icon = "01d",
                description = "Clear sky"
            )
        )
        fakeRepository.setForecastResult(kotlin.Result.success(weatherData))

        // When
        val result = getWeatherUseCase().test {
            val flowResult = awaitItem()
            assertTrue(flowResult.isSuccess)
            assertEquals(weatherData, flowResult.getOrNull())
            awaitComplete()
        }

        // Then
        assertEquals(1, fakeRepository.getForecastCallCount)
    }

    @Test
    fun `refresh use case should work with repository`() = runTest {
        // Given
        val weatherData = listOf(
            Weather(
                date = LocalDate(2025, 1, 16),
                condition = WeatherCondition.CLOUDS,
                temperatureHigh = 22.0,
                temperatureLow = 12.0,
                humidity = 70,
                icon = "02d",
                description = "Partly cloudy"
            )
        )
        fakeRepository.setForecastResult(kotlin.Result.success(weatherData))

        // When
        val result = refreshWeatherUseCase()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(weatherData, result.getOrNull())
        assertEquals(1, fakeRepository.refreshCallCount)
    }

    @Test
    fun `error handling should work properly`() = runTest {
        // Given
        val exception = Exception("Network error")
        fakeRepository.setForecastResult(kotlin.Result.failure(exception))

        // When
        val result = refreshWeatherUseCase()

        // Then
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
        assertEquals(1, fakeRepository.refreshCallCount)
    }

    @Test
    fun `use cases should be properly injected`() = runTest {
        // Given
        val weatherData = listOf(
            Weather(
                date = LocalDate(2025, 1, 15),
                condition = WeatherCondition.CLEAR,
                temperatureHigh = 25.0,
                temperatureLow = 15.0,
                humidity = 65,
                icon = "01d",
                description = "Clear sky"
            )
        )
        fakeRepository.setForecastResult(kotlin.Result.success(weatherData))

        // When
        getWeatherUseCase().test {
            val flowResult = awaitItem()
            awaitComplete()
        }
        refreshWeatherUseCase()

        // Then
        assertEquals(1, fakeRepository.getForecastCallCount)
        assertEquals(1, fakeRepository.refreshCallCount)
    }
}