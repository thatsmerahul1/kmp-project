package com.weather.data.database

import com.weather.domain.common.Logger
import com.weather.domain.common.DomainException
import com.weather.domain.common.Result

/**
 * Database migration abstraction for 2025 architecture standards
 * 
 * This abstraction provides:
 * - Platform-agnostic migration interface
 * - Type-safe migration definitions
 * - Migration rollback support
 * - Migration validation
 * - Integration hooks for Room (Android) and SQLDelight (shared)
 */

/**
 * Represents a database migration with version information
 */
abstract class DatabaseMigration(
    val fromVersion: Int,
    val toVersion: Int
) {
    /**
     * Execute the migration
     */
    abstract suspend fun migrate(database: DatabaseContext): Result<Unit>
    
    /**
     * Validate migration can be executed
     */
    open suspend fun validate(database: DatabaseContext): Result<Unit> {
        return Result.Success(Unit)
    }
    
    /**
     * Rollback migration (optional)
     */
    open suspend fun rollback(database: DatabaseContext): Result<Unit> {
        return Result.Error(DomainException.Database.UnsupportedOperation("Rollback not supported for migration $fromVersion -> $toVersion"))
    }
    
    /**
     * Description of what this migration does
     */
    abstract val description: String
}

/**
 * Database context abstraction for platform-specific operations
 */
interface DatabaseContext {
    /**
     * Execute SQL statement
     */
    suspend fun executeSQL(sql: String): Result<Unit>
    
    /**
     * Execute SQL query and return results
     */
    suspend fun <T> query(sql: String, mapper: (QueryResult) -> T): Result<List<T>>
    
    /**
     * Begin transaction
     */
    suspend fun beginTransaction(): Result<Unit>
    
    /**
     * Commit transaction
     */
    suspend fun commitTransaction(): Result<Unit>
    
    /**
     * Rollback transaction
     */
    suspend fun rollbackTransaction(): Result<Unit>
    
    /**
     * Check if table exists
     */
    suspend fun tableExists(tableName: String): Result<Boolean>
    
    /**
     * Get current database version
     */
    suspend fun getDatabaseVersion(): Result<Int>
    
    /**
     * Set database version
     */
    suspend fun setDatabaseVersion(version: Int): Result<Unit>
}

/**
 * Query result abstraction
 */
interface QueryResult {
    fun getString(columnName: String): String?
    fun getInt(columnName: String): Int?
    fun getLong(columnName: String): Long?
    fun getDouble(columnName: String): Double?
    fun getBoolean(columnName: String): Boolean?
    fun getByteArray(columnName: String): ByteArray?
}

/**
 * Migration manager for handling database schema changes
 */
