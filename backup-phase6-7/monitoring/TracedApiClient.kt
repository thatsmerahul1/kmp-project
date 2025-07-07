package com.weather.monitoring

import com.weather.data.remote.api.WeatherApi
import com.weather.data.remote.dto.WeatherResponseDto
import com.weather.domain.common.Result
import kotlinx.datetime.Clock

/**
 * Instrumented API client with distributed tracing for 2025 monitoring standards
 * 
 * Features:
 * - Automatic span creation for API calls
 * - Performance metrics collection
 * - Error tracking and analysis
 * - Request/response correlation
 * - Distributed trace propagation
 */
class TracedWeatherApi(
    private val delegate: WeatherApi,
    private val telemetryProvider: TelemetryProvider = TelemetryUtils.getTelemetryProvider()
) : WeatherApi {
    
    override suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        days: Int
    ): WeatherResponseDto {
        return TelemetryUtils.withSpan(
            operationName = "weather_api_get_forecast",
            attributes = mapOf(
                "latitude" to latitude.toString(),
                "longitude" to longitude.toString(),
                "days" to days.toString(),
                "api_endpoint" to "weather_forecast"
            )
        ) {
            val startTime = Clock.System.now()
            
            try {
                // Record API call start event
                telemetryProvider.recordEvent(
                    TelemetryEvent(
                        name = "api_call_started",
                        level = EventLevel.INFO,
                        attributes = mapOf(
                            "operation" to "getWeatherForecast",
                            "latitude" to latitude.toString(),
                            "longitude" to longitude.toString()
                        )
                    )
                )
                
                // Execute the actual API call
                val response = delegate.getWeatherForecast(latitude, longitude, days)
                
                // Calculate duration
                val endTime = Clock.System.now()
                val duration = endTime.toEpochMilliseconds() - startTime.toEpochMilliseconds()
                
                // Record success metrics
                recordApiMetrics(
                    endpoint = "weather_forecast",
                    method = "GET",
                    statusCode = 200,
                    duration = duration,
                    success = true,
                    responseSize = estimateResponseSize(response)
                )
                
                response
                
            } catch (e: Exception) {
                // Calculate duration for failed call
                val endTime = Clock.System.now()
                val duration = endTime.toEpochMilliseconds() - startTime.toEpochMilliseconds()
                
                // Record error metrics
                recordApiMetrics(
                    endpoint = "weather_forecast",
                    method = "GET",
                    statusCode = 500,
                    duration = duration,
                    success = false,
                    error = e
                )
                
                // Re-throw the exception to maintain original behavior
                throw e
            }
        }
    }
    
    private suspend fun recordApiMetrics(
        endpoint: String,
        method: String,
        statusCode: Int,
        duration: Long,
        success: Boolean,
        responseSize: Long = 0,
        error: Exception? = null
    ) {
        // Record performance metrics
        TelemetryUtils.recordApiCall(endpoint, method, statusCode, duration, responseSize)
        
        // Record detailed telemetry metrics
        val baseAttributes = mapOf(
            "endpoint" to endpoint,
            "method" to method,
            "status_code" to statusCode.toString(),
            "success" to success.toString()
        )
        
        val metrics = listOf(
            TelemetryMetric(
                name = "api_request_duration",
                value = duration.toDouble(),
                unit = MetricUnit.MILLISECONDS,
                type = MetricType.HISTOGRAM,
                attributes = baseAttributes
            ),
            TelemetryMetric(
                name = "api_request_count",
                value = 1.0,
                unit = MetricUnit.COUNT,
                type = MetricType.COUNTER,
                attributes = baseAttributes
            ),
            TelemetryMetric(
                name = "api_response_size",
                value = responseSize.toDouble(),
                unit = MetricUnit.BYTES,
                type = MetricType.HISTOGRAM,
                attributes = baseAttributes
            )
        )
        
        metrics.forEach { metric ->
            telemetryProvider.recordMetric(metric)
        }
        
        // Record error if present
        if (error != null) {
            TelemetryUtils.recordError(
                operation = "api_call_$endpoint",
                errorType = error::class.simpleName ?: "UnknownError",
                errorMessage = error.message ?: "No error message",
                stackTrace = error.stackTraceToString()
            )
        }
        
        // Record API call completion event
        telemetryProvider.recordEvent(
            TelemetryEvent(
                name = if (success) "api_call_success" else "api_call_error",
                level = if (success) EventLevel.INFO else EventLevel.ERROR,
                attributes = baseAttributes + if (error != null) {
                    mapOf(
                        "error_type" to (error::class.simpleName ?: "Unknown"),
                        "error_message" to (error.message ?: "No message")
                    )
                } else emptyMap()
            )
        )
    }
    
    private fun estimateResponseSize(response: WeatherResponseDto): Long {
        // Estimate response size based on data structure
        // In production, this could be more accurate by measuring actual serialized size
        return (response.toString().length * 2).toLong() // Rough estimate
    }
}

