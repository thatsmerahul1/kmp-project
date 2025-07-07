package com.weather.ui.adaptive

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Adaptive layout management for 2025 UI/UX standards
 * 
 * This module provides:
 * - Foldable device support
 * - Multi-window layout adaptation
 * - Screen size class detection
 * - Orientation change handling
 * - Dynamic layout switching
 */

/**
 * Device form factors
 */
enum class DeviceFormFactor {
    PHONE,           // Standard phone
    FOLDABLE_CLOSED, // Foldable in closed state
    FOLDABLE_OPEN,   // Foldable in open state
    TABLET,          // Tablet
    DESKTOP          // Desktop/large screen
}

/**
 * Screen size classifications following Material Design 3
 */
enum class WindowSizeClass {
    COMPACT,    // Width < 600dp
    MEDIUM,     // Width 600dp - 840dp
    EXPANDED    // Width > 840dp
}

/**
 * Layout orientation
 */
enum class LayoutOrientation {
    PORTRAIT,
    LANDSCAPE
}

/**
 * Hinge information for foldable devices
 */
data class HingeInfo(
    val position: Float,        // Hinge position as percentage (0.0 - 1.0)
    val orientation: HingeOrientation,
    val isOccluding: Boolean,   // Whether hinge blocks content
    val width: Float = 0f       // Hinge width in dp
)

enum class HingeOrientation {
    VERTICAL,   // Hinge runs vertically (book-fold)
    HORIZONTAL  // Hinge runs horizontally (laptop-fold)
}

/**
 * Complete layout configuration
 */
data class LayoutConfiguration(
    val formFactor: DeviceFormFactor,
    val sizeClass: WindowSizeClass,
    val orientation: LayoutOrientation,
    val screenWidthDp: Float,
    val screenHeightDp: Float,
    val hingeInfo: HingeInfo? = null,
    val isMultiWindow: Boolean = false,
    val isDarkMode: Boolean = false
) {
    /**
     * Check if device is in dual-pane mode
     */
    val isDualPane: Boolean
        get() = when (formFactor) {
            DeviceFormFactor.FOLDABLE_OPEN -> true
            DeviceFormFactor.TABLET -> sizeClass == WindowSizeClass.EXPANDED
            DeviceFormFactor.DESKTOP -> true
            else -> false
        }
    
    /**
     * Check if navigation should be rail-style
     */
    val useNavigationRail: Boolean
        get() = sizeClass != WindowSizeClass.COMPACT || isDualPane
    
    /**
     * Check if content should be centered with max width
     */
    val useMaxContentWidth: Boolean
        get() = sizeClass == WindowSizeClass.EXPANDED && !isDualPane
}

/**
 * Main adaptive layout manager
 */
class AdaptiveLayoutManager {
    
    private val _layoutConfiguration = MutableStateFlow(
        LayoutConfiguration(
            formFactor = DeviceFormFactor.PHONE,
            sizeClass = WindowSizeClass.COMPACT,
            orientation = LayoutOrientation.PORTRAIT,
            screenWidthDp = 360f,
            screenHeightDp = 640f
        )
    )
    val layoutConfiguration: StateFlow<LayoutConfiguration> = _layoutConfiguration.asStateFlow()
    
    /**
     * Update layout configuration
     */
    fun updateConfiguration(
        screenWidthDp: Float,
        screenHeightDp: Float,
        formFactor: DeviceFormFactor = detectFormFactor(screenWidthDp, screenHeightDp),
        hingeInfo: HingeInfo? = null,
        isMultiWindow: Boolean = false,
        isDarkMode: Boolean = false
    ) {
        val sizeClass = determineWindowSizeClass(screenWidthDp)
        val orientation = if (screenWidthDp > screenHeightDp) {
            LayoutOrientation.LANDSCAPE
        } else {
            LayoutOrientation.PORTRAIT
        }
        
        val newConfiguration = LayoutConfiguration(
            formFactor = formFactor,
            sizeClass = sizeClass,
            orientation = orientation,
            screenWidthDp = screenWidthDp,
            screenHeightDp = screenHeightDp,
            hingeInfo = hingeInfo,
            isMultiWindow = isMultiWindow,
            isDarkMode = isDarkMode
        )
        
        _layoutConfiguration.value = newConfiguration
    }
    
    /**
     * Detect form factor based on screen dimensions
     */
    private fun detectFormFactor(widthDp: Float, heightDp: Float): DeviceFormFactor {
        val minDimension = minOf(widthDp, heightDp)
        val maxDimension = maxOf(widthDp, heightDp)
        val aspectRatio = maxDimension / minDimension
        
        return when {
            // Very wide aspect ratio suggests foldable open
            aspectRatio > 2.0f && maxDimension > 700 -> DeviceFormFactor.FOLDABLE_OPEN
            // Tablet size
            minDimension >= 600 -> DeviceFormFactor.TABLET
            // Desktop size
            minDimension >= 900 -> DeviceFormFactor.DESKTOP
            // Standard phone
            else -> DeviceFormFactor.PHONE
        }
    }
    
    /**
     * Determine window size class based on width
     */
    private fun determineWindowSizeClass(widthDp: Float): WindowSizeClass {
        return when {
            widthDp < 600 -> WindowSizeClass.COMPACT
            widthDp < 840 -> WindowSizeClass.MEDIUM
            else -> WindowSizeClass.EXPANDED
        }
    }
    
