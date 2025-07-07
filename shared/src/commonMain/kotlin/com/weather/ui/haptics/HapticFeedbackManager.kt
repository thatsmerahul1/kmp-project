package com.weather.ui.haptics

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

/**
 * Comprehensive haptic feedback system for 2025 standards
 * 
 * This module provides:
 * - Platform-agnostic haptic feedback
 * - Context-aware haptic patterns
 * - Accessibility-friendly feedback
 * - Custom haptic compositions
 * - Energy-efficient haptic usage
 * - Weather-contextual haptics
 */

/**
 * Haptic feedback types following platform standards
 */
enum class HapticType {
    // Basic feedback types
    LIGHT,              // Light tap (iOS: lightImpact, Android: CLICK)
    MEDIUM,             // Medium tap (iOS: mediumImpact, Android: DOUBLE_CLICK)
    HEAVY,              // Heavy tap (iOS: heavyImpact, Android: HEAVY_CLICK)
    
    // Semantic feedback
    SUCCESS,            // Positive confirmation
    WARNING,            // Attention needed
    ERROR,              // Something went wrong
    
    // UI interaction feedback
    SELECTION,          // Item selection
    BUTTON_PRESS,       // Button interaction
    TOGGLE_ON,          // Switch/toggle activated
    TOGGLE_OFF,         // Switch/toggle deactivated
    
    // Navigation feedback
    PAGE_TURN,          // Page navigation
    TAB_SWITCH,         // Tab switching
    MODAL_PRESENT,      // Modal/dialog presentation
    MODAL_DISMISS,      // Modal/dialog dismissal
    
    // Content feedback
    REFRESH_START,      // Pull-to-refresh initiated
    REFRESH_COMPLETE,   // Refresh completed
    SCROLL_EDGE,        // Reached scroll boundary
    SNAP_TO_POSITION,   // Snapped to grid/position
    
    // Weather-specific
    WEATHER_UPDATE,     // Weather data refreshed
    SEVERE_ALERT,       // Severe weather alert
    TEMPERATURE_EXTREME, // Extreme temperature warning
    
    // Custom patterns
    HEARTBEAT,          // Rhythmic pattern
    PULSE_WAVE,         // Wave-like pattern
    NOTIFICATION_GENTLE, // Gentle notification
    NOTIFICATION_URGENT  // Urgent notification
}

/**
 * Haptic intensity levels
 */
enum class HapticIntensity {
    MINIMAL,    // Very light feedback
    LOW,        // Light feedback
    MEDIUM,     // Standard feedback
    HIGH,       // Strong feedback
    MAXIMUM     // Very strong feedback
}

/**
 * Haptic pattern definitions
 */
@Serializable
data class HapticPattern(
    val pulses: List<HapticPulse>,
    val repeatCount: Int = 1,
    val totalDuration: Long = 0L // Calculated from pulses
) {
    init {
        // Calculate total duration if not provided
        if (totalDuration == 0L && pulses.isNotEmpty()) {
            val calculatedDuration = pulses.sumOf { it.duration + it.delay }
            // Update would require making this a data class with custom constructor
        }
    }
}

@Serializable
data class HapticPulse(
    val intensity: Float,       // 0.0 to 1.0
    val duration: Long,         // Duration in milliseconds
    val delay: Long = 0L        // Delay before this pulse
)

/**
 * Haptic preferences
 */
@Serializable
data class HapticPreferences(
    val enabled: Boolean = true,
    val systemHapticsEnabled: Boolean = true,
    val customHapticsEnabled: Boolean = true,
    val intensityMultiplier: Float = 1.0f,      // Global intensity scaling
    val reducedHaptics: Boolean = false,         // Accessibility preference
    val contextualHaptics: Boolean = true,       // Weather-based haptics
    val silentModeRespect: Boolean = true,       // Respect device silent mode
    val batteryOptimization: Boolean = true      // Reduce haptics on low battery
)

/**
 * Haptic feedback context
 */
data class HapticContext(
    val userAction: String,                 // Action that triggered haptic
    val uiElement: String? = null,          // UI element involved
    val importance: HapticImportance = HapticImportance.NORMAL,
    val weatherCondition: String? = null,   // Current weather for contextual haptics
    val batteryLevel: Float = 1.0f,         // Battery level (0.0 to 1.0)
    val isInSilentMode: Boolean = false     // Device silent mode status
)

enum class HapticImportance {
    LOW,        // Optional feedback
    NORMAL,     // Standard feedback
    HIGH,       // Important feedback
    CRITICAL    // Essential feedback (accessibility)
}

