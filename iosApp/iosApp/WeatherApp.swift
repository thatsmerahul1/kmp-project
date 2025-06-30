import SwiftUI
import shared

@main
struct WeatherApp: App {
    
    init() {
        // Initialize Koin for dependency injection
        KoinHelper.shared.doInitKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            NavigationView {
                WeatherListView()
                    .navigationTitle("Weather Forecast")
            }
        }
    }
}