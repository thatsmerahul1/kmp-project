import SwiftUI
import shared

// MARK: - Weather Icon Atomic Component
// Corresponds to Android WeatherIcon.kt implementation
// Weather condition-specific icon display

struct WeatherIcon: View {
    let condition: WeatherCondition
    var size: IconSize = .medium
    var color: Color? = nil
    var showBackground: Bool = false
    var animated: Bool = false
    
    var body: some View {
        Image(systemName: systemImageName)
            .font(.system(size: sizeValue, weight: .medium))
            .foregroundColor(effectiveColor)
            .frame(width: containerSize, height: containerSize)
            .background(backgroundForCondition)
            .atomicCornerRadius(showBackground ? AtomicShapes.sm : 0)
            .rotationEffect(.degrees(animated && isRotatable ? 360 : 0))
            .animation(
                animated && isRotatable ? 
                    Animation.linear(duration: 8).repeatForever(autoreverses: false) : nil,
                value: animated
            )
    }
    
    // MARK: - Computed Properties
    private var systemImageName: String {
        switch condition {
        case .clear:
            return "sun.max.fill"
        case .clouds:
            return "cloud.fill"
        case .rain:
            return "cloud.rain.fill"
        case .snow:
            return "cloud.snow.fill"
        case .thunderstorm:
            return "cloud.bolt.rain.fill"
        case .mist:
            return "cloud.fog.fill"
        case .unknown:
            return "questionmark.circle.fill"
        @unknown default:
            return "questionmark.circle.fill"
        }
    }
    
    private var sizeValue: CGFloat {
        switch size {
        case .small:
            return 16
        case .medium:
            return 24
        case .large:
            return 32
        case .extraLarge:
            return 48
        }
    }
    
    private var containerSize: CGFloat {
        showBackground ? sizeValue + 16 : sizeValue
    }
    
    private var effectiveColor: Color {
        if let color = color {
            return color
        }
        return Color.forWeatherCondition(condition)
    }
    
    private var backgroundForCondition: Color {
        guard showBackground else { return .clear }
        return effectiveColor.opacity(0.1)
    }
    
    private var isRotatable: Bool {
        condition == .clear || condition == .thunderstorm
    }
}

// MARK: - Icon Size Enum
enum IconSize {
    case small      // 16pt
    case medium     // 24pt
    case large      // 32pt
    case extraLarge // 48pt
}

// MARK: - Convenience Initializers
extension WeatherIcon {
    init(
        _ condition: WeatherCondition,
        size: IconSize = .medium
    ) {
        self.condition = condition
        self.size = size
    }
    
    init(
        _ condition: WeatherCondition,
        size: IconSize = .medium,
        color: Color
    ) {
        self.condition = condition
        self.size = size
        self.color = color
    }
    
    init(
        _ condition: WeatherCondition,
        size: IconSize = .medium,
        showBackground: Bool
    ) {
        self.condition = condition
        self.size = size
        self.showBackground = showBackground
    }
}

// MARK: - View Modifiers
extension WeatherIcon {
    func iconSize(_ size: IconSize) -> WeatherIcon {
        var copy = self
        copy.size = size
        return copy
    }
    
    func iconColor(_ color: Color?) -> WeatherIcon {
        var copy = self
        copy.color = color
        return copy
    }
    
    func iconBackground(_ showBackground: Bool = true) -> WeatherIcon {
        var copy = self
        copy.showBackground = showBackground
        return copy
    }
    
    func iconAnimated(_ animated: Bool = true) -> WeatherIcon {
        var copy = self
        copy.animated = animated
        return copy
    }
}

// MARK: - Semantic Icon Variants
extension WeatherIcon {
    static func current(_ condition: WeatherCondition) -> WeatherIcon {
        WeatherIcon(condition, size: .large)
            .iconAnimated()
    }
    
    static func forecast(_ condition: WeatherCondition) -> WeatherIcon {
        WeatherIcon(condition, size: .medium)
    }
    
    static func summary(_ condition: WeatherCondition) -> WeatherIcon {
        WeatherIcon(condition, size: .small)
    }
    
