plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kover) apply false
}

// Apply Kover to root project for aggregated coverage
apply(plugin = "org.jetbrains.kotlinx.kover")

// Apply custom test tasks
apply(from = "$rootDir/gradle/test-tasks.gradle.kts")