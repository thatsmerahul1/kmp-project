package com.weather.ui.transitions

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

/**
 * Shared element transitions for 2025 smooth UI/UX standards
 * 
 * This module provides:
 * - Hero animations between screens
 * - Smooth content transitions
 * - Material Motion patterns
 * - Performance-optimized animations
 * - Accessibility-aware transitions
 * - Cross-platform animation abstraction
 */

/**
 * Transition types following Material Motion
 */
enum class TransitionType {
    // Container transforms
    CONTAINER_TRANSFORM,        // Seamless transition between containers
    SHARED_AXIS,               // Spatial or temporal relationship
    FADE_THROUGH,              // Sequential content changes
    FADE,                      // Simple opacity transition
    
    // Element-specific
    HERO,                      // Hero element animation
    SHARED_ELEMENT,            // Direct element sharing
    MORPH,                     // Shape transformation
    
    // Content-specific
    LIST_DETAIL,               // List to detail transition
    TAB_SWITCH,                // Tab content switching
    BOTTOM_SHEET,              // Bottom sheet reveal
    MODAL,                     // Modal presentation
    
    // Weather-specific
    WEATHER_CARD_EXPAND,       // Weather card to detail
    LOCATION_TRANSITION,       // Location switching
    FORECAST_SCROLL            // Forecast timeline
}

/**
 * Animation directions for shared axis transitions
 */
enum class TransitionDirection {
    HORIZONTAL_LEFT,
    HORIZONTAL_RIGHT,
    VERTICAL_UP,
    VERTICAL_DOWN,
    DEPTH_FORWARD,
    DEPTH_BACKWARD
}

/**
 * Transition timing configurations
 */
@Serializable
data class TransitionTiming(
    val duration: Long = 300L,              // Duration in milliseconds
    val delay: Long = 0L,                   // Delay before starting
    val staggerDelay: Long = 50L,           // Delay between staggered elements
    val easing: EasingType = EasingType.EMPHASIZED
)

enum class EasingType {
    LINEAR,
    EMPHASIZED,         // Material You standard
    EMPHASIZED_DECELERATE,
    EMPHASIZED_ACCELERATE,
    STANDARD,
    SPRING,
    BOUNCE,
    ELASTIC
}

/**
 * Shared element definition
 */
@Serializable
data class SharedElement(
    val id: String,                         // Unique identifier
    val tag: String,                        // Transition tag
    val bounds: ElementBounds,              // Current bounds
    val contentType: SharedElementType,     // Type of content
    val properties: ElementProperties       // Visual properties
)

@Serializable
data class ElementBounds(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)

@Serializable
data class ElementProperties(
    val cornerRadius: Float = 0f,
    val elevation: Float = 0f,
    val scaleX: Float = 1f,
    val scaleY: Float = 1f,
    val rotation: Float = 0f,
    val alpha: Float = 1f,
    val backgroundColor: ULong? = null,
    val borderWidth: Float = 0f,
    val borderColor: ULong? = null
)

enum class SharedElementType {
    IMAGE,
    TEXT,
    ICON,
    CONTAINER,
    BACKGROUND,
    CARD,
    BUTTON,
    FAB
}

/**
 * Transition state tracking
 */
data class TransitionState(
    val isActive: Boolean = false,
    val progress: Float = 0f,               // 0.0 to 1.0
    val direction: TransitionDirection? = null,
    val sourceScreen: String? = null,
    val targetScreen: String? = null,
    val sharedElements: List<SharedElement> = emptyList()
)

/**
 * Animation curve definitions
 */
object AnimationCurves {
    
    /**
     * Material You emphasized easing
     */
    fun emphasized(t: Float): Float {
        return cubicBezier(0.2f, 0.0f, 0.0f, 1.0f, t)
    }
    
    /**
     * Material You emphasized decelerate
     */
    fun emphasizedDecelerate(t: Float): Float {
        return cubicBezier(0.05f, 0.7f, 0.1f, 1.0f, t)
    }
    
    /**
     * Material You emphasized accelerate
     */
    fun emphasizedAccelerate(t: Float): Float {
        return cubicBezier(0.3f, 0.0f, 0.8f, 0.15f, t)
    }
    
