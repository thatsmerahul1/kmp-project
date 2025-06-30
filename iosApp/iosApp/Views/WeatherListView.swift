import SwiftUI
import shared

class WeatherViewModelWrapper: ObservableObject {
    @Published var uiState: WeatherUiState
    private let viewModel: IOSWeatherViewModel
    
    init() {
        self.viewModel = IOSWeatherViewModel()
        self.uiState = viewModel.uiState.value as! WeatherUiState
        
        // Start observing state changes
        startObserving()
    }
    
    private func startObserving() {
        // For now, we'll use a simple timer to poll for changes
        // In a production app, you'd want to use proper Flow observation
        Timer.scheduledTimer(withTimeInterval: 0.5, repeats: true) { [weak self] _ in
            DispatchQueue.main.async {
                if let newState = self?.viewModel.uiState.value as? WeatherUiState {
                    self?.uiState = newState
                }
            }
        }
    }
    
    func onEvent(_ event: WeatherUiEvent) {
        viewModel.onEvent(event: event)
    }
    
    func loadWeather() {
        viewModel.loadWeather()
    }
    
    func refreshWeather() {
        viewModel.refreshWeather()
    }
    
    func retryLoad() {
        viewModel.retryLoad()
    }
    
    deinit {
        viewModel.dispose()
    }
}

struct WeatherListView: View {
    @StateObject private var viewModelWrapper = WeatherViewModelWrapper()
    @State private var selectedWeather: Weather?
    
    var body: some View {
        NavigationView {
            VStack {
                if viewModelWrapper.uiState.isLoading {
                    VStack(spacing: 20) {
                        ProgressView()
                            .scaleEffect(1.5)
                        Text("Loading weather...")
                            .font(.headline)
                            .foregroundColor(.secondary)
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else if let error = viewModelWrapper.uiState.error {
                    VStack(spacing: 20) {
                        Image(systemName: "exclamationmark.triangle")
                            .font(.system(size: 50))
                            .foregroundColor(.orange)
                        
                        Text("Error")
                            .font(.title)
                            .fontWeight(.bold)
                        
                        Text(error)
                            .font(.body)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal)
                        
                        Button("Retry") {
                            viewModelWrapper.retryLoad()
                        }
                        .buttonStyle(.borderedProminent)
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else {
                    List(viewModelWrapper.uiState.weatherList, id: \.date) { weather in
                        Button(action: {
                            selectedWeather = weather
                        }) {
                            WeatherRowView(weather: weather)
                        }
                        .buttonStyle(PlainButtonStyle())
                    }
                    .refreshable {
                        viewModelWrapper.refreshWeather()
                    }
                }
            }
            .navigationTitle("Weather Forecast")
            .onAppear {
                viewModelWrapper.loadWeather()
            }
            .sheet(item: Binding<Weather?>(
                get: { selectedWeather },
                set: { selectedWeather = $0 }
            )) { weather in
                WeatherDetailView(weather: weather)
            }
        }
    }
}

extension Weather: @retroactive Identifiable {
    public var id: String {
        return "\(date.year)-\(date.month.ordinal)-\(date.dayOfMonth)"
    }
}

#Preview {
    NavigationView {
        WeatherListView()
            .navigationTitle("Weather Forecast")
    }
}