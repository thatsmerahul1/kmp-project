# 🧪 Testing Guide - WeatherKMP

## 📋 Table of Contents
- [Overview](#overview)
- [Test Structure](#test-structure)
- [Running Tests](#running-tests)
- [Code Coverage](#code-coverage)
- [Writing Tests](#writing-tests)
- [CI/CD Integration](#cicd-integration)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)

## Overview

This guide covers the comprehensive testing infrastructure for the WeatherKMP project, including unit tests, integration tests, code coverage with Kover, and automated test execution commands.

### Key Features
- **Kotlin Multiplatform Testing**: Shared tests run on all platforms
- **85% Coverage Target**: Enforced through Kover verification
- **Automated Test Commands**: Custom Gradle tasks with report popup
- **CI/CD Integration**: Automated testing and coverage reporting in PRs
- **Platform-Specific Tests**: Android and iOS-specific test capabilities

## Test Structure

```
kmp-project/
├── shared/
│   └── src/
│       ├── commonTest/kotlin/     # Shared tests (run on all platforms)
│       ├── androidTest/kotlin/    # Android-specific tests
│       └── iosTest/kotlin/        # iOS-specific tests
├── androidApp/
│   └── src/
│       ├── test/java/            # Android unit tests
│       └── androidTest/java/     # Android instrumentation tests
└── iosApp/
    └── iosAppTests/              # iOS-specific UI tests
```

### Test Categories

#### 1. **Unit Tests** (shared/commonTest)
- Domain model tests
- Use case tests
- Repository tests
- ViewModel tests
- Mapper tests

#### 2. **Integration Tests** (shared/commonTest)
- Database operations
- API integration
- Cache management
- Data flow verification

#### 3. **Platform Tests**
- Android: Compose UI tests
- iOS: SwiftUI tests

## Running Tests

### Quick Commands

```bash
# Run all tests with coverage report popup
./gradlew testAll --popup-report

# Run specific module tests
./gradlew testShared --popup-report
./gradlew testAndroid --popup-report
./gradlew testIos --popup-report

# Run tests with coverage (no popup)
./gradlew testAllWithCoverage

# Generate and open coverage report
./gradlew koverHtmlReportWithOpen

# Verify coverage thresholds
./gradlew verifyCoverage
```

### Detailed Test Execution

#### 1. Shared Module Tests
```bash
# Run all shared tests
./gradlew :shared:allTests

# Run specific platform tests
./gradlew :shared:jvmTest
./gradlew :shared:iosX64Test
./gradlew :shared:iosArm64Test
./gradlew :shared:iosSimulatorArm64Test
```

#### 2. Android Tests
```bash
# Unit tests
./gradlew :androidApp:testDebugUnitTest
./gradlew :androidApp:testReleaseUnitTest

# All Android tests
./gradlew :androidApp:test
```

#### 3. iOS Tests
```bash
# Through shared module
./gradlew :shared:iosTest

# Native iOS tests (requires Xcode)
cd iosApp
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15'
```

## Code Coverage

### Kover Configuration

The project uses Kover 0.7.5 for code coverage with the following features:

#### Coverage Thresholds
- **Minimum Line Coverage**: 80%
- **Verification**: Automatic in CI/CD
- **Reports**: HTML and XML formats

#### Excluded from Coverage
- Generated code (`*_Factory`, `*_Impl`)
- DI modules (`*.di` packages)
- Composable functions (`@Composable` annotated)
- Database generated code
- Build configuration classes

### Coverage Reports

#### Generate Reports
```bash
# HTML report (human-readable)
./gradlew koverHtmlReport

# XML report (for CI/CD)
./gradlew koverXmlReport

# Both reports
./gradlew koverReport
```

#### View Reports
- **HTML**: `build/reports/kover/html/index.html`
- **XML**: `build/reports/kover/xml/report.xml`

#### Coverage Badges
```bash
# Generate coverage badge (coming soon)
./gradlew generateCoverageBadge
```

## Writing Tests

### Test Dependencies

```kotlin
// In shared/commonTest
dependencies {
    implementation(libs.kotlin.test)
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.turbine) // For Flow testing
    implementation(libs.mockk) // For mocking
}
```

### Test Examples

#### 1. Domain Model Test
```kotlin
class WeatherConditionTest {
    @Test
    fun `should map weather codes correctly`() {
        assertEquals(WeatherCondition.CLEAR, WeatherCondition.fromCode(0))
        assertEquals(WeatherCondition.CLOUDS, WeatherCondition.fromCode(3))
        assertEquals(WeatherCondition.RAIN, WeatherCondition.fromCode(61))
        assertEquals(WeatherCondition.UNKNOWN, WeatherCondition.fromCode(999))
    }
}
```

#### 2. Use Case Test with Coroutines
```kotlin
class GetWeatherForecastUseCaseTest {
    private val repository = mockk<WeatherRepository>()
    private val useCase = GetWeatherForecastUseCase(repository)
    
    @Test
    fun `should emit cached then fresh data`() = runTest {
        // Given
        val cachedData = listOf(createWeatherTestData())
        val freshData = listOf(createWeatherTestData(temperature = 25.0))
        
        coEvery { repository.getWeatherForecast() } returns flow {
            emit(Result.success(cachedData))
            emit(Result.success(freshData))
        }
        
        // When & Then
        useCase().test {
            assertEquals(Result.success(cachedData), awaitItem())
            assertEquals(Result.success(freshData), awaitItem())
            awaitComplete()
        }
    }
}
```

#### 3. ViewModel Test with Turbine
```kotlin
class WeatherViewModelTest {
    @Test
    fun `should update UI state when loading weather`() = runTest {
        // Given
        val viewModel = WeatherViewModel(mockUseCase)
        
        // When & Then
        viewModel.uiState.test {
            assertEquals(WeatherUiState(isLoading = true), awaitItem())
            assertEquals(
                WeatherUiState(
                    isLoading = false,
                    weatherList = testWeatherList
                ),
                awaitItem()
            )
        }
    }
}
```

### Test Utilities

#### Test Data Builders
```kotlin
// shared/commonTest/kotlin/com/weather/test/TestDataBuilders.kt
fun createWeatherTestData(
    date: LocalDate = LocalDate(2024, 1, 1),
    temperatureHigh: Double = 20.0,
    temperatureLow: Double = 10.0,
    condition: WeatherCondition = WeatherCondition.CLEAR
) = Weather(
    date = date,
    temperatureHigh = temperatureHigh,
    temperatureLow = temperatureLow,
    condition = condition,
    humidity = 65,
    description = "Clear sky"
)
```

#### Custom Test Rules
```kotlin
// For Coroutines
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
```

## CI/CD Integration

### GitHub Actions Workflow

The CI pipeline automatically:
1. Runs all tests on PR and push
2. Generates coverage reports
3. Posts coverage summary as PR comment
4. Uploads test and coverage artifacts
5. Verifies coverage thresholds

#### PR Comment Example
```
📊 Code Coverage Report
Overall Coverage: 85.3% ✅
Changed Files: 92.1% ✅

| Module | Coverage |
|--------|----------|
| shared | 87.2% |
| androidApp | 82.1% |
```

### Local Pre-commit Hook

```bash
#!/bin/sh
# .git/hooks/pre-commit

echo "Running tests before commit..."
./gradlew testAll

if [ $? -ne 0 ]; then
    echo "Tests failed! Commit aborted."
    exit 1
fi

echo "Checking coverage..."
./gradlew koverVerify

if [ $? -ne 0 ]; then
    echo "Coverage below threshold! Commit aborted."
    exit 1
fi
```

## Best Practices

### 1. **Test Naming**
```kotlin
// Use descriptive test names with backticks
@Test
fun `should return error when network request fails`() { }

// Group related tests in nested classes
class WeatherRepositoryTest {
    @Nested
    inner class GetWeatherForecast {
        @Test
        fun `should emit cached data first`() { }
        
        @Test
        fun `should fetch fresh data after cache`() { }
    }
}
```

### 2. **Test Structure (AAA Pattern)**
```kotlin
@Test
fun `should update weather list on refresh`() {
    // Arrange
    val mockData = createTestWeatherList()
    coEvery { repository.getWeatherForecast() } returns flowOf(Result.success(mockData))
    
    // Act
    viewModel.onEvent(WeatherUiEvent.RefreshWeather)
    
    // Assert
    assertEquals(mockData, viewModel.uiState.value.weatherList)
}
```

### 3. **Coroutine Testing**
```kotlin
// Always use runTest for coroutine tests
@Test
fun `should handle concurrent operations`() = runTest {
    // Use advanceUntilIdle() to execute all pending coroutines
    advanceUntilIdle()
    
    // Use TestScope for controlled execution
    val job = launch {
        delay(1000)
        // Some operation
    }
    
    advanceTimeBy(1000)
    job.join()
}
```

### 4. **Flow Testing**
```kotlin
@Test
fun `should test Flow emissions`() = runTest {
    myFlow.test {
        // Assert first emission
        assertEquals(expected1, awaitItem())
        
        // Assert second emission
        assertEquals(expected2, awaitItem())
        
        // Ensure no more emissions
        awaitComplete()
    }
}
```

### 5. **Mock Management**
```kotlin
class SomeTest {
    @MockK
    private lateinit var mockRepo: WeatherRepository
    
    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        
        // Set up common mock behavior
        coEvery { mockRepo.getWeatherForecast() } returns flowOf(Result.success(emptyList()))
    }
    
    @AfterEach
    fun tearDown() {
        unmockkAll()
    }
}
```

## Troubleshooting

### Common Issues

#### 1. **Tests Not Found**
```bash
# Ensure test source sets are configured
./gradlew :shared:cleanTest :shared:test
```

#### 2. **Coverage Not Generated**
```bash
# Clean and rebuild
./gradlew clean
./gradlew testAll koverHtmlReport
```

#### 3. **iOS Tests Failing**
```bash
# Ensure simulators are available
xcrun simctl list devices

# Reset simulator
xcrun simctl erase all
```

#### 4. **Flaky Tests**
- Use `TestDispatcher` for time-based tests
- Avoid hardcoded delays
- Use `IdlingResource` for async operations
- Mock all external dependencies

#### 5. **Out of Memory**
```bash
# Increase heap size in gradle.properties
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=512m
```

### Debug Commands

```bash
# Run tests with detailed output
./gradlew test --info

# Run specific test class
./gradlew test --tests "com.weather.domain.WeatherConditionTest"

# Run tests with debugging
./gradlew test --debug-jvm

# Generate test report even on failure
./gradlew test --continue
```

## Continuous Improvement

### Coverage Goals
- **Current**: ~85% (target achieved)
- **Next Quarter**: 90%
- **Focus Areas**: 
  - iOS-specific code
  - Error handling paths
  - Edge cases

### Upcoming Features
- [ ] Mutation testing with Pitest
- [ ] Performance benchmarking
- [ ] Visual regression testing
- [ ] API contract testing
- [ ] Load testing for repository layer

---

*Last Updated: January 2025*  
*See also: [TECHNICAL_DOCS.md](TECHNICAL_DOCS.md) | [CONTRIBUTING.md](CONTRIBUTING.md)*