    /**
     * Standard easing
     */
    fun standard(t: Float): Float {
        return cubicBezier(0.2f, 0.0f, 0.0f, 1.0f, t)
    }
    
    /**
     * Spring animation curve
     */
    fun spring(t: Float, tension: Float = 400f, friction: Float = 22f): Float {
        // Simplified spring calculation
        val dampingRatio = friction / (2 * kotlin.math.sqrt(tension))
        val angularFreq = kotlin.math.sqrt(tension)
        
        return if (dampingRatio < 1f) {
            val dampedFreq = angularFreq * kotlin.math.sqrt(1 - dampingRatio * dampingRatio)
            1f - kotlin.math.exp(-dampingRatio * angularFreq * t) * 
                kotlin.math.cos(dampedFreq * t).toFloat()
        } else {
            1f - kotlin.math.exp(-angularFreq * t).toFloat() * (1f + angularFreq * t)
        }
    }
    
    /**
     * Cubic bezier interpolation
     */
    private fun cubicBezier(x1: Float, y1: Float, x2: Float, y2: Float, t: Float): Float {
        // Simplified cubic bezier calculation for demonstration
        // Real implementation would use proper bezier math
        val u = 1f - t
        return u * u * u * 0f + 3f * u * u * t * y1 + 3f * u * t * t * y2 + t * t * t * 1f
    }
}

/**
 * Transition configuration builder
 */
class TransitionBuilder {
    
    private var transitionType: TransitionType = TransitionType.FADE
    private var timing: TransitionTiming = TransitionTiming()
    private var direction: TransitionDirection? = null
    private var sharedElements: MutableList<SharedElement> = mutableListOf()
    private var reducedMotion: Boolean = false
    
    fun type(type: TransitionType): TransitionBuilder {
        this.transitionType = type
        return this
    }
    
    fun timing(timing: TransitionTiming): TransitionBuilder {
        this.timing = timing
        return this
    }
    
    fun direction(direction: TransitionDirection): TransitionBuilder {
        this.direction = direction
        return this
    }
    
    fun addSharedElement(element: SharedElement): TransitionBuilder {
        this.sharedElements.add(element)
        return this
    }
    
    fun reducedMotion(enabled: Boolean): TransitionBuilder {
        this.reducedMotion = enabled
        return this
    }
    
    fun build(): TransitionConfiguration {
        return TransitionConfiguration(
            type = transitionType,
            timing = if (reducedMotion) timing.copy(duration = timing.duration / 3) else timing,
            direction = direction,
            sharedElements = sharedElements.toList(),
            reducedMotion = reducedMotion
        )
    }
}

/**
 * Complete transition configuration
 */
data class TransitionConfiguration(
    val type: TransitionType,
    val timing: TransitionTiming,
    val direction: TransitionDirection?,
    val sharedElements: List<SharedElement>,
    val reducedMotion: Boolean = false
)

/**
 * Main transition manager
 */
class SharedElementTransitionManager {
    
    private val _transitionState = MutableStateFlow(TransitionState())
    val transitionState: StateFlow<TransitionState> = _transitionState.asStateFlow()
    
    private val _activeTransitions = MutableStateFlow<Map<String, TransitionConfiguration>>(emptyMap())
    val activeTransitions: StateFlow<Map<String, TransitionConfiguration>> = _activeTransitions.asStateFlow()
    
    private val registeredElements = mutableMapOf<String, SharedElement>()
    
    /**
     * Register a shared element for transitions
     */
    fun registerSharedElement(element: SharedElement) {
        registeredElements[element.id] = element
    }
    
    /**
     * Unregister a shared element
     */
    fun unregisterSharedElement(elementId: String) {
        registeredElements.remove(elementId)
    }
    
    /**
     * Start a transition between screens
     */
    fun startTransition(
        sourceScreen: String,
        targetScreen: String,
        configuration: TransitionConfiguration
    ) {
        val transitionId = "${sourceScreen}_to_${targetScreen}"
        
        // Update active transitions
        val currentTransitions = _activeTransitions.value.toMutableMap()
        currentTransitions[transitionId] = configuration
        _activeTransitions.value = currentTransitions
        
        // Update transition state
        _transitionState.value = TransitionState(
            isActive = true,
            progress = 0f,
            direction = configuration.direction,
            sourceScreen = sourceScreen,
            targetScreen = targetScreen,
            sharedElements = configuration.sharedElements
        )
        
        // Platform-specific transition execution would happen here
        executeTransition(configuration)
    }
    
