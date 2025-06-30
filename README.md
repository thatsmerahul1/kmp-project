# 🌤️ WeatherKMP - Kotlin Multiplatform Template

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.21-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-API%2024+-green.svg?style=flat&logo=android)](https://developer.android.com)
[![iOS](https://img.shields.io/badge/iOS-14.0+-lightgrey.svg?style=flat&logo=apple)](https://developer.apple.com/ios/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat)](LICENSE)

A **production-ready Kotlin Multiplatform template** featuring a 7-day weather forecast app with offline-first architecture, modern UI frameworks, and industry-standard practices.

## 🎯 Features

- **🏗️ Clean Architecture** - Separation of concerns with Data, Domain, and Presentation layers
- **📱 Modern UI** - Jetpack Compose (Android) with Material 3 design
- **💾 Offline-First** - Smart caching with SQLDelight database
- **🌐 Network Layer** - Ktor client with OpenWeatherMap API integration
- **🔧 Dependency Injection** - Koin for clean dependency management
- **🧪 Testing Ready** - Unit tests with MockK and Turbine
- **📊 State Management** - Reactive flows with Kotlin Coroutines

## 🏛️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                     Presentation Layer                          │
│  ┌─────────────────┐              ┌─────────────────┐           │
│  │   Android App   │              │    iOS App     │           │
│  │ Jetpack Compose │              │    SwiftUI     │           │
│  │   Material 3    │              │      (TBD)     │           │
│  └─────────────────┘              └─────────────────┘           │
└─────────────────────────────────────────────────────────────────┘
                             │
┌─────────────────────────────────────────────────────────────────┐
│                      Domain Layer (Shared)                      │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐    │
│  │   Use Cases     │ │   Repository    │ │  Domain Models  │    │
│  │                 │ │   Interfaces    │ │                 │    │
│  └─────────────────┘ └─────────────────┘ └─────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                             │
┌─────────────────────────────────────────────────────────────────┐
│                      Data Layer (Shared)                        │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐    │
│  │   Repository    │ │  Local Storage  │ │ Remote Data     │    │
│  │ Implementation  │ │   SQLDelight    │ │  Ktor Client    │    │
│  └─────────────────┘ └─────────────────┘ └─────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

## 🚀 Quick Start

### Prerequisites

- **Android Studio** Hedgehog | 2023.1.1 or newer
- **JDK 17** or higher
- **Android SDK** API 34+
- **Xcode 15** (for iOS development)
- **OpenWeatherMap API Key** (free at [openweathermap.org](https://openweathermap.org/api))

### 1. Clone the Template

```bash
git clone https://github.com/your-repo/weather-kmp-template.git
cd weather-kmp-template
```

### 2. Configure API Key

Add your OpenWeatherMap API key to `gradle.properties`:

```properties
WEATHER_API_KEY=your_api_key_here
WEATHER_BASE_URL=https://api.openweathermap.org/data/2.5/
DEFAULT_LOCATION=Bengaluru,India
```

### 3. Build and Run

#### Android
```bash
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:installDebug
```

#### Shared Module Tests
```bash
./gradlew :shared:allTests
```

### 4. Open in IDE

- **Android Studio**: Open the root project directory
- **Xcode**: Open `iosApp/iosApp.xcodeproj` (iOS implementation pending)

## 📱 Screens & Features

### Android App

- **🏠 Weather Dashboard** - 7-day forecast with pull-to-refresh
- **📊 Loading States** - Skeleton loading and progress indicators  
- **❌ Error Handling** - User-friendly error messages with retry
- **🔄 Offline Support** - Cached data when network unavailable
- **🎨 Material 3** - Modern design with dynamic theming

## 🛠️ Tech Stack

### Shared Dependencies
```kotlin
// Core
kotlin-stdlib: 1.9.21
kotlinx-coroutines-core: 1.7.3
kotlinx-serialization-json: 1.6.2
kotlinx-datetime: 0.5.0

// Networking
ktor-client-core: 2.3.7
ktor-client-content-negotiation: 2.3.7
ktor-serialization-kotlinx-json: 2.3.7

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

### Android Dependencies
```kotlin
// Compose
compose-bom: 2024.02.00
compose-material3: Latest
androidx-activity-compose: 1.8.2
androidx-lifecycle-viewmodel-compose: 2.7.0

// Platform-specific
koin-android: 3.5.0
sqldelight-android-driver: 2.0.1
ktor-client-okhttp: 2.3.7
```

## 🏗️ Project Structure

```
WeatherKMP/
├── shared/                          # Shared KMP module
│   ├── commonMain/kotlin/
│   │   ├── data/                    # Data layer
│   │   │   ├── local/              # Local data sources
│   │   │   │   ├── database/       # SQLDelight database
│   │   │   │   └── preferences/    # App preferences
│   │   │   ├── remote/             # Remote data sources
│   │   │   │   ├── api/            # API interfaces
│   │   │   │   └── dto/            # Data transfer objects
│   │   │   ├── repository/         # Repository implementations
│   │   │   └── mapper/             # Data mappers
│   │   ├── domain/                 # Domain layer
│   │   │   ├── model/              # Domain models
│   │   │   ├── repository/         # Repository interfaces
│   │   │   └── usecase/            # Use cases
│   │   ├── presentation/           # Presentation layer
│   │   │   ├── viewmodel/          # Shared ViewModels
│   │   │   └── state/              # UI state models
│   │   └── di/                     # Dependency injection
│   ├── commonTest/                 # Shared tests
│   ├── androidMain/                # Android-specific code
│   └── iosMain/                    # iOS-specific code
├── androidApp/                     # Android application
│   └── src/main/kotlin/
│       ├── ui/                     # Compose UI
│       │   ├── screen/             # Screen composables
│       │   ├── component/          # Reusable components
│       │   └── theme/              # Material 3 theme
│       ├── MainActivity.kt
│       └── WeatherApplication.kt
├── iosApp/                         # iOS application (TBD)
└── TASK_MASTER.md                  # Project progress tracker
```

## 🧪 Testing

The template includes a comprehensive testing foundation:

### Running Tests

```bash
# Run all shared module tests
./gradlew :shared:allTests

# Run specific platform tests
./gradlew :shared:testDebugUnitTest           # Android unit tests
./gradlew :shared:iosX64Test                  # iOS X64 tests

# Run with coverage
./gradlew :shared:allTests --info
```

### Test Categories

- **Unit Tests** - Domain models, mappers, use cases
- **Integration Tests** - Repository and database operations
- **ViewModel Tests** - State management and business logic

## 🎯 Usage as Template

### 1. Customize for Your Project

1. **Rename packages** from `com.weather` to your domain
2. **Update API endpoints** in `NetworkModule`
3. **Modify domain models** to fit your use case
4. **Replace weather logic** with your business logic
5. **Update UI themes** and branding

### 2. Key Extension Points

- **Domain Models** - Add your business entities
- **Use Cases** - Implement your business logic
- **API Integration** - Connect to your backend services
- **UI Components** - Build your app-specific screens
- **Database Schema** - Design your local storage

### 3. Production Checklist

- [ ] Configure ProGuard/R8 rules
- [ ] Set up CI/CD pipeline
- [ ] Add crash reporting (Firebase Crashlytics)
- [ ] Implement analytics
- [ ] Configure app signing
- [ ] Add security measures

## 📊 Code Sharing Metrics

| Layer | Shared Code | Platform-Specific |
|-------|-------------|-------------------|
| **Domain Logic** | 100% | 0% |
| **Data Layer** | 95% | 5% (drivers) |
| **Business Logic** | 100% | 0% |
| **UI Layer** | 15% (ViewModel) | 85% (Views) |
| **Overall** | ~85% | ~15% |

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **OpenWeatherMap** for providing free weather API
- **JetBrains** for Kotlin Multiplatform
- **Square** for SQLDelight and other great libraries
- **Google** for Jetpack Compose and Material Design

## 📚 Learn More

- [Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [SQLDelight Documentation](https://cashapp.github.io/sqldelight/)
- [Ktor Documentation](https://ktor.io/docs/)

---

**Happy coding! 🚀**

Made with ❤️ using Kotlin Multiplatform