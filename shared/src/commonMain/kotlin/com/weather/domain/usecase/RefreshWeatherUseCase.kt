package com.weather.domain.usecase

import com.weather.domain.model.Weather
import com.weather.domain.model.LocationData
import com.weather.domain.repository.WeatherRepository
import com.weather.domain.common.Result

class RefreshWeatherUseCase(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(location: LocationData): Result<List<Weather>> {
        return weatherRepository.refreshWeatherForecast(location)
    }
}