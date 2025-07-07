import SwiftUI

// MARK: - Atomic Navigation Bar
// Top navigation bar atom component
// Matches Android AtomicTopBar.kt implementation

struct AtomicNavigationBar: View {
    
    // MARK: - Properties
    let title: String
    let backAction: (() -> Void)?
    let trailingActions: [NavigationAction]
    let centered: Boolean
    let backgroundColor: Color
    let foregroundColor: Color
    
    // MARK: - Navigation Action
    struct NavigationAction {
        let icon: String
        let action: () -> Void
        let accessibilityLabel: String
        
        init(icon: String, accessibilityLabel: String, action: @escaping () -> Void) {
            self.icon = icon
            self.accessibilityLabel = accessibilityLabel
            self.action = action
        }
    }
    
    // MARK: - Initializer
    init(
        title: String,
        backAction: (() -> Void)? = nil,
        trailingActions: [NavigationAction] = [],
        centered: Boolean = false,
        backgroundColor: Color = .atomicSurface,
        foregroundColor: Color = .atomicOnSurface
    ) {
        self.title = title
        self.backAction = backAction
        self.trailingActions = trailingActions
        self.centered = centered
        self.backgroundColor = backgroundColor
        self.foregroundColor = foregroundColor
    }
    
    // MARK: - Body
    var body: some View {
        HStack(spacing: AtomicSpacing.sm) {
            // Leading - Back Button
            if let backAction = backAction {
                Button(action: backAction) {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 18, weight: .semibold))
                        .foregroundColor(foregroundColor)
                        .frame(width: AtomicSpacing.minTouchTarget, height: AtomicSpacing.minTouchTarget)
                }
                .buttonStyle(PlainButtonStyle())
                .accessibilityLabel("Navigate back")
            } else if !centered {
                Spacer()
                    .frame(width: AtomicSpacing.minTouchTarget)
            }
            
            // Center - Title
            if centered {
                Spacer()
            }
            
            Text(title)
                .font(.system(size: 20, weight: .semibold))
                .foregroundColor(foregroundColor)
                .lineLimit(1)
                .truncationMode(.tail)
            
            if centered {
                Spacer()
            } else {
                Spacer()
            }
            
            // Trailing - Actions
            HStack(spacing: AtomicSpacing.xs) {
                ForEach(Array(trailingActions.enumerated()), id: \.offset) { index, action in
                    Button(action: action.action) {
                        Image(systemName: action.icon)
                            .font(.system(size: 18, weight: .medium))
                            .foregroundColor(foregroundColor)
                            .frame(width: AtomicSpacing.minTouchTarget, height: AtomicSpacing.minTouchTarget)
                    }
                    .buttonStyle(PlainButtonStyle())
                    .accessibilityLabel(action.accessibilityLabel)
                }
            }
            .frame(minWidth: AtomicSpacing.minTouchTarget)
        }
        .frame(height: 56) // Standard navigation bar height
        .atomicPaddingMD(.horizontal)
        .background(backgroundColor)
    }
}

// MARK: - Atomic Tab Bar
// Bottom tab navigation bar atom component
// Matches Android AtomicBottomBar.kt implementation

struct AtomicTabBar: View {
    
    // MARK: - Properties
    @Binding var selectedTab: String
    let tabs: [TabItem]
    let backgroundColor: Color
    let selectedColor: Color
    let unselectedColor: Color
    
    // MARK: - Tab Item
    struct TabItem {
        let id: String
        let title: String
        let icon: String
        let selectedIcon: String?
        let badgeCount: Int?
        
        init(
            id: String,
            title: String,
            icon: String,
            selectedIcon: String? = nil,
            badgeCount: Int? = nil
        ) {
            self.id = id
            self.title = title
            self.icon = icon
            self.selectedIcon = selectedIcon
            self.badgeCount = badgeCount
        }
    }
    
    // MARK: - Initializer
    init(
        selectedTab: Binding<String>,
        tabs: [TabItem],
        backgroundColor: Color = .atomicSurface,
        selectedColor: Color = .atomicPrimary,
        unselectedColor: Color = .atomicOnSurfaceVariant
    ) {
        self._selectedTab = selectedTab
        self.tabs = tabs
        self.backgroundColor = backgroundColor
        self.selectedColor = selectedColor
        self.unselectedColor = unselectedColor
    }
    
    // MARK: - Body
    var body: some View {
        HStack(spacing: 0) {
            ForEach(tabs, id: \.id) { tab in
                Button(action: {
                    selectedTab = tab.id
                }) {
                    VStack(spacing: AtomicSpacing.xs) {
                        // Icon with Badge
                        ZStack {
                            Image(systemName: selectedTab == tab.id ? (tab.selectedIcon ?? tab.icon) : tab.icon)
                                .font(.system(size: 20, weight: selectedTab == tab.id ? .semibold : .medium))
                                .foregroundColor(selectedTab == tab.id ? selectedColor : unselectedColor)
                            
                            // Badge
                            if let badgeCount = tab.badgeCount, badgeCount > 0 {
                                Text("\(badgeCount)")
                                    .font(.system(size: 10, weight: .bold))
                                    .foregroundColor(.white)
                                    .frame(minWidth: 16, minHeight: 16)
                                    .background(Color.atomicError)
                                    .cornerRadius(8)
                                    .offset(x: 12, y: -8)
                            }
                        }
                        
                        // Title
                        Text(tab.title)
                            .font(.system(size: 12, weight: .medium))
                            .foregroundColor(selectedTab == tab.id ? selectedColor : unselectedColor)
                            .lineLimit(1)
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 50)
                }
                .buttonStyle(PlainButtonStyle())
                .accessibilityLabel(tab.title)
                .animation(.easeInOut(duration: 0.2), value: selectedTab)
            }
        }
        .atomicPaddingSM(.vertical)
        .background(backgroundColor)
        .atomicElevation(.low)
    }
}

