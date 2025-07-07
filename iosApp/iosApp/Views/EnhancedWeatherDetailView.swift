import SwiftUI
import shared

struct EnhancedWeatherDetailView: View {
    let weather: Weather
    
    var body: some View {
        ScrollView {
            VStack(spacing: AtomicDesignSystem.Spacing.lg) {
                // Location and date header
                VStack(spacing: AtomicDesignSystem.Spacing.xs) {
                    Text("Bengaluru, India")
                        .font(AtomicDesignSystem.Typography.titleLarge)
                        .fontWeight(.bold)
                        .foregroundColor(AtomicDesignSystem.Colors.onBackground)
                    
                    Text(formatDetailDate(weather.date))
                        .font(AtomicDesignSystem.Typography.bodyMedium)
                        .foregroundColor(AtomicDesignSystem.Colors.onSurfaceVariant)
                }
                .padding(.top, AtomicDesignSystem.Spacing.xs)
                
                // Enhanced main weather card with gradient background
                VStack(spacing: AtomicDesignSystem.Spacing.lg) {
                    // Weather icon
                    Text(getWeatherEmoji(weather.condition))
                        .font(.system(size: 100))
                    
                    // Weather description
                    Text(weather.description_)
                        .font(AtomicDesignSystem.Typography.titleLarge)
                        .fontWeight(.medium)
                        .multilineTextAlignment(.center)
                        .foregroundColor(.white)
                    
                    // Temperature section with improved layout
                    VStack(spacing: AtomicDesignSystem.Spacing.md) {
                        // Current temperature
                        VStack(spacing: AtomicDesignSystem.Spacing.xs) {
                            Text("CURRENT")
                                .font(AtomicDesignSystem.Typography.labelSmall)
                                .foregroundColor(.white.opacity(0.8))
                                .tracking(1)
                            
                            Text("\\(Int(weather.temperatureHigh))°")
                                .font(.system(size: 64, weight: .thin, design: .default))
                                .foregroundColor(.white)
                        }
                        
                        // High/Low temperatures in horizontal layout
                        HStack(spacing: AtomicDesignSystem.Spacing.xxxl - AtomicDesignSystem.Spacing.xs) {
                            VStack(spacing: AtomicDesignSystem.Spacing.sm) {
                                Image(systemName: "arrow.up")
                                    .foregroundColor(.red.opacity(0.8))
                                    .font(.caption)
                                
                                Text("HIGH")
                                    .font(AtomicDesignSystem.Typography.labelSmall)
                                    .foregroundColor(.white.opacity(0.8))
                                    .tracking(0.5)
                                
                                Text("\\(Int(weather.temperatureHigh))°")
                                    .font(AtomicDesignSystem.Typography.headlineMedium)
                                    .fontWeight(.semibold)
                                    .foregroundColor(.white)
                            }
                            
                            // Vertical divider
                            Rectangle()
                                .fill(Color.white.opacity(0.3))
                                .frame(width: 1, height: 50)
                            
                            VStack(spacing: AtomicDesignSystem.Spacing.sm) {
                                Image(systemName: "arrow.down")
                                    .foregroundColor(.blue.opacity(0.8))
                                    .font(.caption)
                                
                                Text("LOW")
                                    .font(AtomicDesignSystem.Typography.labelSmall)
                                    .foregroundColor(.white.opacity(0.8))
                                    .tracking(0.5)
                                
                                Text("\\(Int(weather.temperatureLow))°")
                                    .font(AtomicDesignSystem.Typography.headlineMedium)
                                    .fontWeight(.semibold)
                                    .foregroundColor(.white)
                            }
                        }
                    }
                }
                .frame(maxWidth: .infinity)
                .padding(.horizontal, AtomicDesignSystem.Spacing.lg)
                .padding(.vertical, AtomicDesignSystem.Spacing.xl)
                .background(
                    AtomicDesignSystem.getWeatherGradient(for: weather.condition)
                )
                .overlay(
                    RoundedRectangle(cornerRadius: AtomicDesignSystem.CornerRadius.weatherCard)
                        .stroke(Color.white.opacity(0.2), lineWidth: AtomicDesignSystem.Spacing.borderWidth)
                )
                .cornerRadius(AtomicDesignSystem.CornerRadius.weatherCard)
                .shadow(
                    color: Color.black.opacity(0.1),
                    radius: 10,
                    x: 0,
                    y: 5
                )
                
                // Enhanced additional details with better spacing
                LazyVGrid(columns: [
                    GridItem(.flexible(), spacing: AtomicDesignSystem.Spacing.md),
                    GridItem(.flexible(), spacing: AtomicDesignSystem.Spacing.md)
                ], spacing: AtomicDesignSystem.Spacing.md) {
                    
                    // Humidity card
                    EnhancedDetailCard(
                        icon: "💧",
                        title: "Humidity",
                        value: "\\(weather.humidity)%",
                        subtitle: getHumidityDescription(weather.humidity)
                    )
                    
                    // Condition card
                    EnhancedDetailCard(
                        icon: getWeatherEmoji(weather.condition),
                        title: "Condition",
                        value: weather.condition.name.capitalized,
                        subtitle: "Today"
                    )
                    
                    // Feels like card
                    EnhancedDetailCard(
                        icon: "🌡️",
                        title: "Feels like",
                        value: "\\(Int(weather.temperatureHigh))°",
                        subtitle: "Actual temp"
                    )
                    
                    // Weather index card
                    EnhancedDetailCard(
                        icon: "📊",
                        title: "Weather Index",
                        value: getWeatherIndex(weather),
                        subtitle: "Overall rating"
                    )
                }
                
                Spacer()
                    .frame(minHeight: AtomicDesignSystem.Spacing.lg)
            }
            .padding(.horizontal, AtomicDesignSystem.Spacing.screenPadding)
        }
        .background(AtomicDesignSystem.Colors.surfaceContainer)
        .navigationTitle("Weather Details")
        .navigationBarTitleDisplayMode(.inline)
    }
    
