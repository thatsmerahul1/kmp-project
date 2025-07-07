package com.weather.domain.common

/**
 * Domain-specific exception hierarchy for 2025 architecture standards
 * 
 * This sealed hierarchy provides a comprehensive way to handle all types of errors
 * that can occur in the domain layer. Each exception type carries specific information
 * relevant to its context, making error handling more precise and user-friendly.
 */
sealed class DomainException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    
    /**
     * Network-related exceptions
     */
    sealed class Network(message: String, cause: Throwable? = null) : DomainException(message, cause) {
        
        /**
         * No internet connection available
         */
        object NoConnection : Network("No internet connection available")
        
        /**
         * Request timeout occurred
         */
        data class Timeout(val timeoutMs: Long) : Network("Request timed out after ${timeoutMs}ms")
        
        /**
         * Server returned an error response
         */
        data class ServerError(
            val statusCode: Int,
            val errorBody: String? = null
        ) : Network("Server error: $statusCode${errorBody?.let { " - $it" } ?: ""}")
        
        /**
         * Client-side request error (4xx)
         */
        data class ClientError(
            val statusCode: Int,
            val errorBody: String? = null
        ) : Network("Client error: $statusCode${errorBody?.let { " - $it" } ?: ""}")
        
        /**
         * Failed to parse network response
         */
        data class ParseError(val parseMessage: String) : Network("Failed to parse response: $parseMessage")
        
        /**
         * SSL/TLS certificate error
         */
        data class SSLError(val sslMessage: String) : Network("SSL error: $sslMessage")
        
        /**
         * Generic network error
         */
        data class Generic(val networkMessage: String) : Network(networkMessage)
    }
    
    /**
     * Database/Storage-related exceptions
     */
    sealed class Storage(message: String, cause: Throwable? = null) : DomainException(message, cause) {
        
        /**
         * Database operation failed
         */
        data class DatabaseError(val operation: String, val dbMessage: String) : 
            Storage("Database $operation failed: $dbMessage")
        
        /**
         * Disk space insufficient
         */
        object InsufficientSpace : Storage("Insufficient storage space")
        
        /**
         * File system permission denied
         */
        data class PermissionDenied(val path: String) : Storage("Permission denied for: $path")
        
        /**
         * Data corruption detected
         */
        data class DataCorruption(val details: String) : Storage("Data corruption detected: $details")
        
        /**
         * Cache operation failed
         */
        data class CacheError(val cacheOperation: String) : Storage("Cache $cacheOperation failed")
        
        /**
         * Migration failed
         */
        data class MigrationFailed(val fromVersion: Int, val toVersion: Int) : 
            Storage("Database migration failed from version $fromVersion to $toVersion")
    }
    
    /**
     * Database-specific exceptions
     */
    sealed class Database(message: String) : DomainException(message) {
        /**
         * Database connection failed
         */
        object ConnectionFailed : Database("Failed to connect to database")
        
        /**
         * Database migration error
         */
        data class MigrationError(val details: String) : Database("Database migration error: $details")
        
        /**
         * Schema validation failed
         */
        data class SchemaValidationError(val details: String) : Database("Schema validation failed: $details")
        
        /**
         * Unsupported database operation
         */
        data class UnsupportedOperation(val operation: String) : Database("Unsupported database operation: $operation")
        
        /**
         * Database transaction failed
         */
        data class TransactionError(val details: String) : Database("Transaction error: $details")
        
        /**
         * Database constraint violation
         */
        data class ConstraintViolation(val constraint: String) : Database("Database constraint violation: $constraint")
        
        /**
         * Database version mismatch
         */
        data class VersionMismatch(val expected: Int, val actual: Int) : 
            Database("Database version mismatch: expected $expected, got $actual")
    }
    
    /**
     * Business logic validation exceptions
     */
    sealed class Validation(message: String) : DomainException(message) {
        
        /**
         * Required field is missing or empty
         */
        data class RequiredField(val fieldName: String) : Validation("$fieldName is required")
        
        /**
         * Field value is invalid
         */
        data class InvalidField(val fieldName: String, val reason: String) : 
            Validation("$fieldName is invalid: $reason")
        
        /**
         * Value is out of acceptable range
         */
        data class OutOfRange(val fieldName: String, val min: Any?, val max: Any?, val actual: Any) : 
            Validation("$fieldName value $actual is out of range [$min, $max]")
        
        /**
         * Duplicate value where uniqueness is required
         */
        data class DuplicateValue(val fieldName: String) : Validation("$fieldName must be unique")
        
        /**
         * Invalid format (e.g., email, phone number)
         */
        data class InvalidFormat(val fieldName: String, val expectedFormat: String) : 
            Validation("$fieldName format is invalid. Expected: $expectedFormat")
        
        /**
         * Business rule violation
         */
        data class BusinessRule(val ruleName: String, val ruleDescription: String) : 
            Validation("Business rule '$ruleName' violated: $ruleDescription")
        
        /**
         * Serialization/encoding error
         */
        data class SerializationError(val details: String) : Validation("Serialization error: $details")
        
        /**
         * Deserialization/decoding error
         */
        data class DeserializationError(val details: String) : Validation("Deserialization error: $details")
        
        /**
         * Null value error
         */
        data class NullValueError(val details: String) : Validation("Null value error: $details")
        
        /**
         * Type mismatch error
         */
        data class TypeMismatchError(val details: String) : Validation("Type mismatch error: $details")
        
        /**
         * Condition not met error
         */
        data class ConditionNotMet(val details: String) : Validation("Condition not met: $details")
    }
    
    /**
     * Weather-specific business exceptions
     */
    sealed class Weather(message: String) : DomainException(message) {
        
        /**
         * Location not found or invalid
         */
        data class LocationNotFound(val location: String) : Weather("Weather location not found: $location")
        
        /**
         * Weather data is stale/outdated
         */
        data class StaleData(val lastUpdated: String) : Weather("Weather data is outdated (last updated: $lastUpdated)")
        
        /**
         * Weather service unavailable
         */
        object ServiceUnavailable : Weather("Weather service is currently unavailable")
        
        /**
         * Invalid weather parameters
         */
        data class InvalidParameters(val parameters: String) : Weather("Invalid weather parameters: $parameters")
        
        /**
         * Location permission denied
         */
        object LocationPermissionDenied : Weather("Location permission denied")
        
        /**
         * GPS/Location service disabled
         */
        object LocationServiceDisabled : Weather("Location service is disabled")
    }
    
    /**
     * Authentication and authorization exceptions
     */
    sealed class Auth(message: String) : DomainException(message) {
        
        /**
         * User not authenticated
         */
        object NotAuthenticated : Auth("User is not authenticated")
        
        /**
         * Authentication token expired
         */
        object TokenExpired : Auth("Authentication token has expired")
        
        /**
         * Insufficient permissions
         */
        data class InsufficientPermissions(val requiredPermission: String) : 
            Auth("Insufficient permissions. Required: $requiredPermission")
        
        /**
         * Invalid credentials
         */
        object InvalidCredentials : Auth("Invalid username or password")
        
        /**
         * Account locked or suspended
         */
        data class AccountLocked(val reason: String) : Auth("Account is locked: $reason")
    }
    
    /**
     * Security-related exceptions
     */
    sealed class Security(message: String, cause: Throwable? = null) : DomainException(message, cause) {
        
        /**
         * Encryption/Decryption failed
         */
        data class EncryptionFailed(val operation: String) : Security("Encryption operation '$operation' failed")
        
        /**
         * Certificate validation failed
         */
        data class CertificateValidationFailed(val details: String) : Security("Certificate validation failed: $details")
        
        /**
         * Security context validation failed
         */
        data class SecurityContextInvalid(val details: String) : Security("Security context invalid: $details")
        
        /**
         * Biometric authentication failed
         */
        data class BiometricAuthFailed(val reason: String) : Security("Biometric authentication failed: $reason")
        
        /**
         * Key generation or management failed
         */
        data class KeyManagementFailed(val operation: String) : Security("Key management operation '$operation' failed")
    }
    
    /**
     * Telemetry and monitoring exceptions
     */
    sealed class Telemetry(message: String, cause: Throwable? = null) : DomainException(message, cause) {
        
        /**
         * Telemetry initialization failed
         */
        data class InitializationFailed(val provider: String) : Telemetry("Telemetry initialization failed for provider: $provider")
        
        /**
         * Metric recording failed
         */
        data class MetricRecordingFailed(val metricName: String) : Telemetry("Failed to record metric: $metricName")
        
        /**
         * Span operation failed
         */
        data class SpanOperationFailed(val operation: String) : Telemetry("Span operation '$operation' failed")
        
        /**
         * Telemetry configuration invalid
         */
        data class ConfigurationInvalid(val details: String) : Telemetry("Telemetry configuration invalid: $details")
        
        /**
         * Telemetry provider unavailable
         */
        data class ProviderUnavailable(val provider: String) : Telemetry("Telemetry provider '$provider' is unavailable")
    }

    /**
     * Configuration and setup exceptions
     */
    sealed class Configuration(message: String) : DomainException(message) {
        
        /**
         * Missing required configuration
         */
        data class MissingConfig(val configKey: String) : Configuration("Missing configuration: $configKey")
        
        /**
         * Invalid configuration value
         */
        data class InvalidConfig(val configKey: String, val reason: String) : 
            Configuration("Invalid configuration for $configKey: $reason")
        
        /**
         * API key is missing or invalid
         */
        data class InvalidApiKey(val service: String) : Configuration("Invalid API key for $service")
        
        /**
         * Feature not available in current build/environment
         */
        data class FeatureUnavailable(val featureName: String) : 
            Configuration("Feature '$featureName' is not available in this build")
    }
    
    /**
     * Concurrency and threading exceptions
     */
    sealed class Concurrency(message: String) : DomainException(message) {
        
        /**
         * Operation was cancelled
         */
        data class OperationCancelled(val operationName: String) : 
            Concurrency("Operation '$operationName' was cancelled")
        
        /**
         * Concurrent modification detected
         */
        data class ConcurrentModification(val resourceName: String) : 
            Concurrency("Concurrent modification detected for $resourceName")
        
        /**
         * Deadlock detected
         */
        data class Deadlock(val details: String) : Concurrency("Deadlock detected: $details")
        
        /**
         * Resource is currently locked
         */
        data class ResourceLocked(val resourceName: String) : 
            Concurrency("Resource '$resourceName' is currently locked")
    }
    
    /**
     * Unknown or unexpected exceptions
     */
    data class Unknown(val unknownMessage: String) : DomainException("Unknown error: $unknownMessage")
    
    /**
     * System-level exceptions
     */
    sealed class System(message: String, cause: Throwable? = null) : DomainException(message, cause) {
        
        /**
         * Out of memory
         */
        object OutOfMemory : System("System is out of memory")
        
        /**
         * Platform-specific error
         */
        data class PlatformError(val platform: String, val error: String) : 
            System("Platform error on $platform: $error")
        
        /**
         * Library or dependency error
         */
        data class LibraryError(val library: String, val error: String) : 
            System("Library error in $library: $error")
        
        /**
         * Concurrency-related errors
         */
        data class ConcurrencyError(val details: String) : System("Concurrency error: $details")
        
        /**
         * Timeout errors
         */
        data class TimeoutError(val details: String) : System("Timeout error: $details")
        
        /**
         * Circuit breaker open
         */
        data class CircuitOpenError(val details: String) : System("Circuit breaker open: $details")
    }
}

