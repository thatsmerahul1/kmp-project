import SwiftUI

struct AtomicDesignSystem {
    
    // MARK: - Colors
    struct Colors {
        // Primary Colors
        static let primary = Color(red: 0.098, green: 0.463, blue: 0.824) // #1976D2
        static let primaryVariant = Color(red: 0.082, green: 0.396, blue: 0.753) // #1565C0
        static let onPrimary = Color.white
        
        // Surface Colors
        static let surface = Color(red: 0.98, green: 0.98, blue: 0.98) // #FAFAFA
        static let onSurface = Color(red: 0.129, green: 0.129, blue: 0.129) // #212121
        static let surfaceVariant = Color(red: 0.961, green: 0.961, blue: 0.961) // #F5F5F5
        static let onSurfaceVariant = Color(red: 0.459, green: 0.459, blue: 0.459) // #757575
        static let surfaceContainer = Color(red: 0.953, green: 0.953, blue: 0.953) // #F3F3F3
        
        // Card specific colors - consistent off-white like iOS
        static let weatherCardBackground = Color(red: 0.988, green: 0.988, blue: 0.988) // #FCFCFC
        
        // Background Colors
        static let background = Color.white
        static let onBackground = Color(red: 0.110, green: 0.106, blue: 0.122) // #1C1B1F
        
        // Semantic Colors
        static let success = Color(red: 0.298, green: 0.686, blue: 0.314) // #4CAF50
        static let warning = Color(red: 1.0, green: 0.596, blue: 0.0) // #FF9800
        static let error = Color(red: 0.957, green: 0.263, blue: 0.212) // #F44336
        static let info = Color(red: 0.129, green: 0.588, blue: 0.953) // #2196F3
        static let outline = Color(red: 0.792, green: 0.769, blue: 0.816) // #CAC4D0
        
        // Weather Specific Colors
        static let sunny = Color(red: 1.0, green: 0.835, blue: 0.310) // #FFD54F
        static let cloudy = Color(red: 0.565, green: 0.643, blue: 0.682) // #90A4AE
        static let rainy = Color(red: 0.259, green: 0.647, blue: 0.961) // #42A5F5
        static let snowy = Color(red: 0.882, green: 0.961, blue: 0.996) // #E1F5FE
        static let stormy = Color(red: 0.369, green: 0.208, blue: 0.694) // #5E35B1
        
        // Weather Gradients
        static let sunnyGradientStart = Color(red: 1.0, green: 0.651, blue: 0.149) // #FFA726
        static let sunnyGradientEnd = Color(red: 1.0, green: 0.922, blue: 0.231) // #FFEB3B
        
        static let cloudyGradientStart = Color(red: 0.925, green: 0.937, blue: 0.945) // #ECEFF1
        static let cloudyGradientEnd = Color(red: 0.565, green: 0.643, blue: 0.682) // #90A4AE
        
        static let rainyGradientStart = Color(red: 0.392, green: 0.710, blue: 0.965) // #64B5F6
        static let rainyGradientEnd = Color(red: 0.098, green: 0.463, blue: 0.824) // #1976D2
        
        static let stormyGradientStart = Color(red: 0.475, green: 0.525, blue: 0.796) // #7986CB
        static let stormyGradientEnd = Color(red: 0.188, green: 0.247, blue: 0.624) // #303F9F
        
        static let snowyGradientStart = Color(red: 0.890, green: 0.949, blue: 0.992) // #E3F2FD
        static let snowyGradientEnd = Color(red: 0.733, green: 0.871, blue: 0.984) // #BBDEFB
    }
    
    // MARK: - Spacing
    struct Spacing {
        static let none: CGFloat = 0
        static let xxs: CGFloat = 2     // Double Extra Small
        static let xs: CGFloat = 4      // Extra Small
        static let sm: CGFloat = 8      // Small
        static let md: CGFloat = 16     // Medium
        static let lg: CGFloat = 24     // Large
        static let xl: CGFloat = 32     // Extra Large
        static let xxl: CGFloat = 48    // Double Extra Large
        static let xxxl: CGFloat = 64   // Triple Extra Large
        
