import SwiftUI
import shared

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
                    SearchField(
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

struct SearchField: View {
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

#Preview {
    LocationPickerSheet(
        isPresented: .constant(true),
        onLocationSelected: { _ in },
        onRequestCurrentLocation: {},
        searchResults: [],
        onSearchQueryChanged: { _ in },
        isSearching: false,
        isLocationLoading: false,
        currentLocation: nil
    )
}