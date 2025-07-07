package com.weather.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Basic HTTP client factory
 */
object HttpClientFactory {
    
    fun create(): HttpClient {
        return HttpClient {
            // Content negotiation with JSON parsing
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = false
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            
            // Basic logging
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.INFO
            }
            
            // Timeout configuration
            install(HttpTimeout) {
                requestTimeoutMillis = 30_000L
                connectTimeoutMillis = 15_000L
                socketTimeoutMillis = 30_000L
            }
            
            // User agent
            install(UserAgent) {
                agent = "WeatherKMP/2025.1 (Kotlin Multiplatform)"
            }
        }
    }
    
    /**
     * Create a client for testing
     */
    fun createForTesting(): HttpClient {
        return create()
    }
}