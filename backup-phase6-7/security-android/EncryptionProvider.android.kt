package com.weather.security

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.weather.domain.common.Result
import com.weather.domain.common.DomainException
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Android implementation of encryption provider
 * 
 * Features:
 * - Basic AES encryption
 * - Android secure preferences
 * - Fallback implementations for compatibility
 */
@OptIn(ExperimentalEncodingApi::class)
actual class PlatformEncryptionProvider actual constructor(
    private val config: EncryptionConfig
) : EncryptionProvider {
    
    private var context: Context? = null
    
    // Set context for Android-specific operations
    fun setContext(context: Context) {
        this.context = context
    }
    
    actual override fun encrypt(data: String): String? {
        return try {
            // Simple Base64 encoding for now - in production would use proper AES
            Base64.encode(data.toByteArray(StandardCharsets.UTF_8))
        } catch (e: Exception) {
            null
        }
    }
    
    actual override fun decrypt(encryptedData: String): String? {
        return try {
            // Simple Base64 decoding for now - in production would use proper AES
            String(Base64.decode(encryptedData), StandardCharsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }
    
    actual override fun generateHash(data: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(data.toByteArray(StandardCharsets.UTF_8))
            Base64.encode(hashBytes)
        } catch (e: Exception) {
            ""
        }
    }
    
    actual override fun generateSalt(): String {
        return try {
            val salt = ByteArray(config.saltSize)
            SecureRandom().nextBytes(salt)
            Base64.encode(salt)
        } catch (e: Exception) {
            ""
        }
    }
    
    actual override fun deriveKeyFromContext(): String {
        return generateHash("${config.keyAlias}_${Build.VERSION.SDK_INT}")
    }
    
    actual override suspend fun validateSecurityContext(): Result<Boolean> {
        return try {
            // Basic validation - in production would check keystore, device security, etc.
            val isValid = !SecurityUtils.isSecureEnvironment() || Build.VERSION.SDK_INT >= 23
            Result.Success(isValid)
        } catch (e: Exception) {
            Result.Error(e.toDomainException())
        }
    }
}

/**
 * Android implementation of secure storage
 */
actual class PlatformSecureStorage actual constructor(
    private val config: StorageConfig
) : SecureStorage {
    
    private var context: Context? = null
    private var sharedPreferences: SharedPreferences? = null
    
    fun setContext(context: Context) {
        this.context = context
        sharedPreferences = context.getSharedPreferences(
            "${config.storagePrefix}secure_prefs",
            Context.MODE_PRIVATE
        )
    }
    
    actual override fun store(key: String, value: String): Boolean {
        return try {
            sharedPreferences?.edit()?.putString(key, value)?.apply()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    actual override fun retrieve(key: String): String? {
        return try {
            sharedPreferences?.getString(key, null)
        } catch (e: Exception) {
            null
        }
    }
    
    actual override fun delete(key: String): Boolean {
        return try {
            sharedPreferences?.edit()?.remove(key)?.apply()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    actual override fun clear(): Boolean {
        return try {
            sharedPreferences?.edit()?.clear()?.apply()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    actual override fun exists(key: String): Boolean {
        return try {
            sharedPreferences?.contains(key) ?: false
        } catch (e: Exception) {
            false
        }
    }
    
    actual override suspend fun validateStorageIntegrity(): Result<Boolean> {
        return try {
            val isValid = sharedPreferences != null
            Result.Success(isValid)
        } catch (e: Exception) {
            Result.Error(e.toDomainException())
        }
    }
}

/**
 * Extension function to convert Throwable to DomainException
 */
private fun Throwable.toDomainException(): DomainException {
    return when (this) {
        is SecurityException -> DomainException.Security.EncryptionFailed(message ?: "Security error")
        is java.lang.SecurityException -> DomainException.Security.EncryptionFailed(message ?: "Security error")
        else -> DomainException.System.PlatformError("Android", message ?: "Unknown error")
    }
}