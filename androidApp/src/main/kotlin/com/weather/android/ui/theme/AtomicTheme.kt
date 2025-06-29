package com.weather.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

// Custom design tokens composition locals
val LocalAtomicColors = staticCompositionLocalOf { AtomicColors }
val LocalAtomicTypography = staticCompositionLocalOf { AtomicTypography }
val LocalAtomicSpacing = staticCompositionLocalOf { AtomicSpacing }
val LocalAtomicShapes = staticCompositionLocalOf { AtomicShapes }

// Material 3 color schemes
private val LightColorScheme = lightColorScheme(
    primary = AtomicColors.Primary,
    onPrimary = AtomicColors.OnPrimary,
    primaryContainer = AtomicColors.PrimaryVariant,
    surface = AtomicColors.Surface,
    onSurface = AtomicColors.OnSurface,
    surfaceVariant = AtomicColors.SurfaceVariant,
    onSurfaceVariant = AtomicColors.OnSurfaceVariant,
    background = AtomicColors.Background,
    onBackground = AtomicColors.OnBackground,
    error = AtomicColors.Error,
    onError = AtomicColors.OnError,
    outline = AtomicColors.OnSurfaceVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = AtomicColors.Primary,
    onPrimary = AtomicColors.OnPrimary,
    primaryContainer = AtomicColors.PrimaryVariant,
    surface = AtomicColors.OnSurface,
    onSurface = AtomicColors.Surface,
    surfaceVariant = AtomicColors.OnSurfaceVariant,
    onSurfaceVariant = AtomicColors.SurfaceVariant,
    background = AtomicColors.OnBackground,
    onBackground = AtomicColors.Background,
    error = AtomicColors.Error,
    onError = AtomicColors.OnError,
    outline = AtomicColors.SurfaceVariant
)

@Composable
fun AtomicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    CompositionLocalProvider(
        LocalAtomicColors provides AtomicColors,
        LocalAtomicTypography provides AtomicTypography,
        LocalAtomicSpacing provides AtomicSpacing,
        LocalAtomicShapes provides AtomicShapes
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = androidx.compose.material3.Typography(
                displayLarge = AtomicTypography.DisplayLarge,
                displayMedium = AtomicTypography.DisplayMedium,
                displaySmall = AtomicTypography.DisplaySmall,
                headlineLarge = AtomicTypography.HeadlineLarge,
                headlineMedium = AtomicTypography.HeadlineMedium,
                headlineSmall = AtomicTypography.HeadlineSmall,
                titleLarge = AtomicTypography.TitleLarge,
                titleMedium = AtomicTypography.TitleMedium,
                titleSmall = AtomicTypography.TitleSmall,
                bodyLarge = AtomicTypography.BodyLarge,
                bodyMedium = AtomicTypography.BodyMedium,
                bodySmall = AtomicTypography.BodySmall,
                labelLarge = AtomicTypography.LabelLarge,
                labelMedium = AtomicTypography.LabelMedium,
                labelSmall = AtomicTypography.LabelSmall
            ),
            content = content
        )
    }
}

// Extension property to access custom design tokens
object AtomicDesignSystem {
    val colors: AtomicColors
        @Composable get() = LocalAtomicColors.current
    
    val typography: AtomicTypography
        @Composable get() = LocalAtomicTypography.current
    
    val spacing: AtomicSpacing
        @Composable get() = LocalAtomicSpacing.current
    
    val shapes: AtomicShapes
        @Composable get() = LocalAtomicShapes.current
}