import SwiftUI

// MARK: - Atomic Typography
// Design tokens for consistent typography across the iOS app
// Matches Android AtomicTypography.kt implementation

struct AtomicTypography {
    
    // MARK: - Display Styles
    static let displayLarge = Font.system(size: 32, weight: .bold, design: .default)
    static let displayMedium = Font.system(size: 28, weight: .bold, design: .default)
    static let displaySmall = Font.system(size: 24, weight: .bold, design: .default)
    
    // MARK: - Headline Styles
    static let headlineLarge = Font.system(size: 24, weight: .semibold, design: .default)
    static let headlineMedium = Font.system(size: 20, weight: .semibold, design: .default)
    static let headlineSmall = Font.system(size: 18, weight: .semibold, design: .default)
    
    // MARK: - Title Styles
    static let titleLarge = Font.system(size: 18, weight: .medium, design: .default)
    static let titleMedium = Font.system(size: 16, weight: .medium, design: .default)
    static let titleSmall = Font.system(size: 14, weight: .medium, design: .default)
    
    // MARK: - Body Styles
    static let bodyLarge = Font.system(size: 16, weight: .regular, design: .default)
    static let bodyMedium = Font.system(size: 14, weight: .regular, design: .default)
    static let bodySmall = Font.system(size: 12, weight: .regular, design: .default)
    
    // MARK: - Label Styles
    static let labelLarge = Font.system(size: 14, weight: .medium, design: .default)
    static let labelMedium = Font.system(size: 12, weight: .medium, design: .default)
    static let labelSmall = Font.system(size: 10, weight: .medium, design: .default)
    
    // MARK: - Caption Styles
    static let caption = Font.system(size: 12, weight: .regular, design: .default)
    static let overline = Font.system(size: 10, weight: .medium, design: .default)
}

// MARK: - Font Extensions
extension Font {
    // MARK: - Atomic Font Styles (matching Android implementation)
    static let atomicDisplayLarge = AtomicTypography.displayLarge
    static let atomicDisplayMedium = AtomicTypography.displayMedium
    static let atomicDisplaySmall = AtomicTypography.displaySmall
    
    static let atomicHeadlineLarge = AtomicTypography.headlineLarge
    static let atomicHeadlineMedium = AtomicTypography.headlineMedium
    static let atomicHeadlineSmall = AtomicTypography.headlineSmall
    
    static let atomicTitleLarge = AtomicTypography.titleLarge
    static let atomicTitleMedium = AtomicTypography.titleMedium
    static let atomicTitleSmall = AtomicTypography.titleSmall
    
    static let atomicBodyLarge = AtomicTypography.bodyLarge
    static let atomicBodyMedium = AtomicTypography.bodyMedium
    static let atomicBodySmall = AtomicTypography.bodySmall
    
    static let atomicLabelLarge = AtomicTypography.labelLarge
    static let atomicLabelMedium = AtomicTypography.labelMedium
    static let atomicLabelSmall = AtomicTypography.labelSmall
    
    static let atomicCaption = AtomicTypography.caption
    static let atomicOverline = AtomicTypography.overline
}

// MARK: - Text Style Modifiers
extension Text {
    // MARK: - Display Modifiers
    func atomicDisplayLarge(_ color: Color = .atomicOnSurface) -> some View {
        self
            .font(.atomicDisplayLarge)
            .foregroundColor(color)
    }
    
    func atomicDisplayMedium(_ color: Color = .atomicOnSurface) -> some View {
        self
            .font(.atomicDisplayMedium)
            .foregroundColor(color)
    }
    
    func atomicDisplaySmall(_ color: Color = .atomicOnSurface) -> some View {
        self
            .font(.atomicDisplaySmall)
            .foregroundColor(color)
    }
    
    // MARK: - Headline Modifiers
    func atomicHeadlineLarge(_ color: Color = .atomicOnSurface) -> some View {
        self
            .font(.atomicHeadlineLarge)
            .foregroundColor(color)
    }
    
    func atomicHeadlineMedium(_ color: Color = .atomicOnSurface) -> some View {
        self
            .font(.atomicHeadlineMedium)
            .foregroundColor(color)
    }
    
    func atomicHeadlineSmall(_ color: Color = .atomicOnSurface) -> some View {
        self
            .font(.atomicHeadlineSmall)
            .foregroundColor(color)
    }
    
    // MARK: - Body Modifiers
    func atomicBodyLarge(_ color: Color = .atomicOnSurface) -> some View {
        self
            .font(.atomicBodyLarge)
            .foregroundColor(color)
    }
    
    func atomicBodyMedium(_ color: Color = .atomicOnSurface) -> some View {
        self
            .font(.atomicBodyMedium)
            .foregroundColor(color)
    }
    
    func atomicBodySmall(_ color: Color = .atomicOnSurface) -> some View {
        self
            .font(.atomicBodySmall)
            .foregroundColor(color)
    }
    
    // MARK: - Label Modifiers
    func atomicLabelMedium(_ color: Color = .atomicOnSurface) -> some View {
        self
            .font(.atomicLabelMedium)
            .foregroundColor(color)
    }
    
    func atomicCaption(_ color: Color = .atomicSecondaryLabel) -> some View {
        self
            .font(.atomicCaption)
            .foregroundColor(color)
    }
}

// MARK: - Line Height Extensions
extension Text {
    func lineHeight(_ height: CGFloat) -> some View {
        self.lineSpacing(height - UIFont.systemFont(ofSize: 16).lineHeight)
    }
}