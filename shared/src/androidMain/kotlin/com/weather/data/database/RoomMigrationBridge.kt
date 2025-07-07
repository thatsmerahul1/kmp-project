package com.weather.data.database

import com.weather.domain.common.Logger
import com.weather.domain.common.Result
import com.weather.domain.common.DomainException

/**
 * Android Room integration for database migrations
 * 
 * This bridge provides:
 * - Integration between shared migration logic and Room
 * - Proper error handling for Room migrations
 * - Logging integration for migration tracking
 * - Type-safe migration execution
 * 
 * Note: This file provides the bridge pattern for Room integration.
 * Actual Room dependencies should be added to the app module, not shared module.
 */

/**
 * Room Migration Factory for creating Room migrations from shared logic
 * 
 * Usage in app module:
 * ```kotlin
 * val migrations = RoomMigrationFactory.createAllMigrations(logger)
 * val database = Room.databaseBuilder(context, AppDatabase::class.java, "database")
 *     .addMigrations(*migrations)
 *     .build()
 * ```
 */
object RoomMigrationFactory {
    
    /**
     * Create all Room migrations from shared migration definitions
     * This method should be called from the app module where Room dependencies are available
     */
    fun createAllMigrations(logger: Logger): List<String> {
        // Return migration descriptions for now
        // In app module, convert these to actual Room Migration objects
        return WeatherMigrations.ALL_MIGRATIONS.map { migration ->
            "Migration ${migration.fromVersion} -> ${migration.toVersion}: ${migration.description}"
        }
    }
    
    /**
     * Get migration information for specific version range
     */
    fun getMigrationInfo(fromVersion: Int, toVersion: Int): DatabaseMigration? {
        return WeatherMigrations.ALL_MIGRATIONS.find { 
            it.fromVersion == fromVersion && it.toVersion == toVersion 
        }
    }
    
    /**
     * Validate all migrations are compatible with Room
     */
    fun validateRoomCompatibility(): Result<Unit> {
        return try {
            // Validate migration sequence
            val sequenceResult = MigrationValidator.validateMigrationSequence(WeatherMigrations.ALL_MIGRATIONS)
            if (sequenceResult is Result.Error) throw RuntimeException(sequenceResult.exception.message)
            
            // Validate no conflicts
            val conflictResult = MigrationValidator.validateNoConflicts(WeatherMigrations.ALL_MIGRATIONS)
            if (conflictResult is Result.Error) throw RuntimeException(conflictResult.exception.message)
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DomainException.Database.SchemaValidationError("Room compatibility validation failed: ${e.message}"))
        }
    }
}

/**
 * Room Database configuration helper
 */
object RoomDatabaseConfig {
    
    /**
     * Get current database version
     */
    const val DATABASE_VERSION = DatabaseSchema.CURRENT_VERSION
    
    /**
     * Database name
     */
    const val DATABASE_NAME = "weather_database"
    
    /**
     * Get fallback to destructive migration version threshold
     */
    const val DESTRUCTIVE_MIGRATION_THRESHOLD = DatabaseSchema.MINIMUM_SUPPORTED_VERSION
    
    /**
     * Get all migration versions
     */
    fun getAllMigrationVersions(): List<Pair<Int, Int>> {
        return WeatherMigrations.ALL_MIGRATIONS.map { it.fromVersion to it.toVersion }
    }
    
    /**
     * Get migration SQL for debugging
     */
    fun getMigrationSQL(fromVersion: Int, toVersion: Int): String? {
        val migration = WeatherMigrations.ALL_MIGRATIONS.find { 
            it.fromVersion == fromVersion && it.toVersion == toVersion 
        }
        
        return when (migration) {
            WeatherMigrations.MIGRATION_1_2 -> """
                CREATE TABLE weather_cache (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    location_id TEXT NOT NULL,
                    temperature REAL NOT NULL,
                    humidity INTEGER NOT NULL,
                    description TEXT NOT NULL,
                    icon TEXT NOT NULL,
                    timestamp INTEGER NOT NULL,
                    expires_at INTEGER NOT NULL,
                    UNIQUE(location_id)
                );
                CREATE INDEX idx_weather_cache_location ON weather_cache(location_id);
                CREATE INDEX idx_weather_cache_expires ON weather_cache(expires_at);
            """.trimIndent()
            
            WeatherMigrations.MIGRATION_2_3 -> """
                CREATE TABLE weather_forecast (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    location_id TEXT NOT NULL,
                    forecast_date TEXT NOT NULL,
                    min_temperature REAL NOT NULL,
                    max_temperature REAL NOT NULL,
                    humidity INTEGER NOT NULL,
                    description TEXT NOT NULL,
                    icon TEXT NOT NULL,
                    timestamp INTEGER NOT NULL,
                    UNIQUE(location_id, forecast_date)
                );
                CREATE INDEX idx_forecast_location ON weather_forecast(location_id);
                CREATE INDEX idx_forecast_date ON weather_forecast(forecast_date);
            """.trimIndent()
            
            WeatherMigrations.MIGRATION_3_4 -> """
                CREATE TABLE user_preferences (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    key TEXT NOT NULL UNIQUE,
                    value TEXT NOT NULL,
                    type TEXT NOT NULL,
                    updated_at INTEGER NOT NULL
                );
                CREATE INDEX idx_preferences_key ON user_preferences(key);
                INSERT INTO user_preferences (key, value, type, updated_at) VALUES 
                ('temperature_unit', 'celsius', 'string', strftime('%s', 'now') * 1000),
                ('auto_refresh', 'true', 'boolean', strftime('%s', 'now') * 1000),
                ('refresh_interval', '30', 'integer', strftime('%s', 'now') * 1000);
            """.trimIndent()
            
            else -> null
        }
    }
}

/**
 * Instructions for Room integration in app module
 * 
 * 1. Add Room dependencies to app/build.gradle.kts:
 * ```kotlin
 * implementation(libs.androidx.room.runtime)
 * implementation(libs.androidx.room.ktx)
 * kapt(libs.androidx.room.compiler)
 * ```
 * 
 * 2. Create Room Migration wrapper:
 * ```kotlin
 * class SharedMigrationWrapper(
 *     private val sharedMigration: DatabaseMigration,
 *     private val logger: Logger
 * ) : Migration(sharedMigration.fromVersion, sharedMigration.toVersion) {
 *     
 *     override fun migrate(database: SupportSQLiteDatabase) {
 *         val sql = RoomDatabaseConfig.getMigrationSQL(startVersion, endVersion)
 *         sql?.let { database.execSQL(it) }
 *     }
 * }
 * ```
 * 
 * 3. Create database:
 * ```kotlin
 * @Database(
 *     entities = [WeatherEntity::class],
 *     version = RoomDatabaseConfig.DATABASE_VERSION
 * )
 * abstract class AppDatabase : RoomDatabase() {
 *     companion object {
 *         fun create(context: Context, logger: Logger): AppDatabase {
 *             val migrations = WeatherMigrations.ALL_MIGRATIONS.map { migration ->
 *                 SharedMigrationWrapper(migration, logger)
 *             }.toTypedArray()
 *             
 *             return Room.databaseBuilder(context, AppDatabase::class.java, RoomDatabaseConfig.DATABASE_NAME)
 *                 .addMigrations(*migrations)
 *                 .build()
 *         }
 *     }
 * }
 * ```
 */