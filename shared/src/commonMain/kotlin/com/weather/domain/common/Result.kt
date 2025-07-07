package com.weather.domain.common

/**
 * Enhanced Result wrapper pattern for 2025 architecture standards
 * 
 * Provides a robust way to handle success and error states throughout the application.
 * This pattern eliminates the need for exception-based error handling in most cases
 * and makes error handling explicit and type-safe.
 */
sealed interface Result<out T> {
    
    /**
     * Represents a successful result containing data
     */
    data class Success<T>(val data: T) : Result<T>
    
    /**
     * Represents an error result with detailed error information
     */
    data class Error(val exception: DomainException) : Result<Nothing>
    
    /**
     * Represents a loading state (useful for UI state management)
     */
    object Loading : Result<Nothing>
}

/**
 * Extension functions for Result to make it more ergonomic to use
 */

/**
 * Returns true if this result is a success
 */
val <T> Result<T>.isSuccess: Boolean
    get() = this is Result.Success

/**
 * Returns true if this result is an error
 */
val <T> Result<T>.isError: Boolean
    get() = this is Result.Error

/**
 * Returns true if this result is loading
 */
val <T> Result<T>.isLoading: Boolean
    get() = this is Result.Loading

/**
 * Returns the data if this result is successful, null otherwise
 */
fun <T> Result<T>.getOrNull(): T? = when (this) {
    is Result.Success -> data
    else -> null
}

/**
 * Returns the exception if this result is an error, null otherwise
 */
fun <T> Result<T>.exceptionOrNull(): DomainException? = when (this) {
    is Result.Error -> exception
    else -> null
}

/**
 * Maps the success value using the given transform function
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> this
    is Result.Loading -> this
}

/**
 * Maps the error value using the given transform function
 */
inline fun <T> Result<T>.mapError(transform: (DomainException) -> DomainException): Result<T> = when (this) {
    is Result.Success -> this
    is Result.Error -> Result.Error(transform(exception))
    is Result.Loading -> this
}

/**
 * Flat maps the success value using the given transform function
 */
inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> = when (this) {
    is Result.Success -> transform(data)
    is Result.Error -> this
    is Result.Loading -> this
}

/**
 * Executes the given action if this result is successful
 */
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> = apply {
    if (this is Result.Success) action(data)
}

/**
 * Executes the given action if this result is an error
 */
inline fun <T> Result<T>.onError(action: (DomainException) -> Unit): Result<T> = apply {
    if (this is Result.Error) action(exception)
}

/**
 * Executes the given action if this result is loading
 */
inline fun <T> Result<T>.onLoading(action: () -> Unit): Result<T> = apply {
    if (this is Result.Loading) action()
}

/**
 * Returns the value if successful, or the result of the given function if error/loading
 */
inline fun <T> Result<T>.getOrElse(defaultValue: (Result<T>) -> T): T = when (this) {
    is Result.Success -> data
    else -> defaultValue(this)
}

/**
 * Returns the value if successful, or the given default value if error/loading
 */
fun <T> Result<T>.getOrDefault(defaultValue: T): T = when (this) {
    is Result.Success -> data
    else -> defaultValue
}

/**
 * Converts a nullable value to a Result
 */
fun <T> T?.toResult(error: DomainException = DomainException.Unknown("Value is null")): Result<T> =
    if (this != null) Result.Success(this) else Result.Error(error)

/**
 * Converts a throwable to a Result.Error
 */
fun <T> Throwable.toResult(): Result<T> = Result.Error(
    when (this) {
        is DomainException -> this
        else -> DomainException.Unknown(message ?: "Unknown error occurred")
    }
)

/**
 * Safe execution wrapper that catches exceptions and converts them to Result
 */
inline fun <T> resultOf(action: () -> T): Result<T> = try {
    Result.Success(action())
} catch (e: DomainException) {
    Result.Error(e)
} catch (e: Exception) {
    Result.Error(DomainException.Unknown(e.message ?: "Unknown error occurred"))
}

/**
 * Safe suspend execution wrapper that catches exceptions and converts them to Result
 */
suspend inline fun <T> suspendResultOf(crossinline action: suspend () -> T): Result<T> = try {
    Result.Success(action())
} catch (e: DomainException) {
    Result.Error(e)
} catch (e: Exception) {
    Result.Error(DomainException.Unknown(e.message ?: "Unknown error occurred"))
}

/**
 * Combines multiple Results into a single Result containing a list
 */
fun <T> List<Result<T>>.combineResults(): Result<List<T>> {
    val errors = filterIsInstance<Result.Error>()
    if (errors.isNotEmpty()) {
        // Return the first error found
        return errors.first()
    }
    
    if (any { it is Result.Loading }) {
        return Result.Loading
    }
    
    val successResults = filterIsInstance<Result.Success<T>>()
    return Result.Success(successResults.map { it.data })
}

/**
 * Zips two Results together
 */
inline fun <T1, T2, R> Result<T1>.zip(
    other: Result<T2>,
    transform: (T1, T2) -> R
): Result<R> = when {
    this is Result.Success && other is Result.Success -> Result.Success(transform(data, other.data))
    this is Result.Error -> this
    other is Result.Error -> other
    else -> Result.Loading
}