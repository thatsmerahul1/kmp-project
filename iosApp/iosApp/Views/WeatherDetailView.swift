import SwiftUI
import shared

struct WeatherDetailView: View {
    let weather: Weather
    
    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
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
                                .font(.title)
                                .fontWeight(.bold)
                        }
                        
                        VStack {
                            Text("Low")
                                .font(.caption)
                                .foregroundColor(.secondary)
                            Text("\(Int(weather.temperatureLow))°")
                                .font(.title)
                                .fontWeight(.bold)
                        }
                    }
                }
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(15)
                
                // Additional details
                VStack(spacing: 16) {
                    HStack {
                        Label("Humidity", systemImage: "humidity")
                            .foregroundColor(.blue)
                        Spacer()
                        Text("\(weather.humidity)%")
                            .fontWeight(.medium)
                    }
                    
                    Divider()
                    
                    HStack {
                        Label("Date", systemImage: "calendar")
                            .foregroundColor(.green)
                        Spacer()
                        Text(formatDetailDate(weather.date))
                            .fontWeight(.medium)
                    }
                }
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(15)
                
                Spacer()
            }
            .padding()
        }
        .navigationTitle(getDayName(weather.date))
        .navigationBarTitleDisplayMode(.large)
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