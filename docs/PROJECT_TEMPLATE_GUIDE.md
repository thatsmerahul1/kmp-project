# WeatherKMP 2025 Project Template Guide

## Overview

This comprehensive guide covers the WeatherKMP 2025 project template, a modern Kotlin Multiplatform template that implements cutting-edge development practices and standards for building cross-platform applications.

## Table of Contents

1. [Quick Start](#quick-start)
2. [Architecture Overview](#architecture-overview)
3. [Feature Systems](#feature-systems)
4. [Security Implementation](#security-implementation)
5. [Monitoring & Telemetry](#monitoring--telemetry)
6. [Template Structure](#template-structure)
7. [Development Workflow](#development-workflow)
8. [Customization Guide](#customization-guide)
9. [Best Practices](#best-practices)
10. [Troubleshooting](#troubleshooting)

## Quick Start

### Prerequisites

- **JDK 17+** (Required for Kotlin multiplatform)
- **Android Studio Arctic Fox** or later
- **Xcode 14+** (for iOS development)
- **IntelliJ IDEA 2023.1+** (recommended for desktop development)
- **Node.js 18+** (for web target if enabled)

### Using the Project Initialization Wizard

1. **Run the initialization script:**
   ```bash
   cd scripts
   kotlinc -script init-project.kt -- --interactive
   ```

2. **Follow the interactive prompts:**
   - Project name and package
   - Target platforms (Android, iOS, Desktop, Web)
   - Features to enable
   - Monitoring provider
   - CI/CD platform

3. **Navigate to generated project:**
   ```bash
   cd YourProjectName
   ./gradlew build
   ```

### Manual Setup

If you prefer manual setup:

1. **Clone the template:**
   ```bash
   git clone https://github.com/your-org/weatherkmp-template.git MyWeatherApp
   cd MyWeatherApp
   ```

2. **Configure the project:**
   - Update `gradle.properties` with your settings
   - Modify package names in build files
   - Configure API keys in `local.properties`

3. **Build and run:**
   ```bash
   ./gradlew build
   ./gradlew :androidApp:installDebug  # Android
   # Open iosApp/iosApp.xcodeproj for iOS
   ```

## Architecture Overview

### Layer Structure

The template follows Clean Architecture principles adapted for KMP:

```
┌─────────────────────────────────────────┐
│             Presentation Layer           │
│  ┌─────────────┐ ┌─────────────────────┐ │
│  │  ViewModels │ │   UI Components     │ │
│  │             │ │ (Compose/Platform)  │ │
│  └─────────────┘ └─────────────────────┘ │
└─────────────────────────────────────────┘
                     │
┌─────────────────────────────────────────┐
│              Domain Layer                │
│  ┌─────────────┐ ┌─────────────────────┐ │
│  │  Use Cases  │ │     Entities        │ │
│  │             │ │                     │ │
│  └─────────────┘ └─────────────────────┘ │
└─────────────────────────────────────────┘
                     │
┌─────────────────────────────────────────┐
│               Data Layer                 │
│  ┌─────────────┐ ┌─────────────────────┐ │
│  │Repositories │ │   Data Sources      │ │
│  │             │ │  (Remote/Local)     │ │
│  └─────────────┘ └─────────────────────┘ │
└─────────────────────────────────────────┘
```

### Cross-Platform Strategy

- **Shared Business Logic**: Domain layer and use cases
- **Platform-Specific UI**: Native implementations for optimal UX
- **Common Data Layer**: Shared repositories and data sources
- **Feature Toggles**: Centralized feature management across platforms

## Feature Systems

### Feature Toggle System

The template includes a comprehensive feature toggle system:

#### Basic Usage

```kotlin
// Check if feature is enabled
if (FeatureToggleUtils.isEnabled(FeatureFlag.DARK_MODE)) {
    // Apply dark theme
}

// Use feature with fallback
FeatureToggleUtils.withFeature(
    feature = FeatureFlag.AI_WEATHER_PREDICTIONS,
    enabledBlock = { 
        // AI-powered predictions
        getAIPredictions()
    },
    disabledBlock = { 
        // Standard forecast
        getStandardForecast()
    }
)
```

#### Feature Configuration

Features can be configured with:
- **Rollout percentage**: Gradual feature rollouts
- **Target audience**: User segmentation
- **Environment restrictions**: Dev/staging/prod
- **Time-based controls**: Start/end dates
- **Dependencies**: Feature interdependencies

#### A/B Testing

```kotlin
// A/B test configuration
val abTest = ABTestConfiguration(
    testId = "weather_ui_test",
    feature = "weather_animations",
    variants = mapOf(
        "control" to false,
        "treatment" to true
    ),
    trafficAllocation = mapOf(
        "control" to 50,
        "treatment" to 50
    ),
    startDate = Clock.System.now(),
    endDate = Clock.System.now().plus(30.days)
)
```

### Compose Multiplatform Support

#### Platform-Aware Components

```kotlin
@Composable
fun WeatherCard() {
    val config = rememberPlatformConfiguration()
    
    WeatherKMPCard(
        elevation = when (config.platform) {
            Platform.IOS -> 0.dp      // Flat design
            Platform.DESKTOP -> 4.dp  // Standard elevation
            else -> 2.dp
        }
    ) {
        // Card content
    }
}
```

#### Responsive Layouts

```kotlin
@Composable
fun WeatherScreen() {
    ResponsiveLayout(
        compactContent = { 
            // Mobile layout
            SingleColumnLayout()
        },
        expandedContent = { 
            // Desktop/Tablet layout
            TwoColumnLayout()
        }
    )
}
```

#### Platform-Specific Theming

The template automatically adapts to platform design languages:
- **Android**: Material Design 3 with dynamic colors
- **iOS**: iOS design system colors and typography
- **Desktop**: Desktop-optimized spacing and interactions
- **Web**: Web-friendly hover states and accessibility

## Security Implementation

### API Key Management

```kotlin
// Secure API key storage
val apiKeyManager = ApiKeyManager()

// Store encrypted API key
apiKeyManager.storeEncryptedApiKey(
    service = ApiService.WEATHER,
    apiKey = "your-api-key"
)

// Retrieve decrypted API key
val apiKey = apiKeyManager.getDecryptedApiKey(ApiService.WEATHER)
```

### Certificate Pinning

```kotlin
// Configure certificate pinning
val certificatePins = listOf(
    CertificatePin(
        hostname = "api.openweathermap.org",
        pins = listOf("sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="),
        includeSubdomains = true
    )
)

val httpClient = HttpClient {
    install(CertificatePinningPlugin) {
        pins.addAll(certificatePins)
    }
}
```

### Security Best Practices

1. **Encryption at Rest**: All sensitive data encrypted using platform keystores
2. **Network Security**: Certificate pinning and secure protocols
3. **Code Obfuscation**: ProGuard/R8 rules for release builds
4. **Security Auditing**: Automated OWASP scans in CI/CD
5. **Dependency Scanning**: Vulnerability checks for all dependencies

## Monitoring & Telemetry

### OpenTelemetry Integration

```kotlin
// Record custom metrics
TelemetryUtils.recordPerformanceMetric(
    operation = "weather_data_fetch",
    duration = 1200L,
    success = true,
    additionalAttributes = mapOf(
        "api_endpoint" to "current_weather",
        "location" to "New York"
    )
)

// Distributed tracing
TelemetryUtils.withSpan(
    operationName = "get_weather_forecast",
    attributes = mapOf("location" to "40.7128,-74.0060")
) {
    // Your operation here
    weatherApi.getForecast(latitude, longitude)
}
```

### Metrics Collection

The template automatically collects:

#### System Metrics
- Memory usage and pressure
- CPU utilization
- Network connectivity and performance
- Battery level and charging state
- Storage usage

#### Application Metrics
- App startup time
- Session duration
- Crash and ANR rates
- Feature usage statistics
- User interaction patterns

#### Business Metrics
- Daily active users
- API call success rates
- Weather request patterns
- User retention rates

### Crash Analytics

```kotlin
// Record custom crash information
CrashAnalyticsUtils.recordBreadcrumb(
    message = "User searched for location: $location",
    category = BreadcrumbCategory.USER_ACTION
)

// Set user context
CrashAnalyticsUtils.setUserInfo(
    userId = "user123",
    email = "user@example.com"
)

// Record non-fatal errors
CrashAnalyticsUtils.recordNonFatalError(
    message = "API rate limit exceeded",
    errorType = "RateLimitError",
    context = "weather_api"
)
```

## Template Structure

```
WeatherKMP/
├── androidApp/                    # Android application
│   ├── src/main/kotlin/          # Android-specific code
│   ├── src/test/kotlin/          # Android unit tests
│   ├── src/androidTest/kotlin/   # Android instrumentation tests
│   └── build.gradle.kts          # Android build configuration
│
├── shared/                        # Shared KMP module
│   ├── src/commonMain/kotlin/     # Common Kotlin code
│   │   ├── com/weather/
│   │   │   ├── data/             # Data layer
│   │   │   ├── domain/           # Domain layer
│   │   │   ├── presentation/     # Presentation layer
│   │   │   ├── di/               # Dependency injection
│   │   │   ├── security/         # Security implementations
│   │   │   ├── monitoring/       # Telemetry and monitoring
│   │   │   ├── features/         # Feature toggle system
│   │   │   └── ui/               # Compose Multiplatform UI
│   │   │
│   │   └── resources/            # Shared resources
│   │
│   ├── src/androidMain/kotlin/   # Android-specific implementations
│   ├── src/iosMain/kotlin/       # iOS-specific implementations
│   ├── src/commonTest/kotlin/    # Common tests
│   └── build.gradle.kts          # Shared module build config
│
├── iosApp/                       # iOS application
│   ├── iosApp/                   # iOS source code
│   ├── iosAppTests/              # iOS unit tests
│   └── iosApp.xcodeproj          # Xcode project
│
├── docs/                         # Documentation
│   ├── README.md                 # Project overview
│   ├── ARCHITECTURE.md           # Architecture documentation
│   ├── PROJECT_TEMPLATE_GUIDE.md # This guide
│   └── api/                      # Generated API docs
│
├── scripts/                      # Build and utility scripts
│   ├── init-project.kt          # Project initialization wizard
│   ├── setup-development.sh     # Development environment setup
│   └── deploy.sh                # Deployment scripts
│
├── monitoring/                   # Monitoring configuration
│   ├── dashboards/              # Grafana dashboards
│   ├── config/                  # Prometheus/monitoring config
│   └── docker-compose.yml       # Monitoring stack
│
├── security/                     # Security configurations
│   ├── certificates/            # Certificate pins
│   └── policies/                # Security policies
│
├── .github/workflows/            # GitHub Actions CI/CD
│   ├── ci.yml                   # Continuous integration
│   ├── security-audit.yml       # Security auditing
│   └── monitoring-dashboard.yml # Monitoring deployment
│
├── gradle/                       # Gradle wrapper
├── build-logic/                  # Custom build logic
├── build.gradle.kts             # Root build configuration
├── settings.gradle.kts          # Project structure
├── gradle.properties            # Gradle properties
└── INITIALIZATION_SUMMARY.md    # Setup summary
```

## Development Workflow

### 1. Feature Development

#### Create Feature Branch
```bash
git checkout -b feature/weather-maps
```

#### Enable Feature Toggle
```kotlin
// In development, enable your feature
FeatureToggleUtils.getManager()?.enableFeature("weather_maps")
```

#### Implement Feature
- Add domain entities and use cases
- Implement data layer components
- Create UI components with Compose Multiplatform
- Add platform-specific implementations if needed

#### Add Tests
```kotlin
// Unit tests
@Test
fun `should fetch weather data successfully`() {
    // Test implementation
}

// UI tests
@Test
fun `should display weather information correctly`() {
    // Compose UI test
}
```

### 2. Code Quality Checks

```bash
# Run all quality checks
./gradlew ktlintCheck detekt test

# Security scan
./gradlew securityScan

# Generate documentation
./gradlew dokkaHtml
```

### 3. Platform Testing

#### Android
```bash
./gradlew :androidApp:connectedAndroidTest
./gradlew :shared:testDebugUnitTest
```

#### iOS
```bash
./gradlew :shared:iosTest
# Or open Xcode project for device testing
```

#### Desktop (if enabled)
```bash
./gradlew :desktopApp:test
./gradlew :desktopApp:run
```

### 4. Performance Testing

```bash
# Benchmark tests
./gradlew benchmark

# Memory profiling
./gradlew memoryTest

# Load testing
./gradlew loadTest
```

### 5. Feature Toggle Management

#### Development Testing
```kotlin
// Test feature variants
val adminController = FeatureToggleAdminController(featureManager)
adminController.toggleFeature("weather_maps")
```

#### Gradual Rollout
```kotlin
// Start with 10% rollout
featureManager.setFeatureRolloutPercentage("weather_maps", 10)

// Monitor metrics and gradually increase
featureManager.setFeatureRolloutPercentage("weather_maps", 25)
featureManager.setFeatureRolloutPercentage("weather_maps", 50)
featureManager.setFeatureRolloutPercentage("weather_maps", 100)
```

## Customization Guide

### 1. Adding New Platforms

#### Desktop Support
1. Update `settings.gradle.kts`:
   ```kotlin
   include(":desktopApp")
   ```

2. Create desktop module:
   ```kotlin
   // desktopApp/build.gradle.kts
   plugins {
       alias(libs.plugins.kotlinJvm)
       alias(libs.plugins.jetbrainsCompose)
   }
   ```

3. Add desktop-specific implementations

#### Web Support
1. Add JS target to shared module:
   ```kotlin
   js(IR) {
       browser {
           commonWebpackConfig {
               cssSupport {
                   enabled.set(true)
               }
           }
       }
   }
   ```

2. Create web-specific UI components

### 2. Adding New Features

#### 1. Define Feature Flag
```kotlin
enum class FeatureFlag(val key: String, val defaultEnabled: Boolean = false) {
    // Add your feature
    WEATHER_RADAR("weather_radar", false)
}
```

#### 2. Create Domain Components
```kotlin
// Domain entity
data class WeatherRadar(
    val location: Location,
    val radarData: List<RadarFrame>,
    val timestamp: Instant
)

// Use case
class GetWeatherRadarUseCase(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(location: Location): Result<WeatherRadar> {
        return FeatureToggleUtils.withFeature(
            feature = FeatureFlag.WEATHER_RADAR,
            enabledBlock = { repository.getWeatherRadar(location) },
            disabledBlock = { Result.Error(FeatureDisabledException()) }
        )
    }
}
```

#### 3. Implement Data Layer
```kotlin
// Repository interface
interface WeatherRepository {
    suspend fun getWeatherRadar(location: Location): Result<WeatherRadar>
}

// Implementation
class WeatherRepositoryImpl(
    private val api: WeatherApi,
    private val cache: WeatherCache
) : WeatherRepository {
    
    override suspend fun getWeatherRadar(location: Location): Result<WeatherRadar> {
        return try {
            val radarData = api.getRadarData(location)
            cache.storeRadarData(radarData)
            Result.Success(radarData)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
```

#### 4. Create UI Components
```kotlin
@Composable
fun WeatherRadarScreen(
    viewModel: WeatherRadarViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Feature toggle check
    if (!FeatureToggleUtils.isEnabled(FeatureFlag.WEATHER_RADAR)) {
        FeatureDisabledScreen(
            feature = "Weather Radar",
            description = "This feature is not yet available"
        )
        return
    }
    
    // Radar UI implementation
    WeatherRadarMap(
        radarData = uiState.radarData,
        onLocationChange = viewModel::updateLocation
    )
}
```

### 3. Customizing Monitoring

#### Add Custom Metrics
```kotlin
// Business metric
TelemetryUtils.recordBusinessMetric(
    name = "weather_radar_views",
    value = 1.0,
    attributes = mapOf(
        "location_type" to "city",
        "user_segment" to "premium"
    )
)

// Performance metric
val startTime = Clock.System.now()
val result = performOperation()
val duration = Clock.System.now() - startTime

TelemetryUtils.recordPerformanceMetric(
    operation = "weather_radar_load",
    duration = duration.inWholeMilliseconds,
    success = result.isSuccess
)
```

#### Custom Dashboards
1. Create Grafana dashboard configuration
2. Add to `monitoring/dashboards/custom-feature.json`
3. Update monitoring workflow

### 4. Security Customization

#### Add New Certificate Pins
```kotlin
val customPins = listOf(
    CertificatePin(
        hostname = "api.custom-weather.com",
        pins = listOf("sha256/YOUR_CERTIFICATE_HASH"),
        includeSubdomains = true
    )
)
```

#### Custom Encryption
```kotlin
// Implement custom encryption provider
class CustomEncryptionProvider : EncryptionProvider {
    override fun encrypt(data: String): String? {
        // Custom encryption logic
    }
    
    override fun decrypt(encryptedData: String): String? {
        // Custom decryption logic
    }
}
```

## Best Practices

### 1. Code Organization

#### Package Structure
```
com.yourcompany.weather/
├── data/
│   ├── local/           # Local data sources
│   ├── remote/          # Remote data sources
│   ├── repository/      # Repository implementations
│   └── dto/             # Data transfer objects
├── domain/
│   ├── entity/          # Domain entities
│   ├── repository/      # Repository interfaces
│   ├── usecase/         # Use cases
│   └── common/          # Common domain classes
└── presentation/
    ├── viewmodel/       # ViewModels
    ├── ui/              # UI components
    └── navigation/      # Navigation logic
```

#### Naming Conventions
- **Classes**: PascalCase (`WeatherRepository`)
- **Functions**: camelCase (`getCurrentWeather`)
- **Constants**: SCREAMING_SNAKE_CASE (`API_BASE_URL`)
- **Features**: snake_case (`weather_radar`)

### 2. Feature Development

#### Feature Toggle Strategy
1. **Start Disabled**: New features should default to disabled
2. **Gradual Rollout**: Use percentage-based rollouts
3. **Monitor Metrics**: Track feature usage and performance
4. **A/B Testing**: Compare variants when appropriate
5. **Clean Up**: Remove feature toggles after full rollout

#### Error Handling
```kotlin
// Consistent error handling
sealed class WeatherError : Exception() {
    object NetworkError : WeatherError()
    object ApiKeyInvalid : WeatherError()
    object LocationNotFound : WeatherError()
    data class UnknownError(override val message: String) : WeatherError()
}

// Result pattern
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}
```

### 3. Testing Strategy

#### Test Pyramid
```
       ┌─────────────────┐
       │   UI Tests      │ ← Few
       │   (Integration) │
       ├─────────────────┤
       │ Integration     │ ← Some
       │ Tests           │
       ├─────────────────┤
       │ Unit Tests      │ ← Many
       │ (Fast & Cheap)  │
       └─────────────────┘
```

#### Test Categories
1. **Unit Tests**: Test individual components in isolation
2. **Integration Tests**: Test component interactions
3. **UI Tests**: Test user workflows and interactions
4. **Performance Tests**: Test app performance and resource usage
5. **Security Tests**: Test security implementations

### 4. Performance Optimization

#### Memory Management
```kotlin
// Use lifecycle-aware components
class WeatherViewModel : ViewModel() {
    private val scope = viewModelScope
    
    override fun onCleared() {
        super.onCleared()
        // Clean up resources
    }
}

// Lazy initialization
val expensiveResource by lazy {
    createExpensiveResource()
}
```

#### Network Optimization
```kotlin
// Request coalescing
class WeatherRepository {
    private val pendingRequests = mutableMapOf<String, Deferred<WeatherData>>()
    
    suspend fun getWeather(location: String): WeatherData {
        return pendingRequests.getOrPut(location) {
            scope.async { fetchWeatherFromNetwork(location) }
        }.await()
    }
}
```

#### UI Performance
```kotlin
// Use remember for expensive calculations
@Composable
fun WeatherChart(data: List<WeatherPoint>) {
    val chartData = remember(data) {
        processChartData(data) // Expensive operation
    }
    
    // Chart UI
}

// Lazy loading for large lists
@Composable
fun WeatherList(items: List<Weather>) {
    LazyColumn {
        items(items) { weather ->
            WeatherItem(weather)
        }
    }
}
```

### 5. Security Guidelines

#### Data Protection
1. **Encrypt Sensitive Data**: Use platform keystores
2. **Validate Inputs**: Sanitize all user inputs
3. **Secure Network**: Use HTTPS and certificate pinning
4. **Audit Dependencies**: Regular security scans
5. **Obfuscate Code**: Protect against reverse engineering

#### API Security
```kotlin
// Secure API configuration
val httpClient = HttpClient {
    install(Auth) {
        bearer {
            loadTokens { getSecureApiToken() }
        }
    }
    
    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 15_000
    }
    
    install(CertificatePinningPlugin)
}
```

## Troubleshooting

### Common Issues

#### Build Issues

**Problem**: Gradle sync fails
```
Solution:
1. Check JDK version (must be 17+)
2. Verify Android SDK installation
3. Clean and rebuild:
   ./gradlew clean build
```

**Problem**: iOS build fails
```
Solution:
1. Check Xcode version (14+)
2. Update CocoaPods:
   pod repo update
3. Clean derived data:
   rm -rf ~/Library/Developer/Xcode/DerivedData
```

#### Feature Toggle Issues

**Problem**: Feature not toggling
```
Solution:
1. Check feature key spelling
2. Verify feature toggle initialization
3. Clear feature toggle cache:
   featureStorage.clearAll()
```

**Problem**: A/B test not working
```
Solution:
1. Verify test date range
2. Check user segmentation
3. Validate traffic allocation percentages
```

#### Monitoring Issues

**Problem**: Metrics not appearing
```
Solution:
1. Check telemetry provider initialization
2. Verify network connectivity
3. Check metric collection intervals
4. Review metric naming conventions
```

**Problem**: Dashboard not loading
```
Solution:
1. Verify Grafana configuration
2. Check Prometheus data source
3. Validate dashboard JSON syntax
4. Review metric queries
```

#### Security Issues

**Problem**: Certificate pinning fails
```
Solution:
1. Verify certificate hashes
2. Check certificate expiration
3. Update pinned certificates
4. Test with backup pins
```

**Problem**: API key encryption fails
```
Solution:
1. Check platform keystore access
2. Verify encryption provider setup
3. Test with mock encryption provider
4. Review security permissions
```

### Debug Tools

#### Feature Toggle Admin
```kotlin
// Enable debug mode
val adminController = FeatureToggleAdminController(featureManager)
val report = FeatureToggleAdminUtils.generateFeatureReport(adminController)
println(report)
```

#### Telemetry Inspector
```kotlin
// View collected metrics
val telemetryProvider = TelemetryUtils.getTelemetryProvider()
val metrics = telemetryProvider.getCollectedMetrics()
metrics.forEach { metric ->
    println("${metric.name}: ${metric.value} ${metric.unit}")
}
```

#### Performance Profiler
```kotlin
// Enable performance overlay
if (FeatureToggleUtils.isEnabled(FeatureFlag.PERFORMANCE_OVERLAY)) {
    showPerformanceOverlay()
}
```

### Getting Help

1. **Documentation**: Check the docs/ directory
2. **Issues**: Search existing GitHub issues
3. **Community**: Join the developer community
4. **Support**: Contact the development team

## Migration Guide

### From Previous Versions

#### Version 1.x to 2025.x
1. **Update Gradle**: Upgrade to Gradle 8.5+
2. **Kotlin Version**: Update to Kotlin 2.0+
3. **Dependencies**: Update all dependencies to latest versions
4. **Feature Toggles**: Migrate to new feature toggle system
5. **Security**: Update to new security implementations
6. **Monitoring**: Migrate to OpenTelemetry-based system

#### Breaking Changes
- Feature toggle API changes
- Security provider interfaces updated
- Telemetry system completely rewritten
- UI components now require Compose Multiplatform

### Migration Steps
1. **Backup Project**: Create a backup before starting
2. **Update Build Files**: Gradle and dependency updates
3. **Migrate Features**: Update feature toggle usage
4. **Update Security**: Migrate to new security APIs
5. **Test Thoroughly**: Run all tests and manual testing
6. **Deploy Gradually**: Use feature toggles for gradual rollout

---

## Conclusion

The WeatherKMP 2025 project template provides a comprehensive foundation for building modern, cross-platform applications with industry-leading practices. By following this guide and the established patterns, you can create robust, secure, and maintainable applications that scale across multiple platforms.

For the latest updates and community contributions, visit the project repository and join our developer community.

**Happy coding!** 🌤️

---

*Last updated: ${new Date().toISOString().split('T')[0]}*
*Template version: 2025.1.0*