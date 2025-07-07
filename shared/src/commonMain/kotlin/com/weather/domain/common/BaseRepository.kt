package com.weather.domain.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Base interface for all Repository implementations following 2025 Clean Architecture standards
 * 
 * This base interface provides:
 * - Consistent Result wrapper usage
 * - Standard CRUD operations pattern
 * - Cache management hooks
 * - Error handling consistency
 * - Performance monitoring integration
 */
interface BaseRepository {
    
    /**
     * Clear all cached data for this repository
     * Useful for logout scenarios or cache invalidation
     */
    suspend fun clearCache(): Result<Unit> = resultOf {
        onClearCache()
    }
    
    /**
     * Refresh cached data from remote source
     * Useful for pull-to-refresh scenarios
     */
    suspend fun refresh(): Result<Unit> = resultOf {
        onRefresh()
    }
    
    /**
     * Hook for repository-specific cache clearing logic
     * Override in concrete implementations
     */
    suspend fun onClearCache() {
        // Default implementation does nothing
    }
    
    /**
     * Hook for repository-specific refresh logic
     * Override in concrete implementations
     */
    suspend fun onRefresh() {
        // Default implementation does nothing
    }
}

/**
 * Base class for Repository implementations that provides common functionality
 */
abstract class BaseRepositoryImpl : BaseRepository {
    
    /**
     * Handle network errors and convert them to domain exceptions
     */
    protected fun handleNetworkError(throwable: Throwable): DomainException = when (throwable) {
        is DomainException -> throwable
        else -> when (throwable::class.simpleName) {
            "UnknownHostException" -> DomainException.Network.NoConnection
            "SocketTimeoutException" -> DomainException.Network.Timeout(30000)
            "SSLException" -> DomainException.Network.SSLError(throwable.message ?: "SSL error")
            else -> DomainException.Network.Generic(throwable.message ?: "Network error")
        }
    }
    
    /**
     * Handle storage/database errors and convert them to domain exceptions
     */
    protected fun handleStorageError(throwable: Throwable, operation: String): DomainException = when (throwable) {
        is DomainException -> throwable
        else -> DomainException.Storage.DatabaseError(operation, throwable.message ?: "Database error")
    }
    
    /**
     * Safe network operation wrapper
     */
    protected suspend inline fun <T> safeNetworkCall(
        crossinline operation: suspend () -> T
    ): Result<T> = try {
        Result.Success(operation())
    } catch (e: Exception) {
        Result.Error(handleNetworkError(e))
    }
    
    /**
     * Safe storage operation wrapper
     */
    protected suspend inline fun <T> safeStorageCall(
        operationName: String,
        crossinline operation: suspend () -> T
    ): Result<T> = try {
        Result.Success(operation())
    } catch (e: Exception) {
        Result.Error(handleStorageError(e, operationName))
    }
    
    /**
     * Safe Flow operation wrapper for network calls
     */
    protected fun <T> Flow<T>.safeNetworkFlow(): Flow<Result<T>> = 
        map<T, Result<T>> { Result.Success(it) }
            .catch { emit(Result.Error(handleNetworkError(it))) }
    
    /**
     * Safe Flow operation wrapper for storage calls
     */
    protected fun <T> Flow<T>.safeStorageFlow(operationName: String): Flow<Result<T>> = 
        map<T, Result<T>> { Result.Success(it) }
            .catch { emit(Result.Error(handleStorageError(it, operationName))) }
}

/**
 * Interface for repositories that manage cached data
 */
interface CacheableRepository<T> : BaseRepository {
    
    /**
     * Check if cached data is valid/fresh
     */
    suspend fun isCacheValid(): Boolean
    
    /**
     * Get cache expiry time in milliseconds
     */
    suspend fun getCacheExpiryTime(): Long
    
    /**
     * Update cache with new data
     */
    suspend fun updateCache(data: T): Result<Unit>
    
    /**
     * Get data from cache only
     */
    suspend fun getCachedData(): Result<T?>
    
    /**
     * Get data with cache-first strategy
     * Returns cached data if valid, otherwise fetches from remote
     */
    suspend fun getDataWithCacheFirst(): Result<T>
    
    /**
     * Get data with network-first strategy
     * Fetches from remote, falls back to cache if network fails
     */
    suspend fun getDataWithNetworkFirst(): Result<T>
}

/**
 * Base implementation for cacheable repositories
 */
