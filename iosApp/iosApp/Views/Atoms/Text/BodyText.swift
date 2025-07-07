import SwiftUI
import shared

// MARK: - Body Text Atomic Component
// Corresponds to Android BodyText.kt implementation
// Standard text for content, descriptions, and general reading

struct BodyText: View {
    let text: String
    var color: Color = .atomicOnSurface
    var alignment: TextAlignment = .leading
    var lineLimit: Int? = nil
    var size: BodySize = .medium
    var weight: Font.Weight = .regular
    
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
            return .atomicBodySmall
        case .medium:
            return .atomicBodyMedium
        case .large:
            return .atomicBodyLarge
        }
    }
}

// MARK: - Body Size Variants
enum BodySize {
    case small   // 12pt
    case medium  // 14pt
    case large   // 16pt
}

// MARK: - Convenience Initializers
extension BodyText {
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
        size: BodySize,
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
extension BodyText {
    func bodyColor(_ color: Color) -> BodyText {
        var copy = self
        copy.color = color
        return copy
    }
    
    func bodyAlignment(_ alignment: TextAlignment) -> BodyText {
        var copy = self
        copy.alignment = alignment
        return copy
    }
    
    func bodySize(_ size: BodySize) -> BodyText {
        var copy = self
        copy.size = size
        return copy
    }
    
    func bodyWeight(_ weight: Font.Weight) -> BodyText {
        var copy = self
        copy.weight = weight
        return copy
    }
    
    func bodyLineLimit(_ limit: Int?) -> BodyText {
        var copy = self
        copy.lineLimit = limit
        return copy
    }
}

// MARK: - Semantic Text Variants
extension BodyText {
    static func secondary(_ text: String) -> BodyText {
        BodyText(text, color: .atomicSecondaryLabel)
    }
    
    static func tertiary(_ text: String) -> BodyText {
        BodyText(text, color: .atomicTertiaryLabel)
    }
    
    static func emphasis(_ text: String) -> BodyText {
        BodyText(text, weight: .medium)
    }
    
    static func small(_ text: String) -> BodyText {
        BodyText(text, size: .small)
    }
    
    static func large(_ text: String) -> BodyText {
        BodyText(text, size: .large)
    }
}

// MARK: - Preview Support
#Preview("Body Text Variants") {
    VStack(alignment: .leading, spacing: AtomicSpacing.md) {
        BodyText("Large body text for main content", size: .large)
        BodyText("Medium body text for regular content", size: .medium)
        BodyText("Small body text for minor details", size: .small)
        
        Divider()
        
        BodyText("Regular weight body text")
        BodyText("Medium weight body text", weight: .medium)
        BodyText("Semibold weight body text", weight: .semibold)
        
        Divider()
        
        BodyText.secondary("Secondary body text")
        BodyText.tertiary("Tertiary body text")
        BodyText.emphasis("Emphasized body text")
        
        Divider()
        
        BodyText("Success message in body text")
            .bodyColor(.atomicSuccess)
        
        BodyText("Error message in body text")
            .bodyColor(.atomicError)
        
        BodyText("Warning message in body text")
            .bodyColor(.atomicWarning)
        
        Divider()
        
        BodyText("This is a long paragraph of body text that demonstrates how the component handles multiple lines of content. It should wrap naturally and maintain good readability across different screen sizes.")
            .bodyLineLimit(3)
        
        BodyText("Center aligned body text for special cases")
            .bodyAlignment(.center)
    }
    .atomicPadding()
    .background(Color.atomicSurface)
}