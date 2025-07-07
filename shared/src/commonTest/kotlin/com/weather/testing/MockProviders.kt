package com.weather.testing

import com.weather.domain.repository.WeatherRepository
import com.weather.domain.model.Weather
import com.weather.domain.model.WeatherCondition
import com.weather.security.*
import com.weather.monitoring.*
import com.weather.features.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Clock

/**
 * Cross-platform Mock Providers for KMP Testing
 * 
 * Provides mocks that work across all platforms without MockK dependency
 * in common test source sets. For Android-specific tests, use MockK directly.
 */

/**
 * Weather domain mocks
 */
class FakeWeatherRepository(
    private var shouldReturnError: Boolean = false,
    private var forecastResult: com.weather.domain.common.Result<List<Weather>> = com.weather.domain.common.Result.Success(listOf(aWeather().build()))
) : WeatherRepository {
    
    var getForecastCallCount = 0
        private set
    var refreshCallCount = 0
        private set
    var clearCacheCallCount = 0
        private set
    
    private var cachedData: List<Weather>? = null
    
    override fun getWeatherForecast(): Flow<com.weather.domain.common.Result<List<Weather>>> {
        getForecastCallCount++
        return flowOf(if (shouldReturnError) {
            com.weather.domain.common.Result.Error(com.weather.domain.common.DomainException.Network.Generic("Test error"))
        } else {
            forecastResult
        })
    }
    
    override suspend fun refreshWeatherForecast(): com.weather.domain.common.Result<List<Weather>> {
        refreshCallCount++
        return if (shouldReturnError) {
            com.weather.domain.common.Result.Error(com.weather.domain.common.DomainException.Network.Generic("Test error"))
        } else {
            forecastResult
        }
    }
    
    override suspend fun clearCache() {
        clearCacheCallCount++
        cachedData = null
    }
    
    // Test helpers
    fun setForecastResult(result: com.weather.domain.common.Result<List<Weather>>) {
        forecastResult = result
    }
    
    fun setForecastResult(result: kotlin.Result<List<Weather>>) {
        forecastResult = if (result.isSuccess) {
            com.weather.domain.common.Result.Success(result.getOrNull()!!)
        } else {
            com.weather.domain.common.Result.Error(com.weather.domain.common.DomainException.Unknown(result.exceptionOrNull()?.message ?: "Unknown error"))
        }
    }
    
    fun setCachedData(data: List<Weather>) {
        cachedData = data
        forecastResult = com.weather.domain.common.Result.Success(data)
    }
    
    fun reset() {
        getForecastCallCount = 0
        refreshCallCount = 0
        clearCacheCallCount = 0
        cachedData = null
    }
}

/**
 * Security domain mocks
 */
class FakeEncryptionProvider(
    private var encryptResult: String? = "encrypted_data",
    private var decryptResult: String? = "decrypted_data",
    private var hashResult: String = "test_hash",
    private var saltResult: String = "test_salt",
    private var keyResult: String = "test_key",
    private var validationResult: com.weather.domain.common.Result<Boolean> = com.weather.domain.common.Result.Success(true)
) : EncryptionProvider {
    
    var encryptCallCount = 0
        private set
    var decryptCallCount = 0
        private set
    var generateHashCallCount = 0
        private set
    var generateSaltCallCount = 0
        private set
    var deriveKeyCallCount = 0
        private set
    var validateCallCount = 0
        private set
    
    var lastEncryptedData: String? = null
        private set
    var lastDecryptedData: String? = null
        private set
    var lastHashData: String? = null
        private set
    
    override fun encrypt(data: String): String? {
        encryptCallCount++
        lastEncryptedData = data
        return encryptResult
    }
    
    override fun decrypt(encryptedData: String): String? {
        decryptCallCount++
        lastDecryptedData = encryptedData
        return decryptResult
    }
    
    override fun generateHash(data: String): String {
        generateHashCallCount++
        lastHashData = data
        return hashResult
    }
    
    override fun generateSalt(): String {
        generateSaltCallCount++
        return saltResult
    }
    
    override fun deriveKeyFromContext(): String {
        deriveKeyCallCount++
        return keyResult
    }
    
    override suspend fun validateSecurityContext(): com.weather.domain.common.Result<Boolean> {
        validateCallCount++
        return validationResult
    }
    
    // Test helpers
    fun setEncryptResult(result: String?) {
        encryptResult = result
    }
    
    fun setDecryptResult(result: String?) {
        decryptResult = result
    }
    
    fun setValidationResult(result: com.weather.domain.common.Result<Boolean>) {
        validationResult = result
    }
    
    fun reset() {
        encryptCallCount = 0
        decryptCallCount = 0
        generateHashCallCount = 0
        generateSaltCallCount = 0
        deriveKeyCallCount = 0
        validateCallCount = 0
        lastEncryptedData = null
        lastDecryptedData = null
        lastHashData = null
    }
}

