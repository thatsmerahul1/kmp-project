package com.weather.domain.common

/**
 * Logger abstraction for 2025 architecture standards
 * 
 * This abstraction allows for:
 * - Platform-specific logging implementations
 * - Structured logging with context
 * - Performance monitoring integration
 * - Analytics and crash reporting integration
 * - Environment-based log level control
 */
interface Logger {
    
    /**
     * Log a verbose/trace message
     * Use for detailed debugging information
     */
    fun verbose(tag: String, message: String, throwable: Throwable? = null)
    
    /**
     * Log a debug message
     * Use for debugging information
     */
    fun debug(tag: String, message: String, throwable: Throwable? = null)
    
    /**
     * Log an info message
     * Use for general information
     */
    fun info(tag: String, message: String, throwable: Throwable? = null)
    
    /**
     * Log a warning message
     * Use for potentially problematic situations
     */
    fun warn(tag: String, message: String, throwable: Throwable? = null)
    
    /**
     * Log an error message
     * Use for error conditions that don't crash the app
     */
    fun error(tag: String, message: String, throwable: Throwable? = null)
    
    /**
     * Log a critical error message
     * Use for severe error conditions that might crash the app
     */
    fun critical(tag: String, message: String, throwable: Throwable? = null)
    
    /**
     * Log structured data with additional context
     */
    fun logWithContext(
        level: LogLevel,
        tag: String,
        message: String,
        context: Map<String, Any> = emptyMap(),
        throwable: Throwable? = null
    )
    
    /**
     * Log performance metrics
     */
    fun logPerformance(
        operation: String,
        duration: Long,
        context: Map<String, Any> = emptyMap()
    )
    
    /**
     * Log user events for analytics
     */
    fun logEvent(
        eventName: String,
        parameters: Map<String, Any> = emptyMap()
    )
    
    /**
     * Log business metric
     */
    fun logMetric(
        metricName: String,
        value: Double,
        unit: String? = null,
        context: Map<String, Any> = emptyMap()
    )
    
    /**
     * Set user context for all subsequent logs
     */
    fun setUserContext(userId: String?, userProperties: Map<String, Any> = emptyMap())
    
    /**
     * Set custom context that will be included in all logs
     */
    fun setCustomContext(key: String, value: Any)
    
    /**
     * Clear all custom context
     */
    fun clearContext()
    
    /**
     * Check if logging is enabled for the given level
     */
    fun isLoggable(level: LogLevel): Boolean
}

/**
 * Log levels in order of severity
 */
enum class LogLevel(val value: Int) {
    VERBOSE(0),
    DEBUG(1),
    INFO(2),
    WARN(3),
    ERROR(4),
    CRITICAL(5)
}

/**
 * Default Logger implementation for shared module
 * Platform-specific implementations should extend this
 */
abstract class BaseLogger : Logger {
    
    private var _minLogLevel: LogLevel = LogLevel.DEBUG
    protected val globalContext = mutableMapOf<String, Any>()
    protected var userContext: Map<String, Any> = emptyMap()
    
    /**
     * Set minimum log level
     */
    fun setMinLogLevel(level: LogLevel) {
        _minLogLevel = level
    }
    
    protected val minLogLevel: LogLevel get() = _minLogLevel
    
    override fun isLoggable(level: LogLevel): Boolean {
        return level.value >= minLogLevel.value
    }
    
    override fun verbose(tag: String, message: String, throwable: Throwable?) {
        if (isLoggable(LogLevel.VERBOSE)) {
            logWithContext(LogLevel.VERBOSE, tag, message, emptyMap(), throwable)
        }
    }
    
    override fun debug(tag: String, message: String, throwable: Throwable?) {
        if (isLoggable(LogLevel.DEBUG)) {
            logWithContext(LogLevel.DEBUG, tag, message, emptyMap(), throwable)
        }
    }
    
    override fun info(tag: String, message: String, throwable: Throwable?) {
        if (isLoggable(LogLevel.INFO)) {
            logWithContext(LogLevel.INFO, tag, message, emptyMap(), throwable)
        }
    }
    
    override fun warn(tag: String, message: String, throwable: Throwable?) {
        if (isLoggable(LogLevel.WARN)) {
            logWithContext(LogLevel.WARN, tag, message, emptyMap(), throwable)
        }
    }
    
    override fun error(tag: String, message: String, throwable: Throwable?) {
        if (isLoggable(LogLevel.ERROR)) {
            logWithContext(LogLevel.ERROR, tag, message, emptyMap(), throwable)
        }
    }
    
