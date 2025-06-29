package com.weather.data.mapper

import com.weather.data.mapper.WeatherMapper.toDomain
import com.weather.data.mapper.WeatherMapper.toEntity
import com.weather.data.remote.dto.MainDto
import com.weather.data.remote.dto.WeatherInfoDto
import com.weather.data.remote.dto.WeatherItemDto
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class WeatherMapperTest {

    @Test
    fun `WeatherItemDto toDomain should map correctly`() {
        val dto = WeatherItemDto(
            timestamp = 1642204800, // 2022-01-15 00:00:00 UTC
            main = MainDto(
                temperature = 298.15, // 25°C in Kelvin
                temperatureMin = 293.15, // 20°C in Kelvin
                temperatureMax = 303.15, // 30°C in Kelvin
                humidity = 65
            ),
            weather = listOf(
                WeatherInfoDto(
                    main = "Clear",
                    description = "clear sky",
                    icon = "01d"
                )
            ),
            dateText = "2022-01-15 00:00:00"
        )

        val domain = dto.toDomain()

        assertEquals(WeatherCondition.CLEAR, domain.condition)
        assertEquals(30.0, domain.temperatureHigh) // Should be converted from Kelvin
        assertEquals(20.0, domain.temperatureLow)
        assertEquals(65, domain.humidity)
        assertEquals("01d", domain.icon)
        assertEquals("clear sky", domain.description)
    }

    @Test
    fun `Weather toEntity should map correctly`() {
        val weather = Weather(
            date = LocalDate(2022, 1, 15),
            condition = WeatherCondition.CLEAR,
            temperatureHigh = 25.0,
            temperatureLow = 20.0,
            humidity = 65,
            icon = "01d",
            description = "clear sky"
        )

        val cachedAt = 1642204800000L
        val entity = weather.toEntity(cachedAt)

        assertEquals(weather.date.toEpochDays().toLong(), entity.date)
        assertEquals("CLEAR", entity.condition)
        assertEquals(25.0, entity.temperature_high)
        assertEquals(20.0, entity.temperature_low)
        assertEquals(65L, entity.humidity)
        assertEquals("01d", entity.icon)
        assertEquals("clear sky", entity.description)
        assertEquals(cachedAt, entity.cached_at)
    }
}