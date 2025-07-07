package com.weather.ui.theming

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlin.math.pow

/**
 * Dynamic color theming system for 2025 Material You standards
 * 
 * This module provides:
 * - Material You dynamic color extraction
 * - Adaptive color schemes
 * - High contrast support
 * - Accessibility-compliant colors
 * - Custom color palette generation
 */

/**
 * Color roles in Material You design system
 */
@Serializable
data class ColorScheme(
    // Primary colors
    val primary: ULong,
    val onPrimary: ULong,
    val primaryContainer: ULong,
    val onPrimaryContainer: ULong,
    
    // Secondary colors
    val secondary: ULong,
    val onSecondary: ULong,
    val secondaryContainer: ULong,
    val onSecondaryContainer: ULong,
    
    // Tertiary colors
    val tertiary: ULong,
    val onTertiary: ULong,
    val tertiaryContainer: ULong,
    val onTertiaryContainer: ULong,
    
    // Error colors
    val error: ULong,
    val onError: ULong,
    val errorContainer: ULong,
    val onErrorContainer: ULong,
    
    // Surface colors
    val surface: ULong,
    val onSurface: ULong,
    val surfaceVariant: ULong,
    val onSurfaceVariant: ULong,
    val surfaceTint: ULong,
    
    // Background colors
    val background: ULong,
    val onBackground: ULong,
    
    // Outline colors
    val outline: ULong,
    val outlineVariant: ULong,
    
    // Inverse colors
    val inverseSurface: ULong,
    val inverseOnSurface: ULong,
    val inversePrimary: ULong,
    
    // Shadow and scrim
    val shadow: ULong,
    val scrim: ULong
) {
    companion object {
        // Default light color scheme
        val defaultLight = ColorScheme(
            primary = 0xFF6750A4UL,
            onPrimary = 0xFFFFFFFFUL,
            primaryContainer = 0xFFEADDFFUL,
            onPrimaryContainer = 0xFF21005DUL,
            
            secondary = 0xFF625B71UL,
            onSecondary = 0xFFFFFFFFUL,
            secondaryContainer = 0xFFE8DEF8UL,
            onSecondaryContainer = 0xFF1D192BUL,
            
            tertiary = 0xFF7D5260UL,
            onTertiary = 0xFFFFFFFFUL,
            tertiaryContainer = 0xFFFFD8E4UL,
            onTertiaryContainer = 0xFF31111DUL,
            
            error = 0xFFBA1A1AUL,
            onError = 0xFFFFFFFFUL,
            errorContainer = 0xFFFFDADAUL,
            onErrorContainer = 0xFF410002UL,
            
            surface = 0xFFFFFBFFUL,
            onSurface = 0xFF1C1B1FUL,
            surfaceVariant = 0xFFE7E0ECUL,
            onSurfaceVariant = 0xFF49454FUL,
            surfaceTint = 0xFF6750A4UL,
            
            background = 0xFFFFFBFFUL,
            onBackground = 0xFF1C1B1FUL,
            
            outline = 0xFF79747EUL,
            outlineVariant = 0xFFCAC4D0UL,
            
            inverseSurface = 0xFF313033UL,
            inverseOnSurface = 0xFFF4EFF4UL,
            inversePrimary = 0xFFD0BCFFUL,
            
            shadow = 0xFF000000UL,
            scrim = 0xFF000000UL
        )
        
        // Default dark color scheme
        val defaultDark = ColorScheme(
            primary = 0xFFD0BCFFUL,
            onPrimary = 0xFF381E72UL,
            primaryContainer = 0xFF4F378BUL,
            onPrimaryContainer = 0xFFEADDFFUL,
            
            secondary = 0xFFCCC2DCUL,
            onSecondary = 0xFF332D41UL,
            secondaryContainer = 0xFF4A4458UL,
            onSecondaryContainer = 0xFFE8DEF8UL,
            
            tertiary = 0xFFEFB8C8UL,
            onTertiary = 0xFF492532UL,
            tertiaryContainer = 0xFF633B48UL,
            onTertiaryContainer = 0xFFFFD8E4UL,
            
            error = 0xFFFFB4ABUL,
            onError = 0xFF690005UL,
            errorContainer = 0xFF93000AUL,
            onErrorContainer = 0xFFFFDADAUL,
            
            surface = 0xFF1C1B1FUL,
            onSurface = 0xFFE6E1E5UL,
            surfaceVariant = 0xFF49454FUL,
            onSurfaceVariant = 0xFFCAC4D0UL,
            surfaceTint = 0xFFD0BCFFUL,
            
            background = 0xFF1C1B1FUL,
            onBackground = 0xFFE6E1E5UL,
            
            outline = 0xFF938F99UL,
            outlineVariant = 0xFF49454FUL,
            
            inverseSurface = 0xFFE6E1E5UL,
            inverseOnSurface = 0xFF313033UL,
            inversePrimary = 0xFF6750A4UL,
            
            shadow = 0xFF000000UL,
            scrim = 0xFF000000UL
        )
    }
}