    override fun critical(tag: String, message: String, throwable: Throwable?) {
        if (isLoggable(LogLevel.CRITICAL)) {
            logWithContext(LogLevel.CRITICAL, tag, message, emptyMap(), throwable)
        }
    }
    
    override fun logPerformance(
        operation: String,
        duration: Long,
        context: Map<String, Any>
    ) {
        val perfContext = context.toMutableMap().apply {
            put("operation", operation)
            put("duration_ms", duration)
            put("type", "performance")
        }
        logWithContext(LogLevel.INFO, "PERFORMANCE", "Operation completed", perfContext)
    }
    
    override fun logEvent(eventName: String, parameters: Map<String, Any>) {
        val eventContext = parameters.toMutableMap().apply {
            put("event_name", eventName)
            put("type", "analytics_event")
        }
        logWithContext(LogLevel.INFO, "ANALYTICS", "User event", eventContext)
    }
    
    override fun logMetric(
        metricName: String,
        value: Double,
        unit: String?,
        context: Map<String, Any>
    ) {
        val metricContext = context.toMutableMap().apply {
            put("metric_name", metricName)
            put("value", value)
            unit?.let { put("unit", it) }
            put("type", "metric")
        }
        logWithContext(LogLevel.INFO, "METRICS", "Business metric", metricContext)
    }
    
    override fun setUserContext(userId: String?, userProperties: Map<String, Any>) {
        userContext = userProperties.toMutableMap().apply {
            userId?.let { put("user_id", it) }
        }
    }
    
    override fun setCustomContext(key: String, value: Any) {
        globalContext[key] = value
    }
    
    override fun clearContext() {
        globalContext.clear()
        userContext = emptyMap()
    }
    
    /**
     * Merge all context sources
     */
    protected fun buildFullContext(additionalContext: Map<String, Any>): Map<String, Any> {
        val timestamp = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        return globalContext + userContext + additionalContext + mapOf(
            "timestamp" to timestamp,
            "platform" to "shared"
        )
    }
    
    /**
     * Abstract method for platform-specific logging implementation
     */
    protected abstract fun performLog(
        level: LogLevel,
        tag: String,
        message: String,
        fullContext: Map<String, Any>,
        throwable: Throwable?
    )
    
    override fun logWithContext(
        level: LogLevel,
        tag: String,
        message: String,
        context: Map<String, Any>,
        throwable: Throwable?
    ) {
        if (isLoggable(level)) {
            val fullContext = buildFullContext(context)
            performLog(level, tag, message, fullContext, throwable)
        }
    }
}

/**
 * Console-based logger for development/testing
 */
class ConsoleLogger : BaseLogger() {
    
    override fun performLog(
        level: LogLevel,
        tag: String,
        message: String,
        fullContext: Map<String, Any>,
        throwable: Throwable?
    ) {
        val levelStr = level.name.padEnd(8)
        val tagStr = tag.padEnd(15)
        
        println("$levelStr | $tagStr | $message")
        
        if (fullContext.isNotEmpty()) {
            val contextStr = fullContext.entries
                .filter { it.key !in listOf("timestamp", "thread") }
                .joinToString(", ") { "${it.key}=${it.value}" }
            if (contextStr.isNotEmpty()) {
                println("$levelStr | $tagStr | Context: $contextStr")
            }
        }
        
        throwable?.let {
            println("$levelStr | $tagStr | Exception: ${it.message}")
            println(it.stackTraceToString())
        }
    }
}

/**
 * No-op logger for production when logging should be disabled
 */
class NoOpLogger : Logger {
    override fun verbose(tag: String, message: String, throwable: Throwable?) {}
    override fun debug(tag: String, message: String, throwable: Throwable?) {}
    override fun info(tag: String, message: String, throwable: Throwable?) {}
    override fun warn(tag: String, message: String, throwable: Throwable?) {}
    override fun error(tag: String, message: String, throwable: Throwable?) {}
    override fun critical(tag: String, message: String, throwable: Throwable?) {}
    override fun logWithContext(level: LogLevel, tag: String, message: String, context: Map<String, Any>, throwable: Throwable?) {}
    override fun logPerformance(operation: String, duration: Long, context: Map<String, Any>) {}
    override fun logEvent(eventName: String, parameters: Map<String, Any>) {}
    override fun logMetric(metricName: String, value: Double, unit: String?, context: Map<String, Any>) {}
    override fun setUserContext(userId: String?, userProperties: Map<String, Any>) {}
    override fun setCustomContext(key: String, value: Any) {}
    override fun clearContext() {}
    override fun isLoggable(level: LogLevel): Boolean = false
}

