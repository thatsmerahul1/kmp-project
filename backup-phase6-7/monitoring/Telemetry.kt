package com.weather.monitoring

import com.weather.domain.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * OpenTelemetry-inspired monitoring system for KMP 2025
 * 
 * Features:
 * - Distributed tracing
 * - Custom metrics collection
 * - Performance monitoring
 * - Error tracking
 * - Resource utilization monitoring
 * - Cross-platform compatibility
 */
interface TelemetryProvider {
    suspend fun recordMetric(metric: TelemetryMetric): Result<Unit>
    suspend fun startSpan(span: TelemetrySpan): Result<SpanContext>
    suspend fun endSpan(context: SpanContext, result: SpanResult): Result<Unit>
    suspend fun recordEvent(event: TelemetryEvent): Result<Unit>
    fun getMetricsFlow(): Flow<TelemetryMetric>
    fun getSpansFlow(): Flow<TelemetrySpan>
    suspend fun flush(): Result<Unit>
}

/**
 * Telemetry metric for performance and usage tracking
 */
@Serializable
data class TelemetryMetric(
    val name: String,
    val value: Double,
    val unit: MetricUnit,
    val type: MetricType,
    val timestamp: Instant = Clock.System.now(),
    val attributes: Map<String, String> = emptyMap(),
    val tags: List<String> = emptyList()
)

/**
 * Telemetry span for distributed tracing
 */
@Serializable
data class TelemetrySpan(
    val traceId: String,
    val spanId: String,
    val parentSpanId: String? = null,
    val operationName: String,
    val startTime: Instant = Clock.System.now(),
    val endTime: Instant? = null,
    val duration: Long? = null, // milliseconds
    val status: SpanStatus = SpanStatus.OK,
    val attributes: Map<String, String> = emptyMap(),
    val events: List<SpanEvent> = emptyList()
)

/**
 * Telemetry event for discrete occurrences
 */
@Serializable
data class TelemetryEvent(
    val name: String,
    val timestamp: Instant = Clock.System.now(),
    val level: EventLevel = EventLevel.INFO,
    val attributes: Map<String, String> = emptyMap(),
    val stackTrace: String? = null
)

/**
 * Span context for tracking active spans
 */
data class SpanContext(
    val traceId: String,
    val spanId: String,
    val startTime: Instant
)

/**
 * Span result for completing spans
 */
data class SpanResult(
    val status: SpanStatus,
    val errorMessage: String? = null,
    val attributes: Map<String, String> = emptyMap()
)

@Serializable
data class SpanEvent(
    val name: String,
    val timestamp: Instant = Clock.System.now(),
    val attributes: Map<String, String> = emptyMap()
)

enum class MetricUnit {
    COUNT,
    BYTES,
    MILLISECONDS,
    SECONDS,
    PERCENTAGE,
    REQUESTS_PER_SECOND,
    ERRORS_PER_SECOND,
    MEMORY_BYTES,
    CPU_PERCENTAGE
}

enum class MetricType {
    COUNTER,
    GAUGE,
    HISTOGRAM,
    SUMMARY
}

enum class SpanStatus {
    OK,
    ERROR,
    TIMEOUT,
    CANCELLED
}

enum class EventLevel {
    DEBUG,
    INFO,
    WARNING,
    ERROR,
    CRITICAL
}

/**
 * Production telemetry provider implementation
 */
class WeatherKMPTelemetryProvider : TelemetryProvider {
    
    private val metricsFlow = MutableSharedFlow<TelemetryMetric>()
    private val spansFlow = MutableSharedFlow<TelemetrySpan>()
    private val activeSpans = mutableMapOf<String, SpanContext>()
    private val collectedMetrics = mutableListOf<TelemetryMetric>()
    private val collectedSpans = mutableListOf<TelemetrySpan>()
    private val collectedEvents = mutableListOf<TelemetryEvent>()
    