class DatabaseMigrationManager(
    private val logger: Logger,
    private val migrations: List<DatabaseMigration>
) {
    
    /**
     * Execute all pending migrations
     */
    suspend fun migrate(database: DatabaseContext, targetVersion: Int): Result<Unit> {
        return try {
            val currentVersionResult = database.getDatabaseVersion()
            if (currentVersionResult is Result.Error) {
                return currentVersionResult
            }
            val currentVersion = (currentVersionResult as Result.Success).data
            
            if (currentVersion == targetVersion) {
                logger.info("DatabaseMigration", "Database already at target version $targetVersion")
                return Result.Success(Unit)
            }
            
            val pendingMigrations = getPendingMigrations(currentVersion, targetVersion)
            
            if (pendingMigrations.isEmpty()) {
                return Result.Error(DomainException.Database.MigrationError("No migration path found from $currentVersion to $targetVersion"))
            }
            
            logger.info("DatabaseMigration", "Starting migration from $currentVersion to $targetVersion (${pendingMigrations.size} steps)")
            
            val beginResult = database.beginTransaction()
            if (beginResult is Result.Error) {
                return beginResult
            }
            
            try {
                for (migration in pendingMigrations) {
                    logger.debug("DatabaseMigration", "Executing migration: ${migration.description}")
                    
                    // Validate migration
                    val validateResult = migration.validate(database)
                    if (validateResult is Result.Error) {
                        database.rollbackTransaction()
                        return validateResult
                    }
                    
                    // Execute migration
                    val migrateResult = migration.migrate(database)
                    if (migrateResult is Result.Error) {
                        database.rollbackTransaction()
                        return migrateResult
                    }
                    
                    // Update version
                    val versionResult = database.setDatabaseVersion(migration.toVersion)
                    if (versionResult is Result.Error) {
                        database.rollbackTransaction()
                        return versionResult
                    }
                    
                    logger.info("DatabaseMigration", "Migration completed: ${migration.fromVersion} -> ${migration.toVersion}")
                }
                
                val commitResult = database.commitTransaction()
                if (commitResult is Result.Error) {
                    return commitResult
                }
                logger.info("DatabaseMigration", "All migrations completed successfully")
                Result.Success(Unit)
                
            } catch (e: Exception) {
                database.rollbackTransaction()
                logger.error("DatabaseMigration", "Migration failed, rolling back", e)
                Result.Error(DomainException.Database.MigrationError("Migration failed: ${e.message}"))
            }
            
        } catch (e: Exception) {
            logger.error("DatabaseMigration", "Migration setup failed", e)
            Result.Error(DomainException.Database.MigrationError("Migration setup failed: ${e.message}"))
        }
    }
    
    /**
     * Get migrations that need to be executed
     */
    private fun getPendingMigrations(currentVersion: Int, targetVersion: Int): List<DatabaseMigration> {
        return if (targetVersion > currentVersion) {
            // Forward migration
            migrations
                .filter { it.fromVersion >= currentVersion && it.toVersion <= targetVersion }
                .sortedBy { it.fromVersion }
        } else {
            // Backward migration (if supported)
            migrations
                .filter { it.toVersion <= currentVersion && it.fromVersion >= targetVersion }
                .sortedByDescending { it.toVersion }
        }
    }
    
    /**
     * Validate migration path exists
     */
    fun validateMigrationPath(fromVersion: Int, toVersion: Int): Result<Unit> {
        val path = getPendingMigrations(fromVersion, toVersion)
        
        if (path.isEmpty() && fromVersion != toVersion) {
            return Result.Error(DomainException.Database.MigrationError("No migration path from $fromVersion to $toVersion"))
        }
        
        // Validate path continuity
        var expectedVersion = fromVersion
        for (migration in path) {
            if (migration.fromVersion != expectedVersion) {
                return Result.Error(DomainException.Database.MigrationError("Migration path broken at version $expectedVersion"))
            }
            expectedVersion = migration.toVersion
        }
        
        if (expectedVersion != toVersion) {
            return Result.Error(DomainException.Database.MigrationError("Migration path does not reach target version $toVersion"))
        }
        
        return Result.Success(Unit)
    }
}

/**
 * Weather app specific migrations
 */
object WeatherMigrations {
    
    /**
     * Migration from version 1 to 2: Add weather caching
     */
    val MIGRATION_1_2 = object : DatabaseMigration(1, 2) {
        override val description = "Add weather data caching tables"
        
        override suspend fun migrate(database: DatabaseContext): Result<Unit> {
            val sql = """
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
            
            return database.executeSQL(sql)
        }
        
        override suspend fun rollback(database: DatabaseContext): Result<Unit> {
            return database.executeSQL("DROP TABLE weather_cache;")
        }
    }
    
    /**
     * Migration from version 2 to 3: Add weather forecast support
     */
    val MIGRATION_2_3 = object : DatabaseMigration(2, 3) {
        override val description = "Add weather forecast tables"
        
        override suspend fun migrate(database: DatabaseContext): Result<Unit> {
            val sql = """
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
            
            return database.executeSQL(sql)
        }
        
        override suspend fun rollback(database: DatabaseContext): Result<Unit> {
            return database.executeSQL("DROP TABLE weather_forecast;")
        }
    }
    