    static func hero(_ condition: WeatherCondition) -> WeatherIcon {
        WeatherIcon(condition, size: .extraLarge)
            .iconAnimated()
            .iconBackground()
    }
}

// MARK: - Weather Icon Grid Component
struct WeatherIconGrid: View {
    let conditions: [WeatherCondition] = [
        .clear, .clouds, .rain, .snow, .thunderstorm, .mist, .unknown
    ]
    
    var iconSize: IconSize = .medium
    var showLabels: Bool = true
    
    var body: some View {
        LazyVGrid(columns: Array(repeating: GridItem(.flexible()), count: 4), spacing: AtomicSpacing.md) {
            ForEach(conditions, id: \.self) { condition in
                VStack(spacing: AtomicSpacing.xs) {
                    WeatherIcon(condition, size: iconSize)
                    
                    if showLabels {
                        CaptionText(condition.displayName)
                            .multilineTextAlignment(.center)
                    }
                }
                .atomicPaddingSM()
            }
        }
    }
}

// MARK: - WeatherCondition Display Name Extension
extension WeatherCondition {
    var displayName: String {
        switch self {
        case .clear:
            return "Clear"
        case .clouds:
            return "Cloudy"
        case .rain:
            return "Rainy"
        case .snow:
            return "Snowy"
        case .thunderstorm:
            return "Storm"
        case .mist:
            return "Misty"
        case .unknown:
            return "Unknown"
        @unknown default:
            return "Unknown"
        }
    }
}

// MARK: - Preview Support
#Preview("Weather Icon Variants") {
    ScrollView {
        VStack(spacing: AtomicSpacing.xl) {
            // Size variants
            VStack(spacing: AtomicSpacing.md) {
                Text("Size Variants").atomicHeadlineSmall()
                
                HStack(spacing: AtomicSpacing.lg) {
                    VStack {
                        WeatherIcon(.clear, size: .small)
                        CaptionText("Small")
                    }
                    
                    VStack {
                        WeatherIcon(.clear, size: .medium)
                        CaptionText("Medium")
                    }
                    
                    VStack {
                        WeatherIcon(.clear, size: .large)
                        CaptionText("Large")
                    }
                    
                    VStack {
                        WeatherIcon(.clear, size: .extraLarge)
                        CaptionText("Extra Large")
                    }
                }
            }
            
            Divider()
            
            // All weather conditions
            VStack(spacing: AtomicSpacing.md) {
                Text("Weather Conditions").atomicHeadlineSmall()
                WeatherIconGrid(iconSize: .medium)
            }
            
            Divider()
            
            // Style variants
            VStack(spacing: AtomicSpacing.md) {
                Text("Style Variants").atomicHeadlineSmall()
                
                HStack(spacing: AtomicSpacing.lg) {
                    VStack {
                        WeatherIcon(.rain, size: .large)
                        CaptionText("Default")
                    }
                    
                    VStack {
                        WeatherIcon(.rain, size: .large)
                            .iconBackground()
                        CaptionText("Background")
                    }
                    
                    VStack {
                        WeatherIcon(.rain, size: .large, color: .atomicPrimary)
                        CaptionText("Custom Color")
                    }
                    
                    VStack {
                        WeatherIcon(.clear, size: .large)
                            .iconAnimated()
                        CaptionText("Animated")
                    }
                }
            }
            
            Divider()
            
            // Semantic variants
            VStack(spacing: AtomicSpacing.md) {
                Text("Semantic Variants").atomicHeadlineSmall()
                
                HStack(spacing: AtomicSpacing.lg) {
                    VStack {
                        WeatherIcon.current(.clear)
                        CaptionText("Current")
                    }
                    
                    VStack {
                        WeatherIcon.forecast(.clouds)
                        CaptionText("Forecast")
                    }
                    
                    VStack {
                        WeatherIcon.summary(.rain)
                        CaptionText("Summary")
                    }
                    
                    VStack {
                        WeatherIcon.hero(.thunderstorm)
                        CaptionText("Hero")
                    }
                }
            }
        }
        .atomicPadding()
    }
    .background(Color.atomicSurface)
}