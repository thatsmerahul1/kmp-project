package com.weather.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val countryName: String? = null,
    val state: String? = null,
    val pincode: String? = null,
    val displayName: String = cityName,
    val isCurrentLocation: Boolean = false,
    val accuracy: Float? = null,
    val timestamp: Long = 0L
) {
    fun toCoordinateString(): String = "$latitude,$longitude"
    
    fun getFullDisplayName(): String = buildString {
        append(cityName)
        state?.let { append(", $it") }
        countryName?.let { append(", $it") }
    }
}

@Serializable
data class LocationSearchResult(
    val locationData: LocationData,
    val relevanceScore: Float = 1.0f,
    val searchType: LocationSearchType
)

@Serializable
enum class LocationSearchType {
    CITY,
    PINCODE,
    GPS,
    CACHED,
    DEFAULT
}

sealed class LocationSource {
    object GPS : LocationSource()
    data class UserSelected(val location: LocationData) : LocationSource()
    data class Search(val query: String) : LocationSource()
    object Default : LocationSource()
}