    override suspend fun recordMetric(metric: TelemetryMetric): Result<Unit> {
        return try {
            collectedMetrics.add(metric)
            metricsFlow.emit(metric)
            
            // Log important metrics
            when (metric.type) {
                MetricType.COUNTER -> println("📊 Counter: ${metric.name} = ${metric.value}")
                MetricType.GAUGE -> println("📈 Gauge: ${metric.name} = ${metric.value} ${metric.unit}")
                MetricType.HISTOGRAM -> println("📊 Histogram: ${metric.name} = ${metric.value}")
                MetricType.SUMMARY -> println("📋 Summary: ${metric.name} = ${metric.value}")
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(TelemetryException("Failed to record metric: ${e.message}", e))
        }
    }
    
    override suspend fun startSpan(span: TelemetrySpan): Result<SpanContext> {
        return try {
            val context = SpanContext(
                traceId = span.traceId,
                spanId = span.spanId,
                startTime = span.startTime
            )
            
            activeSpans[span.spanId] = context
            
            println("🔗 Started span: ${span.operationName} (${span.spanId})")
            
            Result.Success(context)
        } catch (e: Exception) {
            Result.Error(TelemetryException("Failed to start span: ${e.message}", e))
        }
    }
    
    override suspend fun endSpan(context: SpanContext, result: SpanResult): Result<Unit> {
        return try {
            val endTime = Clock.System.now()
            val duration = endTime.toEpochMilliseconds() - context.startTime.toEpochMilliseconds()
            
            val completedSpan = TelemetrySpan(
                traceId = context.traceId,
                spanId = context.spanId,
                operationName = "completed_operation",
                startTime = context.startTime,
                endTime = endTime,
                duration = duration,
                status = result.status,
                attributes = result.attributes
            )
            
            collectedSpans.add(completedSpan)
            spansFlow.emit(completedSpan)
            activeSpans.remove(context.spanId)
            
            println("✅ Ended span: ${context.spanId} (${duration}ms, ${result.status})")
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(TelemetryException("Failed to end span: ${e.message}", e))
        }
    }
    
    override suspend fun recordEvent(event: TelemetryEvent): Result<Unit> {
        return try {
            collectedEvents.add(event)
            
            val emoji = when (event.level) {
                EventLevel.DEBUG -> "🔍"
                EventLevel.INFO -> "ℹ️"
                EventLevel.WARNING -> "⚠️"
                EventLevel.ERROR -> "❌"
                EventLevel.CRITICAL -> "🚨"
            }
            
            println("$emoji Event: ${event.name} (${event.level})")
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(TelemetryException("Failed to record event: ${e.message}", e))
        }
    }
    
    override fun getMetricsFlow(): Flow<TelemetryMetric> = metricsFlow
    
    override fun getSpansFlow(): Flow<TelemetrySpan> = spansFlow
    
    override suspend fun flush(): Result<Unit> {
        return try {
            println("🚀 Flushing telemetry data...")
            println("   Metrics: ${collectedMetrics.size}")
            println("   Spans: ${collectedSpans.size}")
            println("   Events: ${collectedEvents.size}")
            
            // In production, this would send data to monitoring service
            exportToMonitoringService()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(TelemetryException("Failed to flush telemetry: ${e.message}", e))
        }
    }
    
    private suspend fun exportToMonitoringService() {
        // Export to monitoring services like:
        // - Prometheus
        // - Grafana
        // - DataDog
        // - New Relic
        // - Custom monitoring endpoint
        
        println("📤 Exporting to monitoring services...")
        
        // Simulate export
        val telemetryData = TelemetryExport(
            metrics = collectedMetrics.takeLast(100), // Last 100 metrics
            spans = collectedSpans.takeLast(50), // Last 50 spans
            events = collectedEvents.takeLast(100) // Last 100 events
        )
        
        // In production, this would be HTTP POST to monitoring endpoint
        println("📊 Exported ${telemetryData.metrics.size} metrics, ${telemetryData.spans.size} spans, ${telemetryData.events.size} events")
    }
    
    fun getCollectedMetrics(): List<TelemetryMetric> = collectedMetrics.toList()
    fun getCollectedSpans(): List<TelemetrySpan> = collectedSpans.toList()
    fun getCollectedEvents(): List<TelemetryEvent> = collectedEvents.toList()
}

@Serializable
data class TelemetryExport(
    val metrics: List<TelemetryMetric>,
    val spans: List<TelemetrySpan>,
    val events: List<TelemetryEvent>,
    val timestamp: Instant = Clock.System.now()
)

/**
 * Telemetry utilities for common monitoring tasks
 */
object TelemetryUtils {
    
    private val telemetryProvider = WeatherKMPTelemetryProvider()
    
