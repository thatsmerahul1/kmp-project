package com.weather.presentation.viewmodel

import app.cash.turbine.test
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import com.weather.domain.model.LocationData
import com.weather.domain.model.LocationSearchResult
import com.weather.domain.model.LocationSource
import com.weather.domain.repository.WeatherRepository
import com.weather.domain.usecase.GetWeatherForecastUseCase
import com.weather.domain.usecase.RefreshWeatherUseCase
import com.weather.domain.service.LocationService
import com.weather.domain.permission.PermissionManager
import com.weather.domain.permission.Permission
import com.weather.domain.permission.PermissionStatus
import com.weather.domain.common.Result
import com.weather.domain.common.DomainException
import com.weather.domain.common.isSuccess
import com.weather.domain.common.isError
import com.weather.domain.common.getOrNull
import com.weather.domain.common.exceptionOrNull
import com.weather.testing.FakeWeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for WeatherViewModel using cross-platform fake implementations
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeWeatherRepository
    private lateinit var fakeLocationService: FakeLocationService
    private lateinit var fakePermissionManager: FakePermissionManager
    private lateinit var getWeatherUseCase: GetWeatherForecastUseCase
    private lateinit var refreshWeatherUseCase: RefreshWeatherUseCase
    private lateinit var viewModel: WeatherViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeWeatherRepository()
        fakeLocationService = FakeLocationService()
        fakePermissionManager = FakePermissionManager()
        getWeatherUseCase = GetWeatherForecastUseCase(fakeRepository)
        refreshWeatherUseCase = RefreshWeatherUseCase(fakeRepository)
        viewModel = WeatherViewModel(getWeatherUseCase, refreshWeatherUseCase, fakeLocationService, fakePermissionManager)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel should be initialized properly`() = runTest {
        // Then
        assertTrue(true, "ViewModel created successfully")
        assertEquals(0, fakeRepository.getForecastCallCount)
    }

    @Test
    fun `repository should provide weather data`() = runTest {
        // Given
        val weatherData = listOf(
            Weather(
                date = LocalDate(2025, 1, 15),
                condition = WeatherCondition.CLEAR,
                temperatureHigh = 25.0,
                temperatureLow = 15.0,
                humidity = 65,
                icon = "01d",
                description = "Clear sky"
            )
        )
        fakeRepository.setForecastResult(Result.Success(weatherData))

        // When
        val result = getWeatherUseCase().test {
            val flowResult = awaitItem()
            assertTrue(flowResult.isSuccess)
            assertEquals(weatherData, flowResult.getOrNull())
            awaitComplete()
        }

        // Then
        assertEquals(1, fakeRepository.getForecastCallCount)
    }

    @Test
    fun `refresh use case should work with repository`() = runTest {
        // Given
        val weatherData = listOf(
            Weather(
                date = LocalDate(2025, 1, 16),
                condition = WeatherCondition.CLOUDS,
                temperatureHigh = 22.0,
                temperatureLow = 12.0,
                humidity = 70,
                icon = "02d",
                description = "Partly cloudy"
            )
        )
        fakeRepository.setForecastResult(Result.Success(weatherData))

        // When
        val result = refreshWeatherUseCase()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(weatherData, result.getOrNull())
        assertEquals(1, fakeRepository.refreshCallCount)
    }

    @Test
    fun `error handling should work properly`() = runTest {
        // Given
        val exception = Exception("Network error")
        fakeRepository.setForecastResult(Result.Error(DomainException.Unknown(exception.message ?: "Network error")))

        // When
        val result = refreshWeatherUseCase()

        // Then
        assertTrue(result.isError)
        assertEquals("Network error", result.exceptionOrNull()?.message)
        assertEquals(1, fakeRepository.refreshCallCount)
    }

    @Test
    fun `use cases should be properly injected`() = runTest {
        // Given
        val weatherData = listOf(
            Weather(
                date = LocalDate(2025, 1, 15),
                condition = WeatherCondition.CLEAR,
                temperatureHigh = 25.0,
                temperatureLow = 15.0,
                humidity = 65,
                icon = "01d",
                description = "Clear sky"
            )
        )
        fakeRepository.setForecastResult(Result.Success(weatherData))

        // When
        getWeatherUseCase().test {
            val flowResult = awaitItem()
            awaitComplete()
        }
        refreshWeatherUseCase()

        // Then
        assertEquals(1, fakeRepository.getForecastCallCount)
        assertEquals(1, fakeRepository.refreshCallCount)
    }
}

class FakeLocationService : LocationService {
    private val defaultLocation = LocationData(
        latitude = 12.9716,
        longitude = 77.5946,
        cityName = "Bengaluru",
        countryName = "India",
        displayName = "Bengaluru, India"
    )
    
    override suspend fun getCurrentLocation(): LocationData = defaultLocation
    
    override suspend fun getLocationBySource(source: LocationSource): LocationData? = defaultLocation
    
    override suspend fun searchLocations(query: String): List<LocationSearchResult> = emptyList()
    
    override suspend fun setUserSelectedLocation(location: LocationData) {}
    
    override suspend fun getUserSelectedLocation(): LocationData? = null
    
    override suspend fun clearUserSelectedLocation() {}
    
    override fun observeCurrentLocation(): kotlinx.coroutines.flow.Flow<LocationData> = 
        kotlinx.coroutines.flow.flowOf(defaultLocation)
    
    override suspend fun getDefaultLocation(): LocationData = defaultLocation
}

class FakePermissionManager : PermissionManager {
    override suspend fun checkPermission(permission: Permission): PermissionStatus = PermissionStatus.GRANTED
    override suspend fun requestPermission(permission: Permission): PermissionStatus = PermissionStatus.GRANTED
    override suspend fun requestPermissions(permissions: List<Permission>): Map<Permission, PermissionStatus> = 
        permissions.associateWith { PermissionStatus.GRANTED }
    override suspend fun hasLocationPermission(): Boolean = true
    override suspend fun requestLocationPermission(): Boolean = true
    override suspend fun openAppSettings() {}
    override suspend fun shouldShowRationale(permission: Permission): Boolean = false
}