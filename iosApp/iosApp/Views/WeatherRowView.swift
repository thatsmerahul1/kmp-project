import SwiftUI
import shared

struct WeatherRowView: View {
    let weather: Weather
    
    var body: some View {
        HStack {
            VStack(alignment: .leading) {
                Text(getDayName(weather.date))
                    .font(.headline)
                    .fontWeight(.bold)
                
                Text(formatDate(weather.date))
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            VStack {
                Text(getWeatherEmoji(weather.condition))
                    .font(.largeTitle)
                
                Text(weather.description_)
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .lineLimit(1)
            }
            
            Spacer()
            
            VStack(alignment: .trailing) {
                Text("\(Int(weather.temperatureHigh))°")
                    .font(.title2)
                    .fontWeight(.bold)
                
                Text("\(Int(weather.temperatureLow))°")
                    .font(.caption)
                    .foregroundColor(.secondary)
                
                Text("\(weather.humidity)%")
                    .font(.caption2)
                    .foregroundColor(.secondary)
            }
        }
        .padding(.vertical, 8)
    }
}

// Utility functions moved to WeatherUtils.swift

#Preview {
    WeatherRowView(
        weather: Weather(
            date: Kotlinx_datetimeLocalDate.companion.parse(isoString: "2024-01-15"),
            condition: WeatherCondition.clear,
            temperatureHigh: 22.0,
            temperatureLow: 15.0,
            humidity: 65,
            icon: "01d",
            description: "Clear sky"
        )
    )
}