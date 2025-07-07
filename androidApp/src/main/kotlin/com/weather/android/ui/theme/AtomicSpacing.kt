package com.weather.android.ui.theme

import androidx.compose.ui.unit.dp

object AtomicSpacing {
    // Base spacing values
    val None = 0.dp
    val XS = 4.dp    // Extra Small - minimal spacing
    val SM = 8.dp    // Small - compact spacing
    val MD = 16.dp   // Medium - standard spacing
    val LG = 24.dp   // Large - generous spacing
    val XL = 32.dp   // Extra Large - section spacing
    val XXL = 48.dp  // Double Extra Large - major section spacing
    val XXXL = 64.dp // Triple Extra Large - page-level spacing
    
    // Component specific spacing
    val CardPadding = MD
    val CardMargin = SM
    val ButtonPadding = MD
    val ButtonHeight = 48.dp
    val IconSize = 24.dp
    val IconSizeMedium = 32.dp
    val IconSizeLarge = 48.dp
    val IconSizeXL = 64.dp
    
    // Layout spacing
    val ScreenPadding = MD
    val SectionSpacing = LG
    val ItemSpacing = SM
    val ListItemPadding = MD
    
    // Weather specific spacing
    val WeatherCardPadding = MD
    val WeatherCardSpacing = SM
    val TemperatureSpacing = XS
    val WeatherIconSpacing = SM
    
    // Touch target sizes (minimum 48dp for accessibility)
    val MinTouchTarget = 48.dp
    val TouchTargetPadding = 12.dp
    
    // Border and divider sizes
    val BorderWidth = 1.dp
    val DividerWidth = 1.dp
}