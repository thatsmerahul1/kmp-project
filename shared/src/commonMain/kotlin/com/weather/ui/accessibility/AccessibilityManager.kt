package com.weather.ui.accessibility

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlin.math.pow

/**
 * Comprehensive accessibility management for 2025 standards
 * 
 * This module provides:
 * - WCAG 2.1 AA/AAA compliance
 * - Screen reader optimization
 * - Voice navigation support
 * - Motor impairment accommodations
 * - Cognitive accessibility features
 * - Multi-language accessibility
 */

/**
 * Accessibility preference categories
 */
@Serializable
data class AccessibilityPreferences(
    // Visual accessibility
    val largeText: Boolean = false,
    val extraLargeText: Boolean = false,
    val highContrast: Boolean = false,
    val reducedMotion: Boolean = false,
    val reducedTransparency: Boolean = false,
    val colorInversion: Boolean = false,
    
    // Auditory accessibility
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val visualIndicators: Boolean = false, // For sound alerts
    
    // Motor accessibility
    val largerTouchTargets: Boolean = false,
    val stickyKeys: Boolean = false,
    val slowKeys: Boolean = false,
    val bounceKeys: Boolean = false,
    val assistiveTouch: Boolean = false,
    
    // Cognitive accessibility
    val simplifiedInterface: Boolean = false,
    val reducedComplexity: Boolean = false,
    val extendedTimeouts: Boolean = false,
    val autoplayDisabled: Boolean = false,
    
    // Screen reader
    val screenReaderEnabled: Boolean = false,
    val verboseDescriptions: Boolean = false,
    val speakPasswords: Boolean = false,
    
    // Focus and navigation
    val focusRingEnabled: Boolean = true,
    val keyboardNavigationOnly: Boolean = false,
    val tabOrderCustomization: Boolean = false
)

/**
 * Content accessibility ratings
 */
enum class AccessibilityRating {
    A,      // WCAG A compliance
    AA,     // WCAG AA compliance
    AAA,    // WCAG AAA compliance
    FAIL    // Does not meet standards
}

/**
 * Accessibility issue types
 */
enum class AccessibilityIssueType {
    CONTRAST_TOO_LOW,
    TOUCH_TARGET_TOO_SMALL,
    MISSING_LABEL,
    MISSING_DESCRIPTION,
    KEYBOARD_TRAP,
    FOCUS_NOT_VISIBLE,
    TEXT_TOO_SMALL,
    NO_ALT_TEXT,
    FLASHING_CONTENT,
    AUDIO_WITHOUT_CONTROLS,
    TIME_LIMIT_TOO_SHORT,
    LANGUAGE_NOT_IDENTIFIED
}

/**
 * Accessibility issue details
 */
@Serializable
data class AccessibilityIssue(
    val type: AccessibilityIssueType,
    val severity: IssueSeverity,
    val description: String,
    val suggestion: String,
    val elementId: String? = null,
    val location: String? = null
)

enum class IssueSeverity {
    CRITICAL,   // Blocks accessibility
    HIGH,       // Major impact
    MEDIUM,     // Moderate impact
    LOW,        // Minor impact
    INFO        // Informational
}

/**
 * Screen reader content types
 */
enum class ScreenReaderContentType {
    HEADING,
    BUTTON,
    LINK,
    IMAGE,
    LIST,
    LIST_ITEM,
    TABLE,
    FORM_FIELD,
    LANDMARK,
    ALERT,
    STATUS,
    LIVE_REGION
}

/**
 * Screen reader announcement
 */
data class ScreenReaderAnnouncement(
    val text: String,
    val priority: AnnouncementPriority = AnnouncementPriority.NORMAL,
    val contentType: ScreenReaderContentType? = null,
    val shouldInterrupt: Boolean = false
)

enum class AnnouncementPriority {
    LOW,      // Polite, wait for user to finish
    NORMAL,   // Assertive, announce when convenient
    HIGH      // Emergency, interrupt immediately
}

/**
 * Accessibility audit result
 */
data class AccessibilityAuditResult(
    val rating: AccessibilityRating,
    val score: Int, // 0-100
    val issues: List<AccessibilityIssue>,
    val suggestions: List<String>,
    val compliance: Map<String, Boolean> // WCAG criteria compliance
)

/**
 * Voice navigation commands
 */
