import SwiftUI
import shared

// MARK: - Caption Text Atomic Component
// Corresponds to Android CaptionText.kt implementation
// Small text for labels, metadata, and supplementary information

struct CaptionText: View {
    let text: String
    var color: Color = .atomicSecondaryLabel
    var alignment: TextAlignment = .leading
    var lineLimit: Int? = nil
    var weight: Font.Weight = .regular
    var isUppercase: Bool = false
    
    var body: some View {
        Text(processedText)
            .font(.atomicCaption.weight(weight))
            .foregroundColor(color)
            .multilineTextAlignment(alignment)
            .lineLimit(lineLimit)
    }
    
    private var processedText: String {
        isUppercase ? text.uppercased() : text
    }
}

// MARK: - Convenience Initializers
extension CaptionText {
    init(
        _ text: String,
        color: Color = .atomicSecondaryLabel,
        alignment: TextAlignment = .leading
    ) {
        self.text = text
        self.color = color
        self.alignment = alignment
    }
    
    init(
        _ text: String,
        weight: Font.Weight,
        color: Color = .atomicSecondaryLabel,
        alignment: TextAlignment = .leading
    ) {
        self.text = text
        self.weight = weight
        self.color = color
        self.alignment = alignment
    }
}

// MARK: - View Modifiers
extension CaptionText {
    func captionColor(_ color: Color) -> CaptionText {
        var copy = self
        copy.color = color
        return copy
    }
    
    func captionAlignment(_ alignment: TextAlignment) -> CaptionText {
        var copy = self
        copy.alignment = alignment
        return copy
    }
    
    func captionWeight(_ weight: Font.Weight) -> CaptionText {
        var copy = self
        copy.weight = weight
        return copy
    }
    
    func captionLineLimit(_ limit: Int?) -> CaptionText {
        var copy = self
        copy.lineLimit = limit
        return copy
    }
    
    func captionUppercase(_ uppercase: Bool = true) -> CaptionText {
        var copy = self
        copy.isUppercase = uppercase
        return copy
    }
}

// MARK: - Semantic Caption Variants
extension CaptionText {
    static func label(_ text: String) -> CaptionText {
        CaptionText(text, weight: .medium)
            .captionUppercase()
    }
    
    static func metadata(_ text: String) -> CaptionText {
        CaptionText(text, color: .atomicTertiaryLabel)
    }
    
    static func timestamp(_ text: String) -> CaptionText {
        CaptionText(text, color: .atomicSecondaryLabel)
    }
    
    static func status(_ text: String, color: Color) -> CaptionText {
        CaptionText(text, weight: .medium, color: color)
            .captionUppercase()
    }
    
    static func badge(_ text: String) -> CaptionText {
        CaptionText(text, weight: .medium, color: .atomicOnPrimary)
    }
}

// MARK: - Weather-Specific Caption Extensions
extension CaptionText {
    static func weatherCondition(_ condition: String) -> CaptionText {
        CaptionText(condition.capitalized, color: .atomicSecondaryLabel)
    }
    
    static func temperature(_ temp: Double, unit: String = "°C") -> CaptionText {
        CaptionText("\(Int(temp))\(unit)", weight: .medium)
    }
    
    static func humidity(_ percentage: Int) -> CaptionText {
        CaptionText("Humidity \(percentage)%", color: .atomicSecondaryLabel)
    }
    
    static func lastUpdated(_ time: String) -> CaptionText {
        CaptionText("Updated \(time)", color: .atomicTertiaryLabel)
    }
}

// MARK: - Preview Support
#Preview("Caption Text Variants") {
    VStack(alignment: .leading, spacing: AtomicSpacing.md) {
        CaptionText("Regular caption text")
        CaptionText("Medium weight caption", weight: .medium)
        CaptionText("Semibold caption", weight: .semibold)
        
        Divider()
        
        CaptionText.label("Section Label")
        CaptionText.metadata("Metadata info")
        CaptionText.timestamp("2 minutes ago")
        
        Divider()
        
        CaptionText.status("Active", color: .atomicSuccess)
        CaptionText.status("Warning", color: .atomicWarning)
        CaptionText.status("Error", color: .atomicError)
        
        Divider()
        
        // Weather-specific examples
        CaptionText.weatherCondition("partly cloudy")
        CaptionText.temperature(24.5)
        CaptionText.humidity(65)
        CaptionText.lastUpdated("just now")
        
        Divider()
        
        HStack {
            CaptionText.badge("NEW")
                .atomicPaddingXS(.horizontal)
                .atomicPaddingXS(.vertical)
                .background(Color.atomicPrimary)
                .atomicCornerRadiusFull()
            
            Spacer.atomicHorizontalSM()
            
            CaptionText.badge("HOT")
                .atomicPaddingXS(.horizontal)
                .atomicPaddingXS(.vertical)
                .background(Color.atomicError)
                .atomicCornerRadiusFull()
        }
        
        Divider()
        
        CaptionText("Uppercase caption example")
            .captionUppercase()
        
        CaptionText("Center aligned caption text")
            .captionAlignment(.center)
    }
    .atomicPadding()
    .background(Color.atomicSurface)
}