    /**
     * Update transition progress
     */
    fun updateTransitionProgress(progress: Float) {
        val currentState = _transitionState.value
        _transitionState.value = currentState.copy(progress = progress.coerceIn(0f, 1f))
        
        if (progress >= 1f) {
            completeTransition()
        }
    }
    
    /**
     * Complete current transition
     */
    private fun completeTransition() {
        _transitionState.value = TransitionState()
        
        // Clean up completed transitions
        val currentTransitions = _activeTransitions.value.toMutableMap()
        val completedTransitions = currentTransitions.filter { (_, config) ->
            // Remove transitions that have completed
            false // Simplified logic
        }
        _activeTransitions.value = completedTransitions
    }
    
    /**
     * Execute platform-specific transition
     */
    private fun executeTransition(configuration: TransitionConfiguration) {
        // Platform-specific implementation would handle actual animation
        // This is a placeholder for the common interface
        
        when (configuration.type) {
            TransitionType.HERO -> executeHeroTransition(configuration)
            TransitionType.CONTAINER_TRANSFORM -> executeContainerTransform(configuration)
            TransitionType.SHARED_AXIS -> executeSharedAxisTransition(configuration)
            TransitionType.FADE_THROUGH -> executeFadeThroughTransition(configuration)
            TransitionType.LIST_DETAIL -> executeListDetailTransition(configuration)
            TransitionType.WEATHER_CARD_EXPAND -> executeWeatherCardTransition(configuration)
            else -> executeGenericTransition(configuration)
        }
    }
    
    /**
     * Hero transition implementation
     */
    private fun executeHeroTransition(configuration: TransitionConfiguration) {
        // Implementation would animate shared elements from source to target positions
        // with smooth interpolation of bounds, properties, and content
    }
    
    /**
     * Container transform implementation
     */
    private fun executeContainerTransform(configuration: TransitionConfiguration) {
        // Implementation would morph container shapes and animate content
    }
    
    /**
     * Shared axis transition implementation
     */
    private fun executeSharedAxisTransition(configuration: TransitionConfiguration) {
        // Implementation would slide content along specified axis
        val direction = configuration.direction ?: TransitionDirection.HORIZONTAL_RIGHT
        // Animate based on direction
    }
    
    /**
     * Fade through transition implementation
     */
    private fun executeFadeThroughTransition(configuration: TransitionConfiguration) {
        // Implementation would fade out old content, then fade in new content
    }
    
    /**
     * List to detail transition implementation
     */
    private fun executeListDetailTransition(configuration: TransitionConfiguration) {
        // Implementation would expand list item to full detail view
    }
    
    /**
     * Weather card expansion transition
     */
    private fun executeWeatherCardTransition(configuration: TransitionConfiguration) {
        // Weather-specific transition for card expansion
        // Could include animated weather icons, data reveals, etc.
    }
    
    /**
     * Generic transition fallback
     */
    private fun executeGenericTransition(configuration: TransitionConfiguration) {
        // Simple fade transition as fallback
    }
    
    /**
     * Cancel current transition
     */
    fun cancelTransition() {
        _transitionState.value = TransitionState()
        _activeTransitions.value = emptyMap()
    }
    
    /**
     * Get registered shared element
     */
    fun getSharedElement(elementId: String): SharedElement? {
        return registeredElements[elementId]
    }
    
    /**
     * Update shared element bounds
     */
    fun updateSharedElementBounds(elementId: String, bounds: ElementBounds) {
        registeredElements[elementId]?.let { element ->
            registeredElements[elementId] = element.copy(bounds = bounds)
        }
    }
    
