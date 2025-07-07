import SwiftUI
import shared

// MARK: - Display Text Atomic Component
// Corresponds to Android DisplayText.kt implementation
// Large, prominent text for hero content, titles, and key information

struct DisplayText: View {
    let text: String
    var color: Color = .atomicOnSurface
    var alignment: TextAlignment = .leading
    var lineLimit: Int? = nil
    var size: DisplaySize = .medium
    var weight: Font.Weight = .bold
    
    var body: some View {
        Text(text)
            .font(fontForSize.weight(weight))
            .foregroundColor(color)
            .multilineTextAlignment(alignment)
            .lineLimit(lineLimit)
    }
    
    private var fontForSize: Font {
        switch size {
        case .small:
            return .atomicDisplaySmall
        case .medium:
            return .atomicDisplayMedium
        case .large:
            return .atomicDisplayLarge
        }
    }
}

// MARK: - Display Size Variants
enum DisplaySize {
    case small   // 24pt
    case medium  // 28pt
    case large   // 32pt
}

// MARK: - Convenience Initializers
extension DisplayText {
    init(
        _ text: String,
        color: Color = .atomicOnSurface,
        alignment: TextAlignment = .leading
    ) {
        self.text = text
        self.color = color
        self.alignment = alignment
    }
    
    init(
        _ text: String,
        size: DisplaySize,
        color: Color = .atomicOnSurface,
        alignment: TextAlignment = .leading
    ) {
        self.text = text
        self.size = size
        self.color = color
        self.alignment = alignment
    }
    
    init(
        _ text: String,
        weight: Font.Weight,
        color: Color = .atomicOnSurface,
        alignment: TextAlignment = .leading
    ) {
        self.text = text
        self.weight = weight
        self.color = color
        self.alignment = alignment
    }
}

// MARK: - View Modifiers
extension DisplayText {
    func displayColor(_ color: Color) -> DisplayText {
        var copy = self
        copy.color = color
        return copy
    }
    
    func displayAlignment(_ alignment: TextAlignment) -> DisplayText {
        var copy = self
        copy.alignment = alignment
        return copy
    }
    
    func displaySize(_ size: DisplaySize) -> DisplayText {
        var copy = self
        copy.size = size
        return copy
    }
    
    func displayWeight(_ weight: Font.Weight) -> DisplayText {
        var copy = self
        copy.weight = weight
        return copy
    }
    
    func displayLineLimit(_ limit: Int?) -> DisplayText {
        var copy = self
        copy.lineLimit = limit
        return copy
    }
}

// MARK: - Semantic Display Variants
extension DisplayText {
    static func hero(_ text: String) -> DisplayText {
        DisplayText(text, size: .large)
            .displayAlignment(.center)
    }
    
    static func title(_ text: String) -> DisplayText {
        DisplayText(text, size: .medium)
    }
    
    static func subtitle(_ text: String) -> DisplayText {
        DisplayText(text, size: .small, weight: .semibold)
    }
    
    static func feature(_ text: String) -> DisplayText {
        DisplayText(text, size: .large, color: .atomicPrimary)
    }
    
    static func error(_ text: String) -> DisplayText {
        DisplayText(text, size: .medium, color: .atomicError)
    }
    
    static func success(_ text: String) -> DisplayText {
        DisplayText(text, size: .medium, color: .atomicSuccess)
    }
}

// MARK: - Weather-Specific Display Extensions
extension DisplayText {
    static func temperature(_ temp: Double, unit: String = "°") -> DisplayText {
        DisplayText("\(Int(temp))\(unit)", size: .large, weight: .bold)
    }
    
    static func locationName(_ location: String) -> DisplayText {
        DisplayText(location, size: .medium)
    }
    
    static func weatherSummary(_ summary: String) -> DisplayText {
        DisplayText(summary.capitalized, size: .small, weight: .medium)
    }
}

// MARK: - Preview Support
#Preview("Display Text Variants") {
    ScrollView {
        VStack(alignment: .leading, spacing: AtomicSpacing.lg) {
            // Size variants
            VStack(alignment: .leading, spacing: AtomicSpacing.md) {
                DisplayText("Large Display Text", size: .large)
                DisplayText("Medium Display Text", size: .medium)
                DisplayText("Small Display Text", size: .small)
            }
            
            Divider()
            
            // Weight variants
            VStack(alignment: .leading, spacing: AtomicSpacing.md) {
                DisplayText("Bold Display (Default)", weight: .bold)
                DisplayText("Heavy Display", weight: .heavy)
                DisplayText("Black Display", weight: .black)
                DisplayText("Semibold Display", weight: .semibold)
            }
            
            Divider()
            
            // Semantic variants
            VStack(alignment: .leading, spacing: AtomicSpacing.md) {
                DisplayText.hero("Hero Display Text")
                DisplayText.title("Title Display")
                DisplayText.subtitle("Subtitle Display")
                DisplayText.feature("Feature Display")
            }
            
            Divider()
            
            // Color variants
            VStack(alignment: .leading, spacing: AtomicSpacing.md) {
                DisplayText("Primary Display")
                    .displayColor(.atomicPrimary)
                
                DisplayText.success("Success Display")
                DisplayText.error("Error Display")
                
                DisplayText("Warning Display")
                    .displayColor(.atomicWarning)
            }
            
            Divider()
            
            // Weather-specific examples
            VStack(alignment: .leading, spacing: AtomicSpacing.md) {
                DisplayText.temperature(24)
                DisplayText.locationName("San Francisco")
                DisplayText.weatherSummary("partly cloudy with light rain")
            }
            
            Divider()
            
            // Alignment examples
            VStack(spacing: AtomicSpacing.md) {
                DisplayText("Left Aligned Display")
                    .displayAlignment(.leading)
                
                DisplayText("Center Aligned Display")
                    .displayAlignment(.center)
                
                DisplayText("Trailing Aligned Display")
                    .displayAlignment(.trailing)
            }
            
            Divider()
            
            // Line limit example
            DisplayText("This is a very long display text that demonstrates how the component handles multiple lines and line limits. It should be readable and maintain visual hierarchy.")
                .displayLineLimit(2)
        }
        .atomicPadding()
    }
    .background(Color.atomicSurface)
}