/**
 * Instrumented repository with distributed tracing
 */
class TracedRepository<T>(
    private val repositoryName: String,
    private val telemetryProvider: TelemetryProvider = TelemetryUtils.getTelemetryProvider()
) {
    
    suspend fun <R> traceOperation(
        operationName: String,
        operationType: RepositoryOperationType,
        additionalAttributes: Map<String, String> = emptyMap(),
        operation: suspend () -> Result<R>
    ): Result<R> {
        return TelemetryUtils.withSpan(
            operationName = "${repositoryName}_$operationName",
            attributes = mapOf(
                "repository" to repositoryName,
                "operation_type" to operationType.name.lowercase(),
                "operation" to operationName
            ) + additionalAttributes
        ) {
            val startTime = Clock.System.now()
            
            try {
                // Record operation start
                telemetryProvider.recordEvent(
                    TelemetryEvent(
                        name = "repository_operation_started",
                        level = EventLevel.INFO,
                        attributes = mapOf(
                            "repository" to repositoryName,
                            "operation" to operationName,
                            "type" to operationType.name
                        )
                    )
                )
                
                // Execute operation
                val result = operation()
                
                // Calculate duration
                val endTime = Clock.System.now()
                val duration = endTime.toEpochMilliseconds() - startTime.toEpochMilliseconds()
                
                // Record metrics based on result
                when (result) {
                    is Result.Success -> {
                        recordRepositoryMetrics(operationName, operationType, duration, true)
                    }
                    is Result.Error -> {
                        recordRepositoryMetrics(operationName, operationType, duration, false, result.exception)
                    }
                }
                
                result
                
            } catch (e: Exception) {
                val endTime = Clock.System.now()
                val duration = endTime.toEpochMilliseconds() - startTime.toEpochMilliseconds()
                
                recordRepositoryMetrics(operationName, operationType, duration, false, e)
                throw e
            }
        }
    }
    
    private suspend fun recordRepositoryMetrics(
        operationName: String,
        operationType: RepositoryOperationType,
        duration: Long,
        success: Boolean,
        error: Throwable? = null
    ) {
        val attributes = mapOf(
            "repository" to repositoryName,
            "operation" to operationName,
            "operation_type" to operationType.name.lowercase(),
            "success" to success.toString()
        )
        
        // Record duration metric
        telemetryProvider.recordMetric(
            TelemetryMetric(
                name = "repository_operation_duration",
                value = duration.toDouble(),
                unit = MetricUnit.MILLISECONDS,
                type = MetricType.HISTOGRAM,
                attributes = attributes
            )
        )
        
        // Record operation count
        telemetryProvider.recordMetric(
            TelemetryMetric(
                name = "repository_operation_count",
                value = 1.0,
                unit = MetricUnit.COUNT,
                type = MetricType.COUNTER,
                attributes = attributes
            )
        )
        
        // Record cache hit/miss if applicable
        if (operationType == RepositoryOperationType.READ) {
            val cacheHit = duration < 100 // Assume cache hit if very fast
            telemetryProvider.recordMetric(
                TelemetryMetric(
                    name = "repository_cache_hit",
                    value = if (cacheHit) 1.0 else 0.0,
                    unit = MetricUnit.COUNT,
                    type = MetricType.COUNTER,
                    attributes = attributes + ("cache_hit" to cacheHit.toString())
                )
            )
        }
        
        // Record error if present
        if (error != null) {
            TelemetryUtils.recordError(
                operation = "${repositoryName}_$operationName",
                errorType = error::class.simpleName ?: "UnknownError",
                errorMessage = error.message ?: "No error message",
                stackTrace = error.stackTraceToString()
            )
        }
        
        // Record completion event
        telemetryProvider.recordEvent(
            TelemetryEvent(
                name = if (success) "repository_operation_success" else "repository_operation_error",
                level = if (success) EventLevel.INFO else EventLevel.ERROR,
                attributes = attributes + if (error != null) {
                    mapOf(
                        "error_type" to (error::class.simpleName ?: "Unknown"),
                        "error_message" to (error.message ?: "No message")
                    )
                } else emptyMap()
            )
        )
    }
}

enum class RepositoryOperationType {
    CREATE,
    READ,
    UPDATE,
    DELETE,
    SYNC,
    CACHE_REFRESH
}

/**
 * Instrumented use case with distributed tracing
 */