abstract class BaseCacheableRepositoryImpl<T> : BaseRepositoryImpl(), CacheableRepository<T> {
    
    /**
     * Default cache validity duration (24 hours)
     * Override in concrete implementations for different durations
     */
    protected open val cacheValidityDurationMs: Long = 60 * 1000L // 24 hours
    
    /**
     * Abstract method to fetch data from remote source
     */
    protected abstract suspend fun fetchFromRemote(): Result<T>
    
    /**
     * Abstract method to get last cache update time
     */
    protected abstract suspend fun getLastCacheUpdateTime(): Long
    
    /**
     * Implementation of cache validity check
     */
    override suspend fun isCacheValid(): Boolean {
        val lastUpdate = getLastCacheUpdateTime()
        val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        return (now - lastUpdate) < cacheValidityDurationMs
    }
    
    /**
     * Implementation of cache expiry time calculation
     */
    override suspend fun getCacheExpiryTime(): Long {
        val lastUpdate = getLastCacheUpdateTime()
        return lastUpdate + cacheValidityDurationMs
    }
    
    /**
     * Cache-first data retrieval strategy
     */
    override suspend fun getDataWithCacheFirst(): Result<T> {
        // First try to get cached data if it's valid
        if (isCacheValid()) {
            getCachedData().getOrNull()?.let { cachedData ->
                return Result.Success(cachedData)
            }
        }
        
        // If cache is invalid or empty, fetch from remote
        return fetchFromRemote().onSuccess { remoteData ->
            updateCache(remoteData)
        }
    }
    
    /**
     * Network-first data retrieval strategy
     */
    override suspend fun getDataWithNetworkFirst(): Result<T> {
        // First try to fetch from remote
        return when (val remoteResult = fetchFromRemote()) {
            is Result.Success -> {
                // Update cache with fresh data
                updateCache(remoteResult.data)
                remoteResult
            }
            is Result.Error -> {
                // If network fails, try to return cached data
                getCachedData().flatMap { cachedData ->
                    if (cachedData != null) {
                        Result.Success(cachedData)
                    } else {
                        remoteResult // Return the original network error
                    }
                }
            }
            is Result.Loading -> remoteResult
        }
    }
}

/**
 * Interface for repositories that support pagination
 */
interface PaginatedRepository<T> : BaseRepository {
    
    /**
     * Get paginated data
     */
    suspend fun getPage(
        page: Int,
        pageSize: Int,
        refresh: Boolean = false
    ): Result<PaginatedResult<T>>
    
    /**
     * Get next page of data
     */
    suspend fun getNextPage(): Result<PaginatedResult<T>>
    
    /**
     * Check if there are more pages available
     */
    suspend fun hasMorePages(): Boolean
    
    /**
     * Reset pagination state
     */
    suspend fun resetPagination()
}

/**
 * Data class representing paginated results
 */
data class PaginatedResult<T>(
    val data: List<T>,
    val currentPage: Int,
    val totalPages: Int,
    val totalItems: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean
)

/**
 * Interface for repositories that support searching
 */
interface SearchableRepository<T> : BaseRepository {
    
    /**
     * Search for items with the given query
     */
    suspend fun search(
        query: String,
        filters: Map<String, Any> = emptyMap()
    ): Result<List<T>>
    
    /**
     * Get search suggestions for the given query
     */
    suspend fun getSearchSuggestions(query: String): Result<List<String>>
    
    /**
     * Clear search history/cache
     */
    suspend fun clearSearchHistory(): Result<Unit>
}

/**
 * Extension functions for Repository patterns
 */

/**
 * Execute repository operation with retry logic
 */
suspend fun <T> BaseRepository.withRetry(
    maxRetries: Int = 3,
    delayMs: Long = 1000,
    operation: suspend () -> Result<T>
): Result<T> {
    repeat(maxRetries) { attempt ->
        when (val result = operation()) {
            is Result.Success -> return result
            is Result.Error -> {
                if (attempt == maxRetries - 1 || !result.exception.isRetryable) {
                    return result
                }
                kotlinx.coroutines.delay(delayMs * (attempt + 1))
            }
            is Result.Loading -> return result
        }
    }
    return Result.Error(DomainException.Unknown("Max retries exceeded"))
}

/**
 * Combine results from multiple repositories
 */
suspend fun <T> combineRepositoryResults(
    vararg repositories: suspend () -> Result<T>
): Result<List<T>> {
    val results = repositories.map { it() }
    return results.combineResults()
}