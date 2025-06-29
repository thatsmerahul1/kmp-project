# Atomic Design System for KMP Weather App

## 🧬 Atomic Design Hierarchy

### **Atoms** → **Molecules** → **Organisms** → **Templates** → **Pages**

```
Atoms (Basic building blocks)
├── Buttons
├── Text Components
├── Icons
├── Input Fields
└── Loading Indicators

Molecules (Simple component groups)
├── Search Bar (Input + Icon)
├── Temperature Display (Text + Icon)
├── Weather Card Header (Icon + Text + Text)
└── Loading Card (Multiple loading indicators)

Organisms (Complex UI sections)
├── Weather List
├── Weather Detail Card
├── Header with Search
└── Error State Container

Templates (Page layouts)
├── Dashboard Template
├── Detail Template
└── Error Template

Pages (Complete screens)
├── Weather Dashboard
├── Weather Detail
└── Settings
```

## 📱 Implementation Structure

### Android (Jetpack Compose)

```
androidApp/src/main/kotlin/ui/
├── atoms/
│   ├── button/
│   │   ├── PrimaryButton.kt
│   │   ├── SecondaryButton.kt
│   │   └── IconButton.kt
│   ├── text/
│   │   ├── HeadlineText.kt
│   │   ├── BodyText.kt
│   │   ├── CaptionText.kt
│   │   └── DisplayText.kt
│   ├── icon/
│   │   ├── WeatherIcon.kt
│   │   ├── SystemIcon.kt
│   │   └── AnimatedIcon.kt
│   ├── input/
│   │   ├── SearchInput.kt
│   │   └── TextField.kt
│   └── loading/
│       ├── CircularLoader.kt
│       ├── ShimmerBox.kt
│       └── SkeletonLoader.kt
├── molecules/
│   ├── TemperatureDisplay.kt
│   ├── WeatherSummary.kt
│   ├── SearchBar.kt
│   ├── WeatherCardHeader.kt
│   └── LoadingCard.kt
├── organisms/
│   ├── WeatherList.kt
│   ├── WeatherDetailCard.kt
│   ├── AppHeader.kt
│   └── ErrorStateView.kt
├── templates/
│   ├── DashboardTemplate.kt
│   ├── DetailTemplate.kt
│   └── ErrorTemplate.kt
├── pages/
│   ├── WeatherDashboard.kt
│   ├── WeatherDetail.kt
│   └── Settings.kt
└── theme/
    ├── AtomicTheme.kt
    ├── Colors.kt
    ├── Typography.kt
    ├── Spacing.kt
    └── Shapes.kt
```

### iOS (SwiftUI)

```
iosApp/Views/
├── Atoms/
│   ├── Buttons/
│   │   ├── PrimaryButton.swift
│   │   ├── SecondaryButton.swift
│   │   └── IconButton.swift
│   ├── Text/
│   │   ├── HeadlineText.swift
│   │   ├── BodyText.swift
│   │   ├── CaptionText.swift
│   │   └── DisplayText.swift
│   ├── Icons/
│   │   ├── WeatherIcon.swift
│   │   ├── SystemIcon.swift
│   │   └── AnimatedIcon.swift
│   └── Loading/
│       ├── CircularLoader.swift
│       ├── ShimmerView.swift
│       └── SkeletonLoader.swift
├── Molecules/
│   ├── TemperatureDisplay.swift
│   ├── WeatherSummary.swift
│   ├── SearchBar.swift
│   └── WeatherCardHeader.swift
├── Organisms/
│   ├── WeatherList.swift
│   ├── WeatherDetailCard.swift
│   └── ErrorStateView.swift
├── Templates/
│   ├── DashboardTemplate.swift
│   └── DetailTemplate.swift
├── Pages/
│   ├── WeatherDashboard.swift
│   └── WeatherDetail.swift
└── DesignSystem/
    ├── AtomicTheme.swift
    ├── Colors.swift
    ├── Typography.swift
    ├── Spacing.swift
    └── Shapes.swift
```

## 🎨 Design System Foundation

### Color System
```kotlin
// Android - Colors.kt
object AtomicColors {
    // Primary Colors
    val Primary = Color(0xFF1976D2)
    val PrimaryVariant = Color(0xFF1565C0)
    val OnPrimary = Color.White
    
    // Surface Colors
    val Surface = Color(0xFFFAFAFA)
    val OnSurface = Color(0xFF212121)
    val SurfaceVariant = Color(0xFFF5F5F5)
    
    // Semantic Colors
    val Success = Color(0xFF4CAF50)
    val Warning = Color(0xFFFF9800)
    val Error = Color(0xFFF44336)
    val Info = Color(0xFF2196F3)
    
    // Weather Specific
    val Sunny = Color(0xFFFFD54F)
    val Cloudy = Color(0xFF90A4AE)
    val Rainy = Color(0xFF42A5F5)
    val Snowy = Color(0xFFE1F5FE)
}
```

