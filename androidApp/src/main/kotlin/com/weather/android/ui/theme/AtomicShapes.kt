package com.weather.android.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

object AtomicShapes {
    // Corner radius values
    val None = RoundedCornerShape(0.dp)
    val XS = RoundedCornerShape(4.dp)
    val SM = RoundedCornerShape(8.dp)
    val MD = RoundedCornerShape(12.dp)
    val LG = RoundedCornerShape(16.dp)
    val XL = RoundedCornerShape(24.dp)
    val XXL = RoundedCornerShape(32.dp)
    
    // Component specific shapes
    val Button = SM
    val ButtonLarge = MD
    val Card = MD
    val CardElevated = LG
    val Sheet = LG
    val Dialog = LG
    val Chip = RoundedCornerShape(16.dp)
    val Badge = RoundedCornerShape(12.dp)
    
    // Weather specific shapes
    val WeatherCard = MD
    val WeatherIcon = SM
    val TemperatureDisplay = SM
    
    // Input shapes
    val TextField = SM
    val InputField = SM
    val SearchField = RoundedCornerShape(24.dp)
    val SearchBar = RoundedCornerShape(24.dp)
    
    // Navigation shapes
    val BottomSheet = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
    
    val TopRounded = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
    
    val BottomRounded = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomStart = 16.dp,
        bottomEnd = 16.dp
    )
    
    // Navigation component shapes
    val NavigationDrawerItem = SM
}