package com.weather.features

import com.weather.domain.common.Result
import com.weather.domain.common.DomainException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Feature Toggle Management System for WeatherKMP 2025
 * 
 * Features:
 * - Dynamic feature enabling/disabling
 * - Remote configuration support
 * - A/B testing capabilities
 * - Gradual rollout support
 * - Environment-specific configurations
 * - Real-time feature updates
 * - Analytics integration
 */

interface FeatureToggleManager {
    suspend fun initialize(): Result<Unit>
    fun isFeatureEnabled(feature: String): Boolean
    fun isFeatureEnabled(feature: FeatureFlag): Boolean
    suspend fun enableFeature(feature: String): Result<Unit>
    suspend fun disableFeature(feature: String): Result<Unit>
    suspend fun setFeatureRolloutPercentage(feature: String, percentage: Int): Result<Unit>
    suspend fun refreshConfiguration(): Result<Unit>
    fun getFeatureUpdatesFlow(): Flow<FeatureUpdate>
    fun getAllFeatures(): Map<String, FeatureConfiguration>
    suspend fun recordFeatureUsage(feature: String, context: Map<String, String> = emptyMap()): Result<Unit>
}

/**
 * Feature flags enumeration for type safety
 */
enum class FeatureFlag(val key: String, val defaultEnabled: Boolean = false) {
    // Security Features
    API_KEY_ENCRYPTION("api_key_encryption", true),
    CERTIFICATE_PINNING("certificate_pinning", true),
    BIOMETRIC_AUTH("biometric_auth", false),
    
    // UI Features
    COMPOSE_MULTIPLATFORM("compose_multiplatform", true),
    DARK_MODE("dark_mode", true),
    WEATHER_ANIMATIONS("weather_animations", true),
    LOCATION_SEARCH("location_search", true),
    WEATHER_MAPS("weather_maps", false),
    
    // Performance Features
    TELEMETRY_MONITORING("telemetry_monitoring", true),
    CRASH_ANALYTICS("crash_analytics", true),
    PERFORMANCE_MONITORING("performance_monitoring", true),
    MEMORY_OPTIMIZATION("memory_optimization", false),
    
    // Business Features
    WEATHER_ALERTS("weather_alerts", true),
    PUSH_NOTIFICATIONS("push_notifications", false),
    PREMIUM_FEATURES("premium_features", false),
    WEATHER_WIDGETS("weather_widgets", true),
    OFFLINE_MODE("offline_mode", true),
    
    // Experimental Features
    AI_WEATHER_PREDICTIONS("ai_weather_predictions", false),
    VOICE_COMMANDS("voice_commands", false),
    AR_WEATHER_VIEW("ar_weather_view", false),
    WEATHER_SOCIAL_SHARING("weather_social_sharing", false),
    
    // Development Features
    DEBUG_LOGGING("debug_logging", false),
    MOCK_DATA("mock_data", false),
    PERFORMANCE_OVERLAY("performance_overlay", false),
    FEATURE_SHOWCASE("feature_showcase", false);
    
    companion object {
        fun fromKey(key: String): FeatureFlag? = values().find { it.key == key }
    }
}

/**
 * Feature configuration with advanced options
 */
@Serializable
data class FeatureConfiguration(
    val key: String,
    val enabled: Boolean,
    val rolloutPercentage: Int = 100, // 0-100, for gradual rollouts
    val targetAudience: List<String> = emptyList(), // user segments
    val environment: List<String> = listOf("all"), // dev, staging, prod, all
    val minimumAppVersion: String? = null,
    val maximumAppVersion: String? = null,
    val startDate: Instant? = null,
    val endDate: Instant? = null,
    val dependencies: List<String> = emptyList(), // other features this depends on
    val metadata: Map<String, String> = emptyMap(),
    val lastUpdated: Instant = Clock.System.now()
)

/**
 * Feature update notification
 */
@Serializable
data class FeatureUpdate(
    val feature: String,
    val oldValue: Boolean,
    val newValue: Boolean,
    val timestamp: Instant = Clock.System.now(),
    val source: UpdateSource = UpdateSource.LOCAL
)

enum class UpdateSource {
    LOCAL, REMOTE, A_B_TEST, ADMIN_OVERRIDE
}

/**
 * A/B test configuration
 */
@Serializable
data class ABTestConfiguration(
    val testId: String,
    val feature: String,
    val variants: Map<String, Boolean>, // variant name -> feature enabled
    val trafficAllocation: Map<String, Int>, // variant name -> percentage
    val startDate: Instant,
    val endDate: Instant,
    val targetAudience: List<String> = emptyList()
)

/**
 * Feature usage analytics
 */
@Serializable
data class FeatureUsageEvent(
    val feature: String,
    val enabled: Boolean,
    val timestamp: Instant = Clock.System.now(),
    val userId: String? = null,
    val sessionId: String,
    val context: Map<String, String> = emptyMap()
)