    /**
     * Create transition for weather app scenarios
     */
    fun createWeatherTransition(
        from: WeatherTransitionSource,
        to: WeatherTransitionTarget,
        reducedMotion: Boolean = false
    ): TransitionConfiguration {
        return when (from to to) {
            WeatherTransitionSource.CITY_LIST to WeatherTransitionTarget.CITY_DETAIL -> {
                TransitionBuilder()
                    .type(TransitionType.LIST_DETAIL)
                    .timing(TransitionTiming(duration = if (reducedMotion) 100L else 300L))
                    .direction(TransitionDirection.VERTICAL_UP)
                    .reducedMotion(reducedMotion)
                    .build()
            }
            WeatherTransitionSource.WEATHER_CARD to WeatherTransitionTarget.DETAIL_VIEW -> {
                TransitionBuilder()
                    .type(TransitionType.WEATHER_CARD_EXPAND)
                    .timing(TransitionTiming(duration = if (reducedMotion) 150L else 400L))
                    .reducedMotion(reducedMotion)
                    .build()
            }
            WeatherTransitionSource.MAIN_SCREEN to WeatherTransitionTarget.FORECAST -> {
                TransitionBuilder()
                    .type(TransitionType.SHARED_AXIS)
                    .timing(TransitionTiming(duration = if (reducedMotion) 100L else 250L))
                    .direction(TransitionDirection.HORIZONTAL_RIGHT)
                    .reducedMotion(reducedMotion)
                    .build()
            }
            else -> {
                TransitionBuilder()
                    .type(TransitionType.FADE_THROUGH)
                    .timing(TransitionTiming(duration = if (reducedMotion) 50L else 200L))
                    .reducedMotion(reducedMotion)
                    .build()
            }
        }
    }
}

/**
 * Weather app specific transition sources and targets
 */
enum class WeatherTransitionSource {
    CITY_LIST,
    WEATHER_CARD,
    MAIN_SCREEN,
    SEARCH_BAR,
    SETTINGS_BUTTON,
    FAB
}

enum class WeatherTransitionTarget {
    CITY_DETAIL,
    DETAIL_VIEW,
    FORECAST,
    SEARCH_SCREEN,
    SETTINGS_SCREEN,
    ADD_LOCATION
}

/**
 * Transition performance monitoring
 */
class TransitionPerformanceMonitor {
    
    private val performanceMetrics = mutableMapOf<String, TransitionMetrics>()
    
    data class TransitionMetrics(
        val transitionType: TransitionType,
        val duration: Long,
        val frameDrops: Int = 0,
        val avgFrameTime: Double = 16.67, // 60 FPS target
        val peakMemoryUsage: Long = 0,
        val cpuUsage: Double = 0.0
    )
    
    /**
     * Start monitoring a transition
     */
    fun startMonitoring(transitionId: String, type: TransitionType) {
        performanceMetrics[transitionId] = TransitionMetrics(
            transitionType = type,
            duration = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        )
    }
    
    /**
     * End monitoring and calculate metrics
     */
    fun endMonitoring(transitionId: String): TransitionMetrics? {
        val startMetrics = performanceMetrics[transitionId] ?: return null
        val endTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        
        val finalMetrics = startMetrics.copy(
            duration = endTime - startMetrics.duration
        )
        
        performanceMetrics.remove(transitionId)
        return finalMetrics
    }
    
    /**
     * Get performance recommendations
     */
    fun getPerformanceRecommendations(): List<String> {
        val recommendations = mutableListOf<String>()
        
        // Analyze collected metrics and provide recommendations
        val averageDuration = performanceMetrics.values.map { it.duration }.average()
        
        if (averageDuration > 500) {
            recommendations.add("Consider reducing transition duration for better performance")
        }
        
        return recommendations
    }
}

/**
 * Transition accessibility manager
 */
object TransitionAccessibility {
    
    /**
     * Create accessibility-friendly transition
     */
    fun createAccessibleTransition(
        baseConfiguration: TransitionConfiguration,
        reducedMotion: Boolean,
        highContrast: Boolean,
        announceTransition: Boolean = true
    ): TransitionConfiguration {
        var config = baseConfiguration
        
        if (reducedMotion) {
            config = config.copy(
                timing = config.timing.copy(
                    duration = minOf(config.timing.duration / 3, 150L)
                ),
                type = when (config.type) {
                    TransitionType.CONTAINER_TRANSFORM,
                    TransitionType.SHARED_AXIS,
                    TransitionType.HERO -> TransitionType.FADE
                    else -> config.type
                }
            )
        }
        
        if (highContrast) {
            // Modify shared elements to ensure high contrast during transition
            val accessibleElements = config.sharedElements.map { element ->
                element.copy(
                    properties = element.properties.copy(
                        borderWidth = maxOf(element.properties.borderWidth, 2f)
                    )
                )
            }
            config = config.copy(sharedElements = accessibleElements)
        }
        
        return config
    }
    
