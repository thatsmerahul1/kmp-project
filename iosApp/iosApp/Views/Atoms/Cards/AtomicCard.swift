import SwiftUI

// MARK: - Atomic Card
// Basic card atom component - foundation for all card-based UI elements
// Matches Android AtomicCard.kt implementation

struct AtomicCard<Content: View>: View {
    
    // MARK: - Properties
    let content: Content
    let onClick: (() -> Void)?
    let enabled: Bool
    let cornerRadius: CGFloat
    let backgroundColor: Color
    let contentColor: Color
    let elevation: AtomicElevation
    let borderColor: Color?
    let borderWidth: CGFloat
    
    // MARK: - Initializer
    init(
        onClick: (() -> Void)? = nil,
        enabled: Bool = true,
        cornerRadius: CGFloat = AtomicShapes.card,
        backgroundColor: Color = .atomicSurface,
        contentColor: Color = .atomicOnSurface,
        elevation: AtomicElevation = .medium,
        borderColor: Color? = nil,
        borderWidth: CGFloat = 0,
        @ViewBuilder content: () -> Content
    ) {
        self.content = content()
        self.onClick = onClick
        self.enabled = enabled
        self.cornerRadius = cornerRadius
        self.backgroundColor = backgroundColor
        self.contentColor = contentColor
        self.elevation = elevation
        self.borderColor = borderColor
        self.borderWidth = borderWidth
    }
    
    // MARK: - Body
    var body: some View {
        VStack(spacing: 0) {
            content
                .foregroundColor(contentColor)
        }
        .frame(maxWidth: .infinity)
        .background(backgroundColor)
        .cornerRadius(cornerRadius)
        .atomicElevation(elevation)
        .overlay(
            RoundedRectangle(cornerRadius: cornerRadius)
                .stroke(borderColor ?? Color.clear, lineWidth: borderWidth)
        )
        .opacity(enabled ? 1.0 : 0.6)
        .scaleEffect(onClick != nil && enabled ? 1.0 : 1.0) // For potential press animation
        .onTapGesture {
            if enabled {
                onClick?()
            }
        }
    }
}

// MARK: - Atomic Outlined Card
struct AtomicOutlinedCard<Content: View>: View {
    
    // MARK: - Properties
    let content: Content
    let onClick: (() -> Void)?
    let enabled: Bool
    let cornerRadius: CGFloat
    let backgroundColor: Color
    let contentColor: Color
    let borderColor: Color
    
    // MARK: - Initializer
    init(
        onClick: (() -> Void)? = nil,
        enabled: Bool = true,
        cornerRadius: CGFloat = AtomicShapes.card,
        backgroundColor: Color = .atomicSurface,
        contentColor: Color = .atomicOnSurface,
        borderColor: Color = .atomicOutline,
        @ViewBuilder content: () -> Content
    ) {
        self.content = content()
        self.onClick = onClick
        self.enabled = enabled
        self.cornerRadius = cornerRadius
        self.backgroundColor = backgroundColor
        self.contentColor = contentColor
        self.borderColor = borderColor
    }
    
    // MARK: - Body
    var body: some View {
        AtomicCard(
            onClick: onClick,
            enabled: enabled,
            cornerRadius: cornerRadius,
            backgroundColor: backgroundColor,
            contentColor: contentColor,
            elevation: .none,
            borderColor: borderColor,
            borderWidth: AtomicShapes.borderMedium
        ) {
            content
        }
    }
}

// MARK: - Atomic Elevated Card
struct AtomicElevatedCard<Content: View>: View {
    
    // MARK: - Properties
    let content: Content
    let onClick: (() -> Void)?
    let enabled: Bool
    let cornerRadius: CGFloat
    let backgroundColor: Color
    let contentColor: Color
    