class FakeSecureStorage(
    private val storage: MutableMap<String, String> = mutableMapOf(),
    private var storeResult: Boolean = true,
    private var deleteResult: Boolean = true,
    private var clearResult: Boolean = true,
    private var validationResult: com.weather.domain.common.Result<Boolean> = com.weather.domain.common.Result.Success(true)
) : SecureStorage {
    
    var storeCallCount = 0
        private set
    var retrieveCallCount = 0
        private set
    var deleteCallCount = 0
        private set
    var clearCallCount = 0
        private set
    var existsCallCount = 0
        private set
    var validateCallCount = 0
        private set
    
    override fun store(key: String, value: String): Boolean {
        storeCallCount++
        return if (storeResult) {
            storage[key] = value
            true
        } else {
            false
        }
    }
    
    override fun retrieve(key: String): String? {
        retrieveCallCount++
        return storage[key]
    }
    
    override fun delete(key: String): Boolean {
        deleteCallCount++
        return if (deleteResult) {
            storage.remove(key)
            true
        } else {
            false
        }
    }
    
    override fun clear(): Boolean {
        clearCallCount++
        return if (clearResult) {
            storage.clear()
            true
        } else {
            false
        }
    }
    
    override fun exists(key: String): Boolean {
        existsCallCount++
        return storage.containsKey(key)
    }
    
    override suspend fun validateStorageIntegrity(): com.weather.domain.common.Result<Boolean> {
        validateCallCount++
        return validationResult
    }
    
    // Test helpers
    fun setStoreResult(result: Boolean) {
        storeResult = result
    }
    
    fun setDeleteResult(result: Boolean) {
        deleteResult = result
    }
    
    fun setClearResult(result: Boolean) {
        clearResult = result
    }
    
    fun setValidationResult(result: com.weather.domain.common.Result<Boolean>) {
        validationResult = result
    }
    
    fun getStorageContents(): Map<String, String> = storage.toMap()
    
    fun reset() {
        storage.clear()
        storeCallCount = 0
        retrieveCallCount = 0
        deleteCallCount = 0
        clearCallCount = 0
        existsCallCount = 0
        validateCallCount = 0
    }
}

/**
 * Monitoring domain mocks
 */
class FakeTelemetryProvider(
    private var recordMetricResult: com.weather.domain.common.Result<Unit> = com.weather.domain.common.Result.Success(Unit),
    private var startSpanResult: com.weather.domain.common.Result<SpanContext> = com.weather.domain.common.Result.Success(SpanContext("trace", "span", Clock.System.now())),
    private var endSpanResult: com.weather.domain.common.Result<Unit> = com.weather.domain.common.Result.Success(Unit),
    private var recordEventResult: com.weather.domain.common.Result<Unit> = com.weather.domain.common.Result.Success(Unit),
    private var flushResult: com.weather.domain.common.Result<Unit> = com.weather.domain.common.Result.Success(Unit)
) : TelemetryProvider {
    
    var recordMetricCallCount = 0
        private set
    var startSpanCallCount = 0
        private set
    var endSpanCallCount = 0
        private set
    var recordEventCallCount = 0
        private set
    var flushCallCount = 0
        private set
    
    private val recordedMetrics = mutableListOf<TelemetryMetric>()
    private val recordedSpans = mutableListOf<TelemetrySpan>()
    private val recordedEvents = mutableListOf<TelemetryEvent>()
    
    override suspend fun recordMetric(metric: TelemetryMetric): com.weather.domain.common.Result<Unit> {
        recordMetricCallCount++
        recordedMetrics.add(metric)
        return recordMetricResult
    }
    
    override suspend fun startSpan(span: TelemetrySpan): com.weather.domain.common.Result<SpanContext> {
        startSpanCallCount++
        recordedSpans.add(span)
        return startSpanResult
    }
    
    override suspend fun endSpan(context: SpanContext, result: SpanResult): com.weather.domain.common.Result<Unit> {
        endSpanCallCount++
        return endSpanResult
    }
    
    override suspend fun recordEvent(event: TelemetryEvent): com.weather.domain.common.Result<Unit> {
        recordEventCallCount++
        recordedEvents.add(event)
        return recordEventResult
    }
    
    override fun getMetricsFlow(): Flow<TelemetryMetric> = flowOf()
    
    override fun getSpansFlow(): Flow<TelemetrySpan> = flowOf()
    
    override suspend fun flush(): com.weather.domain.common.Result<Unit> {
        flushCallCount++
        return flushResult
    }
    
    // Test helpers
    fun getRecordedMetrics(): List<TelemetryMetric> = recordedMetrics.toList()
    fun getRecordedSpans(): List<TelemetrySpan> = recordedSpans.toList()
    fun getRecordedEvents(): List<TelemetryEvent> = recordedEvents.toList()
    
    fun setRecordMetricResult(result: com.weather.domain.common.Result<Unit>) {
        recordMetricResult = result
    }
    
    fun setStartSpanResult(result: com.weather.domain.common.Result<SpanContext>) {
        startSpanResult = result
    }
    
    fun reset() {
        recordMetricCallCount = 0
        startSpanCallCount = 0
        endSpanCallCount = 0
        recordEventCallCount = 0
        flushCallCount = 0
        recordedMetrics.clear()
        recordedSpans.clear()
        recordedEvents.clear()
    }
}

