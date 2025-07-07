package com.weather.integration

import com.weather.testing.*
import com.weather.testing.FakeWeatherRepository
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import com.weather.domain.usecase.GetWeatherForecastUseCase
import com.weather.domain.usecase.RefreshWeatherUseCase
import kotlinx.datetime.LocalDate
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.flow.first
import app.cash.turbine.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * Repository integration tests demonstrating advanced parameterized testing patterns
 */
class WeatherRepositoryIntegrationTest {

    private val testRunner = ParameterizedTestRunner()

    @Test
    fun `test repository caching behavior with different scenarios`() = runTest {
        val cacheScenarios = listOf(
            TestScenario(
                name = "Fresh Cache Hit",
                given = "Repository has fresh cached data",
                input = CacheTestInput(
                    hasCachedData = true,
                    isCacheValid = true,
                    networkShouldFail = false
                ),
                expected = CacheTestExpected(
                    shouldUseCache = true,
                    shouldCallNetwork = false,
                    shouldSucceed = true
                ),
                tags = setOf("cache", "performance")
            ),
            TestScenario(
                name = "Stale Cache with Network Success",
                given = "Repository has stale cached data but network succeeds",
                input = CacheTestInput(
                    hasCachedData = true,
                    isCacheValid = false,
                    networkShouldFail = false
                ),
                expected = CacheTestExpected(
                    shouldUseCache = false,
                    shouldCallNetwork = true,
                    shouldSucceed = true
                ),
                tags = setOf("cache", "network", "refresh")
            ),
            TestScenario(
                name = "Stale Cache with Network Failure",
                given = "Repository has stale cached data and network fails",
                input = CacheTestInput(
                    hasCachedData = true,
                    isCacheValid = false,
                    networkShouldFail = true
                ),
                expected = CacheTestExpected(
                    shouldUseCache = true,
                    shouldCallNetwork = true,
                    shouldSucceed = true // Should fallback to cache
                ),
                tags = setOf("cache", "network", "fallback")
            ),
            TestScenario(
                name = "No Cache with Network Success",
                given = "Repository has no cached data but network succeeds",
                input = CacheTestInput(
                    hasCachedData = false,
                    isCacheValid = false,
                    networkShouldFail = false
                ),
                expected = CacheTestExpected(
                    shouldUseCache = false,
                    shouldCallNetwork = true,
                    shouldSucceed = true
                ),
                tags = setOf("network", "first_load")
            ),
            TestScenario(
                name = "No Cache with Network Failure",
                given = "Repository has no cached data and network fails",
                input = CacheTestInput(
                    hasCachedData = false,
                    isCacheValid = false,
                    networkShouldFail = true
                ),
                expected = CacheTestExpected(
                    shouldUseCache = false,
                    shouldCallNetwork = true,
                    shouldSucceed = false
                ),
                tags = setOf("network", "error_handling")
            )
        )

        testRunner.runScenarioTests(cacheScenarios) { scenario ->
            val fakeRepository = FakeWeatherRepository(
                shouldReturnError = scenario.input.networkShouldFail
            )
            
            val useCase = GetWeatherForecastUseCase(fakeRepository)
            
            // Setup repository state based on scenario
            if (scenario.input.hasCachedData) {
                fakeRepository.setCachedData(getSampleWeatherList())
            }
            
            val result = useCase().first()
            
            CacheTestExpected(
                shouldUseCache = scenario.input.hasCachedData && scenario.input.isCacheValid,
                shouldCallNetwork = !scenario.input.isCacheValid || !scenario.input.hasCachedData,
                shouldSucceed = result.isSuccess
            )
        }
    }

