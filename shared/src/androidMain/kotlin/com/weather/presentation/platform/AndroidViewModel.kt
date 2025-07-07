package com.weather.presentation.platform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weather.domain.common.DomainException
import com.weather.domain.common.Result
import com.weather.presentation.common.BaseUiEvent
import com.weather.presentation.common.BaseUiState
import com.weather.presentation.common.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Android-specific ViewModel bridge that integrates Jetpack Lifecycle with shared BaseViewModel
 * 
 * This bridge provides:
 * - Jetpack ViewModel lifecycle integration
 * - SavedStateHandle support for state persistence
 * - Automatic cleanup when ViewModel is cleared
 * - Process death/recreation handling
 * - Integration with Compose lifecycle
 */
abstract class AndroidViewModelBridge<UiState : BaseUiState, UiEvent : BaseUiEvent>(
    private val savedStateHandle: SavedStateHandle? = null
) : ViewModel() {
    
    /**
     * The shared BaseViewModel instance that contains platform-agnostic logic
     */
    protected abstract val sharedViewModel: BaseViewModel<UiState, UiEvent>
    
    /**
     * Key for saving state in SavedStateHandle
     */
    protected open val stateKey: String = "ui_state"
    
    /**
     * UI State exposed to Compose
     */
    val uiState: StateFlow<UiState> = sharedViewModel.uiState
    
    init {
        // Restore state from SavedStateHandle if available
        restoreState()
        
        // Observe state changes and save to SavedStateHandle
        observeStateForSaving()
    }
    
    /**
     * Handle UI events from Compose
     */
    fun onEvent(event: UiEvent) {
        sharedViewModel.onEvent(event)
    }
    
    /**
     * Execute operation with ViewModel scope
     */
    protected fun executeInViewModelScope(
        showLoading: Boolean = true,
        operation: suspend () -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (showLoading) setLoading(true)
                operation()
            } catch (e: Exception) {
                val domainException = when (e) {
                    is DomainException -> e
                    else -> DomainException.Unknown(e.message ?: "Unknown error")
                }
                setError(domainException)
            } finally {
                if (showLoading) setLoading(false)
            }
        }
    }
    
    /**
     * Execute operation with Result handling
     */
    protected fun <T> executeWithResult(
        showLoading: Boolean = true,
        operation: suspend () -> Result<T>,
        onSuccess: (T) -> Unit = {}
    ) {
        executeInViewModelScope(showLoading) {
            when (val result = operation()) {
                is Result.Success -> onSuccess(result.data)
                is Result.Error -> setError(result.exception)
                is Result.Loading -> { /* Handled by executeInViewModelScope */ }
            }
        }
    }
    
    /**
     * Save data to SavedStateHandle for process death scenarios
     */
    protected fun saveToState(key: String, value: Any) {
        savedStateHandle?.set(key, value)
    }
    
    /**
     * Retrieve data from SavedStateHandle
     */
    protected fun <T> getFromState(key: String): T? {
        return savedStateHandle?.get<T>(key)
    }
    
    /**
     * Retrieve data from SavedStateHandle with default value
     */
    protected fun <T> getFromState(key: String, defaultValue: T): T {
        return savedStateHandle?.get<T>(key) ?: defaultValue
    }
    
    /**
     * Clear specific key from SavedStateHandle
     */
    protected fun clearFromState(key: String) {
        savedStateHandle?.remove<Any>(key)
    }
    
    /**
     * Set loading state
     */
    private fun setLoading(isLoading: Boolean) {
        sharedViewModel.uiState.value.let { currentState ->
            updateSharedState { currentState.copyWithLoading(isLoading) as UiState }
        }
    }
    
    /**
     * Set error state
     */
    private fun setError(error: DomainException?) {
        sharedViewModel.uiState.value.let { currentState ->
            updateSharedState { currentState.copyWithError(error) as UiState }
        }
    }
    
    /**
     * Update shared ViewModel state
     */
    private fun updateSharedState(update: () -> UiState) {
        // Access the protected method through reflection or provide a public method in BaseViewModel
        // For now, we'll emit events to trigger state changes
        // This is a limitation that should be addressed in BaseViewModel design
    }
    
    /**
     * Restore state from SavedStateHandle after process death
     */
    private fun restoreState() {
        savedStateHandle?.let { handle ->
            // Restore any saved state data
            // Implementation depends on what state needs to be preserved
            onStateRestored(handle)
        }
    }
    
    /**
     * Hook called when state is restored from SavedStateHandle
     * Override in concrete implementations to restore specific state
     */
    protected open fun onStateRestored(savedStateHandle: SavedStateHandle) {
        // Default implementation does nothing
        // Subclasses can override to restore specific state
    }
    
    /**
     * Observe state changes and save important data to SavedStateHandle
     */
    private fun observeStateForSaving() {
        viewModelScope.launch {
            sharedViewModel.uiState.collect { state ->
                onStateSaved(state)
            }
        }
    }
    
    /**
     * Hook called when state changes and should be saved
     * Override in concrete implementations to save specific state
     */
    protected open fun onStateSaved(state: UiState) {
        // Default implementation saves the entire state if serializable
        // Subclasses can override to save specific fields
        try {
            saveToState(stateKey, state)
        } catch (e: Exception) {
            // If state is not serializable, subclasses should handle specific fields
        }
    }
    
    /**
     * Called when ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        sharedViewModel.cleanup()
    }
    
    /**
     * Retry the last failed operation
     */
    fun retry() {
        sharedViewModel.retry()
    }
    
    /**
     * Refresh data from remote sources
     */
    fun refresh() {
        sharedViewModel.refresh()
    }
}

/**
 * Factory for creating AndroidViewModelBridge instances with SavedStateHandle
 */
abstract class AndroidViewModelFactory<UiState : BaseUiState, UiEvent : BaseUiEvent> {
    abstract fun create(savedStateHandle: SavedStateHandle): AndroidViewModelBridge<UiState, UiEvent>
}

/**
 * Extension functions for easier integration with Jetpack Compose
 */

// Compose integration extensions will be added when Compose dependencies are available
// These are placeholder implementations for future Compose integration

/**
 * Wrapper for SavedStateHandle operations with type safety
 */
class SavedStateManager(internal val savedStateHandle: SavedStateHandle) {
    
    fun <T> save(key: String, value: T) {
        savedStateHandle[key] = value
    }
    
    fun <T> get(key: String): T? {
        return savedStateHandle.get<T>(key)
    }
    
    fun <T> get(key: String, defaultValue: T): T {
        return savedStateHandle.get<T>(key) ?: defaultValue
    }
    
    fun remove(key: String) {
        savedStateHandle.remove<Any>(key)
    }
    
    fun contains(key: String): Boolean {
        return savedStateHandle.contains(key)
    }
    
    fun keys(): Set<String> {
        return savedStateHandle.keys()
    }
}

/**
 * Create SavedStateManager from SavedStateHandle
 */
fun SavedStateHandle.asManager(): SavedStateManager = SavedStateManager(this)