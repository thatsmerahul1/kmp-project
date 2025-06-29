# WeatherKMP - Product Requirements Document

## 📋 Project Overview

**Project Name**: WeatherKMP  
**Platform**: Kotlin Multiplatform (Android + iOS)  
**Architecture**: MVVM + Clean Architecture + Offline-First  
**Primary Feature**: 7-Day Weather Forecast with Offline Caching  

## 🎯 Objectives

Create a production-ready KMP weather application that demonstrates:
- **Code Sharing**: 70-80% business logic shared between platforms
- **Offline-First Architecture**: Twitter-like data loading strategy
- **Modern UI**: Jetpack Compose (Android) + SwiftUI (iOS)
- **Industry Standards**: Clean Architecture, MVVM, DI, Testing

## 📱 Functional Requirements

### Core Features

#### 1. Weather Dashboard
- **Display**: 7-day weather forecast in a scrollable list
- **Data Points**: 
  - Date and day of week
  - Weather condition (sunny, cloudy, rainy, etc.)
  - High/Low temperatures
  - Humidity percentage
  - Weather icon/illustration
- **Refresh**: Pull-to-refresh functionality
- **Loading States**: Show loading indicators during data fetch

#### 2. Offline-First Data Strategy
- **Primary Flow**: Load cached data immediately → Update UI → Fetch fresh data → Update UI again
- **Cache Duration**: 24 hours (configurable)
- **Cache Policy**: 
  - Show cached data if available (even if expired)
  - Always attempt fresh data fetch in background
  - Update UI only when fresh data differs from cached
- **No Internet**: Gracefully handle offline scenarios with cached data

#### 3. Configuration
- **Cache Duration**: Configurable cache expiry (1-48 hours)
- **Location**: Default location setting (can be hardcoded initially)
- **Units**: Temperature units (Celsius/Fahrenheit)

## 🔧 Technical Requirements

### Architecture & Design Patterns

#### Clean Architecture Layers
```
Presentation Layer (Platform-specific)
├── Android: Jetpack Compose
└── iOS: SwiftUI

Domain Layer (Shared)
├── Use Cases
├── Repository Interfaces
└── Domain Models

Data Layer (Shared)
├── Repository Implementations
├── Network Data Sources
├── Local Data Sources
└── DTOs/Mappers
```

#### MVVM Pattern Implementation
- **Views**: Compose/SwiftUI screens
- **ViewModels**: Shared business logic and state management
- **Models**: Shared data models and states

### Technology Stack

#### **Shared Module Dependencies**
```kotlin
// Core Kotlin
kotlin-stdlib: 1.9.21
kotlinx-coroutines-core: 1.7.3
kotlinx-datetime: 0.5.0
kotlinx-serialization-json: 1.6.2

// Networking
ktor-client-core: 2.3.7
ktor-client-content-negotiation: 2.3.7
ktor-serialization-kotlinx-json: 2.3.7
ktor-client-logging: 2.3.7

// Database
sqldelight-runtime: 2.0.1
sqldelight-coroutines-extensions: 2.0.1

// Dependency Injection
koin-core: 3.5.0

// Testing
kotlin-test: 1.9.21
kotlinx-coroutines-test: 1.7.3
turbine: 1.0.0
```

#### **Android-Specific Dependencies**
```kotlin
// Compose
compose-bom: 2024.02.00
compose-ui: BOM
compose-ui-tooling-preview: BOM
compose-material3: BOM
compose-activity: 1.8.2
compose-navigation: 2.7.6

// Android Extensions
koin-android: 3.5.0
koin-androidx-compose: 3.5.0
sqldelight-android-driver: 2.0.1
ktor-client-okhttp: 2.3.7

// Lifecycle
lifecycle-viewmodel-compose: 2.7.0
```

#### **iOS-Specific Dependencies**
```kotlin
// SQLDelight iOS
sqldelight-native-driver: 2.0.1

// Ktor iOS
ktor-client-darwin: 2.3.7
```

### Project Structure