sealed class VoiceCommand {
    object NavigateNext : VoiceCommand()
    object NavigatePrevious : VoiceCommand()
    object NavigateUp : VoiceCommand()
    object NavigateDown : VoiceCommand()
    object Activate : VoiceCommand()
    object GoBack : VoiceCommand()
    object GoHome : VoiceCommand()
    data class GoTo(val destination: String) : VoiceCommand()
    data class Select(val item: String) : VoiceCommand()
    data class Type(val text: String) : VoiceCommand()
    object StartReading : VoiceCommand()
    object StopReading : VoiceCommand()
    object RepeatLast : VoiceCommand()
}

/**
 * Main accessibility manager
 */
class AccessibilityManager {
    
    private val _preferences = MutableStateFlow(AccessibilityPreferences())
    val preferences: StateFlow<AccessibilityPreferences> = _preferences.asStateFlow()
    
    private val _isScreenReaderActive = MutableStateFlow(false)
    val isScreenReaderActive: StateFlow<Boolean> = _isScreenReaderActive.asStateFlow()
    
    private val _currentFocus = MutableStateFlow<String?>(null)
    val currentFocus: StateFlow<String?> = _currentFocus.asStateFlow()
    
    private val announcements = mutableListOf<ScreenReaderAnnouncement>()
    
    /**
     * Update accessibility preferences
     */
    fun updatePreferences(preferences: AccessibilityPreferences) {
        _preferences.value = preferences
        _isScreenReaderActive.value = preferences.screenReaderEnabled
    }
    
    /**
     * Update individual preference
     */
    fun updatePreference(update: (AccessibilityPreferences) -> AccessibilityPreferences) {
        val current = _preferences.value
        updatePreferences(update(current))
    }
    
    /**
     * Enable/disable screen reader
     */
    fun setScreenReaderEnabled(enabled: Boolean) {
        updatePreference { it.copy(screenReaderEnabled = enabled) }
    }
    
    /**
     * Set high contrast mode
     */
    fun setHighContrastEnabled(enabled: Boolean) {
        updatePreference { it.copy(highContrast = enabled) }
    }
    
    /**
     * Set large text mode
     */
    fun setLargeTextEnabled(enabled: Boolean) {
        updatePreference { it.copy(largeText = enabled) }
    }
    
    /**
     * Set reduced motion
     */
    fun setReducedMotionEnabled(enabled: Boolean) {
        updatePreference { it.copy(reducedMotion = enabled) }
    }
    
    /**
     * Announce text to screen reader
     */
    fun announce(
        text: String,
        priority: AnnouncementPriority = AnnouncementPriority.NORMAL,
        contentType: ScreenReaderContentType? = null,
        shouldInterrupt: Boolean = false
    ) {
        if (_isScreenReaderActive.value) {
            val announcement = ScreenReaderAnnouncement(
                text = text,
                priority = priority,
                contentType = contentType,
                shouldInterrupt = shouldInterrupt
            )
            announcements.add(announcement)
            // Platform-specific implementation would handle actual announcement
        }
    }
    
    /**
     * Set focus to element
     */
    fun setFocus(elementId: String, announceContent: Boolean = true) {
        _currentFocus.value = elementId
        
        if (announceContent && _isScreenReaderActive.value) {
            // Platform-specific implementation would get element content and announce it
            announce("Focused on $elementId")
        }
    }
    
    /**
     * Navigate to next focusable element
     */
    fun navigateNext() {
        // Platform-specific implementation would handle focus navigation
        announce("Navigating to next element")
    }
    
    /**
     * Navigate to previous focusable element
     */
    fun navigatePrevious() {
        // Platform-specific implementation would handle focus navigation
        announce("Navigating to previous element")
    }
    
    /**
     * Handle voice command
     */
    fun handleVoiceCommand(command: VoiceCommand) {
        when (command) {
            is VoiceCommand.NavigateNext -> navigateNext()
            is VoiceCommand.NavigatePrevious -> navigatePrevious()
            is VoiceCommand.Activate -> {
                announce("Activating current item")
                // Platform-specific activation
            }
            is VoiceCommand.GoTo -> {
                announce("Navigating to ${command.destination}")
                // Platform-specific navigation
            }
            is VoiceCommand.Select -> {
                announce("Selecting ${command.item}")
                // Platform-specific selection
            }
            is VoiceCommand.Type -> {
                announce("Typing: ${command.text}")
                // Platform-specific text input
            }
            is VoiceCommand.StartReading -> {
                announce("Starting continuous reading")
                // Platform-specific reading mode
            }
            is VoiceCommand.StopReading -> {
                announce("Stopping reading")
                // Platform-specific stop reading
            }
            is VoiceCommand.RepeatLast -> {
                val lastAnnouncement = announcements.lastOrNull()
                lastAnnouncement?.let { announce(it.text) }
            }
            else -> {
                announce("Command not recognized")
            }
        }
    }
    