// MARK: - Navigation Rail (Side Navigation)
// Side navigation for larger screens
struct AtomicNavigationRail: View {
    
    // MARK: - Properties
    @Binding var selectedItem: String
    let items: [NavigationRailItem]
    let header: AnyView?
    let footer: AnyView?
    let backgroundColor: Color
    let selectedColor: Color
    let unselectedColor: Color
    
    // MARK: - Navigation Rail Item
    struct NavigationRailItem {
        let id: String
        let icon: String
        let selectedIcon: String?
        let label: String?
        let badgeCount: Int?
        
        init(
            id: String,
            icon: String,
            selectedIcon: String? = nil,
            label: String? = nil,
            badgeCount: Int? = nil
        ) {
            self.id = id
            self.icon = icon
            self.selectedIcon = selectedIcon
            self.label = label
            self.badgeCount = badgeCount
        }
    }
    
    // MARK: - Initializer
    init(
        selectedItem: Binding<String>,
        items: [NavigationRailItem],
        header: AnyView? = nil,
        footer: AnyView? = nil,
        backgroundColor: Color = .atomicSurface,
        selectedColor: Color = .atomicPrimary,
        unselectedColor: Color = .atomicOnSurfaceVariant
    ) {
        self._selectedItem = selectedItem
        self.items = items
        self.header = header
        self.footer = footer
        self.backgroundColor = backgroundColor
        self.selectedColor = selectedColor
        self.unselectedColor = unselectedColor
    }
    
    // MARK: - Body
    var body: some View {
        VStack(spacing: AtomicSpacing.md) {
            // Header
            if let header = header {
                header
            }
            
            // Navigation Items
            VStack(spacing: AtomicSpacing.sm) {
                ForEach(items, id: \.id) { item in
                    Button(action: {
                        selectedItem = item.id
                    }) {
                        VStack(spacing: AtomicSpacing.xs) {
                            // Icon with Badge
                            ZStack {
                                Image(systemName: selectedItem == item.id ? (item.selectedIcon ?? item.icon) : item.icon)
                                    .font(.system(size: 24, weight: selectedItem == item.id ? .semibold : .medium))
                                    .foregroundColor(selectedItem == item.id ? selectedColor : unselectedColor)
                                
                                // Badge
                                if let badgeCount = item.badgeCount, badgeCount > 0 {
                                    Text("\(badgeCount)")
                                        .font(.system(size: 10, weight: .bold))
                                        .foregroundColor(.white)
                                        .frame(minWidth: 16, minHeight: 16)
                                        .background(Color.atomicError)
                                        .cornerRadius(8)
                                        .offset(x: 16, y: -12)
                                }
                            }
                            
                            // Label
                            if let label = item.label {
                                Text(label)
                                    .font(.system(size: 12, weight: .medium))
                                    .foregroundColor(selectedItem == item.id ? selectedColor : unselectedColor)
                                    .lineLimit(1)
                            }
                        }
                        .frame(width: 56, height: 56)
                        .background(
                            selectedItem == item.id ?
                                selectedColor.opacity(0.12) :
                                Color.clear
                        )
                        .cornerRadius(AtomicShapes.navigationDrawerItem)
                    }
                    .buttonStyle(PlainButtonStyle())
                    .accessibilityLabel(item.label ?? item.id)
                    .animation(.easeInOut(duration: 0.2), value: selectedItem)
                }
            }
            
            Spacer()
            
            // Footer
            if let footer = footer {
                footer
            }
        }
        .frame(width: 80)
        .frame(maxHeight: .infinity)
        .atomicPaddingMD(.vertical)
        .background(backgroundColor)
        .atomicElevation(.low)
    }
}

// MARK: - Preview
struct AtomicNavigationBar_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 0) {
            AtomicNavigationBar(
                title: "Weather Details",
                backAction: { print("Back tapped") },
                trailingActions: [
                    AtomicNavigationBar.NavigationAction(
                        icon: "heart",
                        accessibilityLabel: "Favorite"
                    ) { print("Favorite tapped") },
                    AtomicNavigationBar.NavigationAction(
                        icon: "square.and.arrow.up",
                        accessibilityLabel: "Share"
                    ) { print("Share tapped") }
                ],
                centered: true
            )
            
            Spacer()
            
            AtomicTabBar(
                selectedTab: .constant("home"),
                tabs: [
                    AtomicTabBar.TabItem(id: "home", title: "Home", icon: "house"),
                    AtomicTabBar.TabItem(id: "favorites", title: "Favorites", icon: "heart", badgeCount: 3),
                    AtomicTabBar.TabItem(id: "settings", title: "Settings", icon: "gear")
                ]
            )
        }
        .previewLayout(.sizeThatFits)
    }
}