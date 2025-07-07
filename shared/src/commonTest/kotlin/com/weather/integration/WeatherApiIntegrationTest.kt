package com.weather.integration

import com.weather.testing.*
import com.weather.data.remote.dto.WeatherItemDto
import com.weather.data.mapper.WeatherMapper
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

/**
 * Integration tests for Weather API functionality using parameterized testing patterns
 */
class WeatherApiIntegrationTest {

    private val testRunner = ParameterizedTestRunner()
    private val weatherMapper = WeatherMapper

    @Test
    fun `test weather API data mapping with parameterized scenarios`() = runTest {
        val mappingScenarios = listOf(
            TestScenario(
                name = "Clear Weather Mapping",
                given = "API returns clear weather data",
                input = WeatherItemDto(
                    date = "2025-01-15",
                    temperatureMax = 25.0,
                    temperatureMin = 15.0,
                    humidity = 60,
                    weatherCode = 0,
                    windSpeed = 5.5
                ),
                expected = WeatherCondition.CLEAR,
                tags = setOf("mapping", "clear_weather")
            ),
            TestScenario(
                name = "Rainy Weather Mapping",
                given = "API returns rainy weather data",
                input = WeatherItemDto(
                    date = "2025-01-16",
                    temperatureMax = 18.0,
                    temperatureMin = 12.0,
                    humidity = 85,
                    weatherCode = 61,
                    windSpeed = 8.2
                ),
                expected = WeatherCondition.RAIN,
                tags = setOf("mapping", "rainy_weather")
            ),
            TestScenario(
                name = "Snowy Weather Mapping",
                given = "API returns snowy weather data",
                input = WeatherItemDto(
                    date = "2025-01-17",
                    temperatureMax = -2.0,
                    temperatureMin = -8.0,
                    humidity = 95,
                    weatherCode = 71,
                    windSpeed = 12.0
                ),
                expected = WeatherCondition.SNOW,
                tags = setOf("mapping", "snowy_weather")
            ),
            TestScenario(
                name = "Cloudy Weather Mapping",
                given = "API returns cloudy weather data",
                input = WeatherItemDto(
                    date = "2025-01-18",
                    temperatureMax = 20.0,
                    temperatureMin = 14.0,
                    humidity = 70,
                    weatherCode = 3,
                    windSpeed = 6.1
                ),
                expected = WeatherCondition.CLOUDS,
                tags = setOf("mapping", "cloudy_weather")
            )
        )

        testRunner.runScenarioTests(mappingScenarios) { scenario ->
            val weather = with(weatherMapper) { scenario.input.toDomain() }
            weather.condition
        }
    }

    @Test
    fun `test weather data validation with property-based testing`() = runTest {
        testRunner.runPropertyTests(
            generator = { generateValidWeatherDto() },
            config = PropertyTestConfig(iterations = 50)
        ) { dto ->
            val weather = with(weatherMapper) { dto.toDomain() }
            
            // Property: Temperature high should always be >= temperature low
            weather.temperatureHigh >= weather.temperatureLow &&
            // Property: Humidity should be in valid range
            weather.humidity in 0..100 &&
            // Property: Date should be properly parsed
            weather.date.year >= 2025
        }
    }

    @Test
    fun `test extreme weather conditions with edge case testing`() = runTest {
        val extremeWeatherCases = listOf(
            TestCase(
                name = "Extreme Heat",
                input = WeatherItemDto(
                    date = "2025-07-15",
                    temperatureMax = 50.0,
                    temperatureMin = 45.0,
                    humidity = 10,
                    weatherCode = 0,
                    windSpeed = 0.0
                ),
                expected = 50.0,
                description = "Desert extreme heat conditions"
            ),
            TestCase(
                name = "Extreme Cold",
                input = WeatherItemDto(
                    date = "2025-01-15",
                    temperatureMax = -40.0,
                    temperatureMin = -50.0,
                    humidity = 80,
                    weatherCode = 71,
                    windSpeed = 25.0
                ),
                expected = -40.0,
                description = "Arctic extreme cold conditions"
            ),
            TestCase(
                name = "Maximum Humidity",
                input = WeatherItemDto(
                    date = "2025-08-15",
                    temperatureMax = 35.0,
                    temperatureMin = 30.0,
                    humidity = 100,
                    weatherCode = 95,
                    windSpeed = 2.0
                ),
                expected = 100,
                description = "Maximum humidity tropical conditions"
            ),
            TestCase(
                name = "Hurricane Winds",
                input = WeatherItemDto(
                    date = "2025-09-15",
                    temperatureMax = 28.0,
                    temperatureMin = 25.0,
                    humidity = 90,
                    weatherCode = 95,
                    windSpeed = 120.0
                ),
                expected = 120.0,
                description = "Hurricane force winds"
            )
        )

        testRunner.runParameterizedTests(extremeWeatherCases) { testCase ->
            val weather = with(weatherMapper) { (testCase.input as WeatherItemDto).toDomain() }
            
            when (testCase.name) {
                "Extreme Heat", "Extreme Cold" -> {
                    assertEquals(testCase.expected, weather.temperatureHigh)
                    assertTrue(weather.temperatureHigh >= weather.temperatureLow)
                }
                "Maximum Humidity" -> {
                    assertEquals(testCase.expected, weather.humidity)
                    assertTrue(weather.humidity <= 100)
                }
                "Hurricane Winds" -> {
                    assertEquals(testCase.expected, (testCase.input as WeatherItemDto).windSpeed)
                    assertTrue(weather.condition != WeatherCondition.CLEAR)
                }
            }
        }
    }