/**
 * Theme mode options
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM  // Follow system theme
}

/**
 * Dynamic color source
 */
enum class DynamicColorSource {
    WALLPAPER,      // Extract from system wallpaper
    USER_IMAGE,     // Extract from user-provided image
    CUSTOM_COLOR,   // User-selected base color
    WEATHER_BASED   // Color based on weather conditions
}

/**
 * Accessibility settings for colors
 */
@Serializable
data class AccessibilityColorSettings(
    val highContrast: Boolean = false,
    val increasedContrast: Boolean = false,
    val reducedTransparency: Boolean = false,
    val protanopia: Boolean = false,        // Red-green colorblind
    val deuteranopia: Boolean = false,      // Red-green colorblind
    val tritanopia: Boolean = false         // Blue-yellow colorblind
)

/**
 * Complete theme configuration
 */
@Serializable
data class ThemeConfiguration(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val dynamicColorSource: DynamicColorSource = DynamicColorSource.WALLPAPER,
    val customBaseColor: ULong? = null,
    val accessibilitySettings: AccessibilityColorSettings = AccessibilityColorSettings(),
    val lightColorScheme: ColorScheme = ColorScheme.defaultLight,
    val darkColorScheme: ColorScheme = ColorScheme.defaultDark
)

/**
 * Weather-based color mapping
 */
object WeatherColorMapping {
    
    fun getBaseColorForWeather(condition: String, temperature: Double): ULong {
        return when {
            condition.contains("rain", ignoreCase = true) -> 0xFF1976D2UL // Blue
            condition.contains("snow", ignoreCase = true) -> 0xFFECEFF1UL // Light blue-grey
            condition.contains("cloud", ignoreCase = true) -> 0xFF607D8BUL // Blue-grey
            condition.contains("sun", ignoreCase = true) || condition.contains("clear", ignoreCase = true) -> {
                when {
                    temperature > 25 -> 0xFFFF9800UL // Orange (hot)
                    temperature > 15 -> 0xFFFFC107UL // Amber (warm)
                    else -> 0xFF2196F3UL // Blue (cool)
                }
            }
            condition.contains("storm", ignoreCase = true) -> 0xFF424242UL // Dark grey
            condition.contains("fog", ignoreCase = true) -> 0xFF9E9E9EUL // Grey
            else -> 0xFF6750A4UL // Default primary
        }
    }
}

/**
 * Advanced color utilities for dynamic theming
 */
object ColorUtils {
    
    /**
     * Convert color to HSL
     */
    fun colorToHsl(color: ULong): Triple<Float, Float, Float> {
        val r = ((color shr 16) and 0xFFUL).toFloat() / 255f
        val g = ((color shr 8) and 0xFFUL).toFloat() / 255f
        val b = (color and 0xFFUL).toFloat() / 255f
        
        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)
        val diff = max - min
        
        val lightness = (max + min) / 2f
        
        val saturation = if (diff == 0f) {
            0f
        } else {
            diff / (1f - kotlin.math.abs(2f * lightness - 1f))
        }
        
        val hue = when {
            diff == 0f -> 0f
            max == r -> 60f * (((g - b) / diff) % 6f)
            max == g -> 60f * (((b - r) / diff) + 2f)
            else -> 60f * (((r - g) / diff) + 4f)
        }
        