    /**
     * Get recommended column count for grid layouts
     */
    fun getGridColumnCount(): Int {
        val config = _layoutConfiguration.value
        return when (config.sizeClass) {
            WindowSizeClass.COMPACT -> if (config.orientation == LayoutOrientation.LANDSCAPE) 2 else 1
            WindowSizeClass.MEDIUM -> if (config.isDualPane) 4 else 2
            WindowSizeClass.EXPANDED -> if (config.isDualPane) 6 else 3
        }
    }
    
    /**
     * Get recommended content max width
     */
    fun getContentMaxWidth(): Float? {
        val config = _layoutConfiguration.value
        return when {
            config.useMaxContentWidth -> 840f // Max content width for readability
            else -> null // No max width restriction
        }
    }
    
    /**
     * Check if bottom navigation should be used
     */
    fun shouldUseBottomNavigation(): Boolean {
        val config = _layoutConfiguration.value
        return config.sizeClass == WindowSizeClass.COMPACT && !config.isDualPane
    }
    
    /**
     * Get safe content regions for foldable devices
     */
    fun getSafeContentRegions(): List<ContentRegion> {
        val config = _layoutConfiguration.value
        val hingeInfo = config.hingeInfo
        
        if (config.formFactor != DeviceFormFactor.FOLDABLE_OPEN || hingeInfo == null) {
            // Single region for non-foldable devices
            return listOf(
                ContentRegion(
                    left = 0f,
                    top = 0f,
                    right = config.screenWidthDp,
                    bottom = config.screenHeightDp,
                    isPrimary = true
                )
            )
        }
        
        return when (hingeInfo.orientation) {
            HingeOrientation.VERTICAL -> {
                // Vertical hinge - split horizontally
                val hingePositionDp = config.screenWidthDp * hingeInfo.position
                listOf(
                    ContentRegion(
                        left = 0f,
                        top = 0f,
                        right = hingePositionDp - (hingeInfo.width / 2),
                        bottom = config.screenHeightDp,
                        isPrimary = true
                    ),
                    ContentRegion(
                        left = hingePositionDp + (hingeInfo.width / 2),
                        top = 0f,
                        right = config.screenWidthDp,
                        bottom = config.screenHeightDp,
                        isPrimary = false
                    )
                )
            }
            HingeOrientation.HORIZONTAL -> {
                // Horizontal hinge - split vertically
                val hingePositionDp = config.screenHeightDp * hingeInfo.position
                listOf(
                    ContentRegion(
                        left = 0f,
                        top = 0f,
                        right = config.screenWidthDp,
                        bottom = hingePositionDp - (hingeInfo.width / 2),
                        isPrimary = true
                    ),
                    ContentRegion(
                        left = 0f,
                        top = hingePositionDp + (hingeInfo.width / 2),
                        right = config.screenWidthDp,
                        bottom = config.screenHeightDp,
                        isPrimary = false
                    )
                )
            }
        }
    }
}

/**
 * Content region for layout positioning
 */
data class ContentRegion(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val isPrimary: Boolean = false
) {
    val width: Float get() = right - left
    val height: Float get() = bottom - top
    val aspectRatio: Float get() = width / height
}

/**
 * Adaptive layout strategies
 */
object AdaptiveLayoutStrategies {
    
    /**
     * Get layout strategy for weather app
     */
    fun getWeatherLayoutStrategy(config: LayoutConfiguration): WeatherLayoutStrategy {
        return when {
            config.isDualPane -> WeatherLayoutStrategy.DUAL_PANE
            config.sizeClass == WindowSizeClass.EXPANDED -> WeatherLayoutStrategy.MASTER_DETAIL
            config.orientation == LayoutOrientation.LANDSCAPE -> WeatherLayoutStrategy.LANDSCAPE_OPTIMIZED
            else -> WeatherLayoutStrategy.SINGLE_PANE
        }
    }
}

enum class WeatherLayoutStrategy {
    SINGLE_PANE,         // Single column, navigation bottom
    DUAL_PANE,           // Two columns side by side
    MASTER_DETAIL,       // List + detail with navigation rail
    LANDSCAPE_OPTIMIZED  // Horizontal layout optimized
}

/**
 * Extension functions for layout calculations
 */

/**
 * Calculate optimal text size based on screen size
 */
fun WindowSizeClass.getOptimalTextScale(): Float {
    return when (this) {
        WindowSizeClass.COMPACT -> 1.0f
        WindowSizeClass.MEDIUM -> 1.1f
        WindowSizeClass.EXPANDED -> 1.2f
    }
}

/**
 * Get spacing multiplier for different screen sizes
 */
fun WindowSizeClass.getSpacingMultiplier(): Float {
    return when (this) {
        WindowSizeClass.COMPACT -> 1.0f
        WindowSizeClass.MEDIUM -> 1.2f
        WindowSizeClass.EXPANDED -> 1.4f
    }
}

/**
 * Calculate optimal touch target size
 */
fun WindowSizeClass.getTouchTargetSize(): Float {
    return when (this) {
        WindowSizeClass.COMPACT -> 48f // Material minimum
        WindowSizeClass.MEDIUM -> 52f
        WindowSizeClass.EXPANDED -> 56f
    }
}