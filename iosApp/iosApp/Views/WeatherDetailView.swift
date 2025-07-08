import SwiftUI
import shared

struct WeatherDetailView: View {
    let weather: Weather
    
    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                // Location and date header
                VStack(spacing: 6) {
                    Text("Bengaluru, India")
                        .font(.title2)
                        .fontWeight(.bold)
                    Text(formatDetailDate(weather.date))
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                .padding(.top)
                
                // Enhanced main weather card with gradient background
                VStack(spacing: 24) {
                    // Weather icon with subtle animation
                    Text(getWeatherEmoji(weather.condition))
                        .font(.system(size: 120))
                        .scaleEffect(1.0)
                        .animation(.easeInOut(duration: 2).repeatForever(autoreverses: true), value: true)
                    
                    // Weather description
                    Text(weather.description_)
                        .font(.title2)
                        .fontWeight(.medium)
                        .multilineTextAlignment(.center)
                        .foregroundColor(.primary)
                    
                    // Temperature section with improved layout
                    VStack(spacing: 16) {
                        // Current/Average temperature (if you have it, otherwise use high)
                        VStack(spacing: 4) {
                            Text("Current")
                                .font(.caption)
                                .foregroundColor(.secondary)
                                .textCase(.uppercase)
                                .tracking(1)
                            Text("\(Int(weather.temperatureHigh))°")
                                .font(.system(size: 64, weight: .thin, design: .rounded))
                                .foregroundColor(.primary)
                        }
                        
                        // High/Low temperatures in horizontal layout
                        HStack(spacing: 60) {
                            VStack(spacing: 8) {
                                Image(systemName: "arrow.up")
                                    .foregroundColor(.red)
                                    .font(.caption)
                                Text("High")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                    .textCase(.uppercase)
                                    .tracking(0.5)
                                Text("\(Int(weather.temperatureHigh))°")
                                    .font(.title)
                                    .fontWeight(.semibold)
                            }
                            
                            // Vertical divider
                            Rectangle()
                                .fill(Color.secondary.opacity(0.3))
                                .frame(width: 1, height: 50)
                            
                            VStack(spacing: 8) {
                                Image(systemName: "arrow.down")
                                    .foregroundColor(.blue)
                                    .font(.caption)
                                Text("Low")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                    .textCase(.uppercase)
                                    .tracking(0.5)
                                Text("\(Int(weather.temperatureLow))°")
                                    .font(.title)
                                    .fontWeight(.semibold)
                            }
                        }
                    }
                }
                .frame(maxWidth: .infinity)
                .padding(.horizontal, 24)
                .padding(.vertical, 32)
                .background(
                    // Gradient background based on weather condition
                    LinearGradient(
                        colors: getGradientColors(for: weather.condition),
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                )
                .overlay(
                    // Subtle border
                    RoundedRectangle(cornerRadius: 20)
                        .stroke(Color.white.opacity(0.2), lineWidth: 1)
                )
                .cornerRadius(20)
                .shadow(color: Color.black.opacity(0.1), radius: 10, x: 0, y: 5)
                
                // Enhanced additional details with better spacing
                LazyVGrid(columns: [
                    GridItem(.flexible(), spacing: 16),
                    GridItem(.flexible(), spacing: 16)
                ], spacing: 16) {
                    
                    // Humidity card
                    DetailCard(
                        icon: "💧",
                        title: "Humidity",
                        value: "\(weather.humidity)%",
                        subtitle: getHumidityDescription(weather.humidity)
                    )
                    
                    // Condition card
                    DetailCard(
                        icon: getWeatherEmoji(weather.condition),
                        title: "Condition",
                        value: weather.condition.name.capitalized,
                        subtitle: "Today"
                    )
                    
                    // Feels like card (if you have this data)
                    DetailCard(
                        icon: "🌡️",
                        title: "Feels like",
                        value: "\(Int(weather.temperatureHigh))°",
                        subtitle: "Actual temp"
                    )
                    
                    // Weather index card
                    DetailCard(
                        icon: "📊",
                        title: "Weather Index",
                        value: getWeatherIndex(weather),
                        subtitle: "Overall rating"
                    )
                }
                
                Spacer(minLength: 20)
            }
            .padding(.horizontal, 20)
        }
        .background(Color(.systemGroupedBackground))
        .navigationTitle("Weather Details")
        .navigationBarTitleDisplayMode(.inline)
    }
    
    // Helper function to get gradient colors based on weather condition
    private func getGradientColors(for condition: WeatherCondition) -> [Color] {
        switch condition {
        case .clear:
            return [Color.orange.opacity(0.3), Color.yellow.opacity(0.2)]
        case .clouds:
            return [Color.gray.opacity(0.3), Color.blue.opacity(0.2)]
        case .rain:
            return [Color.blue.opacity(0.3), Color.indigo.opacity(0.2)]
        case .snow:
            return [Color.white.opacity(0.3), Color.blue.opacity(0.1)]
        default:
            return [Color.blue.opacity(0.3), Color.purple.opacity(0.2)]
        }
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
        // Simple weather scoring based on temperature and humidity
        let tempScore = (weather.temperatureHigh > 20 && weather.temperatureHigh < 30) ? 1 : 0
        let humidityScore = (weather.humidity > 30 && weather.humidity < 70) ? 1 : 0
        let total = tempScore + humidityScore
        
        switch total {
        case 2: return "Excellent"
        case 1: return "Good"
        default: return "Fair"
        }
    }
}

// Reusable detail card component
struct DetailCard: View {
    let icon: String
    let title: String
    let value: String
    let subtitle: String
    
    var body: some View {
        VStack(spacing: 12) {
            Text(icon)
                .font(.title)
            
            VStack(spacing: 4) {
                Text(title)
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .textCase(.uppercase)
                    .tracking(0.5)
                
                Text(value)
                    .font(.title2)
                    .fontWeight(.bold)
                    .lineLimit(2)
                    .multilineTextAlignment(.center)
                
                Text(subtitle)
                    .font(.caption2)
                    .foregroundColor(.secondary)
                    .opacity(0.8)
            }
        }
        .frame(maxWidth: .infinity)
        .frame(height: 120)
        .padding()
        .background(Color(.secondarySystemGroupedBackground))
        .cornerRadius(16)
        .shadow(color: Color.black.opacity(0.05), radius: 4, x: 0, y: 2)
    }
}

#Preview {
    NavigationView {
        WeatherDetailView(
            weather: Weather(
                date: Kotlinx_datetimeLocalDate.companion.parse(isoString: "2024-01-15"),
                condition: WeatherCondition.clear,
                temperatureHigh: 25.0,
                temperatureLow: 15.0,
                temperatureCurrent: 25.0,
                humidity: 65,
                icon: "01d",
                description: "Clear sky with pleasant weather",
                pressure: 1015.0,
                windSpeed: 10.0,
                windDirection: 180,
                visibility: 10.0,
                uvIndex: 5,
                precipitationChance: 10,
                precipitationAmount: 0.0,
                cloudCover: 20,
                feelsLike: 25.0,
                dewPoint: 12.0,
                sunrise: "06:30",
                sunset: "18:45",
                moonPhase: "Waxing Crescent",
                airQuality: nil
            )
        )
    }
}
