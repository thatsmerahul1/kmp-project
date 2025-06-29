import SwiftUI
import shared

@main
struct WeatherApp: App {
    
    init() {
        // Initialize Koin for dependency injection
        KoinHelper.shared.initKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}