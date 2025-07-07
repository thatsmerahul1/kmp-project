package com.weather.testing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlinx.coroutines.test.runTest

/**
 * Parameterized Testing Support for KMP 2025
 * 
 * Features:
 * - Property-based testing patterns
 * - Data-driven test scenarios
 * - Cross-platform test execution
 * - Edge case generation
 * - Test case combinations
 */

/**
 * Test case data class for parameterized tests
 */
data class TestCase<T>(
    val name: String,
    val input: T,
    val expected: Any? = null,
    val description: String = ""
)

/**
 * Test scenario for complex testing
 */
data class TestScenario<TInput, TExpected>(
    val name: String,
    val given: String,
    val input: TInput,
    val expected: TExpected,
    val tags: Set<String> = emptySet()
)

/**
 * Property-based test configuration
 */
data class PropertyTestConfig(
    val iterations: Int = 100,
    val seed: Long? = null,
    val shrinkingEnabled: Boolean = true,
    val maxShrinkAttempts: Int = 50
)

/**
 * Parameterized test runner
 */
class ParameterizedTestRunner {
    
    /**
     * Run tests for multiple test cases
     */
    inline fun <T> runParameterizedTests(
        testCases: List<TestCase<T>>,
        crossinline testAction: suspend (TestCase<T>) -> Unit
    ) = runTest {
        testCases.forEach { testCase ->
            try {
                println("Running test case: ${testCase.name}")
                testAction(testCase)
                println("✅ Test case passed: ${testCase.name}")
            } catch (e: Exception) {
                println("❌ Test case failed: ${testCase.name} - ${e.message}")
                throw AssertionError("Test case '${testCase.name}' failed: ${e.message}", e)
            }
        }
    }
    
    /**
     * Run property-based tests
     */
    inline fun <T> runPropertyTests(
        generator: () -> T,
        config: PropertyTestConfig = PropertyTestConfig(),
        crossinline predicate: suspend (T) -> Boolean
    ) = runTest {
        repeat(config.iterations) { iteration ->
            val input = generator()
            try {
                val result = predicate(input)
                assertTrue(result, "Property test failed at iteration $iteration with input: $input")
            } catch (e: Exception) {
                println("❌ Property test failed at iteration $iteration with input: $input")
                throw e
            }
        }
        println("✅ Property test passed for ${config.iterations} iterations")
    }
    
    /**
     * Run scenario-based tests
     */
    inline fun <TInput, TExpected> runScenarioTests(
        scenarios: List<TestScenario<TInput, TExpected>>,
        crossinline testAction: suspend (TestScenario<TInput, TExpected>) -> TExpected
    ) = runTest {
        scenarios.forEach { scenario ->
            try {
                println("Running scenario: ${scenario.name}")
                println("Given: ${scenario.given}")
                
                val actual = testAction(scenario)
                assertEquals(scenario.expected, actual, "Scenario '${scenario.name}' failed")
                
                println("✅ Scenario passed: ${scenario.name}")
            } catch (e: Exception) {
                println("❌ Scenario failed: ${scenario.name} - ${e.message}")
                throw AssertionError("Scenario '${scenario.name}' failed: ${e.message}", e)
            }
        }
    }
}

/**
 * Common test data sets for weather domain
 */
object WeatherTestData {
    
    val temperatureTestCases = listOf(
        TestCase("Freezing", -10.0, "Below freezing"),
        TestCase("Cold", 5.0, "Cold weather"),
        TestCase("Mild", 20.0, "Comfortable temperature"),
        TestCase("Hot", 35.0, "Hot weather"),
        TestCase("Extreme Heat", 50.0, "Extreme temperature"),
        TestCase("Extreme Cold", -50.0, "Extreme cold")
    )
    
    val coordinateTestCases = listOf(
        TestCase("New York", Pair(40.7128, -74.0060)),
        TestCase("London", Pair(51.5074, -0.1278)),
        TestCase("Tokyo", Pair(35.6762, 139.6503)),
        TestCase("Sydney", Pair(-33.8688, 151.2093)),
        TestCase("North Pole", Pair(90.0, 0.0)),
        TestCase("South Pole", Pair(-90.0, 0.0)),
        TestCase("Greenwich", Pair(51.4769, 0.0))
    )
    
