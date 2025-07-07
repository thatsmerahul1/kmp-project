import SwiftUI

// MARK: - Atomic Shapes
// Design tokens for consistent shapes and corner radius across the iOS app
// Matches Android AtomicShapes.kt implementation

struct AtomicShapes {
    
    // MARK: - Corner Radius Values
    static let none: CGFloat = 0
    static let xs: CGFloat = 4      // Extra Small corners
    static let sm: CGFloat = 8      // Small corners
    static let md: CGFloat = 12     // Medium corners (default)
    static let lg: CGFloat = 16     // Large corners
    static let xl: CGFloat = 24     // Extra Large corners
    static let full: CGFloat = 9999 // Fully rounded (pill shape)
    
    // MARK: - Component Specific Shapes
    static let button = sm          // Button corner radius
    static let card = md           // Card corner radius
    static let chip = full         // Chip/Tag corner radius
    static let dialog = lg         // Dialog/Modal corner radius
    static let sheet = xl          // Bottom sheet corner radius (top corners only)
    
    // MARK: - Border Widths
    static let borderThin: CGFloat = 0.5
    static let borderMedium: CGFloat = 1.0
    static let borderThick: CGFloat = 2.0
    
    // MARK: - Shadow Properties
    static let shadowRadius: CGFloat = 4
    static let shadowOpacity: CGFloat = 0.1
    static let shadowOffset = CGSize(width: 0, height: 2)
}

// MARK: - View Extensions for Shapes
extension View {
    
    // MARK: - Corner Radius Extensions
    func atomicCornerRadius(_ radius: CGFloat = AtomicShapes.md) -> some View {
        self.cornerRadius(radius)
    }
    
    func atomicCornerRadiusXS() -> some View {
        self.cornerRadius(AtomicShapes.xs)
    }
    
    func atomicCornerRadiusSM() -> some View {
        self.cornerRadius(AtomicShapes.sm)
    }
    
    func atomicCornerRadiusMD() -> some View {
        self.cornerRadius(AtomicShapes.md)
    }
    
    func atomicCornerRadiusLG() -> some View {
        self.cornerRadius(AtomicShapes.lg)
    }
    
    func atomicCornerRadiusXL() -> some View {
        self.cornerRadius(AtomicShapes.xl)
    }
    
    func atomicCornerRadiusFull() -> some View {
        self.cornerRadius(AtomicShapes.full)
    }
    
    // MARK: - Border Extensions
    func atomicBorder(
        _ color: Color = .atomicDivider,
        width: CGFloat = AtomicShapes.borderMedium
    ) -> some View {
        self.overlay(
            RoundedRectangle(cornerRadius: AtomicShapes.md)
                .stroke(color, lineWidth: width)
        )
    }
    
    func atomicBorderThin(_ color: Color = .atomicDivider) -> some View {
        self.atomicBorder(color, width: AtomicShapes.borderThin)
    }
    
    func atomicBorderThick(_ color: Color = .atomicDivider) -> some View {
        self.atomicBorder(color, width: AtomicShapes.borderThick)
    }
    
    // MARK: - Shadow Extensions
    func atomicShadow(
        radius: CGFloat = AtomicShapes.shadowRadius,
        opacity: CGFloat = AtomicShapes.shadowOpacity,
        offset: CGSize = AtomicShapes.shadowOffset
    ) -> some View {
        self.shadow(
            color: Color.black.opacity(opacity),
            radius: radius,
            x: offset.width,
            y: offset.height
        )
    }
    
    func atomicElevation(_ level: AtomicElevation = .medium) -> some View {
        switch level {
        case .none:
            return self.eraseToAnyView()
        case .low:
            return self.atomicShadow(radius: 2, opacity: 0.05, offset: CGSize(width: 0, height: 1))
                .eraseToAnyView()
        case .medium:
            return self.atomicShadow(radius: 4, opacity: 0.1, offset: CGSize(width: 0, height: 2))
                .eraseToAnyView()
        case .high:
            return self.atomicShadow(radius: 8, opacity: 0.15, offset: CGSize(width: 0, height: 4))
                .eraseToAnyView()
        }
    }
    
    // MARK: - Shape Combinations
    func atomicButtonShape() -> some View {
        self
            .atomicCornerRadius(AtomicShapes.button)
            .atomicElevation(.low)
    }
    
    func atomicCardShape() -> some View {
        self
            .atomicCornerRadius(AtomicShapes.card)
            .atomicElevation(.medium)
    }
    
    func atomicChipShape() -> some View {
        self
            .atomicCornerRadius(AtomicShapes.chip)
            .atomicBorderThin()
    }
    
    func atomicDialogShape() -> some View {
        self
            .atomicCornerRadius(AtomicShapes.dialog)
            .atomicElevation(.high)
    }
}

// MARK: - Elevation Levels
enum AtomicElevation {
    case none
    case low
    case medium
    case high
}

// MARK: - Helper Extensions
extension View {
    func eraseToAnyView() -> AnyView {
        AnyView(self)
    }
}

// MARK: - Custom Shapes
struct AtomicRoundedRectangle: Shape {
    let cornerRadius: CGFloat
    let corners: UIRectCorner
    
    init(cornerRadius: CGFloat, corners: UIRectCorner = .allCorners) {
        self.cornerRadius = cornerRadius
        self.corners = corners
    }
    
    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(
            roundedRect: rect,
            byRoundingCorners: corners,
            cornerRadii: CGSize(width: cornerRadius, height: cornerRadius)
        )
        return Path(path.cgPath)
    }
}

// MARK: - Custom Shape Extensions
extension View {
    func atomicClipShape(
        _ corners: UIRectCorner,
        radius: CGFloat = AtomicShapes.md
    ) -> some View {
        self.clipShape(AtomicRoundedRectangle(cornerRadius: radius, corners: corners))
    }
    
    func atomicTopCorners(radius: CGFloat = AtomicShapes.xl) -> some View {
        self.atomicClipShape([.topLeft, .topRight], radius: radius)
    }
    
    func atomicBottomCorners(radius: CGFloat = AtomicShapes.md) -> some View {
        self.atomicClipShape([.bottomLeft, .bottomRight], radius: radius)
    }
}