import SwiftUI

// MARK: - Atomic Component Showcase
// Template demonstrating all iOS atomic components and molecules
// Matches Android AtomicComponentShowcase.kt implementation

struct AtomicComponentShowcase: View {
    
    // MARK: - State Properties
    @State private var searchQuery = ""
    @State private var textFieldValue = ""
    @State private var isToggleOn = true
    @State private var isCheckboxChecked = false
    @State private var selectedTab = "home"
    @State private var selectedNavItem = "home"
    @State private var settingsData = SearchForm.SettingsData()
    @State private var email = ""
    @State private var password = ""
    @State private var showingSearchLoading = false
    @State private var showingLoginLoading = false
    
    // MARK: - Body
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: AtomicSpacing.lg) {
                    // Header
                    sectionHeader("iOS Atomic Design System")
                    
                    // Input Atoms Section
                    atomicInputsSection
                    
                    // Button Atoms Section
                    atomicButtonsSection
                    
                    // Card Atoms Section
                    atomicCardsSection
                    
                    // Navigation Atoms Section
                    atomicNavigationSection
                    
                    // Form Molecules Section
                    formMoleculesSection
                    
                    Spacer(minLength: AtomicSpacing.xl)
                }
                .atomicPadding()
            }
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .principal) {
                    AtomicNavigationBar(
                        title: "Atomic Components",
                        trailingActions: [
                            AtomicNavigationBar.NavigationAction(
                                icon: "info.circle",
                                accessibilityLabel: "Info"
                            ) { print("Info tapped") }
                        ],
                        centered: true
                    )
                }
            }
        }
    }
    
    // MARK: - Section Views
    
    private var atomicInputsSection: some View {
        VStack(alignment: .leading, spacing: AtomicSpacing.md) {
            sectionHeader("Input Atoms")
            
            AtomicCard {
                VStack(spacing: AtomicSpacing.md) {
                    AtomicTextField(
                        title: "Text Field",
                        placeholder: "Enter some text...",
                        text: $textFieldValue
                    )
                    
                    AtomicSearchField(
                        placeholder: "Search locations...",
                        text: $searchQuery
                    )
                    
                    AtomicToggle(
                        label: "Enable notifications",
                        isOn: $isToggleOn
                    )
                    
                    AtomicCheckbox(
                        label: "Accept terms and conditions",
                        isChecked: $isCheckboxChecked
                    )
                }
                .atomicPadding()
            }
        }
    }
    
    private var atomicButtonsSection: some View {
        VStack(alignment: .leading, spacing: AtomicSpacing.md) {
            sectionHeader("Button Atoms")
            
            AtomicCard {
                VStack(spacing: AtomicSpacing.sm) {
                    // Primary Button (using existing PrimaryButton from Buttons folder)
                    HStack {
                        Text("Primary Action")
                            .font(.system(size: 16, weight: .medium))
                            .foregroundColor(.atomicOnPrimary)
                        Spacer()
                    }
                    .frame(height: AtomicSpacing.buttonHeight)
                    .atomicPaddingMD(.horizontal)
                    .background(Color.atomicPrimary)
                    .cornerRadius(AtomicShapes.button)
                    .onTapGesture {
                        print("Primary button tapped")
                    }
                    
                    // Secondary Button
                    HStack {
                        Text("Secondary Action")
                            .font(.system(size: 16, weight: .medium))
                            .foregroundColor(.atomicPrimary)
                        Spacer()
                    }
                    .frame(height: AtomicSpacing.buttonHeight)
                    .atomicPaddingMD(.horizontal)
                    .background(Color.clear)
                    .overlay(
                        RoundedRectangle(cornerRadius: AtomicShapes.button)
                            .stroke(Color.atomicPrimary, lineWidth: AtomicShapes.borderMedium)
                    )
                    .onTapGesture {
                        print("Secondary button tapped")
                    }
                }
                .atomicPadding()
            }
        }
    }
    
    private var atomicCardsSection: some View {
        VStack(alignment: .leading, spacing: AtomicSpacing.md) {
            sectionHeader("Card Atoms")
            
            VStack(spacing: AtomicSpacing.sm) {
                AtomicCard(onClick: {
                    print("Basic card tapped")
                }) {
                    Text("Basic Card")
                        .font(.system(size: 16))
                        .atomicPadding()
                }
                
                AtomicOutlinedCard(onClick: {
                    print("Outlined card tapped")
                }) {
                    VStack(alignment: .leading, spacing: AtomicSpacing.xs) {
                        Text("Outlined Card")
                            .font(.system(size: 16, weight: .medium))
                        Text("This card has a border")
                            .font(.system(size: 14))
                            .foregroundColor(.atomicOnSurfaceVariant)
                    }
                    .atomicPadding()
                }
                
                AtomicElevatedCard(onClick: {
                    print("Elevated card tapped")
                }) {
                    HStack {
                        Image(systemName: "star.fill")
                            .foregroundColor(.atomicPrimary)
                            .font(.title2)
                        VStack(alignment: .leading, spacing: AtomicSpacing.xs) {
                            Text("Elevated Card")
                                .font(.system(size: 16, weight: .medium))
                            Text("Clickable with elevation")
                                .font(.system(size: 14))
                                .foregroundColor(.atomicOnSurfaceVariant)
                        }
                        Spacer()
                    }
                    .atomicPadding()
                }
                
                AtomicWeatherCard(
                    weatherCondition: .clear,
                    onClick: {
                        print("Weather card tapped")
                    }
                ) {
                    HStack {
                        Image(systemName: "sun.max.fill")
                            .foregroundColor(.atomicSunny)
                            .font(.title)
                        VStack(alignment: .leading, spacing: AtomicSpacing.xs) {
                            Text("Weather Card")
                                .font(.system(size: 16, weight: .medium))
                            Text("Sunny conditions")
                                .font(.system(size: 14))
                                .foregroundColor(.atomicOnSurfaceVariant)
                        }
                        Spacer()
                    }
                    .atomicPadding()
                }
            }
        }
    }
    
    private var atomicNavigationSection: some View {
        VStack(alignment: .leading, spacing: AtomicSpacing.md) {
            sectionHeader("Navigation Atoms")
            
            AtomicCard {
                VStack(spacing: AtomicSpacing.md) {
                    // Tab Bar Preview
                    AtomicTabBar(
                        selectedTab: $selectedTab,
                        tabs: [
                            AtomicTabBar.TabItem(id: "home", title: "Home", icon: "house"),
                            AtomicTabBar.TabItem(id: "favorites", title: "Favorites", icon: "heart", badgeCount: 3),
                            AtomicTabBar.TabItem(id: "settings", title: "Settings", icon: "gear")
                        ]
                    )
                    
                    Divider()
                    
                    // Navigation Rail Preview (Horizontal)
                    HStack(spacing: AtomicSpacing.sm) {
                        ForEach(["home", "search", "profile"], id: \.self) { item in
                            Button(action: {
                                selectedNavItem = item
                            }) {
                                VStack(spacing: AtomicSpacing.xs) {
                                    Image(systemName: iconForNavItem(item))
                                        .font(.system(size: 20, weight: selectedNavItem == item ? .semibold : .medium))
                                        .foregroundColor(selectedNavItem == item ? .atomicPrimary : .atomicOnSurfaceVariant)
                                    
                                    Text(item.capitalized)
                                        .font(.system(size: 12, weight: .medium))
                                        .foregroundColor(selectedNavItem == item ? .atomicPrimary : .atomicOnSurfaceVariant)
                                }
                                .frame(width: 60, height: 50)
                                .background(
                                    selectedNavItem == item ?
                                        Color.atomicPrimary.opacity(0.12) :
                                        Color.clear
                                )
                                .cornerRadius(AtomicShapes.sm)
                            }
                            .buttonStyle(PlainButtonStyle())
                        }
                    }
                    .frame(maxWidth: .infinity)
                }
                .atomicPadding()
            }
        }
    }
    
    private var formMoleculesSection: some View {
        VStack(alignment: .leading, spacing: AtomicSpacing.md) {
            sectionHeader("Form Molecules")
            
            VStack(spacing: AtomicSpacing.md) {
                // Search Form
                SearchForm(
                    searchQuery: $searchQuery,
                    onSearch: {
                        showingSearchLoading = true
                        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                            showingSearchLoading = false
                        }
                    },
                    isLoading: showingSearchLoading
                )
                
                // Settings Form
                SettingsForm(settingsData: $settingsData)
                
                // Login Form
                LoginForm(
                    email: $email,
                    password: $password,
                    onLogin: {
                        showingLoginLoading = true
                        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                            showingLoginLoading = false
                        }
                    },
                    onForgotPassword: {
                        print("Forgot password tapped")
                    },
                    isLoading: showingLoginLoading
                )
            }
        }
    }
    
    // MARK: - Helper Methods
    
    private func sectionHeader(_ title: String) -> some View {
        Text(title)
            .font(.system(size: 20, weight: .bold))
            .foregroundColor(.atomicOnSurface)
            .frame(maxWidth: .infinity, alignment: .leading)
    }
    
    private func iconForNavItem(_ item: String) -> String {
        switch item {
        case "home":
            return "house"
        case "search":
            return "magnifyingglass"
        case "profile":
            return "person"
        default:
            return "circle"
        }
    }
}

// MARK: - Preview
struct AtomicComponentShowcase_Previews: PreviewProvider {
    static var previews: some View {
        AtomicComponentShowcase()
            .previewDevice("iPhone 14")
    }
}