import SwiftUI

struct WeatherRowView: View {
    let weather: WeatherData
    
    var body: some View {
        HStack {
            VStack(alignment: .leading) {
                Text(weather.dayName)
                    .font(.headline)
                    .fontWeight(.bold)
                
                Text(weather.date)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            VStack {
                Text(weather.emoji)
                    .font(.largeTitle)
                
                Text(weather.description)
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
            }
            
            Spacer()
            
            VStack(alignment: .trailing) {
                Text("\(weather.highTemp)°")
                    .font(.title2)
                    .fontWeight(.bold)
                
                Text("\(weather.lowTemp)°")
                    .font(.body)
                    .foregroundColor(.secondary)
                
                Text("\(weather.humidity)%")
                    .font(.caption2)
                    .foregroundColor(.secondary)
            }
        }
        .padding(.vertical, 4)
    }
}

#Preview {
    WeatherRowView(
        weather: WeatherData(
            dayName: "Today",
            date: "2024-06-30",
            condition: "Clear",
            emoji: "☀️",
            highTemp: 22,
            lowTemp: 15,
            humidity: 65,
            description: "Clear sky"
        )
    )
    .padding()
}