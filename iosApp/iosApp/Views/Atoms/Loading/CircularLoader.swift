import SwiftUI
import shared

// MARK: - Circular Loader Atomic Component
// Corresponds to Android CircularLoader.kt implementation
// Standard loading indicator for async operations

struct CircularLoader: View {
    var size: LoaderSize = .medium
    var color: Color = .atomicPrimary
    var lineWidth: CGFloat = 2.0
    var isAnimating: Bool = true
    
    var body: some View {
        ProgressView()
            .progressViewStyle(CircularProgressViewStyle(tint: color))
            .scaleEffect(scaleForSize)
            .opacity(isAnimating ? 1.0 : 0.0)
            .animation(.easeInOut(duration: 0.3), value: isAnimating)
    }
    
    private var scaleForSize: CGFloat {
        switch size {
        case .small:
            return 0.6
        case .medium:
            return 1.0
        case .large:
            return 1.4
        case .extraLarge:
            return 2.0
        }
    }
}

// MARK: - Loader Size Enum
enum LoaderSize {
    case small
    case medium
    case large
    case extraLarge
}

// MARK: - Convenience Initializers
extension CircularLoader {
    init(
        size: LoaderSize = .medium,
        color: Color = .atomicPrimary
    ) {
        self.size = size
        self.color = color
    }
    
    init(
        isAnimating: Bool,
        size: LoaderSize = .medium,
        color: Color = .atomicPrimary
    ) {
        self.isAnimating = isAnimating
        self.size = size
        self.color = color
    }
}

// MARK: - View Modifiers
extension CircularLoader {
    func loaderSize(_ size: LoaderSize) -> CircularLoader {
        var copy = self
        copy.size = size
        return copy
    }
    
    func loaderColor(_ color: Color) -> CircularLoader {
        var copy = self
        copy.color = color
        return copy
    }
    
    func loaderAnimating(_ animating: Bool) -> CircularLoader {
        var copy = self
        copy.isAnimating = animating
        return copy
    }
}

// MARK: - Semantic Loader Variants
extension CircularLoader {
    static func loading() -> CircularLoader {
        CircularLoader(size: .medium, color: .atomicPrimary)
    }
    
    static func refreshing() -> CircularLoader {
        CircularLoader(size: .small, color: .atomicSecondaryLabel)
    }
    
    static func processing() -> CircularLoader {
        CircularLoader(size: .large, color: .atomicPrimary)
    }
    
    static func small() -> CircularLoader {
        CircularLoader(size: .small, color: .atomicPrimary)
    }
    
    static func large() -> CircularLoader {
        CircularLoader(size: .large, color: .atomicPrimary)
    }
}

// MARK: - Loading State Wrapper
struct LoadingStateView<Content: View>: View {
    let isLoading: Bool
    let content: () -> Content
    var loaderSize: LoaderSize = .medium
    var loaderColor: Color = .atomicPrimary
    var overlayBackground: Color = Color.atomicOverlay
    
    var body: some View {
        ZStack {
            content()
                .disabled(isLoading)
                .blur(radius: isLoading ? 2 : 0)
            
            if isLoading {
                overlayBackground
                    .ignoresSafeArea()
                
                CircularLoader(size: loaderSize, color: loaderColor)
            }
        }
        .animation(.easeInOut(duration: 0.3), value: isLoading)
    }
}

// MARK: - View Extension for Loading State
extension View {
    func loadingState(
        _ isLoading: Bool,
        loaderSize: LoaderSize = .medium,
        loaderColor: Color = .atomicPrimary
    ) -> some View {
        LoadingStateView(
            isLoading: isLoading,
            content: { self },
            loaderSize: loaderSize,
            loaderColor: loaderColor
        )
    }
}

// MARK: - Preview Support
#Preview("Circular Loader Variants") {
    VStack(spacing: AtomicSpacing.xl) {
        // Size variants
        VStack(spacing: AtomicSpacing.md) {
            Text("Size Variants").atomicHeadlineSmall()
            
            HStack(spacing: AtomicSpacing.lg) {
                VStack {
                    CircularLoader.small()
                    CaptionText("Small")
                }
                
                VStack {
                    CircularLoader(size: .medium)
                    CaptionText("Medium")
                }
                
                VStack {
                    CircularLoader.large()
                    CaptionText("Large")
                }
                
                VStack {
                    CircularLoader(size: .extraLarge)
                    CaptionText("Extra Large")
                }
            }
        }
        
        Divider()
        
        // Color variants
        VStack(spacing: AtomicSpacing.md) {
            Text("Color Variants").atomicHeadlineSmall()
            
            HStack(spacing: AtomicSpacing.lg) {
                VStack {
                    CircularLoader(color: .atomicPrimary)
                    CaptionText("Primary")
                }
                
                VStack {
                    CircularLoader(color: .atomicSuccess)
                    CaptionText("Success")
                }
                
                VStack {
                    CircularLoader(color: .atomicWarning)
                    CaptionText("Warning")
                }
                
                VStack {
                    CircularLoader(color: .atomicError)
                    CaptionText("Error")
                }
            }
        }
        
        Divider()
        
        // Semantic variants
        VStack(spacing: AtomicSpacing.md) {
            Text("Semantic Variants").atomicHeadlineSmall()
            
            HStack(spacing: AtomicSpacing.lg) {
                VStack {
                    CircularLoader.loading()
                    CaptionText("Loading")
                }
                
                VStack {
                    CircularLoader.refreshing()
                    CaptionText("Refreshing")
                }
                
                VStack {
                    CircularLoader.processing()
                    CaptionText("Processing")
                }
            }
        }
        
        Divider()
        
        // Loading state example
        VStack(spacing: AtomicSpacing.md) {
            Text("Loading State Example").atomicHeadlineSmall()
            
            VStack {
                BodyText("This content can be shown with a loading overlay")
                PrimaryButton("Sample Button") {}
            }
            .atomicCard()
            .loadingState(true)
        }
    }
    .atomicPadding()
    .background(Color.atomicSurface)
}