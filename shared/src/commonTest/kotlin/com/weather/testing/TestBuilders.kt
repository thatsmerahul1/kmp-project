package com.weather.testing

import com.weather.domain.common.Result
import com.weather.domain.model.*
import com.weather.security.*
import com.weather.monitoring.*
import com.weather.features.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.random.Random

/**
 * Test Data Builders for 2025 Testing Standards
 * 
 * Features:
 * - Builder pattern for clean test data creation
 * - Reasonable defaults for all properties
 * - Fluent API for customization
 * - Support for all domain models
 * - Random data generation utilities
 */

/**
 * Weather domain model builders
 */
class WeatherInfoBuilder {
    private var temperature: Double = 20.0
    private var humidity: Double = 65.0
    private var windSpeed: Double = 5.5
    private var windDirection: Double = 180.0
    private var pressure: Double = 1013.25
    private var visibility: Double = 10.0
    private var uvIndex: Double = 3.0
    private var cloudCover: Double = 30.0
    private var condition: String = "Partly Cloudy"
    private var location: String = "Test City"
    private var timestamp: Instant = Clock.System.now()
    
    fun temperature(value: Double) = apply { temperature = value }
    fun humidity(value: Double) = apply { humidity = value }
    fun windSpeed(value: Double) = apply { windSpeed = value }
    fun windDirection(value: Double) = apply { windDirection = value }
    fun pressure(value: Double) = apply { pressure = value }
    fun visibility(value: Double) = apply { visibility = value }
    fun uvIndex(value: Double) = apply { uvIndex = value }
    fun cloudCover(value: Double) = apply { cloudCover = value }
    fun condition(value: String) = apply { condition = value }
    fun location(value: String) = apply { location = value }
    fun timestamp(value: Instant) = apply { timestamp = value }
    
    fun build() = WeatherInfo(
        temperature = temperature,
        humidity = humidity,
        windSpeed = windSpeed,
        windDirection = windDirection,
        pressure = pressure,
        visibility = visibility,
        uvIndex = uvIndex,
        cloudCover = cloudCover,
        condition = condition,
        location = location,
        timestamp = timestamp
    )
}

class LocationBuilder {
    private var name: String = "Test City"
    private var country: String = "Test Country"
    private var region: String = "Test Region"
    private var latitude: Double = 40.7128
    private var longitude: Double = -74.0060
    private var timezone: String = "America/New_York"
    
    fun name(value: String) = apply { name = value }
    fun country(value: String) = apply { country = value }
    fun region(value: String) = apply { region = value }
    fun latitude(value: Double) = apply { latitude = value }
    fun longitude(value: Double) = apply { longitude = value }
    fun timezone(value: String) = apply { timezone = value }
    
    fun build() = Location(
        name = name,
        country = country,
        region = region,
        latitude = latitude,
        longitude = longitude,
        timezone = timezone
    )
}

/**
 * Security domain model builders
 */
class EncryptionConfigBuilder {
    private var algorithm: String = "AES-256-GCM"
    private var keySize: Int = 256
    private var iterationCount: Int = 100000
    private var saltSize: Int = 32
    private var enableHardwareBackedKeys: Boolean = true
    private var enableAntiTampering: Boolean = true
    private var keyAlias: String = "test_key"
    
    fun algorithm(value: String) = apply { algorithm = value }
    fun keySize(value: Int) = apply { keySize = value }
    fun iterationCount(value: Int) = apply { iterationCount = value }
    fun saltSize(value: Int) = apply { saltSize = value }
    fun enableHardwareBackedKeys(value: Boolean) = apply { enableHardwareBackedKeys = value }
    fun enableAntiTampering(value: Boolean) = apply { enableAntiTampering = value }
    fun keyAlias(value: String) = apply { keyAlias = value }
    
    fun build() = EncryptionConfig(
        algorithm = algorithm,
        keySize = keySize,
        iterationCount = iterationCount,
        saltSize = saltSize,
        enableHardwareBackedKeys = enableHardwareBackedKeys,
        enableAntiTampering = enableAntiTampering,
        keyAlias = keyAlias
    )
}

class CertificatePinBuilder {
    private var hostname: String = "api.test.com"
    private var pins: List<String> = listOf("test-pin-1", "test-pin-2")
    private var includeSubdomains: Boolean = false
    private var enforceBackupPin: Boolean = true
    private var maxAge: Long = 7776000L
    private var reportOnly: Boolean = false
    
    fun hostname(value: String) = apply { hostname = value }
    fun pins(value: List<String>) = apply { pins = value }
    fun includeSubdomains(value: Boolean) = apply { includeSubdomains = value }
    fun enforceBackupPin(value: Boolean) = apply { enforceBackupPin = value }
    fun maxAge(value: Long) = apply { maxAge = value }
    fun reportOnly(value: Boolean) = apply { reportOnly = value }
    
