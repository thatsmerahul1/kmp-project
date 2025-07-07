import SwiftUI
import shared

class WeatherViewModelWrapper: ObservableObject {
    @Published var uiState: WeatherUiState
    private let viewModel: IOSWeatherViewModel
    
    init() {
        self.viewModel = IOSWeatherViewModel()
        self.uiState = viewModel.getCurrentState()
        
        // Start observing state changes using proper Flow observation
        startObserving()
    }
    
    private func startObserving() {
        // Use the StateFlowWrapper for proper reactive observation
        viewModel.uiStateWrapper.subscribe { [weak self] newState in
            DispatchQueue.main.async {
                self?.uiState = newState
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