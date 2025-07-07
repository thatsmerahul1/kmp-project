package com.weather.features

import com.weather.domain.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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
 * Production feature toggle manager implementation
 */
class WeatherKMPFeatureToggleManager(
    private val localStorage: FeatureToggleStorage,
    private val remoteConfigProvider: RemoteConfigProvider? = null,
    private val analyticsProvider: FeatureAnalyticsProvider? = null,
    private val environment: Environment = Environment.DEVELOPMENT
) : FeatureToggleManager {
    
    private val featureUpdatesFlow = MutableStateFlow<FeatureUpdate?>(null)
    private val featuresMap = mutableMapOf<String, FeatureConfiguration>()
    private val activeABTests = mutableMapOf<String, ABTestConfiguration>()
    private var userSegment: String = "default"
    private var userId: String? = null
    
    override suspend fun initialize(): Result<Unit> {
        return try {
            // Load local configuration
            loadLocalConfiguration()
            
            // Load remote configuration if available
            remoteConfigProvider?.let { provider ->
                val remoteResult = provider.fetchConfiguration()
                if (remoteResult is Result.Success) {
                    mergeRemoteConfiguration(remoteResult.data)
                }
            }
            
            // Initialize A/B tests
            initializeABTests()
            
            // Record initialization
            analyticsProvider?.recordEvent(
                FeatureUsageEvent(
                    feature = "feature_toggle_system",
                    enabled = true,
                    sessionId = generateSessionId()
                )
            )
            
            println("🎛️ Feature toggle manager initialized with ${featuresMap.size} features")
            Result.Success(Unit)
            
        } catch (e: Exception) {
            Result.Error(Exception("Failed to initialize feature toggle manager: ${e.message}", e))
        }
    }
    
    override fun isFeatureEnabled(feature: String): Boolean {
        val config = featuresMap[feature] ?: return false
        return evaluateFeature(config)
    }
    
    override fun isFeatureEnabled(feature: FeatureFlag): Boolean {
        return isFeatureEnabled(feature.key)
    }
    
    override suspend fun enableFeature(feature: String): Result<Unit> {
        return updateFeature(feature, enabled = true)
    }
    
    override suspend fun disableFeature(feature: String): Result<Unit> {
        return updateFeature(feature, enabled = false)
    }
    
    override suspend fun setFeatureRolloutPercentage(feature: String, percentage: Int): Result<Unit> {
        return try {
            val config = featuresMap[feature] ?: return Result.Error(
                Exception("Feature not found: $feature")
            )
            
            val updatedConfig = config.copy(
                rolloutPercentage = percentage.coerceIn(0, 100),
                lastUpdated = Clock.System.now()
            )
            
            featuresMap[feature] = updatedConfig
            localStorage.saveFeature(updatedConfig)
            
            println("📊 Updated rollout percentage for $feature: $percentage%")
            Result.Success(Unit)
            
        } catch (e: Exception) {
            Result.Error(Exception("Failed to update rollout percentage: ${e.message}", e))
        }
    }
    
    override suspend fun refreshConfiguration(): Result<Unit> {
        return try {
            remoteConfigProvider?.let { provider ->
                when (val result = provider.fetchConfiguration()) {
                    is Result.Success -> {
                        mergeRemoteConfiguration(result.data)
                        println("🔄 Remote configuration refreshed")
                    }
                    is Result.Error -> {
                        println("⚠️ Failed to refresh remote configuration: ${result.exception.message}")
                    }
                }
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(Exception("Failed to refresh configuration: ${e.message}", e))
        }
    }
    
    override fun getFeatureUpdatesFlow(): Flow<FeatureUpdate> {
        return featureUpdatesFlow.asStateFlow().filterNotNull()
    }
    
    override fun getAllFeatures(): Map<String, FeatureConfiguration> {
        return featuresMap.toMap()
    }
    
    override suspend fun recordFeatureUsage(feature: String, context: Map<String, String>): Result<Unit> {
        return try {
            val enabled = isFeatureEnabled(feature)
            val event = FeatureUsageEvent(
                feature = feature,
                enabled = enabled,
                userId = userId,
                sessionId = generateSessionId(),
                context = context
            )
            
            analyticsProvider?.recordEvent(event)
            Result.Success(Unit)
            
        } catch (e: Exception) {
            Result.Error(Exception("Failed to record feature usage: ${e.message}", e))
        }
    }
    
    private fun loadLocalConfiguration() {
        // Initialize with default feature flags
        FeatureFlag.values().forEach { flag ->
            val existingConfig = localStorage.loadFeature(flag.key)
            val config = existingConfig ?: FeatureConfiguration(
                key = flag.key,
                enabled = flag.defaultEnabled,
                environment = listOf(environment.name.lowercase())
            )
            featuresMap[flag.key] = config
        }
        
        // Load any additional custom features
        val customFeatures = localStorage.loadAllFeatures()
        customFeatures.forEach { (key, config) ->
            if (!featuresMap.containsKey(key)) {
                featuresMap[key] = config
            }
        }
    }
    
    private suspend fun mergeRemoteConfiguration(remoteFeatures: Map<String, FeatureConfiguration>) {
        remoteFeatures.forEach { (key, remoteConfig) ->
            val localConfig = featuresMap[key]
            
            // Only update if remote config is newer
            if (localConfig == null || remoteConfig.lastUpdated > localConfig.lastUpdated) {
                val oldEnabled = localConfig?.enabled ?: false
                featuresMap[key] = remoteConfig
                
                // Save to local storage
                localStorage.saveFeature(remoteConfig)
                
                // Emit update if enabled state changed
                if (oldEnabled != remoteConfig.enabled) {
                    featureUpdatesFlow.value = FeatureUpdate(
                        feature = key,
                        oldValue = oldEnabled,
                        newValue = remoteConfig.enabled,
                        source = UpdateSource.REMOTE
                    )
                }
            }
        }
    }
    
    private fun initializeABTests() {
        // Load active A/B tests from storage or remote config
        val abTests = localStorage.loadABTests()
        val currentTime = Clock.System.now()
        
        abTests.forEach { test ->
            // Only consider active tests
            if (currentTime >= test.startDate && currentTime <= test.endDate) {
                activeABTests[test.testId] = test
                
                // Apply A/B test variant to feature
                val variant = selectABTestVariant(test)
                if (variant != null) {
                    val featureEnabled = test.variants[variant] ?: false
                    val existingConfig = featuresMap[test.feature]
                    
                    if (existingConfig != null) {
                        featuresMap[test.feature] = existingConfig.copy(
                            enabled = featureEnabled,
                            metadata = existingConfig.metadata + ("ab_test_variant" to variant)
                        )
                    }
                }
            }
        }
    }
    
    private fun selectABTestVariant(test: ABTestConfiguration): String? {
        // Simple hash-based variant selection for consistency
        val hash = (userId ?: "anonymous").hashCode()
        val bucket = Math.abs(hash % 100)
        
        var cumulative = 0
        test.trafficAllocation.forEach { (variant, percentage) ->
            cumulative += percentage
            if (bucket < cumulative) {
                return variant
            }
        }
        
        return null // User not in test
    }
    
    private fun evaluateFeature(config: FeatureConfiguration): Boolean {
        // Check if feature is globally enabled
        if (!config.enabled) return false
        
        // Check environment
        if (!config.environment.contains("all") && !config.environment.contains(environment.name.lowercase())) {
            return false
        }
        
        // Check time-based constraints
        val now = Clock.System.now()
        if (config.startDate != null && now < config.startDate) return false
        if (config.endDate != null && now > config.endDate) return false
        
        // Check rollout percentage
        if (config.rolloutPercentage < 100) {
            val hash = (userId ?: "anonymous").hashCode()
            val bucket = Math.abs(hash % 100)
            if (bucket >= config.rolloutPercentage) return false
        }
        
        // Check dependencies
        config.dependencies.forEach { dependency ->
            if (!isFeatureEnabled(dependency)) return false
        }
        
        return true
    }
    
    private suspend fun updateFeature(feature: String, enabled: Boolean): Result<Unit> {
        return try {
            val existingConfig = featuresMap[feature]
            val oldEnabled = existingConfig?.enabled ?: false
            
            val config = existingConfig?.copy(
                enabled = enabled,
                lastUpdated = Clock.System.now()
            ) ?: FeatureConfiguration(
                key = feature,
                enabled = enabled
            )
            
            featuresMap[feature] = config
            localStorage.saveFeature(config)
            
            // Emit update
            featureUpdatesFlow.value = FeatureUpdate(
                feature = feature,
                oldValue = oldEnabled,
                newValue = enabled,
                source = UpdateSource.LOCAL
            )
            
            println("🎛️ Feature $feature ${if (enabled) "enabled" else "disabled"}")
            Result.Success(Unit)
            
        } catch (e: Exception) {
            Result.Error(Exception("Failed to update feature: ${e.message}", e))
        }
    }
    
    private fun generateSessionId(): String = "session-${Clock.System.now().toEpochMilliseconds()}"
    
    // Configuration methods
    fun setUserId(userId: String) {
        this.userId = userId
    }
    
    fun setUserSegment(segment: String) {
        this.userSegment = segment
    }
}

/**
 * Storage interface for feature configurations
 */
interface FeatureToggleStorage {
    fun saveFeature(config: FeatureConfiguration)
    fun loadFeature(key: String): FeatureConfiguration?
    fun loadAllFeatures(): Map<String, FeatureConfiguration>
    fun saveABTest(test: ABTestConfiguration)
    fun loadABTests(): List<ABTestConfiguration>
    fun clearAll()
}

/**
 * Remote configuration provider interface
 */
interface RemoteConfigProvider {
    suspend fun fetchConfiguration(): Result<Map<String, FeatureConfiguration>>
    suspend fun fetchABTests(): Result<List<ABTestConfiguration>>
}

/**
 * Analytics provider for feature usage tracking
 */
interface FeatureAnalyticsProvider {
    suspend fun recordEvent(event: FeatureUsageEvent)
    suspend fun recordFeatureToggle(feature: String, enabled: Boolean, source: UpdateSource)
}

enum class Environment {
    DEVELOPMENT, STAGING, PRODUCTION
}

/**
 * Extension function to filter null values from flows
 */
private fun <T> Flow<T?>.filterNotNull(): Flow<T> = kotlinx.coroutines.flow.flow {
    collect { value ->
        if (value != null) {
            emit(value)
        }
    }
}

/**
 * Feature toggle utilities and convenience methods
 */
object FeatureToggleUtils {
    
    private lateinit var featureManager: FeatureToggleManager
    
    fun initialize(manager: FeatureToggleManager) {
        featureManager = manager
    }
    
    suspend fun isEnabled(feature: FeatureFlag): Boolean {
        return if (::featureManager.isInitialized) {
            featureManager.isFeatureEnabled(feature)
        } else {
            feature.defaultEnabled
        }
    }
    
    suspend fun isEnabled(feature: String): Boolean {
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
    
    suspend fun <T> withFeature(
        feature: FeatureFlag,
        enabledBlock: suspend () -> T,
        disabledBlock: suspend () -> T = { throw UnsupportedOperationException("Feature ${feature.key} is disabled") }
    ): T {
        return if (isEnabled(feature)) {
            recordUsage(feature)
            enabledBlock()
        } else {
            disabledBlock()
        }
    }
    
    fun getManager(): FeatureToggleManager? {
        return if (::featureManager.isInitialized) featureManager else null
    }
}

/**
 * Simple in-memory storage implementation for development
 */
class InMemoryFeatureToggleStorage : FeatureToggleStorage {
    
    private val features = mutableMapOf<String, FeatureConfiguration>()
    private val abTests = mutableListOf<ABTestConfiguration>()
    
    override fun saveFeature(config: FeatureConfiguration) {
        features[config.key] = config
    }
    
    override fun loadFeature(key: String): FeatureConfiguration? {
        return features[key]
    }
    
    override fun loadAllFeatures(): Map<String, FeatureConfiguration> {
        return features.toMap()
    }
    
    override fun saveABTest(test: ABTestConfiguration) {
        abTests.removeAll { it.testId == test.testId }
        abTests.add(test)
    }
    
    override fun loadABTests(): List<ABTestConfiguration> {
        return abTests.toList()
    }
    
    override fun clearAll() {
        features.clear()
        abTests.clear()
    }
}

/**
 * Mock remote config provider for development
 */
class MockRemoteConfigProvider : RemoteConfigProvider {
    
    override suspend fun fetchConfiguration(): Result<Map<String, FeatureConfiguration>> {
        // Simulate network delay
        kotlinx.coroutines.delay(1000)
        
        // Return some mock remote features
        val remoteFeatures = mapOf(
            "premium_features" to FeatureConfiguration(
                key = "premium_features",
                enabled = false,
                rolloutPercentage = 25,
                environment = listOf("production")
            ),
            "ai_weather_predictions" to FeatureConfiguration(
                key = "ai_weather_predictions",
                enabled = true,
                rolloutPercentage = 10,
                environment = listOf("development", "staging")
            )
        )
        
        return Result.Success(remoteFeatures)
    }
    
    override suspend fun fetchABTests(): Result<List<ABTestConfiguration>> {
        // Mock A/B test
        val abTest = ABTestConfiguration(
            testId = "weather_ui_test",
            feature = "weather_animations",
            variants = mapOf(
                "control" to false,
                "treatment" to true
            ),
            trafficAllocation = mapOf(
                "control" to 50,
                "treatment" to 50
            ),
            startDate = Clock.System.now(),
            endDate = Clock.System.now().plus(kotlinx.datetime.DateTimeUnit.DAY * 30)
        )
        
        return Result.Success(listOf(abTest))
    }
}

/**
 * Console analytics provider for development
 */
class ConsoleFeatureAnalyticsProvider : FeatureAnalyticsProvider {
    
    override suspend fun recordEvent(event: FeatureUsageEvent) {
        println("📊 Feature Usage: ${event.feature} = ${event.enabled} @ ${event.timestamp}")
    }
    
    override suspend fun recordFeatureToggle(feature: String, enabled: Boolean, source: UpdateSource) {
        println("🎛️ Feature Toggle: $feature = $enabled (source: $source)")
    }
}