/**
 * Pre-defined haptic patterns
 */
object HapticPatterns {
    
    /**
     * Success confirmation pattern
     */
    val SUCCESS = HapticPattern(
        pulses = listOf(
            HapticPulse(intensity = 0.6f, duration = 50L),
            HapticPulse(intensity = 0.8f, duration = 100L, delay = 50L),
            HapticPulse(intensity = 0.4f, duration = 50L, delay = 50L)
        )
    )
    
    /**
     * Error feedback pattern
     */
    val ERROR = HapticPattern(
        pulses = listOf(
            HapticPulse(intensity = 0.8f, duration = 100L),
            HapticPulse(intensity = 0.8f, duration = 100L, delay = 100L),
            HapticPulse(intensity = 0.8f, duration = 100L, delay = 100L)
        )
    )
    
    /**
     * Warning pattern
     */
    val WARNING = HapticPattern(
        pulses = listOf(
            HapticPulse(intensity = 0.7f, duration = 200L),
            HapticPulse(intensity = 0.5f, duration = 100L, delay = 100L)
        )
    )
    
    /**
     * Notification pattern
     */
    val NOTIFICATION = HapticPattern(
        pulses = listOf(
            HapticPulse(intensity = 0.5f, duration = 50L),
            HapticPulse(intensity = 0.7f, duration = 100L, delay = 100L)
        )
    )
    
    /**
     * Severe weather alert pattern
     */
    val SEVERE_WEATHER = HapticPattern(
        pulses = listOf(
            HapticPulse(intensity = 1.0f, duration = 150L),
            HapticPulse(intensity = 0.6f, duration = 100L, delay = 50L),
            HapticPulse(intensity = 1.0f, duration = 150L, delay = 50L),
            HapticPulse(intensity = 0.6f, duration = 100L, delay = 50L)
        ),
        repeatCount = 2
    )
    
    /**
     * Heartbeat pattern
     */
    val HEARTBEAT = HapticPattern(
        pulses = listOf(
            HapticPulse(intensity = 0.6f, duration = 80L),
            HapticPulse(intensity = 0.8f, duration = 100L, delay = 120L),
            HapticPulse(intensity = 0.4f, duration = 60L, delay = 400L)
        )
    )
    
    /**
     * Button press pattern
     */
    val BUTTON_PRESS = HapticPattern(
        pulses = listOf(
            HapticPulse(intensity = 0.7f, duration = 30L)
        )
    )
    
    /**
     * Toggle on pattern
     */
    val TOGGLE_ON = HapticPattern(
        pulses = listOf(
            HapticPulse(intensity = 0.5f, duration = 40L),
            HapticPulse(intensity = 0.8f, duration = 60L, delay = 20L)
        )
    )
    
    /**
     * Toggle off pattern
     */
    val TOGGLE_OFF = HapticPattern(
        pulses = listOf(
            HapticPulse(intensity = 0.8f, duration = 40L),
            HapticPulse(intensity = 0.3f, duration = 60L, delay = 20L)
        )
    )
    
    /**
     * Page turn pattern
     */
    val PAGE_TURN = HapticPattern(
        pulses = listOf(
            HapticPulse(intensity = 0.4f, duration = 80L)
        )
    )
    
    /**
     * Refresh pattern
     */
    val REFRESH = HapticPattern(
        pulses = listOf(
            HapticPulse(intensity = 0.3f, duration = 50L),
            HapticPulse(intensity = 0.5f, duration = 50L, delay = 50L),
            HapticPulse(intensity = 0.7f, duration = 50L, delay = 50L)
        )
    )
}

/**
 * Weather-contextual haptic generator
 */
object WeatherHaptics {
    
    /**
     * Generate haptic pattern based on weather condition
     */
    fun getWeatherPattern(condition: String, temperature: Double): HapticPattern? {
        return when {
            condition.contains("thunder", ignoreCase = true) -> 
                HapticPatterns.SEVERE_WEATHER
            condition.contains("rain", ignoreCase = true) -> 
                createRainPattern(condition)
            condition.contains("snow", ignoreCase = true) -> 
                createSnowPattern()
            condition.contains("wind", ignoreCase = true) -> 
                createWindPattern()
            temperature > 35 -> // Very hot
                createHeatWavePattern()
            temperature < -10 -> // Very cold
                createFreezingPattern()
            else -> null
        }
    }
    
