import SwiftUI
import shared

struct EnhancedWeatherListView: View {
    @ObservedObject var viewModel: WeatherViewModel
    @State private var isRefreshing = false
    
    var body: some View {
        NavigationView {
            ZStack {
                // Background gradient
                LinearGradient(
                    colors: [AtomicDesignSystem.Colors.background, AtomicDesignSystem.Colors.surfaceContainer],
                    startPoint: .top,
                    endPoint: .bottom
                )
                .ignoresSafeArea()
                
                if viewModel.isLoading && viewModel.weatherList.isEmpty {
                    LoadingStateView()
                } else if viewModel.hasError && viewModel.weatherList.isEmpty {
                    ErrorStateView(
                        error: viewModel.errorMessage ?? "Unknown error",
                        onRetry: {
                            viewModel.loadWeatherData()
                        }
                    )
                } else {
                    WeatherListContent(
                        weatherList: viewModel.weatherList,
                        onWeatherClick: { weather in
                            // Navigation handled by parent
                        }
                    )
                }
            }
            .navigationTitle("Weather Forecast")
            .navigationBarTitleDisplayMode(.large)
            .refreshable {
                await refreshWeatherData()
            }
        }
    }
    
    @MainActor
    private func refreshWeatherData() async {
        isRefreshing = true
        viewModel.loadWeatherData()
        // Simulate network delay for better UX
        try? await Task.sleep(nanoseconds: 500_000_000) // 0.5 seconds
        isRefreshing = false
    }
}

struct WeatherListContent: View {
    let weatherList: [Weather]
    let onWeatherClick: (Weather) -> Void
    
    var body: some View {
        ScrollView {
            LazyVStack(spacing: AtomicDesignSystem.Spacing.weatherCardSpacing) {
                ForEach(weatherList, id: \.self) { weather in
                    EnhancedWeatherRowCard(
                        weather: weather,
                        onClick: { onWeatherClick(weather) }
                    )
                    .padding(.horizontal, AtomicDesignSystem.Spacing.screenPadding)
                }
                
                Spacer()
                    .frame(height: AtomicDesignSystem.Spacing.lg)
            }
            .padding(.top, AtomicDesignSystem.Spacing.sm)
        }
    }
}

struct EnhancedWeatherRowCard: View {
    let weather: Weather
    let onClick: () -> Void
    
    var body: some View {
        Button(action: onClick) {
            HStack(spacing: AtomicDesignSystem.Spacing.md) {
                // Date section
                VStack(alignment: .leading, spacing: AtomicDesignSystem.Spacing.xs) {
                    Text(getDayName(weather.date))
                        .font(AtomicDesignSystem.Typography.titleMedium)
                        .fontWeight(.bold)
                        .foregroundColor(AtomicDesignSystem.Colors.onSurface)
                    
                    Text(formatDate(weather.date))
                        .font(AtomicDesignSystem.Typography.labelMedium)
                        .foregroundColor(AtomicDesignSystem.Colors.onSurfaceVariant)
                }
                .frame(width: AtomicDesignSystem.Spacing.weatherCardDateWidth, alignment: .leading)
                
                // Weather icon and condition
                VStack(alignment: .center, spacing: AtomicDesignSystem.Spacing.xs) {
                    Text(getWeatherEmoji(weather.condition))
                        .font(.system(size: 32))
                    
                    Text(weather.description_)
                        .font(AtomicDesignSystem.Typography.labelSmall)
                        .foregroundColor(AtomicDesignSystem.Colors.onSurfaceVariant)
                        .multilineTextAlignment(.center)
                        .lineLimit(2)
                }
                .frame(width: AtomicDesignSystem.Spacing.weatherCardIconWidth)
                
                Spacer()
                
                // Temperature and details
                VStack(alignment: .trailing, spacing: AtomicDesignSystem.Spacing.xs) {
                    HStack(spacing: AtomicDesignSystem.Spacing.sm) {
                        // High temperature
                        VStack(alignment: .center, spacing: AtomicDesignSystem.Spacing.xxs) {
                            Text("HIGH")
                                .font(AtomicDesignSystem.Typography.labelSmall)
                                .foregroundColor(AtomicDesignSystem.Colors.onSurfaceVariant)
                                .tracking(0.5)
                            
                            Text("\\(Int(weather.temperatureHigh))°")
                                .font(AtomicDesignSystem.Typography.titleLarge)
                                .fontWeight(.bold)
                                .foregroundColor(AtomicDesignSystem.Colors.onSurface)
                        }
                        
                        // Low temperature
                        VStack(alignment: .center, spacing: AtomicDesignSystem.Spacing.xxs) {
                            Text("LOW")
                                .font(AtomicDesignSystem.Typography.labelSmall)
                                .foregroundColor(AtomicDesignSystem.Colors.onSurfaceVariant)
                                .tracking(0.5)
                            
                            Text("\\(Int(weather.temperatureLow))°")
                                .font(AtomicDesignSystem.Typography.titleMedium)
                                .fontWeight(.medium)
                                .foregroundColor(AtomicDesignSystem.Colors.onSurfaceVariant)
                        }
                    }
                    
                    // Humidity with icon
                    HStack(spacing: AtomicDesignSystem.Spacing.xs) {
                        Text("💧")
                            .font(AtomicDesignSystem.Typography.labelMedium)
                        
                        Text("\\(weather.humidity)%")
                            .font(AtomicDesignSystem.Typography.labelMedium)
                            .foregroundColor(AtomicDesignSystem.Colors.onSurfaceVariant)
                    }
                }
            }
            .padding(.horizontal, AtomicDesignSystem.Spacing.xl)
            .padding(.vertical, AtomicDesignSystem.Spacing.md)
        }
        .background(AtomicDesignSystem.Colors.weatherCardBackground)
        .cornerRadius(AtomicDesignSystem.CornerRadius.weatherCard)
        .shadow(
            color: Color.black.opacity(0.05),
            radius: AtomicDesignSystem.Spacing.sm,
            x: 0,
            y: 2
        )
        .overlay(
            RoundedRectangle(cornerRadius: AtomicDesignSystem.CornerRadius.weatherCard)
                .stroke(AtomicDesignSystem.Colors.outline.opacity(0.3), lineWidth: AtomicDesignSystem.Spacing.borderWidthThin)
        )
        .buttonStyle(PlainButtonStyle())
    }
}