```
WeatherKMP/
├── shared/
│   ├── commonMain/kotlin/
│   │   ├── data/
│   │   │   ├── local/
│   │   │   │   ├── database/
│   │   │   │   │   ├── WeatherDatabase.sq
│   │   │   │   │   └── WeatherDao.kt
│   │   │   │   └── preferences/
│   │   │   │       └── AppPreferences.kt
│   │   │   ├── remote/
│   │   │   │   ├── api/
│   │   │   │   │   └── WeatherApi.kt
│   │   │   │   └── dto/
│   │   │   │       ├── WeatherResponseDto.kt
│   │   │   │       └── WeatherItemDto.kt
│   │   │   ├── repository/
│   │   │   │   └── WeatherRepositoryImpl.kt
│   │   │   └── mapper/
│   │   │       └── WeatherMapper.kt
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── Weather.kt
│   │   │   │   ├── WeatherCondition.kt
│   │   │   │   └── CacheConfig.kt
│   │   │   ├── repository/
│   │   │   │   └── WeatherRepository.kt
│   │   │   └── usecase/
│   │   │       ├── GetWeatherForecastUseCase.kt
│   │   │       ├── RefreshWeatherUseCase.kt
│   │   │       └── GetCacheConfigUseCase.kt
│   │   ├── presentation/
│   │   │   ├── viewmodel/
│   │   │   │   └── WeatherViewModel.kt
│   │   │   └── state/
│   │   │       ├── WeatherUiState.kt
│   │   │       └── WeatherUiEvent.kt
│   │   └── di/
│   │       ├── DatabaseModule.kt
│   │       ├── NetworkModule.kt
│   │       ├── RepositoryModule.kt
│   │       └── UseCaseModule.kt
│   ├── commonTest/
│   ├── androidMain/
│   └── iosMain/
├── androidApp/
│   └── src/main/
│       ├── kotlin/
│       │   ├── ui/
│       │   │   ├── screen/
│       │   │   │   └── WeatherScreen.kt
│       │   │   ├── component/
│       │   │   │   ├── WeatherItem.kt
│       │   │   │   └── LoadingIndicator.kt
│       │   │   ├── theme/
│       │   │   │   ├── Color.kt
│       │   │   │   ├── Theme.kt
│       │   │   │   └── Type.kt
│       │   │   └── navigation/
│       │   │       └── AppNavigation.kt
│       │   ├── MainActivity.kt
│       │   └── WeatherApplication.kt
│       └── res/
├── iosApp/
│   └── iosApp/
│       ├── ContentView.swift
│       ├── Views/
│       │   ├── WeatherListView.swift
│       │   └── WeatherRowView.swift
│       └── WeatherApp.swift
└── gradle/
```

## 🏗️ Data Architecture

### API Integration

#### Weather API Requirements
- **Recommended**: OpenWeatherMap API (free tier available)
- **Endpoint**: `/forecast` for 7-day forecast
- **Required Data**:
  ```json
  {
    "list": [
      {
        "dt": 1234567890,
        "main": {
          "temp_max": 25.5,
          "temp_min": 18.2,
          "humidity": 65
        },
        "weather": [
          {
            "main": "Clear",
            "description": "clear sky",
            "icon": "01d"
          }
        ]
      }
    ]
  }
  ```

#### Alternative APIs
- **WeatherAPI.com**: Free tier with 7-day forecast
- **AccuWeather**: Comprehensive weather data
- **Tomorrow.io**: Modern weather API

### Database Schema

```sql
-- WeatherDatabase.sq
CREATE TABLE WeatherForecast (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    date INTEGER NOT NULL,
    condition TEXT NOT NULL,
    temperature_high REAL NOT NULL,
    temperature_low REAL NOT NULL,
    humidity INTEGER NOT NULL,
    icon TEXT NOT NULL,
    description TEXT NOT NULL,
    cached_at INTEGER NOT NULL,
    UNIQUE(date)
);

-- Queries
selectAll:
SELECT * FROM WeatherForecast ORDER BY date ASC;

selectByDateRange:
SELECT * FROM WeatherForecast 
WHERE date BETWEEN ? AND ? 
ORDER BY date ASC;

insertOrReplace:
INSERT OR REPLACE INTO WeatherForecast 
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);

deleteOlderThan:
DELETE FROM WeatherForecast WHERE cached_at < ?;

isCacheValid:
SELECT COUNT(*) > 0 FROM WeatherForecast 
WHERE cached_at > ? LIMIT 1;
```

### Data Models

