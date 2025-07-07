package com.weather.domain.common

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlin.math.pow

/**
 * Modern Kotlin features for 2025 architecture standards
 * 
 * This module demonstrates and provides utilities for:
 * - Latest Flow operators and patterns
 * - Structured concurrency patterns
 * - Advanced serialization features
 * - Performance-optimized coroutine usage
 * - Type-safe builders and DSLs
 */

/**
 * Enhanced Flow operators for 2025
 */
object ModernFlowOperators {
    
    /**
     * Retry with exponential backoff strategy
     */
    fun <T> Flow<T>.retryWithBackoff(
        maxRetries: Int = 3,
        initialDelay: Long = 1000L,
        backoffFactor: Double = 2.0,
        maxDelay: Long = 10000L
    ): Flow<T> = retryWhen { cause, attempt ->
        if (attempt >= maxRetries) {
            false
        } else {
            val delay = (initialDelay * backoffFactor.pow(attempt.toDouble()))
                .toLong()
                .coerceAtMost(maxDelay)
            
            kotlinx.coroutines.delay(delay)
            true
        }
    }
    
    /**
     * Cache with TTL (Time To Live)
     */
    fun <T> Flow<T>.cacheWithTTL(
        ttlMs: Long,
        clock: () -> Long = { kotlinx.datetime.Clock.System.now().toEpochMilliseconds() }
    ): Flow<T> {
        var cachedValue: T? = null
        var cacheTimestamp: Long = 0
        
        return flow {
            val currentTime = clock()
            
            if (cachedValue != null && (currentTime - cacheTimestamp) < ttlMs) {
                emit(cachedValue!!)
            } else {
                collect { value ->
                    cachedValue = value
                    cacheTimestamp = currentTime
                    emit(value)
                }
            }
        }
    }
    
    /**
     * Merge with priority (emit from priority source first)
     */
    fun <T> Flow<T>.mergeWithPriority(other: Flow<T>): Flow<T> = flow {
        coroutineScope {
            val primaryFlow = async { this@mergeWithPriority.collect { emit(it) } }
            val secondaryFlow = async { other.collect { emit(it) } }
            
            // Wait for primary to complete first, then secondary
            primaryFlow.await()
            secondaryFlow.await()
        }
    }
    
    /**
     * Sample latest with minimum interval
     */
    fun <T> Flow<T>.sampleLatestWithMinInterval(intervalMs: Long): Flow<T> = flow {
        var lastEmissionTime = 0L
        
        collect { value ->
            val currentTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            if (currentTime - lastEmissionTime >= intervalMs) {
                emit(value)
                lastEmissionTime = currentTime
            }
        }
    }
    
    /**
     * Chunked by time window
     */
    fun <T> Flow<T>.chunkedByTime(
        windowMs: Long,
        maxSize: Int = Int.MAX_VALUE
    ): Flow<List<T>> = flow {
        val buffer = mutableListOf<T>()
        var windowStart = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        
        collect { value ->
            val currentTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            
            if (currentTime - windowStart >= windowMs || buffer.size >= maxSize) {
                if (buffer.isNotEmpty()) {
                    emit(buffer.toList())
                    buffer.clear()
                }
                windowStart = currentTime
            }
            
            buffer.add(value)
        }
        
        if (buffer.isNotEmpty()) {
            emit(buffer.toList())
        }
    }
}

/**
 * Structured concurrency patterns for 2025
 */
object StructuredConcurrencyPatterns {
    
    /**
     * Parallel execution with error handling
     */
    suspend fun <T1, T2> executeInParallel(
        operation1: suspend () -> T1,
        operation2: suspend () -> T2,
        onError: (Throwable) -> Unit = {}
    ): Pair<Result<T1>, Result<T2>> = coroutineScope {
        val deferred1 = async {
            try {
                Result.Success(operation1())
            } catch (e: Exception) {
                onError(e)
                Result.Error(DomainException.System.ConcurrencyError("Parallel operation 1 failed: ${e.message}"))
            }
        }
        
        val deferred2 = async {
            try {
                Result.Success(operation2())
            } catch (e: Exception) {
                onError(e)
                Result.Error(DomainException.System.ConcurrencyError("Parallel operation 2 failed: ${e.message}"))
            }
        }
        
        Pair(deferred1.await(), deferred2.await())
    }
    
