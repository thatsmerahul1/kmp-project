package com.weather.presentation.ui.abstraction

/**
 * UI Component Abstraction Layer
 * 
 * This interface provides a foundation for creating platform-agnostic UI components
 * that can be easily migrated to Compose Multiplatform in the future.
 * 
 * Each platform should implement these interfaces with their native UI framework:
 * - Android: Jetpack Compose
 * - iOS: SwiftUI
 * - Future: Compose Multiplatform for both platforms
 */

/**
 * Base interface for all UI components
 * @param T The state type that this component renders
 */
interface UIComponent<T> {
    /**
     * Renders the component with the given state
     * @param state The current state to render
     */
    fun render(state: T)
    
    /**
     * Updates the component with new state
     * @param newState The new state to apply
     */
    fun updateState(newState: T)
    
    /**
     * Lifecycle method called when component is created
     */
    fun onCreate() {}
    
    /**
     * Lifecycle method called when component is destroyed
     */
    fun onDestroy() {}
}

/**
 * Interface for components that can handle user interactions
 */
interface InteractiveUIComponent<T, E> : UIComponent<T> {
    /**
     * Handles user events
     * @param event The user event to handle
     */
    fun onEvent(event: E)
}

/**
 * Interface for components that support loading states
 */
interface LoadableUIComponent<T> : UIComponent<T> {
    /**
     * Shows loading state
     */
    fun showLoading()
    
    /**
     * Hides loading state
     */
    fun hideLoading()
    
    /**
     * Shows error state with message
     * @param error The error message to display
     */
    fun showError(error: String)
    
    /**
     * Clears any error state
     */
    fun clearError()
}

/**
 * Interface for list components
 */
interface ListUIComponent<T, I> : UIComponent<List<I>> {
    /**
     * Updates a single item in the list
     * @param item The item to update
     */
    fun updateItem(item: I)
    
    /**
     * Adds an item to the list
     * @param item The item to add
     */
    fun addItem(item: I)
    
    /**
     * Removes an item from the list
     * @param item The item to remove
     */
    fun removeItem(item: I)
    
    /**
     * Clears all items from the list
     */
    fun clearItems()
}

/**
 * Interface for navigation components
 */
interface NavigationUIComponent {
    /**
     * Navigates to a specific destination
     * @param destination The destination to navigate to
     */
    fun navigateTo(destination: String)
    
    /**
     * Navigates back to the previous screen
     */
    fun navigateBack()
    
    /**
     * Replaces the current screen with a new one
     * @param destination The destination to replace with
     */
    fun replace(destination: String)
}

/**
 * Factory interface for creating UI components
 */
interface UIComponentFactory {
    /**
     * Creates a weather list component
     */
    fun createWeatherListComponent(): WeatherListUIComponent
    
    /**
     * Creates a weather detail component
     */
    fun createWeatherDetailComponent(): WeatherDetailUIComponent
    
    /**
     * Creates a loading component
     */
    fun createLoadingComponent(): LoadingUIComponent
    
    /**
     * Creates an error component
     */
    fun createErrorComponent(): ErrorUIComponent
}

/**
 * Weather-specific UI component interfaces
 */
interface WeatherListUIComponent : 
    UIComponent<com.weather.presentation.state.WeatherUiState>,
    LoadableUIComponent<com.weather.presentation.state.WeatherUiState>,
    InteractiveUIComponent<com.weather.presentation.state.WeatherUiState, com.weather.presentation.state.WeatherUiEvent> {
    
    /**
     * Updates a weather item in the list
     */
    fun updateWeatherItem(item: com.weather.domain.model.Weather)
    
    /**
     * Adds a weather item to the list
     */
    fun addWeatherItem(item: com.weather.domain.model.Weather)
    
    /**
     * Removes a weather item from the list
     */
    fun removeWeatherItem(item: com.weather.domain.model.Weather)
    
    /**
     * Clears all weather items
     */
    fun clearWeatherItems()
}

interface WeatherDetailUIComponent : 
    UIComponent<com.weather.domain.model.Weather>,
    NavigationUIComponent

interface LoadingUIComponent : UIComponent<Boolean>

interface ErrorUIComponent : UIComponent<String?> {
    /**
     * Sets the retry action
     * @param onRetry The action to execute when retry is pressed
     */
    fun setRetryAction(onRetry: () -> Unit)
}