    /**
     * Get text scale factor based on preferences
     */
    fun getTextScaleFactor(): Float {
        val prefs = _preferences.value
        return when {
            prefs.extraLargeText -> 1.5f
            prefs.largeText -> 1.2f
            else -> 1.0f
        }
    }
    
    /**
     * Get touch target scale factor
     */
    fun getTouchTargetScaleFactor(): Float {
        return if (_preferences.value.largerTouchTargets) 1.3f else 1.0f
    }
    
    /**
     * Get minimum touch target size
     */
    fun getMinimumTouchTargetSize(): Float {
        val baseSizeDp = 48f // Material Design minimum
        return baseSizeDp * getTouchTargetScaleFactor()
    }
    
    /**
     * Check if animations should be reduced
     */
    fun shouldReduceMotion(): Boolean {
        return _preferences.value.reducedMotion
    }
    
    /**
     * Get animation duration multiplier
     */
    fun getAnimationDurationMultiplier(): Float {
        return if (shouldReduceMotion()) 0.3f else 1.0f
    }
    
    /**
     * Clear announcement queue
     */
    fun clearAnnouncements() {
        announcements.clear()
    }
    
    /**
     * Get pending announcements
     */
    fun getPendingAnnouncements(): List<ScreenReaderAnnouncement> {
        return announcements.toList()
    }
}

/**
 * Accessibility audit tools
 */
class AccessibilityAuditor {
    
    /**
     * Audit color contrast
     */
    fun auditContrast(foregroundColor: ULong, backgroundColor: ULong): AccessibilityIssue? {
        val contrast = calculateContrastRatio(foregroundColor, backgroundColor)
        
        return when {
            contrast < 3.0 -> AccessibilityIssue(
                type = AccessibilityIssueType.CONTRAST_TOO_LOW,
                severity = IssueSeverity.CRITICAL,
                description = "Contrast ratio $contrast is too low (minimum 3.0 required)",
                suggestion = "Increase contrast between foreground and background colors"
            )
            contrast < 4.5 -> AccessibilityIssue(
                type = AccessibilityIssueType.CONTRAST_TOO_LOW,
                severity = IssueSeverity.HIGH,
                description = "Contrast ratio $contrast does not meet AA standards (4.5 required)",
                suggestion = "Increase contrast to meet WCAG AA standards"
            )
            contrast < 7.0 -> AccessibilityIssue(
                type = AccessibilityIssueType.CONTRAST_TOO_LOW,
                severity = IssueSeverity.MEDIUM,
                description = "Contrast ratio $contrast does not meet AAA standards (7.0 required)",
                suggestion = "Consider increasing contrast for AAA compliance"
            )
            else -> null // Contrast is acceptable
        }
    }
    
    /**
     * Audit touch target size
     */
    fun auditTouchTargetSize(widthDp: Float, heightDp: Float): AccessibilityIssue? {
        val minSize = 48f // Material Design minimum
        
        return when {
            widthDp < minSize || heightDp < minSize -> AccessibilityIssue(
                type = AccessibilityIssueType.TOUCH_TARGET_TOO_SMALL,
                severity = IssueSeverity.HIGH,
                description = "Touch target ${widthDp}x${heightDp}dp is smaller than minimum ${minSize}dp",
                suggestion = "Increase touch target size to at least ${minSize}x${minSize}dp"
            )
            else -> null
        }
    }
    
    /**
     * Audit text size
     */
    fun auditTextSize(textSizeSp: Float): AccessibilityIssue? {
        val minSize = 12f // Minimum readable size
        
        return when {
            textSizeSp < minSize -> AccessibilityIssue(
                type = AccessibilityIssueType.TEXT_TOO_SMALL,
                severity = IssueSeverity.MEDIUM,
                description = "Text size ${textSizeSp}sp is smaller than recommended minimum ${minSize}sp",
                suggestion = "Increase text size to at least ${minSize}sp"
            )
            else -> null
        }
    }
    