/**
 * Composite logger that forwards to multiple loggers
 */
class CompositeLogger(private val loggers: List<Logger>) : Logger {
    
    override fun verbose(tag: String, message: String, throwable: Throwable?) {
        loggers.forEach { it.verbose(tag, message, throwable) }
    }
    
    override fun debug(tag: String, message: String, throwable: Throwable?) {
        loggers.forEach { it.debug(tag, message, throwable) }
    }
    
    override fun info(tag: String, message: String, throwable: Throwable?) {
        loggers.forEach { it.info(tag, message, throwable) }
    }
    
    override fun warn(tag: String, message: String, throwable: Throwable?) {
        loggers.forEach { it.warn(tag, message, throwable) }
    }
    
    override fun error(tag: String, message: String, throwable: Throwable?) {
        loggers.forEach { it.error(tag, message, throwable) }
    }
    
    override fun critical(tag: String, message: String, throwable: Throwable?) {
        loggers.forEach { it.critical(tag, message, throwable) }
    }
    
    override fun logWithContext(level: LogLevel, tag: String, message: String, context: Map<String, Any>, throwable: Throwable?) {
        loggers.forEach { it.logWithContext(level, tag, message, context, throwable) }
    }
    
    override fun logPerformance(operation: String, duration: Long, context: Map<String, Any>) {
        loggers.forEach { it.logPerformance(operation, duration, context) }
    }
    
    override fun logEvent(eventName: String, parameters: Map<String, Any>) {
        loggers.forEach { it.logEvent(eventName, parameters) }
    }
    
    override fun logMetric(metricName: String, value: Double, unit: String?, context: Map<String, Any>) {
        loggers.forEach { it.logMetric(metricName, value, unit, context) }
    }
    
    override fun setUserContext(userId: String?, userProperties: Map<String, Any>) {
        loggers.forEach { it.setUserContext(userId, userProperties) }
    }
    
    override fun setCustomContext(key: String, value: Any) {
        loggers.forEach { it.setCustomContext(key, value) }
    }
    
    override fun clearContext() {
        loggers.forEach { it.clearContext() }
    }
    
    override fun isLoggable(level: LogLevel): Boolean {
        return loggers.any { it.isLoggable(level) }
    }
}

/**
 * Global logger instance
 * Should be set during app initialization with platform-specific implementation
 */
object AppLogger {
    private var logger: Logger = ConsoleLogger()
    
    fun setLogger(newLogger: Logger) {
        logger = newLogger
    }
    
    fun getLogger(): Logger = logger
    
    // Convenience methods
    fun v(tag: String, message: String, throwable: Throwable? = null) = logger.verbose(tag, message, throwable)
    fun d(tag: String, message: String, throwable: Throwable? = null) = logger.debug(tag, message, throwable)
    fun i(tag: String, message: String, throwable: Throwable? = null) = logger.info(tag, message, throwable)
    fun w(tag: String, message: String, throwable: Throwable? = null) = logger.warn(tag, message, throwable)
    fun e(tag: String, message: String, throwable: Throwable? = null) = logger.error(tag, message, throwable)
    fun c(tag: String, message: String, throwable: Throwable? = null) = logger.critical(tag, message, throwable)
}

/**
 * Extension functions for easier logging
 */
inline fun <T> T.logPerformance(
    operation: String,
    logger: Logger = AppLogger.getLogger(),
    block: () -> T
): T {
    val startTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
    return try {
        val result = block()
        val duration = kotlinx.datetime.Clock.System.now().toEpochMilliseconds() - startTime
        logger.logPerformance(operation, duration, mapOf("success" to true))
        result
    } catch (e: Exception) {
        val duration = kotlinx.datetime.Clock.System.now().toEpochMilliseconds() - startTime
        logger.logPerformance(operation, duration, mapOf("success" to false, "error" to e.message.orEmpty()))
        throw e
    }
}

/**
 * DSL for structured logging
 */
class LogContext {
    private val context = mutableMapOf<String, Any>()
    
    fun put(key: String, value: Any) {
        context[key] = value
    }
    
    operator fun String.invoke(value: Any) {
        context[this] = value
    }
    
    fun build(): Map<String, Any> = context.toMap()
}

inline fun Logger.logWithContext(
    level: LogLevel,
    tag: String,
    message: String,
    throwable: Throwable? = null,
    contextBuilder: LogContext.() -> Unit
) {
    val context = LogContext().apply(contextBuilder).build()
    logWithContext(level, tag, message, context, throwable)
}