```swift
// iOS - Colors.swift
extension Color {
    static let atomicPrimary = Color(red: 0.098, green: 0.463, blue: 0.824)
    static let atomicSurface = Color(red: 0.98, green: 0.98, blue: 0.98)
    static let atomicSuccess = Color(red: 0.298, green: 0.686, blue: 0.314)
    // ... other colors
}
```

### Typography System
```kotlin
// Android - Typography.kt
object AtomicTypography {
    val DisplayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    )
    
    val HeadlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    )
    
    val BodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )
    
    val LabelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
}
```

### Spacing System
```kotlin
// Android - Spacing.kt
object AtomicSpacing {
    val None = 0.dp
    val XS = 4.dp
    val SM = 8.dp
    val MD = 16.dp
    val LG = 24.dp
    val XL = 32.dp
    val XXL = 48.dp
}
```

## ⚛️ Atomic Components Implementation

### Atoms - Basic Building Blocks

#### 1. Text Components (Android)
```kotlin
// HeadlineText.kt
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

// BodyText.kt
@Composable
fun BodyText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = AtomicColors.OnSurface,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        text = text,
        style = AtomicTypography.BodyLarge,
        color = color,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}
```

#### 2. Button Components (Android)
```kotlin
// PrimaryButton.kt
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = AtomicColors.Primary,
            contentColor = AtomicColors.OnPrimary
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.height(48.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = AtomicColors.OnPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = AtomicTypography.LabelMedium
            )
        }
    }
}
```

#### 3. Weather Icon (Android)
```kotlin
// WeatherIcon.kt
@Composable
fun WeatherIcon(
    condition: WeatherCondition,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    tint: Color = Color.Unspecified
) {
    val iconRes = when (condition) {
        WeatherCondition.CLEAR -> Icons.Default.WbSunny
        WeatherCondition.CLOUDS -> Icons.Default.Cloud
        WeatherCondition.RAIN -> Icons.Default.Grain
        WeatherCondition.SNOW -> Icons.Default.AcUnit
        WeatherCondition.THUNDERSTORM -> Icons.Default.FlashOn
        else -> Icons.Default.Help
    }
    
    Icon(
        imageVector = iconRes,
        contentDescription = condition.name,
        tint = tint,
        modifier = modifier.size(size)
    )
}
```

### Molecules - Component Combinations

#### 1. Temperature Display
```kotlin
// TemperatureDisplay.kt
@Composable
fun TemperatureDisplay(
    high: Double,
    low: Double,
    unit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeadlineText(
            text = "${high.toInt()}°${unit.symbol}",
            color = AtomicColors.OnSurface
        )
        BodyText(
            text = "Low ${low.toInt()}°${unit.symbol}",
            color = AtomicColors.OnSurface.copy(alpha = 0.6f)
        )
    }
}
```

#### 2. Weather Summary
```kotlin
// WeatherSummary.kt
@Composable
fun WeatherSummary(
    weather: Weather,
    modifier: Modifier = Modifier,
    showDate: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            if (showDate) {
                BodyText(
                    text = weather.date.dayOfWeek.getDisplayName(
                        TextStyle.FULL, 
                        Locale.getDefault()
                    ),
                    color = AtomicColors.Primary
                )
            }
            CaptionText(
                text = weather.description.replaceFirstChar { it.uppercase() },
                color = AtomicColors.OnSurface.copy(alpha = 0.6f)
            )
        }
        
        WeatherIcon(
            condition = weather.condition,
            size = 32.dp
        )
        
        TemperatureDisplay(
            high = weather.temperatureHigh,
            low = weather.temperatureLow,
            unit = TemperatureUnit.CELSIUS
        )
    }
}
```

### Organisms - Complex Components

#### 1. Weather List
```kotlin
// WeatherList.kt
@Composable
fun WeatherList(
    weatherList: List<Weather>,
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh
    )
    
    Box(
        modifier = modifier.pullRefresh(pullRefreshState)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(AtomicSpacing.MD),
            verticalArrangement = Arrangement.spacedBy(AtomicSpacing.SM)
        ) {
            items(
                items = weatherList,
                key = { it.date.toEpochDays() }
            ) { weather ->
                WeatherCard(
                    weather = weather,
                    onClick = { /* Handle click */ }
                )
            }
        }
        
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
```

