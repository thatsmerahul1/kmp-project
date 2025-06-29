package com.weather.data.repository

import app.cash.turbine.test
import com.weather.data.local.LocalWeatherDataSource
import com.weather.data.local.preferences.AppPreferences
import com.weather.data.remote.RemoteWeatherDataSource
import com.weather.domain.model.CacheConfig
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WeatherRepositoryImplTest {

    private val mockLocalDataSource = mockk<LocalWeatherDataSource>(relaxed = true)
    private val mockRemoteDataSource = mockk<RemoteWeatherDataSource>(relaxed = true)
    private val mockAppPreferences = mockk<AppPreferences>(relaxed = true)
    
    private val repository = WeatherRepositoryImpl(
        localDataSource = mockLocalDataSource,
        remoteDataSource = mockRemoteDataSource,
        appPreferences = mockAppPreferences
    )

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

    private val freshWeatherList = listOf(
        Weather(
            date = LocalDate(2024, 1, 15),
            condition = WeatherCondition.CLOUDS,
            temperatureHigh = 23.0,
            temperatureLow = 14.0,
            humidity = 65,
            icon = "02d",
            description = "Few clouds"
        )
    )

    @Test
    fun `getWeatherForecast should emit cached data first, then fresh data`() = runTest {
        // Given
        coEvery { mockAppPreferences.getCacheConfig() } returns CacheConfig()
        coEvery { mockAppPreferences.getApiKey() } returns "test_api_key"
        coEvery { mockAppPreferences.getDefaultLocation() } returns "London,UK"
        
        coEvery { mockLocalDataSource.getWeatherForecasts() } returns sampleWeatherList
        coEvery { mockLocalDataSource.isCacheValid(any()) } returns true
        coEvery { mockRemoteDataSource.getWeatherForecast(any(), any()) } returns freshWeatherList
        
        // When
        val result = repository.getWeatherForecast()
        
        // Then
        result.test {
            // First emission - cached data
            val cachedResult = awaitItem()
            assertTrue(cachedResult.isSuccess)
            assertEquals(sampleWeatherList, cachedResult.getOrNull())
            
            // Second emission - fresh data (different from cached)
            val freshResult = awaitItem()
            assertTrue(freshResult.isSuccess)
            assertEquals(freshWeatherList, freshResult.getOrNull())
            
            awaitComplete()
        }
        
        coVerify { mockLocalDataSource.getWeatherForecasts() }
        coVerify { mockRemoteDataSource.getWeatherForecast("London,UK", "test_api_key") }
        coVerify { mockLocalDataSource.saveWeatherForecasts(freshWeatherList) }
    }

    @Test
    fun `getWeatherForecast should only emit cached data when cache is valid and fresh data is same`() = runTest {
        // Given
        coEvery { mockAppPreferences.getCacheConfig() } returns CacheConfig()
        coEvery { mockAppPreferences.getApiKey() } returns "test_api_key"
        coEvery { mockAppPreferences.getDefaultLocation() } returns "London,UK"
        
        coEvery { mockLocalDataSource.getWeatherForecasts() } returns sampleWeatherList
        coEvery { mockLocalDataSource.isCacheValid(any()) } returns true
        coEvery { mockRemoteDataSource.getWeatherForecast(any(), any()) } returns sampleWeatherList // Same data
        
        // When
        val result = repository.getWeatherForecast()
        
        // Then
        result.test {
            // Only cached data should be emitted since fresh data is same
            val cachedResult = awaitItem()
            assertTrue(cachedResult.isSuccess)
            assertEquals(sampleWeatherList, cachedResult.getOrNull())
            
            awaitComplete()
        }
    }

    @Test
    fun `getWeatherForecast should emit error when no cached data and API fails`() = runTest {
        // Given
        coEvery { mockAppPreferences.getCacheConfig() } returns CacheConfig()
        coEvery { mockAppPreferences.getApiKey() } returns "test_api_key"
        coEvery { mockAppPreferences.getDefaultLocation() } returns "London,UK"
        
        coEvery { mockLocalDataSource.getWeatherForecasts() } returns emptyList()
        coEvery { mockRemoteDataSource.getWeatherForecast(any(), any()) } throws Exception("Network error")
        
        // When
        val result = repository.getWeatherForecast()
        
        // Then
        result.test {
            val errorResult = awaitItem()
            assertTrue(errorResult.isFailure)
            assertEquals("Network error", errorResult.exceptionOrNull()?.message)
            
            awaitComplete()
        }
    }

    @Test
    fun `getWeatherForecast should only emit cached data when API fails but cache exists`() = runTest {
        // Given
        coEvery { mockAppPreferences.getCacheConfig() } returns CacheConfig()
        coEvery { mockAppPreferences.getApiKey() } returns "test_api_key"
        coEvery { mockAppPreferences.getDefaultLocation() } returns "London,UK"
        
        coEvery { mockLocalDataSource.getWeatherForecasts() } returns sampleWeatherList
        coEvery { mockRemoteDataSource.getWeatherForecast(any(), any()) } throws Exception("Network error")
        
        // When
        val result = repository.getWeatherForecast()
        
        // Then
        result.test {
            // Should only emit cached data, no error since cache exists
            val cachedResult = awaitItem()
            assertTrue(cachedResult.isSuccess)
            assertEquals(sampleWeatherList, cachedResult.getOrNull())
            
            awaitComplete()
        }
    }

    @Test
    fun `refreshWeatherForecast should fetch fresh data and save to cache`() = runTest {
        // Given
        coEvery { mockAppPreferences.getApiKey() } returns "test_api_key"
        coEvery { mockAppPreferences.getDefaultLocation() } returns "London,UK"
        coEvery { mockRemoteDataSource.getWeatherForecast(any(), any()) } returns freshWeatherList
        
        // When
        val result = repository.refreshWeatherForecast()
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(freshWeatherList, result.getOrNull())
        
        coVerify { mockRemoteDataSource.getWeatherForecast("London,UK", "test_api_key") }
        coVerify { mockLocalDataSource.saveWeatherForecasts(freshWeatherList) }
    }

    @Test
    fun `refreshWeatherForecast should return error when API key is empty`() = runTest {
        // Given
        coEvery { mockAppPreferences.getApiKey() } returns ""
        
        // When
        val result = repository.refreshWeatherForecast()
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("API key not configured", result.exceptionOrNull()?.message)
    }

    @Test
    fun `clearCache should delegate to local data source`() = runTest {
        // When
        repository.clearCache()
        
        // Then
        coVerify { mockLocalDataSource.clearAllCache() }
    }
}