    /**
     * Migration from version 3 to 4: Add user preferences
     */
    val MIGRATION_3_4 = object : DatabaseMigration(3, 4) {
        override val description = "Add user preferences table"
        
        override suspend fun migrate(database: DatabaseContext): Result<Unit> {
            val sql = """
                CREATE TABLE user_preferences (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    key TEXT NOT NULL UNIQUE,
                    value TEXT NOT NULL,
                    type TEXT NOT NULL,
                    updated_at INTEGER NOT NULL
                );
                
                CREATE INDEX idx_preferences_key ON user_preferences(key);
                
                -- Insert default preferences
                INSERT INTO user_preferences (key, value, type, updated_at) VALUES 
                ('temperature_unit', 'celsius', 'string', strftime('%s', 'now') * 1000),
                ('auto_refresh', 'true', 'boolean', strftime('%s', 'now') * 1000),
                ('refresh_interval', '30', 'integer', strftime('%s', 'now') * 1000);
            """.trimIndent()
            
            return database.executeSQL(sql)
        }
        
        override suspend fun rollback(database: DatabaseContext): Result<Unit> {
            return database.executeSQL("DROP TABLE user_preferences;")
        }
    }
    
    /**
     * All available migrations in order
     */
    val ALL_MIGRATIONS = listOf(
        MIGRATION_1_2,
        MIGRATION_2_3,
        MIGRATION_3_4
    )
}

/**
 * Room-specific migration wrapper for Android
 * Implementation moved to androidMain/RoomMigrationBridge.kt
 */

/**
 * SQLDelight migration support
 */
interface SQLDelightMigrationSupport {
    /**
     * Execute migration using SQLDelight
     */
    suspend fun executeMigration(migration: DatabaseMigration): Result<Unit>
    
    /**
     * Get current schema version
     */
    suspend fun getCurrentSchemaVersion(): Result<Int>
    
    /**
     * Set schema version
     */
    suspend fun setSchemaVersion(version: Int): Result<Unit>
}

/**
 * Migration validation utilities
 */
object MigrationValidator {
    
    /**
     * Validate migration sequence
     */
    fun validateMigrationSequence(migrations: List<DatabaseMigration>): Result<Unit> {
        val sortedMigrations = migrations.sortedBy { it.fromVersion }
        
        for (i in 0 until sortedMigrations.size - 1) {
            val current = sortedMigrations[i]
            val next = sortedMigrations[i + 1]
            
            if (current.toVersion != next.fromVersion) {
                return Result.Error(
                    DomainException.Database.MigrationError(
                        "Migration gap found: ${current.toVersion} -> ${next.fromVersion}"
                    )
                )
            }
        }
        
        return Result.Success(Unit)
    }
    
    /**
     * Validate migration doesn't have conflicts
     */
    fun validateNoConflicts(migrations: List<DatabaseMigration>): Result<Unit> {
        val versionPairs = migrations.map { it.fromVersion to it.toVersion }.toSet()
        
        if (versionPairs.size != migrations.size) {
            return Result.Error(
                DomainException.Database.MigrationError("Duplicate migration versions found")
            )
        }
        
        return Result.Success(Unit)
    }
}

/**
 * Database schema version management
 */
object DatabaseSchema {
    const val CURRENT_VERSION = 4
    const val MINIMUM_SUPPORTED_VERSION = 1
    
    /**
     * Get target migration version
     */
    fun getTargetVersion(): Int = CURRENT_VERSION
    
    /**
     * Check if version is supported
     */
    fun isVersionSupported(version: Int): Boolean {
        return version in MINIMUM_SUPPORTED_VERSION..CURRENT_VERSION
    }
    
    /**
     * Get all migrations for version range
     */
    fun getMigrationsForRange(fromVersion: Int, toVersion: Int): List<DatabaseMigration> {
        return WeatherMigrations.ALL_MIGRATIONS.filter { migration ->
            migration.fromVersion >= fromVersion && migration.toVersion <= toVersion
        }
    }
}