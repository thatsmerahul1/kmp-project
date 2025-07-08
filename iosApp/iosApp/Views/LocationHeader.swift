import SwiftUI
import shared

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

#Preview {
    LocationHeader(
        currentLocation: LocationData(
            latitude: 12.9716,
            longitude: 77.5946,
            cityName: "Bengaluru",
            countryName: "India",
            state: "Karnataka",
            pincode: nil,
            displayName: "Bengaluru, Karnataka, India",
            isCurrentLocation: false,
            accuracy: nil,
            timestamp: Int64(Date().timeIntervalSince1970 * 1000)
        ),
        isLocationLoading: false,
        onLocationTap: {}
    )
    .padding()
}