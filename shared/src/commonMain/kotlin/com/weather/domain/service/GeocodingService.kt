package com.weather.domain.service

import com.weather.domain.model.LocationData
import com.weather.domain.model.LocationSearchResult

interface GeocodingService {
    suspend fun searchLocations(query: String, limit: Int = 5): List<LocationSearchResult>
    suspend fun reverseGeocode(latitude: Double, longitude: Double): LocationData?
    suspend fun getLocationByPincode(pincode: String): LocationData?
    suspend fun getLocationByCity(cityName: String, countryCode: String? = null): LocationData?
}