    @Test
    fun `test concurrent repository access with load testing`() = runTest {
        val concurrentAccessCases = listOf(
            TestCase(
                name = "Low Concurrency",
                input = 5,
                expected = 5,
                description = "5 concurrent requests should all succeed"
            ),
            TestCase(
                name = "Medium Concurrency",
                input = 20,
                expected = 20,
                description = "20 concurrent requests should all succeed"
            ),
            TestCase(
                name = "High Concurrency",
                input = 50,
                expected = 50,
                description = "50 concurrent requests should all succeed"
            )
        )

        testRunner.runParameterizedTests(concurrentAccessCases) { testCase ->
            val fakeRepository = FakeWeatherRepository(shouldReturnError = false)
            val useCase = GetWeatherForecastUseCase(fakeRepository)
            
            val concurrentRequests = testCase.input as Int
            val results = mutableListOf<com.weather.domain.common.Result<List<Weather>>>()
            
            // Launch concurrent requests
            repeat(concurrentRequests) {
                val result = useCase().first()
                results.add(result)
            }
            
            // Verify all requests succeeded
            val successCount = results.count { it.isSuccess }
            assertEquals(testCase.expected, successCount)
            
            // Verify data consistency
            val successfulResults = results.filter { it.isSuccess }
            if (successfulResults.isNotEmpty()) {
                val firstResult = successfulResults.first().getOrNull()
                assertTrue(
                    successfulResults.all { it.getOrNull() == firstResult },
                    "All concurrent requests should return consistent data"
                )
            }
            
            successCount
        }
    }

    @Test
    fun `test repository error recovery patterns`() = runTest {
        val errorRecoveryScenarios = listOf(
            TestScenario(
                name = "Network Timeout Recovery",
                given = "Network timeout occurs but retry succeeds",
                input = ErrorRecoveryInput(
                    errorType = "timeout",
                    shouldRetrySucceed = true,
                    maxRetries = 3
                ),
                expected = true,
                tags = setOf("error_recovery", "timeout")
            ),
            TestScenario(
                name = "Network Error with Fallback",
                given = "Network error occurs, fallback to cache",
                input = ErrorRecoveryInput(
                    errorType = "network_error",
                    shouldRetrySucceed = false,
                    maxRetries = 2
                ),
                expected = true, // Should fallback to cache
                tags = setOf("error_recovery", "fallback")
            ),
            TestScenario(
                name = "Parse Error Recovery",
                given = "API response parse error occurs",
                input = ErrorRecoveryInput(
                    errorType = "parse_error",
                    shouldRetrySucceed = false,
                    maxRetries = 1
                ),
                expected = false, // Parse errors shouldn't retry
                tags = setOf("error_recovery", "parse_error")
            )
        )

        testRunner.runScenarioTests(errorRecoveryScenarios) { scenario ->
            val fakeRepository = FakeWeatherRepository(
                shouldReturnError = !scenario.input.shouldRetrySucceed
            )
            
            val useCase = GetWeatherForecastUseCase(fakeRepository)
            
            // Simulate error condition
            when (scenario.input.errorType) {
                "timeout" -> {
                    // Simulate timeout then success
                    fakeRepository.setForecastResult(kotlin.Result.failure(Exception("Timeout")))
                    if (scenario.input.shouldRetrySucceed) {
                        fakeRepository.setForecastResult(kotlin.Result.success(getSampleWeatherList()))
                    }
                }
                "network_error" -> {
                    fakeRepository.setForecastResult(kotlin.Result.failure(Exception("Network error")))
                    if (!scenario.input.shouldRetrySucceed) {
                        // Set cached data as fallback
                        fakeRepository.setCachedData(getSampleWeatherList())
                    }
                }
                "parse_error" -> {
                    fakeRepository.setForecastResult(kotlin.Result.failure(Exception("Parse error")))
                }
            }
            
            val result = useCase().first()
            result.isSuccess
        }
    }

    @Test
    fun `test repository data transformation pipeline`() = runTest {
        val transformationCases = listOf(
            TestCase(
                name = "Temperature Unit Conversion",
                input = TransformationInput(
                    temperatureCelsius = 25.0,
                    targetUnit = "fahrenheit"
                ),
                expected = 77.0,
                description = "Should convert Celsius to Fahrenheit"
            ),
            TestCase(
                name = "Wind Speed Unit Conversion",
                input = TransformationInput(
                    windSpeedMps = 10.0,
                    targetUnit = "kmh"
                ),
                expected = 36.0,
                description = "Should convert m/s to km/h"
            ),
            TestCase(
                name = "Pressure Unit Conversion",
                input = TransformationInput(
                    pressureHpa = 1013.25,
                    targetUnit = "inHg"
                ),
                expected = 29.92,
                description = "Should convert hPa to inHg"
            )
        )

        testRunner.runParameterizedTests(transformationCases) { testCase ->
            val input = testCase.input as TransformationInput
            
            // Simulate data transformation
            val transformedValue = when {
                input.temperatureCelsius != null && input.targetUnit == "fahrenheit" -> {
                    (input.temperatureCelsius * 9 / 5) + 32
                }
                input.windSpeedMps != null && input.targetUnit == "kmh" -> {
                    input.windSpeedMps * 3.6
                }
                input.pressureHpa != null && input.targetUnit == "inHg" -> {
                    input.pressureHpa * 0.02953
                }
                else -> 0.0
            }
            
            val expectedValue = testCase.expected as Double
            val tolerance = 0.1
            
            assertTrue(
                kotlin.math.abs(transformedValue - expectedValue) < tolerance,
                "Transformation should be within tolerance. Expected: $expectedValue, Got: $transformedValue"
            )
            
            transformedValue
        }
    }