    /**
     * Race condition with timeout
     */
    suspend fun <T> raceWithTimeout(
        timeoutMs: Long,
        vararg operations: suspend () -> T
    ): Result<T> = withContext(Dispatchers.Default) {
        try {
            kotlinx.coroutines.withTimeoutOrNull(timeoutMs) {
                coroutineScope {
                    val deferreds = operations.map { operation ->
                        async { operation() }
                    }
                    
                    // Return first successful result
                    kotlinx.coroutines.selects.select<T> {
                        deferreds.forEach { deferred ->
                            deferred.onAwait { it }
                        }
                    }
                }
            }?.let { Result.Success(it) } 
                ?: Result.Error(DomainException.System.TimeoutError("Race operations timed out after ${timeoutMs}ms"))
        } catch (e: Exception) {
            Result.Error(DomainException.System.ConcurrencyError("Race operation failed: ${e.message}"))
        }
    }
    
    /**
     * Circuit breaker pattern
     */
    class CircuitBreaker(
        private val failureThreshold: Int = 5,
        private val recoveryTimeoutMs: Long = 30000L,
        private val onStateChange: (CircuitState) -> Unit = {}
    ) {
        enum class CircuitState { CLOSED, OPEN, HALF_OPEN }
        
        private var state = CircuitState.CLOSED
        private var failureCount = 0
        private var lastFailureTime = 0L
        
        suspend fun <T> execute(operation: suspend () -> T): Result<T> {
            when (state) {
                CircuitState.OPEN -> {
                    val currentTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
                    if (currentTime - lastFailureTime >= recoveryTimeoutMs) {
                        state = CircuitState.HALF_OPEN
                        onStateChange(state)
                    } else {
                        return Result.Error(DomainException.System.CircuitOpenError("Circuit breaker is OPEN"))
                    }
                }
                CircuitState.HALF_OPEN, CircuitState.CLOSED -> {
                    // Proceed with operation
                }
            }
            
            return try {
                val result = operation()
                onSuccess()
                Result.Success(result)
            } catch (e: Exception) {
                onFailure()
                Result.Error(DomainException.System.ConcurrencyError("Circuit breaker operation failed: ${e.message}"))
            }
        }
        
        private fun onSuccess() {
            failureCount = 0
            state = CircuitState.CLOSED
            onStateChange(state)
        }
        
        private fun onFailure() {
            failureCount++
            lastFailureTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            
            if (failureCount >= failureThreshold) {
                state = CircuitState.OPEN
                onStateChange(state)
            }
        }
    }
}

/**
 * Advanced serialization features for 2025
 */
object ModernSerialization {
    
    /**
     * Enhanced JSON configuration for 2025
     */
    val modernJson = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = false
        prettyPrint = false
        useArrayPolymorphism = false
        allowStructuredMapKeys = true
        allowSpecialFloatingPointValues = true
        useAlternativeNames = true
        namingStrategy = null // Could use custom naming strategy
    }
    
    /**
     * Type-safe serialization with error handling
     */
    inline fun <reified T> encodeToJsonSafely(
        value: T,
        json: Json = modernJson
    ): Result<String> {
        return try {
            Result.Success(json.encodeToString(value))
        } catch (e: Exception) {
            Result.Error(DomainException.Validation.SerializationError("Failed to encode ${T::class.simpleName}: ${e.message}"))
        }
    }
    
    /**
     * Type-safe deserialization with error handling
     */
    inline fun <reified T> decodeFromJsonSafely(
        jsonString: String,
        json: Json = modernJson
    ): Result<T> {
        return try {
            Result.Success(json.decodeFromString<T>(jsonString))
        } catch (e: Exception) {
            Result.Error(DomainException.Validation.DeserializationError("Failed to decode ${T::class.simpleName}: ${e.message}"))
        }
    }
    
    /**
     * Batch serialization for collections
     */
    inline fun <reified T> encodeBatchSafely(
        items: List<T>,
        json: Json = modernJson
    ): Result<List<String>> {
        return try {
            val encoded = items.map { json.encodeToString(it) }
            Result.Success(encoded)
        } catch (e: Exception) {
            Result.Error(DomainException.Validation.SerializationError("Failed to encode batch: ${e.message}"))
        }
    }
}

/**
 * Performance optimization utilities
 */
object PerformanceOptimizations {
    
    /**
     * Memoization for expensive computations
     */
    class Memoize<T, R>(private val function: (T) -> R) {
        private val cache = mutableMapOf<T, R>()
        
