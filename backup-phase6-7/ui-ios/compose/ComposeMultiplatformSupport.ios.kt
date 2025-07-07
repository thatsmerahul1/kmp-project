package com.weather.ui.compose

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import platform.UIKit.*

/**
 * iOS-specific implementations for Compose Multiplatform support
 */

actual enum class Platform {
    ANDROID, IOS, DESKTOP, WEB
}

actual fun getPlatform(): Platform = Platform.IOS

@Composable
actual fun dynamicLightColorScheme(): ColorScheme {
    // iOS doesn't have dynamic colors like Android 12+
    // But we can create iOS-themed colors
    return lightColorScheme(
        primary = androidx.compose.ui.graphics.Color(0xFF007AFF), // iOS Blue
        onPrimary = androidx.compose.ui.graphics.Color.White,
        primaryContainer = androidx.compose.ui.graphics.Color(0xFFE3F2FD),
        onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF001D35),
        secondary = androidx.compose.ui.graphics.Color(0xFF34C759), // iOS Green
        onSecondary = androidx.compose.ui.graphics.Color.White,
        background = androidx.compose.ui.graphics.Color(0xFFF2F2F7), // iOS Light Gray
        onBackground = androidx.compose.ui.graphics.Color(0xFF1C1C1E),
        surface = androidx.compose.ui.graphics.Color.White,
        onSurface = androidx.compose.ui.graphics.Color(0xFF1C1C1E)
    )
}

@Composable
actual fun dynamicDarkColorScheme(): ColorScheme {
    // iOS dark mode colors
    return darkColorScheme(
        primary = androidx.compose.ui.graphics.Color(0xFF0A84FF), // iOS Blue Dark
        onPrimary = androidx.compose.ui.graphics.Color.White,
        primaryContainer = androidx.compose.ui.graphics.Color(0xFF004B73),
        onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFB3E5FC),
        secondary = androidx.compose.ui.graphics.Color(0xFF30D158), // iOS Green Dark
        onSecondary = androidx.compose.ui.graphics.Color.Black,
        background = androidx.compose.ui.graphics.Color(0xFF000000), // iOS Dark Background
        onBackground = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
        surface = androidx.compose.ui.graphics.Color(0xFF1C1C1E), // iOS Dark Surface
        onSurface = androidx.compose.ui.graphics.Color(0xFFFFFFFF)
    )
}

actual class PlatformHapticFeedback : HapticFeedback {
    
    override fun performHapticFeedback(type: HapticFeedbackType) {
        when (type) {
            HapticFeedbackType.CLICK -> {
                val generator = UIImpactFeedbackGenerator(UIImpactFeedbackStyleLight)
                generator.impactOccurred()
            }
            HapticFeedbackType.LONG_PRESS -> {
                val generator = UIImpactFeedbackGenerator(UIImpactFeedbackStyleMedium)
                generator.impactOccurred()
            }
            HapticFeedbackType.SUCCESS -> {
                val generator = UINotificationFeedbackGenerator()
                generator.notificationOccurred(UINotificationFeedbackTypeSuccess)
            }
            HapticFeedbackType.WARNING -> {
                val generator = UINotificationFeedbackGenerator()
                generator.notificationOccurred(UINotificationFeedbackTypeWarning)
            }
            HapticFeedbackType.ERROR -> {
                val generator = UINotificationFeedbackGenerator()
                generator.notificationOccurred(UINotificationFeedbackTypeError)
            }
        }
    }
}