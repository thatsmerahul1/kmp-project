import SwiftUI

// MARK: - Atomic Spacing
// Design tokens for consistent spacing across the iOS app
// Matches Android AtomicSpacing.kt implementation

struct AtomicSpacing {
    
    // MARK: - Base Spacing Values
    static let none: CGFloat = 0
    static let xs: CGFloat = 4      // Extra Small
    static let sm: CGFloat = 8      // Small
    static let md: CGFloat = 16     // Medium (base unit)
    static let lg: CGFloat = 24     // Large
    static let xl: CGFloat = 32     // Extra Large
    static let xxl: CGFloat = 48    // Extra Extra Large
    
    // MARK: - Semantic Spacing
    static let padding = md         // Default padding
    static let margin = md          // Default margin
    static let gap = sm            // Default gap between elements
    static let section = xl        // Space between major sections
    
    // MARK: - Component Specific Spacing
    static let buttonPadding = CGFloat(12)
    static let buttonHeight = CGFloat(48)
    static let cardPadding = md
    static let listItemPadding = md
    static let iconSpacing = xs
    static let iconSize = CGFloat(24)
    static let iconSizeMedium = CGFloat(32)
    static let iconSizeLarge = CGFloat(48)
    static let iconSizeXL = CGFloat(64)
    
    // MARK: - Border and Divider Sizes
    static let borderWidth = CGFloat(1)
    static let dividerWidth = CGFloat(1)
    
    // MARK: - Touch Target Sizes
    static let minTouchTarget = CGFloat(48)
    static let touchTargetPadding = CGFloat(12)
    
    // MARK: - Grid System
    static let gridGutter = md
    static let gridMargin = md
    
    // MARK: - Safe Area Considerations
    static let safeAreaPadding = md
}

// MARK: - View Extensions for Spacing
extension View {
    
    // MARK: - Padding Extensions
    func atomicPadding(_ edges: Edge.Set = .all) -> some View {
        self.padding(edges, AtomicSpacing.padding)
    }
    
    func atomicPaddingXS(_ edges: Edge.Set = .all) -> some View {
        self.padding(edges, AtomicSpacing.xs)
    }
    
    func atomicPaddingSM(_ edges: Edge.Set = .all) -> some View {
        self.padding(edges, AtomicSpacing.sm)
    }
    
    func atomicPaddingMD(_ edges: Edge.Set = .all) -> some View {
        self.padding(edges, AtomicSpacing.md)
    }
    
    func atomicPaddingLG(_ edges: Edge.Set = .all) -> some View {
        self.padding(edges, AtomicSpacing.lg)
    }
    
    func atomicPaddingXL(_ edges: Edge.Set = .all) -> some View {
        self.padding(edges, AtomicSpacing.xl)
    }
    
    func atomicPaddingXXL(_ edges: Edge.Set = .all) -> some View {
        self.padding(edges, AtomicSpacing.xxl)
    }
    
    // MARK: - Margin Extensions (using spacers)
    func atomicMarginTop() -> some View {
        VStack(spacing: 0) {
            Spacer().frame(height: AtomicSpacing.margin)
            self
        }
    }
    
    func atomicMarginBottom() -> some View {
        VStack(spacing: 0) {
            self
            Spacer().frame(height: AtomicSpacing.margin)
        }
    }
    
    func atomicMarginLeading() -> some View {
        HStack(spacing: 0) {
            Spacer().frame(width: AtomicSpacing.margin)
            self
        }
    }
    
    func atomicMarginTrailing() -> some View {
        HStack(spacing: 0) {
            self
            Spacer().frame(width: AtomicSpacing.margin)
        }
    }
    
    // MARK: - Card Styling
    func atomicCard() -> some View {
        self
            .atomicPadding()
            .background(Color.atomicSurface)
            .cornerRadius(AtomicSpacing.sm)
            .shadow(radius: 2)
    }
    
    // MARK: - List Item Styling
    func atomicListItem() -> some View {
        self
            .atomicPaddingMD(.vertical)
            .atomicPaddingMD(.horizontal)
    }
}

// MARK: - Spacer Extensions
extension Spacer {
    
    static func atomicXS() -> some View {
        Spacer().frame(height: AtomicSpacing.xs)
    }
    
    static func atomicSM() -> some View {
        Spacer().frame(height: AtomicSpacing.sm)
    }
    
    static func atomicMD() -> some View {
        Spacer().frame(height: AtomicSpacing.md)
    }
    
    static func atomicLG() -> some View {
        Spacer().frame(height: AtomicSpacing.lg)
    }
    
    static func atomicXL() -> some View {
        Spacer().frame(height: AtomicSpacing.xl)
    }
    
    static func atomicXXL() -> some View {
        Spacer().frame(height: AtomicSpacing.xxl)
    }
    
    // Horizontal spacers
    static func atomicHorizontalXS() -> some View {
        Spacer().frame(width: AtomicSpacing.xs)
    }
    
    static func atomicHorizontalSM() -> some View {
        Spacer().frame(width: AtomicSpacing.sm)
    }
    
    static func atomicHorizontalMD() -> some View {
        Spacer().frame(width: AtomicSpacing.md)
    }
    
    static func atomicHorizontalLG() -> some View {
        Spacer().frame(width: AtomicSpacing.lg)
    }
}

// MARK: - Stack Extensions
extension VStack {
    static func atomicVStack<Content: View>(
        alignment: HorizontalAlignment = .center,
        @ViewBuilder content: @escaping () -> Content
    ) -> some View {
        VStack(alignment: alignment, spacing: AtomicSpacing.gap, content: content)
    }
}

extension HStack {
    static func atomicHStack<Content: View>(
        alignment: VerticalAlignment = .center,
        @ViewBuilder content: @escaping () -> Content
    ) -> some View {
        HStack(alignment: alignment, spacing: AtomicSpacing.gap, content: content)
    }
}