/**
 * Extension functions for DomainException to make them easier to work with
 */

/**
 * Check if this exception is retryable
 */
val DomainException.isRetryable: Boolean
    get() = when (this) {
        is DomainException.Network.NoConnection,
        is DomainException.Network.Timeout,
        is DomainException.Network.ServerError -> true
        is DomainException.Storage.InsufficientSpace,
        is DomainException.Storage.PermissionDenied -> false
        is DomainException.Weather.ServiceUnavailable -> true
        is DomainException.Auth.TokenExpired -> true
        else -> false
    }

/**
 * Check if this exception should be shown to the user
 */
val DomainException.isUserFacing: Boolean
    get() = when (this) {
        is DomainException.Network.NoConnection,
        is DomainException.Validation,
        is DomainException.Weather,
        is DomainException.Auth -> true
        else -> false
    }

/**
 * Get a user-friendly error message
 */
val DomainException.userMessage: String
    get() = when (this) {
        is DomainException.Network.NoConnection -> "Please check your internet connection"
        is DomainException.Network.Timeout -> "Request is taking too long. Please try again"
        is DomainException.Network.ServerError -> "Server is experiencing issues. Please try again later"
        is DomainException.Weather.LocationNotFound -> "Unable to find weather for this location"
        is DomainException.Weather.ServiceUnavailable -> "Weather service is temporarily unavailable"
        is DomainException.Auth.NotAuthenticated -> "Please sign in to continue"
        is DomainException.Auth.TokenExpired -> "Your session has expired. Please sign in again"
        is DomainException.Validation -> message ?: "Validation error"
        else -> "An unexpected error occurred"
    }