    fun build() = CertificatePin(
        hostname = hostname,
        pins = pins,
        includeSubdomains = includeSubdomains,
        enforceBackupPin = enforceBackupPin,
        maxAge = maxAge,
        reportOnly = reportOnly
    )
}

/**
 * Monitoring domain model builders
 */
class TelemetryMetricBuilder {
    private var name: String = "test_metric"
    private var value: Double = 100.0
    private var unit: MetricUnit = MetricUnit.COUNT
    private var type: MetricType = MetricType.COUNTER
    private var timestamp: Instant = Clock.System.now()
    private var attributes: Map<String, String> = emptyMap()
    private var tags: List<String> = emptyList()
    
    fun name(value: String) = apply { name = value }
    fun value(value: Double) = apply { this.value = value }
    fun unit(value: MetricUnit) = apply { unit = value }
    fun type(value: MetricType) = apply { type = value }
    fun timestamp(value: Instant) = apply { timestamp = value }
    fun attributes(value: Map<String, String>) = apply { attributes = value }
    fun tags(value: List<String>) = apply { tags = value }
    
    fun build() = TelemetryMetric(
        name = name,
        value = value,
        unit = unit,
        type = type,
        timestamp = timestamp,
        attributes = attributes,
        tags = tags
    )
}

class TelemetrySpanBuilder {
    private var traceId: String = "test-trace-${Random.nextInt()}"
    private var spanId: String = "test-span-${Random.nextInt()}"
    private var parentSpanId: String? = null
    private var operationName: String = "test_operation"
    private var startTime: Instant = Clock.System.now()
    private var endTime: Instant? = null
    private var duration: Long? = null
    private var status: SpanStatus = SpanStatus.OK
    private var attributes: Map<String, String> = emptyMap()
    private var events: List<SpanEvent> = emptyList()
    
    fun traceId(value: String) = apply { traceId = value }
    fun spanId(value: String) = apply { spanId = value }
    fun parentSpanId(value: String?) = apply { parentSpanId = value }
    fun operationName(value: String) = apply { operationName = value }
    fun startTime(value: Instant) = apply { startTime = value }
    fun endTime(value: Instant?) = apply { endTime = value }
    fun duration(value: Long?) = apply { duration = value }
    fun status(value: SpanStatus) = apply { status = value }
    fun attributes(value: Map<String, String>) = apply { attributes = value }
    fun events(value: List<SpanEvent>) = apply { events = value }
    
    fun build() = TelemetrySpan(
        traceId = traceId,
        spanId = spanId,
        parentSpanId = parentSpanId,
        operationName = operationName,
        startTime = startTime,
        endTime = endTime,
        duration = duration,
        status = status,
        attributes = attributes,
        events = events
    )
}

class CrashReportBuilder {
    private var crashId: String = "crash-${Random.nextInt()}"
    private var timestamp: Instant = Clock.System.now()
    private var exception: ExceptionInfo = aExceptionInfo().build()
    private var stackTrace: String = "test.stack.trace"
    private var deviceInfo: DeviceInfo = aDeviceInfo().build()
    private var appInfo: AppInfo = anAppInfo().build()
    private var userContext: UserContext? = null
    private var breadcrumbs: List<Breadcrumb> = emptyList()
    private var customData: Map<String, String> = emptyMap()
    private var severity: CrashSeverity = CrashSeverity.FATAL
    private var sessionId: String = "session-${Random.nextInt()}"
    private var userId: String? = null
    
    fun crashId(value: String) = apply { crashId = value }
    fun timestamp(value: Instant) = apply { timestamp = value }
    fun exception(value: ExceptionInfo) = apply { exception = value }
    fun stackTrace(value: String) = apply { stackTrace = value }
    fun deviceInfo(value: DeviceInfo) = apply { deviceInfo = value }
    fun appInfo(value: AppInfo) = apply { appInfo = value }
    fun userContext(value: UserContext?) = apply { userContext = value }
    fun breadcrumbs(value: List<Breadcrumb>) = apply { breadcrumbs = value }
    fun customData(value: Map<String, String>) = apply { customData = value }
    fun severity(value: CrashSeverity) = apply { severity = value }
    fun sessionId(value: String) = apply { sessionId = value }
    fun userId(value: String?) = apply { userId = value }
    
    fun build() = CrashReport(
        crashId = crashId,
        timestamp = timestamp,
        exception = exception,
        stackTrace = stackTrace,
        deviceInfo = deviceInfo,
        appInfo = appInfo,
        userContext = userContext,
        breadcrumbs = breadcrumbs,
        customData = customData,
        severity = severity,
        sessionId = sessionId,
        userId = userId
    )
}