#### Domain Models
```kotlin
// Weather.kt
data class Weather(
    val date: LocalDate,
    val condition: WeatherCondition,
    val temperatureHigh: Double,
    val temperatureLow: Double,
    val humidity: Int,
    val icon: String,
    val description: String
)

// WeatherCondition.kt
enum class WeatherCondition {
    CLEAR, CLOUDS, RAIN, SNOW, THUNDERSTORM, MIST, UNKNOWN
}

// CacheConfig.kt
data class CacheConfig(
    val cacheExpiryHours: Int = 24,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS
)

enum class TemperatureUnit { CELSIUS, FAHRENHEIT }
```

#### UI State Models
```kotlin
// WeatherUiState.kt
data class WeatherUiState(
    val weatherList: List<Weather> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val lastUpdated: Instant? = null,
    val isOffline: Boolean = false
)

// WeatherUiEvent.kt
sealed class WeatherUiEvent {
    object LoadWeather : WeatherUiEvent()
    object RefreshWeather : WeatherUiEvent()
    object RetryLoad : WeatherUiEvent()
    object ClearError : WeatherUiEvent()
}
```

## 🔄 Offline-First Implementation

### Data Flow Strategy

#### 1. Initial Load
```
User Opens App
    ↓
Check Local Cache
    ↓
If Cache Exists → Display Cached Data Immediately
    ↓
Fetch Fresh Data from API (Background)
    ↓
If Fresh Data ≠ Cached Data → Update UI
    ↓
Save Fresh Data to Cache
```

#### 2. Pull-to-Refresh
```
User Pulls to Refresh
    ↓
Show Refresh Indicator
    ↓
Fetch Fresh Data from API
    ↓
Update UI with Fresh Data
    ↓
Save to Cache
    ↓
Hide Refresh Indicator
```

#### 3. Cache Management
- **Cache Validation**: Check timestamp before displaying
- **Background Sync**: Always attempt API call even with valid cache
- **Cache Cleanup**: Remove entries older than configured duration
- **Graceful Degradation**: Show cached data with "offline" indicator when API fails

### Repository Implementation Strategy

```kotlin
class WeatherRepositoryImpl : WeatherRepository {
    
    override fun getWeatherForecast(): Flow<Result<List<Weather>>> = flow {
        // 1. Emit cached data immediately
        val cachedData = localDataSource.getWeatherForecast()
        if (cachedData.isNotEmpty()) {
            emit(Result.success(cachedData))
        }
        
        // 2. Fetch fresh data
        try {
            val freshData = remoteDataSource.getWeatherForecast()
            // 3. Save to cache
            localDataSource.saveWeatherForecast(freshData)
            // 4. Emit fresh data
            emit(Result.success(freshData))
        } catch (e: Exception) {
            // 5. If fresh fetch fails and no cache, emit error
            if (cachedData.isEmpty()) {
                emit(Result.failure(e))
            }
        }
    }
}
```

## 🎨 UI/UX Requirements

### Android UI (Jetpack Compose)

#### Weather List Screen
- **Layout**: LazyColumn with weather cards
- **Pull-to-Refresh**: SwipeRefresh composable
- **Loading States**: Shimmer effect or skeleton loading
- **Error States**: Retry button with error message
- **Empty States**: Illustration with friendly message

#### Weather Item Design
- **Card Design**: Material 3 cards with rounded corners
- **Layout**: 
  - Date and day on left
  - Weather icon in center
  - Temperature range on right
  - Humidity below temperature
- **Typography**: Material 3 typography scale
- **Colors**: Dynamic color theming

### iOS UI (SwiftUI)

#### Weather List View
- **Layout**: List with custom rows
- **Pull-to-Refresh**: Native refreshable modifier
- **Loading States**: ProgressView with custom styling
- **Navigation**: SwiftUI navigation with proper titles

#### Weather Row Design
- **Layout**: HStack with consistent spacing
- **Design System**: iOS Human Interface Guidelines
- **Colors**: Adaptive colors for light/dark mode
- **Typography**: iOS system fonts

### Shared Design Principles
- **Consistency**: Similar visual hierarchy across platforms
- **Accessibility**: Proper labels and semantic markup
- **Responsive**: Adapts to different screen sizes
- **Performance**: Smooth scrolling and animations

## 🧪 Testing Strategy

### Test Coverage Requirements

