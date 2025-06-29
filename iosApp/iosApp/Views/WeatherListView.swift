import SwiftUI

struct WeatherListView: View {
    var body: some View {
        List {
            ForEach(sampleWeatherData, id: \.id) { weather in
                WeatherRowView(weather: weather)
            }
        }
        .refreshable {
            // Refresh functionality will be added with KMP integration
        }
    }
}

struct WeatherData: Identifiable {
    let id = UUID()
    let dayName: String
    let date: String
    let condition: String
    let emoji: String
    let highTemp: Int
    let lowTemp: Int
    let humidity: Int
    let description: String
}

let sampleWeatherData = [
    WeatherData(dayName: "Today", date: "2024-06-30", condition: "Clear", emoji: "☀️", highTemp: 22, lowTemp: 15, humidity: 65, description: "Clear sky"),
    WeatherData(dayName: "Tomorrow", date: "2024-07-01", condition: "Partly Cloudy", emoji: "⛅", highTemp: 20, lowTemp: 13, humidity: 70, description: "Partly cloudy"),
    WeatherData(dayName: "Tuesday", date: "2024-07-02", condition: "Rain", emoji: "🌧️", highTemp: 18, lowTemp: 12, humidity: 85, description: "Light rain"),
    WeatherData(dayName: "Wednesday", date: "2024-07-03", condition: "Cloudy", emoji: "☁️", highTemp: 19, lowTemp: 14, humidity: 75, description: "Overcast"),
]

#Preview {
    WeatherListView()
}