/**
 * Simple feature toggle storage interface
 */
interface FeatureToggleStorage {
    suspend fun getFeatureConfiguration(key: String): FeatureConfiguration?
    suspend fun setFeatureConfiguration(config: FeatureConfiguration): Result<Unit>
    suspend fun getAllConfigurations(): Map<String, FeatureConfiguration>
    suspend fun clearConfiguration(key: String): Result<Unit>
}

/**
 * In-memory feature toggle storage implementation
 */
class InMemoryFeatureToggleStorage : FeatureToggleStorage {
    private val storage = mutableMapOf<String, FeatureConfiguration>()
    
    override suspend fun getFeatureConfiguration(key: String): FeatureConfiguration? {
        return storage[key]
    }
    
    override suspend fun setFeatureConfiguration(config: FeatureConfiguration): Result<Unit> {
        return try {
            storage[config.key] = config
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DomainException.Configuration.InvalidConfig(config.key, e.message ?: "Storage error"))
        }
    }
    
    override suspend fun getAllConfigurations(): Map<String, FeatureConfiguration> {
        return storage.toMap()
    }
    
    override suspend fun clearConfiguration(key: String): Result<Unit> {
        return try {
            storage.remove(key)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DomainException.Configuration.InvalidConfig(key, e.message ?: "Clear error"))
        }
    }
}

/**
 * Production feature toggle manager implementation
 */
