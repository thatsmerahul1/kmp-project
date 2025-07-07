package com.weather.presentation.ui.abstraction

/**
 * UI Event Abstraction Layer
 * 
 * Common UI events that are shared across platforms and can be easily
 * migrated to Compose Multiplatform in the future.
 */

/**
 * Base interface for all UI events
 */
interface UIEvent

/**
 * Click events
 */
sealed class ClickEvent : UIEvent {
    object Click : ClickEvent()
    object LongClick : ClickEvent()
    object DoubleClick : ClickEvent()
}

/**
 * Navigation events
 */
sealed class NavigationEvent : UIEvent {
    data class NavigateTo(val destination: String, val params: Map<String, Any> = emptyMap()) : NavigationEvent()
    object NavigateBack : NavigationEvent()
    data class Replace(val destination: String, val params: Map<String, Any> = emptyMap()) : NavigationEvent()
    object PopToRoot : NavigationEvent()
}

/**
 * Input events
 */
sealed class InputEvent : UIEvent {
    data class TextChanged(val text: String, val fieldId: String? = null) : InputEvent()
    data class FocusChanged(val hasFocus: Boolean, val fieldId: String? = null) : InputEvent()
    data class SelectionChanged(val selectedItem: Any) : InputEvent()
}

/**
 * List events
 */
sealed class ListEvent : UIEvent {
    data class ItemClicked(val item: Any, val position: Int) : ListEvent()
    data class ItemLongClicked(val item: Any, val position: Int) : ListEvent()
    object Refresh : ListEvent()
    object LoadMore : ListEvent()
    data class ItemSelected(val item: Any) : ListEvent()
    data class ItemDeselected(val item: Any) : ListEvent()
}

/**
 * Loading events
 */
sealed class LoadingEvent : UIEvent {
    object StartLoading : LoadingEvent()
    object StopLoading : LoadingEvent()
    data class ShowProgress(val progress: Float) : LoadingEvent()
}

/**
 * Error events
 */
sealed class ErrorEvent : UIEvent {
    data class ShowError(val message: String, val isRetryable: Boolean = true) : ErrorEvent()
    object ClearError : ErrorEvent()
    object Retry : ErrorEvent()
}

/**
 * Dialog events
 */
sealed class DialogEvent : UIEvent {
    data class ShowDialog(
        val title: String,
        val message: String,
        val positiveButton: String? = null,
        val negativeButton: String? = null,
        val onPositive: (() -> Unit)? = null,
        val onNegative: (() -> Unit)? = null
    ) : DialogEvent()
    
    object DismissDialog : DialogEvent()
}

/**
 * Snackbar/Toast events
 */
sealed class MessageEvent : UIEvent {
    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null,
        val action: (() -> Unit)? = null,
        val duration: MessageDuration = MessageDuration.SHORT
    ) : MessageEvent()
    
    data class ShowToast(
        val message: String,
        val duration: MessageDuration = MessageDuration.SHORT
    ) : MessageEvent()
    
    object DismissMessage : MessageEvent()
}

/**
 * Message duration enum
 */
enum class MessageDuration {
    SHORT, LONG, INDEFINITE
}

/**
 * Form events
 */
sealed class FormEvent : UIEvent {
    object Submit : FormEvent()
    object Reset : FormEvent()
    data class ValidateField(val fieldId: String) : FormEvent()
    object ValidateAll : FormEvent()
}

/**
 * Search events
 */
sealed class SearchEvent : UIEvent {
    data class QueryChanged(val query: String) : SearchEvent()
    object SearchSubmitted : SearchEvent()
    object ClearSearch : SearchEvent()
    data class FilterChanged(val filter: String, val value: Any) : SearchEvent()
}

/**
 * Swipe events
 */
sealed class SwipeEvent : UIEvent {
    object SwipeLeft : SwipeEvent()
    object SwipeRight : SwipeEvent()
    object SwipeUp : SwipeEvent()
    object SwipeDown : SwipeEvent()
    object SwipeToRefresh : SwipeEvent()
}

/**
 * Theme events
 */
sealed class ThemeEvent : UIEvent {
    object ToggleDarkMode : ThemeEvent()
    data class ChangePrimaryColor(val color: String) : ThemeEvent()
    data class ChangeFontScale(val scale: Float) : ThemeEvent()
}

/**
 * Lifecycle events
 */
sealed class LifecycleEvent : UIEvent {
    object OnAppear : LifecycleEvent()
    object OnDisappear : LifecycleEvent()
    object OnPause : LifecycleEvent()
    object OnResume : LifecycleEvent()
    object OnDestroy : LifecycleEvent()
}

/**
 * Platform-specific events wrapper
 */
data class PlatformEvent(
    val platformSpecificData: Any
) : UIEvent

/**
 * Event handler interface
 */
interface UIEventHandler<E : UIEvent> {
    fun handleEvent(event: E)
}

/**
 * Event dispatcher interface
 */
interface UIEventDispatcher {
    fun dispatch(event: UIEvent)
    fun <E : UIEvent> subscribe(eventType: String, handler: UIEventHandler<E>)
    fun <E : UIEvent> unsubscribe(eventType: String, handler: UIEventHandler<E>)
}

/**
 * Convenience extensions for common events
 */
fun click() = ClickEvent.Click
fun longClick() = ClickEvent.LongClick
fun navigateTo(destination: String, params: Map<String, Any> = emptyMap()) = 
    NavigationEvent.NavigateTo(destination, params)
fun navigateBack() = NavigationEvent.NavigateBack
fun refresh() = ListEvent.Refresh
fun retry() = ErrorEvent.Retry
fun showError(message: String) = ErrorEvent.ShowError(message)
fun showLoading() = LoadingEvent.StartLoading
fun hideLoading() = LoadingEvent.StopLoading