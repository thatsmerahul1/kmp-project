import SwiftUI
import shared

func getWeatherEmoji(_ condition: WeatherCondition) -> String {
    switch condition {
    case WeatherCondition.clear:
        return "☀️"
    case WeatherCondition.clouds:
        return "☁️"
    case WeatherCondition.rain:
        return "🌧️"
    case WeatherCondition.drizzle:
        return "🌦️"
    case WeatherCondition.snow:
        return "❄️"
    case WeatherCondition.thunderstorm:
        return "⛈️"
    case WeatherCondition.fog:
        return "🌫️"
    case WeatherCondition.mist:
        return "🌫️"
    case WeatherCondition.smoke:
        return "💨"
    case WeatherCondition.haze:
        return "🌫️"
    case WeatherCondition.dust:
        return "💨"
    case WeatherCondition.sand:
        return "💨"
    case WeatherCondition.ash:
        return "💨"
    case WeatherCondition.squall:
        return "💨"
    case WeatherCondition.tornado:
        return "🌪️"
    default:
        return "🌤️"
    }
}

func getDayName(_ date: Kotlinx_datetimeLocalDate) -> String {
    let today = Calendar.current.dateComponents([.year, .month, .day], from: Date())
    let weatherDate = DateComponents(year: Int(date.year), month: Int(date.month.ordinal), day: Int(date.dayOfMonth))
    
    if today.year == weatherDate.year && today.month == weatherDate.month && today.day == weatherDate.day {
        return "Today"
    }
    
    let dayOfWeek = date.dayOfWeek.name.lowercased().capitalized
    return dayOfWeek
}

func formatDate(_ date: Kotlinx_datetimeLocalDate) -> String {
    let month = date.month.name.lowercased().capitalized
    let day = date.dayOfMonth
    return "\(month) \(day)"
}

func formatDetailDate(_ date: Kotlinx_datetimeLocalDate) -> String {
    let month = date.month.name.lowercased().capitalized
    let day = date.dayOfMonth
    let year = date.year
    return "\(month) \(day), \(year)"
} 