    val invalidCoordinateTestCases = listOf(
        TestCase("Invalid Latitude High", Pair(91.0, 0.0)),
        TestCase("Invalid Latitude Low", Pair(-91.0, 0.0)),
        TestCase("Invalid Longitude High", Pair(0.0, 181.0)),
        TestCase("Invalid Longitude Low", Pair(0.0, -181.0))
    )
    
    val weatherConditionTestCases = listOf(
        TestCase("Sunny", "Clear skies"),
        TestCase("Cloudy", "Overcast conditions"),
        TestCase("Rainy", "Precipitation expected"),
        TestCase("Snowy", "Snow conditions"),
        TestCase("Stormy", "Severe weather"),
        TestCase("Foggy", "Reduced visibility")
    )
}

/**
 * Security testing data sets
 */
object SecurityTestData {
    
    val encryptionTestCases = listOf(
        TestCase("Empty String", ""),
        TestCase("Short Text", "Hello"),
        TestCase("Long Text", "A".repeat(1000)),
        TestCase("Unicode Text", "Hello 世界 🌍"),
        TestCase("Special Characters", "!@#$%^&*()[]{}"),
        TestCase("JSON Data", """{"key": "value", "number": 123}"""),
        TestCase("XML Data", "<root><item>value</item></root>")
    )
    
    val invalidApiKeyTestCases = listOf(
        TestCase("Empty Key", ""),
        TestCase("Too Short", "abc"),
        TestCase("Invalid Characters", "key with spaces"),
        TestCase("SQL Injection", "'; DROP TABLE users; --"),
        TestCase("XSS Attempt", "<script>alert('xss')</script>")
    )
    
    val certificatePinningTestCases = listOf(
        TestCase("Valid Pin", "YLh1dUR9y6Kja30RrAn7JKnbQG/uEtLMkBgFF2Fuihg="),
        TestCase("Invalid Pin Length", "invalid"),
        TestCase("Non-Base64", "not-base64-data!"),
        TestCase("Empty Pin", "")
    )
}

/**
 * Monitoring testing data sets
 */
object MonitoringTestData {
    
    val metricNameTestCases = listOf(
        TestCase("Counter Metric", "api_requests_total"),
        TestCase("Gauge Metric", "memory_usage_bytes"),
        TestCase("Histogram Metric", "request_duration_seconds"),
        TestCase("Summary Metric", "response_size_bytes")
    )
    
    val metricValueTestCases = listOf(
        TestCase("Zero", 0.0),
        TestCase("Positive Small", 0.001),
        TestCase("Positive Large", 1000000.0),
        TestCase("Negative", -1.0),
        TestCase("Infinity", Double.POSITIVE_INFINITY),
        TestCase("NaN", Double.NaN)
    )
    
    val telemetryAttributeTestCases = listOf(
        TestCase("Empty Attributes", emptyMap<String, String>()),
        TestCase("Single Attribute", mapOf("key" to "value")),
        TestCase("Multiple Attributes", mapOf("service" to "weather", "version" to "1.0", "env" to "test")),
        TestCase("Special Characters", mapOf("key-with-dash" to "value_with_underscore")),
        TestCase("Unicode Values", mapOf("location" to "北京", "weather" to "晴れ"))
    )
}

/**
 * Feature toggle testing data sets
 */
object FeatureToggleTestData {
    
    val rolloutPercentageTestCases = listOf(
        TestCase("Disabled", 0),
        TestCase("Partial 25%", 25),
        TestCase("Half", 50),
        TestCase("Partial 75%", 75),
        TestCase("Fully Enabled", 100)
    )
    
    val invalidRolloutTestCases = listOf(
        TestCase("Negative", -1),
        TestCase("Over 100", 101),
        TestCase("Way Over", 1000)
    )
    
    val environmentTestCases = listOf(
        TestCase("Development", "dev"),
        TestCase("Staging", "staging"),
        TestCase("Production", "prod"),
        TestCase("All Environments", "all")
    )
}

/**
 * Edge case generators
 */
object EdgeCaseGenerators {
    
    fun generateBoundaryValues(min: Double, max: Double): List<Double> = listOf(
        min - 1.0,     // Just below minimum
        min,           // Minimum
        min + 0.001,   // Just above minimum
        (min + max) / 2, // Middle value
        max - 0.001,   // Just below maximum
        max,           // Maximum
        max + 1.0      // Just above maximum
    )
    
