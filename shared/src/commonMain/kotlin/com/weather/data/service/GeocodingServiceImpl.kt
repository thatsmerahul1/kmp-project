package com.weather.data.service

import com.weather.domain.service.GeocodingService
import com.weather.domain.model.LocationData
import com.weather.domain.model.LocationSearchResult
import com.weather.domain.model.LocationSearchType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class GeocodingServiceImpl(
    private val httpClient: HttpClient,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : GeocodingService {

    companion object {
        private const val GEOCODING_BASE_URL = "https://geocoding-api.open-meteo.com/v1"
        private const val REVERSE_GEOCODING_URL = "https://api.bigdatacloud.net/data/reverse-geocode-client"
    }

    override suspend fun searchLocations(query: String, limit: Int): List<LocationSearchResult> {
        return try {
            val response: GeocodingResponse = httpClient.get("$GEOCODING_BASE_URL/search") {
                parameter("name", query)
                parameter("count", limit)
                parameter("language", "en")
                parameter("format", "json")
            }.body()

            response.results?.map { result ->
                LocationSearchResult(
                    locationData = LocationData(
                        latitude = result.latitude,
                        longitude = result.longitude,
                        cityName = result.name,
                        countryName = result.country,
                        state = result.admin1,
                        displayName = buildString {
                            append(result.name)
                            result.admin1?.let { append(", $it") }
                            result.country?.let { append(", $it") }
                        }
                    ),
                    searchType = if (query.matches(Regex("\\d+"))) LocationSearchType.PINCODE else LocationSearchType.CITY
                )
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun reverseGeocode(latitude: Double, longitude: Double): LocationData? {
        return try {
            println("GeocodingService: Reverse geocoding coordinates: $latitude, $longitude")
            val response: ReverseGeocodeResponse = httpClient.get(REVERSE_GEOCODING_URL) {
                parameter("latitude", latitude)
                parameter("longitude", longitude)
                parameter("localityLanguage", "en")
            }.body()

            val cityName = response.city ?: response.locality ?: "Unknown"
            println("GeocodingService: Reverse geocoding result - City: $cityName, Country: ${response.countryName}, State: ${response.principalSubdivision}")

            val locationData = LocationData(
                latitude = latitude,
                longitude = longitude,
                cityName = cityName,
                countryName = response.countryName,
                state = response.principalSubdivision,
                pincode = response.postcode,
                displayName = buildString {
                    append(cityName)
                    response.principalSubdivision?.let { append(", $it") }
                    response.countryName?.let { append(", $it") }
                },
                isCurrentLocation = true
            )
            
            println("GeocodingService: Created LocationData: ${locationData.displayName}")
            locationData
        } catch (e: Exception) {
            println("GeocodingService: Reverse geocoding failed: ${e.message}")
            null
        }
    }

    override suspend fun getLocationByPincode(pincode: String): LocationData? {
        val results = searchLocations(pincode, 1)
        return results.firstOrNull()?.locationData
    }

    override suspend fun getLocationByCity(cityName: String, countryCode: String?): LocationData? {
        val query = if (countryCode != null) "$cityName,$countryCode" else cityName
        val results = searchLocations(query, 1)
        return results.firstOrNull()?.locationData
    }
}

@Serializable
data class GeocodingResponse(
    val results: List<GeocodingResult>? = null
)

@Serializable
data class GeocodingResult(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val elevation: Double? = null,
    val feature_code: String? = null,
    val country_code: String? = null,
    val admin1_id: Long? = null,
    val admin2_id: Long? = null,
    val admin3_id: Long? = null,
    val admin4_id: Long? = null,
    val timezone: String? = null,
    val population: Long? = null,
    val postcodes: List<String>? = null,
    val country_id: Long? = null,
    val country: String? = null,
    val admin1: String? = null,
    val admin2: String? = null,
    val admin3: String? = null,
    val admin4: String? = null
)

@Serializable
data class ReverseGeocodeResponse(
    val latitude: Double,
    val longitude: Double,
    val continent: String? = null,
    val lookupSource: String? = null,
    val continentCode: String? = null,
    val localityLanguageRequested: String? = null,
    val city: String? = null,
    val countryName: String? = null,
    val countryCode: String? = null,
    val postcode: String? = null,
    val principalSubdivision: String? = null,
    val principalSubdivisionCode: String? = null,
    val plusCode: String? = null,
    val locality: String? = null,
    val localityInfo: LocalityInfo? = null
)

@Serializable
data class LocalityInfo(
    val administrative: List<Administrative>? = null,
    val informative: List<Informative>? = null
)

@Serializable
data class Administrative(
    val name: String? = null,
    val description: String? = null,
    val isoName: String? = null,
    val order: Int? = null,
    val adminLevel: Int? = null,
    val isoCode: String? = null,
    val wikidataId: String? = null,
    val geonameId: Long? = null
)

@Serializable
data class Informative(
    val name: String? = null,
    val description: String? = null,
    val isoName: String? = null,
    val order: Int? = null,
    val isoCode: String? = null,
    val wikidataId: String? = null,
    val geonameId: Long? = null
)