    /**
     * Audit content for missing labels
     */
    fun auditContentLabels(
        elementType: ScreenReaderContentType,
        hasLabel: Boolean,
        hasDescription: Boolean
    ): List<AccessibilityIssue> {
        val issues = mutableListOf<AccessibilityIssue>()
        
        if (!hasLabel) {
            when (elementType) {
                ScreenReaderContentType.BUTTON,
                ScreenReaderContentType.LINK,
                ScreenReaderContentType.FORM_FIELD -> {
                    issues.add(
                        AccessibilityIssue(
                            type = AccessibilityIssueType.MISSING_LABEL,
                            severity = IssueSeverity.HIGH,
                            description = "${elementType.name} is missing a label",
                            suggestion = "Add a descriptive label or aria-label attribute"
                        )
                    )
                }
                ScreenReaderContentType.IMAGE -> {
                    issues.add(
                        AccessibilityIssue(
                            type = AccessibilityIssueType.NO_ALT_TEXT,
                            severity = IssueSeverity.HIGH,
                            description = "Image is missing alt text",
                            suggestion = "Add descriptive alt text or mark as decorative"
                        )
                    )
                }
                else -> { /* Optional for other types */ }
            }
        }
        
        if (!hasDescription && elementType == ScreenReaderContentType.FORM_FIELD) {
            issues.add(
                AccessibilityIssue(
                    type = AccessibilityIssueType.MISSING_DESCRIPTION,
                    severity = IssueSeverity.MEDIUM,
                    description = "Form field is missing a description",
                    suggestion = "Add a description to help users understand the field purpose"
                )
            )
        }
        
        return issues
    }
    
    /**
     * Perform comprehensive accessibility audit
     */
    fun performAudit(
        textElements: List<TextAuditData>,
        interactiveElements: List<InteractiveAuditData>,
        colorCombinations: List<ColorAuditData>
    ): AccessibilityAuditResult {
        val issues = mutableListOf<AccessibilityIssue>()
        
        // Audit text elements
        textElements.forEach { textData ->
            auditTextSize(textData.sizeSp)?.let { issues.add(it) }
        }
        
        // Audit interactive elements
        interactiveElements.forEach { interactiveData ->
            auditTouchTargetSize(interactiveData.widthDp, interactiveData.heightDp)?.let { 
                issues.add(it) 
            }
            issues.addAll(
                auditContentLabels(
                    interactiveData.contentType,
                    interactiveData.hasLabel,
                    interactiveData.hasDescription
                )
            )
        }
        
        // Audit color combinations
        colorCombinations.forEach { colorData ->
            auditContrast(colorData.foreground, colorData.background)?.let { 
                issues.add(it) 
            }
        }
        
        // Calculate score and rating
        val score = calculateAccessibilityScore(issues)
        val rating = determineAccessibilityRating(score, issues)
        
        return AccessibilityAuditResult(
            rating = rating,
            score = score,
            issues = issues,
            suggestions = generateSuggestions(issues),
            compliance = checkWCAGCompliance(issues)
        )
    }
    
    /**
     * Calculate contrast ratio between two colors
     */
    private fun calculateContrastRatio(color1: ULong, color2: ULong): Double {
        val luminance1 = calculateRelativeLuminance(color1)
        val luminance2 = calculateRelativeLuminance(color2)
        
        val lighter = maxOf(luminance1, luminance2)
        val darker = minOf(luminance1, luminance2)
        
        return (lighter + 0.05) / (darker + 0.05)
    }
    
    /**
     * Calculate relative luminance of a color
     */
    private fun calculateRelativeLuminance(color: ULong): Double {
        val r = ((color shr 16) and 0xFFUL).toDouble() / 255.0
        val g = ((color shr 8) and 0xFFUL).toDouble() / 255.0
        val b = (color and 0xFFUL).toDouble() / 255.0
        
        fun linearize(value: Double): Double {
            return if (value <= 0.03928) {
                value / 12.92
            } else {
                ((value + 0.055) / 1.055).pow(2.4)
            }
        }
        
        return 0.2126 * linearize(r) + 0.7152 * linearize(g) + 0.0722 * linearize(b)
    }
    
    /**
     * Calculate overall accessibility score
     */
    private fun calculateAccessibilityScore(issues: List<AccessibilityIssue>): Int {
        if (issues.isEmpty()) return 100
        
        val totalDeductions = issues.fold(0) { acc, issue ->
            acc + when (issue.severity) {
                IssueSeverity.CRITICAL -> 25
                IssueSeverity.HIGH -> 15
                IssueSeverity.MEDIUM -> 8
                IssueSeverity.LOW -> 3
                IssueSeverity.INFO -> 1
            }
        }
        
        return (100 - totalDeductions).coerceAtLeast(0)
    }
    
