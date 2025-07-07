import SwiftUI
import shared

// MARK: - Headline Text Atomic Component
// Corresponds to Android HeadlineText.kt implementation
// Large, prominent text for headings and important titles

struct HeadlineText: View {
    let text: String
    var color: Color = .atomicOnSurface
    var alignment: TextAlignment = .leading
    var lineLimit: Int? = nil
    var size: HeadlineSize = .medium
    
    var body: some View {
        Text(text)
            .font(fontForSize)
            .foregroundColor(color)
            .multilineTextAlignment(alignment)
            .lineLimit(lineLimit)
    }
    
    private var fontForSize: Font {
        switch size {
        case .small:
            return .atomicHeadlineSmall
        case .medium:
            return .atomicHeadlineMedium
        case .large:
            return .atomicHeadlineLarge
        }
    }
}

// MARK: - Headline Size Variants
enum HeadlineSize {
    case small   // 18pt
    case medium  // 20pt
    case large   // 24pt
}

// MARK: - Convenience Initializers
extension HeadlineText {
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
        size: HeadlineSize,
        color: Color = .atomicOnSurface,
        alignment: TextAlignment = .leading
    ) {
        self.text = text
        self.size = size
        self.color = color
        self.alignment = alignment
    }
}

// MARK: - View Modifiers
extension HeadlineText {
    func headlineColor(_ color: Color) -> HeadlineText {
        var copy = self
        copy.color = color
        return copy
    }
    
    func headlineAlignment(_ alignment: TextAlignment) -> HeadlineText {
        var copy = self
        copy.alignment = alignment
        return copy
    }
    
    func headlineSize(_ size: HeadlineSize) -> HeadlineText {
        var copy = self
        copy.size = size
        return copy
    }
    
    func headlineLineLimit(_ limit: Int?) -> HeadlineText {
        var copy = self
        copy.lineLimit = limit
        return copy
    }
}

// MARK: - Preview Support
#Preview("Headline Text Variants") {
    VStack(alignment: .leading, spacing: AtomicSpacing.md) {
        HeadlineText("Large Headline", size: .large)
        HeadlineText("Medium Headline", size: .medium)
        HeadlineText("Small Headline", size: .small)
        
        Divider()
        
        HeadlineText("Primary Color Headline")
            .headlineColor(.atomicPrimary)
        
        HeadlineText("Success Color Headline")
            .headlineColor(.atomicSuccess)
        
        HeadlineText("Error Color Headline")
            .headlineColor(.atomicError)
        
        Divider()
        
        HeadlineText("Center Aligned Headline")
            .headlineAlignment(.center)
        
        HeadlineText("This is a very long headline that might wrap to multiple lines and we want to test how it behaves with line limits")
            .headlineLineLimit(2)
    }
    .atomicPadding()
    .background(Color.atomicSurface)
}