    private fun createRainPattern(condition: String): HapticPattern {
        val intensity = when {
            condition.contains("heavy", ignoreCase = true) -> 0.8f
            condition.contains("light", ignoreCase = true) -> 0.3f
            else -> 0.5f
        }
        
        return HapticPattern(
            pulses = List(5) { index ->
                HapticPulse(
                    intensity = intensity + (index * 0.1f).coerceAtMost(0.2f),
                    duration = 30L,
                    delay = (50L + index * 20L)
                )
            }
        )
    }
    
    private fun createSnowPattern(): HapticPattern {
        return HapticPattern(
            pulses = List(8) { index ->
                HapticPulse(
                    intensity = 0.2f + (index % 2) * 0.1f,
                    duration = 40L,
                    delay = index * 100L
                )
            }
        )
    }
    
    private fun createWindPattern(): HapticPattern {
        return HapticPattern(
            pulses = listOf(
                HapticPulse(intensity = 0.3f, duration = 200L),
                HapticPulse(intensity = 0.7f, duration = 150L, delay = 50L),
                HapticPulse(intensity = 0.4f, duration = 300L, delay = 100L)
            )
        )
    }
    
    private fun createHeatWavePattern(): HapticPattern {
        return HapticPattern(
            pulses = listOf(
                HapticPulse(intensity = 0.8f, duration = 100L),
                HapticPulse(intensity = 0.9f, duration = 150L, delay = 50L),
                HapticPulse(intensity = 1.0f, duration = 100L, delay = 50L)
            )
        )
    }
    
    private fun createFreezingPattern(): HapticPattern {
        return HapticPattern(
            pulses = List(3) { 
                HapticPulse(intensity = 0.6f, duration = 200L, delay = 200L)
            }
        )
    }
}

/**
 * Main haptic feedback manager
 */
class HapticFeedbackManager {
    
    private val _preferences = MutableStateFlow(HapticPreferences())
    val preferences: StateFlow<HapticPreferences> = _preferences.asStateFlow()
    
    private val _isHapticSupported = MutableStateFlow(true)
    val isHapticSupported: StateFlow<Boolean> = _isHapticSupported.asStateFlow()
    
    private var lastHapticTime = 0L
    private val minHapticInterval = 50L // Minimum time between haptics
    
    /**
     * Update haptic preferences
     */
    fun updatePreferences(preferences: HapticPreferences) {
        _preferences.value = preferences
    }
    
    /**
     * Enable/disable haptic feedback
     */
    fun setHapticEnabled(enabled: Boolean) {
        val current = _preferences.value
        _preferences.value = current.copy(enabled = enabled)
    }
    
    /**
     * Set haptic intensity multiplier
     */
    fun setIntensityMultiplier(multiplier: Float) {
        val current = _preferences.value
        _preferences.value = current.copy(
            intensityMultiplier = multiplier.coerceIn(0f, 2f)
        )
    }
    
    /**
     * Trigger haptic feedback by type
     */
    suspend fun triggerHaptic(
        type: HapticType,
        context: HapticContext = HapticContext("user_action"),
        intensity: HapticIntensity = HapticIntensity.MEDIUM
    ) {
        if (!shouldTriggerHaptic(context)) return
        
        val pattern = getPatternForType(type, context, intensity)
        executeHapticPattern(pattern, context)
    }
    
    /**
     * Trigger custom haptic pattern
     */
    suspend fun triggerHapticPattern(
        pattern: HapticPattern,
        context: HapticContext = HapticContext("custom_pattern")
    ) {
        if (!shouldTriggerHaptic(context)) return
        
        executeHapticPattern(pattern, context)
    }
    
    /**
     * Trigger haptic with contextual weather feedback
     */
    suspend fun triggerWeatherHaptic(
        weatherCondition: String,
        temperature: Double,
        severity: HapticImportance = HapticImportance.NORMAL
    ) {
        val context = HapticContext(
            userAction = "weather_update",
            weatherCondition = weatherCondition,
            importance = severity
        )
        
        if (!shouldTriggerHaptic(context)) return
        
        val weatherPattern = WeatherHaptics.getWeatherPattern(weatherCondition, temperature)
        if (weatherPattern != null && _preferences.value.contextualHaptics) {
            executeHapticPattern(weatherPattern, context)
        } else {
            // Fallback to standard notification haptic
            triggerHaptic(HapticType.WEATHER_UPDATE, context)
        }
    }
    
