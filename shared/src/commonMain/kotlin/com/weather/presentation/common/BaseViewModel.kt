package com.weather.presentation.common

import com.weather.domain.common.DomainException
import com.weather.domain.common.Result
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel class following 2025 MVVM architecture standards
 * 
 * This base class provides:
 * - Consistent state management with StateFlow
 * - Built-in loading and error handling
 * - Automatic lifecycle management
 * - Exception handling with Result wrapper
 * - Common UI operations (loading, error display)
 * - Performance monitoring hooks
 */
abstract class BaseViewModel<UiState : BaseUiState, UiEvent : BaseUiEvent> {
    
    /**
     * CoroutineScope for ViewModel operations
     * Uses SupervisorJob to prevent child coroutine failures from cancelling the entire scope
     */
    protected val viewModelScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate + CoroutineExceptionHandler { _, throwable ->
            handleUnexpectedException(throwable)
        }
    )
    
    /**
     * Mutable state that can be updated by the ViewModel
     */
    protected abstract val _uiState: MutableStateFlow<UiState>
    
    /**
     * Public read-only state exposed to the UI
     */
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    /**
     * Current state value for convenient access
     */
    protected val currentState: UiState
        get() = _uiState.value
    
    /**
     * Abstract method to get the initial state
     * Subclasses must implement this to provide the starting state
     */
    protected abstract fun createInitialState(): UiState
    
    /**
     * Handle UI events from the View layer
     * Subclasses must implement this to respond to user interactions
     */
    abstract fun onEvent(event: UiEvent)
    
    /**
     * Update the UI state
     * This method is thread-safe and can be called from any coroutine
     */
    protected fun updateState(update: (UiState) -> UiState) {
        _uiState.value = update(currentState)
    }
    
    /**
     * Set loading state
     */
    protected fun setLoading(isLoading: Boolean) {
        updateState { it.copyWithLoading(isLoading) as UiState }
    }
    
    /**
     * Set error state
     */
    protected fun setError(error: DomainException?) {
        updateState { it.copyWithError(error) as UiState }
    }
    
    /**
     * Clear error state
     */
    protected fun clearError() {
        setError(null)
    }
    
    /**
     * Execute an operation with automatic loading and error handling
     */
    protected fun executeOperation(
        showLoading: Boolean = true,
        clearErrorFirst: Boolean = true,
        operation: suspend () -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (clearErrorFirst) clearError()
                if (showLoading) setLoading(true)
                
                operation()
                
            } catch (e: DomainException) {
                setError(e)
                onOperationError(e)
            } catch (e: Exception) {
                val domainException = DomainException.Unknown(e.message ?: "Unexpected error")
                setError(domainException)
                onOperationError(domainException)
            } finally {
                if (showLoading) setLoading(false)
            }
        }
    }
    
    /**
     * Execute an operation that returns a Result with automatic handling
     */
    protected fun <T> executeWithResult(
        showLoading: Boolean = true,
        clearErrorFirst: Boolean = true,
        operation: suspend () -> Result<T>,
        onSuccess: (T) -> Unit = {}
    ) {
        executeOperation(showLoading, clearErrorFirst) {
            when (val result = operation()) {
                is Result.Success -> {
                    onSuccess(result.data)
                }
                is Result.Error -> {
                    setError(result.exception)
                    onOperationError(result.exception)
                }
                is Result.Loading -> {
                    // Loading state is handled by executeOperation
                }
            }
        }
    }
    
    /**
     * Hook called when an operation error occurs
     * Override in subclasses for custom error handling (logging, analytics, etc.)
     */
    protected open fun onOperationError(exception: DomainException) {
        // Default implementation does nothing
        // Subclasses can override for custom error handling
    }
    
    /**
     * Handle unexpected exceptions that weren't caught by the Result wrapper
     */
    protected open fun handleUnexpectedException(throwable: Throwable) {
        val domainException = DomainException.Unknown(
            throwable.message ?: "An unexpected error occurred"
        )
        setError(domainException)
        onOperationError(domainException)
    }
    
    /**
     * Cleanup resources when ViewModel is no longer needed
     * Call this from the platform-specific ViewModel cleanup (onCleared on Android)
     */
    open fun cleanup() {
        viewModelScope.cancel()
    }
    
    /**
     * Retry the last failed operation
     * Override in subclasses to implement retry logic
     */
    open fun retry() {
        clearError()
        // Subclasses should override to implement specific retry logic
    }
    
    /**
     * Refresh data from remote sources
     * Override in subclasses to implement refresh logic
     */
    open fun refresh() {
        // Subclasses should override to implement specific refresh logic
    }
}

/**
 * Base interface for all UI States
 * All UI State classes should implement this interface
 */
interface BaseUiState {
    val isLoading: Boolean
    val error: DomainException?
    
    /**
     * Create a copy of this state with updated loading state
     */
    fun copyWithLoading(isLoading: Boolean): BaseUiState
    
    /**
     * Create a copy of this state with updated error state
     */
    fun copyWithError(error: DomainException?): BaseUiState
}

/**
 * Base interface for all UI Events
 * All UI Event classes should implement this interface
 */
interface BaseUiEvent

/**
 * Common UI events that most ViewModels will handle
 */
sealed class CommonUiEvent : BaseUiEvent {
    object Retry : CommonUiEvent()
    object Refresh : CommonUiEvent()
    object ClearError : CommonUiEvent()
    object LoadMore : CommonUiEvent()
    data class Search(val query: String) : CommonUiEvent()
    data class Filter(val filters: Map<String, Any>) : CommonUiEvent()
}

/**
 * Base UI State implementation with common properties
 */
abstract class BaseUiStateImpl(
    override val isLoading: Boolean = false,
    override val error: DomainException? = null
) : BaseUiState

/**
 * Extension functions for BaseViewModel
 */