    // Helper function for humidity description
    private func getHumidityDescription(_ humidity: Int32) -> String {
        switch humidity {
        case 0...30: return "Dry"
        case 31...60: return "Comfortable"
        case 61...80: return "Humid"
        default: return "Very Humid"
        }
    }
    
    // Helper function for weather index
    private func getWeatherIndex(_ weather: Weather) -> String {
        let tempScore = (weather.temperatureHigh > 20 && weather.temperatureHigh < 30) ? 1 : 0
        let humidityScore = (weather.humidity > 30 && weather.humidity < 70) ? 1 : 0
        let total = tempScore + humidityScore
        
        switch total {
        case 2: return "Excellent"
        case 1: return "Good"
        default: return "Fair"
        }
    }
    
    private func formatDetailDate(_ date: Kotlinx_datetimeLocalDate) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "EEEE, MMMM d"
        
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
}

// Enhanced detail card component following design system
struct EnhancedDetailCard: View {
    let icon: String
    let title: String
    let value: String
    let subtitle: String
    
    var body: some View {
        VStack(spacing: AtomicDesignSystem.Spacing.sm) {
            Text(icon)
                .font(AtomicDesignSystem.Typography.headlineMedium)
            
            VStack(spacing: AtomicDesignSystem.Spacing.xs) {
                Text(title)
                    .font(AtomicDesignSystem.Typography.labelSmall)
                    .foregroundColor(AtomicDesignSystem.Colors.onSurfaceVariant)
                    .textCase(.uppercase)
                    .tracking(0.5)
                
                Text(value)
                    .font(AtomicDesignSystem.Typography.titleMedium)
                    .fontWeight(.bold)
                    .lineLimit(2)
                    .multilineTextAlignment(.center)
                    .foregroundColor(AtomicDesignSystem.Colors.onSurface)
                
                Text(subtitle)
                    .font(AtomicDesignSystem.Typography.labelSmall)
                    .foregroundColor(AtomicDesignSystem.Colors.onSurfaceVariant.opacity(0.8))
            }
        }
        .frame(maxWidth: .infinity)
        .frame(height: 120)
        .padding(AtomicDesignSystem.Spacing.md)
        .background(AtomicDesignSystem.Colors.weatherCardBackground)
        .cornerRadius(AtomicDesignSystem.CornerRadius.cardElevated)
        .shadow(
            color: Color.black.opacity(0.05),
            radius: 4,
            x: 0,
            y: 2
        )
    }
}

#Preview {
    NavigationView {
        EnhancedWeatherDetailView(
            weather: Weather(
                date: Kotlinx_datetimeLocalDate.companion.parse(isoString: "2024-01-15"),
                condition: WeatherCondition.clear,
                temperatureHigh: 25.0,
                temperatureLow: 15.0,
                humidity: 65,
                icon: "01d",
                description: "Clear sky with pleasant weather"
            )
        )
    }
}