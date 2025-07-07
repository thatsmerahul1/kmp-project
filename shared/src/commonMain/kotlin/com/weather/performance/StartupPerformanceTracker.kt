package com.weather.performance

import com.weather.domain.common.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * App startup performance tracking for 2025 architecture standards
 * 
 * This tracker provides:
 * - Cold start, warm start, and hot start measurement
 * - Component initialization timing
 * - Memory usage tracking during startup
 * - Performance bottleneck identification
 * - Startup optimization recommendations
 */

/**
 * Startup phase definitions
 */
enum class StartupPhase(val description: String) {
    APP_CREATED("Application created"),
    DI_INITIALIZED("Dependency injection initialized"),
    DATABASE_READY("Database ready"),
    NETWORK_READY("Network client ready"),
    UI_READY("UI framework ready"),
    FIRST_SCREEN_DISPLAYED("First screen displayed"),
    STARTUP_COMPLETE("Startup complete")
}

/**
 * Startup type classification
 */
enum class StartupType {
    COLD,   // App process not in memory
    WARM,   // App process in memory, but activity destroyed
    HOT     // App process and activity in memory
}

/**
 * Performance measurement data
 */
data class PerformanceMeasurement(
    val phase: StartupPhase,
    val timestamp: Long,
    val duration: Long,
    val memoryUsageMB: Double = 0.0,
    val metadata: Map<String, Any> = emptyMap()
)

/**
 * Complete startup performance report
 */
data class StartupPerformanceReport(
    val startupType: StartupType,
    val totalDuration: Long,
    val measurements: List<PerformanceMeasurement>,
    val bottlenecks: List<String>,
    val recommendations: List<String>,
    val summary: Map<String, Any>
) {
    val isOptimal: Boolean
        get() = totalDuration < getOptimalThreshold(startupType)
    
    private fun getOptimalThreshold(type: StartupType): Long = when (type) {
        StartupType.COLD -> 2000L   // 2 seconds
        StartupType.WARM -> 1000L   // 1 second
        StartupType.HOT -> 500L     // 0.5 seconds
    }
}

/**
 * Main startup performance tracker
 */