    fun generateStringEdgeCases(): List<String> = listOf(
        "",                    // Empty
        " ",                   // Single space
        "a",                   // Single character
        "A".repeat(1000),      // Very long
        "Hello\nWorld",        // With newline
        "Hello\tWorld",        // With tab
        "Hello\"World",        // With quotes
        "Hello'World",         // With apostrophe
        "Hello\\World",        // With backslash
        "null",                // String null
        "undefined",           // String undefined
        "0",                   // String zero
        "false",               // String false
        "🌍🌎🌏",              // Emojis
        "北京天气预报"           // Non-Latin characters
    )
    
    fun generateIntegerEdgeCases(): List<Int> = listOf(
        Int.MIN_VALUE,
        Int.MIN_VALUE + 1,
        -1000000,
        -1,
        0,
        1,
        1000000,
        Int.MAX_VALUE - 1,
        Int.MAX_VALUE
    )
    
    fun generateDoubleEdgeCases(): List<Double> = listOf(
        Double.NEGATIVE_INFINITY,
        -Double.MAX_VALUE,
        -1000000.0,
        -1.0,
        -Double.MIN_VALUE,
        0.0,
        Double.MIN_VALUE,
        1.0,
        1000000.0,
        Double.MAX_VALUE,
        Double.POSITIVE_INFINITY,
        Double.NaN
    )
}

/**
 * Property generators for property-based testing
 */
object PropertyGenerators {
    
    fun generateValidCoordinates(): Pair<Double, Double> = Pair(
        kotlin.random.Random.nextDouble(-90.0, 90.0),
        kotlin.random.Random.nextDouble(-180.0, 180.0)
    )
    
    fun generateValidTemperature(): Double = 
        kotlin.random.Random.nextDouble(-50.0, 50.0)
    
    fun generateValidPercentage(): Int = 
        kotlin.random.Random.nextInt(0, 101)
    
    fun generateValidApiKey(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..32).map { chars.random() }.joinToString("")
    }
    
    fun generateValidMetricName(): String {
        val prefixes = listOf("api", "db", "cache", "memory", "cpu", "network")
        val suffixes = listOf("count", "duration", "size", "rate", "total", "percentage")
        return "${prefixes.random()}_${suffixes.random()}"
    }
    
    fun generateRandomAttributes(): Map<String, String> {
        val keys = listOf("service", "version", "environment", "region", "instance")
        val values = listOf("weather", "auth", "api", "1.0", "2.0", "prod", "dev", "us-east", "eu-west")
        
        return keys.take(kotlin.random.Random.nextInt(1, 4)).associateWith { values.random() }
    }
}

/**
 * Test execution helpers
 */
object TestExecutionHelpers {
    
    /**
     * Measure execution time of a suspend function
     */
    suspend fun <T> measureTime(action: suspend () -> T): Pair<T, Long> {
        val startTime = kotlinx.datetime.Clock.System.now()
        val result = action()
        val endTime = kotlinx.datetime.Clock.System.now()
        val duration = endTime.toEpochMilliseconds() - startTime.toEpochMilliseconds()
        return Pair(result, duration)
    }
    
    /**
     * Retry a test action with exponential backoff
     */
    suspend fun <T> retryWithBackoff(
        maxAttempts: Int = 3,
        initialDelayMs: Long = 100,
        maxDelayMs: Long = 1000,
        action: suspend () -> T
    ): T {
        var lastException: Exception? = null
        var delayMs = initialDelayMs
        
        repeat(maxAttempts) { attempt ->
            try {
                return action()
            } catch (e: Exception) {
                lastException = e
                if (attempt < maxAttempts - 1) {
                    kotlinx.coroutines.delay(delayMs)
                    delayMs = minOf(delayMs * 2, maxDelayMs)
                }
            }
        }
        
        throw lastException ?: Exception("Retry failed after $maxAttempts attempts")
    }
    
    /**
     * Assert that an action completes within a time limit
     */
    suspend fun <T> assertCompletesWithin(
        timeoutMs: Long,
        action: suspend () -> T
    ): T = kotlinx.coroutines.withTimeout(timeoutMs) {
        action()
    }
}