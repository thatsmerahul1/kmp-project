package com.weather.integration

import com.weather.testing.*
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import com.weather.domain.model.TemperatureUnit
import kotlinx.datetime.LocalDate
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Comprehensive demonstration of advanced parameterized testing patterns
 * This showcases the full capabilities of our 2025 testing framework
 */
class AdvancedParameterizedTestDemo {

    private val testRunner = ParameterizedTestRunner()

    @Test
    fun `demonstrate test matrix building with multiple parameters`() = runTest {
        val temperatureValues = listOf(-10.0, 0.0, 25.0, 40.0)
        val humidityValues = listOf(30, 60, 90)
        val testMatrix = TestBuilders.buildTestMatrix(temperatureValues, humidityValues)

        // Validate test matrix
        assertTrue(TestValidators.validateUniqueTestNames(testMatrix))
        assertEquals(12, testMatrix.size) // 4 * 3 = 12 combinations

        testRunner.runParameterizedTests(testMatrix) { testCase ->
            val (temperature, humidity) = testCase.input
            
            // Verify temperature and humidity combination makes sense
            when {
                temperature < 0 -> {
                    // Cold weather should have reasonable humidity
                    TestMatchers.assertWithinRange(humidity, 20, 100)
                }
                temperature > 35 -> {
                    // Hot weather scenario
                    TestMatchers.assertWithinRange(humidity, 10, 80)
                }
                else -> {
                    // Normal temperature range
                    TestMatchers.assertWithinRange(humidity, 20, 90)
                }
            }
            
            // Return a composite score for validation
            temperature + humidity
        }
    }

    @Test
    fun `demonstrate advanced property-based testing with custom generators`() = runTest {
        testRunner.runPropertyTests(
            generator = ::generateRealisticWeatherScenario,
            config = PropertyTestConfig(
                iterations = 30,
                shrinkingEnabled = true,
                maxShrinkAttempts = 10
            )
        ) { weatherScenario ->
            // Property 1: Temperature consistency
            val temperatureConsistent = weatherScenario.temperatureHigh >= weatherScenario.temperatureLow
            
            // Property 2: Seasonal appropriateness
            val seasonallyAppropriate = when (weatherScenario.date.monthNumber) {
                in 12..2 -> weatherScenario.temperatureHigh <= 15.0 // Winter
                in 3..5 -> weatherScenario.temperatureHigh in 10.0..25.0 // Spring
                in 6..8 -> weatherScenario.temperatureHigh >= 20.0 // Summer
                in 9..11 -> weatherScenario.temperatureHigh in 5.0..20.0 // Fall
                else -> true
            }
            
            // Property 3: Weather condition matches temperature
            val conditionMatchesTemp = when (weatherScenario.condition) {
                WeatherCondition.SNOW -> weatherScenario.temperatureHigh <= 5.0
                WeatherCondition.RAIN -> weatherScenario.humidity >= 70
                WeatherCondition.CLEAR -> weatherScenario.humidity <= 80
                else -> true
            }
            
            temperatureConsistent && seasonallyAppropriate && conditionMatchesTemp
        }
    }

    @Test
    fun `demonstrate edge case testing with comprehensive coverage`() = runTest {
        val normalWeather = Weather(
            date = LocalDate(2025, 6, 15),
            condition = WeatherCondition.CLEAR,
            temperatureHigh = 25.0,
            temperatureLow = 15.0,
            humidity = 60,
            icon = "01d",
            description = "Clear sky"
        )

        val edgeValues = listOf(
            // Extreme temperatures
            normalWeather.copy(temperatureHigh = -50.0, temperatureLow = -60.0, condition = WeatherCondition.SNOW),
            normalWeather.copy(temperatureHigh = 60.0, temperatureLow = 50.0, condition = WeatherCondition.CLEAR),
            
            // Extreme humidity
            normalWeather.copy(humidity = 0, condition = WeatherCondition.CLEAR),
            normalWeather.copy(humidity = 100, condition = WeatherCondition.RAIN),
            
            // Edge dates
            normalWeather.copy(date = LocalDate(2025, 1, 1)),
            normalWeather.copy(date = LocalDate(2025, 12, 31)),
            
            // Same temperature
            normalWeather.copy(temperatureHigh = 20.0, temperatureLow = 20.0)
        )

        val edgeCases = TestBuilders.buildEdgeCases(
            normalValue = normalWeather,
            edgeValues = edgeValues,
            testName = "Weather Edge Cases"
        )

        testRunner.runParameterizedTests(edgeCases) { testCase ->
            val weather = testCase.input
            
            // Validate basic constraints are maintained
            assertTrue(weather.temperatureHigh >= weather.temperatureLow, "Temperature consistency")
            TestMatchers.assertWithinRange(weather.humidity, 0, 100, "Humidity range")
            assertTrue(weather.date.year >= 2025, "Date validity")
            assertTrue(weather.description.isNotBlank(), "Description not empty")
            
            // Return validation score
            when {
                weather.temperatureHigh < -40 -> "extreme_cold"
                weather.temperatureHigh > 50 -> "extreme_heat"
                weather.humidity == 0 -> "no_humidity"
                weather.humidity == 100 -> "max_humidity"
                else -> "normal"
            }
        }
    }