class StartupPerformanceTracker(
    private val logger: Logger
) {
    private var startupStartTime: Long = 0L
    private var currentStartupType: StartupType = StartupType.COLD
    private val measurements = mutableListOf<PerformanceMeasurement>()
    private var lastPhaseTime: Long = 0L
    
    private val _startupState = MutableStateFlow<StartupPhase?>(null)
    val startupState: StateFlow<StartupPhase?> = _startupState.asStateFlow()
    
    private val _isStartupComplete = MutableStateFlow(false)
    val isStartupComplete: StateFlow<Boolean> = _isStartupComplete.asStateFlow()
    
    /**
     * Begin startup tracking
     */
    fun beginStartupTracking(startupType: StartupType = StartupType.COLD) {
        startupStartTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        lastPhaseTime = startupStartTime
        currentStartupType = startupType
        measurements.clear()
        _isStartupComplete.value = false
        
        logger.info("StartupTracker", "🚀 Starting $startupType startup tracking")
        recordPhase(StartupPhase.APP_CREATED)
    }
    
    /**
     * Record completion of a startup phase
     */
    fun recordPhase(
        phase: StartupPhase,
        metadata: Map<String, Any> = emptyMap()
    ) {
        val currentTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        val phaseDuration = currentTime - lastPhaseTime
        val memoryUsage = estimateMemoryUsage()
        
        val measurement = PerformanceMeasurement(
            phase = phase,
            timestamp = currentTime,
            duration = phaseDuration,
            memoryUsageMB = memoryUsage,
            metadata = metadata
        )
        
        measurements.add(measurement)
        _startupState.value = phase
        lastPhaseTime = currentTime
        
        logger.info("StartupTracker", "📈 ${phase.description} completed in ${phaseDuration}ms (Memory: ${memoryUsage}MB)")
        
        if (phase == StartupPhase.STARTUP_COMPLETE) {
            completeStartupTracking()
        }
    }
    
    /**
     * Complete startup tracking and generate report
     */
    private fun completeStartupTracking() {
        val totalDuration = kotlinx.datetime.Clock.System.now().toEpochMilliseconds() - startupStartTime
        _isStartupComplete.value = true
        
        val report = generateReport(totalDuration)
        logger.info("StartupTracker", "🏁 Startup completed in ${totalDuration}ms")
        logger.info("StartupTracker", "📊 Performance report: ${if (report.isOptimal) "✅ OPTIMAL" else "⚠️ NEEDS IMPROVEMENT"}")
        
        // Log recommendations
        report.recommendations.forEach { recommendation ->
            logger.info("StartupTracker", "💡 $recommendation")
        }
    }
    
    /**
     * Generate comprehensive performance report
     */
    fun generateReport(totalDuration: Long = getCurrentDuration()): StartupPerformanceReport {
        val bottlenecks = identifyBottlenecks()
        val recommendations = generateRecommendations(totalDuration, bottlenecks)
        val summary = generateSummary(totalDuration)
        
        return StartupPerformanceReport(
            startupType = currentStartupType,
            totalDuration = totalDuration,
            measurements = measurements.toList(),
            bottlenecks = bottlenecks,
            recommendations = recommendations,
            summary = summary
        )
    }
    
    /**
     * Get current startup duration
     */
    private fun getCurrentDuration(): Long {
        return kotlinx.datetime.Clock.System.now().toEpochMilliseconds() - startupStartTime
    }
    
    /**
     * Identify performance bottlenecks
     */
    private fun identifyBottlenecks(): List<String> {
        val bottlenecks = mutableListOf<String>()
        
        measurements.forEach { measurement ->
            when {
                measurement.duration > 500L -> {
                    bottlenecks.add("🐌 ${measurement.phase.description} took ${measurement.duration}ms (>500ms threshold)")
                }
                measurement.memoryUsageMB > 50.0 -> {
                    bottlenecks.add("💾 High memory usage during ${measurement.phase.description}: ${measurement.memoryUsageMB}MB")
                }
            }
        }
        
        // Check for missing phases
        val expectedPhases = StartupPhase.values()
        val recordedPhases = measurements.map { it.phase }
        expectedPhases.forEach { expectedPhase ->
            if (expectedPhase !in recordedPhases) {
                bottlenecks.add("❌ Missing startup phase: ${expectedPhase.description}")
            }
        }
        
        return bottlenecks
    }
    
    /**
     * Generate optimization recommendations
     */
    private fun generateRecommendations(totalDuration: Long, bottlenecks: List<String>): List<String> {
        val recommendations = mutableListOf<String>()
        
        // Duration-based recommendations
        when (currentStartupType) {
            StartupType.COLD -> {
                when {
                    totalDuration > 3000L -> recommendations.add("🚀 Cold start >3s: Consider lazy loading non-critical components")
                    totalDuration > 2000L -> recommendations.add("⚡ Cold start >2s: Optimize dependency injection initialization")
                }
            }
            StartupType.WARM -> {
                if (totalDuration > 1500L) {
                    recommendations.add("🔥 Warm start >1.5s: Cache critical data between sessions")
                }
            }
            StartupType.HOT -> {
                if (totalDuration > 800L) {
                    recommendations.add("⚡ Hot start >800ms: Review UI rendering performance")
                }
            }
        }
        
        // Phase-specific recommendations
        val diPhase = measurements.find { it.phase == StartupPhase.DI_INITIALIZED }
        if (diPhase != null && diPhase.duration > 200L) {
            recommendations.add("🔧 DI initialization slow: Consider compile-time DI with Dagger/Hilt")
        }
        
        val dbPhase = measurements.find { it.phase == StartupPhase.DATABASE_READY }
        if (dbPhase != null && dbPhase.duration > 300L) {
            recommendations.add("💾 Database initialization slow: Use lazy loading or pre-populated database")
        }
        
        val uiPhase = measurements.find { it.phase == StartupPhase.UI_READY }
        if (uiPhase != null && uiPhase.duration > 400L) {
            recommendations.add("🎨 UI initialization slow: Optimize Compose startup or use splash screen")
        }
        
        // Memory-based recommendations
        val maxMemory = measurements.maxOfOrNull { it.memoryUsageMB } ?: 0.0
        if (maxMemory > 100.0) {
            recommendations.add("📱 High memory usage (${maxMemory}MB): Review object retention and caching strategies")
        }
        
        // General recommendations
        if (bottlenecks.isNotEmpty()) {
            recommendations.add("🔍 Multiple bottlenecks detected: Consider profiling with Perfetto or systrace")
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("✨ Startup performance is optimal! Consider baseline profiles for even better performance")
        }
        
        return recommendations
    }
    
    /**
     * Generate performance summary
     */
    private fun generateSummary(totalDuration: Long): Map<String, Any> {
        return mapOf(
            "total_duration_ms" to totalDuration,
            "startup_type" to currentStartupType.name,
            "phase_count" to measurements.size,
            "average_phase_duration_ms" to if (measurements.isNotEmpty()) measurements.map { it.duration }.average() else 0.0,
            "max_phase_duration_ms" to (measurements.maxOfOrNull { it.duration } ?: 0L),
            "max_memory_usage_mb" to (measurements.maxOfOrNull { it.memoryUsageMB } ?: 0.0),
            "is_optimal" to (totalDuration < getOptimalThreshold()),
            "performance_grade" to getPerformanceGrade(totalDuration)
        )
    }
    
    /**
     * Get optimal threshold for current startup type
     */
    private fun getOptimalThreshold(): Long = when (currentStartupType) {
        StartupType.COLD -> 2000L
        StartupType.WARM -> 1000L
        StartupType.HOT -> 500L
    }
    
    /**
     * Get performance grade
     */
    private fun getPerformanceGrade(duration: Long): String {
        val threshold = getOptimalThreshold()
        return when {
            duration <= threshold * 0.75 -> "A+ (Excellent)"
            duration <= threshold -> "A (Good)"
            duration <= threshold * 1.5 -> "B (Fair)"
            duration <= threshold * 2.0 -> "C (Poor)"
            else -> "D (Very Poor)"
        }
    }
    
    /**
     * Estimate memory usage (simplified)
     */
    private fun estimateMemoryUsage(): Double {
        // In a real implementation, this would use platform-specific memory APIs
        // For now, return a simulated value based on startup progress
        val phaseCount = measurements.size
        return 20.0 + (phaseCount * 5.0) + (0..10).random() // Simulated memory usage
    }
    
    /**
     * Reset tracking state
     */
    fun reset() {
        measurements.clear()
        _startupState.value = null
        _isStartupComplete.value = false
        startupStartTime = 0L
        lastPhaseTime = 0L
        logger.debug("StartupTracker", "🔄 Tracker reset")
    }
    
    /**
     * Export performance data for analysis
     */
    fun exportPerformanceData(): Map<String, Any> {
        return mapOf(
            "startup_type" to currentStartupType.name,
            "total_duration_ms" to getCurrentDuration(),
            "measurements" to measurements.map { measurement ->
                mapOf(
                    "phase" to measurement.phase.name,
                    "duration_ms" to measurement.duration,
                    "memory_mb" to measurement.memoryUsageMB,
                    "timestamp" to measurement.timestamp,
                    "metadata" to measurement.metadata
                )
            },
            "device_info" to getDeviceInfo(),
            "app_version" to "1.0.0", // Could be injected
            "tracking_timestamp" to kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        )
    }
    
    /**
     * Get device information for performance context
     */
    private fun getDeviceInfo(): Map<String, String> {
        return mapOf(
            "platform" to "KMP",
            "architecture" to "Shared", // Platform-specific implementations would provide real data
            "memory_class" to "unknown",
            "cpu_count" to "unknown"
        )
    }
}

