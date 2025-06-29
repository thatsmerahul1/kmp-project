package com.weather.domain.usecase

import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import com.weather.domain.repository.WeatherRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RefreshWeatherUseCaseTest {

    private val mockRepository = mockk<WeatherRepository>()
    private val useCase = RefreshWeatherUseCase(mockRepository)

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
    fun `invoke should return refreshed weather forecast from repository`() = runTest {
        // Given
        coEvery { mockRepository.refreshWeatherForecast() } returns Result.success(sampleWeatherList)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(sampleWeatherList, result.getOrNull())
        coVerify { mockRepository.refreshWeatherForecast() }
    }

    @Test
    fun `invoke should return error from repository`() = runTest {
        // Given
        val errorMessage = "Refresh failed"
        coEvery { mockRepository.refreshWeatherForecast() } returns Result.failure(Exception(errorMessage))

        // When
        val result = useCase()

        // Then
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
        coVerify { mockRepository.refreshWeatherForecast() }
    }
}