    /**
     * Generate unique trace ID
     */
    fun generateTraceId(): String {
        return "trace-${Clock.System.now().toEpochMilliseconds()}-${(0..9999).random()}"
    }
    
    /**
     * Generate unique span ID
     */
    fun generateSpanId(): String {
        return "span-${Clock.System.now().toEpochMilliseconds()}-${(0..9999).random()}"
    }
    
    /**
     * Record API call metrics
     */
    suspend fun recordApiCall(
        endpoint: String,
        method: String,
        statusCode: Int,
        duration: Long,
        size: Long = 0
    ): Result<Unit> {
        val attributes = mapOf(
            "endpoint" to endpoint,
            "method" to method,
            "status_code" to statusCode.toString()
        )
        
        // Record multiple related metrics
        val metrics = listOf(
            TelemetryMetric(
                name = "api_call_duration",
                value = duration.toDouble(),
                unit = MetricUnit.MILLISECONDS,
                type = MetricType.HISTOGRAM,
                attributes = attributes
            ),
            TelemetryMetric(
                name = "api_call_count",
                value = 1.0,
                unit = MetricUnit.COUNT,
                type = MetricType.COUNTER,
                attributes = attributes
            ),
            TelemetryMetric(
                name = "api_response_size",
                value = size.toDouble(),
                unit = MetricUnit.BYTES,
                type = MetricType.HISTOGRAM,
                attributes = attributes
            )
        )
        
        return try {
            metrics.forEach { metric ->
                telemetryProvider.recordMetric(metric)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(TelemetryException("Failed to record API call metrics: ${e.message}", e))
        }
    }
    
    /**
     * Record performance metrics
     */
    suspend fun recordPerformanceMetric(
        operation: String,
        duration: Long,
        success: Boolean,
        additionalAttributes: Map<String, String> = emptyMap()
    ): Result<Unit> {
        val attributes = mapOf(
            "operation" to operation,
            "success" to success.toString()
        ) + additionalAttributes
        
        val metric = TelemetryMetric(
            name = "operation_performance",
            value = duration.toDouble(),
            unit = MetricUnit.MILLISECONDS,
            type = MetricType.HISTOGRAM,
            attributes = attributes
        )
        
        return telemetryProvider.recordMetric(metric)
    }
    
    /**
     * Record error metrics
     */
    suspend fun recordError(
        operation: String,
        errorType: String,
        errorMessage: String,
        stackTrace: String? = null
    ): Result<Unit> {
        val metric = TelemetryMetric(
            name = "error_count",
            value = 1.0,
            unit = MetricUnit.COUNT,
            type = MetricType.COUNTER,
            attributes = mapOf(
                "operation" to operation,
                "error_type" to errorType,
                "error_message" to errorMessage.take(100) // Truncate for storage
            )
        )
        
        val event = TelemetryEvent(
            name = "error_occurred",
            level = EventLevel.ERROR,
            attributes = mapOf(
                "operation" to operation,
                "error_type" to errorType,
                "error_message" to errorMessage
            ),
            stackTrace = stackTrace
        )
        
        return try {
            telemetryProvider.recordMetric(metric)
            telemetryProvider.recordEvent(event)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(TelemetryException("Failed to record error: ${e.message}", e))
        }
    }
    
    /**
     * Create instrumented span for operation tracing
     */
    suspend fun <T> withSpan(
        operationName: String,
        attributes: Map<String, String> = emptyMap(),
        operation: suspend () -> T
    ): T {
        val traceId = generateTraceId()
        val spanId = generateSpanId()
        
        val span = TelemetrySpan(
            traceId = traceId,
            spanId = spanId,
            operationName = operationName,
            attributes = attributes
        )
        
        val context = when (val result = telemetryProvider.startSpan(span)) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
        
        return try {
            val result = operation()
            telemetryProvider.endSpan(context, SpanResult(SpanStatus.OK))
            result
        } catch (e: Exception) {
            telemetryProvider.endSpan(
                context, 
                SpanResult(
                    status = SpanStatus.ERROR,
                    errorMessage = e.message,
                    attributes = mapOf("exception_type" to e::class.simpleName.orEmpty())
                )
            )
            throw e
        }
    }
    
    /**
     * Get telemetry provider instance
     */
    fun getTelemetryProvider(): TelemetryProvider = telemetryProvider
}

/**
 * Telemetry exception for monitoring errors
 */
class TelemetryException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)