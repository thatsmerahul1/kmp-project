package com.weather.android.ui.theme

import androidx.compose.ui.graphics.Color

object AtomicColors {
    // Primary Colors
    val Primary = Color(0xFF1976D2)
    val PrimaryVariant = Color(0xFF1565C0)
    val OnPrimary = Color.White
    
    // Surface Colors
    val Surface = Color(0xFFFAFAFA)
    val OnSurface = Color(0xFF212121)
    val SurfaceVariant = Color(0xFFF5F5F5)
    val OnSurfaceVariant = Color(0xFF757575)
    
    // Background Colors
    val Background = Color.White
    val OnBackground = Color(0xFF1C1B1F)
    
    // Semantic Colors
    val Success = Color(0xFF4CAF50)
    val Warning = Color(0xFFFF9800)
    val Error = Color(0xFFF44336)
    val Info = Color(0xFF2196F3)
    val OnError = Color.White
    
    // Weather Specific Colors
    val Sunny = Color(0xFFFFD54F)
    val Cloudy = Color(0xFF90A4AE)
    val Rainy = Color(0xFF42A5F5)
    val Snowy = Color(0xFFE1F5FE)
    val Stormy = Color(0xFF5E35B1)
    
    // Gradient Colors
    val SunnyGradientStart = Color(0xFFFFEB3B)
    val SunnyGradientEnd = Color(0xFFFFC107)
    val CloudyGradientStart = Color(0xFFCFD8DC)
    val CloudyGradientEnd = Color(0xFF90A4AE)
    val RainyGradientStart = Color(0xFF81C784)
    val RainyGradientEnd = Color(0xFF42A5F5)
    
    // Opacity Variations
    val OnSurfaceDisabled = OnSurface.copy(alpha = 0.38f)
    val OnSurfaceMedium = OnSurface.copy(alpha = 0.6f)
    val OnSurfaceHigh = OnSurface.copy(alpha = 0.87f)
}