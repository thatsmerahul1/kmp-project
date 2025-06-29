import SwiftUI

struct ContentView: View {
    var body: some View {
        NavigationView {
            WeatherListView()
                .navigationTitle("Weather Forecast")
        }
    }
}

#Preview {
    ContentView()
}