#### Unit Tests (Shared)
- **ViewModels**: State management and business logic
- **Use Cases**: Business rules and data transformation
- **Repository**: Data fetching and caching logic
- **Mappers**: Data transformation accuracy

#### Integration Tests
- **Database**: SQLDelight operations
- **API**: Network requests and responses
- **Cache Logic**: Offline-first flow validation

#### Sample Test Structure
```kotlin
// WeatherViewModelTest.kt
class WeatherViewModelTest {
    @Test
    fun `when loading weather, should emit loading state then success`() = runTest {
        // Given
        val mockWeatherList = listOf(/* mock data */)
        coEvery { getWeatherUseCase() } returns flowOf(Result.success(mockWeatherList))
        
        // When
        val viewModel = WeatherViewModel(getWeatherUseCase)
        
        // Then
        viewModel.uiState.test {
            assertEquals(WeatherUiState(isLoading = true), awaitItem())
            assertEquals(WeatherUiState(weatherList = mockWeatherList), awaitItem())
        }
    }
}
```

### Testing Tools
- **kotlin.test**: Common testing framework
- **Turbine**: Flow testing
- **MockK**: Mocking framework
- **kotlinx.coroutines.test**: Coroutine testing utilities

## ⚙️ Configuration & Setup

### Environment Variables
```properties
# API Configuration
WEATHER_API_KEY=your_api_key_here
WEATHER_BASE_URL=https://api.openweathermap.org/data/2.5/
DEFAULT_LOCATION=London,UK

# Cache Settings
DEFAULT_CACHE_HOURS=24
CACHE_CLEANUP_INTERVAL_HOURS=6
```

### Build Configuration

#### Shared Module
```kotlin
// shared/build.gradle.kts
kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    sourceSets {
        commonMain.dependencies {
            // Dependencies listed above
        }
        commonTest.dependencies {
            // Test dependencies
        }
    }
}

sqldelight {
    databases {
        create("WeatherDatabase") {
            packageName.set("com.weather.database")
        }
    }
}
```

## 🚀 Implementation Phases

### Phase 1: Core Setup (Week 1)
- [ ] Project structure creation
- [ ] Dependency injection setup
- [ ] Basic models and database schema
- [ ] Network layer foundation

### Phase 2: Data Layer (Week 2)
- [ ] API integration
- [ ] Database operations
- [ ] Repository implementation
- [ ] Cache logic implementation

### Phase 3: Business Logic (Week 3)
- [ ] Use cases implementation
- [ ] ViewModel creation
- [ ] Offline-first flow implementation
- [ ] State management

### Phase 4: UI Implementation (Week 4)
- [ ] Android Compose UI
- [ ] iOS SwiftUI implementation
- [ ] Navigation setup
- [ ] Loading and error states

### Phase 5: Testing & Polish (Week 5)
- [ ] Unit tests
- [ ] Integration tests
- [ ] UI testing
- [ ] Performance optimization

## 📊 Success Metrics

### Technical Metrics
- **Code Sharing**: 70%+ shared code between platforms
- **Test Coverage**: 80%+ unit test coverage
- **Performance**: App startup < 2 seconds
- **Offline Support**: 100% functionality offline with cached data

### User Experience Metrics
- **Load Time**: Initial data display < 1 second (cached)
- **Refresh Time**: Pull-to-refresh < 3 seconds
- **Error Handling**: Graceful degradation in all scenarios
- **Accessibility**: 100% compliance with platform guidelines

## 🔧 Development Guidelines

### Code Standards
- **Kotlin Coding Conventions**: Follow official Kotlin style guide
- **Clean Architecture**: Strict layer separation
- **SOLID Principles**: Apply throughout the codebase
- **Documentation**: KDoc for all public APIs

### Git Workflow
- **Branch Strategy**: Feature branches with PR reviews
- **Commit Messages**: Conventional commits format
- **CI/CD**: Automated testing and builds

### Error Handling
- **Exceptions**: Use sealed classes for domain errors
- **Network Errors**: Proper retry mechanisms
- **UI Errors**: User-friendly error messages
- **Logging**: Comprehensive logging for debugging

This PRD provides a complete blueprint for implementing a production-ready KMP weather application with offline-first architecture, modern UI frameworks, and industry-standard practices.