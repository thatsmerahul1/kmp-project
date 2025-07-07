import SwiftUI
import shared

// MARK: - Primary Button Atomic Component
// Corresponds to Android PrimaryButton.kt implementation
// Main call-to-action button with prominent styling

struct PrimaryButton: View {
    let title: String
    let action: () -> Void
    var isEnabled: Bool = true
    var isLoading: Bool = false
    var size: ButtonSize = .medium
    var fullWidth: Bool = false
    
    var body: some View {
        Button(action: isEnabled && !isLoading ? action : {}) {
            HStack(spacing: AtomicSpacing.xs) {
                if isLoading {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .atomicOnPrimary))
                        .scaleEffect(0.8)
                } else {
                    Text(title)
                        .font(fontForSize)
                        .fontWeight(.medium)
                        .foregroundColor(.atomicOnPrimary)
                }
            }
            .frame(minWidth: fullWidth ? 0 : minWidthForSize)
            .frame(maxWidth: fullWidth ? .infinity : nil)
            .frame(height: heightForSize)
            .background(backgroundColorForState)
            .atomicButtonShape()
            .opacity(opacityForState)
        }
        .disabled(!isEnabled || isLoading)
        .animation(.easeInOut(duration: 0.2), value: isEnabled)
        .animation(.easeInOut(duration: 0.2), value: isLoading)
    }
    
    // MARK: - Computed Properties
    private var fontForSize: Font {
        switch size {
        case .small:
            return .atomicLabelSmall
        case .medium:
            return .atomicLabelMedium
        case .large:
            return .atomicLabelLarge
        }
    }
    
    private var heightForSize: CGFloat {
        switch size {
        case .small:
            return 36
        case .medium:
            return 44
        case .large:
            return 52
        }
    }
    
    private var minWidthForSize: CGFloat {
        switch size {
        case .small:
            return 80
        case .medium:
            return 120
        case .large:
            return 160
        }
    }
    
    private var backgroundColorForState: Color {
        if !isEnabled {
            return .atomicPrimary.opacity(0.3)
        }
        return .atomicPrimary
    }
    
    private var opacityForState: Double {
        if !isEnabled {
            return 0.6
        }
        return 1.0
    }
}

// MARK: - Button Size Enum
enum ButtonSize {
    case small
    case medium
    case large
}

// MARK: - Convenience Initializers
extension PrimaryButton {
    init(
        _ title: String,
        action: @escaping () -> Void
    ) {
        self.title = title
        self.action = action
    }
    
    init(
        _ title: String,
        size: ButtonSize,
        action: @escaping () -> Void
    ) {
        self.title = title
        self.size = size
        self.action = action
    }
    
    init(
        _ title: String,
        isEnabled: Bool,
        action: @escaping () -> Void
    ) {
        self.title = title
        self.isEnabled = isEnabled
        self.action = action
    }
    
    init(
        _ title: String,
        isLoading: Bool,
        action: @escaping () -> Void
    ) {
        self.title = title
        self.isLoading = isLoading
        self.action = action
    }
}

// MARK: - View Modifiers
extension PrimaryButton {
    func buttonSize(_ size: ButtonSize) -> PrimaryButton {
        var copy = self
        copy.size = size
        return copy
    }
    
    func buttonEnabled(_ enabled: Bool) -> PrimaryButton {
        var copy = self
        copy.isEnabled = enabled
        return copy
    }
    
    func buttonLoading(_ loading: Bool) -> PrimaryButton {
        var copy = self
        copy.isLoading = loading
        return copy
    }
    
    func buttonFullWidth(_ fullWidth: Bool = true) -> PrimaryButton {
        var copy = self
        copy.fullWidth = fullWidth
        return copy
    }
}

// MARK: - Semantic Button Variants
extension PrimaryButton {
    static func save(action: @escaping () -> Void) -> PrimaryButton {
        PrimaryButton("Save", action: action)
    }
    
    static func submit(action: @escaping () -> Void) -> PrimaryButton {
        PrimaryButton("Submit", action: action)
    }
    
    static func confirm(action: @escaping () -> Void) -> PrimaryButton {
        PrimaryButton("Confirm", action: action)
    }
    
    static func retry(action: @escaping () -> Void) -> PrimaryButton {
        PrimaryButton("Retry", action: action)
    }
    
    static func refresh(isLoading: Bool = false, action: @escaping () -> Void) -> PrimaryButton {
        PrimaryButton("Refresh", isLoading: isLoading, action: action)
    }
    
    static func loadMore(isLoading: Bool = false, action: @escaping () -> Void) -> PrimaryButton {
        PrimaryButton("Load More", isLoading: isLoading, action: action)
    }
}

// MARK: - Preview Support
#Preview("Primary Button Variants") {
    ScrollView {
        VStack(spacing: AtomicSpacing.lg) {
            // Size variants
            VStack(spacing: AtomicSpacing.md) {
                Text("Size Variants").atomicHeadlineSmall()
                
                PrimaryButton("Small Button", size: .small) {}
                PrimaryButton("Medium Button", size: .medium) {}
                PrimaryButton("Large Button", size: .large) {}
            }
            
            Divider()
            
            // State variants
            VStack(spacing: AtomicSpacing.md) {
                Text("State Variants").atomicHeadlineSmall()
                
                PrimaryButton("Enabled Button") {}
                PrimaryButton("Disabled Button", isEnabled: false) {}
                PrimaryButton("Loading Button", isLoading: true) {}
            }
            
            Divider()
            
            // Width variants
            VStack(spacing: AtomicSpacing.md) {
                Text("Width Variants").atomicHeadlineSmall()
                
                PrimaryButton("Regular Width") {}
                PrimaryButton("Full Width")
                    .buttonFullWidth()
            }
            
            Divider()
            
            // Semantic variants
            VStack(spacing: AtomicSpacing.md) {
                Text("Semantic Variants").atomicHeadlineSmall()
                
                PrimaryButton.save {}
                PrimaryButton.submit {}
                PrimaryButton.confirm {}
                PrimaryButton.retry {}
                PrimaryButton.refresh {}
                PrimaryButton.refresh(isLoading: true) {}
            }
            
            Divider()
            
            // Combined examples
            VStack(spacing: AtomicSpacing.md) {
                Text("Combined Examples").atomicHeadlineSmall()
                
                PrimaryButton("Small Disabled", size: .small, isEnabled: false) {}
                PrimaryButton("Large Loading", size: .large, isLoading: true) {}
                PrimaryButton("Full Width Save")
                    .buttonFullWidth()
                    .buttonSize(.large)
            }
        }
        .atomicPadding()
    }
    .background(Color.atomicSurface)
}