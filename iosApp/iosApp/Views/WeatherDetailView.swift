import SwiftUI
import shared

struct WeatherDetailView: View {
    let weather: Weather
    
    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                // Location and date header
                VStack(spacing: 4) {
                    Text("Bengaluru, India")
                        .font(.title2)
                        .fontWeight(.bold)
                    Text(formatDetailDate(weather.date))
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                .padding(.top)
                
                // Main weather card
                VStack(spacing: 16) {
                    Text(getWeatherEmoji(weather.condition))
                        .font(.system(size: 100))
                    
                    Text(weather.description_)
                        .font(.title2)
                        .fontWeight(.medium)
                        .multilineTextAlignment(.center)
                    
                    HStack(spacing: 40) {
                        VStack {
                            Text("High")
                                .font(.caption)
                                .foregroundColor(.secondary)
                            Text("\(Int(weather.temperatureHigh))°")
                                .font(.largeTitle)
                                .fontWeight(.bold)
                        }
                        
                        VStack {
                            Text("Low")
                                .font(.caption)
                                .foregroundColor(.secondary)
                            Text("\(Int(weather.temperatureLow))°")
                                .font(.largeTitle)
                                .fontWeight(.bold)
                        }
                    }
                }
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(15)
                
                // Additional details cards
                HStack(spacing: 16) {
                    // Humidity card
                    VStack(spacing: 8) {
                        Text("💧")
                            .font(.title)
                        Text("Humidity")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        Text("\(weather.humidity)%")
                            .font(.title2)
                            .fontWeight(.bold)
                    }
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color(.systemGray6))
                    .cornerRadius(12)
                    
                    // Condition card
                    VStack(spacing: 8) {
                        Text(getWeatherEmoji(weather.condition))
                            .font(.title)
                        Text("Condition")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        Text(weather.condition.name.capitalized)
                            .font(.title3)
                            .fontWeight(.bold)
                            .multilineTextAlignment(.center)
                    }
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color(.systemGray6))
                    .cornerRadius(12)
                }
                
                Spacer()
            }
            .padding()
        }
        .navigationTitle("Weather Details")
        .navigationBarTitleDisplayMode(.inline)
    }
}

// formatDetailDate function moved to WeatherUtils.swift

#Preview {
    NavigationView {
        WeatherDetailView(
            weather: Weather(
                date: Kotlinx_datetimeLocalDate.companion.parse(isoString: "2024-01-15"),
                condition: WeatherCondition.clear,
                temperatureHigh: 25.0,
                temperatureLow: 15.0,
                humidity: 65,
                icon: "01d",
                description: "Clear sky"
            )
        )
    }
} 