## 🍎 iOS SwiftUI Implementation

### Atoms
```swift
// HeadlineText.swift
struct HeadlineText: View {
    let text: String
    var color: Color = .primary
    var alignment: TextAlignment = .leading
    
    var body: some View {
        Text(text)
            .font(.title2)
            .fontWeight(.semibold)
            .foregroundColor(color)
            .multilineTextAlignment(alignment)
    }
}

// PrimaryButton.swift
struct PrimaryButton: View {
    let title: String
    let action: () -> Void
    var isLoading: Bool = false
    var isEnabled: Bool = true
    
    var body: some View {
        Button(action: action) {
            HStack {
                if isLoading {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        .scaleEffect(0.8)
                } else {
                    Text(title)
                        .fontWeight(.medium)
                }
            }
            .frame(maxWidth: .infinity)
            .frame(height: 48)
            .background(Color.atomicPrimary)
            .foregroundColor(.white)
            .cornerRadius(8)
        }
        .disabled(!isEnabled || isLoading)
    }
}
```

### Molecules
```swift
// TemperatureDisplay.swift
struct TemperatureDisplay: View {
    let high: Double
    let low: Double
    let unit: TemperatureUnit
    
    var body: some View {
        VStack(alignment: .center, spacing: 2) {
            HeadlineText(
                text: "\(Int(high))°\(unit.symbol)",
                color: .primary
            )
            BodyText(
                text: "Low \(Int(low))°\(unit.symbol)",
                color: .secondary
            )
        }
    }
}
```

## 🎯 Design System Standards

### Industry Standards for Atomic Design

#### 1. **Material Design 3** (Android)
- **Component Library**: Material 3 components as atoms
- **Design Tokens**: Colors, typography, spacing from Material guidelines
- **Accessibility**: Built-in a11y support
- **Theming**: Dynamic color and typography theming

#### 2. **Apple Human Interface Guidelines** (iOS)
- **Native Components**: SF Symbols, system fonts, native controls
- **Adaptive Design**: Automatic dark/light mode support
- **Accessibility**: VoiceOver and accessibility trait support
- **Platform Integration**: Native look and feel

#### 3. **Cross-Platform Consistency**
- **Shared Design Language**: Same visual hierarchy and spacing
- **Semantic Naming**: Consistent component and color naming
- **Behavioral Consistency**: Same interactions and animations
- **Content Strategy**: Consistent copy and messaging

### Recommended Tools & Libraries

#### Documentation & Storybook Alternatives
- **Compose Previews**: Interactive component previews in Android Studio
- **SwiftUI Previews**: Live component previews in Xcode
- **Design System Documentation**: Markdown-based component docs
- **Figma Integration**: Design tokens sync with Figma

#### Testing Atomic Components
```kotlin
// Android - Component Testing
@Test
fun primaryButton_displaysCorrectText() {
    composeTestRule.setContent {
        PrimaryButton(
            text = "Test Button",
            onClick = {}
        )
    }
    
    composeTestRule
        .onNodeWithText("Test Button")
        .assertIsDisplayed()
}
```

## 📋 Updated PRD Addition

### Atomic Design System Integration

Add this section to your Weather App PRD:

```markdown
## 🧬 Atomic Design System

### Component Library Structure
- **Atoms**: 15+ reusable basic components
- **Molecules**: 8+ component combinations  
- **Organisms**: 5+ complex UI sections
- **Templates**: 3+ page layouts
- **Pages**: Complete screen implementations

### Design System Benefits
- **Consistency**: Same visual language across platforms
- **Reusability**: 90%+ component reuse within each platform
- **Maintainability**: Single source of truth for UI components
- **Scalability**: Easy to extend and modify design system
- **Developer Experience**: Clear component hierarchy and documentation

### Implementation Priority
1. Core atoms (Text, Button, Icon)
2. Weather-specific molecules (TemperatureDisplay, WeatherSummary)
3. List organisms (WeatherList, ErrorState)
4. Page templates and final screen assembly
```

This atomic design approach will give you:
- **Better consistency** than React Native's component libraries
- **Platform-native performance** while maintaining design consistency  
- **Type-safe components** with compile-time validation
- **Easier testing** with isolated component testing
- **Better developer experience** with hot reload and previews

The combination of KMP's business logic sharing + Atomic Design System gives you the best of both worlds: shared logic with platform-optimized, consistent UI components! 🚀