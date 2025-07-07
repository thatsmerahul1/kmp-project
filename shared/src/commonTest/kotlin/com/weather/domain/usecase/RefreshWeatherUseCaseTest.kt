package com.weather.domain.usecase

import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import com.weather.domain.repository.WeatherRepository
import com.weather.testing.FakeWeatherRepository
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for RefreshWeatherUseCase using cross-platform fake implementations
 */
class RefreshWeatherUseCaseTest {

    private val fakeRepository = FakeWeatherRepository()
    private val useCase = RefreshWeatherUseCase(fakeRepository)

    @Test
    fun `invoke should refresh weather from repository successfully`() = runTest {
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
        val result = useCase()

        // Then
        assertTrue(result.isSuccess, "Use case should return successful result")
        assertEquals(expectedWeather, result.getOrNull())
        assertEquals(1, fakeRepository.refreshCallCount)
    }

    @Test
    fun `invoke should return error when repository refresh fails`() = runTest {
        // Given
        val expectedException = Exception("Network error")
        fakeRepository.setForecastResult(kotlin.Result.failure(expectedException))

        // When
        val result = useCase()

        // Then
        assertTrue(result.isFailure, "Use case should return failure when repository fails")
        assertEquals("Network error", result.exceptionOrNull()?.message)
        assertEquals(1, fakeRepository.refreshCallCount)
    }

    @Test
    fun `invoke should call repository refreshWeatherForecast`() = runTest {
        // Given
        fakeRepository.reset()

        // When
        useCase()

        // Then
        assertEquals(1, fakeRepository.refreshCallCount)
    }
}