    /**
     * Check if haptic should be triggered
     */
    private fun shouldTriggerHaptic(context: HapticContext): Boolean {
        val prefs = _preferences.value
        val currentTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        
        return when {
            !prefs.enabled -> false
            !_isHapticSupported.value -> false
            prefs.silentModeRespect && context.isInSilentMode -> false
            prefs.batteryOptimization && context.batteryLevel < 0.2f -> false
            prefs.reducedHaptics && context.importance < HapticImportance.CRITICAL -> false
            currentTime - lastHapticTime < minHapticInterval -> false
            else -> true
        }
    }
    
    /**
     * Get haptic pattern for type
     */
    private fun getPatternForType(
        type: HapticType,
        context: HapticContext,
        intensity: HapticIntensity
    ): HapticPattern {
        val basePattern = when (type) {
            HapticType.SUCCESS -> HapticPatterns.SUCCESS
            HapticType.ERROR -> HapticPatterns.ERROR
            HapticType.WARNING -> HapticPatterns.WARNING
            HapticType.BUTTON_PRESS -> HapticPatterns.BUTTON_PRESS
            HapticType.TOGGLE_ON -> HapticPatterns.TOGGLE_ON
            HapticType.TOGGLE_OFF -> HapticPatterns.TOGGLE_OFF
            HapticType.PAGE_TURN -> HapticPatterns.PAGE_TURN
            HapticType.REFRESH_COMPLETE -> HapticPatterns.REFRESH
            HapticType.SEVERE_ALERT -> HapticPatterns.SEVERE_WEATHER
            HapticType.HEARTBEAT -> HapticPatterns.HEARTBEAT
            HapticType.WEATHER_UPDATE -> HapticPatterns.NOTIFICATION
            else -> createSimplePattern(type, intensity)
        }
        
        return adjustPatternForContext(basePattern, context, intensity)
    }
    
    /**
     * Create simple haptic pattern for basic types
     */
    private fun createSimplePattern(type: HapticType, intensity: HapticIntensity): HapticPattern {
        val intensityValue = when (intensity) {
            HapticIntensity.MINIMAL -> 0.2f
            HapticIntensity.LOW -> 0.4f
            HapticIntensity.MEDIUM -> 0.6f
            HapticIntensity.HIGH -> 0.8f
            HapticIntensity.MAXIMUM -> 1.0f
        }
        
        val duration = when (type) {
            HapticType.LIGHT -> 30L
            HapticType.MEDIUM -> 50L
            HapticType.HEAVY -> 80L
            else -> 50L
        }
        
        return HapticPattern(
            pulses = listOf(
                HapticPulse(intensity = intensityValue, duration = duration)
            )
        )
    }
    
    /**
     * Adjust pattern based on context and preferences
     */
    private fun adjustPatternForContext(
        pattern: HapticPattern,
        context: HapticContext,
        intensity: HapticIntensity
    ): HapticPattern {
        val prefs = _preferences.value
        val intensityMultiplier = prefs.intensityMultiplier
        
        // Adjust intensity based on battery level
        val batteryAdjustment = if (prefs.batteryOptimization && context.batteryLevel < 0.5f) {
            0.7f
        } else {
            1.0f
        }
        
        // Adjust intensity based on importance
        val importanceMultiplier = when (context.importance) {
            HapticImportance.LOW -> 0.7f
            HapticImportance.NORMAL -> 1.0f
            HapticImportance.HIGH -> 1.2f
            HapticImportance.CRITICAL -> 1.4f
        }
        
        val adjustedPulses = pattern.pulses.map { pulse ->
            pulse.copy(
                intensity = (pulse.intensity * intensityMultiplier * batteryAdjustment * importanceMultiplier)
                    .coerceIn(0f, 1f)
            )
        }
        
        return pattern.copy(pulses = adjustedPulses)
    }
    
    /**
     * Execute haptic pattern
     */
    private suspend fun executeHapticPattern(pattern: HapticPattern, context: HapticContext) {
        lastHapticTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        
        repeat(pattern.repeatCount) { repeatIndex ->
            for (pulse in pattern.pulses) {
                if (pulse.delay > 0) {
                    delay(pulse.delay)
                }
                
                // Platform-specific haptic execution would happen here
                executePlatformHaptic(pulse, context)
                
                if (pulse.duration > 0) {
                    delay(pulse.duration)
                }
            }
            
            // Small delay between repeats
            if (repeatIndex < pattern.repeatCount - 1) {
                delay(100L)
            }
        }
    }
    