class TracedUseCase<T>(
    private val useCaseName: String,
    private val telemetryProvider: TelemetryProvider = TelemetryUtils.getTelemetryProvider()
) {
    
    suspend fun <R> execute(
        operation: suspend () -> Result<R>,
        additionalAttributes: Map<String, String> = emptyMap()
    ): Result<R> {
        return TelemetryUtils.withSpan(
            operationName = "usecase_$useCaseName",
            attributes = mapOf(
                "use_case" to useCaseName,
                "layer" to "domain"
            ) + additionalAttributes
        ) {
            val startTime = Clock.System.now()
            
            try {
                // Record use case execution start
                telemetryProvider.recordEvent(
                    TelemetryEvent(
                        name = "usecase_execution_started",
                        level = EventLevel.INFO,
                        attributes = mapOf(
                            "use_case" to useCaseName
                        ) + additionalAttributes
                    )
                )
                
                val result = operation()
                
                val endTime = Clock.System.now()
                val duration = endTime.toEpochMilliseconds() - startTime.toEpochMilliseconds()
                
                // Record metrics
                recordUseCaseMetrics(duration, result is Result.Success, if (result is Result.Error) result.exception else null)
                
                result
                
            } catch (e: Exception) {
                val endTime = Clock.System.now()
                val duration = endTime.toEpochMilliseconds() - startTime.toEpochMilliseconds()
                
                recordUseCaseMetrics(duration, false, e)
                throw e
            }
        }
    }
    
    private suspend fun recordUseCaseMetrics(duration: Long, success: Boolean, error: Throwable?) {
        val attributes = mapOf(
            "use_case" to useCaseName,
            "success" to success.toString()
        )
        
        // Record duration
        telemetryProvider.recordMetric(
            TelemetryMetric(
                name = "usecase_execution_duration",
                value = duration.toDouble(),
                unit = MetricUnit.MILLISECONDS,
                type = MetricType.HISTOGRAM,
                attributes = attributes
            )
        )
        
        // Record execution count
        telemetryProvider.recordMetric(
            TelemetryMetric(
                name = "usecase_execution_count",
                value = 1.0,
                unit = MetricUnit.COUNT,
                type = MetricType.COUNTER,
                attributes = attributes
            )
        )
        
        // Record error if present
        if (error != null) {
            TelemetryUtils.recordError(
                operation = "usecase_$useCaseName",
                errorType = error::class.simpleName ?: "UnknownError",
                errorMessage = error.message ?: "No error message",
                stackTrace = error.stackTraceToString()
            )
        }
        
        // Record completion event
        telemetryProvider.recordEvent(
            TelemetryEvent(
                name = if (success) "usecase_execution_success" else "usecase_execution_error",
                level = if (success) EventLevel.INFO else EventLevel.ERROR,
                attributes = attributes + if (error != null) {
                    mapOf(
                        "error_type" to (error::class.simpleName ?: "Unknown"),
                        "error_message" to (error.message ?: "No message")
                    )
                } else emptyMap()
            )
        )
    }
}

/**
 * Tracing utilities for instrumentation
 */
object TracingUtils {
    
    /**
     * Create traced API client
     */
    fun traceApiClient(weatherApi: WeatherApi): WeatherApi {
        return TracedWeatherApi(weatherApi)
    }
    
    /**
     * Create traced repository wrapper
     */
    fun <T> createTracedRepository(repositoryName: String): TracedRepository<T> {
        return TracedRepository(repositoryName)
    }
    
    /**
     * Create traced use case wrapper
     */
    fun <T> createTracedUseCase(useCaseName: String): TracedUseCase<T> {
        return TracedUseCase(useCaseName)
    }
    
    /**
     * Extract trace context from current span
     */
    fun getCurrentTraceContext(): Map<String, String> {
        // In a real implementation, this would extract trace context from active span
        return mapOf(
            "trace-id" to TelemetryUtils.generateTraceId(),
            "span-id" to TelemetryUtils.generateSpanId(),
            "sampling" to "1"
        )
    }
    
    /**
     * Inject trace context into HTTP headers
     */
    fun injectTraceHeaders(headers: MutableMap<String, String>) {
        val traceContext = getCurrentTraceContext()
        headers["X-Trace-Id"] = traceContext["trace-id"] ?: ""
        headers["X-Span-Id"] = traceContext["span-id"] ?: ""
        headers["X-Sampling"] = traceContext["sampling"] ?: "0"
    }
    
    /**
     * Create correlated span for child operations
     */
    suspend fun createChildSpan(
        parentTraceId: String,
        operationName: String,
        attributes: Map<String, String> = emptyMap()
    ): TelemetrySpan {
        return TelemetrySpan(
            traceId = parentTraceId,
            spanId = TelemetryUtils.generateSpanId(),
            operationName = operationName,
            attributes = attributes
        )
    }
}