        return Triple(hue, saturation, lightness)
    }
    
    /**
     * Convert HSL to color
     */
    fun hslToColor(h: Float, s: Float, l: Float): ULong {
        val c = (1f - kotlin.math.abs(2f * l - 1f)) * s
        val x = c * (1f - kotlin.math.abs((h / 60f) % 2f - 1f))
        val m = l - c / 2f
        
        val (rPrime, gPrime, bPrime) = when {
            h < 60f -> Triple(c, x, 0f)
            h < 120f -> Triple(x, c, 0f)
            h < 180f -> Triple(0f, c, x)
            h < 240f -> Triple(0f, x, c)
            h < 300f -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }
        
        val r = ((rPrime + m) * 255f).toInt().coerceIn(0, 255)
        val g = ((gPrime + m) * 255f).toInt().coerceIn(0, 255)
        val b = ((bPrime + m) * 255f).toInt().coerceIn(0, 255)
        
        return (0xFFUL shl 24) or (r.toULong() shl 16) or (g.toULong() shl 8) or b.toULong()
    }
    
    /**
     * Calculate contrast ratio between two colors
     */
    fun contrastRatio(color1: ULong, color2: ULong): Float {
        val luminance1 = relativeLuminance(color1)
        val luminance2 = relativeLuminance(color2)
        
        val lighter = maxOf(luminance1, luminance2)
        val darker = minOf(luminance1, luminance2)
        
        return (lighter + 0.05f) / (darker + 0.05f)
    }
    
    /**
     * Calculate relative luminance
     */
    private fun relativeLuminance(color: ULong): Float {
        val r = ((color shr 16) and 0xFFUL).toFloat() / 255f
        val g = ((color shr 8) and 0xFFUL).toFloat() / 255f
        val b = (color and 0xFFUL).toFloat() / 255f
        
        fun linearize(value: Float): Float {
            return if (value <= 0.03928f) {
                value / 12.92f
            } else {
                ((value + 0.055f) / 1.055f).pow(2.4f)
            }
        }
        
        return 0.2126f * linearize(r) + 0.7152f * linearize(g) + 0.0722f * linearize(b)
    }
    
    /**
     * Adjust color for accessibility
     */
    fun adjustForAccessibility(
        color: ULong,
        backgroundColor: ULong,
        settings: AccessibilityColorSettings
    ): ULong {
        var adjustedColor = color
        
        // High contrast adjustment
        if (settings.highContrast || settings.increasedContrast) {
            val currentContrast = contrastRatio(color, backgroundColor)
            val targetContrast = if (settings.highContrast) 7.0f else 4.5f
            
            if (currentContrast < targetContrast) {
                adjustedColor = enhanceContrast(color, backgroundColor, targetContrast)
            }
        }
        
        // Color blindness adjustments
        if (settings.protanopia || settings.deuteranopia || settings.tritanopia) {
            adjustedColor = adjustForColorBlindness(adjustedColor, settings)
        }
        
        return adjustedColor
    }
    
    /**
     * Enhance contrast between colors
     */
    private fun enhanceContrast(color: ULong, backgroundColor: ULong, targetContrast: Float): ULong {
        val (h, s, l) = colorToHsl(color)
        val backgroundLuminance = relativeLuminance(backgroundColor)
        
        // Determine if we should make the color lighter or darker
        val shouldBeLighter = backgroundLuminance < 0.5f
        
        var newLightness = l
        var iterations = 0
        val maxIterations = 50
        
        while (iterations < maxIterations) {
            val testColor = hslToColor(h, s, newLightness)
            val currentContrast = contrastRatio(testColor, backgroundColor)
            
            if (currentContrast >= targetContrast) {
                return testColor
            }
            
            newLightness = if (shouldBeLighter) {
                (newLightness + 0.05f).coerceAtMost(1.0f)
            } else {
                (newLightness - 0.05f).coerceAtLeast(0.0f)
            }
            
            iterations++
        }
        
        return color // Return original if we can't achieve target contrast
    }
    
    /**
     * Adjust colors for color blindness
     */
    private fun adjustForColorBlindness(color: ULong, settings: AccessibilityColorSettings): ULong {
        // Simplified color blindness adjustment
        // In a real implementation, this would use more sophisticated algorithms
        val (h, s, l) = colorToHsl(color)
        
        val adjustedHue = when {
            settings.protanopia -> {
                // Shift reds towards oranges/yellows
                if (h in 330f..360f || h in 0f..30f) h + 30f else h
            }
            settings.deuteranopia -> {
                // Shift greens towards blues/yellows
                if (h in 90f..150f) h + 30f else h
            }
            settings.tritanopia -> {
                // Shift blues towards greens
                if (h in 210f..270f) h - 30f else h
            }
            else -> h
        }
        
        return hslToColor(adjustedHue.rem(360f), s, l)
    }
}

