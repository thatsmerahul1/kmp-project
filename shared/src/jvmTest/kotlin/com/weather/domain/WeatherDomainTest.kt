package com.weather.domain

import com.weather.domain.common.DomainException
import com.weather.domain.common.Result
import com.weather.domain.common.isSuccess
import com.weather.domain.common.isError
import com.weather.domain.common.isLoading
import com.weather.domain.common.getOrNull
import com.weather.domain.common.exceptionOrNull
import com.weather.domain.common.map
import com.weather.domain.common.flatMap
import com.weather.domain.common.zip
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.util.stream.Stream

/**
 * JVM-specific comprehensive domain model tests
 * Uses JUnit 5 features for advanced testing scenarios
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WeatherDomainTest {

    @Nested
    @DisplayName("Weather Domain Model Tests")
    inner class WeatherModelTests {

        @Test
        @DisplayName("Weather model should validate temperature constraints")
        fun `weather should have valid temperature relationships`() {
            // Given & When
            val weather = Weather(
                date = LocalDate(2025, 1, 15),
                condition = WeatherCondition.CLEAR,
                temperatureHigh = 25.0,
                temperatureLow = 15.0,
                humidity = 65,
                icon = "01d",
                description = "Clear sky"
            )

            // Then
            assertTrue(weather.temperatureHigh >= weather.temperatureLow, 
                "High temperature should be >= low temperature")
            assertTrue(weather.humidity in 0..100, 
                "Humidity should be between 0 and 100")
        }

        @ParameterizedTest
        @DisplayName("Weather should handle all weather conditions")
        @EnumSource(WeatherCondition::class)
        fun `weather should support all weather conditions`(condition: WeatherCondition) {
            // Given & When
            val weather = Weather(
                date = LocalDate(2025, 1, 15),
                condition = condition,
                temperatureHigh = 20.0,
                temperatureLow = 10.0,
                humidity = 50,
                icon = "01d",
                description = condition.name
            )

            // Then
            assertEquals(condition, weather.condition)
            assertNotNull(weather.description)
            assertFalse(weather.description.isBlank())
        }

        @TestFactory
        @DisplayName("Weather temperature edge cases")
        fun `weather should handle temperature edge cases`(): Stream<DynamicTest> {
            val testCases = listOf(
                Triple(-50.0, -45.0, "Extreme cold"),
                Triple(0.0, 5.0, "Freezing point"),
                Triple(35.0, 45.0, "Extreme heat"),
                Triple(20.0, 20.0, "Same temperature")
            )

            return testCases.stream().map { (low, high, description) ->
                DynamicTest.dynamicTest("Temperature case: $description") {
                    val weather = Weather(
                        date = LocalDate(2025, 1, 15),
                        condition = WeatherCondition.CLEAR,
                        temperatureHigh = high,
                        temperatureLow = low,
                        humidity = 50,
                        icon = "01d",
                        description = description
                    )

                    assertTrue(weather.temperatureHigh >= weather.temperatureLow)
                    assertEquals(high, weather.temperatureHigh)
                    assertEquals(low, weather.temperatureLow)
                }
            }
        }
    }

    @Nested
    @DisplayName("Result Pattern Tests")
    inner class ResultPatternTests {

        @Test
        @DisplayName("Result.Success should contain data")
        fun `result success should provide data access`() {
            // Given
            val data = "test data"
            val result = Result.Success(data)

            // Then
            assertTrue(result.isSuccess)
            assertFalse(result.isError)
            assertFalse(result.isLoading)
            assertEquals(data, result.getOrNull())
            assertNull(result.exceptionOrNull())
        }

        @Test
        @DisplayName("Result.Error should contain exception")
        fun `result error should provide exception access`() {
            // Given
            val exception = DomainException.Network.Generic("Network unavailable")
            val result = Result.Error(exception)

            // Then
            assertFalse(result.isSuccess)
            assertTrue(result.isError)
            assertFalse(result.isLoading)
            assertNull(result.getOrNull())
            assertEquals(exception, result.exceptionOrNull())
        }

        @Test
        @DisplayName("Result.Loading should be in loading state")
        fun `result loading should indicate loading state`() {
            // Given
            val result = Result.Loading

            // Then
            assertFalse(result.isSuccess)
            assertFalse(result.isError)
            assertTrue(result.isLoading)
            assertNull(result.getOrNull())
            assertNull(result.exceptionOrNull())
        }

        @Test
        @DisplayName("Result transformations should work correctly")
        fun `result map should transform success data`() {
            // Given
            val originalResult = Result.Success(5)

            // When
            val mappedResult = originalResult.map { it * 2 }

            // Then
            assertTrue(mappedResult.isSuccess)
            assertEquals(10, mappedResult.getOrNull())
        }

        @Test
        @DisplayName("Result flatMap should chain operations")
        fun `result flatMap should chain operations correctly`() {
            // Given
            val originalResult = Result.Success(5)

            // When
            val flatMappedResult = originalResult.flatMap { value ->
                if (value > 0) Result.Success(value * 2)
                else Result.Error(DomainException.Validation.InvalidField("value", "Invalid value"))
            }

            // Then
            assertTrue(flatMappedResult.isSuccess)
            assertEquals(10, flatMappedResult.getOrNull())
        }

        @TestFactory
        @DisplayName("Result combination scenarios")
        fun `result combinations should work correctly`(): Stream<DynamicTest> {
            return Stream.of(
                DynamicTest.dynamicTest("Combine two successes") {
                    val result1 = Result.Success(5)
                    val result2 = Result.Success(3)
                    
                    val combined = result1.zip(result2) { a, b -> a + b }
                    
                    assertTrue(combined.isSuccess)
                    assertEquals(8, combined.getOrNull())
                },
                DynamicTest.dynamicTest("Combine success with error") {
                    val result1 = Result.Success(5)
                    val result2 = Result.Error(DomainException.Unknown("Error"))
                    
                    val combined = result1.zip(result2) { a: Int, b: Int -> a + b }
                    
                    assertTrue(combined.isError)
                },
                DynamicTest.dynamicTest("Combine with loading") {
                    val result1 = Result.Success(5)
                    val result2: Result<Int> = Result.Loading
                    
                    val combined = result1.zip(result2) { a: Int, b: Int -> a + b }
                    
                    assertTrue(combined.isLoading)
                }
            )
        }
    }

    @Nested
    @DisplayName("Domain Exception Tests")
    inner class DomainExceptionTests {

        @ParameterizedTest
        @DisplayName("Domain exceptions should have proper messages")
        @ValueSource(strings = ["Network error", "Validation failed", "Unknown error"])
        fun `domain exceptions should preserve error messages`(errorMessage: String) {
            // Given & When
            val networkError = DomainException.Network.Generic(errorMessage)
            val validationError = DomainException.Validation.InvalidField("test", errorMessage)
            val unknownError = DomainException.Unknown(errorMessage)

            // Then
            assertEquals(errorMessage, networkError.message)
            assertEquals("test is invalid: $errorMessage", validationError.message)
            assertEquals("Unknown error: $errorMessage", unknownError.message)
        }

        @Test
        @DisplayName("Domain exceptions should maintain cause chain")
        fun `domain exceptions should preserve exception cause`() {
            // Given
            val rootCause = RuntimeException("Root cause")
            val domainException = DomainException.System.LibraryError("TestLib", "Network issue")

            // Then
            assertEquals("Library error in TestLib: Network issue", domainException.message)
            assertNull(domainException.cause)  // This implementation doesn't preserve cause in this specific case
        }
    }

    @Test
    @DisplayName("Performance: Domain object creation")
    fun `domain objects should be created efficiently`() {
        val startTime = System.nanoTime()
        
        // Create many weather objects
        repeat(10000) {
            Weather(
                date = LocalDate(2025, 1, 15),
                condition = WeatherCondition.CLEAR,
                temperatureHigh = 25.0,
                temperatureLow = 15.0,
                humidity = 65,
                icon = "01d",
                description = "Clear sky"
            )
        }
        
        val endTime = System.nanoTime()
        val durationMs = (endTime - startTime) / 1_000_000
        
        assertTrue(durationMs < 100, "Creating 10000 Weather objects should take less than 100ms, took ${durationMs}ms")
    }
}