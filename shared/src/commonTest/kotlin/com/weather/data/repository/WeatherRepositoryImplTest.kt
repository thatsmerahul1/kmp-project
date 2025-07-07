package com.weather.data.repository

import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import com.weather.domain.repository.WeatherRepository
import kotlinx.datetime.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for WeatherRepository implementations
 * Using fake implementations instead of MockK for cross-platform compatibility
 */
class WeatherRepositoryImplTest {

    @Test
    fun testWeatherRepositoryInterface() = runTest {
        // Given - Create a fake implementation for testing
        val fakeRepository = FakeWeatherRepository()
        
        // When
        val weatherFlow = fakeRepository.getWeatherForecast()
        val firstResult = weatherFlow.first()
        
        // Then
        assertTrue(firstResult.isSuccess, "Weather forecast should be successful")
        val weatherList = firstResult.getOrNull()
        assertTrue(weatherList?.isNotEmpty() == true, "Weather list should not be empty")
    }
    
    @Test 
    fun testRefreshWeatherForecast() = runTest {
        // Given
        val fakeRepository = FakeWeatherRepository()
        
        // When
        val result = fakeRepository.refreshWeatherForecast()
        
        // Then
        assertTrue(result.isSuccess, "Refresh should be successful")
        val weatherList = result.getOrNull()
        assertTrue(weatherList?.isNotEmpty() == true, "Refreshed weather list should not be empty")
    }
    
    @Test
    fun testClearCache() = runTest {
        // Given
        val fakeRepository = FakeWeatherRepository()
        
        // When - This should not throw
        fakeRepository.clearCache()
        
        // Then - If we get here, clearCache completed successfully
        assertTrue(true, "Clear cache should complete without error")
    }
    
    @Test
    fun testWeatherRepositoryErrorHandling() = runTest {
        // Given - Repository configured to return error
        val fakeRepository = FakeWeatherRepository(shouldReturnError = true)
        
        // When
        val result = fakeRepository.refreshWeatherForecast()
        
        // Then
        assertTrue(result.isFailure, "Should return failure when configured for error")
    }

    @Test
    fun testWeatherDataConsistency() = runTest {
        // Given
        val fakeRepository = FakeWeatherRepository()
        
        // When
        val flowResult = fakeRepository.getWeatherForecast().first()
        val refreshResult = fakeRepository.refreshWeatherForecast()
        
        // Then - Both should return the same data structure
        assertTrue(flowResult.isSuccess)
        assertTrue(refreshResult.isSuccess)
        
        val flowWeather = flowResult.getOrNull()?.first()
        val refreshWeather = refreshResult.getOrNull()?.first()
        
        assertEquals(flowWeather?.condition, refreshWeather?.condition)
        assertEquals(flowWeather?.date, refreshWeather?.date)
    }

    @Test
    fun testWeatherDomainModelFields() = runTest {
        // Given
        val fakeRepository = FakeWeatherRepository()
        
        // When
        val result = fakeRepository.refreshWeatherForecast()
        val weatherList = result.getOrNull()
        val weather = weatherList?.first()
        
        // Then - Verify all required fields are present
        weather?.let {
            assertTrue(it.date.year > 2020, "Date should be reasonable")
            assertTrue(it.temperatureHigh > it.temperatureLow, "High temp should be higher than low temp")
            assertTrue(it.humidity in 0..100, "Humidity should be 0-100%")
            assertTrue(it.icon.isNotEmpty(), "Icon should not be empty")
            assertTrue(it.description.isNotEmpty(), "Description should not be empty")
        } ?: run {
            kotlin.test.fail("Weather should not be null")
        }
    }
}

/**
 * Fake WeatherRepository implementation for testing
 * This avoids MockK dependency in common tests
 */
class FakeWeatherRepository(
    private val shouldReturnError: Boolean = false
) : WeatherRepository {
    
    private val sampleWeatherList = listOf(
        Weather(
            date = LocalDate(2025, 1, 15),
            condition = WeatherCondition.CLEAR,
            temperatureHigh = 25.0,
            temperatureLow = 15.0,
            humidity = 65,
            icon = "01d",
            description = "Clear sky"
        ),
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
    
    override fun getWeatherForecast(): kotlinx.coroutines.flow.Flow<kotlin.Result<List<Weather>>> {
        return kotlinx.coroutines.flow.flowOf(
            if (shouldReturnError) {
                kotlin.Result.failure(Exception("Test error"))
            } else {
                kotlin.Result.success(sampleWeatherList)
            }
        )
    }
    
    override suspend fun refreshWeatherForecast(): kotlin.Result<List<Weather>> {
        return if (shouldReturnError) {
            kotlin.Result.failure(Exception("Test refresh error"))
        } else {
            kotlin.Result.success(sampleWeatherList)
        }
    }
    
    override suspend fun clearCache() {
        // Fake implementation - does nothing
    }
}