    @Test
    fun `demonstrate benchmark testing with performance analysis`() = runTest {
        val benchmark = TestExecutionHelpers.benchmark(
            warmupIterations = 3,
            measurementIterations = 10
        ) {
            // Simulate weather data processing
            val weatherList = generateLargeWeatherDataset(100)
            weatherList.groupBy { it.condition }
                .mapValues { (_, weathers) -> 
                    weathers.map { it.temperatureHigh }.average() 
                }
        }

        // Analyze benchmark results
        assertTrue(benchmark.averageMs < 100.0, "Average execution time should be under 100ms")
        assertTrue(benchmark.standardDeviation < 50.0, "Standard deviation should be reasonable")
        
        println("Benchmark Results:")
        println("Average: ${benchmark.averageMs}ms")
        println("Min: ${benchmark.minMs}ms")
        println("Max: ${benchmark.maxMs}ms")
        println("Std Dev: ${benchmark.standardDeviation}ms")
    }

    @Test
    fun `demonstrate stress testing with load analysis`() = runTest {
        val stressResults = TestExecutionHelpers.runStressTest(
            iterations = 50,
            concurrency = 1 // Sequential for common code
        ) { iteration ->
            // Simulate weather API processing under load
            val weather = generateRealisticWeatherScenario()
            
            // Process weather data
            val processedData = mapOf(
                "temperature_celsius" to weather.temperatureHigh,
                "temperature_fahrenheit" to (weather.temperatureHigh * 9/5) + 32,
                "condition" to weather.condition.name,
                "humidity_category" to when (weather.humidity) {
                    in 0..30 -> "low"
                    in 31..70 -> "medium"
                    else -> "high"
                }
            )
            
            processedData
        }

        // Validate stress test results
        assertEquals(50, stressResults.size, "All iterations should complete")
        
        TestMatchers.assertAllSatisfy(stressResults, { result ->
            result.containsKey("temperature_celsius") &&
            result.containsKey("temperature_fahrenheit") &&
            result.containsKey("condition") &&
            result.containsKey("humidity_category")
        }, "All results should contain required keys")
        
        // Check for data consistency across iterations
        val temperatureValues = stressResults.mapNotNull { 
            it["temperature_celsius"] as? Double 
        }
        assertTrue(temperatureValues.isNotEmpty(), "Temperature values should be present")
        TestMatchers.assertAllSatisfy(temperatureValues, { temp ->
            temp >= -60.0 && temp <= 60.0
        }, "All temperatures should be within realistic range")
    }

    @Test
    fun `demonstrate scenario-based testing with complex workflows`() = runTest {
        val weatherWorkflowScenarios = listOf(
            TestScenario(
                name = "Morning Weather Check",
                given = "User checks weather at 8 AM on a workday",
                input = WorkflowInput(
                    time = "08:00",
                    dayType = "workday",
                    location = "urban",
                    userPreferences = mapOf("unit" to "celsius", "notifications" to "enabled")
                ),
                expected = WorkflowExpected(
                    shouldShowCommute = true,
                    shouldShowHourly = true,
                    temperatureUnit = TemperatureUnit.CELSIUS
                ),
                tags = setOf("morning", "commute", "workflow")
            ),
            TestScenario(
                name = "Weekend Leisure Check",
                given = "User checks weather on Saturday afternoon",
                input = WorkflowInput(
                    time = "14:00",
                    dayType = "weekend",
                    location = "suburban",
                    userPreferences = mapOf("unit" to "fahrenheit", "extended_forecast" to "enabled")
                ),
                expected = WorkflowExpected(
                    shouldShowCommute = false,
                    shouldShowWeekly = true,
                    temperatureUnit = TemperatureUnit.FAHRENHEIT
                ),
                tags = setOf("weekend", "leisure", "workflow")
            ),
            TestScenario(
                name = "Travel Planning Check",
                given = "User checks weather for different location while planning travel",
                input = WorkflowInput(
                    time = "20:00",
                    dayType = "any",
                    location = "travel_destination",
                    userPreferences = mapOf("unit" to "celsius", "travel_mode" to "enabled")
                ),
                expected = WorkflowExpected(
                    shouldShowWeekly = true,
                    shouldShowAlerts = true,
                    temperatureUnit = TemperatureUnit.CELSIUS
                ),
                tags = setOf("travel", "planning", "workflow")
            )
        )

        testRunner.runScenarioTests(weatherWorkflowScenarios) { scenario ->
            val input = scenario.input
            val preferences = input.userPreferences
            
            // Simulate workflow processing
            val result = WorkflowExpected(
                shouldShowCommute = input.dayType == "workday" && input.time.startsWith("0"),
                shouldShowHourly = input.time.startsWith("0") || input.time.startsWith("1"),
                shouldShowWeekly = input.dayType == "weekend" || input.location == "travel_destination",
                shouldShowAlerts = input.location == "travel_destination",
                temperatureUnit = when (preferences["unit"]) {
                    "fahrenheit" -> TemperatureUnit.FAHRENHEIT
                    else -> TemperatureUnit.CELSIUS
                }
            )
            
            result
        }
    }

