import SwiftUI

// MARK: - Search Form Molecule
// Complete search form composed of atomic components
// Matches Android SearchForm.kt implementation

struct SearchForm: View {
    
    // MARK: - Properties
    @Binding var searchQuery: String
    let onSearch: () -> Void
    let isLoading: Bool
    let errorMessage: String?
    let placeholder: String
    let searchButtonText: String
    
    // MARK: - Initializer
    init(
        searchQuery: Binding<String>,
        onSearch: @escaping () -> Void,
        isLoading: Bool = false,
        errorMessage: String? = nil,
        placeholder: String = "Search for a location...",
        searchButtonText: String = "Search Weather"
    ) {
        self._searchQuery = searchQuery
        self.onSearch = onSearch
        self.isLoading = isLoading
        self.errorMessage = errorMessage
        self.placeholder = placeholder
        self.searchButtonText = searchButtonText
    }
    
    // MARK: - Body
    var body: some View {
        VStack(alignment: .leading, spacing: AtomicSpacing.sm) {
            // Search Field
            AtomicSearchField(
                placeholder: placeholder,
                text: $searchQuery,
                enabled: !isLoading,
                onSearch: {
                    if !searchQuery.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                        onSearch()
                    }
                }
            )
            
            // Error Message
            if let errorMessage = errorMessage {
                Text(errorMessage)
                    .font(.system(size: 14))
                    .foregroundColor(.atomicError)
                    .atomicPaddingXS(.horizontal)
            }
            
            // Search Button
            Button(action: onSearch) {
                HStack {
                    if isLoading {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: .atomicOnPrimary))
                            .scaleEffect(0.8)
                    }
                    
                    Text(searchButtonText)
                        .font(.system(size: 16, weight: .medium))
                        .foregroundColor(.atomicOnPrimary)
                }
                .frame(maxWidth: .infinity)
                .frame(height: AtomicSpacing.buttonHeight)
                .background(
                    (canSearch && !isLoading) ?
                        Color.atomicPrimary : Color.atomicOnSurfaceVariant
                )
                .cornerRadius(AtomicShapes.button)
            }
            .disabled(!canSearch || isLoading)
            .buttonStyle(PlainButtonStyle())
            .animation(.easeInOut(duration: 0.2), value: isLoading)
        }
    }
    
    // MARK: - Computed Properties
    private var canSearch: Bool {
        !searchQuery.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }
}

// MARK: - Settings Form Molecule
// Complete settings form with multiple input types
// Matches Android SettingsForm.kt implementation

struct SettingsForm: View {
    
    // MARK: - Settings Data Model
    struct SettingsData {
        var defaultLocation: String = ""
        var enableNotifications: Bool = true
        var enableDarkMode: Bool = false
        var enableAutoRefresh: Bool = true
        var enableLocationServices: Bool = false
        var refreshInterval: Int = 30 // minutes
        var temperatureUnit: TemperatureUnit = .celsius
        
        enum TemperatureUnit: String, CaseIterable {
            case celsius = "°C"
            case fahrenheit = "°F"
            
            var displayName: String {
                switch self {
                case .celsius:
                    return "Celsius (°C)"
                case .fahrenheit:
                    return "Fahrenheit (°F)"
                }
            }
        }
    }
    
    // MARK: - Properties
    @Binding var settingsData: SettingsData
    
    // MARK: - Initializer
    init(settingsData: Binding<SettingsData>) {
        self._settingsData = settingsData
    }
    
    // MARK: - Body
    var body: some View {
        VStack(spacing: AtomicSpacing.md) {
            // Location Settings Section
            AtomicCard {
                VStack(alignment: .leading, spacing: AtomicSpacing.md) {
                    Text("Location Settings")
                        .font(.system(size: 18, weight: .semibold))
                        .foregroundColor(.atomicOnSurface)
                    
                    AtomicTextField(
                        title: "Default Location",
                        placeholder: "Enter city name...",
                        text: $settingsData.defaultLocation,
                        keyboardType: .default,
                        textContentType: .location
                    )
                    
                    AtomicToggle(
                        label: "Enable Location Services",
                        isOn: $settingsData.enableLocationServices
                    )
                }
                .atomicPadding()
            }
            
            // App Preferences Section
            AtomicCard {
                VStack(alignment: .leading, spacing: AtomicSpacing.md) {
                    Text("App Preferences")
                        .font(.system(size: 18, weight: .semibold))
                        .foregroundColor(.atomicOnSurface)
                    
                    AtomicToggle(
                        label: "Dark Mode",
                        isOn: $settingsData.enableDarkMode
                    )
                    
                    AtomicToggle(
                        label: "Auto Refresh Weather Data",
                        isOn: $settingsData.enableAutoRefresh
                    )
                    
                    AtomicCheckbox(
                        label: "Enable Push Notifications",
                        isChecked: $settingsData.enableNotifications
                    )
                    
                    // Temperature Unit Picker
                    VStack(alignment: .leading, spacing: AtomicSpacing.xs) {
                        Text("Temperature Unit")
                            .font(.system(size: 14, weight: .medium))
                            .foregroundColor(.atomicOnSurface)
                        
                        Picker("Temperature Unit", selection: $settingsData.temperatureUnit) {
                            ForEach(SettingsData.TemperatureUnit.allCases, id: \.self) { unit in
                                Text(unit.displayName).tag(unit)
                            }
                        }
                        .pickerStyle(SegmentedPickerStyle())
                    }
                    
                    // Refresh Interval Stepper
                    if settingsData.enableAutoRefresh {
                        VStack(alignment: .leading, spacing: AtomicSpacing.xs) {
                            Text("Refresh Interval")
                                .font(.system(size: 14, weight: .medium))
                                .foregroundColor(.atomicOnSurface)
                            
                            HStack {
                                Text("\(settingsData.refreshInterval) minutes")
                                    .font(.system(size: 16))
                                    .foregroundColor(.atomicOnSurface)
                                
                                Spacer()
                                
                                Stepper(
                                    "",
                                    value: $settingsData.refreshInterval,
                                    in: 5...120,
                                    step: 5
                                )
                                .labelsHidden()
                            }
                        }
                        .transition(.opacity.combined(with: .slide))
                    }
                }
                .atomicPadding()
            }
            .animation(.easeInOut(duration: 0.3), value: settingsData.enableAutoRefresh)
        }
    }
}