class FakeMetricsCollector(
    private var startResult: com.weather.domain.common.Result<Unit> = com.weather.domain.common.Result.Success(Unit),
    private var stopResult: com.weather.domain.common.Result<Unit> = com.weather.domain.common.Result.Success(Unit),
    private var snapshotResult: com.weather.domain.common.Result<MetricsSnapshot> = com.weather.domain.common.Result.Success(createTestSnapshot()),
    private var recordCustomResult: com.weather.domain.common.Result<Unit> = com.weather.domain.common.Result.Success(Unit)
) : MetricsCollector {
    
    var startCallCount = 0
        private set
    var stopCallCount = 0
        private set
    var snapshotCallCount = 0
        private set
    var recordCustomCallCount = 0
        private set
    
    private val customMetrics = mutableMapOf<String, Pair<Double, Map<String, String>>>()
    
    override suspend fun startCollection(): com.weather.domain.common.Result<Unit> {
        startCallCount++
        return startResult
    }
    
    override suspend fun stopCollection(): com.weather.domain.common.Result<Unit> {
        stopCallCount++
        return stopResult
    }
    
    override suspend fun collectSnapshot(): com.weather.domain.common.Result<MetricsSnapshot> {
        snapshotCallCount++
        return snapshotResult
    }
    
    override fun getMetricsStream(): Flow<MetricsSnapshot> = flowOf()
    
    override suspend fun recordCustomMetric(name: String, value: Double, tags: Map<String, String>): com.weather.domain.common.Result<Unit> {
        recordCustomCallCount++
        customMetrics[name] = Pair(value, tags)
        return recordCustomResult
    }
    
    // Test helpers
    fun getCustomMetrics(): Map<String, Pair<Double, Map<String, String>>> = customMetrics.toMap()
    
    fun setSnapshotResult(result: com.weather.domain.common.Result<MetricsSnapshot>) {
        snapshotResult = result
    }
    
    fun reset() {
        startCallCount = 0
        stopCallCount = 0
        snapshotCallCount = 0
        recordCustomCallCount = 0
        customMetrics.clear()
    }
    
    companion object {
        private fun createTestSnapshot(): MetricsSnapshot {
            return MetricsSnapshot(
                systemMetrics = SystemMetrics(
                    memoryUsage = MemoryMetrics(
                        totalMemoryMB = 4096.0,
                        usedMemoryMB = 2048.0,
                        availableMemoryMB = 2048.0,
                        memoryPressure = MemoryPressure.LOW
                    ),
                    cpuUsage = CpuMetrics(
                        cpuUsagePercentage = 25.0,
                        systemLoadAverage = 0.5,
                        threadCount = 10,
                        activeThreadCount = 3
                    ),
                    networkMetrics = NetworkMetrics(
                        isConnected = true,
                        connectionType = ConnectionType.WIFI,
                        signalStrength = SignalStrength.EXCELLENT
                    ),
                    storageMetrics = StorageMetrics(
                        totalStorageGB = 128.0,
                        usedStorageGB = 64.0,
                        availableStorageGB = 64.0,
                        cacheUsageGB = 2.0
                    ),
                    batteryMetrics = BatteryMetrics(
                        batteryLevel = 0.85,
                        isCharging = false,
                        batteryHealth = BatteryHealth.GOOD,
                        powerSavingMode = false
                    )
                ),
                applicationMetrics = ApplicationMetrics(
                    appVersion = "1.0.0",
                    buildNumber = "1",
                    sessionDurationMs = 300000
                ),
                performanceMetrics = PerformanceMetrics(
                    frameRate = FrameRateMetrics(
                        averageFps = 60.0,
                        droppedFrames = 2,
                        jankyFrames = 1,
                        renderTimeMs = 16.7
                    ),
                    apiPerformance = ApiPerformanceMetrics(
                        averageResponseTimeMs = 250.0,
                        successRate = 0.95,
                        timeoutCount = 1,
                        retryCount = 2,
                        cacheHitRate = 0.8
                    ),
                    databasePerformance = DatabasePerformanceMetrics(
                        averageQueryTimeMs = 15.0,
                        queryCount = 50,
                        cacheHitRate = 0.9,
                        databaseSizeMB = 5.0
                    ),
                    uiPerformance = UiPerformanceMetrics(
                        screenTransitionTimeMs = 200.0,
                        scrollPerformance = ScrollPerformanceMetrics(
                            averageScrollFps = 58.0,
                            scrollJankCount = 1,
                            scrollDuration = 2000
                        ),
                        inputLatencyMs = 10.0
                    )
                ),
                businessMetrics = BusinessMetrics(
                    dailyActiveUsers = 1,
                    sessionCount = 1,
                    weatherRequestCount = 5,
                    crashFreeSessionRate = 1.0
                )
            )
        }
    }
}

