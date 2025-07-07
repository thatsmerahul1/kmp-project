package com.weather.data.remote

import com.weather.data.mapper.WeatherMapper.toDomain
import com.weather.data.remote.api.WeatherApi
import com.weather.data.remote.dto.WeatherItemDto
import com.weather.domain.model.Weather
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface RemoteWeatherDataSource {
    suspend fun getWeatherForecast(location: String): List<Weather>
}

class RemoteWeatherDataSourceImpl(
    private val weatherApi: WeatherApi,
    private val httpClient: HttpClient
) : RemoteWeatherDataSource {

    override suspend fun getWeatherForecast(location: String): List<Weather> {
        // First, geocode the location to get coordinates
        val coordinates = geocodeLocation(location)
        
        // Get weather forecast using coordinates
        val response = weatherApi.getWeatherForecast(
            latitude = coordinates.latitude,
            longitude = coordinates.longitude,
            days = 7
        )
        
        // Convert daily arrays to list of WeatherItemDto objects
        val weatherItems = response.daily.time.mapIndexed { index, date ->
            WeatherItemDto(
                date = date,
                temperatureMax = response.daily.temperatureMax[index],
                temperatureMin = response.daily.temperatureMin[index],
                temperatureCurrent = response.daily.temperatureCurrent?.getOrNull(index),
                humidity = response.daily.humidity[index],
                weatherCode = response.daily.weatherCode[index],
                windSpeed = response.daily.windSpeed[index],
                // Enhanced weather fields
                pressure = response.daily.pressure?.getOrNull(index),
                windDirection = response.daily.windDirection?.getOrNull(index),
                visibility = response.daily.visibility?.getOrNull(index),
                uvIndex = response.daily.uvIndex?.getOrNull(index),
                precipitationChance = response.daily.precipitationChance?.getOrNull(index),
                precipitationAmount = response.daily.precipitationAmount?.getOrNull(index),
                cloudCover = response.daily.cloudCover?.getOrNull(index),
                feelsLike = response.daily.feelsLike?.getOrNull(index),
                dewPoint = response.daily.dewPoint?.getOrNull(index),
                sunrise = response.daily.sunrise?.getOrNull(index),
                sunset = response.daily.sunset?.getOrNull(index)
            )
        }
        
        // Convert to domain objects
        return weatherItems.map { it.toDomain() }
    }
    
    private suspend fun geocodeLocation(location: String): Coordinates {
        // Use Open-Meteo's free geocoding API
        return try {
            val response = httpClient.get("https://geocoding-api.open-meteo.com/v1/search") {
                parameter("name", location)
                parameter("count", 1)
                parameter("language", "en")
                parameter("format", "json")
            }.body<GeocodingResponse>()
            
            val result = response.results.firstOrNull()
                ?: throw Exception("Location not found: $location")
            
            Coordinates(result.latitude, result.longitude)
        } catch (e: Exception) {
            // Fallback to London coordinates if geocoding fails
            Coordinates(51.5074, -0.1278)
        }
    }
}

@Serializable
data class GeocodingResponse(
    val results: List<GeocodingResult>
)

@Serializable
data class GeocodingResult(
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val country: String
)

data class Coordinates(
    val latitude: Double,
    val longitude: Double
)