/**
 * Get error category for analytics/logging
 */
val DomainException.category: String
    get() = when (this) {
        is DomainException.Network -> "network"
        is DomainException.Storage -> "storage"
        is DomainException.Database -> "database"
        is DomainException.Validation -> "validation"
        is DomainException.Weather -> "weather"
        is DomainException.Auth -> "authentication"
        is DomainException.Security -> "security"
        is DomainException.Telemetry -> "telemetry"
        is DomainException.Configuration -> "configuration"
        is DomainException.Concurrency -> "concurrency"
        is DomainException.System -> "system"
        is DomainException.Unknown -> "unknown"
    }

/**
 * Type aliases for commonly used exceptions to maintain compatibility
 */
typealias SecurityException = DomainException.Security
typealias TelemetryException = DomainException.Telemetry

/**
 * Extension functions to convert standard exceptions to domain exceptions
 */
fun Throwable.toSecurityException(): DomainException.Security = when (this) {
    is DomainException.Security -> this
    else -> DomainException.Security.EncryptionFailed(message ?: "Unknown security error")
}

fun Throwable.toTelemetryException(): DomainException.Telemetry = when (this) {
    is DomainException.Telemetry -> this
    else -> DomainException.Telemetry.ConfigurationInvalid(message ?: "Unknown telemetry error")
}

fun Exception.toDomainException(): DomainException = when (this) {
    is DomainException -> this
    is IllegalStateException -> DomainException.Validation.ConditionNotMet(message ?: "Illegal state")
    is IllegalArgumentException -> DomainException.Validation.InvalidField("argument", message ?: "Invalid argument")
    else -> DomainException.Unknown(message ?: "Unknown error")
}