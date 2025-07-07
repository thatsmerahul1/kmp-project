package com.weather.features

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import platform.Foundation.*

/**
 * iOS-specific implementation of FeatureToggleStorage using UserDefaults
 */
class IosFeatureToggleStorage(
    private val suiteName: String = "com.weather.kmp.features"
) : FeatureToggleStorage {
    
    private val userDefaults = NSUserDefaults(suiteName = suiteName)
    
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }
    
    companion object {
        private const val FEATURE_PREFIX = "feature_"
        private const val AB_TEST_PREFIX = "ab_test_"
    }
    
    override fun saveFeature(config: FeatureConfiguration) {
        try {
            val configJson = json.encodeToString(config)
            userDefaults.setObject(configJson, forKey = FEATURE_PREFIX + config.key)
            userDefaults.synchronize()
        } catch (e: Exception) {
            println("❌ Failed to save feature configuration: ${e.message}")
        }
    }
    
    override fun loadFeature(key: String): FeatureConfiguration? {
        return try {
            val configJson = userDefaults.stringForKey(FEATURE_PREFIX + key)
            configJson?.let { json.decodeFromString<FeatureConfiguration>(it) }
        } catch (e: Exception) {
            println("❌ Failed to load feature configuration for $key: ${e.message}")
            null
        }
    }
    
    override fun loadAllFeatures(): Map<String, FeatureConfiguration> {
        val features = mutableMapOf<String, FeatureConfiguration>()
        
        try {
            val dictionary = userDefaults.dictionaryRepresentation()
            
            @Suppress("UNCHECKED_CAST")
            val allKeys = dictionary.allKeys as? List<String> ?: emptyList()
            
            allKeys.forEach { key ->
                if (key.startsWith(FEATURE_PREFIX)) {
                    try {
                        val configJson = userDefaults.stringForKey(key)
                        configJson?.let {
                            val config = json.decodeFromString<FeatureConfiguration>(it)
                            features[config.key] = config
                        }
                    } catch (e: Exception) {
                        println("❌ Failed to parse feature configuration for $key: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            println("❌ Failed to load all features: ${e.message}")
        }
        
        return features
    }
    
    override fun saveABTest(test: ABTestConfiguration) {
        try {
            val testJson = json.encodeToString(test)
            userDefaults.setObject(testJson, forKey = AB_TEST_PREFIX + test.testId)
            userDefaults.synchronize()
        } catch (e: Exception) {
            println("❌ Failed to save A/B test configuration: ${e.message}")
        }
    }
    
    override fun loadABTests(): List<ABTestConfiguration> {
        val abTests = mutableListOf<ABTestConfiguration>()
        
        try {
            val dictionary = userDefaults.dictionaryRepresentation()
            
            @Suppress("UNCHECKED_CAST")
            val allKeys = dictionary.allKeys as? List<String> ?: emptyList()
            
            allKeys.forEach { key ->
                if (key.startsWith(AB_TEST_PREFIX)) {
                    try {
                        val testJson = userDefaults.stringForKey(key)
                        testJson?.let {
                            val test = json.decodeFromString<ABTestConfiguration>(it)
                            abTests.add(test)
                        }
                    } catch (e: Exception) {
                        println("❌ Failed to parse A/B test configuration for $key: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            println("❌ Failed to load A/B tests: ${e.message}")
        }
        
        return abTests
    }
    
    override fun clearAll() {
        try {
            val dictionary = userDefaults.dictionaryRepresentation()
            
            @Suppress("UNCHECKED_CAST")
            val allKeys = dictionary.allKeys as? List<String> ?: emptyList()
            
            // Remove all feature toggles and A/B tests
            allKeys.forEach { key ->
                if (key.startsWith(FEATURE_PREFIX) || key.startsWith(AB_TEST_PREFIX)) {
                    userDefaults.removeObjectForKey(key)
                }
            }
            
            userDefaults.synchronize()
            println("🧹 Cleared all feature toggle data")
            
        } catch (e: Exception) {
            println("❌ Failed to clear feature toggle data: ${e.message}")
        }
    }
    
    /**
     * Export feature configurations for backup or debugging
     */
    fun exportConfiguration(): String {
        return try {
            val allData = mutableMapOf<String, String>()
            val dictionary = userDefaults.dictionaryRepresentation()
            
            @Suppress("UNCHECKED_CAST")
            val allKeys = dictionary.allKeys as? List<String> ?: emptyList()
            
            allKeys.forEach { key ->
                if (key.startsWith(FEATURE_PREFIX) || key.startsWith(AB_TEST_PREFIX)) {
                    val value = userDefaults.stringForKey(key)
                    if (value != null) {
                        allData[key] = value
                    }
                }
            }
            
            json.encodeToString(allData)
        } catch (e: Exception) {
            println("❌ Failed to export configuration: ${e.message}")
            "{}"
        }
    }
    
    /**
     * Import feature configurations from backup
     */
    fun importConfiguration(configurationJson: String): Boolean {
        return try {
            val data = json.decodeFromString<Map<String, String>>(configurationJson)
            
            data.forEach { (key, value) ->
                if (key.startsWith(FEATURE_PREFIX) || key.startsWith(AB_TEST_PREFIX)) {
                    userDefaults.setObject(value, forKey = key)
                }
            }
            
            userDefaults.synchronize()
            println("📥 Imported feature configuration")
            true
            
        } catch (e: Exception) {
            println("❌ Failed to import configuration: ${e.message}")
            false
        }
    }
}