    /**
     * Determine accessibility rating based on score and issues
     */
    private fun determineAccessibilityRating(score: Int, issues: List<AccessibilityIssue>): AccessibilityRating {
        val hasCriticalIssues = issues.any { it.severity == IssueSeverity.CRITICAL }
        val hasHighIssues = issues.any { it.severity == IssueSeverity.HIGH }
        
        return when {
            hasCriticalIssues -> AccessibilityRating.FAIL
            hasHighIssues || score < 70 -> AccessibilityRating.A
            score < 85 -> AccessibilityRating.AA
            else -> AccessibilityRating.AAA
        }
    }
    
    /**
     * Generate improvement suggestions
     */
    private fun generateSuggestions(issues: List<AccessibilityIssue>): List<String> {
        val suggestions = issues.map { it.suggestion }.distinct().toMutableList()
        
        // Add general suggestions
        if (issues.isNotEmpty()) {
            suggestions.add("Test with actual screen readers and assistive technologies")
            suggestions.add("Get feedback from users with disabilities")
            suggestions.add("Consider implementing skip navigation links")
            suggestions.add("Ensure all interactive elements are keyboard accessible")
        }
        
        return suggestions
    }
    
    /**
     * Check WCAG compliance criteria
     */
    private fun checkWCAGCompliance(issues: List<AccessibilityIssue>): Map<String, Boolean> {
        return mapOf(
            "1.1.1 Non-text Content" to !issues.any { it.type == AccessibilityIssueType.NO_ALT_TEXT },
            "1.4.3 Contrast (Minimum)" to !issues.any { 
                it.type == AccessibilityIssueType.CONTRAST_TOO_LOW && it.severity >= IssueSeverity.HIGH 
            },
            "1.4.6 Contrast (Enhanced)" to !issues.any { it.type == AccessibilityIssueType.CONTRAST_TOO_LOW },
            "2.1.1 Keyboard" to !issues.any { it.type == AccessibilityIssueType.KEYBOARD_TRAP },
            "2.4.7 Focus Visible" to !issues.any { it.type == AccessibilityIssueType.FOCUS_NOT_VISIBLE },
            "3.2.2 On Input" to true, // Assume compliance unless specific issues detected
            "4.1.2 Name, Role, Value" to !issues.any { 
                it.type == AccessibilityIssueType.MISSING_LABEL || 
                it.type == AccessibilityIssueType.MISSING_DESCRIPTION 
            }
        )
    }
}

/**
 * Data classes for audit input
 */
data class TextAuditData(
    val sizeSp: Float,
    val content: String,
    val elementId: String? = null
)

data class InteractiveAuditData(
    val widthDp: Float,
    val heightDp: Float,
    val contentType: ScreenReaderContentType,
    val hasLabel: Boolean,
    val hasDescription: Boolean,
    val elementId: String? = null
)

data class ColorAuditData(
    val foreground: ULong,
    val background: ULong,
    val elementId: String? = null
)

/**
 * Accessibility extensions and utilities
 */

/**
 * Create accessible text description
 */
fun createAccessibleDescription(
    type: ScreenReaderContentType,
    text: String,
    context: String? = null
): String {
    val typeDescription = when (type) {
        ScreenReaderContentType.BUTTON -> "Button"
        ScreenReaderContentType.LINK -> "Link"
        ScreenReaderContentType.HEADING -> "Heading"
        ScreenReaderContentType.IMAGE -> "Image"
        ScreenReaderContentType.FORM_FIELD -> "Text field"
        else -> ""
    }
    
    return buildString {
        if (typeDescription.isNotEmpty()) {
            append(typeDescription)
            append(": ")
        }
        append(text)
        if (context != null) {
            append(". ")
            append(context)
        }
    }
}

/**
 * Generate semantic content for screen readers
 */
fun generateSemanticContent(
    primaryText: String,
    secondaryText: String? = null,
    state: String? = null,
    position: String? = null
): String {
    return buildString {
        append(primaryText)
        
        if (secondaryText != null) {
            append(", ")
            append(secondaryText)
        }
        
        if (state != null) {
            append(", ")
            append(state)
        }
        
        if (position != null) {
            append(", ")
            append(position)
        }
    }
}