    @Test
    fun `demonstrate test validation and quality assurance`() = runTest {
        // Create test cases with intentional issues for validation
        val testCases = listOf(
            TestCase("Unique Test 1", 1, description = "First test"),
            TestCase("Unique Test 2", 2, description = "Second test"),
            TestCase("Unique Test 3", 3, description = "Third test")
        )

        // Validate test quality
        assertTrue(
            TestValidators.validateUniqueTestNames(testCases),
            "Test names should be unique"
        )

        val scenarios = listOf(
            TestScenario("Scenario 1", "Given condition 1", "input1", "output1", setOf("tag1", "tag2")),
            TestScenario("Scenario 2", "Given condition 2", "input2", "output2", setOf("tag2", "tag3"))
        )

        assertTrue(
            TestValidators.validateTagConsistency(scenarios),
            "Test scenarios should have consistent tag usage"
        )

        val config = PropertyTestConfig(iterations = 50, maxShrinkAttempts = 20)
        assertTrue(
            TestValidators.validatePropertyTestConfig(config),
            "Property test configuration should be valid"
        )
    }

    // Helper functions and data classes
    private fun generateRealisticWeatherScenario(): Weather {
        val random = kotlin.random.Random
        val month = random.nextInt(1, 13)
        val day = random.nextInt(1, 29)
        
        // Generate season-appropriate weather
        val baseTemp = when (month) {
            12, 1, 2 -> random.nextDouble(-10.0, 10.0) // Winter
            3, 4, 5 -> random.nextDouble(5.0, 20.0)    // Spring
            6, 7, 8 -> random.nextDouble(20.0, 35.0)   // Summer
            else -> random.nextDouble(10.0, 25.0)      // Fall
        }
        
        val condition = when {
            baseTemp < 0 -> WeatherCondition.SNOW
            baseTemp > 30 && random.nextBoolean() -> WeatherCondition.CLEAR
            random.nextDouble() < 0.3 -> WeatherCondition.RAIN
            random.nextDouble() < 0.5 -> WeatherCondition.CLOUDS
            else -> WeatherCondition.CLEAR
        }
        
        val humidity = when (condition) {
            WeatherCondition.RAIN -> random.nextInt(70, 101)
            WeatherCondition.CLEAR -> random.nextInt(20, 70)
            WeatherCondition.SNOW -> random.nextInt(80, 101)
            else -> random.nextInt(40, 80)
        }
        
        return Weather(
            date = LocalDate(2025, month, day),
            condition = condition,
            temperatureHigh = baseTemp + random.nextDouble(0.0, 5.0),
            temperatureLow = baseTemp - random.nextDouble(0.0, 10.0),
            humidity = humidity,
            icon = "${condition.name.lowercase().take(2)}d",
            description = generateWeatherDescription(condition, baseTemp)
        )
    }

    private fun generateWeatherDescription(condition: WeatherCondition, temp: Double): String {
        val tempDesc = when {
            temp < 0 -> "very cold"
            temp < 10 -> "cold"
            temp < 20 -> "cool"
            temp < 30 -> "warm"
            else -> "hot"
        }
        
        return when (condition) {
            WeatherCondition.CLEAR -> "Clear and $tempDesc"
            WeatherCondition.CLOUDS -> "Cloudy and $tempDesc"
            WeatherCondition.RAIN -> "Rainy and $tempDesc"
            WeatherCondition.SNOW -> "Snowy and cold"
            WeatherCondition.THUNDERSTORM -> "Stormy and $tempDesc"
            else -> "Weather is $tempDesc"
        }
    }

    private fun generateLargeWeatherDataset(size: Int): List<Weather> {
        return (1..size).map { index ->
            generateRealisticWeatherScenario().copy(
                date = LocalDate(2025, 1, (index % 28) + 1)
            )
        }
    }

    // Data classes for complex scenarios
    data class WorkflowInput(
        val time: String,
        val dayType: String,
        val location: String,
        val userPreferences: Map<String, String>
    )

    data class WorkflowExpected(
        val shouldShowCommute: Boolean = false,
        val shouldShowHourly: Boolean = false,
        val shouldShowWeekly: Boolean = false,
        val shouldShowAlerts: Boolean = false,
        val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS
    )
}