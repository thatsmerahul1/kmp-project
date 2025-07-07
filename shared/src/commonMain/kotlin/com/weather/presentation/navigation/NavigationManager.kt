package com.weather.presentation.navigation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Navigation abstraction for 2025 architecture standards
 * 
 * This abstraction provides:
 * - Platform-agnostic navigation commands
 * - Type-safe navigation with sealed classes
 * - Navigation state management
 * - Deep linking support preparation
 * - Back stack management
 * - Integration hooks for Navigation Component (Android) and NavigationStack (iOS)
 */

/**
 * Sealed class representing all possible navigation commands
 */
sealed class NavigationCommand {
    
    /**
     * Navigate to a specific destination
     */
    data class NavigateTo(
        val route: String,
        val args: Map<String, Any> = emptyMap(),
        val options: NavigationOptions = NavigationOptions()
    ) : NavigationCommand()
    
    /**
     * Navigate back to previous destination
     */
    data class NavigateBack(
        val result: Map<String, Any>? = null
    ) : NavigationCommand()
    
    /**
     * Navigate back to a specific destination
     */
    data class NavigateBackTo(
        val route: String,
        val inclusive: Boolean = false
    ) : NavigationCommand()
    
    /**
     * Replace current destination with a new one
     */
    data class Replace(
        val route: String,
        val args: Map<String, Any> = emptyMap()
    ) : NavigationCommand()
    
    /**
     * Navigate and clear back stack
     */
    data class NavigateAndClearStack(
        val route: String,
        val args: Map<String, Any> = emptyMap()
    ) : NavigationCommand()
    
    /**
     * Pop to root and navigate to new destination
     */
    data class PopToRootAndNavigate(
        val route: String,
        val args: Map<String, Any> = emptyMap()
    ) : NavigationCommand()
    
    /**
     * Handle deep link navigation
     */
    data class HandleDeepLink(
        val url: String,
        val fallbackRoute: String? = null
    ) : NavigationCommand()
    
    /**
     * Show bottom sheet or modal
     */
    data class ShowBottomSheet(
        val route: String,
        val args: Map<String, Any> = emptyMap()
    ) : NavigationCommand()
    
    /**
     * Dismiss bottom sheet or modal
     */
    object DismissBottomSheet : NavigationCommand()
    
    /**
     * Show dialog
     */
    data class ShowDialog(
        val route: String,
        val args: Map<String, Any> = emptyMap()
    ) : NavigationCommand()
    
    /**
     * Dismiss dialog
     */
    object DismissDialog : NavigationCommand()
}

/**
 * Navigation options for customizing navigation behavior
 */
data class NavigationOptions(
    val clearBackStack: Boolean = false,
    val singleTop: Boolean = false,
    val restoreState: Boolean = false,
    val saveState: Boolean = false,
    val animationType: AnimationType = AnimationType.Default
)

/**
 * Animation types for navigation transitions
 */
enum class AnimationType {
    Default,
    Slide,
    Fade,
    None,
    Custom
}

/**
 * Navigation destinations with type safety
 */
object Destinations {
    const val WEATHER_LIST = "weather_list"
    const val WEATHER_DETAIL = "weather_detail/{weatherId}"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
    
    // Deep link patterns
    const val WEATHER_DETAIL_DEEP_LINK = "weatherapp://weather/{weatherId}"
    
    /**
     * Generate route with arguments
     */
    fun weatherDetail(weatherId: String): String {
        return "weather_detail/$weatherId"
    }
    
    /**
     * Extract arguments from route
     */
    fun extractWeatherId(route: String): String? {
        return route.split("/").getOrNull(1)
    }
}

/**
 * Navigation result for handling results from destinations
 */
data class NavigationResult<T>(
    val key: String,
    val data: T
)

/**
 * Navigation state representing current navigation status
 */
