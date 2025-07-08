import SwiftUI
import shared

// MARK: - Location Components (Temporary inline until added to Xcode project)

struct LocationHeader: View {
    let currentLocation: LocationData?
    let isLocationLoading: Bool
    let onLocationTap: () -> Void
    
    var body: some View {
        Button(action: onLocationTap) {
            HStack(spacing: 6) {
                if isLocationLoading {
                    ProgressView()
                        .scaleEffect(0.7)
                        .frame(width: 16, height: 16)
                } else {
                    Image(systemName: "location.fill")
                        .foregroundColor(.blue)
                        .font(.system(size: 14))
                }
                
                Text(currentLocation?.cityName ?? "Select Location")
                    .font(.system(size: 14, weight: .medium))
                    .foregroundColor(.primary)
                    .lineLimit(1)
                
                Image(systemName: "chevron.down")
                    .foregroundColor(.secondary)
                    .font(.system(size: 12))
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 6)
            .background(
                RoundedRectangle(cornerRadius: 8)
                    .fill(Color(.systemGray6))
            )
        }
        .buttonStyle(PlainButtonStyle())
    }
}

struct LocationPickerSheet: View {
    @Binding var isPresented: Bool
    let onLocationSelected: (LocationData) -> Void
    let onRequestCurrentLocation: () -> Void
    let searchResults: [LocationSearchResult]
    let onSearchQueryChanged: (String) -> Void
    let isSearching: Bool
    let isLocationLoading: Bool
    let currentLocation: LocationData?
    
    @State private var searchText = ""
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // Search field
                VStack(spacing: 16) {
                    LocationSearchField(
                        text: $searchText,
                        placeholder: "Search city or pincode...",
                        onSearchChanged: onSearchQueryChanged
                    )
                    
                    // Current Location Button
                    CurrentLocationButton(
                        currentLocation: currentLocation,
                        isLocationLoading: isLocationLoading,
                        onTap: onRequestCurrentLocation
                    )
                }
                .padding(.horizontal, 16)
                .padding(.top, 8)
                
                Divider()
                    .padding(.vertical, 16)
                
                // Search Results
                if isSearching {
                    VStack {
                        ProgressView("Searching...")
                            .frame(maxWidth: .infinity, maxHeight: .infinity)
                    }
                } else if !searchResults.isEmpty {
                    List(searchResults, id: \.locationData.displayName) { result in
                        LocationSearchResultRow(
                            result: result,
                            onTap: {
                                onLocationSelected(result.locationData)
                                isPresented = false
                            }
                        )
                    }
                    .listStyle(PlainListStyle())
                } else if !searchText.isEmpty {
                    VStack {
                        Image(systemName: "magnifyingglass")
                            .font(.system(size: 48))
                            .foregroundColor(.secondary)
                        Text("No locations found")
                            .font(.headline)
                            .foregroundColor(.secondary)
                            .padding(.top, 8)
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else {
                    VStack {
                        Image(systemName: "magnifyingglass")
                            .font(.system(size: 48))
                            .foregroundColor(.secondary)
                        Text("Search for a city or pincode")
                            .font(.headline)
                            .foregroundColor(.secondary)
                            .padding(.top, 8)
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                }
                
                Spacer()
            }
            .navigationTitle("Select Location")
            .navigationBarTitleDisplayMode(.inline)
            .navigationBarItems(
                trailing: Button("Done") {
                    isPresented = false
                }
            )
        }
    }
}

struct LocationSearchField: View {
    @Binding var text: String
    let placeholder: String
    let onSearchChanged: (String) -> Void
    
    var body: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.secondary)
            
            TextField(placeholder, text: $text)
                .textFieldStyle(PlainTextFieldStyle())
                .onChange(of: text) { newValue in
                    onSearchChanged(newValue)
                }
            
            if !text.isEmpty {
                Button(action: {
                    text = ""
                    onSearchChanged("")
                }) {
                    Image(systemName: "xmark.circle.fill")
                        .foregroundColor(.secondary)
                }
            }
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 8)
        .background(
            RoundedRectangle(cornerRadius: 10)
                .fill(Color(.systemGray6))
        )
    }
}

