import SwiftUI
import shared

struct WeatherDetailCard: View {
    let title: String
    let icon: String
    let content: AnyView
    
    init<Content: View>(title: String, icon: String, @ViewBuilder content: () -> Content) {
        self.title = title
        self.icon = icon
        self.content = AnyView(content())
    }
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack(spacing: 8) {
                Text(icon)
                    .font(.title2)
                Text(title)
                    .font(.headline)
                    .fontWeight(.semibold)
                    .foregroundColor(.primary)
                Spacer()
            }
            
            content
        }
        .padding(16)
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(Color(.systemGray6))
        )
        .shadow(color: .black.opacity(0.05), radius: 4, x: 0, y: 2)
    }
}

struct TemperatureDetailCard: View {
    let weather: Weather
    
    var body: some View {
        WeatherDetailCard(title: "Temperature", icon: "🌡️") {
            VStack(spacing: 8) {
                if let current = weather.temperatureCurrent {
                    DetailRow(label: "Current", value: "\(Int(current))°C", isHighlight: true)
                }
                if let feelsLike = weather.feelsLike {
                    DetailRow(label: "Feels like", value: "\(Int(feelsLike))°C")
                }
                DetailRow(label: "High", value: "\(Int(weather.temperatureHigh))°C")
                DetailRow(label: "Low", value: "\(Int(weather.temperatureLow))°C")
                if let dewPoint = weather.dewPoint {
                    DetailRow(label: "Dew point", value: "\(Int(dewPoint))°C")
                }
            }
        }
    }
}

struct WindDetailCard: View {
    let weather: Weather
    
    var body: some View {
        WeatherDetailCard(title: "Wind", icon: "💨") {
            VStack(spacing: 8) {
                if let speed = weather.windSpeed {
                    DetailRow(label: "Speed", value: "\(Int(speed)) km/h", isHighlight: true)
                }
                if let direction = weather.windDirection {
                    DetailRow(label: "Direction", value: "\(direction)° \(getWindDirection(direction))")
                }
            }
        }
    }
    
    private func getWindDirection(_ degrees: Int32) -> String {
        let index = Int((Double(degrees) + 22.5) / 45) % 8
        let directions = ["N", "NE", "E", "SE", "S", "SW", "W", "NW"]
        return directions[index]
    }
}

struct AtmosphericDetailCard: View {
    let weather: Weather
    
    var body: some View {
        WeatherDetailCard(title: "Atmospheric", icon: "📊") {
            VStack(spacing: 8) {
                if let pressure = weather.pressure {
                    DetailRow(label: "Pressure", value: "\(Int(pressure)) hPa", isHighlight: true)
                }
                DetailRow(label: "Humidity", value: "\(weather.humidity)%")
                if let visibility = weather.visibility {
                    DetailRow(label: "Visibility", value: "\(Int(visibility)) km")
                }
                if let cloudCover = weather.cloudCover {
                    DetailRow(label: "Cloud cover", value: "\(cloudCover)%")
                }
            }
        }
    }
}

struct PrecipitationDetailCard: View {
    let weather: Weather
    
    var body: some View {
        WeatherDetailCard(title: "Precipitation", icon: "💧") {
            VStack(spacing: 8) {
                if let chance = weather.precipitationChance {
                    DetailRow(label: "Chance", value: "\(chance)%", isHighlight: true)
                }
                if let amount = weather.precipitationAmount {
                    DetailRow(label: "Amount", value: "\(amount, specifier: "%.1f") mm")
                }
            }
        }
    }
}

struct UVIndexDetailCard: View {
    let weather: Weather
    