/**
 * Feature toggle mocks
 */
class FakeFeatureToggleManager(
    private val features: MutableMap<String, Boolean> = mutableMapOf(),
    private var initializeResult: com.weather.domain.common.Result<Unit> = com.weather.domain.common.Result.Success(Unit),
    private var enableResult: com.weather.domain.common.Result<Unit> = com.weather.domain.common.Result.Success(Unit),
    private var disableResult: com.weather.domain.common.Result<Unit> = com.weather.domain.common.Result.Success(Unit),
    private var rolloutResult: com.weather.domain.common.Result<Unit> = com.weather.domain.common.Result.Success(Unit),
    private var refreshResult: com.weather.domain.common.Result<Unit> = com.weather.domain.common.Result.Success(Unit),
    private var recordUsageResult: com.weather.domain.common.Result<Unit> = com.weather.domain.common.Result.Success(Unit)
) : FeatureToggleManager {
    
    var initializeCallCount = 0
        private set
    var enableCallCount = 0
        private set
    var disableCallCount = 0
        private set
    var rolloutCallCount = 0
        private set
    var refreshCallCount = 0
        private set
    var recordUsageCallCount = 0
        private set
    
    private val featureConfigurations = mutableMapOf<String, FeatureConfiguration>()
    private val usageEvents = mutableListOf<Pair<String, Map<String, String>>>()
    
    override suspend fun initialize(): com.weather.domain.common.Result<Unit> {
        initializeCallCount++
        return initializeResult
    }
    
    override fun isFeatureEnabled(feature: String): Boolean = features[feature] ?: false
    
    override fun isFeatureEnabled(feature: FeatureFlag): Boolean = isFeatureEnabled(feature.key)
    
    override suspend fun enableFeature(feature: String): com.weather.domain.common.Result<Unit> {
        enableCallCount++
        features[feature] = true
        return enableResult
    }
    
    override suspend fun disableFeature(feature: String): com.weather.domain.common.Result<Unit> {
        disableCallCount++
        features[feature] = false
        return disableResult
    }
    
    override suspend fun setFeatureRolloutPercentage(feature: String, percentage: Int): com.weather.domain.common.Result<Unit> {
        rolloutCallCount++
        return rolloutResult
    }
    
    override suspend fun refreshConfiguration(): com.weather.domain.common.Result<Unit> {
        refreshCallCount++
        return refreshResult
    }
    
    override fun getFeatureUpdatesFlow(): Flow<FeatureUpdate> = flowOf()
    
    override fun getAllFeatures(): Map<String, FeatureConfiguration> = featureConfigurations.toMap()
    
    override suspend fun recordFeatureUsage(feature: String, context: Map<String, String>): com.weather.domain.common.Result<Unit> {
        recordUsageCallCount++
        usageEvents.add(Pair(feature, context))
        return recordUsageResult
    }
    
    // Test helpers
    fun setFeatureEnabled(feature: String, enabled: Boolean) {
        features[feature] = enabled
    }
    
    fun setFeatureEnabled(feature: FeatureFlag, enabled: Boolean) {
        features[feature.key] = enabled
    }
    
    fun getUsageEvents(): List<Pair<String, Map<String, String>>> = usageEvents.toList()
    
    fun reset() {
        features.clear()
        featureConfigurations.clear()
        usageEvents.clear()
        initializeCallCount = 0
        enableCallCount = 0
        disableCallCount = 0
        rolloutCallCount = 0
        refreshCallCount = 0
        recordUsageCallCount = 0
    }
}

/**
 * Factory for creating test doubles
 */
object TestDoubleFactory {
    
    fun createWeatherRepository(): FakeWeatherRepository = FakeWeatherRepository()
    
    fun createEncryptionProvider(): FakeEncryptionProvider = FakeEncryptionProvider()
    
    fun createSecureStorage(): FakeSecureStorage = FakeSecureStorage()
    
    fun createTelemetryProvider(): FakeTelemetryProvider = FakeTelemetryProvider()
    
    fun createMetricsCollector(): FakeMetricsCollector = FakeMetricsCollector()
    
    fun createFeatureToggleManager(): FakeFeatureToggleManager = FakeFeatureToggleManager()
}