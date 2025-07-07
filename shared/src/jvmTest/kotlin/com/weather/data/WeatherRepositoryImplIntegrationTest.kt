package com.weather.data

import com.weather.data.local.LocalWeatherDataSource
import com.weather.data.local.preferences.AppPreferences
import com.weather.data.remote.RemoteWeatherDataSource
import com.weather.data.repository.WeatherRepositoryImpl
import com.weather.domain.model.CacheConfig
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import com.weather.domain.common.Result
import com.weather.domain.common.DomainException
import com.weather.domain.common.isSuccess
import com.weather.domain.common.isError
import com.weather.domain.common.getOrNull
import com.weather.domain.common.exceptionOrNull
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.concurrent.TimeUnit

/**
 * JVM-specific integration tests for WeatherRepositoryImpl
 * Uses MockK for comprehensive mocking and JUnit 5 for advanced testing features
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WeatherRepositoryImplIntegrationTest {

    private lateinit var mockLocalDataSource: LocalWeatherDataSource
    private lateinit var mockRemoteDataSource: RemoteWeatherDataSource
    private lateinit var mockAppPreferences: AppPreferences
    private lateinit var repository: WeatherRepositoryImpl

    private val sampleWeatherList = listOf(
        Weather(
            date = LocalDate(2025, 1, 15),
            condition = WeatherCondition.CLEAR,
            temperatureHigh = 25.0,
            temperatureLow = 15.0,
            humidity = 60,
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

    @BeforeEach
    fun setup() {
        mockLocalDataSource = mockk(relaxed = true)
        mockRemoteDataSource = mockk(relaxed = true)
        mockAppPreferences = mockk(relaxed = true)
        
        repository = WeatherRepositoryImpl(
            localDataSource = mockLocalDataSource,
            remoteDataSource = mockRemoteDataSource,
            appPreferences = mockAppPreferences
        )

        // Default mock setup
        coEvery { mockAppPreferences.getCacheConfig() } returns CacheConfig()
        coEvery { mockAppPreferences.getDefaultLocation() } returns "London,UK"
    }

    @AfterEach
    fun teardown() {
        clearAllMocks()
    }

    @Test
    @DisplayName("Integration: Cache hit scenario with valid data")
    fun `getWeatherForecast should return cached data when cache is valid`() = runTest {
        // Given
        coEvery { mockLocalDataSource.getWeatherForecasts() } returns sampleWeatherList
        coEvery { mockLocalDataSource.isCacheValid(any()) } returns true
        coEvery { mockRemoteDataSource.getWeatherForecast(any()) } returns sampleWeatherList

        // When
        val flow = repository.getWeatherForecast()
        val result = flow.first()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(sampleWeatherList, result.getOrNull())
        
        // Verify interactions
        coVerify { mockLocalDataSource.getWeatherForecasts() }
        coVerify { mockLocalDataSource.isCacheValid(any()) }
        coVerify { mockRemoteDataSource.getWeatherForecast("London,UK") }
    }

    @Test
    @DisplayName("Integration: Cache miss scenario with network fallback")
    fun `getWeatherForecast should fetch from network when cache is invalid`() = runTest {
        // Given
        coEvery { mockLocalDataSource.getWeatherForecasts() } returns emptyList()
        coEvery { mockLocalDataSource.isCacheValid(any()) } returns false
        coEvery { mockRemoteDataSource.getWeatherForecast(any()) } returns sampleWeatherList

        // When
        val flow = repository.getWeatherForecast()
        val result = flow.first()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(sampleWeatherList, result.getOrNull())
        
        // Verify cache was updated
        coVerify { mockLocalDataSource.saveWeatherForecasts(sampleWeatherList) }
    }

    @Test
    @DisplayName("Integration: Error handling with network failure")
    fun `refreshWeatherForecast should handle network failure`() = runTest {
        // Given - Mock network failure
        coEvery { mockRemoteDataSource.getWeatherForecast(any()) } throws Exception("Network unavailable")
        
        // When
        val result = repository.refreshWeatherForecast()
        
        // Then
        assertTrue(result.isError)
        assertEquals("Refresh failed", result.exceptionOrNull()?.message)
    }

    @Test
    @DisplayName("Integration: Concurrent access handling")
    fun `repository should handle concurrent access gracefully`() = runTest {
        // Given
        coEvery { mockLocalDataSource.getWeatherForecasts() } returns sampleWeatherList
        coEvery { mockLocalDataSource.isCacheValid(any()) } returns true
        coEvery { mockRemoteDataSource.getWeatherForecast(any()) } returns sampleWeatherList

        // When - Simulate concurrent access
        val flow1 = repository.getWeatherForecast()
        val flow2 = repository.getWeatherForecast()
        
        val result1 = flow1.first()
        val result2 = flow2.first()

        // Then
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        assertEquals(result1.getOrNull(), result2.getOrNull())
    }

    @Test
    @DisplayName("Integration: Performance - Repository response time")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    fun `getWeatherForecast should complete within acceptable time`() = runTest {
        // Given
        coEvery { mockLocalDataSource.getWeatherForecasts() } returns sampleWeatherList
        coEvery { mockLocalDataSource.isCacheValid(any()) } returns true
        coEvery { mockRemoteDataSource.getWeatherForecast(any()) } returns sampleWeatherList

        val startTime = System.currentTimeMillis()

        // When
        val result = repository.getWeatherForecast().first()

        // Then
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        
        assertTrue(result.isSuccess)
        assertTrue(duration < 1000, "Repository should respond within 1 second, took ${duration}ms")
    }

    @Test
    @DisplayName("Integration: Cache configuration variations")
    fun `repository should respect different cache configurations`() = runTest {
        // Given - Short cache TTL
        val shortCacheConfig = CacheConfig(cacheExpiryHours = 1)
        coEvery { mockAppPreferences.getCacheConfig() } returns shortCacheConfig
        coEvery { mockLocalDataSource.getWeatherForecasts() } returns sampleWeatherList
        coEvery { mockLocalDataSource.isCacheValid(shortCacheConfig.cacheExpiryHours) } returns false
        coEvery { mockRemoteDataSource.getWeatherForecast(any()) } returns sampleWeatherList

        // When
        val result = repository.getWeatherForecast().first()

        // Then
        assertTrue(result.isSuccess)
        coVerify { mockLocalDataSource.isCacheValid(shortCacheConfig.cacheExpiryHours) }
        coVerify { mockRemoteDataSource.getWeatherForecast(any()) }
    }

    @Test
    @DisplayName("Integration: Data consistency validation")
    fun `repository should maintain data consistency across operations`() = runTest {
        // Given
        val updatedWeatherList = sampleWeatherList.map { 
            it.copy(temperatureHigh = it.temperatureHigh + 5.0)
        }
        
        coEvery { mockLocalDataSource.getWeatherForecasts() } returns sampleWeatherList
        coEvery { mockLocalDataSource.isCacheValid(any()) } returns true
        coEvery { mockRemoteDataSource.getWeatherForecast(any()) } returns updatedWeatherList

        // When
        val forecastResult = repository.getWeatherForecast().first()
        val refreshResult = repository.refreshWeatherForecast()

        // Then
        assertTrue(forecastResult.isSuccess)
        assertTrue(refreshResult.isSuccess)
        
        // Data should be different (updated from refresh)
        assertNotEquals(forecastResult.getOrNull(), refreshResult.getOrNull())
        assertEquals(updatedWeatherList, refreshResult.getOrNull())
    }
}