data class NavigationState(
    val currentRoute: String = "",
    val backStackSize: Int = 0,
    val canGoBack: Boolean = false,
    val isBottomSheetOpen: Boolean = false,
    val isDialogOpen: Boolean = false
)

/**
 * Navigation manager interface
 */
interface NavigationManager {
    
    /**
     * Navigation commands flow
     */
    val navigationCommands: Flow<NavigationCommand>
    
    /**
     * Navigation state flow
     */
    val navigationState: Flow<NavigationState>
    
    /**
     * Execute navigation command
     */
    fun navigate(command: NavigationCommand)
    
    /**
     * Convenience methods for common navigation patterns
     */
    fun navigateTo(route: String, args: Map<String, Any> = emptyMap()) {
        navigate(NavigationCommand.NavigateTo(route, args))
    }
    
    fun navigateBack(result: Map<String, Any>? = null) {
        navigate(NavigationCommand.NavigateBack(result))
    }
    
    fun navigateBackTo(route: String, inclusive: Boolean = false) {
        navigate(NavigationCommand.NavigateBackTo(route, inclusive))
    }
    
    fun replace(route: String, args: Map<String, Any> = emptyMap()) {
        navigate(NavigationCommand.Replace(route, args))
    }
    
    fun navigateAndClearStack(route: String, args: Map<String, Any> = emptyMap()) {
        navigate(NavigationCommand.NavigateAndClearStack(route, args))
    }
    
    fun handleDeepLink(url: String, fallbackRoute: String? = null) {
        navigate(NavigationCommand.HandleDeepLink(url, fallbackRoute))
    }
    
    fun showBottomSheet(route: String, args: Map<String, Any> = emptyMap()) {
        navigate(NavigationCommand.ShowBottomSheet(route, args))
    }
    
    fun dismissBottomSheet() {
        navigate(NavigationCommand.DismissBottomSheet)
    }
    
    fun showDialog(route: String, args: Map<String, Any> = emptyMap()) {
        navigate(NavigationCommand.ShowDialog(route, args))
    }
    
    fun dismissDialog() {
        navigate(NavigationCommand.DismissDialog)
    }
    
    /**
     * Set navigation result for previous destination
     */
    fun setResult(key: String, data: Any)
    
    /**
     * Get navigation result
     */
    fun getResult(key: String): Any?
    
    /**
     * Clear navigation result
     */
    fun clearResult(key: String)
}

/**
 * Default implementation of NavigationManager
 */
class NavigationManagerImpl : NavigationManager {
    
    private val _navigationCommands = MutableSharedFlow<NavigationCommand>()
    override val navigationCommands = _navigationCommands.asSharedFlow()
    
    private val _navigationState = MutableSharedFlow<NavigationState>(replay = 1)
    override val navigationState = _navigationState.asSharedFlow()
    
    private val results = mutableMapOf<String, Any>()
    
    init {
        // Initialize with default state
        _navigationState.tryEmit(NavigationState())
    }
    
    override fun navigate(command: NavigationCommand) {
        _navigationCommands.tryEmit(command)
    }
    
    override fun setResult(key: String, data: Any) {
        results[key] = data
    }
    
    override fun getResult(key: String): Any? {
        return results[key]
    }
    
    override fun clearResult(key: String) {
        results.remove(key)
    }
    
    /**
     * Update navigation state (called by platform-specific implementations)
     */
    fun updateState(state: NavigationState) {
        _navigationState.tryEmit(state)
    }
}

/**
 * Extension functions for easier navigation
 */

/**
 * Navigate to weather detail with ID
 */
fun NavigationManager.navigateToWeatherDetail(weatherId: String) {
    navigateTo(Destinations.weatherDetail(weatherId), mapOf("weatherId" to weatherId))
}

/**
 * Navigate to settings
 */
fun NavigationManager.navigateToSettings() {
    navigateTo(Destinations.SETTINGS)
}

/**
 * Navigate to about
 */
fun NavigationManager.navigateToAbout() {
    navigateTo(Destinations.ABOUT)
}