class ExceptionInfoBuilder {
    private var type: String = "TestException"
    private var message: String = "Test exception message"
    private var localizedMessage: String? = null
    private var cause: ExceptionInfo? = null
    
    fun type(value: String) = apply { type = value }
    fun message(value: String) = apply { message = value }
    fun localizedMessage(value: String?) = apply { localizedMessage = value }
    fun cause(value: ExceptionInfo?) = apply { cause = value }
    
    fun build() = ExceptionInfo(
        type = type,
        message = message,
        localizedMessage = localizedMessage,
        cause = cause
    )
}

class DeviceInfoBuilder {
    private var platform: String = "Test"
    private var osVersion: String = "1.0"
    private var deviceModel: String = "TestDevice"
    private var manufacturer: String = "TestCorp"
    private var architecture: String = "x64"
    private var screenResolution: String = "1920x1080"
    private var availableMemoryMB: Long = 4096
    private var totalMemoryMB: Long = 8192
    private var storageAvailableGB: Double = 32.0
    private var batteryLevel: Double? = 0.85
    private var isCharging: Boolean? = false
    private var networkType: String = "WiFi"
    private var locale: String = "en_US"
    
    fun platform(value: String) = apply { platform = value }
    fun osVersion(value: String) = apply { osVersion = value }
    fun deviceModel(value: String) = apply { deviceModel = value }
    fun manufacturer(value: String) = apply { manufacturer = value }
    fun architecture(value: String) = apply { architecture = value }
    fun screenResolution(value: String) = apply { screenResolution = value }
    fun availableMemoryMB(value: Long) = apply { availableMemoryMB = value }
    fun totalMemoryMB(value: Long) = apply { totalMemoryMB = value }
    fun storageAvailableGB(value: Double) = apply { storageAvailableGB = value }
    fun batteryLevel(value: Double?) = apply { batteryLevel = value }
    fun isCharging(value: Boolean?) = apply { isCharging = value }
    fun networkType(value: String) = apply { networkType = value }
    fun locale(value: String) = apply { locale = value }
    
    fun build() = DeviceInfo(
        platform = platform,
        osVersion = osVersion,
        deviceModel = deviceModel,
        manufacturer = manufacturer,
        architecture = architecture,
        screenResolution = screenResolution,
        availableMemoryMB = availableMemoryMB,
        totalMemoryMB = totalMemoryMB,
        storageAvailableGB = storageAvailableGB,
        batteryLevel = batteryLevel,
        isCharging = isCharging,
        networkType = networkType,
        locale = locale
    )
}

class AppInfoBuilder {
    private var version: String = "1.0.0"
    private var buildNumber: String = "1"
    private var packageName: String = "com.test.app"
    private var debugMode: Boolean = true
    private var installationId: String = "install-${Random.nextInt()}"
    private var sessionId: String = "session-${Random.nextInt()}"
    private var sessionStartTime: Instant = Clock.System.now()
    private var memoryUsageAtCrash: Long = 128
    private var cpuUsageAtCrash: Double = 15.5
    
    fun version(value: String) = apply { version = value }
    fun buildNumber(value: String) = apply { buildNumber = value }
    fun packageName(value: String) = apply { packageName = value }
    fun debugMode(value: Boolean) = apply { debugMode = value }
    fun installationId(value: String) = apply { installationId = value }
    fun sessionId(value: String) = apply { sessionId = value }
    fun sessionStartTime(value: Instant) = apply { sessionStartTime = value }
    fun memoryUsageAtCrash(value: Long) = apply { memoryUsageAtCrash = value }
    fun cpuUsageAtCrash(value: Double) = apply { cpuUsageAtCrash = value }
    
    fun build() = AppInfo(
        version = version,
        buildNumber = buildNumber,
        packageName = packageName,
        debugMode = debugMode,
        installationId = installationId,
        sessionId = sessionId,
        sessionStartTime = sessionStartTime,
        memoryUsageAtCrash = memoryUsageAtCrash,
        cpuUsageAtCrash = cpuUsageAtCrash
    )
}

/**
 * Feature toggle builders
 */
class FeatureConfigurationBuilder {
    private var key: String = "test_feature"
    private var enabled: Boolean = true
    private var rolloutPercentage: Int = 100
    private var targetAudience: List<String> = emptyList()
    private var environment: List<String> = listOf("all")
    private var minimumAppVersion: String? = null
    private var maximumAppVersion: String? = null
    private var startDate: Instant? = null
    private var endDate: Instant? = null
    private var dependencies: List<String> = emptyList()
    private var metadata: Map<String, String> = emptyMap()
    private var lastUpdated: Instant = Clock.System.now()
    