        operator fun invoke(input: T): R {
            return cache.getOrPut(input) { function(input) }
        }
        
        fun clearCache() = cache.clear()
        fun cacheSize() = cache.size
    }
    
    /**
     * Create memoized function
     */
    fun <T, R> memoize(function: (T) -> R): Memoize<T, R> = Memoize(function)
    
    /**
     * Lazy initialization with thread safety
     */
    fun <T> lazyThreadSafe(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.SYNCHRONIZED, initializer)
    
    /**
     * Benchmark execution time
     */
    suspend fun <T> benchmark(
        operation: suspend () -> T,
        logger: Logger? = null,
        operationName: String = "Operation"
    ): Pair<T, Long> {
        val startTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        val result = operation()
        val endTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        val duration = endTime - startTime
        
        logger?.info("Performance", "$operationName completed in ${duration}ms")
        
        return Pair(result, duration)
    }
}

/**
 * Type-safe builder DSL patterns
 */
object ModernDSLPatterns {
    
    /**
     * Configuration builder DSL
     */
    @Serializable
    data class AppConfiguration(
        val apiTimeout: Long = 30000L,
        val retryAttempts: Int = 3,
        val cacheEnabled: Boolean = true,
        val loggingLevel: String = "INFO",
        val features: Map<String, Boolean> = emptyMap()
    )
    
    class AppConfigurationBuilder {
        var apiTimeout: Long = 30000L
        var retryAttempts: Int = 3
        var cacheEnabled: Boolean = true
        var loggingLevel: String = "INFO"
        private val features = mutableMapOf<String, Boolean>()
        
        fun feature(name: String, enabled: Boolean) {
            features[name] = enabled
        }
        
        fun build(): AppConfiguration {
            return AppConfiguration(
                apiTimeout = apiTimeout,
                retryAttempts = retryAttempts,
                cacheEnabled = cacheEnabled,
                loggingLevel = loggingLevel,
                features = features.toMap()
            )
        }
    }
    
    /**
     * Configuration DSL function
     */
    fun appConfiguration(builder: AppConfigurationBuilder.() -> Unit): AppConfiguration {
        return AppConfigurationBuilder().apply(builder).build()
    }
    
    /**
     * Query builder DSL for type-safe API queries
     */
    data class WeatherQuery(
        val location: String,
        val units: String = "metric",
        val lang: String = "en",
        val include: Set<String> = emptySet(),
        val exclude: Set<String> = emptySet()
    )
    
    class WeatherQueryBuilder(private val location: String) {
        var units: String = "metric"
        var language: String = "en"
        private val includes = mutableSetOf<String>()
        private val excludes = mutableSetOf<String>()
        
        fun include(vararg fields: String) {
            includes.addAll(fields)
        }
        
        fun exclude(vararg fields: String) {
            excludes.addAll(fields)
        }
        
        fun build(): WeatherQuery {
            return WeatherQuery(
                location = location,
                units = units,
                lang = language,
                include = includes.toSet(),
                exclude = excludes.toSet()
            )
        }
    }
    
    /**
     * Weather query DSL function
     */
    fun weatherQuery(location: String, builder: WeatherQueryBuilder.() -> Unit = {}): WeatherQuery {
        return WeatherQueryBuilder(location).apply(builder).build()
    }
}

/**
 * Extension functions for enhanced usability
 */

/**
 * Safe cast with Result
 */
inline fun <reified T> Any?.safeCast(): Result<T> {
    return when (this) {
        is T -> Result.Success(this)
        null -> Result.Error(DomainException.Validation.NullValueError("Value is null"))
        else -> Result.Error(DomainException.Validation.TypeMismatchError("Cannot cast ${this::class.simpleName} to ${T::class.simpleName}"))
    }
}

/**
 * Conditional execution
 */
inline fun <T> T.takeIfResult(predicate: (T) -> Boolean): Result<T> {
    return if (predicate(this)) {
        Result.Success(this)
    } else {
        Result.Error(DomainException.Validation.ConditionNotMet("Predicate condition not met"))
    }
}

/**
 * Null-safe operations with Result
 */
inline fun <T, R> T?.mapNotNull(transform: (T) -> R): Result<R> {
    return when (this) {
        null -> Result.Error(DomainException.Validation.NullValueError("Value is null"))
        else -> Result.Success(transform(this))
    }
}