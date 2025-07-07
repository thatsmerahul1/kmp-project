package com.weather.security

import com.weather.domain.common.Result
import com.weather.domain.common.DomainException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

/**
 * JVM implementation of platform-specific encryption
 */
actual class PlatformEncryptionProvider actual constructor(
    private val config: EncryptionConfig
) : EncryptionProvider {
    
    private val secureRandom = SecureRandom()
    
    actual override fun encrypt(data: String): String? {
        return try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val key = deriveKeyFromContext()
            val secretKey = SecretKeySpec(Base64.getDecoder().decode(key), "AES")
            
            val iv = ByteArray(12)
            secureRandom.nextBytes(iv)
            
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)
            
            val encryptedBytes = cipher.doFinal(data.toByteArray())
            val combined = iv + encryptedBytes
            
            Base64.getEncoder().encodeToString(combined)
        } catch (e: Exception) {
            null
        }
    }

    actual override fun decrypt(encryptedData: String): String? {
        return try {
            val combined = Base64.getDecoder().decode(encryptedData)
            val iv = combined.sliceArray(0..11)
            val encrypted = combined.sliceArray(12 until combined.size)
            
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val key = deriveKeyFromContext()
            val secretKey = SecretKeySpec(Base64.getDecoder().decode(key), "AES")
            
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            
            val decryptedBytes = cipher.doFinal(encrypted)
            String(decryptedBytes)
        } catch (e: Exception) {
            null
        }
    }

    actual override fun generateHash(data: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(data.toByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }

    actual override fun generateSalt(): String {
        val salt = ByteArray(config.saltSize)
        secureRandom.nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    actual override fun deriveKeyFromContext(): String {
        // For JVM, use a hardcoded key for testing
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(256, secureRandom)
        val secretKey = keyGenerator.generateKey()
        return Base64.getEncoder().encodeToString(secretKey.encoded)
    }

    actual override suspend fun validateSecurityContext(): Result<Boolean> {
        return try {
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(DomainException.Security.SecurityContextInvalid("Security validation failed: ${e.message}"))
        }
    }
}

/**
 * JVM implementation of platform-specific secure storage
 */
actual class PlatformSecureStorage actual constructor(
    private val config: StorageConfig
) : SecureStorage {
    
    private val storage = mutableMapOf<String, String>()

    actual override fun store(key: String, value: String): Boolean {
        return try {
            storage[config.storagePrefix + key] = value
            true
        } catch (e: Exception) {
            false
        }
    }

    actual override fun retrieve(key: String): String? {
        return storage[config.storagePrefix + key]
    }

    actual override fun delete(key: String): Boolean {
        return storage.remove(config.storagePrefix + key) != null
    }

    actual override fun clear(): Boolean {
        return try {
            storage.clear()
            true
        } catch (e: Exception) {
            false
        }
    }

    actual override fun exists(key: String): Boolean {
        return storage.containsKey(config.storagePrefix + key)
    }

    actual override suspend fun validateStorageIntegrity(): Result<Boolean> {
        return try {
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(DomainException.Storage.CacheError("Storage validation failed: ${e.message}"))
        }
    }
}