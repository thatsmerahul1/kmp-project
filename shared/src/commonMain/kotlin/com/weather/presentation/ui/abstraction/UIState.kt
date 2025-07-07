package com.weather.presentation.ui.abstraction

/**
 * UI State Abstraction Layer
 * 
 * Common UI states that are shared across platforms and can be easily
 * migrated to Compose Multiplatform in the future.
 */

/**
 * Base interface for all UI states
 */
interface UIState

/**
 * Loading state representation
 */
data class LoadingState(
    val isLoading: Boolean = false,
    val message: String? = null
) : UIState

/**
 * Error state representation
 */
data class ErrorState(
    val hasError: Boolean = false,
    val message: String? = null,
    val isRetryable: Boolean = true
) : UIState

/**
 * Empty state representation
 */
data class EmptyState(
    val isEmpty: Boolean = false,
    val message: String? = null,
    val actionLabel: String? = null
) : UIState

/**
 * Success state representation
 */
data class SuccessState<T>(
    val data: T
) : UIState

/**
 * Combined UI state that includes common states
 */
data class CombinedUIState<T>(
    val data: T? = null,
    val loading: LoadingState = LoadingState(),
    val error: ErrorState = ErrorState(),
    val empty: EmptyState = EmptyState()
) : UIState {
    
    val isLoading: Boolean get() = loading.isLoading
    val hasError: Boolean get() = error.hasError
    val isEmpty: Boolean get() = empty.isEmpty && data == null
    val isSuccess: Boolean get() = data != null && !isLoading && !hasError
    
    companion object {
        fun <T> loading(message: String? = null): CombinedUIState<T> =
            CombinedUIState(loading = LoadingState(true, message))
        
        fun <T> error(message: String, isRetryable: Boolean = true): CombinedUIState<T> =
            CombinedUIState(error = ErrorState(true, message, isRetryable))
        
        fun <T> success(data: T): CombinedUIState<T> =
            CombinedUIState(data = data)
        
        fun <T> empty(message: String? = null, actionLabel: String? = null): CombinedUIState<T> =
            CombinedUIState(empty = EmptyState(true, message, actionLabel))
    }
}

/**
 * UI state extensions for common transformations
 */
fun <T> UIState.asLoading(): CombinedUIState<T> = CombinedUIState.loading()

fun <T> UIState.asError(message: String): CombinedUIState<T> = CombinedUIState.error(message)

fun <T> T.asSuccess(): CombinedUIState<T> = CombinedUIState.success(this)

/**
 * List-specific UI state
 */
data class ListUIState<T>(
    val items: List<T> = emptyList(),
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMoreItems: Boolean = false,
    val selectedItem: T? = null
) : UIState {
    
    val isEmpty: Boolean get() = items.isEmpty()
    val hasItems: Boolean get() = items.isNotEmpty()
}

/**
 * Form UI state
 */
data class FormUIState(
    val fields: Map<String, FormFieldState> = emptyMap(),
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val isValid: Boolean = false
) : UIState

/**
 * Form field state
 */
data class FormFieldState(
    val value: String = "",
    val error: String? = null,
    val isRequired: Boolean = false,
    val isValid: Boolean = true
) {
    val hasError: Boolean get() = error != null
}

/**
 * Navigation state
 */
data class NavigationState(
    val currentDestination: String,
    val canGoBack: Boolean = false,
    val backStackCount: Int = 0
) : UIState

/**
 * Theme state
 */
data class ThemeState(
    val isDarkMode: Boolean = false,
    val primaryColor: String = "#1976D2",
    val fontScale: Float = 1.0f
) : UIState