/**
 * Dynamic color scheme generator
 */
class DynamicColorSchemeGenerator {
    
    /**
     * Generate color scheme from base color
     */
    fun generateFromBaseColor(
        baseColor: ULong,
        isDark: Boolean = false,
        accessibilitySettings: AccessibilityColorSettings = AccessibilityColorSettings()
    ): ColorScheme {
        val (hue, saturation, _) = ColorUtils.colorToHsl(baseColor)
        
        // Generate tonal palette
        val primary = generateTonalColor(hue, saturation, if (isDark) 0.8f else 0.4f)
        val primaryContainer = generateTonalColor(hue, saturation, if (isDark) 0.2f else 0.9f)
        val secondary = generateTonalColor((hue + 60f) % 360f, saturation * 0.7f, if (isDark) 0.7f else 0.5f)
        val tertiary = generateTonalColor((hue + 120f) % 360f, saturation * 0.8f, if (isDark) 0.6f else 0.6f)
        
        // Generate neutral colors
        val surface = if (isDark) 0xFF1C1B1FUL else 0xFFFFFBFFUL
        val background = if (isDark) 0xFF1C1B1FUL else 0xFFFFFBFFUL
        
        val scheme = ColorScheme(
            primary = primary,
            onPrimary = if (isDark) 0xFF000000UL else 0xFFFFFFFFUL,
            primaryContainer = primaryContainer,
            onPrimaryContainer = if (isDark) 0xFFFFFFFFUL else 0xFF000000UL,
            
            secondary = secondary,
            onSecondary = if (isDark) 0xFF000000UL else 0xFFFFFFFFUL,
            secondaryContainer = generateTonalColor((hue + 60f) % 360f, saturation * 0.5f, if (isDark) 0.15f else 0.95f),
            onSecondaryContainer = if (isDark) 0xFFFFFFFFUL else 0xFF000000UL,
            
            tertiary = tertiary,
            onTertiary = if (isDark) 0xFF000000UL else 0xFFFFFFFFUL,
            tertiaryContainer = generateTonalColor((hue + 120f) % 360f, saturation * 0.6f, if (isDark) 0.2f else 0.9f),
            onTertiaryContainer = if (isDark) 0xFFFFFFFFUL else 0xFF000000UL,
            
            error = if (isDark) 0xFFFFB4ABUL else 0xFFBA1A1AUL,
            onError = if (isDark) 0xFF690005UL else 0xFFFFFFFFUL,
            errorContainer = if (isDark) 0xFF93000AUL else 0xFFFFDADAUL,
            onErrorContainer = if (isDark) 0xFFFFDADAUL else 0xFF410002UL,
            
            surface = surface,
            onSurface = if (isDark) 0xFFE6E1E5UL else 0xFF1C1B1FUL,
            surfaceVariant = if (isDark) 0xFF49454FUL else 0xFFE7E0ECUL,
            onSurfaceVariant = if (isDark) 0xFFCAC4D0UL else 0xFF49454FUL,
            surfaceTint = primary,
            
            background = background,
            onBackground = if (isDark) 0xFFE6E1E5UL else 0xFF1C1B1FUL,
            
            outline = if (isDark) 0xFF938F99UL else 0xFF79747EUL,
            outlineVariant = if (isDark) 0xFF49454FUL else 0xFFCAC4D0UL,
            
            inverseSurface = if (isDark) 0xFFE6E1E5UL else 0xFF313033UL,
            inverseOnSurface = if (isDark) 0xFF313033UL else 0xFFF4EFF4UL,
            inversePrimary = if (isDark) primary else generateTonalColor(hue, saturation, 0.8f),
            
            shadow = 0xFF000000UL,
            scrim = 0xFF000000UL
        )
        
        // Apply accessibility adjustments
        return applyAccessibilityAdjustments(scheme, accessibilitySettings)
    }
    
    /**
     * Generate tonal color variation
     */
    private fun generateTonalColor(hue: Float, saturation: Float, lightness: Float): ULong {
        return ColorUtils.hslToColor(hue, saturation, lightness)
    }
    
