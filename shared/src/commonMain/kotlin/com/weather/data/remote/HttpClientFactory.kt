package com.weather.data.remote

import com.weather.security.CertificatePinningPlugin
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
 * Enhanced HTTP client factory with 2025 security standards
 */
object HttpClientFactory {
    
    fun create(enableSecurity: Boolean = true): HttpClient {
        return HttpClient {
            // Content negotiation with JSON parsing
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = false
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            
            // Security-aware logging
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.INFO
                sanitizeHeader { name ->
                    when {
                        name.equals("Authorization", ignoreCase = true) -> true
                        name.equals("Cookie", ignoreCase = true) -> true
                        name.equals("Set-Cookie", ignoreCase = true) -> true
                        name.equals("X-API-Key", ignoreCase = true) -> true
                        else -> false
                    }
                }
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
            
            // Certificate pinning for production security
            if (enableSecurity) {
                install(CertificatePinningPlugin)
            }
        }
    }
    
    /**
     * Create a client for testing (without security features)
     */
    fun createForTesting(): HttpClient {
        return create(enableSecurity = false)
    }
}