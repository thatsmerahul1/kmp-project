package com.weather.domain.service

import com.weather.domain.model.LocationData
import com.weather.domain.model.LocationSearchResult
import com.weather.domain.model.LocationSource
import kotlinx.coroutines.flow.Flow

interface LocationService {
    suspend fun getCurrentLocation(): LocationData
    suspend fun getLocationBySource(source: LocationSource): LocationData?
    suspend fun searchLocations(query: String): List<LocationSearchResult>
    suspend fun setUserSelectedLocation(location: LocationData)
    suspend fun getUserSelectedLocation(): LocationData?
    suspend fun clearUserSelectedLocation()
    fun observeCurrentLocation(): Flow<LocationData>
    suspend fun getDefaultLocation(): LocationData
}