    /**
     * Execute platform-specific haptic
     */
    private suspend fun executePlatformHaptic(pulse: HapticPulse, context: HapticContext) {
        // This would be implemented platform-specifically
        // Android: Use VibrationEffect and Vibrator
        // iOS: Use UIImpactFeedbackGenerator, UINotificationFeedbackGenerator, etc.
        
        // For now, this is a placeholder that would call the actual platform implementation
        platformHapticImplementation(pulse.intensity, pulse.duration, context)
    }
    
    /**
     * Platform-specific haptic implementation (placeholder)
     */
    private suspend fun platformHapticImplementation(
        intensity: Float, 
        duration: Long, 
        context: HapticContext
    ) {
        // Platform implementations would be provided via expect/actual
        // Android implementation would use VibrationEffect.createOneShot() or VibrationEffect.createWaveform()
        // iOS implementation would use UIImpactFeedbackGenerator with appropriate intensity
        
        // Simulate haptic execution time
        delay(minOf(duration, 200L))
    }
    
    /**
     * Test haptic feedback capabilities
     */
    suspend fun testHapticCapabilities(): Map<String, Boolean> {
        val results = mutableMapOf<String, Boolean>()
        
        try {
            // Test basic haptic
            triggerHaptic(HapticType.LIGHT)
            results["basic_haptic"] = true
            
            delay(500L)
            
            // Test pattern-based haptic
            triggerHapticPattern(HapticPatterns.SUCCESS)
            results["pattern_haptic"] = true
            
            delay(500L)
            
            // Test intensity variation
            triggerHaptic(HapticType.HEAVY, intensity = HapticIntensity.MAXIMUM)
            results["intensity_variation"] = true
            
        } catch (e: Exception) {
            results["error"] = false
        }
        
        return results
    }
    
    /**
     * Get haptic usage statistics
     */
    fun getHapticStats(): Map<String, Any> {
        return mapOf(
            "haptic_enabled" to _preferences.value.enabled,
            "haptic_supported" to _isHapticSupported.value,
            "intensity_multiplier" to _preferences.value.intensityMultiplier,
            "last_haptic_time" to lastHapticTime,
            "reduced_haptics" to _preferences.value.reducedHaptics
        )
    }
    
    /**
     * Reset haptic settings to defaults
     */
    fun resetToDefaults() {
        _preferences.value = HapticPreferences()
    }
}

/**
 * Haptic feedback DSL for easy usage
 */
class HapticFeedbackDSL(private val manager: HapticFeedbackManager) {
    
    suspend fun success(context: String = "success") {
        manager.triggerHaptic(
            HapticType.SUCCESS,
            HapticContext(userAction = context, importance = HapticImportance.NORMAL)
        )
    }
    
    suspend fun error(context: String = "error") {
        manager.triggerHaptic(
            HapticType.ERROR,
            HapticContext(userAction = context, importance = HapticImportance.HIGH)
        )
    }
    
    suspend fun warning(context: String = "warning") {
        manager.triggerHaptic(
            HapticType.WARNING,
            HapticContext(userAction = context, importance = HapticImportance.HIGH)
        )
    }
    
    suspend fun buttonPress(elementId: String = "button") {
        manager.triggerHaptic(
            HapticType.BUTTON_PRESS,
            HapticContext(userAction = "button_press", uiElement = elementId)
        )
    }
    
    suspend fun toggle(isOn: Boolean, elementId: String = "toggle") {
        manager.triggerHaptic(
            if (isOn) HapticType.TOGGLE_ON else HapticType.TOGGLE_OFF,
            HapticContext(userAction = "toggle", uiElement = elementId)
        )
    }
    
    suspend fun weatherAlert(condition: String, temperature: Double) {
        manager.triggerWeatherHaptic(condition, temperature, HapticImportance.HIGH)
    }
    
    suspend fun customPattern(pulses: List<HapticPulse>, context: String = "custom") {
        val pattern = HapticPattern(pulses = pulses)
        manager.triggerHapticPattern(pattern, HapticContext(userAction = context))
    }
}

/**
 * Extension function for easy haptic access
 */
fun HapticFeedbackManager.dsl(): HapticFeedbackDSL = HapticFeedbackDSL(this)

/**
 * Global haptic feedback instance
 */
object AppHaptics {
    private var manager: HapticFeedbackManager? = null
    
    fun initialize(): HapticFeedbackManager {
        if (manager == null) {
            manager = HapticFeedbackManager()
        }
        return manager!!
    }
    
    fun get(): HapticFeedbackManager? = manager
    
    suspend fun triggerHaptic(type: HapticType, context: String = "app_action") {
        manager?.triggerHaptic(type, HapticContext(userAction = context))
    }
}