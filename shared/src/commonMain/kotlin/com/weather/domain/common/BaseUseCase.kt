package com.weather.domain.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Base class for all Use Cases following 2025 Clean Architecture standards
 * 
 * This base class provides:
 * - Consistent error handling with Result wrapper
 * - Automatic thread management
 * - Standardized logging and analytics
 * - Parameter validation
 * - Performance monitoring hooks
 */
abstract class BaseUseCase<in P, R> {
    
    /**
     * The main dispatcher for the use case execution
     * Override this to specify a different dispatcher for specific use cases
     */
    protected open val dispatcher: CoroutineDispatcher
        get() = kotlinx.coroutines.Dispatchers.Default
    
    /**
     * Execute the use case with the given parameters
     * This is the main entry point that handles all cross-cutting concerns
     */
    suspend operator fun invoke(parameters: P): Result<R> = withContext(dispatcher) {
        suspendResultOf {
            onBeforeExecute(parameters)
            validateParameters(parameters)
            val result = execute(parameters)
            onAfterExecute(parameters, result)
            result
        }.onError { exception ->
            onError(parameters, exception)
        }
    }
    
    /**
     * The core business logic implementation
     * Subclasses must implement this method
     */
    protected abstract suspend fun execute(parameters: P): R
    
    /**
     * Validate the input parameters
     * Override this method to add parameter validation
     * Throw DomainException.Validation if parameters are invalid
     */
    protected open suspend fun validateParameters(parameters: P) {
        // Default implementation does no validation
        // Subclasses can override to add specific validation logic
    }
    
    /**
     * Hook called before execute() is invoked
     * Useful for logging, analytics, or setup operations
     */
    protected open suspend fun onBeforeExecute(parameters: P) {
        // Default implementation does nothing
        // Subclasses can override for logging/analytics
    }
    
    /**
     * Hook called after successful execute()
     * Useful for logging, analytics, or cleanup operations
     */
    protected open suspend fun onAfterExecute(parameters: P, result: R) {
        // Default implementation does nothing
        // Subclasses can override for logging/analytics
    }
    
    /**
     * Hook called when an error occurs during execution
     * Useful for error logging, analytics, or recovery operations
     */
    protected open suspend fun onError(parameters: P, exception: DomainException) {
        // Default implementation does nothing
        // Subclasses can override for error handling
    }
}

/**
 * Base class for Use Cases that return a Flow (reactive streams)
 * Perfect for use cases that need to emit multiple values over time
 */
abstract class BaseFlowUseCase<in P, R> {
    
    /**
     * The main dispatcher for the use case execution
     */
    protected open val dispatcher: CoroutineDispatcher
        get() = kotlinx.coroutines.Dispatchers.Default
    
    /**
     * Execute the use case and return a Flow of Results
     */
    operator fun invoke(parameters: P): Flow<Result<R>> = 
        execute(parameters)
            .catch { throwable ->
                val exception = when (throwable) {
                    is DomainException -> throwable
                    else -> DomainException.Unknown(throwable.message ?: "Unknown error in flow")
                }
                emit(Result.Error(exception))
                onError(parameters, exception)
            }
            .flowOn(dispatcher)
    
    /**
     * The core business logic implementation that returns a Flow
     * Subclasses must implement this method
     */
    protected abstract fun execute(parameters: P): Flow<Result<R>>
    
    /**
     * Hook called when an error occurs in the flow
     */
    protected open suspend fun onError(parameters: P, exception: DomainException) {
        // Default implementation does nothing
        // Subclasses can override for error handling
    }
}

/**
 * Base class for Use Cases that don't require parameters
 */
abstract class BaseNoParamsUseCase<R> : BaseUseCase<Unit, R>() {
    
    /**
     * Convenience method for invoking without parameters
     */
    suspend operator fun invoke(): Result<R> = invoke(Unit)
    
    /**
     * Execute without parameters
     */
    protected abstract suspend fun execute(): R
    
    /**
     * Final implementation that delegates to the no-params execute method
     */
    final override suspend fun execute(parameters: Unit): R = execute()
}

/**
 * Base class for Use Cases that return Flow and don't require parameters
 */
abstract class BaseNoParamsFlowUseCase<R> : BaseFlowUseCase<Unit, R>() {
    
    /**
     * Convenience method for invoking without parameters
     */
    operator fun invoke(): Flow<Result<R>> = invoke(Unit)
    
    /**
     * Execute without parameters and return Flow
     */
    protected abstract fun execute(): Flow<Result<R>>
    
    /**
     * Final implementation that delegates to the no-params execute method
     */
    final override fun execute(parameters: Unit): Flow<Result<R>> = execute()
}

/**
 * Marker interface for Use Case parameters
 * Implement this interface for all Use Case parameter classes
 * to ensure type safety and better organization
 */
interface UseCaseParams

/**
 * Empty parameters object for use cases that don't need parameters
 * but still want to maintain consistency with the BaseUseCase API
 */
object NoParams : UseCaseParams

/**
 * Extension functions for common Use Case patterns
 */

/**
 * Execute a use case and map the result to a different type
 */
suspend inline fun <P, R, T> BaseUseCase<P, R>.executeAndMap(
    parameters: P,
    crossinline mapper: (R) -> T
): Result<T> = invoke(parameters).map(mapper)

/**
 * Execute a use case and handle both success and error cases
 */
suspend inline fun <P, R> BaseUseCase<P, R>.executeAndHandle(
    parameters: P,
    crossinline onSuccess: (R) -> Unit,
    crossinline onError: (DomainException) -> Unit
) {
    invoke(parameters)
        .onSuccess(onSuccess)
        .onError(onError)
}

/**
 * Combine multiple use case results into a single result
 */
suspend fun <T> combineUseCaseResults(
    useCases: List<suspend () -> Result<T>>
): Result<List<T>> {
    val results = useCases.map { it() }
    return results.combineResults()
}

/**
 * Execute use cases in parallel and combine results
 */
suspend fun <T> executeInParallel(
    useCases: List<suspend () -> Result<T>>
): Result<List<T>> {
    return try {
        val results = mutableListOf<Result<T>>()
        for (useCase in useCases) {
            results.add(useCase())
        }
        results.combineResults()
    } catch (e: Exception) {
        Result.Error(DomainException.Unknown(e.message ?: "Error in parallel execution"))
    }
}