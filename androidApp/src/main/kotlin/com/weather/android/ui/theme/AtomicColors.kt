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
    val SurfaceContainer = Color(0xFFF3F3F3)
    
    // Card specific colors - consistent off-white like iOS
    val WeatherCardBackground = Color(0xFFFCFCFC) // Very light off-white for weather cards
    
    // Secondary Colors
    val Secondary = Color(0xFF03DAC6)
    val SecondaryContainer = Color(0xFF4ECCA3)
    val OnSecondaryContainer = Color(0xFF003735)
    
    // Background Colors
    val Background = Color.White
    val OnBackground = Color(0xFF1C1B1F)
    
    // Semantic Colors
    val Success = Color(0xFF4CAF50)
    val Warning = Color(0xFFFF9800)
    val Error = Color(0xFFF44336)
    val Info = Color(0xFF2196F3)
    val OnError = Color.White
    val Outline = Color(0xFFCAC4D0)
    
    // Weather Specific Colors - Modern and vibrant
    val Sunny = Color(0xFFFFD54F)
    val Cloudy = Color(0xFF90A4AE)
    val Rainy = Color(0xFF42A5F5)
    val Snowy = Color(0xFFE1F5FE)
    val Stormy = Color(0xFF5E35B1)
    
    // Modern Weather Gradients - Inspired by Apple Weather and Dark Sky
    // Sunny gradients
    val SunnyGradientStart = Color(0xFFFFA726) // Warm orange
    val SunnyGradientEnd = Color(0xFFFFEB3B)   // Bright yellow
    val SunnyGradientAccent = Color(0xFFFF8F00) // Deep orange accent
    
    // Cloudy gradients  
    val CloudyGradientStart = Color(0xFFECEFF1) // Light gray
    val CloudyGradientEnd = Color(0xFF90A4AE)   // Medium gray
    val CloudyGradientAccent = Color(0xFF607D8B) // Dark gray accent
    
    // Rainy gradients
    val RainyGradientStart = Color(0xFF64B5F6) // Light blue
    val RainyGradientEnd = Color(0xFF1976D2)   // Dark blue
    val RainyGradientAccent = Color(0xFF0D47A1) // Navy accent
    
    // Thunderstorm gradients
    val StormyGradientStart = Color(0xFF7986CB) // Light purple
    val StormyGradientEnd = Color(0xFF303F9F)   // Dark indigo
    val StormyGradientAccent = Color(0xFF1A237E) // Deep purple accent
    
    // Snow gradients
    val SnowyGradientStart = Color(0xFFE3F2FD) // Very light blue
    val SnowyGradientEnd = Color(0xFFBBDEFB)   // Light blue
    val SnowyGradientAccent = Color(0xFF90CAF9) // Medium blue accent
    
    // Opacity Variations
    val OnSurfaceDisabled = OnSurface.copy(alpha = 0.38f)
    val OnSurfaceMedium = OnSurface.copy(alpha = 0.6f)
    val OnSurfaceHigh = OnSurface.copy(alpha = 0.87f)
}