        // Component specific spacing
        static let cardPadding: CGFloat = md
        static let cardMargin: CGFloat = sm
        static let buttonPadding: CGFloat = md
        static let buttonHeight: CGFloat = 48
        static let iconSize: CGFloat = 24
        static let iconSizeMedium: CGFloat = 32
        static let iconSizeLarge: CGFloat = 48
        static let iconSizeXL: CGFloat = 64
        
        // Layout spacing
        static let screenPadding: CGFloat = md
        static let sectionSpacing: CGFloat = lg
        static let itemSpacing: CGFloat = sm
        static let listItemPadding: CGFloat = md
        
        // Weather specific spacing
        static let weatherCardPadding: CGFloat = md
        static let weatherCardSpacing: CGFloat = sm
        static let temperatureSpacing: CGFloat = xs
        static let weatherIconSpacing: CGFloat = sm
        
        // Border sizes
        static let borderWidth: CGFloat = 1
        static let borderWidthThin: CGFloat = 0.5
        
        // Weather card specific dimensions
        static let weatherCardDateWidth: CGFloat = 80
        static let weatherCardIconWidth: CGFloat = 80
    }
    
    // MARK: - Corner Radius
    struct CornerRadius {
        static let none: CGFloat = 0
        static let xs: CGFloat = 4
        static let sm: CGFloat = 8
        static let md: CGFloat = 12
        static let lg: CGFloat = 16
        static let xl: CGFloat = 24
        static let xxl: CGFloat = 32
        
        // Component specific corner radius
        static let button: CGFloat = xl        // 24
        static let card: CGFloat = lg          // 16
        static let cardElevated: CGFloat = lg  // 16
        static let weatherCard: CGFloat = lg   // 16
        static let textField: CGFloat = sm     // 8
        static let searchField: CGFloat = xl   // 24
    }
    
    // MARK: - Typography
    struct Typography {
        // Display Styles
        static let displayLarge = Font.system(size: 32, weight: .bold, design: .default)
        static let displayMedium = Font.system(size: 28, weight: .bold, design: .default)
        static let displaySmall = Font.system(size: 24, weight: .bold, design: .default)
        
        // Headline Styles
        static let headlineLarge = Font.system(size: 28, weight: .semibold, design: .default)
        static let headlineMedium = Font.system(size: 24, weight: .semibold, design: .default)
        static let headlineSmall = Font.system(size: 20, weight: .semibold, design: .default)
        
        // Title Styles
        static let titleLarge = Font.system(size: 18, weight: .medium, design: .default)
        static let titleMedium = Font.system(size: 16, weight: .medium, design: .default)
        static let titleSmall = Font.system(size: 14, weight: .medium, design: .default)
        
        // Body Styles
        static let bodyLarge = Font.system(size: 16, weight: .regular, design: .default)
        static let bodyMedium = Font.system(size: 14, weight: .regular, design: .default)
        static let bodySmall = Font.system(size: 12, weight: .regular, design: .default)
        
        // Label Styles
        static let labelLarge = Font.system(size: 14, weight: .medium, design: .default)
        static let labelMedium = Font.system(size: 12, weight: .medium, design: .default)
        static let labelSmall = Font.system(size: 10, weight: .medium, design: .default)
        
        // Weather Specific Typography
        static let temperatureLarge = Font.system(size: 48, weight: .thin, design: .default)
        static let temperatureMedium = Font.system(size: 24, weight: .regular, design: .default)
    }
    
    // MARK: - Helper Functions
    static func getWeatherGradient(for condition: WeatherCondition) -> LinearGradient {
        switch condition {
        case .clear:
            return LinearGradient(
                colors: [Colors.sunnyGradientStart.opacity(0.3), Colors.sunnyGradientEnd.opacity(0.2)],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
        case .clouds:
            return LinearGradient(
                colors: [Colors.cloudyGradientStart.opacity(0.3), Colors.cloudyGradientEnd.opacity(0.2)],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
        case .rain:
            return LinearGradient(
                colors: [Colors.rainyGradientStart.opacity(0.3), Colors.rainyGradientEnd.opacity(0.2)],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
        case .snow:
            return LinearGradient(
                colors: [Colors.snowyGradientStart.opacity(0.3), Colors.snowyGradientEnd.opacity(0.1)],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
        default:
            return LinearGradient(
                colors: [Colors.stormyGradientStart.opacity(0.3), Colors.stormyGradientEnd.opacity(0.2)],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
        }
    }
}