/**
 * Navigate back to weather list
 */
fun NavigationManager.navigateBackToWeatherList() {
    navigateBackTo(Destinations.WEATHER_LIST)
}

/**
 * Handle weather deep link
 */
fun NavigationManager.handleWeatherDeepLink(weatherId: String) {
    handleDeepLink(
        Destinations.WEATHER_DETAIL_DEEP_LINK.replace("{weatherId}", weatherId),
        Destinations.WEATHER_LIST
    )
}

/**
 * Navigate with weather result
 */
fun NavigationManager.navigateBackWithWeatherResult(weather: Any) {
    setResult("weather_result", weather)
    navigateBack()
}

/**
 * Type-safe argument extraction
 */
object NavigationArgs {
    
    fun extractWeatherId(args: Map<String, Any>): String? {
        return args["weatherId"] as? String
    }
    
    fun extractQuery(args: Map<String, Any>): String? {
        return args["query"] as? String
    }
    
    fun extractRefresh(args: Map<String, Any>): Boolean {
        return args["refresh"] as? Boolean ?: false
    }
}

/**
 * Navigation graph definition for type safety
 */
object NavigationGraph {
    
    /**
     * All available routes in the app
     */
    val routes = setOf(
        Destinations.WEATHER_LIST,
        Destinations.WEATHER_DETAIL,
        Destinations.SETTINGS,
        Destinations.ABOUT
    )
    
    /**
     * Routes that require authentication
     */
    val authenticatedRoutes = setOf<String>(
        // Add routes that require auth
    )
    
    /**
     * Routes that should be in back stack
     */
    val backStackRoutes = setOf(
        Destinations.WEATHER_LIST,
        Destinations.WEATHER_DETAIL,
        Destinations.SETTINGS
    )
    
    /**
     * Deep link patterns
     */
    val deepLinkPatterns = mapOf(
        Destinations.WEATHER_DETAIL_DEEP_LINK to Destinations.WEATHER_DETAIL
    )
    
    /**
     * Validate route
     */
    fun isValidRoute(route: String): Boolean {
        return routes.any { pattern ->
            route.matches(pattern.replace(Regex("\\{.*?\\}"), ".*").toRegex())
        }
    }
    
    /**
     * Check if route requires authentication
     */
    fun requiresAuth(route: String): Boolean {
        return authenticatedRoutes.any { pattern ->
            route.matches(pattern.replace(Regex("\\{.*?\\}"), ".*").toRegex())
        }
    }
}

/**
 * Navigation middleware for handling cross-cutting concerns
 */
interface NavigationMiddleware {
    fun intercept(command: NavigationCommand): NavigationCommand?
}

/**
 * Authentication middleware
 */
class AuthNavigationMiddleware(
    private val isAuthenticated: () -> Boolean,
    private val loginRoute: String = "login"
) : NavigationMiddleware {
    
    override fun intercept(command: NavigationCommand): NavigationCommand? {
        return when (command) {
            is NavigationCommand.NavigateTo -> {
                if (NavigationGraph.requiresAuth(command.route) && !isAuthenticated()) {
                    NavigationCommand.NavigateTo(loginRoute)
                } else {
                    command
                }
            }
            else -> command
        }
    }
}

/**
 * Analytics middleware
 */
class AnalyticsNavigationMiddleware(
    private val logEvent: (String, Map<String, Any>) -> Unit
) : NavigationMiddleware {
    
    override fun intercept(command: NavigationCommand): NavigationCommand? {
        when (command) {
            is NavigationCommand.NavigateTo -> {
                logEvent("navigation_navigate_to", mapOf(
                    "route" to command.route,
                    "args_count" to command.args.size
                ))
            }
            is NavigationCommand.NavigateBack -> {
                logEvent("navigation_back", emptyMap())
            }
            else -> { /* Log other navigation events as needed */ }
        }
        return command
    }
}