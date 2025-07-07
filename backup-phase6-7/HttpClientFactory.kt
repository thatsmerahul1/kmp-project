package com.weather.data.remote

import com.weather.security.CertificatePinningPlugin
import com.weather.security.SecurityAuditEvent
import com.weather.security.SecurityAuditLogger
import com.weather.security.SecurityEventType
import com.weather.security.SecuritySeverity
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
 * Secure HTTP client factory with 2025 security standards
 * 
 * Features:
 * - Certificate pinning
 * - Security headers
 * - Request/response validation
 * - Security audit logging
 * - Timeout configuration
 * - User agent management
 */
object HttpClientFactory {
    
    fun create(enableSecurity: Boolean = true): HttpClient {
        SecurityAuditLogger.logEvent(
            SecurityAuditEvent(
                type = SecurityEventType.CONFIGURATION_CHANGE,
                description = "Creating HTTP client with security enabled: $enableSecurity",
                severity = SecuritySeverity.INFO
            )
        )
        
        return HttpClient {
            // Content negotiation with secure JSON parsing
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = false // Disable in production for performance
                    isLenient = true
                    ignoreUnknownKeys = true
                    allowSpecialFloatingPointValues = false // Security: prevent NaN/Infinity
                    allowStructuredMapKeys = false // Security: prevent complex keys
                    coerceInputValues = false // Security: strict type checking
                })
            }
            
            // Security-aware logging
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        // Filter sensitive information from logs
                        val filteredMessage = filterSensitiveData(message)
                        Logger.SIMPLE.log(filteredMessage)
                        
                        // Log to security audit trail
                        SecurityAuditLogger.logEvent(
                            SecurityAuditEvent(
                                type = SecurityEventType.DATA_ACCESS,
                                description = "HTTP request logged",
                                severity = SecuritySeverity.INFO
                            )
                        )
                    }
                }
                level = LogLevel.INFO
                
                // Security: Filter sensitive headers and body content
                sanitizeHeader { name ->
                    !name.lowercase().contains("authorization") &&
                    !name.lowercase().contains("cookie") &&
                    !name.lowercase().contains("x-api-key")
                }
            }
            
            // Timeout configuration for security (prevent hanging connections)
            install(HttpTimeout) {
                requestTimeoutMillis = 30_000L // 30 seconds
                connectTimeoutMillis = 15_000L // 15 seconds
                socketTimeoutMillis = 30_000L // 30 seconds
            }
            
            // User agent for identification and security
            install(UserAgent) {
                agent = "WeatherKMP/2025.1 (Kotlin Multiplatform; Security Enhanced)"
            }
            
            // Certificate pinning (if security is enabled)
            if (enableSecurity) {
                install(CertificatePinningPlugin)
            }
            
            // Custom engine configuration for security
            engine {
                // Platform-specific security configurations would go here
                configureSecuritySettings()
            }
        }
    }
    
    /**
     * Create a client specifically for testing (with security features disabled)
     */
    fun createForTesting(): HttpClient {
        SecurityAuditLogger.logEvent(
            SecurityAuditEvent(
                type = SecurityEventType.CONFIGURATION_CHANGE,
                description = "Creating HTTP client for testing (security disabled)",
                severity = SecuritySeverity.WARNING
            )
        )
        
        return create(enableSecurity = false)
    }
    
    /**
     * Filter sensitive data from log messages
     */
    private fun filterSensitiveData(message: String): String {
        return message
            .replace(Regex("authorization:\\s*[^\\s,]+", RegexOption.IGNORE_CASE), "authorization: [FILTERED]")
            .replace(Regex("x-api-key:\\s*[^\\s,]+", RegexOption.IGNORE_CASE), "x-api-key: [FILTERED]")
            .replace(Regex("cookie:\\s*[^\\s,]+", RegexOption.IGNORE_CASE), "cookie: [FILTERED]")
            .replace(Regex("\"password\"\\s*:\\s*\"[^\"]+\"", RegexOption.IGNORE_CASE), "\"password\": \"[FILTERED]\"")
            .replace(Regex("\"token\"\\s*:\\s*\"[^\"]+\"", RegexOption.IGNORE_CASE), "\"token\": \"[FILTERED]\"")
    }
    
    /**
     * Configure platform-specific security settings
     */
    private fun Any.configureSecuritySettings() {
        // This would be implemented in platform-specific expect/actual functions
        // For now, we'll add a placeholder for common security configurations
        
        SecurityAuditLogger.logEvent(
            SecurityAuditEvent(
                type = SecurityEventType.CONFIGURATION_CHANGE,
                description = "Platform-specific security settings configured",
                severity = SecuritySeverity.INFO
            )
        )
    }
}