    var body: some View {
        WeatherDetailCard(title: "UV Index", icon: "☀️") {
            if let uvIndex = weather.uvIndex {
                VStack(spacing: 8) {
                    DetailRow(label: "UV Index", value: "\(uvIndex)", isHighlight: true)
                    DetailRow(label: "Category", value: getUVCategory(Int(uvIndex)))
                    Text(getUVAdvice(Int(uvIndex)))
                        .font(.caption)
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.leading)
                }
            }
        }
    }
    
    private func getUVCategory(_ uvIndex: Int) -> String {
        switch uvIndex {
        case 0...2: return "Low"
        case 3...5: return "Moderate"
        case 6...7: return "High"
        case 8...10: return "Very High"
        default: return "Extreme"
        }
    }
    
    private func getUVAdvice(_ uvIndex: Int) -> String {
        switch uvIndex {
        case 0...2: return "Minimal sun protection required"
        case 3...5: return "Seek shade during midday hours"
        case 6...7: return "Sun protection essential"
        case 8...10: return "Extra protection needed"
        default: return "Take all precautions"
        }
    }
}

struct AirQualityDetailCard: View {
    let weather: Weather
    
    var body: some View {
        if let airQuality = weather.airQuality {
            WeatherDetailCard(title: "Air Quality", icon: "🌫️") {
                VStack(spacing: 8) {
                    DetailRow(label: "AQI", value: "\(airQuality.aqi)", isHighlight: true)
                    DetailRow(label: "Category", value: airQuality.category.name.replacingOccurrences(of: "_", with: " "))
                    if let pm25 = airQuality.pm25 {
                        DetailRow(label: "PM2.5", value: "\(pm25, specifier: "%.1f") μg/m³")
                    }
                    if let pm10 = airQuality.pm10 {
                        DetailRow(label: "PM10", value: "\(pm10, specifier: "%.1f") μg/m³")
                    }
                }
            }
        }
    }
}

struct SunMoonDetailCard: View {
    let weather: Weather
    
    var body: some View {
        WeatherDetailCard(title: "Sun & Moon", icon: "🌅") {
            VStack(spacing: 8) {
                if let sunrise = weather.sunrise {
                    DetailRow(label: "Sunrise", value: sunrise, isHighlight: true)
                }
                if let sunset = weather.sunset {
                    DetailRow(label: "Sunset", value: sunset)
                }
                if let moonPhase = weather.moonPhase {
                    DetailRow(label: "Moon phase", value: moonPhase)
                }
            }
        }
    }
}

struct DetailRow: View {
    let label: String
    let value: String
    let isHighlight: Bool
    
    init(label: String, value: String, isHighlight: Bool = false) {
        self.label = label
        self.value = value
        self.isHighlight = isHighlight
    }
    
    var body: some View {
        HStack {
            Text(label)
                .font(.body)
                .foregroundColor(.secondary)
            Spacer()
            Text(value)
                .font(isHighlight ? .headline : .body)
                .fontWeight(isHighlight ? .semibold : .regular)
                .foregroundColor(isHighlight ? .primary : .secondary)
        }
    }
}

#Preview {
    VStack(spacing: 16) {
        TemperatureDetailCard(weather: Weather(
            date: LocalDate(year: 2024, month: 1, dayOfMonth: 15),
            condition: WeatherCondition.clear,
            temperatureHigh: 25.0,
            temperatureLow: 15.0,
            temperatureCurrent: 22.0,
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
            feelsLike: 24.0,
            dewPoint: 12.0,
            sunrise: "06:30",
            sunset: "18:45",
            moonPhase: "Waxing Crescent",
            airQuality: nil
        ))
        
        HStack(spacing: 16) {
            WindDetailCard(weather: Weather(
                date: LocalDate(year: 2024, month: 1, dayOfMonth: 15),
                condition: WeatherCondition.clear,
                temperatureHigh: 25.0,
                temperatureLow: 15.0,
                humidity: 65,
                icon: "01d",
                description: "Clear",
                windSpeed: 12.5,
                windDirection: 225
            ))
            .frame(maxWidth: .infinity)
            
            AtmosphericDetailCard(weather: Weather(
                date: LocalDate(year: 2024, month: 1, dayOfMonth: 15),
                condition: WeatherCondition.clear,
                temperatureHigh: 25.0,
                temperatureLow: 15.0,
                humidity: 65,
                icon: "01d",
                description: "Clear",
                pressure: 1013.2,
                visibility: 10.0,
                cloudCover: 15
            ))
            .frame(maxWidth: .infinity)
        }
    }
    .padding()
}