struct CurrentLocationButton: View {
    let currentLocation: LocationData?
    let isLocationLoading: Bool
    let onTap: () -> Void
    
    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 12) {
                if isLocationLoading {
                    ProgressView()
                        .frame(width: 24, height: 24)
                } else {
                    Image(systemName: "location.fill")
                        .foregroundColor(.white)
                        .font(.system(size: 16))
                        .frame(width: 24, height: 24)
                }
                
                VStack(alignment: .leading, spacing: 2) {
                    Text("Use Current Location")
                        .font(.system(size: 16, weight: .medium))
                        .foregroundColor(.white)
                    
                    if let location = currentLocation {
                        Text(location.displayName)
                            .font(.system(size: 14))
                            .foregroundColor(.white.opacity(0.8))
                            .lineLimit(1)
                    }
                }
                
                Spacer()
            }
            .padding(16)
            .background(
                RoundedRectangle(cornerRadius: 12)
                    .fill(Color.blue)
            )
        }
        .buttonStyle(PlainButtonStyle())
    }
}

struct LocationSearchResultRow: View {
    let result: LocationSearchResult
    let onTap: () -> Void
    
    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 12) {
                Image(systemName: "location")
                    .foregroundColor(.blue)
                    .font(.system(size: 16))
                
                VStack(alignment: .leading, spacing: 2) {
                    Text(result.locationData.cityName)
                        .font(.system(size: 16, weight: .medium))
                        .foregroundColor(.primary)
                        .multilineTextAlignment(.leading)
                    
                    if result.locationData.displayName != result.locationData.cityName {
                        Text(result.locationData.displayName)
                            .font(.system(size: 14))
                            .foregroundColor(.secondary)
                            .lineLimit(1)
                            .multilineTextAlignment(.leading)
                    }
                }
                
                Spacer()
            }
            .padding(.vertical, 4)
        }
        .buttonStyle(PlainButtonStyle())
    }
}

// MARK: - Main View Model and Screen

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
    @State private var showLocationPicker = false
    @State private var searchResults: [LocationSearchResult] = []
    @State private var isSearching = false
    
    var body: some View {
        NavigationView {
            VStack {
                if viewModelWrapper.uiState.isLoading && viewModelWrapper.uiState.weatherList.isEmpty {
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
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    LocationHeader(
                        currentLocation: viewModelWrapper.uiState.currentLocation,
                        isLocationLoading: viewModelWrapper.uiState.isLocationLoading,
                        onLocationTap: {
                            showLocationPicker = true
                            viewModelWrapper.onEvent(WeatherUiEvent.ShowLocationPicker())
                        }
                    )
                }
            }
            .sheet(item: Binding<Weather?>(
                get: { selectedWeather },
                set: { selectedWeather = $0 }
            )) { weather in
                WeatherDetailView(weather: weather)
            }
            .sheet(isPresented: $showLocationPicker) {
                LocationPickerSheet(
                    isPresented: $showLocationPicker,
                    onLocationSelected: { location in
                        viewModelWrapper.onEvent(WeatherUiEvent.SelectLocation(location: location))
                        showLocationPicker = false
                    },
                    onRequestCurrentLocation: {
                        viewModelWrapper.onEvent(WeatherUiEvent.RequestCurrentLocation())
                    },
                    searchResults: searchResults,
                    onSearchQueryChanged: { query in
                        if !query.isEmpty {
                            isSearching = true
                            viewModelWrapper.onEvent(WeatherUiEvent.SearchLocations(query: query))
                        } else {
                            searchResults = []
                            isSearching = false
                        }
                    },
                    isSearching: isSearching,
                    isLocationLoading: viewModelWrapper.uiState.isLocationLoading,
                    currentLocation: viewModelWrapper.uiState.currentLocation
                )
            }
            .onChange(of: viewModelWrapper.uiState.showLocationPicker) { showPicker in
                showLocationPicker = showPicker
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