class WeatherKMPFeatureToggleManager(
    private val localStorage: FeatureToggleStorage = InMemoryFeatureToggleStorage()
) : FeatureToggleManager {
    
    private val featuresStateFlow = MutableStateFlow<Map<String, FeatureConfiguration>>(emptyMap())
    private val featureUpdatesFlow = MutableStateFlow<FeatureUpdate?>(null)
    private val currentEnvironment = "dev" // Would be configurable
    private val currentAppVersion = "2025.1.0"
    
    override suspend fun initialize(): Result<Unit> {
        return try {
            // Initialize with default feature flags
            FeatureFlag.values().forEach { flag ->
                val existingConfig = localStorage.getFeatureConfiguration(flag.key)
                if (existingConfig == null) {
                    val defaultConfig = FeatureConfiguration(
                        key = flag.key,
                        enabled = flag.defaultEnabled,
                        environment = listOf("all")
                    )
                    localStorage.setFeatureConfiguration(defaultConfig)
                }
            }
            
            // Load all configurations
            val allConfigs = localStorage.getAllConfigurations()
            featuresStateFlow.value = allConfigs
            
            println("🎛️ Feature toggle manager initialized with ${allConfigs.size} features")
            Result.Success(Unit)
            
        } catch (e: Exception) {
            Result.Error(DomainException.Configuration.InvalidConfig("initialization", e.message ?: "Init failed"))
        }
    }
    
    override fun isFeatureEnabled(feature: String): Boolean {
        return try {
            val config = featuresStateFlow.value[feature] ?: return false
            evaluateFeatureConfiguration(config)
        } catch (e: Exception) {
            false
        }
    }
    
    override fun isFeatureEnabled(feature: FeatureFlag): Boolean {
        return isFeatureEnabled(feature.key)
    }
    
    override suspend fun enableFeature(feature: String): Result<Unit> {
        return updateFeatureState(feature, true)
    }
    
    override suspend fun disableFeature(feature: String): Result<Unit> {
        return updateFeatureState(feature, false)
    }
    
    override suspend fun setFeatureRolloutPercentage(feature: String, percentage: Int): Result<Unit> {
        return try {
            if (percentage !in 0..100) {
                return Result.Error(DomainException.Configuration.InvalidConfig(feature, "Percentage must be 0-100"))
            }
            
            val currentConfig = featuresStateFlow.value[feature]
                ?: return Result.Error(DomainException.Configuration.MissingConfig(feature))
            
            val updatedConfig = currentConfig.copy(
                rolloutPercentage = percentage,
                lastUpdated = Clock.System.now()
            )
            
            localStorage.setFeatureConfiguration(updatedConfig)
            updateLocalState()
            
            Result.Success(Unit)
            
        } catch (e: Exception) {
            Result.Error(DomainException.Configuration.InvalidConfig(feature, e.message ?: "Rollout update failed"))
        }
    }
    
    override suspend fun refreshConfiguration(): Result<Unit> {
        return try {
            // In production, this would fetch from remote configuration service
            updateLocalState()
            println("🔄 Feature configuration refreshed")
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DomainException.Configuration.InvalidConfig("refresh", e.message ?: "Refresh failed"))
        }
    }
    
    override fun getFeatureUpdatesFlow(): Flow<FeatureUpdate> {
        return kotlinx.coroutines.flow.flow {
            featureUpdatesFlow.collect { update ->
                if (update != null) {
                    emit(update)
                }
            }
        }
    }
    
    override fun getAllFeatures(): Map<String, FeatureConfiguration> {
        return featuresStateFlow.value
    }
    
    override suspend fun recordFeatureUsage(feature: String, context: Map<String, String>): Result<Unit> {
        return try {
            val usageEvent = FeatureUsageEvent(
                feature = feature,
                enabled = isFeatureEnabled(feature),
                sessionId = "session_${Clock.System.now().toEpochMilliseconds()}",
                context = context
            )
            
            // In production, this would send to analytics service
            println("📊 Feature usage: $feature (enabled: ${usageEvent.enabled})")
            
            Result.Success(Unit)
            
        } catch (e: Exception) {
            Result.Error(DomainException.Configuration.InvalidConfig(feature, e.message ?: "Usage recording failed"))
        }
    }
    
    private suspend fun updateFeatureState(feature: String, enabled: Boolean): Result<Unit> {
        return try {
            val currentConfig = featuresStateFlow.value[feature]
            val oldValue = currentConfig?.enabled ?: false
            
            val updatedConfig = (currentConfig ?: FeatureConfiguration(feature, false)).copy(
                enabled = enabled,
                lastUpdated = Clock.System.now()
            )
            
            localStorage.setFeatureConfiguration(updatedConfig)
            updateLocalState()
            
            // Emit feature update
            featureUpdatesFlow.value = FeatureUpdate(
                feature = feature,
                oldValue = oldValue,
                newValue = enabled,
                source = UpdateSource.LOCAL
            )
            
            println("🎛️ Feature $feature ${if (enabled) "enabled" else "disabled"}")
            Result.Success(Unit)
            
        } catch (e: Exception) {
            Result.Error(DomainException.Configuration.InvalidConfig(feature, e.message ?: "State update failed"))
        }
    }
    
    private suspend fun updateLocalState() {
        val allConfigs = localStorage.getAllConfigurations()
        featuresStateFlow.value = allConfigs
    }
    
    private fun evaluateFeatureConfiguration(config: FeatureConfiguration): Boolean {
        // Check if feature is explicitly disabled
        if (!config.enabled) return false
        
        // Check environment
        if (!config.environment.contains("all") && !config.environment.contains(currentEnvironment)) {
            return false
        }
        
        // Check app version constraints
        if (config.minimumAppVersion != null && !isVersionAtLeast(currentAppVersion, config.minimumAppVersion)) {
            return false
        }
        
        if (config.maximumAppVersion != null && isVersionAtLeast(currentAppVersion, config.maximumAppVersion)) {
            return false
        }
        
        // Check date constraints
        val now = Clock.System.now()
        if (config.startDate != null && now < config.startDate) {
            return false
        }
        
        if (config.endDate != null && now > config.endDate) {
            return false
        }
        
        // Check rollout percentage (simplified random check)
        if (config.rolloutPercentage < 100) {
            val random = (0..100).random()
            if (random > config.rolloutPercentage) {
                return false
            }
        }
        
        // Check dependencies
        for (dependency in config.dependencies) {
            if (!isFeatureEnabled(dependency)) {
                return false
            }
        }
        
        return true
    }
    
    private fun isVersionAtLeast(current: String, minimum: String): Boolean {
        // Simplified version comparison
        return try {
            val currentParts = current.split(".").map { it.toInt() }
            val minimumParts = minimum.split(".").map { it.toInt() }
            
            for (i in 0 until maxOf(currentParts.size, minimumParts.size)) {
                val currentPart = currentParts.getOrNull(i) ?: 0
                val minimumPart = minimumParts.getOrNull(i) ?: 0
                
                when {
                    currentPart > minimumPart -> return true
                    currentPart < minimumPart -> return false
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * Feature toggle utilities for easy integration
 */
object FeatureToggleUtils {
    
    private lateinit var featureManager: FeatureToggleManager
    
    fun initialize(manager: FeatureToggleManager) {
        featureManager = manager
    }
    
    fun isEnabled(feature: FeatureFlag): Boolean {
        return if (::featureManager.isInitialized) {
            featureManager.isFeatureEnabled(feature)
        } else {
            feature.defaultEnabled
        }
    }
    
    fun isEnabled(feature: String): Boolean {
        return if (::featureManager.isInitialized) {
            featureManager.isFeatureEnabled(feature)
        } else {
            false
        }
    }
    
    suspend fun recordUsage(feature: FeatureFlag, context: Map<String, String> = emptyMap()) {
        if (::featureManager.isInitialized) {
            featureManager.recordFeatureUsage(feature.key, context)
        }
    }
    
    suspend fun recordUsage(feature: String, context: Map<String, String> = emptyMap()) {
        if (::featureManager.isInitialized) {
            featureManager.recordFeatureUsage(feature, context)
        }
    }
}