    /**
     * Generate accessibility announcement for transition
     */
    fun generateTransitionAnnouncement(
        sourceScreen: String,
        targetScreen: String,
        transitionType: TransitionType
    ): String {
        return when (transitionType) {
            TransitionType.LIST_DETAIL -> "Opening detailed view for $targetScreen"
            TransitionType.SHARED_AXIS -> "Navigating to $targetScreen"
            TransitionType.MODAL -> "Opening $targetScreen dialog"
            TransitionType.BOTTOM_SHEET -> "Opening $targetScreen options"
            else -> "Navigating from $sourceScreen to $targetScreen"
        }
    }
}

/**
 * Utility functions for transition calculations
 */
object TransitionUtils {
    
    /**
     * Calculate optimal transition duration based on distance
     */
    fun calculateOptimalDuration(
        startBounds: ElementBounds,
        endBounds: ElementBounds,
        baselineDistance: Float = 100f,
        baselineDuration: Long = 300L
    ): Long {
        val distance = kotlin.math.sqrt(
            (endBounds.x - startBounds.x).let { it * it } +
            (endBounds.y - startBounds.y).let { it * it }
        )
        
        val scaleFactor = (distance / baselineDistance).coerceIn(0.5f, 2.0f)
        return (baselineDuration * scaleFactor).toLong()
    }
    
    /**
     * Interpolate between element bounds
     */
    fun interpolateBounds(
        start: ElementBounds,
        end: ElementBounds,
        progress: Float
    ): ElementBounds {
        val t = progress.coerceIn(0f, 1f)
        return ElementBounds(
            x = start.x + (end.x - start.x) * t,
            y = start.y + (end.y - start.y) * t,
            width = start.width + (end.width - start.width) * t,
            height = start.height + (end.height - start.height) * t
        )
    }
    
    /**
     * Interpolate between element properties
     */
    fun interpolateProperties(
        start: ElementProperties,
        end: ElementProperties,
        progress: Float
    ): ElementProperties {
        val t = progress.coerceIn(0f, 1f)
        return ElementProperties(
            cornerRadius = start.cornerRadius + (end.cornerRadius - start.cornerRadius) * t,
            elevation = start.elevation + (end.elevation - start.elevation) * t,
            scaleX = start.scaleX + (end.scaleX - start.scaleX) * t,
            scaleY = start.scaleY + (end.scaleY - start.scaleY) * t,
            rotation = start.rotation + (end.rotation - start.rotation) * t,
            alpha = start.alpha + (end.alpha - start.alpha) * t,
            backgroundColor = interpolateColor(start.backgroundColor, end.backgroundColor, t),
            borderWidth = start.borderWidth + (end.borderWidth - start.borderWidth) * t,
            borderColor = interpolateColor(start.borderColor, end.borderColor, t)
        )
    }
    
    /**
     * Interpolate between colors
     */
    private fun interpolateColor(start: ULong?, end: ULong?, progress: Float): ULong? {
        if (start == null || end == null) return start ?: end
        
        val t = progress.coerceIn(0f, 1f)
        
        val startA = ((start shr 24) and 0xFFUL).toFloat()
        val startR = ((start shr 16) and 0xFFUL).toFloat()
        val startG = ((start shr 8) and 0xFFUL).toFloat()
        val startB = (start and 0xFFUL).toFloat()
        
        val endA = ((end shr 24) and 0xFFUL).toFloat()
        val endR = ((end shr 16) and 0xFFUL).toFloat()
        val endG = ((end shr 8) and 0xFFUL).toFloat()
        val endB = (end and 0xFFUL).toFloat()
        
        val a = (startA + (endA - startA) * t).toULong()
        val r = (startR + (endR - startR) * t).toULong()
        val g = (startG + (endG - startG) * t).toULong()
        val b = (startB + (endB - startB) * t).toULong()
        
        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }
}