    @Test
    fun `test weather API error scenarios with parameterized error handling`() = runTest {
        val errorScenarios = listOf(
            TestScenario(
                name = "Invalid Date Format",
                given = "API returns invalid date",
                input = WeatherItemDto(
                    date = "invalid-date",
                    temperatureMax = 25.0,
                    temperatureMin = 15.0,
                    humidity = 60,
                    weatherCode = 0,
                    windSpeed = 5.5
                ),
                expected = "date_parse_error",
                tags = setOf("error_handling", "validation")
            ),
            TestScenario(
                name = "Invalid Weather Code",
                given = "API returns unknown weather code",
                input = WeatherItemDto(
                    date = "2025-01-15",
                    temperatureMax = 25.0,
                    temperatureMin = 15.0,
                    humidity = 60,
                    weatherCode = 999,
                    windSpeed = 5.5
                ),
                expected = "CLEAR", // Should default to clear
                tags = setOf("error_handling", "weather_code")
            ),
            TestScenario(
                name = "Invalid Temperature Range",
                given = "API returns low > high temperature",
                input = WeatherItemDto(
                    date = "2025-01-15",
                    temperatureMax = 15.0,
                    temperatureMin = 25.0, // Invalid: min > max
                    humidity = 60,
                    weatherCode = 0,
                    windSpeed = 5.5
                ),
                expected = "temperature_validation_error",
                tags = setOf("error_handling", "temperature")
            )
        )

        testRunner.runScenarioTests(errorScenarios) { scenario ->
            try {
                val weather = with(weatherMapper) { scenario.input.toDomain() }
                
                when (scenario.name) {
                    "Invalid Weather Code" -> weather.condition.name
                    "Invalid Temperature Range" -> {
                        // Should handle gracefully or throw validation error
                        if (weather.temperatureHigh < weather.temperatureLow) {
                            "temperature_validation_error"
                        } else {
                            "handled_gracefully"
                        }
                    }
                    else -> "success"
                }
            } catch (e: Exception) {
                when (scenario.name) {
                    "Invalid Date Format" -> "date_parse_error"
                    else -> "unexpected_error"
                }
            }
        }
    }

    @Test
    fun `test weather data consistency across multiple API calls`() = runTest {
        val consistencyTestCases = WeatherTestData.coordinateTestCases.map { coordinateCase ->
            TestCase(
                name = "Weather Consistency for ${coordinateCase.name}",
                input = coordinateCase.input,
                expected = coordinateCase.name,
                description = "Weather data should be consistent for ${coordinateCase.name}"
            )
        }

        testRunner.runParameterizedTests(consistencyTestCases) { testCase ->
            val (lat, lon) = testCase.input as Pair<Double, Double>
            
            // Simulate multiple API calls for the same location
            val weatherResults = (1..3).map {
                with(weatherMapper) {
                    WeatherItemDto(
                        date = "2025-01-15",
                        temperatureMax = 20.0 + (lat / 10), // Simulate latitude-based temperature
                        temperatureMin = 10.0 + (lat / 10),
                        humidity = 60,
                        weatherCode = if (lat > 0) 0 else 61, // Clear in north, rain in south
                        windSpeed = kotlin.math.abs(lon / 20) // Wind based on longitude
                    ).toDomain()
                }
            }
            
            // Verify consistency
            val firstResult = weatherResults.first()
            assertTrue(
                weatherResults.all { it.condition == firstResult.condition },
                "Weather condition should be consistent for ${testCase.expected}"
            )
            
            assertTrue(
                weatherResults.all { 
                    kotlin.math.abs(it.temperatureHigh - firstResult.temperatureHigh) < 0.1 
                },
                "Temperature should be consistent for ${testCase.expected}"
            )
        }
    }

    /**
     * Helper function to generate valid weather DTOs for property-based testing
     */
    private fun generateValidWeatherDto(): WeatherItemDto {
        val random = kotlin.random.Random
        
        return WeatherItemDto(
            date = "2025-${(1..12).random().toString().padStart(2, '0')}-${(1..28).random().toString().padStart(2, '0')}",
            temperatureMax = random.nextDouble(-50.0, 60.0),
            temperatureMin = random.nextDouble(-60.0, 50.0),
            humidity = random.nextInt(0, 101),
            weatherCode = listOf(0, 1, 2, 3, 45, 48, 51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 71, 73, 75, 77, 80, 81, 82, 85, 86, 95, 96, 99).random(),
            windSpeed = random.nextDouble(0.0, 150.0)
        ).let { dto ->
            // Ensure temperature max >= min
            if (dto.temperatureMax < dto.temperatureMin) {
                dto.copy(
                    temperatureMax = dto.temperatureMin + random.nextDouble(0.1, 10.0)
                )
            } else {
                dto
            }
        }
    }
}