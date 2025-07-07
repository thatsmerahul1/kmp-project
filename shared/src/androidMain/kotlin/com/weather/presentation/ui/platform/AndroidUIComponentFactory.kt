package com.weather.presentation.ui.platform

import com.weather.presentation.ui.abstraction.*

/**
 * Android-specific implementation of UIComponentFactory
 * 
 * This implementation creates Android Compose-based UI components.
 * When migrating to Compose Multiplatform, this can be replaced with
 * a common implementation.
 */
class AndroidUIComponentFactory : UIComponentFactory {
    
    override fun createWeatherListComponent(): WeatherListUIComponent {
        return AndroidWeatherListComponent()
    }
    
    override fun createWeatherDetailComponent(): WeatherDetailUIComponent {
        return AndroidWeatherDetailComponent()
    }
    
    override fun createLoadingComponent(): LoadingUIComponent {
        return AndroidLoadingComponent()
    }
    
    override fun createErrorComponent(): ErrorUIComponent {
        return AndroidErrorComponent()
    }
}

/**
 * Android-specific weather list component
 */
class AndroidWeatherListComponent : WeatherListUIComponent {
    private var currentState: com.weather.presentation.state.WeatherUiState? = null
    private var eventHandler: ((com.weather.presentation.state.WeatherUiEvent) -> Unit)? = null
    
    override fun render(state: com.weather.presentation.state.WeatherUiState) {
        currentState = state
        // In actual implementation, this would update the Compose UI
        println("Android: Rendering weather list with ${state.weatherList.size} items")
    }
    
    override fun updateState(newState: com.weather.presentation.state.WeatherUiState) {
        render(newState)
    }
    
    override fun onEvent(event: com.weather.presentation.state.WeatherUiEvent) {
        eventHandler?.invoke(event)
    }
    
    override fun updateWeatherItem(item: com.weather.domain.model.Weather) {
        println("Android: Updating weather item for ${item.date}")
    }
    
    override fun addWeatherItem(item: com.weather.domain.model.Weather) {
        println("Android: Adding weather item for ${item.date}")
    }
    
    override fun removeWeatherItem(item: com.weather.domain.model.Weather) {
        println("Android: Removing weather item for ${item.date}")
    }
    
    override fun clearWeatherItems() {
        println("Android: Clearing all weather items")
    }
    
    override fun showLoading() {
        println("Android: Showing loading state")
    }
    
    override fun hideLoading() {
        println("Android: Hiding loading state")
    }
    
    override fun showError(error: String) {
        println("Android: Showing error: $error")
    }
    
    override fun clearError() {
        println("Android: Clearing error state")
    }
    
    fun setEventHandler(handler: (com.weather.presentation.state.WeatherUiEvent) -> Unit) {
        this.eventHandler = handler
    }
}

/**
 * Android-specific weather detail component
 */
class AndroidWeatherDetailComponent : WeatherDetailUIComponent {
    private var currentWeather: com.weather.domain.model.Weather? = null
    
    override fun render(state: com.weather.domain.model.Weather) {
        currentWeather = state
        println("Android: Rendering weather detail for ${state.date}")
    }
    
    override fun updateState(newState: com.weather.domain.model.Weather) {
        render(newState)
    }
    
    override fun navigateTo(destination: String) {
        println("Android: Navigating to $destination")
    }
    
    override fun navigateBack() {
        println("Android: Navigating back")
    }
    
    override fun replace(destination: String) {
        println("Android: Replacing with $destination")
    }
}

/**
 * Android-specific loading component
 */
class AndroidLoadingComponent : LoadingUIComponent {
    private var isLoading: Boolean = false
    
    override fun render(state: Boolean) {
        isLoading = state
        println("Android: Rendering loading state: $state")
    }
    
    override fun updateState(newState: Boolean) {
        render(newState)
    }
}

/**
 * Android-specific error component
 */
class AndroidErrorComponent : ErrorUIComponent {
    private var errorMessage: String? = null
    private var retryAction: (() -> Unit)? = null
    
    override fun render(state: String?) {
        errorMessage = state
        println("Android: Rendering error state: $state")
    }
    
    override fun updateState(newState: String?) {
        render(newState)
    }
    
    override fun setRetryAction(onRetry: () -> Unit) {
        retryAction = onRetry
    }
    
    fun onRetryClicked() {
        retryAction?.invoke()
    }
}

/**
 * Android theme provider implementation
 */
class AndroidThemeProvider : ThemeProvider {
    private var _currentTheme: UITheme = UITheme.light()
    
    override val currentTheme: UITheme
        get() = _currentTheme
    
    override fun setTheme(theme: UITheme) {
        _currentTheme = theme
        println("Android: Theme updated to ${if (theme.isDarkMode) "dark" else "light"} mode")
    }
    
    override fun toggleDarkMode() {
        _currentTheme = if (_currentTheme.isDarkMode) {
            UITheme.light()
        } else {
            UITheme.dark()
        }
        println("Android: Toggled to ${if (_currentTheme.isDarkMode) "dark" else "light"} mode")
    }
    
    override fun updateColorScheme(colorScheme: ColorScheme) {
        _currentTheme = _currentTheme.copy(colorScheme = colorScheme)
        println("Android: Color scheme updated")
    }
}

/**
 * Android event dispatcher implementation
 */
class AndroidUIEventDispatcher : UIEventDispatcher {
    private val handlers = mutableMapOf<String, MutableList<UIEventHandler<*>>>()
    
    override fun dispatch(event: UIEvent) {
        val eventType = event::class.simpleName ?: "Unknown"
        handlers[eventType]?.forEach { handler ->
            @Suppress("UNCHECKED_CAST")
            (handler as UIEventHandler<UIEvent>).handleEvent(event)
        }
        println("Android: Dispatched event: $eventType")
    }
    
    override fun <E : UIEvent> subscribe(eventType: String, handler: UIEventHandler<E>) {
        handlers.getOrPut(eventType) { mutableListOf() }.add(handler)
        println("Android: Subscribed to $eventType")
    }
    
    override fun <E : UIEvent> unsubscribe(eventType: String, handler: UIEventHandler<E>) {
        handlers[eventType]?.remove(handler)
        println("Android: Unsubscribed from $eventType")
    }
}