    // MARK: - Initializer
    init(
        onClick: (() -> Void)? = nil,
        enabled: Bool = true,
        cornerRadius: CGFloat = AtomicShapes.cardElevated,
        backgroundColor: Color = .atomicSurfaceContainer,
        contentColor: Color = .atomicOnSurface,
        @ViewBuilder content: () -> Content
    ) {
        self.content = content()
        self.onClick = onClick
        self.enabled = enabled
        self.cornerRadius = cornerRadius
        self.backgroundColor = backgroundColor
        self.contentColor = contentColor
    }
    
    // MARK: - Body
    var body: some View {
        AtomicCard(
            onClick: onClick,
            enabled: enabled,
            cornerRadius: cornerRadius,
            backgroundColor: backgroundColor,
            contentColor: contentColor,
            elevation: .high
        ) {
            content
        }
    }
}

// MARK: - Atomic Weather Card
// Specialized card for weather content
struct AtomicWeatherCard<Content: View>: View {
    
    // MARK: - Properties
    let content: Content
    let weatherCondition: WeatherCondition?
    let onClick: (() -> Void)?
    let enabled: Bool
    
    // MARK: - Initializer
    init(
        weatherCondition: WeatherCondition? = nil,
        onClick: (() -> Void)? = nil,
        enabled: Bool = true,
        @ViewBuilder content: () -> Content
    ) {
        self.content = content()
        self.weatherCondition = weatherCondition
        self.onClick = onClick
        self.enabled = enabled
    }
    
    // MARK: - Body
    var body: some View {
        AtomicCard(
            onClick: onClick,
            enabled: enabled,
            cornerRadius: AtomicShapes.weatherCard,
            backgroundColor: weatherCondition?.backgroundColor ?? .atomicSurface,
            contentColor: .atomicOnSurface,
            elevation: .medium
        ) {
            content
        }
    }
}

// MARK: - Weather Condition Background Extension
extension WeatherCondition {
    var backgroundColor: Color {
        switch self {
        case .clear:
            return .atomicSunny.opacity(0.1)
        case .clouds:
            return .atomicCloudy.opacity(0.1)
        case .rain:
            return .atomicRainy.opacity(0.1)
        case .snow:
            return .atomicSnowy
        case .thunderstorm:
            return .atomicCloudy.opacity(0.2)
        case .mist:
            return .atomicCloudy.opacity(0.05)
        case .unknown:
            return .atomicSurface
        @unknown default:
            return .atomicSurface
        }
    }
}

// MARK: - Preview
struct AtomicCard_Previews: PreviewProvider {
    static var previews: some View {
        ScrollView {
            VStack(spacing: AtomicSpacing.md) {
                AtomicCard {
                    Text("Basic Card")
                        .atomicPadding()
                }
                
                AtomicOutlinedCard(onClick: {
                    print("Outlined card tapped")
                }) {
                    VStack {
                        Text("Outlined Card")
                            .font(.headline)
                        Text("This card has a border")
                            .font(.caption)
                            .foregroundColor(.atomicOnSurfaceVariant)
                    }
                    .atomicPadding()
                }
                
                AtomicElevatedCard(onClick: {
                    print("Elevated card tapped")
                }) {
                    HStack {
                        Image(systemName: "star.fill")
                            .foregroundColor(.atomicPrimary)
                        VStack(alignment: .leading) {
                            Text("Elevated Card")
                                .font(.headline)
                            Text("Clickable with elevation")
                                .font(.caption)
                        }
                        Spacer()
                    }
                    .atomicPadding()
                }
                
                AtomicWeatherCard(
                    weatherCondition: .clear,
                    onClick: {
                        print("Weather card tapped")
                    }
                ) {
                    HStack {
                        Image(systemName: "sun.max.fill")
                            .foregroundColor(.atomicSunny)
                            .font(.title)
                        VStack(alignment: .leading) {
                            Text("Weather Card")
                                .font(.headline)
                            Text("Sunny conditions")
                                .font(.caption)
                        }
                        Spacer()
                    }
                    .atomicPadding()
                }
                
                AtomicCard(enabled: false) {
                    Text("Disabled Card")
                        .atomicPadding()
                }
            }
            .atomicPadding()
        }
        .previewLayout(.sizeThatFits)
    }
}