    fun key(value: String) = apply { key = value }
    fun enabled(value: Boolean) = apply { enabled = value }
    fun rolloutPercentage(value: Int) = apply { rolloutPercentage = value }
    fun targetAudience(value: List<String>) = apply { targetAudience = value }
    fun environment(value: List<String>) = apply { environment = value }
    fun minimumAppVersion(value: String?) = apply { minimumAppVersion = value }
    fun maximumAppVersion(value: String?) = apply { maximumAppVersion = value }
    fun startDate(value: Instant?) = apply { startDate = value }
    fun endDate(value: Instant?) = apply { endDate = value }
    fun dependencies(value: List<String>) = apply { dependencies = value }
    fun metadata(value: Map<String, String>) = apply { metadata = value }
    fun lastUpdated(value: Instant) = apply { lastUpdated = value }
    
    fun build() = FeatureConfiguration(
        key = key,
        enabled = enabled,
        rolloutPercentage = rolloutPercentage,
        targetAudience = targetAudience,
        environment = environment,
        minimumAppVersion = minimumAppVersion,
        maximumAppVersion = maximumAppVersion,
        startDate = startDate,
        endDate = endDate,
        dependencies = dependencies,
        metadata = metadata,
        lastUpdated = lastUpdated
    )
}

/**
 * Result builders for testing success/error scenarios
 */
class ResultBuilder<T> {
    fun success(data: T) = Result.Success(data)
    fun error(exception: Exception) = Result.Error(exception.toDomainException())
    fun loading() = Result.Loading
}

/**
 * DSL Functions for fluent test data creation
 */

// Weather domain
fun aWeatherInfo(): WeatherInfoBuilder = WeatherInfoBuilder()
fun aLocation(): LocationBuilder = LocationBuilder()

// Security domain
fun anEncryptionConfig(): EncryptionConfigBuilder = EncryptionConfigBuilder()
fun aCertificatePin(): CertificatePinBuilder = CertificatePinBuilder()

// Monitoring domain
fun aTelemetryMetric(): TelemetryMetricBuilder = TelemetryMetricBuilder()
fun aTelemetrySpan(): TelemetrySpanBuilder = TelemetrySpanBuilder()
fun aCrashReport(): CrashReportBuilder = CrashReportBuilder()
fun aExceptionInfo(): ExceptionInfoBuilder = ExceptionInfoBuilder()
fun aDeviceInfo(): DeviceInfoBuilder = DeviceInfoBuilder()
fun anAppInfo(): AppInfoBuilder = AppInfoBuilder()

// Feature toggles
fun aFeatureConfiguration(): FeatureConfigurationBuilder = FeatureConfigurationBuilder()

// Results
fun <T> aResult(): ResultBuilder<T> = ResultBuilder()

/**
 * Random data generators for property-based testing
 */
object TestDataGenerators {
    
    fun randomWeatherInfo(): WeatherInfo = aWeatherInfo()
        .temperature(Random.nextDouble(-50.0, 50.0))
        .humidity(Random.nextDouble(0.0, 100.0))
        .windSpeed(Random.nextDouble(0.0, 50.0))
        .pressure(Random.nextDouble(980.0, 1040.0))
        .build()
    
    fun randomLocation(): Location = aLocation()
        .name("City${Random.nextInt(1, 1000)}")
        .latitude(Random.nextDouble(-90.0, 90.0))
        .longitude(Random.nextDouble(-180.0, 180.0))
        .build()
    
    fun randomTelemetryMetric(): TelemetryMetric = aTelemetryMetric()
        .name("metric_${Random.nextInt()}")
        .value(Random.nextDouble(0.0, 1000.0))
        .unit(MetricUnit.values().random())
        .type(MetricType.values().random())
        .build()
    
    fun randomFeatureFlag(): FeatureFlag = FeatureFlag.values().random()
    
    fun randomFeatureConfiguration(): FeatureConfiguration = aFeatureConfiguration()
        .key("feature_${Random.nextInt()}")
        .enabled(Random.nextBoolean())
        .rolloutPercentage(Random.nextInt(0, 101))
        .build()
}

/**
 * Test assertion helpers
 */
object TestAssertions {
    
    fun assertResultSuccess(result: Result<*>) {
        check(result is Result.Success) { "Expected Result.Success but was $result" }
    }
    
    fun assertResultError(result: Result<*>) {
        check(result is Result.Error) { "Expected Result.Error but was $result" }
    }
    
    fun assertResultLoading(result: Result<*>) {
        check(result is Result.Loading) { "Expected Result.Loading but was $result" }
    }
    
    fun <T> assertResultSuccessData(result: Result<T>, expectedData: T) {
        check(result is Result.Success && result.data == expectedData) {
            "Expected Result.Success($expectedData) but was $result"
        }
    }
}