/**
 * Startup performance DSL for easy integration
 */
class StartupPerformanceDSL(private val tracker: StartupPerformanceTracker) {
    
    suspend fun <T> measurePhase(
        phase: StartupPhase,
        metadata: Map<String, Any> = emptyMap(),
        operation: suspend () -> T
    ): T {
        val result = operation()
        tracker.recordPhase(phase, metadata)
        return result
    }
    
    suspend fun <T> measureCustomPhase(
        phaseName: String,
        operation: suspend () -> T
    ): T {
        val startTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        val result = operation()
        val endTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        val duration = endTime - startTime
        
        // Log custom phase (could be extended to support custom phases)
        tracker.recordPhase(
            StartupPhase.STARTUP_COMPLETE, // Placeholder
            mapOf(
                "custom_phase" to phaseName,
                "custom_duration_ms" to duration
            )
        )
        
        return result
    }
}

/**
 * Extension function for easy startup tracking
 */
fun StartupPerformanceTracker.dsl(): StartupPerformanceDSL = StartupPerformanceDSL(this)

/**
 * Global startup performance tracker instance
 */
object AppStartupTracker {
    private var tracker: StartupPerformanceTracker? = null
    
    fun initialize(logger: Logger): StartupPerformanceTracker {
        if (tracker == null) {
            tracker = StartupPerformanceTracker(logger)
        }
        return tracker!!
    }
    
    fun get(): StartupPerformanceTracker? = tracker
    
    fun recordPhase(phase: StartupPhase, metadata: Map<String, Any> = emptyMap()) {
        tracker?.recordPhase(phase, metadata)
    }
}