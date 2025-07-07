package com.weather.features

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

/**
 * Android-specific implementation of FeatureToggleStorage using SharedPreferences
 */
class AndroidFeatureToggleStorage(
    private val context: Context,
    private val prefsName: String = "weather_feature_toggles"
) : FeatureToggleStorage {
    
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    }
    
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
            prefs.edit()
                .putString(FEATURE_PREFIX + config.key, configJson)
                .apply()
        } catch (e: Exception) {
            println("❌ Failed to save feature configuration: ${e.message}")
        }
    }
    
    override fun loadFeature(key: String): FeatureConfiguration? {
        return try {
            val configJson = prefs.getString(FEATURE_PREFIX + key, null)
            configJson?.let { json.decodeFromString<FeatureConfiguration>(it) }
        } catch (e: Exception) {
            println("❌ Failed to load feature configuration for $key: ${e.message}")
            null
        }
    }
    
    override fun loadAllFeatures(): Map<String, FeatureConfiguration> {
        val features = mutableMapOf<String, FeatureConfiguration>()
        
        try {
            prefs.all.forEach { (key, value) ->
                if (key.startsWith(FEATURE_PREFIX) && value is String) {
                    try {
                        val config = json.decodeFromString<FeatureConfiguration>(value)
                        features[config.key] = config
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
            prefs.edit()
                .putString(AB_TEST_PREFIX + test.testId, testJson)
                .apply()
        } catch (e: Exception) {
            println("❌ Failed to save A/B test configuration: ${e.message}")
        }
    }
    
    override fun loadABTests(): List<ABTestConfiguration> {
        val abTests = mutableListOf<ABTestConfiguration>()
        
        try {
            prefs.all.forEach { (key, value) ->
                if (key.startsWith(AB_TEST_PREFIX) && value is String) {
                    try {
                        val test = json.decodeFromString<ABTestConfiguration>(value)
                        abTests.add(test)
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
            val editor = prefs.edit()
            
            // Remove all feature toggles and A/B tests
            prefs.all.keys.forEach { key ->
                if (key.startsWith(FEATURE_PREFIX) || key.startsWith(AB_TEST_PREFIX)) {
                    editor.remove(key)
                }
            }
            
            editor.apply()
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
            val allData = mutableMapOf<String, Any>()
            
            prefs.all.forEach { (key, value) ->
                if ((key.startsWith(FEATURE_PREFIX) || key.startsWith(AB_TEST_PREFIX)) && value is String) {
                    allData[key] = value
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
            val editor = prefs.edit()
            
            data.forEach { (key, value) ->
                if (key.startsWith(FEATURE_PREFIX) || key.startsWith(AB_TEST_PREFIX)) {
                    editor.putString(key, value)
                }
            }
            
            editor.apply()
            println("📥 Imported feature configuration")
            true
            
        } catch (e: Exception) {
            println("❌ Failed to import configuration: ${e.message}")
            false
        }
    }
}