struct LoadingStateView: View {
    var body: some View {
        VStack(spacing: AtomicDesignSystem.Spacing.lg) {
            ProgressView()
                .scaleEffect(1.5)
                .foregroundColor(AtomicDesignSystem.Colors.primary)
            
            VStack(spacing: AtomicDesignSystem.Spacing.sm) {
                Text("Loading Weather")
                    .font(AtomicDesignSystem.Typography.titleLarge)
                    .fontWeight(.semibold)
                    .foregroundColor(AtomicDesignSystem.Colors.onBackground)
                
                Text("Fetching the latest forecast...")
                    .font(AtomicDesignSystem.Typography.bodyMedium)
                    .foregroundColor(AtomicDesignSystem.Colors.onSurfaceVariant)
            }
        }
    }
}

struct ErrorStateView: View {
    let error: String
    let onRetry: () -> Void
    
    var body: some View {
        VStack(spacing: AtomicDesignSystem.Spacing.lg) {
            VStack(spacing: AtomicDesignSystem.Spacing.md) {
                Image(systemName: "cloud.slash")
                    .font(.system(size: 60))
                    .foregroundColor(AtomicDesignSystem.Colors.error)
                
                Text("Something went wrong")
                    .font(AtomicDesignSystem.Typography.titleLarge)
                    .fontWeight(.bold)
                    .foregroundColor(AtomicDesignSystem.Colors.onBackground)
                
                Text(error)
                    .font(AtomicDesignSystem.Typography.bodyMedium)
                    .foregroundColor(AtomicDesignSystem.Colors.onSurfaceVariant)
                    .multilineTextAlignment(.center)
            }
            
            Button(action: onRetry) {
                HStack(spacing: AtomicDesignSystem.Spacing.sm) {
                    Image(systemName: "arrow.clockwise")
                        .font(.system(size: 18))
                    
                    Text("Try Again")
                        .font(AtomicDesignSystem.Typography.titleMedium)
                        .fontWeight(.medium)
                }
                .foregroundColor(.white)
                .padding(.horizontal, AtomicDesignSystem.Spacing.buttonPadding)
                .frame(height: AtomicDesignSystem.Spacing.buttonHeight)
                .background(AtomicDesignSystem.Colors.primary)
                .cornerRadius(AtomicDesignSystem.CornerRadius.button)
            }
        }
        .padding(AtomicDesignSystem.Spacing.xxl)
    }
}

// Helper functions
private func getDayName(_ date: Kotlinx_datetimeLocalDate) -> String {
    let dateFormatter = DateFormatter()
    dateFormatter.dateFormat = "EEEE"
    
    let calendar = Calendar.current
    let components = DateComponents(
        year: Int(date.year),
        month: Int(date.monthNumber),
        day: Int(date.dayOfMonth)
    )
    
    guard let date = calendar.date(from: components) else {
        return "Unknown"
    }
    
    return dateFormatter.string(from: date)
}

private func formatDate(_ date: Kotlinx_datetimeLocalDate) -> String {
    let dateFormatter = DateFormatter()
    dateFormatter.dateFormat = "MMM d"
    
    let calendar = Calendar.current
    let components = DateComponents(
        year: Int(date.year),
        month: Int(date.monthNumber),
        day: Int(date.dayOfMonth)
    )
    
    guard let date = calendar.date(from: components) else {
        return "Unknown"
    }
    
    return dateFormatter.string(from: date)
}

#Preview {
    EnhancedWeatherListView(viewModel: WeatherViewModel())
}