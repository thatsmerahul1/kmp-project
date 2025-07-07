package com.weather.domain.usecase

import com.weather.domain.model.Weather
import com.weather.domain.repository.WeatherRepository
import com.weather.domain.common.Result
import kotlinx.coroutines.flow.Flow

class GetWeatherForecastUseCase(
    private val weatherRepository: WeatherRepository
) {
    operator fun invoke(): Flow<Result<List<Weather>>> {
        return weatherRepository.getWeatherForecast()
    }
}