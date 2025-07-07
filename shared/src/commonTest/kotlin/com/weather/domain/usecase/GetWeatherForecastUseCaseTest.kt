package com.weather.domain.usecase

import app.cash.turbine.test
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import com.weather.domain.repository.WeatherRepository
import com.weather.testing.FakeWeatherRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for GetWeatherForecastUseCase using cross-platform fake implementations
 */
class GetWeatherForecastUseCaseTest {

    private val fakeRepository = FakeWeatherRepository()
    private val useCase = GetWeatherForecastUseCase(fakeRepository)

    @Test
    fun `invoke should return weather forecast from repository`() = runTest {
        // Given
        val expectedWeather = listOf(
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
        fakeRepository.setForecastResult(kotlin.Result.success(expectedWeather))

        // When
        val flow = useCase()

        // Then
        flow.test {
            val result = awaitItem()
            assertTrue(result.isSuccess, "Use case should return successful result")
            assertEquals(expectedWeather, result.getOrNull())
            awaitComplete()
        }
        
        assertEquals(1, fakeRepository.getForecastCallCount)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        // Given
        val expectedException = Exception("Network error")
        fakeRepository.setForecastResult(kotlin.Result.failure(expectedException))

        // When
        val flow = useCase()

        // Then
        flow.test {
            val result = awaitItem()
            assertTrue(result.isFailure, "Use case should return failure when repository fails")
            assertEquals("Network error", result.exceptionOrNull()?.message)
            awaitComplete()
        }
        
        assertEquals(1, fakeRepository.getForecastCallCount)
    }

    @Test
    fun `invoke should call repository getWeatherForecast`() = runTest {
        // Given
        fakeRepository.reset()

        // When
        val flow = useCase()
        flow.test {
            awaitItem()
            awaitComplete()
        }

        // Then
        assertEquals(1, fakeRepository.getForecastCallCount)
    }
}