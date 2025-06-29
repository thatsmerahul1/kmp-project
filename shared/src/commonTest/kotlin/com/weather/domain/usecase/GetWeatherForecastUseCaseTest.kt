package com.weather.domain.usecase

import app.cash.turbine.test
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import com.weather.domain.repository.WeatherRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetWeatherForecastUseCaseTest {

    private val mockRepository = mockk<WeatherRepository>()
    private val useCase = GetWeatherForecastUseCase(mockRepository)

    private val sampleWeatherList = listOf(
        Weather(
            date = LocalDate(2024, 1, 15),
            condition = WeatherCondition.CLEAR,
            temperatureHigh = 25.0,
            temperatureLow = 15.0,
            humidity = 60,
            icon = "01d",
            description = "Clear sky"
        )
    )

    @Test
    fun `invoke should return weather forecast from repository`() = runTest {
        // Given
        every { mockRepository.getWeatherForecast() } returns flowOf(Result.success(sampleWeatherList))

        // When
        val result = useCase()

        // Then
        result.test {
            val weatherResult = awaitItem()
            assertTrue(weatherResult.isSuccess)
            assertEquals(sampleWeatherList, weatherResult.getOrNull())
            awaitComplete()
        }

        verify { mockRepository.getWeatherForecast() }
    }

    @Test
    fun `invoke should return error from repository`() = runTest {
        // Given
        val errorMessage = "Network error"
        every { mockRepository.getWeatherForecast() } returns flowOf(Result.failure(Exception(errorMessage)))

        // When
        val result = useCase()

        // Then
        result.test {
            val weatherResult = awaitItem()
            assertTrue(weatherResult.isFailure)
            assertEquals(errorMessage, weatherResult.exceptionOrNull()?.message)
            awaitComplete()
        }

        verify { mockRepository.getWeatherForecast() }
    }
}