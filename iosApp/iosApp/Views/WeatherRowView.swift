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
            VStack(alignment: .leading, spacing: 10) {}
            .padding(32)
            .background(
              EllipticalGradient(
                stops: [
                  Gradient.Stop(color: Color(red: 0.27, green: 0.15, blue: 0.55), location: 0.00),
                  Gradient.Stop(color: Color(red: 0.18, green: 0.2, blue: 0.35), location: 1.00),
                ],
                center: UnitPoint(x: 0.93, y: 0.74)
              )
            )
            .cornerRadius(44)
            .shadow(color: Color(red: 0.29, green: 0.22, blue: 0.5).opacity(0.7), radius: 50, x: 0, y: 20)
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