    /**
     * Apply accessibility adjustments to entire color scheme
     */
    private fun applyAccessibilityAdjustments(
        scheme: ColorScheme,
        settings: AccessibilityColorSettings
    ): ColorScheme {
        if (!settings.highContrast && !settings.increasedContrast && 
            !settings.protanopia && !settings.deuteranopia && !settings.tritanopia) {
            return scheme
        }
        
        return scheme.copy(
            primary = ColorUtils.adjustForAccessibility(scheme.primary, scheme.surface, settings),
            secondary = ColorUtils.adjustForAccessibility(scheme.secondary, scheme.surface, settings),
            tertiary = ColorUtils.adjustForAccessibility(scheme.tertiary, scheme.surface, settings),
            error = ColorUtils.adjustForAccessibility(scheme.error, scheme.surface, settings)
        )
    }
}

/**
 * Main theme manager
 */
class DynamicThemeManager {
    
    private val colorSchemeGenerator = DynamicColorSchemeGenerator()
    
    private val _themeConfiguration = MutableStateFlow(ThemeConfiguration())
    val themeConfiguration: StateFlow<ThemeConfiguration> = _themeConfiguration.asStateFlow()
    
    private val _currentColorScheme = MutableStateFlow(ColorScheme.defaultLight)
    val currentColorScheme: StateFlow<ColorScheme> = _currentColorScheme.asStateFlow()
    
    /**
     * Update theme configuration
     */
    fun updateThemeConfiguration(config: ThemeConfiguration) {
        _themeConfiguration.value = config
        updateColorScheme()
    }
    
    /**
     * Set theme mode
     */
    fun setThemeMode(mode: ThemeMode) {
        val current = _themeConfiguration.value
        _themeConfiguration.value = current.copy(themeMode = mode)
        updateColorScheme()
    }
    
    /**
     * Enable/disable dynamic colors
     */
    fun setDynamicColorEnabled(enabled: Boolean) {
        val current = _themeConfiguration.value
        _themeConfiguration.value = current.copy(dynamicColor = enabled)
        updateColorScheme()
    }
    
    /**
     * Set custom base color
     */
    fun setCustomBaseColor(color: ULong) {
        val current = _themeConfiguration.value
        _themeConfiguration.value = current.copy(
            customBaseColor = color,
            dynamicColorSource = DynamicColorSource.CUSTOM_COLOR
        )
        updateColorScheme()
    }
    
    /**
     * Update colors based on weather
     */
    fun updateWeatherBasedColors(condition: String, temperature: Double) {
        if (_themeConfiguration.value.dynamicColorSource == DynamicColorSource.WEATHER_BASED) {
            val baseColor = WeatherColorMapping.getBaseColorForWeather(condition, temperature)
            setCustomBaseColor(baseColor)
        }
    }
    
    /**
     * Update accessibility settings
     */
    fun updateAccessibilitySettings(settings: AccessibilityColorSettings) {
        val current = _themeConfiguration.value
        _themeConfiguration.value = current.copy(accessibilitySettings = settings)
        updateColorScheme()
    }
    
    /**
     * Update current color scheme based on configuration
     */
    private fun updateColorScheme() {
        val config = _themeConfiguration.value
        val isDarkMode = shouldUseDarkMode(config.themeMode)
        
        val scheme = if (config.dynamicColor && config.customBaseColor != null) {
            colorSchemeGenerator.generateFromBaseColor(
                baseColor = config.customBaseColor!!,
                isDark = isDarkMode,
                accessibilitySettings = config.accessibilitySettings
            )
        } else {
            if (isDarkMode) config.darkColorScheme else config.lightColorScheme
        }
        
        _currentColorScheme.value = scheme
    }
    
    /**
     * Determine if dark mode should be used
     */
    private fun shouldUseDarkMode(themeMode: ThemeMode): Boolean {
        return when (themeMode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isSystemDarkMode() // Platform-specific implementation needed
        }
    }
    
    /**
     * Check if system is in dark mode (platform-specific)
     */
    private fun isSystemDarkMode(): Boolean {
        // This would need platform-specific implementation
        // For now, return false as default
        return false
    }
    
    /**
     * Generate color scheme from image (future implementation)
     */
    fun generateSchemeFromImage(imageData: ByteArray): ColorScheme {
        // Future implementation would analyze image and extract dominant colors
        // For now, return default scheme
        return ColorScheme.defaultLight
    }
}