// MARK: - Login Form Molecule
// User authentication form
struct LoginForm: View {
    
    // MARK: - Properties
    @Binding var email: String
    @Binding var password: String
    let onLogin: () -> Void
    let onForgotPassword: () -> Void
    let isLoading: Bool
    let errorMessage: String?
    
    @State private var showPassword: Bool = false
    
    // MARK: - Initializer
    init(
        email: Binding<String>,
        password: Binding<String>,
        onLogin: @escaping () -> Void,
        onForgotPassword: @escaping () -> Void,
        isLoading: Bool = false,
        errorMessage: String? = nil
    ) {
        self._email = email
        self._password = password
        self.onLogin = onLogin
        self.onForgotPassword = onForgotPassword
        self.isLoading = isLoading
        self.errorMessage = errorMessage
    }
    
    // MARK: - Body
    var body: some View {
        VStack(spacing: AtomicSpacing.lg) {
            // Form Fields
            VStack(spacing: AtomicSpacing.md) {
                AtomicTextField(
                    title: "Email",
                    placeholder: "Enter your email",
                    text: $email,
                    isError: !isValidEmail && !email.isEmpty,
                    keyboardType: .emailAddress,
                    textContentType: .emailAddress
                )
                
                AtomicTextField(
                    title: "Password",
                    placeholder: "Enter your password",
                    text: $password,
                    isError: !isValidPassword && !password.isEmpty,
                    keyboardType: showPassword ? .default : .default,
                    textContentType: .password
                )
            }
            
            // Error Message
            if let errorMessage = errorMessage {
                Text(errorMessage)
                    .font(.system(size: 14))
                    .foregroundColor(.atomicError)
                    .multilineTextAlignment(.center)
            }
            
            // Action Buttons
            VStack(spacing: AtomicSpacing.sm) {
                // Login Button
                Button(action: onLogin) {
                    HStack {
                        if isLoading {
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle(tint: .atomicOnPrimary))
                                .scaleEffect(0.8)
                        }
                        
                        Text("Sign In")
                            .font(.system(size: 16, weight: .medium))
                            .foregroundColor(.atomicOnPrimary)
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: AtomicSpacing.buttonHeight)
                    .background(
                        (canLogin && !isLoading) ?
                            Color.atomicPrimary : Color.atomicOnSurfaceVariant
                    )
                    .cornerRadius(AtomicShapes.button)
                }
                .disabled(!canLogin || isLoading)
                .buttonStyle(PlainButtonStyle())
                
                // Forgot Password Button
                Button(action: onForgotPassword) {
                    Text("Forgot Password?")
                        .font(.system(size: 14, weight: .medium))
                        .foregroundColor(.atomicPrimary)
                }
                .buttonStyle(PlainButtonStyle())
                .disabled(isLoading)
            }
        }
    }
    
    // MARK: - Computed Properties
    private var isValidEmail: Bool {
        email.contains("@") && email.contains(".")
    }
    
    private var isValidPassword: Bool {
        password.count >= 6
    }
    
    private var canLogin: Bool {
        isValidEmail && isValidPassword
    }
}

// MARK: - Preview
struct SearchForm_Previews: PreviewProvider {
    static var previews: some View {
        ScrollView {
            VStack(spacing: AtomicSpacing.xl) {
                SearchForm(
                    searchQuery: .constant(""),
                    onSearch: { print("Search initiated") }
                )
                
                SearchForm(
                    searchQuery: .constant("New York"),
                    onSearch: { print("Search initiated") },
                    isLoading: true
                )
                
                SearchForm(
                    searchQuery: .constant("Invalid"),
                    onSearch: { print("Search initiated") },
                    errorMessage: "Location not found. Please try a different search."
                )
                
                SettingsForm(
                    settingsData: .constant(SettingsForm.SettingsData())
                )
                
                LoginForm(
                    email: .constant("user@example.com"),
                    password: .constant("password"),
                    onLogin: { print("Login initiated") },
                    onForgotPassword: { print("Forgot password") }
                )
            }
            .atomicPadding()
        }
        .previewLayout(.sizeThatFits)
    }
}