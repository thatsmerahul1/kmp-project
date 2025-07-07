import SwiftUI

// MARK: - Atomic Colors
// Design tokens for consistent color usage across the iOS app
// Matches Android AtomicColors.kt implementation

extension Color {
    // MARK: - Primary Colors
    static let atomicPrimary = Color(red: 0.098, green: 0.463, blue: 0.824) // #1976D2
    static let atomicPrimaryVariant = Color(red: 0.082, green: 0.396, blue: 0.753) // #1565C0
    static let atomicOnPrimary = Color.white
    
    // MARK: - Surface Colors
    static let atomicSurface = Color(red: 0.98, green: 0.98, blue: 0.98) // #FAFAFA
    static let atomicOnSurface = Color(red: 0.129, green: 0.129, blue: 0.129) // #212121
    static let atomicSurfaceVariant = Color(red: 0.961, green: 0.961, blue: 0.961) // #F5F5F5
    static let atomicOnSurfaceVariant = Color(red: 0.459, green: 0.459, blue: 0.459) // #757575
    static let atomicSurfaceContainer = Color(red: 0.953, green: 0.953, blue: 0.953) // #F3F3F3
    
    // MARK: - Secondary Colors
    static let atomicSecondary = Color(red: 0.012, green: 0.855, blue: 0.776) // #03DAC6
    static let atomicSecondaryContainer = Color(red: 0.302, green: 0.8, blue: 0.639) // #4ECCA3
    static let atomicOnSecondaryContainer = Color(red: 0.0, green: 0.216, blue: 0.208) // #003735
    
    // MARK: - Border Colors
    static let atomicOutline = Color(red: 0.792, green: 0.769, blue: 0.816) // #CAC4D0
    
    // MARK: - Semantic Colors
    static let atomicSuccess = Color(red: 0.298, green: 0.686, blue: 0.314) // #4CAF50
    static let atomicWarning = Color(red: 1.0, green: 0.596, blue: 0.0) // #FF9800
    static let atomicError = Color(red: 0.957, green: 0.263, blue: 0.212) // #F44336
    static let atomicInfo = Color(red: 0.129, green: 0.588, blue: 0.953) // #2196F3
    
    // MARK: - Weather Specific Colors
    static let atomicSunny = Color(red: 1.0, green: 0.835, blue: 0.310) // #FFD54F
    static let atomicCloudy = Color(red: 0.565, green: 0.643, blue: 0.682) // #90A4AE
    static let atomicRainy = Color(red: 0.259, green: 0.647, blue: 0.961) // #42A5F5
    static let atomicSnowy = Color(red: 0.882, green: 0.961, blue: 0.996) // #E1F5FE
    
    // MARK: - Adaptive Colors (Light/Dark mode support)
    static let atomicBackground = Color(UIColor.systemBackground)
    static let atomicSecondaryBackground = Color(UIColor.secondarySystemBackground)
    static let atomicTertiaryBackground = Color(UIColor.tertiarySystemBackground)
    
    static let atomicLabel = Color(UIColor.label)
    static let atomicSecondaryLabel = Color(UIColor.secondaryLabel)
    static let atomicTertiaryLabel = Color(UIColor.tertiaryLabel)
    
    // MARK: - Opacity Variants
    static let atomicOverlay = Color.black.opacity(0.6)
    static let atomicScrim = Color.black.opacity(0.32)
    static let atomicDivider = Color.gray.opacity(0.12)
}

// MARK: - Color Extensions for Weather Conditions
extension Color {
    static func forWeatherCondition(_ condition: WeatherCondition) -> Color {
        switch condition {
        case .clear:
            return .atomicSunny
        case .clouds:
            return .atomicCloudy
        case .rain:
            return .atomicRainy
        case .snow:
            return .atomicSnowy
        case .thunderstorm:
            return .atomicCloudy
        case .mist:
            return .atomicCloudy
        case .unknown:
            return .atomicCloudy
        @unknown default:
            return .atomicCloudy
        }
    }
}

// MARK: - Color Accessibility
extension Color {
    /// Returns appropriate text color for accessibility on this background
    var accessibleTextColor: Color {
        // Simplified accessibility check - in production, use proper luminance calculation
        return self == .atomicPrimary ? .atomicOnPrimary : .atomicOnSurface
    }
}