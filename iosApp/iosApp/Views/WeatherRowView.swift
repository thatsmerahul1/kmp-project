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
            
            Spacer(minLength: 16)
            
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
            temperatureCurrent: 20.0,
            humidity: 65,
            icon: "01d",
            description: "Clear sky",
            pressure: 1013.2,
            windSpeed: 12.5,
            windDirection: 225,
            visibility: 10.0,
            uvIndex: 6,
            precipitationChance: 20,
            precipitationAmount: 0.5,
            cloudCover: 15,
            feelsLike: 21.0,
            dewPoint: 12.0,
            sunrise: "06:30",
            sunset: "18:45",
            moonPhase: "Waxing Crescent",
            airQuality: nil
        )
    )
}