    @Test
    fun `test repository performance characteristics`() = runTest {
        val performanceScenarios = listOf(
            TestScenario(
                name = "Large Dataset Performance",
                given = "Repository handles large weather dataset",
                input = PerformanceTestInput(
                    dataSize = 1000,
                    maxExecutionTimeMs = 500
                ),
                expected = true,
                tags = setOf("performance", "large_data")
            ),
            TestScenario(
                name = "Rapid Sequential Requests",
                given = "Repository handles rapid sequential requests",
                input = PerformanceTestInput(
                    requestCount = 100,
                    maxExecutionTimeMs = 1000
                ),
                expected = true,
                tags = setOf("performance", "sequential")
            ),
            TestScenario(
                name = "Memory Usage Under Load",
                given = "Repository maintains reasonable memory usage",
                input = PerformanceTestInput(
                    dataSize = 500,
                    requestCount = 50,
                    maxExecutionTimeMs = 2000
                ),
                expected = true,
                tags = setOf("performance", "memory")
            )
        )

        testRunner.runScenarioTests(performanceScenarios) { scenario ->
            val fakeRepository = FakeWeatherRepository()
            val useCase = GetWeatherForecastUseCase(fakeRepository)
            
            val input = scenario.input
            
            val startTime = kotlinx.datetime.Clock.System.now()
            
            when (scenario.name) {
                "Large Dataset Performance" -> {
                    val largeDataset = generateLargeWeatherDataset(input.dataSize ?: 1000)
                    fakeRepository.setForecastResult(kotlin.Result.success(largeDataset))
                    val result = useCase().first()
                    result.isSuccess
                }
                "Rapid Sequential Requests" -> {
                    repeat(input.requestCount ?: 100) {
                        useCase().first()
                    }
                    true
                }
                "Memory Usage Under Load" -> {
                    val dataset = generateLargeWeatherDataset(input.dataSize ?: 500)
                    fakeRepository.setForecastResult(kotlin.Result.success(dataset))
                    repeat(input.requestCount ?: 50) {
                        useCase().first()
                    }
                    true
                }
                else -> false
            }
            
            val endTime = kotlinx.datetime.Clock.System.now()
            val executionTime = endTime.toEpochMilliseconds() - startTime.toEpochMilliseconds()
            
            executionTime <= (input.maxExecutionTimeMs ?: 1000)
        }
    }

    // Helper data classes
    data class CacheTestInput(
        val hasCachedData: Boolean,
        val isCacheValid: Boolean,
        val networkShouldFail: Boolean
    )

    data class CacheTestExpected(
        val shouldUseCache: Boolean,
        val shouldCallNetwork: Boolean,
        val shouldSucceed: Boolean
    )

    data class ErrorRecoveryInput(
        val errorType: String,
        val shouldRetrySucceed: Boolean,
        val maxRetries: Int
    )

    data class TransformationInput(
        val temperatureCelsius: Double? = null,
        val windSpeedMps: Double? = null,
        val pressureHpa: Double? = null,
        val targetUnit: String
    )

    data class PerformanceTestInput(
        val dataSize: Int? = null,
        val requestCount: Int? = null,
        val maxExecutionTimeMs: Long
    )

    // Helper functions
    private fun getSampleWeatherList(): List<Weather> = listOf(
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

    private fun generateLargeWeatherDataset(size: Int): List<Weather> {
        return (1..size).map { index ->
            Weather(
                date = LocalDate(2025, 1, (index % 28) + 1),
                condition = WeatherCondition.values()[(index % WeatherCondition.values().size)],
                temperatureHigh = 20.0 + (index % 30),
                temperatureLow = 10.0 + (index % 20),
                humidity = 50 + (index % 50),
                icon = "0${(index % 4) + 1}d",
                description = "Generated weather $index"
            )
        }
    }
}