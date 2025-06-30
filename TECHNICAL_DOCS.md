# 🔧 WeatherKMP - Technical Documentation

> **Complete technical specification for developers building production-ready Kotlin Multiplatform applications with 85% code sharing and atomic design systems.**

## 📋 Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Code Sharing Strategy](#code-sharing-strategy)
3. [Atomic Design Implementation](#atomic-design-implementation)
4. [Development Standards](#development-standards)
5. [Platform-Specific Guidelines](#platform-specific-guidelines)
6. [Build Configuration](#build-configuration)
7. [Testing Framework](#testing-framework)
8. [Performance Optimization](#performance-optimization)
9. [Security Implementation](#security-implementation)
10. [Deployment Pipeline](#deployment-pipeline)

---

## 🏛️ Architecture Overview

### **Clean Architecture Principles**

The project implements **Uncle Bob's Clean Architecture** with **Domain-Driven Design (DDD)** principles:

```kotlin
// Domain Layer (100% Shared)
shared/src/commonMain/kotlin/com/weather/domain/
├── model/           # Pure business entities
├── repository/      # Repository interfaces (abstractions)
└── usecase/         # Business logic operations

// Data Layer (95% Shared)  
shared/src/commonMain/kotlin/com/weather/data/
├── local/          # Local storage (SQLDelight)
├── remote/         # API integration (Ktor)
├── repository/     # Repository implementations
└── mapper/         # Data transformations

// Presentation Layer (70% Shared)
shared/src/commonMain/kotlin/com/weather/presentation/
├── viewmodel/      # Shared UI logic
└── state/          # UI state management

// Platform-Specific (15%)
androidMain/        # Android-specific implementations
iosMain/            # iOS-specific implementations
```

### **Dependency Flow**

```
┌─────────────────┐    ┌─────────────────┐
│   Presentation  │───▶│     Domain      │
│   (ViewModels)  │    │   (Use Cases)   │
└─────────────────┘    └─────────────────┘
         │                       ▲
         ▼                       │
┌─────────────────┐              │
│      Data       │──────────────┘
│  (Repository)   │
└─────────────────┘
```

**Key Principles:**
- **Dependency Inversion**: High-level modules don't depend on low-level modules
- **Single Responsibility**: Each class has one reason to change
- **Interface Segregation**: Small, focused interfaces
- **Open/Closed**: Open for extension, closed for modification

---

## 🔄 Code Sharing Strategy

### **Layer-by-Layer Analysis**

| **Layer** | **Shared %** | **What's Shared** | **Platform-Specific** |
|-----------|--------------|-------------------|----------------------|
| **Domain** | 100% | Business logic, entities, interfaces | None |
| **Data** | 95% | Repository, networking, caching | Database drivers only |
| **Presentation** | 70% | ViewModels, state management | Platform UI bridges |
| **UI** | 15% | Design tokens (partial) | Complete UI framework |

### **Shared Components**

#### **1. Domain Models (100% Shared)**
```kotlin
// shared/src/commonMain/kotlin/com/weather/domain/model/Weather.kt
@Serializable
data class Weather(
    val date: LocalDate,
    val condition: WeatherCondition,
    val temperatureHigh: Double,
    val temperatureLow: Double,
    val humidity: Int,
    val icon: String,
    val description: String
) {
    val temperatureRange: String get() = "${temperatureLow.toInt()}° - ${temperatureHigh.toInt()}°"
    val isHot: Boolean get() = temperatureHigh > 30.0
    val isCold: Boolean get() = temperatureHigh < 10.0
}

enum class WeatherCondition {
    CLEAR, CLOUDS, RAIN, DRIZZLE, THUNDERSTORM, SNOW, MIST, FOG, HAZE;
    
    companion object {
        fun fromWeatherCode(code: Int): WeatherCondition = when (code) {
            0 -> CLEAR
            1, 2, 3 -> CLOUDS
            45, 48 -> FOG
            // ... comprehensive mapping
            else -> CLEAR
        }
    }
}
```

#### **2. Repository Implementation (95% Shared)**
```kotlin
// shared/src/commonMain/kotlin/com/weather/data/repository/WeatherRepositoryImpl.kt
class WeatherRepositoryImpl(
    private val localDataSource: LocalWeatherDataSource,
    private val remoteDataSource: RemoteWeatherDataSource,
    private val cacheConfig: CacheConfig
) : WeatherRepository {
    
    override fun getWeatherForecast(location: String): Flow<Result<List<Weather>>> = flow {
        // 1. Emit cached data immediately (offline-first)
        localDataSource.getWeatherForecast()
            .firstOrNull()
            ?.takeIf { it.isNotEmpty() }
            ?.let { emit(Result.success(it)) }
        
        try {
            // 2. Fetch fresh data from API
            val freshData = remoteDataSource.getWeatherForecast(location)
            
            // 3. Cache the fresh data
            localDataSource.saveWeatherForecast(freshData)
            
            // 4. Emit fresh data
            emit(Result.success(freshData))
            
        } catch (e: Exception) {
            // 5. If network fails, rely on cached data
            val cachedData = localDataSource.getWeatherForecast().first()
            if (cachedData.isEmpty()) {
                emit(Result.failure(e))
            }
            // Keep the cached data showing (already emitted above)
        }
    }
}
```

#### **3. Shared ViewModels (70% Shared)**
```kotlin
// shared/src/commonMain/kotlin/com/weather/presentation/viewmodel/WeatherViewModel.kt
class WeatherViewModel(
    private val getWeatherForecastUseCase: GetWeatherForecastUseCase,
    private val refreshWeatherUseCase: RefreshWeatherUseCase
) {
    private val _uiState = MutableStateFlow(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()
    
    fun loadWeather(location: String = "London") {
        viewModelScope.launch {
            getWeatherForecastUseCase(location)
                .collect { result ->
                    _uiState.value = when {
                        result.isSuccess -> WeatherUiState.Success(result.getOrThrow())
                        result.isFailure -> WeatherUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                    }
                }
        }
    }
    
    fun refreshWeather() {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Refreshing(_uiState.value)
            refreshWeatherUseCase()
        }
    }
}
```

### **Platform-Specific Implementations**

#### **Android Platform Bridge**
```kotlin
// shared/src/androidMain/kotlin/com/weather/presentation/WeatherViewModelBridge.kt
class WeatherViewModelBridge(
    private val weatherViewModel: WeatherViewModel
) : ViewModel() {
    
    val uiState = weatherViewModel.uiState.asLiveData()
    
    fun loadWeather(location: String) = weatherViewModel.loadWeather(location)
    fun refreshWeather() = weatherViewModel.refreshWeather()
    
    override fun onCleared() {
        super.onCleared()
        weatherViewModel.onCleared()
    }
}
```

#### **iOS Platform Bridge**
```kotlin
// shared/src/iosMain/kotlin/com/weather/presentation/IOSWeatherViewModel.kt
class IOSWeatherViewModel(
    private val weatherViewModel: WeatherViewModel
) {
    private var stateJob: Job? = null
    
    @Throws(CancellationException::class)
    fun observeUiState(onChange: (WeatherUiState) -> Unit) {
        stateJob = weatherViewModel.uiState
            .onEach { onChange(it) }
            .launchIn(GlobalScope)
    }
    
    fun loadWeather(location: String) = weatherViewModel.loadWeather(location)
    fun refreshWeather() = weatherViewModel.refreshWeather()
    
    fun dispose() {
        stateJob?.cancel()
    }
}
```

---

## 🎨 Atomic Design Implementation

### **Design System Architecture**

Following **Brad Frost's Atomic Design methodology** with platform-specific implementations:

```
Atomic Design Hierarchy:
🟢 Atoms → 🔵 Molecules → 🟡 Organisms → 📄 Templates → 📱 Pages
```

### **Android Implementation (Complete)**

#### **1. Design Tokens**
```kotlin
// androidApp/src/main/kotlin/com/weather/android/ui/theme/AtomicColors.kt
object AtomicColors {
    // Primary palette
    val Primary = Color(0xFF1976D2)
    val PrimaryVariant = Color(0xFF1565C0)
    val Secondary = Color(0xFF03DAC6)
    
    // Weather-specific colors
    val Sunny = Color(0xFFFFD54F)
    val Cloudy = Color(0xFF90A4AE)
    val Rainy = Color(0xFF5D4E75)
    val Snowy = Color(0xFFE3F2FD)
    
    // Semantic colors
    val Success = Color(0xFF4CAF50)
    val Warning = Color(0xFFFF9800)
    val Error = Color(0xFFF44336)
    
    // Weather gradients
    val SunnyGradient = listOf(Sunny, Color(0xFFFF8F00))
    val CloudyGradient = listOf(Cloudy, Color(0xFF607D8B))
    val RainyGradient = listOf(Rainy, Color(0xFF9C88FF))
}

// androidApp/src/main/kotlin/com/weather/android/ui/theme/AtomicTypography.kt
object AtomicTypography {
    private val defaultTypography = Typography()
    
    val DisplayLarge = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    )
    
    val TemperatureLarge = TextStyle(
        fontSize = 48.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 56.sp,
        letterSpacing = (-0.5).sp
    )
    
    val WeatherDescription = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )
}

// androidApp/src/main/kotlin/com/weather/android/ui/theme/AtomicSpacing.kt
object AtomicSpacing {
    val XS = 4.dp
    val SM = 8.dp
    val MD = 16.dp
    val LG = 24.dp
    val XL = 32.dp
    val XXL = 48.dp
    val XXXL = 64.dp
    
    // Semantic spacing
    val WeatherCardPadding = MD
    val TemperatureSpacing = SM
    val ComponentGap = XS
    val ScreenPadding = MD
}
```

#### **2. Atomic Components**

**Atoms - Basic Building Blocks:**
```kotlin
// androidApp/src/main/kotlin/com/weather/android/ui/atoms/button/PrimaryButton.kt
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = AtomicColors.Primary,
            contentColor = Color.White
        ),
        shape = AtomicShapes.ButtonShape
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = AtomicTypography.LabelLarge
            )
        }
    }
}

// androidApp/src/main/kotlin/com/weather/android/ui/atoms/text/HeadlineText.kt
@Composable
fun HeadlineText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = AtomicColors.OnSurface,
    textAlign: TextAlign = TextAlign.Start
) {
    Text(
        text = text,
        style = AtomicTypography.HeadlineMedium,
        color = color,
        textAlign = textAlign,
        modifier = modifier
    )
}

// androidApp/src/main/kotlin/com/weather/android/ui/atoms/icon/WeatherIcon.kt
@Composable
fun WeatherIcon(
    condition: WeatherCondition,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = AtomicColors.getWeatherColor(condition)
) {
    val icon = when (condition) {
        WeatherCondition.CLEAR -> Icons.Default.WbSunny
        WeatherCondition.CLOUDS -> Icons.Default.Cloud
        WeatherCondition.RAIN -> Icons.Default.Umbrella
        WeatherCondition.SNOW -> Icons.Default.AcUnit
        else -> Icons.Default.WbCloudy
    }
    
    Icon(
        imageVector = icon,
        contentDescription = condition.name,
        modifier = modifier.size(size),
        tint = tint
    )
}
```

**Molecules - Component Combinations:**
```kotlin
// androidApp/src/main/kotlin/com/weather/android/ui/molecules/TemperatureDisplay.kt
@Composable
fun TemperatureDisplay(
    temperature: Double,
    unit: String = "°C",
    modifier: Modifier = Modifier,
    style: TemperatureStyle = TemperatureStyle.Large
) {
    val textStyle = when (style) {
        TemperatureStyle.Large -> AtomicTypography.TemperatureLarge
        TemperatureStyle.Medium -> AtomicTypography.TemperatureMedium
        TemperatureStyle.Small -> AtomicTypography.TemperatureSmall
    }
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = temperature.toInt().toString(),
            style = textStyle,
            color = AtomicColors.OnSurface
        )
        Text(
            text = unit,
            style = textStyle.copy(fontSize = textStyle.fontSize * 0.6f),
            color = AtomicColors.OnSurfaceVariant,
            modifier = Modifier.offset(y = 4.dp)
        )
    }
}

// androidApp/src/main/kotlin/com/weather/android/ui/molecules/WeatherSummary.kt
@Composable
fun WeatherSummary(
    weather: Weather,
    modifier: Modifier = Modifier,
    showDate: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AtomicSpacing.SM)
    ) {
        WeatherIcon(
            condition = weather.condition,
            size = 32.dp
        )
        
        Column {
            if (showDate) {
                CaptionText(
                    text = weather.date.dayOfWeek.name,
                    color = AtomicColors.OnSurfaceVariant
                )
            }
            BodyText(text = weather.description)
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        TemperatureDisplay(
            temperature = weather.temperatureHigh,
            style = TemperatureStyle.Medium
        )
    }
}
```

**Organisms - Complex UI Sections:**
```kotlin
// androidApp/src/main/kotlin/com/weather/android/ui/organisms/WeatherCard.kt
@Composable
fun WeatherCard(
    weather: Weather,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    expanded: Boolean = false
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable { onClick() }
                else Modifier
            ),
        shape = AtomicShapes.CardShape,
        colors = CardDefaults.cardColors(
            containerColor = AtomicColors.Surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(AtomicSpacing.WeatherCardPadding)
        ) {
            WeatherSummary(
                weather = weather,
                showDate = true
            )
            
            if (expanded) {
                Spacer(modifier = Modifier.height(AtomicSpacing.MD))
                
                WeatherDetails(
                    humidity = weather.humidity,
                    temperatureRange = "${weather.temperatureLow.toInt()}° - ${weather.temperatureHigh.toInt()}°"
                )
            }
        }
    }
}
```

**Templates - Page Layouts:**
```kotlin
// androidApp/src/main/kotlin/com/weather/android/ui/templates/DashboardTemplate.kt
@Composable
fun DashboardTemplate(
    title: String,
    content: @Composable () -> Unit,
    floatingActionButton: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { HeadlineText(text = title) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AtomicColors.Surface,
                    titleContentColor = AtomicColors.OnSurface
                )
            )
        },
        floatingActionButton = floatingActionButton ?: {},
        containerColor = AtomicColors.Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = AtomicSpacing.ScreenPadding)
        ) {
            content()
        }
    }
}
```

**Pages - Complete Screens:**
```kotlin
// androidApp/src/main/kotlin/com/weather/android/ui/pages/WeatherDashboard.kt
@Composable
fun WeatherDashboard(
    viewModel: WeatherViewModelBridge = koinViewModel()
) {
    val uiState by viewModel.uiState.observeAsState(WeatherUiState.Loading)
    
    DashboardTemplate(
        title = "Weather Forecast",
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.refreshWeather() }
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }
    ) {
        when (uiState) {
            is WeatherUiState.Loading -> {
                WeatherLoadingState()
            }
            
            is WeatherUiState.Success -> {
                WeatherList(
                    weatherList = uiState.data,
                    onWeatherClick = { weather ->
                        // Navigate to detail screen
                    }
                )
            }
            
            is WeatherUiState.Error -> {
                WeatherErrorState(
                    message = uiState.message,
                    onRetry = { viewModel.loadWeather() }
                )
            }
        }
    }
}
```

### **iOS Implementation (Needs Development)**

Currently, iOS implementation lacks atomic design structure. Here's the recommended implementation:

```swift
// iosApp/iosApp/DesignSystem/Colors.swift
import SwiftUI

extension Color {
    // Primary palette (matching Android)
    static let atomicPrimary = Color(red: 0.098, green: 0.463, blue: 0.824)
    static let atomicSecondary = Color(red: 0.012, green: 0.854, blue: 0.776)
    
    // Weather-specific colors  
    static let atomicSunny = Color(red: 1.0, green: 0.835, blue: 0.310)
    static let atomicCloudy = Color(red: 0.565, green: 0.643, blue: 0.682)
    static let atomicRainy = Color(red: 0.365, green: 0.306, blue: 0.459)
    
    static func atomicWeatherColor(for condition: WeatherCondition) -> Color {
        switch condition {
        case .clear: return .atomicSunny
        case .clouds: return .atomicCloudy
        case .rain: return .atomicRainy
        default: return .atomicPrimary
        }
    }
}

// iosApp/iosApp/DesignSystem/Typography.swift
import SwiftUI

extension Font {
    static let atomicDisplayLarge = Font.system(size: 32, weight: .bold)
    static let atomicTemperatureLarge = Font.system(size: 48, weight: .bold)
    static let atomicHeadline = Font.title2.weight(.semibold)
    static let atomicBody = Font.body
    static let atomicCaption = Font.caption
}

// iosApp/iosApp/DesignSystem/Spacing.swift
import SwiftUI

extension CGFloat {
    static let atomicXS: CGFloat = 4
    static let atomicSM: CGFloat = 8
    static let atomicMD: CGFloat = 16
    static let atomicLG: CGFloat = 24
    static let atomicXL: CGFloat = 32
    
    // Semantic spacing
    static let atomicWeatherCardPadding: CGFloat = .atomicMD
    static let atomicScreenPadding: CGFloat = .atomicMD
}
```

**iOS Atomic Components (To Be Implemented):**

```swift
// iosApp/iosApp/Views/Atoms/WeatherIcon.swift
struct WeatherIcon: View {
    let condition: WeatherCondition
    let size: CGFloat
    
    var body: some View {
        Image(systemName: iconName)
            .foregroundColor(Color.atomicWeatherColor(for: condition))
            .font(.system(size: size))
    }
    
    private var iconName: String {
        switch condition {
        case .clear: return "sun.max.fill"
        case .clouds: return "cloud.fill"
        case .rain: return "cloud.rain.fill"
        case .snow: return "cloud.snow.fill"
        default: return "cloud.fill"
        }
    }
}

// iosApp/iosApp/Views/Molecules/TemperatureDisplay.swift
struct TemperatureDisplay: View {
    let temperature: Double
    let style: TemperatureStyle
    
    var body: some View {
        HStack(alignment: .top, spacing: 2) {
            Text("\(Int(temperature))")
                .font(style.font)
                .foregroundColor(.primary)
            
            Text("°")
                .font(style.unitFont)
                .foregroundColor(.secondary)
                .offset(y: 4)
        }
    }
}

enum TemperatureStyle {
    case large, medium, small
    
    var font: Font {
        switch self {
        case .large: return .atomicTemperatureLarge
        case .medium: return .atomicHeadline
        case .small: return .atomicBody
        }
    }
    
    var unitFont: Font {
        switch self {
        case .large: return .title
        case .medium: return .body
        case .small: return .caption
        }
    }
}
```

---

## 📱 Development Standards

### **Kotlin Coding Conventions**

#### **1. Package Structure**
```kotlin
com.weather.{module}/
├── data/
│   ├── local/           # Database, preferences
│   ├── remote/          # API, DTOs
│   ├── repository/      # Repository implementations
│   └── mapper/          # Data transformations
├── domain/
│   ├── model/           # Business entities
│   ├── repository/      # Repository interfaces
│   └── usecase/         # Business logic
├── presentation/
│   ├── viewmodel/       # Shared ViewModels
│   └── state/           # UI state classes
└── di/                  # Dependency injection
```

#### **2. Naming Conventions**
```kotlin
// Classes and interfaces
class WeatherRepository            // PascalCase
interface NetworkDataSource       // Interface with descriptive suffix
data class WeatherUiState         // Data classes for state
sealed class WeatherError         // Sealed classes for types
enum class WeatherCondition       // Enums for constants

// Functions and variables
fun getWeatherForecast()          // camelCase, verb for functions
val isLoading: Boolean            // Boolean with is/has/can prefix
private val _uiState             // Private mutable state with underscore
val weatherData by lazy { }       // Lazy initialization

// Constants
const val DEFAULT_CACHE_TTL = 24 * 60 * 60 * 1000L  // UPPER_SNAKE_CASE
private const val TAG = "WeatherViewModel"           // Logging tags
```

#### **3. Error Handling Patterns**
```kotlin
// Use Result type for operations that can fail
suspend fun getWeatherData(): Result<List<Weather>> = try {
    val data = apiService.getWeather()
    Result.success(data.map { it.toDomain() })
} catch (e: Exception) {
    Result.failure(e)
}

// Sealed classes for specific error types
sealed class WeatherError : Exception() {
    object NetworkUnavailable : WeatherError()
    object CacheCorrupted : WeatherError()
    data class ApiError(val code: Int, val message: String) : WeatherError()
    data class UnknownError(val cause: Throwable) : WeatherError()
}

// Flow error handling
fun observeWeather(): Flow<WeatherUiState> = 
    weatherRepository.getWeatherForecast()
        .map { result ->
            result.fold(
                onSuccess = { WeatherUiState.Success(it) },
                onFailure = { WeatherUiState.Error(it.message ?: "Unknown error") }
            )
        }
        .catch { emit(WeatherUiState.Error(it.message ?: "Flow error")) }
        .onStart { emit(WeatherUiState.Loading) }
```

#### **4. Coroutines Best Practices**
```kotlin
class WeatherViewModel(
    private val useCase: GetWeatherForecastUseCase
) {
    private val viewModelScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate
    )
    
    fun loadWeather() {
        viewModelScope.launch {
            // Use appropriate dispatcher for the work
            val data = withContext(Dispatchers.IO) {
                useCase.execute()
            }
            
            // Switch back to Main for UI updates
            updateUiState(data)
        }
    }
    
    fun onCleared() {
        viewModelScope.cancel() // Clean up coroutines
    }
}
```

### **Compose UI Standards**

#### **1. Composable Function Guidelines**
```kotlin
// Stateless composables preferred
@Composable
fun WeatherCard(
    weather: Weather,                    // Required parameters first
    modifier: Modifier = Modifier,       // Modifier always with default
    onClick: (() -> Unit)? = null,       // Optional callbacks
    expanded: Boolean = false            // Configuration parameters
) {
    // Implementation
}

// Preview composables for design validation
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun WeatherCardPreview() {
    AtomicTheme {
        WeatherCard(
            weather = Weather.sampleData(),
            expanded = true
        )
    }
}
```

#### **2. State Management**
```kotlin
// Hoisted state pattern
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    WeatherContent(
        uiState = uiState,
        onRefresh = viewModel::refreshWeather,
        onWeatherClick = viewModel::selectWeather
    )
}

@Composable
private fun WeatherContent(
    uiState: WeatherUiState,
    onRefresh: () -> Unit,
    onWeatherClick: (Weather) -> Unit
) {
    // Stateless implementation
}
```

#### **3. Theme Integration**
```kotlin
// Always use design tokens from theme
@Composable
fun StyledComponent() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = AtomicColors.Surface    // ✅ Use theme colors
        ),
        shape = AtomicShapes.CardShape,              // ✅ Use theme shapes
        modifier = Modifier
            .padding(AtomicSpacing.MD)               // ✅ Use theme spacing
    ) {
        Text(
            text = "Styled text",
            style = AtomicTypography.BodyLarge,      // ✅ Use theme typography
            color = AtomicColors.OnSurface           // ✅ Use semantic colors
        )
    }
}
```

### **SwiftUI Standards (iOS)**

#### **1. View Structure**
```swift
// Follow SwiftUI conventions
struct WeatherCardView: View {
    let weather: Weather
    let onTap: (() -> Void)?
    
    var body: some View {
        VStack(alignment: .leading, spacing: .atomicSM) {
            WeatherSummaryView(weather: weather)
            TemperatureDisplay(temperature: weather.temperatureHigh)
        }
        .padding(.atomicWeatherCardPadding)
        .background(Color.systemBackground)
        .cornerRadius(.atomicMD)
        .onTapGesture {
            onTap?()
        }
    }
}

// Preview with sample data
struct WeatherCardView_Previews: PreviewProvider {
    static var previews: some View {
        WeatherCardView(
            weather: Weather.sampleData(),
            onTap: nil
        )
        .previewLayout(.sizeThatFits)
        .padding()
    }
}
```

#### **2. State Observation**
```swift
// Proper state observation from KMP ViewModels
class WeatherObservableObject: ObservableObject {
    @Published var uiState: WeatherUiState = .loading
    
    private let viewModel: IOSWeatherViewModel
    private var cancellables = Set<AnyCancellable>()
    
    init(viewModel: IOSWeatherViewModel) {
        self.viewModel = viewModel
        observeState()
    }
    
    private func observeState() {
        viewModel.observeUiState { [weak self] state in
            DispatchQueue.main.async {
                self?.uiState = state
            }
        }
    }
    
    func loadWeather() {
        viewModel.loadWeather(location: "London")
    }
}
```

---

## 🔧 Build Configuration

### **Gradle Configuration**

#### **1. Version Catalog (gradle/libs.versions.toml)**
```toml
[versions]
kotlin = "1.9.22"
agp = "8.2.2"
compose-bom = "2024.02.00"
ktor = "2.3.8"
sqldelight = "2.0.1"
koin = "3.5.3"

[libraries]
# Kotlin & Coroutines
kotlin-stdlib = { module = "org.jetbrains.kotlin:kotlin-stdlib", version.ref = "kotlin" }
kotlinx-coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version = "1.7.3" }
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version = "1.6.2" }
kotlinx-datetime = { module = "org.jetbrains.kotlinx:kotlinx-datetime", version = "0.5.0" }

# Networking (Ktor)
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
ktor-client-content-negotiation = { module = "io.ktor:ktor-client-content-negotiation", version.ref = "ktor" }
ktor-serialization-kotlinx-json = { module = "io.ktor:ktor-serialization-kotlinx-json", version.ref = "ktor" }
ktor-client-logging = { module = "io.ktor:ktor-client-logging", version.ref = "ktor" }

# Platform-specific HTTP engines
ktor-client-okhttp = { module = "io.ktor:ktor-client-okhttp", version.ref = "ktor" }
ktor-client-darwin = { module = "io.ktor:ktor-client-darwin", version.ref = "ktor" }

# Database (SQLDelight)
sqldelight-runtime = { module = "app.cash.sqldelight:runtime", version.ref = "sqldelight" }
sqldelight-coroutines-extensions = { module = "app.cash.sqldelight:coroutines-extensions", version.ref = "sqldelight" }
sqldelight-android-driver = { module = "app.cash.sqldelight:android-driver", version.ref = "sqldelight" }
sqldelight-native-driver = { module = "app.cash.sqldelight:native-driver", version.ref = "sqldelight" }

# Dependency Injection (Koin)
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-android = { module = "io.insert-koin:koin-android", version.ref = "koin" }

# Testing
kotlin-test = { module = "org.jetbrains.kotlin:kotlin-test", version.ref = "kotlin" }
kotlinx-coroutines-test = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-test", version = "1.7.3" }
turbine = { module = "app.cash.turbine:turbine", version = "1.0.0" }
mockk = { module = "io.mockk:mockk", version = "1.13.8" }

# Android Compose
compose-bom = { module = "androidx.compose:compose-bom", version.ref = "compose-bom" }
compose-material3 = { module = "androidx.compose.material3:material3" }
compose-ui = { module = "androidx.compose.ui:ui" }
compose-ui-tooling = { module = "androidx.compose.ui:ui-tooling" }
compose-ui-tooling-preview = { module = "androidx.compose.ui:ui-tooling-preview" }
activity-compose = { module = "androidx.activity:activity-compose", version = "1.8.2" }
lifecycle-viewmodel-compose = { module = "androidx.lifecycle:lifecycle-viewmodel-compose", version = "2.7.0" }

[plugins]
kotlin-multiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
android-application = { id = "com.android.application", version.ref = "agp" }
android-library = { id = "com.android.library", version.ref = "agp" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
sqldelight = { id = "app.cash.sqldelight", version.ref = "sqldelight" }
```

#### **2. Shared Module Configuration**
```kotlin
// shared/build.gradle.kts
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    // Target platforms
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    // iOS Framework configuration
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.framework {
            baseName = "shared"
            isStatic = true
            
            // Export main APIs to iOS
            export(libs.kotlinx.datetime)
            export(libs.koin.core)
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            
            // Networking
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            
            // Database
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)
            
            // DI
            implementation(libs.koin.core)
        }
        
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }
        
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.koin.android)
        }
        
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }
    }
}

android {
    namespace = "com.weather.shared"
    compileSdk = 34
    
    defaultConfig {
        minSdk = 24
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

sqldelight {
    databases {
        create("WeatherDatabase") {
            packageName.set("com.weather.database")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
            verifyMigrations.set(true)
        }
    }
}
```

#### **3. Android App Configuration**
```kotlin
// androidApp/build.gradle.kts
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.weather.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.weather.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            // Build configuration fields
            buildConfigField("String", "API_BASE_URL", "\"https://api.open-meteo.com/v1/\"")
            buildConfigField("String", "DEFAULT_LOCATION", "\"London\"")
        }
        
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
            
            buildConfigField("String", "API_BASE_URL", "\"https://api.open-meteo.com/v1/\"")
            buildConfigField("String", "DEFAULT_LOCATION", "\"London\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(projects.shared)
    
    // Compose BOM
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    
    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    
    // Debug tools
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}
```

### **iOS Project Configuration**

#### **1. Xcode Build Settings**
```bash
# iosApp/scripts/build_framework.sh
#!/bin/bash

set -e

# Determine build configuration
CONFIGURATION=${CONFIGURATION:-Debug}

# Determine target architecture
if [[ "$PLATFORM_NAME" == "iphonesimulator" ]]; then
    if [[ "$ARCHS" == *"arm64"* ]]; then
        TARGET="iosSimulatorArm64"
    else
        TARGET="iosX64"
    fi
else
    TARGET="iosArm64"
fi

# Build the shared framework
cd "${SRCROOT}/.."
./gradlew :shared:link${CONFIGURATION}Framework${TARGET}

# Copy framework to Xcode build directory
FRAMEWORK_PATH="${SRCROOT}/../shared/build/bin/${TARGET,,}/${CONFIGURATION,,}Framework/shared.framework"
DEST_PATH="${BUILT_PRODUCTS_DIR}/shared.framework"

if [ -d "$DEST_PATH" ]; then
    rm -rf "$DEST_PATH"
fi

cp -R "$FRAMEWORK_PATH" "$DEST_PATH"

echo "✅ Shared framework built and copied successfully"
```

#### **2. Info.plist Configuration**
```xml
<!-- iosApp/iosApp/Info.plist -->
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>CFBundleDevelopmentRegion</key>
    <string>$(DEVELOPMENT_LANGUAGE)</string>
    <key>CFBundleExecutable</key>
    <string>$(EXECUTABLE_NAME)</string>
    <key>CFBundleIdentifier</key>
    <string>$(PRODUCT_BUNDLE_IDENTIFIER)</string>
    <key>CFBundleInfoDictionaryVersion</key>
    <string>6.0</string>
    <key>CFBundleName</key>
    <string>$(PRODUCT_NAME)</string>
    <key>CFBundlePackageType</key>
    <string>$(PRODUCT_BUNDLE_PACKAGE_TYPE)</string>
    <key>CFBundleShortVersionString</key>
    <string>1.0</string>
    <key>CFBundleVersion</key>
    <string>1</string>
    <key>LSRequiresIPhoneOS</key>
    <true/>
    <key>UIApplicationSceneManifest</key>
    <dict>
        <key>UIApplicationSupportsMultipleScenes</key>
        <true/>
        <key>UISceneConfigurations</key>
        <dict>
            <key>UIWindowSceneSessionRoleApplication</key>
            <array>
                <dict>
                    <key>UISceneConfigurationName</key>
                    <string>Default Configuration</string>
                    <key>UISceneDelegateClassName</key>
                    <string>$(PRODUCT_MODULE_NAME).SceneDelegate</string>
                </dict>
            </array>
        </dict>
    </dict>
    <key>UIRequiredDeviceCapabilities</key>
    <array>
        <string>armv7</string>
    </array>
    <key>UISupportedInterfaceOrientations</key>
    <array>
        <string>UIInterfaceOrientationPortrait</string>
        <string>UIInterfaceOrientationLandscapeLeft</string>
        <string>UIInterfaceOrientationLandscapeRight</string>
    </array>
    <key>UISupportedInterfaceOrientations~ipad</key>
    <array>
        <string>UIInterfaceOrientationPortrait</string>
        <string>UIInterfaceOrientationPortraitUpsideDown</string>
        <string>UIInterfaceOrientationLandscapeLeft</string>
        <string>UIInterfaceOrientationLandscapeRight</string>
    </array>
    <key>NSAppTransportSecurity</key>
    <dict>
        <key>NSAllowsArbitraryLoads</key>
        <false/>
        <key>NSExceptionDomains</key>
        <dict>
            <key>api.open-meteo.com</key>
            <dict>
                <key>NSExceptionRequiresForwardSecrecy</key>
                <false/>
                <key>NSExceptionMinimumTLSVersion</key>
                <string>TLSv1.2</string>
                <key>NSThirdPartyExceptionRequiresForwardSecrecy</key>
                <false/>
            </dict>
        </dict>
    </dict>
</dict>
</plist>
```

---

## 🧪 Testing Framework

### **Testing Architecture**

```
Testing Pyramid:
┌─────────────────┐
│   E2E Tests     │  ← Platform-specific UI tests
├─────────────────┤
│ Integration     │  ← Repository, Database, API
├─────────────────┤  
│   Unit Tests    │  ← Domain, ViewModels, Mappers
└─────────────────┘
```

### **Shared Testing Setup**

#### **1. Base Test Configuration**
```kotlin
// shared/src/commonTest/kotlin/com/weather/testing/TestBase.kt
abstract class TestBase {
    @BeforeTest
    fun setUp() {
        // Common test setup
        Dispatchers.setMain(StandardTestDispatcher())
    }
    
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }
}

// Test utilities
object TestData {
    val sampleWeather = Weather(
        date = LocalDate(2024, 1, 15),
        condition = WeatherCondition.CLEAR,
        temperatureHigh = 22.0,
        temperatureLow = 15.0,
        humidity = 65,
        icon = "01d",
        description = "Clear sky"
    )
    
    val sampleWeatherList = listOf(
        sampleWeather,
        sampleWeather.copy(
            date = LocalDate(2024, 1, 16),
            condition = WeatherCondition.CLOUDS,
            temperatureHigh = 18.0,
            description = "Partly cloudy"
        )
    )
}
```

#### **2. Domain Layer Tests**
```kotlin
// shared/src/commonTest/kotlin/com/weather/domain/model/WeatherTest.kt
class WeatherTest : TestBase() {
    
    @Test
    fun `weather temperature range displays correctly`() {
        val weather = TestData.sampleWeather
        
        assertEquals("15° - 22°", weather.temperatureRange)
    }
    
    @Test
    fun `weather condition classification works`() {
        val hotWeather = TestData.sampleWeather.copy(temperatureHigh = 35.0)
        val coldWeather = TestData.sampleWeather.copy(temperatureHigh = 5.0)
        val normalWeather = TestData.sampleWeather
        
        assertTrue(hotWeather.isHot)
        assertFalse(hotWeather.isCold)
        
        assertTrue(coldWeather.isCold)
        assertFalse(coldWeather.isHot)
        
        assertFalse(normalWeather.isHot)
        assertFalse(normalWeather.isCold)
    }
}

// shared/src/commonTest/kotlin/com/weather/domain/usecase/GetWeatherForecastUseCaseTest.kt
class GetWeatherForecastUseCaseTest : TestBase() {
    
    private val mockRepository = mockk<WeatherRepository>()
    private val useCase = GetWeatherForecastUseCase(mockRepository)
    
    @Test
    fun `use case returns success when repository succeeds`() = runTest {
        // Given
        val location = "London"
        val expectedWeather = TestData.sampleWeatherList
        every { mockRepository.getWeatherForecast(location) } returns 
            flowOf(Result.success(expectedWeather))
        
        // When
        val result = useCase(location).first()
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedWeather, result.getOrNull())
        verify { mockRepository.getWeatherForecast(location) }
    }
    
    @Test
    fun `use case returns failure when repository fails`() = runTest {
        // Given
        val location = "InvalidLocation"
        val error = Exception("Location not found")
        every { mockRepository.getWeatherForecast(location) } returns 
            flowOf(Result.failure(error))
        
        // When
        val result = useCase(location).first()
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Location not found", result.exceptionOrNull()?.message)
    }
}
```

#### **3. Data Layer Tests**
```kotlin
// shared/src/commonTest/kotlin/com/weather/data/repository/WeatherRepositoryImplTest.kt
class WeatherRepositoryImplTest : TestBase() {
    
    private val mockLocalDataSource = mockk<LocalWeatherDataSource>()
    private val mockRemoteDataSource = mockk<RemoteWeatherDataSource>()
    private val mockCacheConfig = CacheConfig(cacheExpiryHours = 24)
    
    private val repository = WeatherRepositoryImpl(
        localDataSource = mockLocalDataSource,
        remoteDataSource = mockRemoteDataSource,
        cacheConfig = mockCacheConfig
    )
    
    @Test
    fun `repository returns cached data first then fresh data`() = runTest {
        // Given
        val location = "London"
        val cachedData = TestData.sampleWeatherList
        val freshData = TestData.sampleWeatherList.map { 
            it.copy(temperatureHigh = it.temperatureHigh + 2.0) 
        }
        
        every { mockLocalDataSource.getWeatherForecast() } returns 
            flowOf(cachedData)
        coEvery { mockRemoteDataSource.getWeatherForecast(location) } returns 
            freshData
        coEvery { mockLocalDataSource.saveWeatherForecast(any()) } just Runs
        
        // When
        val results = repository.getWeatherForecast(location).take(2).toList()
        
        // Then
        assertEquals(2, results.size)
        assertTrue(results[0].isSuccess)
        assertEquals(cachedData, results[0].getOrNull())
        assertTrue(results[1].isSuccess)
        assertEquals(freshData, results[1].getOrNull())
        
        coVerify { mockRemoteDataSource.getWeatherForecast(location) }
        coVerify { mockLocalDataSource.saveWeatherForecast(freshData) }
    }
}

// shared/src/commonTest/kotlin/com/weather/data/mapper/WeatherMapperTest.kt
class WeatherMapperTest : TestBase() {
    
    @Test
    fun `DTO to domain mapping works correctly`() {
        // Given
        val dto = WeatherItemDto(
            date = "2024-01-15",
            temperatureMax = 22.0,
            temperatureMin = 15.0,
            humidity = 65,
            weatherCode = 0,
            windSpeed = 10.0
        )
        
        // When
        val domain = dto.toDomain()
        
        // Then
        assertEquals(LocalDate(2024, 1, 15), domain.date)
        assertEquals(WeatherCondition.CLEAR, domain.condition)
        assertEquals(22.0, domain.temperatureHigh)
        assertEquals(15.0, domain.temperatureLow)
        assertEquals(65, domain.humidity)
        assertEquals("01d", domain.icon)
        assertEquals("Clear sky", domain.description)
    }
    
    @Test
    fun `domain to entity mapping preserves data`() {
        // Given
        val weather = TestData.sampleWeather
        val cachedAt = System.currentTimeMillis()
        
        // When
        val entity = weather.toEntity(cachedAt)
        
        // Then
        assertEquals(weather.date.toEpochDays().toLong(), entity.date)
        assertEquals(weather.condition.name, entity.condition)
        assertEquals(weather.temperatureHigh, entity.temperature_high)
        assertEquals(weather.temperatureLow, entity.temperature_low)
        assertEquals(weather.humidity.toLong(), entity.humidity)
        assertEquals(cachedAt, entity.cached_at)
    }
}
```

#### **4. Presentation Layer Tests**
```kotlin
// shared/src/commonTest/kotlin/com/weather/presentation/viewmodel/WeatherViewModelTest.kt
class WeatherViewModelTest : TestBase() {
    
    private val mockGetWeatherUseCase = mockk<GetWeatherForecastUseCase>()
    private val mockRefreshUseCase = mockk<RefreshWeatherUseCase>()
    
    private val viewModel = WeatherViewModel(
        getWeatherForecastUseCase = mockGetWeatherUseCase,
        refreshWeatherUseCase = mockRefreshUseCase
    )
    
    @Test
    fun `initial state is loading`() {
        assertEquals(WeatherUiState.Loading, viewModel.uiState.value)
    }
    
    @Test
    fun `load weather success updates state correctly`() = runTest {
        // Given
        val weatherData = TestData.sampleWeatherList
        every { mockGetWeatherUseCase.invoke(any()) } returns 
            flowOf(Result.success(weatherData))
        
        // When
        viewModel.loadWeather("London")
        
        // Then
        viewModel.uiState.test {
            assertEquals(WeatherUiState.Loading, awaitItem())
            val successState = awaitItem() as WeatherUiState.Success
            assertEquals(weatherData, successState.data)
        }
    }
    
    @Test
    fun `load weather failure updates state with error`() = runTest {
        // Given
        val errorMessage = "Network error"
        every { mockGetWeatherUseCase.invoke(any()) } returns 
            flowOf(Result.failure(Exception(errorMessage)))
        
        // When
        viewModel.loadWeather("London")
        
        // Then
        viewModel.uiState.test {
            assertEquals(WeatherUiState.Loading, awaitItem())
            val errorState = awaitItem() as WeatherUiState.Error
            assertEquals(errorMessage, errorState.message)
        }
    }
    
    @Test
    fun `refresh weather calls refresh use case`() = runTest {
        // Given
        coEvery { mockRefreshUseCase.invoke() } just Runs
        
        // When
        viewModel.refreshWeather()
        
        // Then
        coVerify { mockRefreshUseCase.invoke() }
    }
}
```

### **Platform-Specific Testing**

#### **Android Testing**
```kotlin
// androidApp/src/test/kotlin/com/weather/android/ui/WeatherViewModelBridgeTest.kt
@RunWith(AndroidJUnit4::class)
class WeatherViewModelBridgeTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private val mockWeatherViewModel = mockk<WeatherViewModel>()
    private val bridge = WeatherViewModelBridge(mockWeatherViewModel)
    
    @Test
    fun `bridge delegates load weather call`() {
        // Given
        every { mockWeatherViewModel.loadWeather(any()) } just Runs
        
        // When
        bridge.loadWeather("London")
        
        // Then
        verify { mockWeatherViewModel.loadWeather("London") }
    }
    
    @Test
    fun `bridge converts state flow to live data`() {
        // Given
        val testState = WeatherUiState.Success(TestData.sampleWeatherList)
        every { mockWeatherViewModel.uiState } returns MutableStateFlow(testState)
        
        // When
        val liveData = bridge.uiState
        
        // Then
        assertEquals(testState, liveData.value)
    }
}

// androidApp/src/androidTest/kotlin/com/weather/android/ui/WeatherDashboardTest.kt
@RunWith(AndroidJUnit4::class)
class WeatherDashboardTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun `weather dashboard displays loading state`() {
        composeTestRule.setContent {
            WeatherDashboard(
                uiState = WeatherUiState.Loading
            )
        }
        
        composeTestRule
            .onNodeWithTag("LoadingIndicator")
            .assertIsDisplayed()
    }
    
    @Test
    fun `weather dashboard displays weather list`() {
        val weatherData = TestData.sampleWeatherList
        
        composeTestRule.setContent {
            WeatherDashboard(
                uiState = WeatherUiState.Success(weatherData)
            )
        }
        
        composeTestRule
            .onNodeWithText("Clear sky")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("22°")
            .assertIsDisplayed()
    }
}
```

#### **iOS Testing**
```swift
// iosApp/iosAppTests/WeatherViewModelTests.swift
import XCTest
@testable import iosApp

class WeatherViewModelTests: XCTestCase {
    
    var viewModel: IOSWeatherViewModel!
    var mockSharedViewModel: MockWeatherViewModel!
    
    override func setUp() {
        super.setUp()
        mockSharedViewModel = MockWeatherViewModel()
        viewModel = IOSWeatherViewModel(weatherViewModel: mockSharedViewModel)
    }
    
    override func tearDown() {
        viewModel.dispose()
        super.tearDown()
    }
    
    func testLoadWeatherCallsSharedViewModel() {
        // When
        viewModel.loadWeather(location: "London")
        
        // Then
        XCTAssertTrue(mockSharedViewModel.loadWeatherCalled)
        XCTAssertEqual("London", mockSharedViewModel.lastLocation)
    }
}

// Mock implementation for testing
class MockWeatherViewModel: WeatherViewModel {
    var loadWeatherCalled = false
    var lastLocation: String?
    
    override func loadWeather(location: String) {
        loadWeatherCalled = true
        lastLocation = location
    }
}
```

### **Test Execution**

```bash
# Run all shared tests
./gradlew :shared:allTests

# Run specific test suites
./gradlew :shared:commonTest              # Shared tests
./gradlew :shared:testDebugUnitTest       # Android unit tests
./gradlew :shared:iosX64Test              # iOS tests

# Run with coverage
./gradlew :shared:allTests jacocoTestReport

# Run Android instrumented tests
./gradlew :androidApp:connectedAndroidTest

# Run iOS tests
xcodebuild test -project iosApp.xcodeproj -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15'
```

---

## 🚀 Performance Optimization

### **Memory Management**

#### **1. Lifecycle-Aware Components**
```kotlin
// Proper coroutine lifecycle management
class WeatherViewModel {
    private val viewModelScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate
    )
    
    fun onCleared() {
        viewModelScope.cancel() // Prevents memory leaks
    }
}

// Android Bridge with proper cleanup
class WeatherViewModelBridge(
    private val weatherViewModel: WeatherViewModel
) : ViewModel() {
    
    override fun onCleared() {
        super.onCleared()
        weatherViewModel.onCleared()
    }
}
```

#### **2. Efficient State Management**
```kotlin
// Use StateFlow for reactive state with replay(1)
private val _uiState = MutableStateFlow(WeatherUiState.Loading)
val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

// Avoid unnecessary state emissions
fun updateWeather(data: List<Weather>) {
    val newState = WeatherUiState.Success(data)
    if (_uiState.value != newState) {  // Only emit if state changed
        _uiState.value = newState
    }
}
```

#### **3. Database Optimization**
```sql
-- shared/src/commonMain/sqldelight/com/weather/database/WeatherDatabase.sq

-- Efficient queries with proper indexing
CREATE INDEX weather_date_idx ON WeatherForecast(date);
CREATE INDEX weather_cached_at_idx ON WeatherForecast(cached_at);

-- Cleanup old cache data
DELETE FROM WeatherForecast 
WHERE cached_at < :cutoffTime;

-- Efficient data retrieval
SELECT * FROM WeatherForecast 
WHERE date >= :startDate 
ORDER BY date ASC 
LIMIT :maxResults;
```

### **Network Optimization**

#### **1. Smart Caching Strategy**
```kotlin
class WeatherRepositoryImpl {
    
    suspend fun getWeatherWithCaching(location: String): Flow<Result<List<Weather>>> = flow {
        // 1. Check cache validity
        val cachedData = localDataSource.getWeatherForecast().firstOrNull()
        val isCacheValid = cachedData?.let { 
            System.currentTimeMillis() - it.first().cachedAt < cacheConfig.cacheExpiryMillis 
        } ?: false
        
        // 2. Emit cached data if valid
        if (isCacheValid && cachedData.isNotEmpty()) {
            emit(Result.success(cachedData))
        }
        
        // 3. Fetch fresh data only if needed
        if (!isCacheValid) {
            try {
                val freshData = remoteDataSource.getWeatherForecast(location)
                localDataSource.saveWeatherForecast(freshData)
                emit(Result.success(freshData))
            } catch (e: Exception) {
                // Fallback to stale cache if available
                if (cachedData?.isNotEmpty() == true) {
                    emit(Result.success(cachedData))
                } else {
                    emit(Result.failure(e))
                }
            }
        }
    }
}
```

#### **2. Request Optimization**
```kotlin
// Ktor client configuration for performance
val httpClient = HttpClient(platformEngine) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true      // Handle API changes gracefully
            coerceInputValues = true      // Handle malformed data
        })
    }
    
    install(HttpTimeout) {
        requestTimeoutMillis = 15_000     // 15 second timeout
        connectTimeoutMillis = 10_000     // 10 second connection timeout
    }
    
    install(Logging) {
        level = if (BuildConfig.DEBUG) LogLevel.BODY else LogLevel.NONE
    }
    
    defaultRequest {
        header("User-Agent", "WeatherKMP/1.0")
        header("Accept", "application/json")
    }
}
```

### **UI Performance**

#### **Android Compose Optimization**
```kotlin
// Use remember for expensive calculations
@Composable
fun WeatherCard(weather: Weather) {
    val temperatureColor = remember(weather.temperatureHigh) {
        when {
            weather.temperatureHigh > 30 -> AtomicColors.Hot
            weather.temperatureHigh < 10 -> AtomicColors.Cold
            else -> AtomicColors.Normal
        }
    }
    
    // Use derivedStateOf for computed state
    val isExpanded by remember { mutableStateOf(false) }
    val animatedHeight by animateDpAsState(
        targetValue = if (isExpanded) 200.dp else 100.dp,
        label = "card_height"
    )
    
    Card(
        modifier = Modifier
            .height(animatedHeight)
            .clickable { isExpanded = !isExpanded }
    ) {
        // Card content
    }
}

// Lazy loading with proper keys
@Composable
fun WeatherList(weatherList: List<Weather>) {
    LazyColumn {
        items(
            items = weatherList,
            key = { it.date.toEpochDays() }  // Stable key for recomposition
        ) { weather ->
            WeatherCard(weather = weather)
        }
    }
}
```

#### **iOS SwiftUI Optimization**
```swift
// Use @StateObject for view model lifecycle
struct WeatherView: View {
    @StateObject private var viewModel = WeatherObservableObject()
    
    var body: some View {
        List(weatherData, id: \.date) { weather in
            WeatherRowView(weather: weather)
                .onAppear {
                    // Lazy loading trigger
                    if shouldLoadMore(for: weather) {
                        viewModel.loadMoreWeather()
                    }
                }
        }
        .refreshable {
            await viewModel.refreshWeather()
        }
    }
}

// Efficient state observation
class WeatherObservableObject: ObservableObject {
    @Published var weatherData: [Weather] = []
    
    private let viewModel: IOSWeatherViewModel
    private var cancellables = Set<AnyCancellable>()
    
    func observeWeatherData() {
        viewModel.weatherStatePublisher
            .receive(on: DispatchQueue.main)
            .sink { [weak self] state in
                switch state {
                case .success(let data):
                    self?.weatherData = data
                case .loading, .error:
                    break
                }
            }
            .store(in: &cancellables)
    }
}
```

---

## 🔒 Security Implementation

### **API Security**

#### **1. Environment-Based Configuration**
```kotlin
// gradle.properties (gitignored)
WEATHER_API_KEY=your_actual_api_key_here
WEATHER_BASE_URL=https://api.open-meteo.com/v1/

// shared/src/commonMain/kotlin/com/weather/BuildConfig.kt
object BuildConfig {
    const val API_BASE_URL = "https://api.open-meteo.com/v1/"
    // API key injected at build time, never hardcoded
}

// Secure API key handling
class ApiKeyProvider {
    fun getApiKey(): String {
        return System.getenv("WEATHER_API_KEY") 
            ?: throw SecurityException("API key not configured")
    }
}
```

#### **2. Network Security**
```kotlin
// Certificate pinning for production
val httpClient = HttpClient {
    engine {
        // Certificate pinning
        certificatePinner {
            add("api.open-meteo.com") {
                "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
            }
        }
    }
    
    install(Auth) {
        bearer {
            loadTokens {
                // Secure token loading from encrypted storage
                BearerTokens(getSecureApiKey(), null)
            }
        }
    }
}

// Request/Response encryption
suspend fun secureApiCall(): Result<WeatherResponse> = try {
    val response = httpClient.get("$BASE_URL/forecast") {
        headers {
            append("Authorization", "Bearer ${getEncryptedToken()}")
            append("X-Request-ID", generateSecureRequestId())
        }
    }
    Result.success(response.body())
} catch (e: Exception) {
    logSecurityEvent("API_CALL_FAILED", e.message)
    Result.failure(e)
}
```

### **Data Security**

#### **1. Database Encryption**
```kotlin
// Encrypted SQLDelight database
class SecureWeatherDatabase {
    private val database: WeatherDatabase by lazy {
        val driver = when (Platform.current) {
            is Platform.Android -> {
                AndroidSqliteDriver(
                    schema = WeatherDatabase.Schema,
                    context = context,
                    name = "weather.db",
                    // Enable encryption
                    factory = SupportSQLiteOpenHelper.Factory { configuration ->
                        SQLCipherUtils.encrypt(context, "weather.db", getDatabaseKey())
                        FrameworkSQLiteOpenHelperFactory().create(configuration)
                    }
                )
            }
            is Platform.iOS -> {
                NativeSqliteDriver(
                    schema = WeatherDatabase.Schema,
                    name = "weather.db"
                    // iOS keychain integration for encryption key
                )
            }
        }
        WeatherDatabase(driver)
    }
    
    private fun getDatabaseKey(): String {
        return secureKeyProvider.getDatabaseEncryptionKey()
    }
}
```

#### **2. Secure Data Storage**
```kotlin
// Platform-specific secure storage
expect class SecureStorage {
    fun store(key: String, value: String)
    fun retrieve(key: String): String?
    fun delete(key: String)
}

// Android implementation
actual class SecureStorage(private val context: Context) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    
    private val sharedPreferences = EncryptedSharedPreferences.create(
        "weather_secure_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    actual fun store(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }
    
    actual fun retrieve(key: String): String? {
        return sharedPreferences.getString(key, null)
    }
}

// iOS implementation
actual class SecureStorage {
    actual fun store(key: String, value: String) {
        // iOS Keychain implementation
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrAccount as String: key,
            kSecValueData as String: value.data(using: .utf8)!
        ]
        SecItemAdd(query as CFDictionary, nil)
    }
}
```

### **Input Validation**

```kotlin
// Comprehensive input validation
class InputValidator {
    
    fun validateLocation(location: String): ValidationResult {
        return when {
            location.isBlank() -> ValidationResult.Error("Location cannot be empty")
            location.length > 100 -> ValidationResult.Error("Location too long")
            !location.matches(Regex("[a-zA-Z\\s,.-]+")) -> 
                ValidationResult.Error("Invalid characters in location")
            else -> ValidationResult.Valid
        }
    }
    
    fun sanitizeInput(input: String): String {
        return input.trim()
            .replace(Regex("[<>\"'&]"), "")  // Remove potential XSS characters
            .take(100)  // Limit length
    }
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}
```

---

## 🚀 Deployment Pipeline

### **CI/CD Configuration**

#### **1. GitHub Actions - Android**
```yaml
# .github/workflows/android-ci.yml
name: Android CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
        
    - name: Cache Gradle packages
      uses: actions/cache@v3
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
        restore-keys: |
          ${{ runner.os }}-gradle-
          
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
      
    - name: Run shared tests
      run: ./gradlew :shared:allTests
      
    - name: Run Android tests
      run: ./gradlew :androidApp:testDebugUnitTest
      
    - name: Build Android APK
      run: ./gradlew :androidApp:assembleDebug
      
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: debug-apk
        path: androidApp/build/outputs/apk/debug/*.apk

  lint:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
        
    - name: Run Ktlint
      run: ./gradlew ktlintCheck
      
    - name: Run Detekt
      run: ./gradlew detekt

  security:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    - name: Run security scan
      uses: securecodewarrior/github-action-add-sarif@v1
      with:
        sarif-file: 'security-scan-results.sarif'
```

#### **2. GitHub Actions - iOS**
```yaml
# .github/workflows/ios-ci.yml
name: iOS CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: macos-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK 17 for Gradle
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
        
    - name: Cache Gradle packages
      uses: actions/cache@v3
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
        
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
      
    - name: Build shared framework
      run: ./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
      
    - name: Build iOS app
      run: |
        cd iosApp
        xcodebuild clean build \
          -project iosApp.xcodeproj \
          -scheme iosApp \
          -destination 'platform=iOS Simulator,name=iPhone 15' \
          CODE_SIGNING_REQUIRED=NO
          
    - name: Run iOS tests
      run: |
        cd iosApp
        xcodebuild test \
          -project iosApp.xcodeproj \
          -scheme iosApp \
          -destination 'platform=iOS Simulator,name=iPhone 15'

  archive:
    runs-on: macos-latest
    if: github.ref == 'refs/heads/main'
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Build and Archive
      run: |
        cd iosApp
        xcodebuild archive \
          -project iosApp.xcodeproj \
          -scheme iosApp \
          -archivePath ./build/iosApp.xcarchive \
          -configuration Release
          
    - name: Export IPA
      run: |
        cd iosApp
        xcodebuild -exportArchive \
          -archivePath ./build/iosApp.xcarchive \
          -exportPath ./build \
          -exportOptionsPlist exportOptions.plist
          
    - name: Upload IPA
      uses: actions/upload-artifact@v3
      with:
        name: ios-ipa
        path: iosApp/build/*.ipa
```

### **Release Management**

#### **1. Automated Versioning**
```kotlin
// build.gradle.kts (root)
val versionMajor = 1
val versionMinor = 0
val versionPatch = 0
val versionBuild = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 0

allprojects {
    version = "$versionMajor.$versionMinor.$versionPatch.$versionBuild"
}

// Android versioning
android {
    defaultConfig {
        versionCode = versionMajor * 1000000 + versionMinor * 10000 + versionPatch * 100 + versionBuild
        versionName = "$versionMajor.$versionMinor.$versionPatch"
    }
}
```

#### **2. Release Notes Generation**
```yaml
# .github/workflows/release.yml
name: Release

on:
  push:
    tags:
      - 'v*'

jobs:
  release:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
      with:
        fetch-depth: 0
        
    - name: Generate Release Notes
      id: release_notes
      run: |
        # Generate changelog from commits
        echo "RELEASE_NOTES<<EOF" >> $GITHUB_OUTPUT
        git log --pretty=format:"- %s" $(git describe --tags --abbrev=0 HEAD^)..HEAD >> $GITHUB_OUTPUT
        echo "EOF" >> $GITHUB_OUTPUT
        
    - name: Create Release
      uses: actions/create-release@v1
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
      with:
        tag_name: ${{ github.ref }}
        release_name: Release ${{ github.ref }}
        body: ${{ steps.release_notes.outputs.RELEASE_NOTES }}
        draft: false
        prerelease: false
```

### **Production Deployment**

#### **1. Android Play Store**
```bash
# Automated Play Store deployment
- name: Deploy to Play Store
  uses: r0adkll/upload-google-play@v1
  with:
    serviceAccountJsonPlainText: ${{ secrets.GOOGLE_PLAY_SERVICE_ACCOUNT }}
    packageName: com.weather.android
    releaseFiles: androidApp/build/outputs/bundle/release/*.aab
    track: production
    status: completed
```

#### **2. iOS App Store**
```bash
# Automated App Store deployment
- name: Deploy to App Store
  uses: apple-actions/upload-testflight-build@v1
  with:
    app-path: iosApp/build/iosApp.ipa
    issuer-id: ${{ secrets.APPSTORE_ISSUER_ID }}
    api-key-id: ${{ secrets.APPSTORE_API_KEY_ID }}
    api-private-key: ${{ secrets.APPSTORE_API_PRIVATE_KEY }}
```

---

## 📚 Additional Resources

### **Learning Resources**

1. **Kotlin Multiplatform**
   - [Official KMP Documentation](https://kotlinlang.org/docs/multiplatform.html)
   - [KMP Samples Repository](https://github.com/Kotlin/kmm-samples)

2. **Atomic Design**
   - [Atomic Design by Brad Frost](https://atomicdesign.bradfrost.com/)
   - [Atomic Design in Practice](https://medium.com/@audreyhacq/atomic-design-in-practice-9a4e5f1a21ab)

3. **Architecture Patterns**
   - [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
   - [MVVM in Android](https://developer.android.com/jetpack/guide)

### **Development Tools**

1. **Code Quality**
   - [Ktlint](https://ktlint.github.io/) - Kotlin linter
   - [Detekt](https://detekt.github.io/detekt/) - Static analysis

2. **Performance**
   - [Compose Performance](https://developer.android.com/jetpack/compose/performance)
   - [SQLDelight Performance](https://cashapp.github.io/sqldelight/performance/)

3. **Testing**
   - [Turbine](https://github.com/cashapp/turbine) - Flow testing
   - [MockK](https://mockk.io/) - Mocking library

---

**🔧 This technical documentation provides the complete foundation for building production-ready KMP applications with atomic design systems. Follow these patterns and standards to ensure code quality, performance, and maintainability across platforms.**