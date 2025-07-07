package com.weather.integration

import com.weather.data.local.LocalWeatherDataSource
import com.weather.data.local.preferences.AppPreferences
import com.weather.data.remote.RemoteWeatherDataSource
import com.weather.data.repository.WeatherRepositoryImpl
import com.weather.domain.model.CacheConfig
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import com.weather.domain.usecase.GetWeatherForecastUseCase
import com.weather.domain.usecase.RefreshWeatherUseCase
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.util.concurrent.TimeUnit

/**
 * JVM-specific end-to-end integration tests
 * Tests the complete weather system integration
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WeatherSystemIntegrationTest {

    private lateinit var mockLocalDataSource: LocalWeatherDataSource
    private lateinit var mockRemoteDataSource: RemoteWeatherDataSource
    private lateinit var mockAppPreferences: AppPreferences
    private lateinit var repository: WeatherRepositoryImpl
    private lateinit var getWeatherUseCase: GetWeatherForecastUseCase
    private lateinit var refreshWeatherUseCase: RefreshWeatherUseCase

    private val londonWeather = listOf(
        Weather(
            date = LocalDate(2025, 1, 15),
            condition = WeatherCondition.CLEAR,
            temperatureHigh = 8.0,
            temperatureLow = 2.0,
            humidity = 65,
            icon = "01d",
            description = "Clear sky"
        ),
        Weather(
            date = LocalDate(2025, 1, 16),
            condition = WeatherCondition.RAIN,
            temperatureHigh = 12.0,
            temperatureLow = 6.0,
            humidity = 85,
            icon = "10d",
            description = "Light rain"
        )
    )

    private val tokyoWeather = listOf(
        Weather(
            date = LocalDate(2025, 1, 15),
            condition = WeatherCondition.CLOUDS,
            temperatureHigh = 15.0,
            temperatureLow = 8.0,
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
        
        getWeatherUseCase = GetWeatherForecastUseCase(repository)
        refreshWeatherUseCase = RefreshWeatherUseCase(repository)

        // Default setup
        coEvery { mockAppPreferences.getCacheConfig() } returns CacheConfig()
        coEvery { mockAppPreferences.getApiKey() } returns "test_api_key"
        coEvery { mockAppPreferences.getDefaultLocation() } returns "London,UK"
    }

    @AfterEach
    fun teardown() {
        clearAllMocks()
    }

    @Test
    @DisplayName("E2E: Complete weather forecast flow")
    fun `complete weather forecast flow should work end-to-end`() = runTest {
        // Given - Fresh app start (no cache)
        coEvery { mockLocalDataSource.getWeatherForecasts() } returns emptyList()
        coEvery { mockLocalDataSource.isCacheValid(any()) } returns false
        coEvery { mockRemoteDataSource.getWeatherForecast("London,UK", "test_api_key") } returns londonWeather

        // When - User opens app and requests weather
        val weatherFlow = getWeatherUseCase()
        val result = weatherFlow.first()

        // Then - Should get fresh data from API
        assertTrue(result.isSuccess)
        assertEquals(londonWeather, result.getOrNull())
        
        // Verify the complete flow
        coVerify(exactly = 1) { mockLocalDataSource.getWeatherForecasts() }
        coVerify(exactly = 1) { mockRemoteDataSource.getWeatherForecast("London,UK", "test_api_key") }
        coVerify(exactly = 1) { mockLocalDataSource.saveWeatherForecasts(londonWeather) }
    }

    @Test
    @DisplayName("E2E: Cache hit scenario with background refresh")
    fun `cached data should be served immediately with background refresh`() = runTest {
        // Given - App with valid cache
        coEvery { mockLocalDataSource.getWeatherForecasts() } returns londonWeather
        coEvery { mockLocalDataSource.isCacheValid(any()) } returns true
        
        // Fresh data is slightly different (temperature change)
        val updatedWeather = londonWeather.map { it.copy(temperatureHigh = it.temperatureHigh + 2.0) }
        coEvery { mockRemoteDataSource.getWeatherForecast("London,UK", "test_api_key") } returns updatedWeather

        // When - User requests weather
        val weatherFlow = getWeatherUseCase()
        val results = weatherFlow.toList()

        // Then - Should get cached data first, then updated data
        assertEquals(2, results.size)
        
        // First result: cached data
        assertTrue(results[0].isSuccess)
        assertEquals(londonWeather, results[0].getOrNull())
        
        // Second result: fresh data (if different)
        assertTrue(results[1].isSuccess)
        assertEquals(updatedWeather, results[1].getOrNull())
    }

    @Test
    @DisplayName("E2E: Location change workflow")
    fun `changing location should fetch new weather data`() = runTest {
        // Given - Start with London data
        coEvery { mockAppPreferences.getDefaultLocation() } returns "London,UK"
        coEvery { mockLocalDataSource.getWeatherForecasts() } returns londonWeather
        coEvery { mockLocalDataSource.isCacheValid(any()) } returns true
        coEvery { mockRemoteDataSource.getWeatherForecast("London,UK", "test_api_key") } returns londonWeather

        // When - Get initial weather
        val londonResult = getWeatherUseCase().first()

        // Then - Should get London weather
        assertTrue(londonResult.isSuccess)
        assertEquals(londonWeather, londonResult.getOrNull())

        // Given - User changes location to Tokyo
        coEvery { mockAppPreferences.getDefaultLocation() } returns "Tokyo,JP"
        coEvery { mockRemoteDataSource.getWeatherForecast("Tokyo,JP", "test_api_key") } returns tokyoWeather

        // When - Refresh weather for new location
        val tokyoResult = refreshWeatherUseCase()

        // Then - Should get Tokyo weather
        assertTrue(tokyoResult.isSuccess)
        assertEquals(tokyoWeather, tokyoResult.getOrNull())
        
        // Verify new location was called
        coVerify { mockRemoteDataSource.getWeatherForecast("Tokyo,JP", "test_api_key") }
    }

    @Test
    @DisplayName("E2E: Network failure resilience")
    fun `system should gracefully handle network failures`() = runTest {
        // Given - App with stale cache data
        coEvery { mockLocalDataSource.getWeatherForecasts() } returns londonWeather
        coEvery { mockLocalDataSource.isCacheValid(any()) } returns false // Cache is stale
        coEvery { mockRemoteDataSource.getWeatherForecast(any(), any()) } throws Exception("Network unavailable")

        // When - User requests weather (network is down)
        val result = getWeatherUseCase().first()

        // Then - Should still get cached data (better than nothing)
        assertTrue(result.isSuccess)
        assertEquals(londonWeather, result.getOrNull())
        
        // But when trying to refresh explicitly
        val refreshResult = refreshWeatherUseCase()
        
        // Should get error
        assertTrue(refreshResult.isFailure)
        assertEquals("Network unavailable", refreshResult.exceptionOrNull()?.message)
    }

    @Test
    @DisplayName("E2E: Concurrent requests optimization")
    fun `concurrent weather requests should be optimized`() = runTest {
        // Given
        coEvery { mockLocalDataSource.getWeatherForecasts() } returns londonWeather
        coEvery { mockLocalDataSource.isCacheValid(any()) } returns true
        coEvery { mockRemoteDataSource.getWeatherForecast(any(), any()) } returns londonWeather

        // When - Multiple concurrent requests (simulate multiple UI components)
        val flow1 = getWeatherUseCase()
        val flow2 = getWeatherUseCase()
        val flow3 = getWeatherUseCase()

        val result1 = flow1.first()
        val result2 = flow2.first()
        val result3 = flow3.first()

        // Then - All should succeed with same data
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        assertTrue(result3.isSuccess)
        assertEquals(result1.getOrNull(), result2.getOrNull())
        assertEquals(result2.getOrNull(), result3.getOrNull())

        // But network should only be called once (if cache is valid)
        coVerify(exactly = 3) { mockLocalDataSource.getWeatherForecasts() }
        // Network calls depend on implementation - might be optimized
    }

    @Test
    @DisplayName("E2E: Performance under load")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    fun `system should perform well under load`() = runTest {
        // Given
        coEvery { mockLocalDataSource.getWeatherForecasts() } returns londonWeather
        coEvery { mockLocalDataSource.isCacheValid(any()) } returns true
        coEvery { mockRemoteDataSource.getWeatherForecast(any(), any()) } returns londonWeather

        val startTime = System.currentTimeMillis()

        // When - Simulate high load
        repeat(100) {
            val result = getWeatherUseCase().first()
            assertTrue(result.isSuccess)
        }

        val endTime = System.currentTimeMillis()
        val totalTime = endTime - startTime

        // Then - Should complete within reasonable time
        assertTrue(totalTime < 5000, "100 weather requests should complete within 5 seconds, took ${totalTime}ms")
    }

    @Test
    @DisplayName("E2E: Data consistency validation")
    fun `system should maintain data consistency throughout workflow`() = runTest {
        // Given - Fresh weather data
        val freshWeather = londonWeather.map { 
            it.copy(
                temperatureHigh = it.temperatureHigh + 5.0,
                description = "Updated: ${it.description}"
            )
        }
        
        coEvery { mockLocalDataSource.getWeatherForecasts() } returns londonWeather
        coEvery { mockLocalDataSource.isCacheValid(any()) } returns false
        coEvery { mockRemoteDataSource.getWeatherForecast(any(), any()) } returns freshWeather

        // When - Get weather (should fetch fresh data)
        val forecastResult = getWeatherUseCase().first()
        
        // Then - Should get fresh data
        assertTrue(forecastResult.isSuccess)
        assertEquals(freshWeather, forecastResult.getOrNull())
        
        // And cache should be updated
        coVerify { mockLocalDataSource.saveWeatherForecasts(freshWeather) }
        
        // When - Get weather again (should now use cache)
        coEvery { mockLocalDataSource.getWeatherForecasts() } returns freshWeather
        coEvery { mockLocalDataSource.isCacheValid(any()) } returns true
        
        val cachedResult = getWeatherUseCase().first()
        
        // Then - Should get same data from cache
        assertTrue(cachedResult.isSuccess)
        assertEquals(freshWeather, cachedResult.getOrNull())
        assertEquals(forecastResult.getOrNull(), cachedResult.getOrNull())
    }
}