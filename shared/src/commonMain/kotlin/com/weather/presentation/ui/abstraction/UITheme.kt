package com.weather.presentation.ui.abstraction

/**
 * UI Theme Abstraction Layer
 * 
 * Common theme definitions that can be shared across platforms and easily
 * migrated to Compose Multiplatform in the future.
 */

/**
 * Color scheme interface
 */
interface ColorScheme {
    val primary: String
    val primaryVariant: String
    val secondary: String
    val secondaryVariant: String
    val background: String
    val surface: String
    val error: String
    val onPrimary: String
    val onSecondary: String
    val onBackground: String
    val onSurface: String
    val onError: String
    
    // Weather-specific colors
    val sunny: String
    val cloudy: String
    val rainy: String
    val snowy: String
}

/**
 * Light color scheme
 */
data class LightColorScheme(
    override val primary: String = "#1976D2",
    override val primaryVariant: String = "#1565C0",
    override val secondary: String = "#03DAC6",
    override val secondaryVariant: String = "#018786",
    override val background: String = "#FFFFFF",
    override val surface: String = "#FFFFFF",
    override val error: String = "#B00020",
    override val onPrimary: String = "#FFFFFF",
    override val onSecondary: String = "#000000",
    override val onBackground: String = "#000000",
    override val onSurface: String = "#000000",
    override val onError: String = "#FFFFFF",
    override val sunny: String = "#FFD54F",
    override val cloudy: String = "#90A4AE",
    override val rainy: String = "#42A5F5",
    override val snowy: String = "#E1F5FE"
) : ColorScheme

/**
 * Dark color scheme
 */
data class DarkColorScheme(
    override val primary: String = "#BB86FC",
    override val primaryVariant: String = "#3700B3",
    override val secondary: String = "#03DAC6",
    override val secondaryVariant: String = "#03DAC6",
    override val background: String = "#121212",
    override val surface: String = "#121212",
    override val error: String = "#CF6679",
    override val onPrimary: String = "#000000",
    override val onSecondary: String = "#000000",
    override val onBackground: String = "#FFFFFF",
    override val onSurface: String = "#FFFFFF",
    override val onError: String = "#000000",
    override val sunny: String = "#FFA000",
    override val cloudy: String = "#78909C",
    override val rainy: String = "#1976D2",
    override val snowy: String = "#B3E5FC"
) : ColorScheme

/**
 * Typography scale
 */
data class Typography(
    val displayLarge: TextStyle = TextStyle(fontSize = 32f, fontWeight = FontWeight.Bold),
    val displayMedium: TextStyle = TextStyle(fontSize = 28f, fontWeight = FontWeight.Bold),
    val displaySmall: TextStyle = TextStyle(fontSize = 24f, fontWeight = FontWeight.Bold),
    val headlineLarge: TextStyle = TextStyle(fontSize = 24f, fontWeight = FontWeight.SemiBold),
    val headlineMedium: TextStyle = TextStyle(fontSize = 20f, fontWeight = FontWeight.SemiBold),
    val headlineSmall: TextStyle = TextStyle(fontSize = 18f, fontWeight = FontWeight.SemiBold),
    val titleLarge: TextStyle = TextStyle(fontSize = 18f, fontWeight = FontWeight.Medium),
    val titleMedium: TextStyle = TextStyle(fontSize = 16f, fontWeight = FontWeight.Medium),
    val titleSmall: TextStyle = TextStyle(fontSize = 14f, fontWeight = FontWeight.Medium),
    val bodyLarge: TextStyle = TextStyle(fontSize = 16f, fontWeight = FontWeight.Regular),
    val bodyMedium: TextStyle = TextStyle(fontSize = 14f, fontWeight = FontWeight.Regular),
    val bodySmall: TextStyle = TextStyle(fontSize = 12f, fontWeight = FontWeight.Regular),
    val labelLarge: TextStyle = TextStyle(fontSize = 14f, fontWeight = FontWeight.Medium),
    val labelMedium: TextStyle = TextStyle(fontSize = 12f, fontWeight = FontWeight.Medium),
    val labelSmall: TextStyle = TextStyle(fontSize = 10f, fontWeight = FontWeight.Medium)
)

/**
 * Text style data class
 */
data class TextStyle(
    val fontSize: Float,
    val fontWeight: FontWeight,
    val lineHeight: Float? = null,
    val letterSpacing: Float? = null,
    val fontFamily: String? = null
)

/**
 * Font weight enum
 */
enum class FontWeight(val value: Int) {
    Thin(100),
    ExtraLight(200),
    Light(300),
    Regular(400),
    Medium(500),
    SemiBold(600),
    Bold(700),
    ExtraBold(800),
    Black(900)
}

/**
 * Spacing scale
 */
data class Spacing(
    val none: Float = 0f,
    val xs: Float = 4f,
    val sm: Float = 8f,
    val md: Float = 16f,
    val lg: Float = 24f,
    val xl: Float = 32f,
    val xxl: Float = 48f
)

/**
 * Shape definitions
 */
data class Shapes(
    val none: Float = 0f,
    val xs: Float = 4f,
    val sm: Float = 8f,
    val md: Float = 12f,
    val lg: Float = 16f,
    val xl: Float = 24f,
    val full: Float = 9999f
)

/**
 * Elevation levels
 */
data class Elevation(
    val none: Float = 0f,
    val low: Float = 2f,
    val medium: Float = 4f,
    val high: Float = 8f,
    val highest: Float = 16f
)

/**
 * Complete theme definition
 */
data class UITheme(
    val colorScheme: ColorScheme,
    val typography: Typography = Typography(),
    val spacing: Spacing = Spacing(),
    val shapes: Shapes = Shapes(),
    val elevation: Elevation = Elevation(),
    val isDarkMode: Boolean = false
) {
    companion object {
        fun light(): UITheme = UITheme(
            colorScheme = LightColorScheme(),
            isDarkMode = false
        )
        
        fun dark(): UITheme = UITheme(
            colorScheme = DarkColorScheme(),
            isDarkMode = true
        )
    }
}

/**
 * Theme provider interface
 */
interface ThemeProvider {
    val currentTheme: UITheme
    fun setTheme(theme: UITheme)
    fun toggleDarkMode()
    fun updateColorScheme(colorScheme: ColorScheme)
}

/**
 * Component styling interface
 */
interface ComponentStyle {
    val name: String
    fun applyTo(component: Any): Any
}

/**
 * Button style
 */
data class ButtonStyle(
    override val name: String = "DefaultButton",
    val backgroundColor: String,
    val textColor: String,
    val borderRadius: Float,
    val elevation: Float,
    val padding: Float,
    val minWidth: Float,
    val minHeight: Float
) : ComponentStyle {
    override fun applyTo(component: Any): Any = component
}

/**
 * Card style
 */
data class CardStyle(
    override val name: String = "DefaultCard",
    val backgroundColor: String,
    val borderRadius: Float,
    val elevation: Float,
    val padding: Float,
    val margin: Float
) : ComponentStyle {
    override fun applyTo(component: Any): Any = component
}

/**
 * List style
 */
data class ListStyle(
    override val name: String = "DefaultList",
    val backgroundColor: String,
    val itemSpacing: Float,
    val padding: Float,
    val showDividers: Boolean
) : ComponentStyle {
    override fun applyTo(component: Any): Any = component
}

/**
 * Style registry for managing component styles
 */
interface StyleRegistry {
    fun <T : ComponentStyle> registerStyle(style: T)
    fun <T : ComponentStyle> getStyle(name: String): T?
    fun removeStyle(name: String)
    fun clear()
}