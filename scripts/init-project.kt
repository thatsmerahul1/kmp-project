#!/usr/bin/env kotlin

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * WeatherKMP 2025 Project Initialization Wizard
 * 
 * Automated project setup with all modern standards:
 * - Security infrastructure
 * - Monitoring and telemetry
 * - Modern architecture patterns
 * - CI/CD pipeline
 * - Testing framework
 * - Documentation templates
 */

data class ProjectConfig(
    val projectName: String,
    val packageName: String,
    val organizationName: String,
    val targetPlatforms: List<Platform>,
    val features: List<Feature>,
    val monitoringProvider: MonitoringProvider,
    val cicdProvider: CicdProvider,
    val outputDirectory: String
)

enum class Platform {
    ANDROID, IOS, DESKTOP, WEB
}

enum class Feature {
    SECURITY_ENCRYPTION,
    MONITORING_TELEMETRY,
    CRASH_ANALYTICS,
    OFFLINE_SUPPORT,
    PUSH_NOTIFICATIONS,
    BIOMETRIC_AUTH,
    FEATURE_TOGGLES,
    COMPOSE_MULTIPLATFORM
}

enum class MonitoringProvider {
    PROMETHEUS_GRAFANA,
    DATADOG,
    NEW_RELIC,
    CUSTOM
}

enum class CicdProvider {
    GITHUB_ACTIONS,
    GITLAB_CI,
    AZURE_DEVOPS,
    JENKINS
}

class ProjectInitializer {
    
    fun initializeProject(config: ProjectConfig) {
        println("🚀 Initializing WeatherKMP 2025 project: ${config.projectName}")
        println("📦 Package: ${config.packageName}")
        println("🏢 Organization: ${config.organizationName}")
        println("🎯 Platforms: ${config.targetPlatforms.joinToString(", ")}")
        println("✨ Features: ${config.features.joinToString(", ")}")
        
        val projectDir = File(config.outputDirectory, config.projectName)
        
        try {
            // Create project structure
            createProjectStructure(projectDir, config)
            
            // Generate configuration files
            generateConfigurationFiles(projectDir, config)
            
            // Setup security infrastructure
            setupSecurityInfrastructure(projectDir, config)
            
            // Setup monitoring and telemetry
            setupMonitoringInfrastructure(projectDir, config)
            
            // Generate CI/CD pipeline
            generateCicdPipeline(projectDir, config)
            
            // Create documentation
            generateDocumentation(projectDir, config)
            
            // Generate sample code
            generateSampleCode(projectDir, config)
            
            // Create initialization summary
            createInitializationSummary(projectDir, config)
            
            println("✅ Project initialization completed successfully!")
            println("📁 Project location: ${projectDir.absolutePath}")
            
        } catch (e: Exception) {
            println("❌ Project initialization failed: ${e.message}")
            throw e
        }
    }
    
    private fun createProjectStructure(projectDir: File, config: ProjectConfig) {
        println("📁 Creating project structure...")
        
        val directories = listOf(
            // Core structure
            "shared/src/commonMain/kotlin/${config.packageName.replace('.', '/')}/",
            "shared/src/commonTest/kotlin/${config.packageName.replace('.', '/')}/",
            "shared/src/androidMain/kotlin/${config.packageName.replace('.', '/')}/",
            "shared/src/androidUnitTest/kotlin/${config.packageName.replace('.', '/')}/",
            "shared/src/iosMain/kotlin/${config.packageName.replace('.', '/')}/",
            "shared/src/iosTest/kotlin/${config.packageName.replace('.', '/')}/",
            
            // Android app
            "androidApp/src/main/kotlin/${config.packageName.replace('.', '/')}/",
            "androidApp/src/test/kotlin/${config.packageName.replace('.', '/')}/",
            "androidApp/src/androidTest/kotlin/${config.packageName.replace('.', '/')}/",
            "androidApp/src/main/res/values/",
            "androidApp/src/main/res/layout/",
            "androidApp/src/main/res/drawable/",
            
            // iOS app
            "iosApp/iosApp/",
            "iosApp/iosAppTests/",
            
            // Desktop (if enabled)
            if (config.targetPlatforms.contains(Platform.DESKTOP)) "desktopApp/src/main/kotlin/${config.packageName.replace('.', '/')}/" else null,
            
            // Web (if enabled)
            if (config.targetPlatforms.contains(Platform.WEB)) "webApp/src/main/kotlin/${config.packageName.replace('.', '/')}/" else null,
            
            // Feature modules
            "shared/src/commonMain/kotlin/${config.packageName.replace('.', '/')}/data/",
            "shared/src/commonMain/kotlin/${config.packageName.replace('.', '/')}/domain/",
            "shared/src/commonMain/kotlin/${config.packageName.replace('.', '/')}/presentation/",
            "shared/src/commonMain/kotlin/${config.packageName.replace('.', '/')}/di/",
            
            // Security
            if (config.features.contains(Feature.SECURITY_ENCRYPTION)) 
                "shared/src/commonMain/kotlin/${config.packageName.replace('.', '/')}/security/" else null,
            
            // Monitoring
            if (config.features.contains(Feature.MONITORING_TELEMETRY)) 
                "shared/src/commonMain/kotlin/${config.packageName.replace('.', '/')}/monitoring/" else null,
            
            // Configuration and scripts
            "scripts/",
            "docs/",
            "monitoring/",
            "security/",
            ".github/workflows/",
            "gradle/",
            "build-logic/",
            
            // Test infrastructure
            "shared/src/commonTest/kotlin/${config.packageName.replace('.', '/')}/testutils/",
            "test-reports/",
            
            // Resources
            "shared/src/commonMain/resources/",
            "shared/src/androidMain/res/",
            "shared/src/iosMain/",
        ).filterNotNull()
        
        directories.forEach { dir ->
            File(projectDir, dir).mkdirs()
        }
        
        println("✅ Created ${directories.size} directories")
    }
    
    private fun generateConfigurationFiles(projectDir: File, config: ProjectConfig) {
        println("⚙️ Generating configuration files...")
        
        // Root build.gradle.kts
        generateRootBuildGradle(projectDir, config)
        
        // Shared module build.gradle.kts
        generateSharedBuildGradle(projectDir, config)
        
        // Android app build.gradle.kts
        generateAndroidAppBuildGradle(projectDir, config)
        
        // Gradle properties
        generateGradleProperties(projectDir, config)
        
        // Settings.gradle.kts
        generateSettingsGradle(projectDir, config)
        
        // Gradle wrapper
        generateGradleWrapper(projectDir, config)
        
        println("✅ Generated configuration files")
    }
    
    private fun generateRootBuildGradle(projectDir: File, config: ProjectConfig) {
        val content = """
plugins {
    // Kotlin Multiplatform
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinCocoapods) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    
    // Compose Multiplatform
    ${if (config.features.contains(Feature.COMPOSE_MULTIPLATFORM)) 
        "alias(libs.plugins.compose.compiler) apply false\n    alias(libs.plugins.jetbrainsCompose) apply false" 
        else "// Compose Multiplatform disabled"}
    
    // Quality & Security
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.kotlinter) apply false
    
    // Documentation
    alias(libs.plugins.dokka) apply false
    
    // Dependency analysis
    alias(libs.plugins.dependencyGuard) apply false
    alias(libs.plugins.gradleVersions) apply false
}

// Project-wide configurations
allprojects {
    group = "${config.packageName}"
    version = "2025.1.0"
    
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

// Quality gates for all modules
subprojects {
    apply(plugin = "org.jmailen.kotlinter")
    apply(plugin = "io.gitlab.arturbosch.detekt")
    
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs += listOf(
                "-Xopt-in=kotlin.RequiresOptIn",
                "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi"
            )
        }
    }
}

// Security scanning task
tasks.register("securityScan") {
    group = "verification"
    description = "Run security scans on the project"
    
    doLast {
        println("🔒 Running security scans...")
        // Integration with OWASP, Snyk, or other security tools
    }
}

// Performance testing
tasks.register("performanceTest") {
    group = "verification"
    description = "Run performance tests"
    
    doLast {
        println("⚡ Running performance tests...")
    }
}

// Generate project documentation
tasks.register("generateDocs") {
    group = "documentation"
    description = "Generate comprehensive project documentation"
    
    doLast {
        println("📚 Generating project documentation...")
    }
}
        """.trimIndent()
        
        File(projectDir, "build.gradle.kts").writeText(content)
    }
    
    private fun generateSharedBuildGradle(projectDir: File, config: ProjectConfig) {
        val content = """
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    ${if (config.features.contains(Feature.COMPOSE_MULTIPLATFORM)) 
        "alias(libs.plugins.jetbrainsCompose)\n    alias(libs.plugins.compose.compiler)" 
        else "// Compose Multiplatform disabled"}
    alias(libs.plugins.dokka)
}

kotlin {
    // Target platforms
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    
    ${if (config.targetPlatforms.contains(Platform.IOS)) """
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }""" else "// iOS targets disabled"}
    
    ${if (config.targetPlatforms.contains(Platform.DESKTOP)) """
    jvm("desktop") {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }""" else "// Desktop target disabled"}
    
    ${if (config.targetPlatforms.contains(Platform.WEB)) """
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
    }""" else "// Web target disabled"}
    
    sourceSets {
        commonMain.dependencies {
            // Kotlin
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            
            // Networking
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.client.contentnegotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            
            // Dependency Injection
            implementation(libs.koin.core)
            
            // Database
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
            
            ${if (config.features.contains(Feature.COMPOSE_MULTIPLATFORM)) """
            // Compose Multiplatform
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.navigation.compose)""" else "// Compose dependencies disabled"}
            
            ${if (config.features.contains(Feature.MONITORING_TELEMETRY)) """
            // Monitoring & Telemetry (Custom implementation)
            // implementation("io.opentelemetry:opentelemetry-api")""" else "// Monitoring dependencies disabled"}
        }
        
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.kotest.assertions.core)
        }
        
        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.koin.android)
            
            ${if (config.features.contains(Feature.SECURITY_ENCRYPTION)) """
            // Android Security
            implementation(libs.androidx.security.crypto)
            implementation(libs.androidx.biometric)""" else "// Security dependencies disabled"}
            
            ${if (config.features.contains(Feature.CRASH_ANALYTICS)) """
            // Crash Analytics
            implementation(libs.firebase.crashlytics)""" else "// Crash analytics disabled"}
        }
        
        ${if (config.targetPlatforms.contains(Platform.IOS)) """
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }""" else "// iOS dependencies disabled"}
        
        ${if (config.targetPlatforms.contains(Platform.DESKTOP)) """
        val desktopMain by getting {
            dependencies {
                implementation(libs.ktor.client.cio)
                implementation(libs.sqldelight.sqlite.driver)
                ${if (config.features.contains(Feature.COMPOSE_MULTIPLATFORM)) 
                    "implementation(compose.desktop.currentOs)" 
                    else "// Compose desktop disabled"}
            }
        }""" else "// Desktop dependencies disabled"}
        
        ${if (config.targetPlatforms.contains(Platform.WEB)) """
        val jsMain by getting {
            dependencies {
                implementation(libs.ktor.client.js)
                ${if (config.features.contains(Feature.COMPOSE_MULTIPLATFORM)) 
                    "implementation(compose.html.core)" 
                    else "// Compose web disabled"}
            }
        }""" else "// Web dependencies disabled"}
    }
}

android {
    namespace = "${config.packageName}.shared"
    compileSdk = 34
    
    defaultConfig {
        minSdk = 24
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    ${if (config.features.contains(Feature.SECURITY_ENCRYPTION)) """
    buildTypes {
        release {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }""" else "// ProGuard configuration disabled"}
}

// Documentation generation
tasks.dokkaHtml.configure {
    outputDirectory.set(project.file("../docs/api"))
}

// Custom tasks for project management
tasks.register("generateFeatureToggleConfig") {
    group = "project"
    description = "Generate feature toggle configuration"
    
    doLast {
        val featureConfig = """
            {
              "features": {
                ${config.features.joinToString(",\n                ") { "\"${it.name.lowercase()}\": true" }}
              },
              "environment": "development",
              "version": "2025.1.0"
            }
        """.trimIndent()
        
        File(project.projectDir, "src/commonMain/resources/feature-config.json").apply {
            parentFile.mkdirs()
            writeText(featureConfig)
        }
        
        println("✅ Generated feature toggle configuration")
    }
}

// Performance benchmarking
tasks.register("benchmark") {
    group = "verification"
    description = "Run performance benchmarks"
    
    doLast {
        println("📊 Running performance benchmarks...")
        // Integration with benchmarking tools
    }
}
        """.trimIndent()
        
        File(projectDir, "shared/build.gradle.kts").writeText(content)
    }
    
    private fun generateAndroidAppBuildGradle(projectDir: File, config: ProjectConfig) {
        val content = """
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    ${if (config.features.contains(Feature.COMPOSE_MULTIPLATFORM)) 
        "alias(libs.plugins.jetbrainsCompose)\n    alias(libs.plugins.compose.compiler)" 
        else "// Compose plugins disabled"}
    ${if (config.features.contains(Feature.CRASH_ANALYTICS)) 
        "alias(libs.plugins.google.services)\n    alias(libs.plugins.firebase.crashlytics)" 
        else "// Firebase plugins disabled"}
}

android {
    namespace = "${config.packageName}"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "${config.packageName}"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "2025.1.0"
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        
        ${if (config.features.contains(Feature.SECURITY_ENCRYPTION)) """
        // Security configurations
        buildConfigField("String", "API_BASE_URL", "\\"https://api.openweathermap.org/\\"")
        buildConfigField("boolean", "ENABLE_SECURITY_FEATURES", "true")""" 
        else "// Security build configs disabled"}
    }
    
    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            
            ${if (config.features.contains(Feature.MONITORING_TELEMETRY)) """
            buildConfigField("boolean", "ENABLE_TELEMETRY", "true")
            buildConfigField("String", "TELEMETRY_ENDPOINT", "\\"http://localhost:9090/\\"")""" 
            else "// Telemetry configs disabled"}
        }
        
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            ${if (config.features.contains(Feature.SECURITY_ENCRYPTION)) """
            // Enhanced security for release builds
            buildConfigField("boolean", "ENABLE_SECURITY_FEATURES", "true")
            buildConfigField("boolean", "ENABLE_CERTIFICATE_PINNING", "true")""" 
            else "// Security configs disabled"}
            
            ${if (config.features.contains(Feature.MONITORING_TELEMETRY)) """
            buildConfigField("boolean", "ENABLE_TELEMETRY", "true")
            buildConfigField("String", "TELEMETRY_ENDPOINT", "\\"https://monitoring.${config.organizationName.lowercase()}.com/\\"")""" 
            else "// Telemetry configs disabled"}
        }
        
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api"
        )
    }
    
    ${if (config.features.contains(Feature.COMPOSE_MULTIPLATFORM)) """
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }""" else "// Compose build features disabled"}
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(projects.shared)
    
    // Android UI
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    
    ${if (config.features.contains(Feature.COMPOSE_MULTIPLATFORM)) """
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)""" 
    else "// Compose dependencies disabled"}
    
    // Dependency Injection
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    
    ${if (config.features.contains(Feature.SECURITY_ENCRYPTION)) """
    // Security
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.biometric)""" 
    else "// Security dependencies disabled"}
    
    ${if (config.features.contains(Feature.CRASH_ANALYTICS)) """
    // Analytics & Crash Reporting
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)""" 
    else "// Analytics dependencies disabled"}
    
    ${if (config.features.contains(Feature.PUSH_NOTIFICATIONS)) """
    // Push Notifications
    implementation(libs.firebase.messaging)""" 
    else "// Push notification dependencies disabled"}
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotest.assertions.core)
    
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

// Custom tasks
tasks.register("generateAppModule") {
    group = "project"
    description = "Generate app module with all configured features"
    
    doLast {
        println("🏗️ Generating app module with features: ${config.features.joinToString(", ")}")
    }
}
        """.trimIndent()
        
        File(projectDir, "androidApp/build.gradle.kts").writeText(content)
    }
    
    private fun generateGradleProperties(projectDir: File, config: ProjectConfig) {
        val content = """
# Gradle Properties for ${config.projectName}
# Generated on ${LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}

# Kotlin
kotlin.code.style=official
kotlin.incremental=true
kotlin.incremental.multiplatform=true
kotlin.native.binary.memoryModel=experimental
kotlin.native.binary.freezing=disabled

# Android
android.useAndroidX=true
android.nonTransitiveRClass=true

# Gradle
org.gradle.jvmargs=-Xmx4g -XX:+UseParallelGC -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configuration-cache=true

# Project specific
${config.projectName.uppercase()}_VERSION=2025.1.0
${config.projectName.uppercase()}_GROUP=${config.packageName}

# Feature flags (can be overridden in local.properties)
ENABLE_SECURITY_FEATURES=${config.features.contains(Feature.SECURITY_ENCRYPTION)}
ENABLE_MONITORING=${config.features.contains(Feature.MONITORING_TELEMETRY)}
ENABLE_CRASH_ANALYTICS=${config.features.contains(Feature.CRASH_ANALYTICS)}
ENABLE_COMPOSE_MULTIPLATFORM=${config.features.contains(Feature.COMPOSE_MULTIPLATFORM)}
ENABLE_FEATURE_TOGGLES=${config.features.contains(Feature.FEATURE_TOGGLES)}

# Build optimization
kotlin.mpp.stability.nowarn=true
kotlin.mpp.androidSourceSetLayoutVersion=2
kotlin.native.ignoreDisabledTargets=true

# Compose
compose.kotlin.compiler.extension.version=1.5.8
        """.trimIndent()
        
        File(projectDir, "gradle.properties").writeText(content)
    }
    
    private fun generateSettingsGradle(projectDir: File, config: ProjectConfig) {
        val content = """
rootProject.name = "${config.projectName}"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

include(":androidApp")
include(":shared")

${if (config.targetPlatforms.contains(Platform.DESKTOP)) "include(\":desktopApp\")" else "// Desktop app disabled"}
${if (config.targetPlatforms.contains(Platform.WEB)) "include(\":webApp\")" else "// Web app disabled"}

// Build logic
includeBuild("build-logic")
        """.trimIndent()
        
        File(projectDir, "settings.gradle.kts").writeText(content)
    }
    
    private fun generateGradleWrapper(projectDir: File, config: ProjectConfig) {
        // Create gradle wrapper directory
        val wrapperDir = File(projectDir, "gradle/wrapper")
        wrapperDir.mkdirs()
        
        // gradle-wrapper.properties
        val wrapperProperties = """
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\\://services.gradle.org/distributions/gradle-8.5-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
        """.trimIndent()
        
        File(wrapperDir, "gradle-wrapper.properties").writeText(wrapperProperties)
        
        // gradlew script
        val gradlewScript = """
#!/bin/sh

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Attempt to set APP_HOME
# Resolve links: ${'$'}0 may be a link
PRG="${'$'}0"
# Need this for relative symlinks.
while [ -h "${'$'}PRG" ] ; do
    ls=`ls -ld "${'$'}PRG"`
    link=`expr "${'$'}ls" : '.*-> \(.*\)${'$'}'`
    if expr "${'$'}link" : '/.*' > /dev/null; then
        PRG="${'$'}link"
    else
        PRG=`dirname "${'$'}PRG"`"/${'$'}link"
    fi
done
SAVED="`pwd`"
cd "`dirname \"${'$'}PRG\"`/" >/dev/null
APP_HOME="`pwd -P`"
cd "${'$'}SAVED" >/dev/null

APP_NAME="Gradle"
APP_BASE_NAME=`basename "${'$'}0"`

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn () {
    echo "${'$'}*"
}

die () {
    echo
    echo "${'$'}*"
    echo
    exit 1
}

exec "${'$'}APP_HOME/gradlew" "${'$'}@"
        """.trimIndent()
        
        File(projectDir, "gradlew").apply {
            writeText(gradlewScript)
            setExecutable(true)
        }
    }
    
    private fun setupSecurityInfrastructure(projectDir: File, config: ProjectConfig) {
        if (!config.features.contains(Feature.SECURITY_ENCRYPTION)) return
        
        println("🔒 Setting up security infrastructure...")
        
        // Copy security files from the template
        val securityFiles = listOf(
            "shared/src/commonMain/kotlin/${config.packageName.replace('.', '/')}/security/",
            "androidApp/proguard-security.pro",
            "security/",
            ".github/workflows/security-audit.yml"
        )
        
        // Create ProGuard security rules
        val proguardRules = """
# Security ProGuard rules for ${config.projectName}
# Generated on ${LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}

# Obfuscation
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-forceprocessing

# Keep security-critical classes
-keep class ${config.packageName}.security.** { *; }
-keep class ${config.packageName}.data.remote.** { *; }

# Kotlin serialization
-keep @kotlinx.serialization.Serializable class ** { *; }

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# API key obfuscation
-keep class ${config.packageName}.BuildConfig { *; }
-keepclassmembers class ${config.packageName}.BuildConfig {
    public static final java.lang.String API_KEY;
}
        """.trimIndent()
        
        File(projectDir, "androidApp/proguard-security.pro").writeText(proguardRules)
        
        println("✅ Security infrastructure setup completed")
    }
    
    private fun setupMonitoringInfrastructure(projectDir: File, config: ProjectConfig) {
        if (!config.features.contains(Feature.MONITORING_TELEMETRY)) return
        
        println("📊 Setting up monitoring infrastructure...")
        
        // Generate monitoring configuration based on provider
        when (config.monitoringProvider) {
            MonitoringProvider.PROMETHEUS_GRAFANA -> generatePrometheusConfig(projectDir, config)
            MonitoringProvider.DATADOG -> generateDataDogConfig(projectDir, config)
            MonitoringProvider.NEW_RELIC -> generateNewRelicConfig(projectDir, config)
            MonitoringProvider.CUSTOM -> generateCustomMonitoringConfig(projectDir, config)
        }
        
        println("✅ Monitoring infrastructure setup completed")
    }
    
    private fun generatePrometheusConfig(projectDir: File, config: ProjectConfig) {
        // Copy monitoring files and adapt them
        val monitoringDir = File(projectDir, "monitoring")
        
        // Create docker-compose for monitoring stack
        val dockerCompose = """
version: '3.8'

services:
  prometheus:
    image: prom/prometheus:latest
    container_name: ${config.projectName.lowercase()}-prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./config/prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/etc/prometheus/console_libraries'
      - '--web.console.templates=/etc/prometheus/consoles'
      - '--storage.tsdb.retention.time=200h'
      - '--web.enable-lifecycle'
    restart: unless-stopped
    
  grafana:
    image: grafana/grafana:latest
    container_name: ${config.projectName.lowercase()}-grafana
    ports:
      - "3000:3000"
    volumes:
      - grafana_data:/var/lib/grafana
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
      - GF_USERS_ALLOW_SIGN_UP=false
    restart: unless-stopped

volumes:
  prometheus_data:
  grafana_data:

networks:
  default:
    name: ${config.projectName.lowercase()}-monitoring
        """.trimIndent()
        
        File(monitoringDir, "docker-compose.yml").apply {
            parentFile.mkdirs()
            writeText(dockerCompose)
        }
    }
    
    private fun generateDataDogConfig(projectDir: File, config: ProjectConfig) {
        val datadogConfig = """
# DataDog configuration for ${config.projectName}
api_key: YOUR_DATADOG_API_KEY
app_key: YOUR_DATADOG_APP_KEY

# Application configuration
app_name: ${config.projectName.lowercase()}
env: development
service: weather-kmp
version: 2025.1.0

# Monitoring configuration
enable_tracing: true
enable_profiling: true
enable_runtime_metrics: true

# Mobile RUM
mobile_rum:
  application_id: YOUR_RUM_APPLICATION_ID
  client_token: YOUR_RUM_CLIENT_TOKEN
  session_sample_rate: 100
        """.trimIndent()
        
        File(projectDir, "monitoring/datadog.yml").apply {
            parentFile.mkdirs()
            writeText(datadogConfig)
        }
    }
    
    private fun generateNewRelicConfig(projectDir: File, config: ProjectConfig) {
        val newRelicConfig = """
# New Relic configuration for ${config.projectName}
app_name: ${config.projectName}
license_key: YOUR_NEW_RELIC_LICENSE_KEY

# Application settings
environment: development
enabled: true
log_level: info

# Mobile monitoring
mobile:
  application_token: YOUR_MOBILE_APPLICATION_TOKEN
  enable_crash_reporting: true
  enable_interaction_tracing: true
  enable_distributed_tracing: true
        """.trimIndent()
        
        File(projectDir, "monitoring/newrelic.yml").apply {
            parentFile.mkdirs()
            writeText(newRelicConfig)
        }
    }
    
    private fun generateCustomMonitoringConfig(projectDir: File, config: ProjectConfig) {
        val customConfig = """
# Custom monitoring configuration for ${config.projectName}
monitoring:
  enabled: true
  endpoint: "https://monitoring.${config.organizationName.lowercase()}.com"
  api_key: "YOUR_CUSTOM_MONITORING_API_KEY"
  
  metrics:
    collection_interval: 30s
    batch_size: 100
    enabled_collectors:
      - system
      - application
      - business
      - performance
  
  tracing:
    enabled: true
    sample_rate: 0.1
    max_spans_per_trace: 1000
  
  logging:
    level: info
    structured: true
    include_stack_trace: true
        """.trimIndent()
        
        File(projectDir, "monitoring/config.yml").apply {
            parentFile.mkdirs()
            writeText(customConfig)
        }
    }
    
    private fun generateCicdPipeline(projectDir: File, config: ProjectConfig) {
        println("🚀 Generating CI/CD pipeline...")
        
        when (config.cicdProvider) {
            CicdProvider.GITHUB_ACTIONS -> generateGitHubActions(projectDir, config)
            CicdProvider.GITLAB_CI -> generateGitLabCI(projectDir, config)
            CicdProvider.AZURE_DEVOPS -> generateAzureDevOps(projectDir, config)
            CicdProvider.JENKINS -> generateJenkinsfile(projectDir, config)
        }
        
        println("✅ CI/CD pipeline generated")
    }
    
    private fun generateGitHubActions(projectDir: File, config: ProjectConfig) {
        val workflowsDir = File(projectDir, ".github/workflows")
        
        // Main CI workflow
        val ciWorkflow = """
name: CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

env:
  JAVA_VERSION: '17'
  JAVA_DISTRIBUTION: 'zulu'

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK
      uses: actions/setup-java@v4
      with:
        java-version: ${'$'}{{ env.JAVA_VERSION }}
        distribution: ${'$'}{{ env.JAVA_DISTRIBUTION }}
        
    - name: Cache Gradle packages
      uses: actions/cache@v4
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${'$'}{{ runner.os }}-gradle-${'$'}{{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
        restore-keys: |
          ${'$'}{{ runner.os }}-gradle-
          
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
      
    - name: Run tests
      run: ./gradlew test
      
    - name: Run linting
      run: ./gradlew ktlintCheck detekt
      
    ${if (config.features.contains(Feature.SECURITY_ENCRYPTION)) """
    - name: Security scan
      run: ./gradlew securityScan""" else "# Security scan disabled"}
      
    - name: Upload test results
      uses: actions/upload-artifact@v4
      if: always()
      with:
        name: test-results
        path: |
          **/build/reports/tests/
          **/build/reports/detekt/
          
  build:
    needs: test
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK
      uses: actions/setup-java@v4
      with:
        java-version: ${'$'}{{ env.JAVA_VERSION }}
        distribution: ${'$'}{{ env.JAVA_DISTRIBUTION }}
        
    - name: Build project
      run: ./gradlew build
      
    - name: Upload build artifacts
      uses: actions/upload-artifact@v4
      with:
        name: build-artifacts
        path: |
          androidApp/build/outputs/
          shared/build/libs/

  ${if (config.targetPlatforms.contains(Platform.IOS)) """
  build-ios:
    needs: test
    runs-on: macos-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK
      uses: actions/setup-java@v4
      with:
        java-version: ${'$'}{{ env.JAVA_VERSION }}
        distribution: ${'$'}{{ env.JAVA_DISTRIBUTION }}
        
    - name: Build iOS framework
      run: ./gradlew linkPodReleaseFrameworkIosArm64
      
    - name: Upload iOS artifacts
      uses: actions/upload-artifact@v4
      with:
        name: ios-framework
        path: shared/build/bin/iosArm64/releaseFramework/""" else "# iOS build disabled"}
        """.trimIndent()
        
        File(workflowsDir, "ci.yml").apply {
            parentFile.mkdirs()
            writeText(ciWorkflow)
        }
    }
    
    private fun generateGitLabCI(projectDir: File, config: ProjectConfig) {
        val gitlabCI = """
# GitLab CI/CD for ${config.projectName}

stages:
  - test
  - build
  - security
  - deploy

variables:
  GRADLE_OPTS: "-Dorg.gradle.daemon=false"
  GRADLE_USER_HOME: "${'$'}CI_PROJECT_DIR/.gradle"

cache:
  paths:
    - .gradle/

before_script:
  - export GRADLE_USER_HOME=`pwd`/.gradle
  - chmod +x ./gradlew

test:
  stage: test
  image: openjdk:17-jdk
  script:
    - ./gradlew test ktlintCheck detekt
  artifacts:
    reports:
      junit: "**/build/test-results/test/TEST-*.xml"
    paths:
      - "**/build/reports/"
    expire_in: 1 week

build:
  stage: build
  image: openjdk:17-jdk
  script:
    - ./gradlew build
  artifacts:
    paths:
      - "androidApp/build/outputs/"
      - "shared/build/libs/"
    expire_in: 1 week

${if (config.features.contains(Feature.SECURITY_ENCRYPTION)) """
security_scan:
  stage: security
  image: openjdk:17-jdk
  script:
    - ./gradlew securityScan
  artifacts:
    reports:
      security: security-report.json
  allow_failure: true""" else "# Security scan disabled"}

${if (config.targetPlatforms.contains(Platform.IOS)) """
build_ios:
  stage: build
  tags:
    - macos
  script:
    - ./gradlew linkPodReleaseFrameworkIosArm64
  artifacts:
    paths:
      - "shared/build/bin/iosArm64/releaseFramework/"
    expire_in: 1 week""" else "# iOS build disabled"}
        """.trimIndent()
        
        File(projectDir, ".gitlab-ci.yml").writeText(gitlabCI)
    }
    
    private fun generateAzureDevOps(projectDir: File, config: ProjectConfig) {
        val azurePipeline = """
# Azure DevOps Pipeline for ${config.projectName}

trigger:
  branches:
    include:
      - main
      - develop

pool:
  vmImage: 'ubuntu-latest'

variables:
  JAVA_VERSION: '17'
  GRADLE_USER_HOME: $(Pipeline.Workspace)/.gradle

stages:
- stage: Test
  jobs:
  - job: UnitTests
    steps:
    - task: JavaToolInstaller@0
      inputs:
        versionSpec: '$(JAVA_VERSION)'
        jdkArchitectureOption: 'x64'
        jdkSourceOption: 'PreInstalled'
        
    - task: Cache@2
      inputs:
        key: 'gradle | "$(Agent.OS)" | **/build.gradle.kts, **/gradle-wrapper.properties'
        restoreKeys: |
          gradle | "$(Agent.OS)"
          gradle
        path: $(GRADLE_USER_HOME)
      displayName: Cache Gradle packages
      
    - script: |
        chmod +x gradlew
        ./gradlew test ktlintCheck detekt
      displayName: 'Run tests and linting'
      
    - task: PublishTestResults@2
      inputs:
        testResultsFormat: 'JUnit'
        testResultsFiles: '**/TEST-*.xml'
        testRunTitle: 'Unit Tests'
      condition: always()

- stage: Build
  dependsOn: Test
  jobs:
  - job: BuildAndroid
    steps:
    - script: ./gradlew build
      displayName: 'Build project'
      
    - task: PublishBuildArtifacts@1
      inputs:
        PathtoPublish: 'androidApp/build/outputs/'
        ArtifactName: 'android-artifacts'

${if (config.targetPlatforms.contains(Platform.IOS)) """
  - job: BuildiOS
    pool:
      vmImage: 'macOS-latest'
    steps:
    - script: ./gradlew linkPodReleaseFrameworkIosArm64
      displayName: 'Build iOS framework'
      
    - task: PublishBuildArtifacts@1
      inputs:
        PathtoPublish: 'shared/build/bin/iosArm64/releaseFramework/'
        ArtifactName: 'ios-framework'""" else "# iOS build disabled"}
        """.trimIndent()
        
        File(projectDir, "azure-pipelines.yml").writeText(azurePipeline)
    }
    
    private fun generateJenkinsfile(projectDir: File, config: ProjectConfig) {
        val jenkinsfile = """
pipeline {
    agent any
    
    environment {
        JAVA_VERSION = '17'
        GRADLE_OPTS = '-Dorg.gradle.daemon=false'
    }
    
    tools {
        jdk "${'$'}JAVA_VERSION"
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Setup') {
            steps {
                sh 'chmod +x gradlew'
            }
        }
        
        stage('Test') {
            parallel {
                stage('Unit Tests') {
                    steps {
                        sh './gradlew test'
                    }
                    post {
                        always {
                            publishTestResults(
                                testResultsPattern: '**/TEST-*.xml',
                                allowEmptyResults: true
                            )
                        }
                    }
                }
                
                stage('Linting') {
                    steps {
                        sh './gradlew ktlintCheck detekt'
                    }
                    post {
                        always {
                            publishHTML([
                                allowMissing: false,
                                alwaysLinkToLastBuild: true,
                                keepAll: true,
                                reportDir: 'build/reports/detekt',
                                reportFiles: 'detekt.html',
                                reportName: 'Detekt Report'
                            ])
                        }
                    }
                }
                
                ${if (config.features.contains(Feature.SECURITY_ENCRYPTION)) """
                stage('Security Scan') {
                    steps {
                        sh './gradlew securityScan'
                    }
                }""" else "// Security scan disabled"}
            }
        }
        
        stage('Build') {
            parallel {
                stage('Android Build') {
                    steps {
                        sh './gradlew build'
                    }
                    post {
                        always {
                            archiveArtifacts(
                                artifacts: 'androidApp/build/outputs/**/*',
                                allowEmptyArchive: true
                            )
                        }
                    }
                }
                
                ${if (config.targetPlatforms.contains(Platform.IOS)) """
                stage('iOS Build') {
                    agent { label 'macos' }
                    steps {
                        sh './gradlew linkPodReleaseFrameworkIosArm64'
                    }
                    post {
                        always {
                            archiveArtifacts(
                                artifacts: 'shared/build/bin/iosArm64/releaseFramework/**/*',
                                allowEmptyArchive: true
                            )
                        }
                    }
                }""" else "// iOS build disabled"}
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        failure {
            emailext(
                subject: "Build Failed: ${'$'}JOB_NAME - ${'$'}BUILD_NUMBER",
                body: "Build failed. Check console output at ${'$'}BUILD_URL",
                to: "${config.organizationName.lowercase()}@example.com"
            )
        }
    }
}
        """.trimIndent()
        
        File(projectDir, "Jenkinsfile").writeText(jenkinsfile)
    }
    
    private fun generateDocumentation(projectDir: File, config: ProjectConfig) {
        println("📚 Generating documentation...")
        
        val docsDir = File(projectDir, "docs")
        
        // README.md
        val readme = """
# ${config.projectName}

A modern Kotlin Multiplatform weather application built with 2025 standards.

## Overview

${config.projectName} is a comprehensive weather application that demonstrates modern KMP development practices, including security, monitoring, testing, and cross-platform compatibility.

## Features

${config.features.joinToString("\n") { "- ${it.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }}" }}

## Supported Platforms

${config.targetPlatforms.joinToString("\n") { "- ${it.name.lowercase().replaceFirstChar { it.uppercase() }}" }}

## Architecture

The project follows Clean Architecture principles with:

- **Domain Layer**: Business logic and entities
- **Data Layer**: Repositories, data sources, and DTOs  
- **Presentation Layer**: UI components and view models

### Key Technologies

- **Kotlin Multiplatform**: Shared business logic across platforms
- **Ktor**: Networking and HTTP client
- **SQLDelight**: Database management
- **Kotlinx.Serialization**: JSON serialization
- **Koin**: Dependency injection
${if (config.features.contains(Feature.COMPOSE_MULTIPLATFORM)) "- **Compose Multiplatform**: Modern UI framework" else ""}
${if (config.features.contains(Feature.SECURITY_ENCRYPTION)) "- **Android Keystore/iOS Keychain**: Secure storage" else ""}
${if (config.features.contains(Feature.MONITORING_TELEMETRY)) "- **OpenTelemetry**: Monitoring and tracing" else ""}

## Quick Start

### Prerequisites

- JDK 17+
- Android Studio Arctic Fox or later
- Xcode 14+ (for iOS development)
${if (config.targetPlatforms.contains(Platform.DESKTOP)) "- IntelliJ IDEA (for desktop development)" else ""}

### Setup

1. Clone the repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Run the Android app: `./gradlew :androidApp:installDebug`
${if (config.targetPlatforms.contains(Platform.IOS)) "5. Open `iosApp/iosApp.xcodeproj` in Xcode for iOS development" else ""}

### Configuration

Create `local.properties` file in the root directory:

```properties
# API Keys (encrypt in production)
WEATHER_API_KEY=your_openweathermap_api_key

# Optional: Override feature flags
ENABLE_SECURITY_FEATURES=true
ENABLE_MONITORING=true
ENABLE_CRASH_ANALYTICS=true

# Development settings
sdk.dir=/path/to/android/sdk
```

${if (config.features.contains(Feature.MONITORING_TELEMETRY)) """
### Monitoring Setup

${when (config.monitoringProvider) {
    MonitoringProvider.PROMETHEUS_GRAFANA -> """
Start the monitoring stack:
```bash
cd monitoring
docker-compose up -d
```

Access dashboards:
- Grafana: http://localhost:3000 (admin/admin)
- Prometheus: http://localhost:9090"""
    
    MonitoringProvider.DATADOG -> """
Configure DataDog:
1. Set your DataDog API key in `monitoring/datadog.yml`
2. Configure mobile RUM in the app configuration"""
    
    MonitoringProvider.NEW_RELIC -> """
Configure New Relic:
1. Set your license key in `monitoring/newrelic.yml`
2. Configure mobile monitoring in the app"""
    
    MonitoringProvider.CUSTOM -> """
Configure custom monitoring:
1. Update endpoint in `monitoring/config.yml`
2. Set your API key and configure collectors"""
}}""" else ""}

## Development

### Code Style

This project uses Kotlin coding conventions with:
- ktlint for formatting
- detekt for static analysis
- Dokka for documentation generation

Run code quality checks:
```bash
./gradlew ktlintCheck detekt
```

### Testing

Run all tests:
```bash
./gradlew test
```

Run platform-specific tests:
```bash
./gradlew :shared:testDebugUnitTest    # Android
./gradlew :shared:iosTest              # iOS
```

${if (config.features.contains(Feature.SECURITY_ENCRYPTION)) """
### Security

The project includes:
- API key encryption using platform keystores
- Certificate pinning for network security
- ProGuard obfuscation for release builds
- OWASP security scanning in CI/CD

Run security scan:
```bash
./gradlew securityScan
```""" else ""}

## Project Structure

```
${config.projectName}/
├── androidApp/              # Android application
├── shared/                  # Shared KMP module
│   ├── src/commonMain/      # Common code
│   ├── src/androidMain/     # Android-specific code
│   └── src/iosMain/         # iOS-specific code
${if (config.targetPlatforms.contains(Platform.IOS)) "├── iosApp/                  # iOS application" else ""}
${if (config.targetPlatforms.contains(Platform.DESKTOP)) "├── desktopApp/              # Desktop application" else ""}
${if (config.targetPlatforms.contains(Platform.WEB)) "├── webApp/                  # Web application" else ""}
├── docs/                    # Documentation
├── scripts/                 # Build and utility scripts
${if (config.features.contains(Feature.MONITORING_TELEMETRY)) "├── monitoring/              # Monitoring configuration" else ""}
${if (config.features.contains(Feature.SECURITY_ENCRYPTION)) "├── security/                # Security configurations" else ""}
└── .github/workflows/       # CI/CD pipelines
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions:
- Create an issue in the repository
- Contact: ${config.organizationName.lowercase()}@example.com

---

Generated with WeatherKMP 2025 Project Wizard on ${LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)}
        """.trimIndent()
        
        File(docsDir, "README.md").apply {
            parentFile.mkdirs()
            writeText(readme)
        }
        
        // Architecture documentation
        val architecture = """
# Architecture Documentation

## Overview

${config.projectName} follows Clean Architecture principles adapted for Kotlin Multiplatform development.

## Layer Structure

### Domain Layer
- **Entities**: Core business objects
- **Use Cases**: Business logic and rules
- **Repository Interfaces**: Data access abstractions

### Data Layer
- **Repository Implementations**: Data access logic
- **Data Sources**: Remote and local data sources
- **DTOs**: Data transfer objects
- **Mappers**: Entity ↔ DTO conversion

### Presentation Layer
- **ViewModels**: UI state management
- **UI Components**: Platform-specific UI
- **Navigation**: Screen navigation logic

## Dependency Flow

```
Presentation → Domain ← Data
```

## Key Patterns

### Repository Pattern
Centralizes data access logic with clean interfaces.

### Use Case Pattern
Encapsulates business logic in reusable components.

### MVVM Pattern
Separates UI logic from business logic.

${if (config.features.contains(Feature.SECURITY_ENCRYPTION)) """
### Security Architecture

- **Encryption**: Platform-specific keystores
- **Certificate Pinning**: HTTPS security
- **API Key Management**: Secure storage and rotation""" else ""}

${if (config.features.contains(Feature.MONITORING_TELEMETRY)) """
### Monitoring Architecture

- **Telemetry**: OpenTelemetry-inspired system
- **Metrics**: System, application, and business metrics
- **Tracing**: Distributed request tracing
- **Error Tracking**: Comprehensive error reporting""" else ""}

## Platform-Specific Implementations

### Android
- Material Design 3
- Jetpack Compose
- Android Keystore
- WorkManager for background tasks

${if (config.targetPlatforms.contains(Platform.IOS)) """
### iOS
- SwiftUI integration
- iOS Keychain Services
- Background App Refresh
- Push Notifications""" else ""}

${if (config.targetPlatforms.contains(Platform.DESKTOP)) """
### Desktop
- Compose for Desktop
- JVM-based implementation
- File system access
- System tray integration""" else ""}

${if (config.targetPlatforms.contains(Platform.WEB)) """
### Web
- Compose for Web
- Browser APIs
- Progressive Web App features
- WebAssembly optimization""" else ""}
        """.trimIndent()
        
        File(docsDir, "ARCHITECTURE.md").writeText(architecture)
        
        println("✅ Documentation generated")
    }
    
    private fun generateSampleCode(projectDir: File, config: ProjectConfig) {
        println("💻 Generating sample code...")
        
        // Generate sample domain entities
        generateSampleDomainCode(projectDir, config)
        
        // Generate sample data layer
        generateSampleDataCode(projectDir, config)
        
        // Generate sample presentation layer
        generateSamplePresentationCode(projectDir, config)
        
        println("✅ Sample code generated")
    }
    
    private fun generateSampleDomainCode(projectDir: File, config: ProjectConfig) {
        val packagePath = config.packageName.replace('.', '/')
        val domainDir = File(projectDir, "shared/src/commonMain/kotlin/$packagePath/domain")
        
        // Weather entity
        val weatherEntity = """
package ${config.packageName}.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Weather(
    val location: String,
    val temperature: Double,
    val condition: WeatherCondition,
    val humidity: Int,
    val windSpeed: Double,
    val timestamp: Long
)

enum class WeatherCondition {
    SUNNY, CLOUDY, RAINY, SNOWY, STORMY
}
        """.trimIndent()
        
        File(domainDir, "entity/Weather.kt").apply {
            parentFile.mkdirs()
            writeText(weatherEntity)
        }
        
        // Weather repository interface
        val weatherRepository = """
package ${config.packageName}.domain.repository

import ${config.packageName}.domain.entity.Weather
import ${config.packageName}.domain.common.Result
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): Result<Weather>
    suspend fun getWeatherForecast(latitude: Double, longitude: Double, days: Int): Result<List<Weather>>
    fun getWeatherUpdates(): Flow<Weather>
    suspend fun refreshWeatherData(): Result<Unit>
}
        """.trimIndent()
        
        File(domainDir, "repository/WeatherRepository.kt").apply {
            parentFile.mkdirs()
            writeText(weatherRepository)
        }
        
        // Get weather use case
        val getWeatherUseCase = """
package ${config.packageName}.domain.usecase

import ${config.packageName}.domain.entity.Weather
import ${config.packageName}.domain.repository.WeatherRepository
import ${config.packageName}.domain.common.Result

class GetCurrentWeatherUseCase(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): Result<Weather> {
        return weatherRepository.getCurrentWeather(latitude, longitude)
    }
}
        """.trimIndent()
        
        File(domainDir, "usecase/GetCurrentWeatherUseCase.kt").apply {
            parentFile.mkdirs()
            writeText(getWeatherUseCase)
        }
    }
    
    private fun generateSampleDataCode(projectDir: File, config: ProjectConfig) {
        val packagePath = config.packageName.replace('.', '/')
        val dataDir = File(projectDir, "shared/src/commonMain/kotlin/$packagePath/data")
        
        // Weather DTO
        val weatherDto = """
package ${config.packageName}.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponseDto(
    @SerialName("location") val location: LocationDto,
    @SerialName("current") val current: CurrentWeatherDto,
    @SerialName("forecast") val forecast: ForecastDto? = null
)

@Serializable
data class LocationDto(
    @SerialName("name") val name: String,
    @SerialName("region") val region: String,
    @SerialName("country") val country: String,
    @SerialName("lat") val latitude: Double,
    @SerialName("lon") val longitude: Double
)

@Serializable
data class CurrentWeatherDto(
    @SerialName("temp_c") val temperatureCelsius: Double,
    @SerialName("condition") val condition: ConditionDto,
    @SerialName("humidity") val humidity: Int,
    @SerialName("wind_kph") val windSpeedKph: Double,
    @SerialName("last_updated_epoch") val lastUpdatedEpoch: Long
)

@Serializable
data class ConditionDto(
    @SerialName("text") val text: String,
    @SerialName("code") val code: Int
)

@Serializable
data class ForecastDto(
    @SerialName("forecastday") val forecastDays: List<ForecastDayDto>
)

@Serializable
data class ForecastDayDto(
    @SerialName("date") val date: String,
    @SerialName("day") val day: DayDto
)

@Serializable
data class DayDto(
    @SerialName("maxtemp_c") val maxTempCelsius: Double,
    @SerialName("mintemp_c") val minTempCelsius: Double,
    @SerialName("condition") val condition: ConditionDto
)
        """.trimIndent()
        
        File(dataDir, "remote/dto/WeatherDto.kt").apply {
            parentFile.mkdirs()
            writeText(weatherDto)
        }
        
        // Weather API
        val weatherApi = """
package ${config.packageName}.data.remote.api

import ${config.packageName}.data.remote.dto.WeatherResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

interface WeatherApi {
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): WeatherResponseDto
    suspend fun getWeatherForecast(latitude: Double, longitude: Double, days: Int): WeatherResponseDto
}

class WeatherApiImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://api.weatherapi.com/v1",
    private val apiKey: String
) : WeatherApi {
    
    override suspend fun getCurrentWeather(latitude: Double, longitude: Double): WeatherResponseDto {
        return httpClient.get("${'$'}baseUrl/current.json") {
            parameter("key", apiKey)
            parameter("q", "${'$'}latitude,${'$'}longitude")
            parameter("aqi", "no")
        }.body()
    }
    
    override suspend fun getWeatherForecast(latitude: Double, longitude: Double, days: Int): WeatherResponseDto {
        return httpClient.get("${'$'}baseUrl/forecast.json") {
            parameter("key", apiKey)
            parameter("q", "${'$'}latitude,${'$'}longitude")
            parameter("days", days)
            parameter("aqi", "no")
            parameter("alerts", "no")
        }.body()
    }
}
        """.trimIndent()
        
        File(dataDir, "remote/api/WeatherApi.kt").apply {
            parentFile.mkdirs()
            writeText(weatherApi)
        }
        
        // Weather repository implementation
        val weatherRepositoryImpl = """
package ${config.packageName}.data.repository

import ${config.packageName}.domain.entity.Weather
import ${config.packageName}.domain.entity.WeatherCondition
import ${config.packageName}.domain.repository.WeatherRepository
import ${config.packageName}.domain.common.Result
import ${config.packageName}.data.remote.api.WeatherApi
import ${config.packageName}.data.remote.dto.WeatherResponseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepositoryImpl(
    private val weatherApi: WeatherApi
) : WeatherRepository {
    
    override suspend fun getCurrentWeather(latitude: Double, longitude: Double): Result<Weather> {
        return try {
            val response = weatherApi.getCurrentWeather(latitude, longitude)
            val weather = response.toWeatherEntity()
            Result.Success(weather)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun getWeatherForecast(latitude: Double, longitude: Double, days: Int): Result<List<Weather>> {
        return try {
            val response = weatherApi.getWeatherForecast(latitude, longitude, days)
            val weatherList = response.forecast?.forecastDays?.map { day ->
                Weather(
                    location = response.location.name,
                    temperature = day.day.maxTempCelsius,
                    condition = mapCondition(day.day.condition.code),
                    humidity = 0, // Not available in forecast
                    windSpeed = 0.0, // Not available in forecast
                    timestamp = System.currentTimeMillis()
                )
            } ?: emptyList()
            Result.Success(weatherList)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override fun getWeatherUpdates(): Flow<Weather> = flow {
        // Implementation for real-time weather updates
        // This could use WebSockets or periodic polling
    }
    
    override suspend fun refreshWeatherData(): Result<Unit> {
        return try {
            // Implementation for cache refresh
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    private fun WeatherResponseDto.toWeatherEntity(): Weather {
        return Weather(
            location = location.name,
            temperature = current.temperatureCelsius,
            condition = mapCondition(current.condition.code),
            humidity = current.humidity,
            windSpeed = current.windSpeedKph,
            timestamp = current.lastUpdatedEpoch * 1000
        )
    }
    
    private fun mapCondition(code: Int): WeatherCondition {
        return when (code) {
            1000 -> WeatherCondition.SUNNY
            1003, 1006, 1009 -> WeatherCondition.CLOUDY
            in 1063..1201 -> WeatherCondition.RAINY
            in 1210..1282 -> WeatherCondition.SNOWY
            in 1273..1282 -> WeatherCondition.STORMY
            else -> WeatherCondition.CLOUDY
        }
    }
}
        """.trimIndent()
        
        File(dataDir, "repository/WeatherRepositoryImpl.kt").apply {
            parentFile.mkdirs()
            writeText(weatherRepositoryImpl)
        }
    }
    
    private fun generateSamplePresentationCode(projectDir: File, config: ProjectConfig) {
        val packagePath = config.packageName.replace('.', '/')
        val presentationDir = File(projectDir, "shared/src/commonMain/kotlin/$packagePath/presentation")
        
        // Weather view model
        val weatherViewModel = """
package ${config.packageName}.presentation.weather

import ${config.packageName}.domain.entity.Weather
import ${config.packageName}.domain.usecase.GetCurrentWeatherUseCase
import ${config.packageName}.domain.common.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WeatherViewModel(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val coroutineScope: CoroutineScope
) {
    
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()
    
    fun loadWeather(latitude: Double, longitude: Double) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        coroutineScope.launch {
            when (val result = getCurrentWeatherUseCase(latitude, longitude)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        weather = result.data,
                        isLoading = false
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        error = result.exception.message ?: "Unknown error",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class WeatherUiState(
    val weather: Weather? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
        """.trimIndent()
        
        File(presentationDir, "weather/WeatherViewModel.kt").apply {
            parentFile.mkdirs()
            writeText(weatherViewModel)
        }
        
        if (config.features.contains(Feature.COMPOSE_MULTIPLATFORM)) {
            // Weather screen composable
            val weatherScreen = """
package ${config.packageName}.presentation.weather

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ${config.packageName}.domain.entity.Weather
import ${config.packageName}.domain.entity.WeatherCondition

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        // Load weather for default location (e.g., user's current location)
        viewModel.loadWeather(40.7128, -74.0060) // New York coordinates
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = { Text("Weather App") }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
                Text(
                    text = "Loading weather...",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            uiState.error != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error loading weather",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = uiState.error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Button(
                            onClick = { viewModel.clearError() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
            
            uiState.weather != null -> {
                WeatherCard(weather = uiState.weather)
            }
        }
    }
}

@Composable
fun WeatherCard(
    weather: Weather,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = weather.location,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "${'$'}{weather.temperature.toInt()}°C",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = weather.condition.name.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailItem(
                    label = "Humidity",
                    value = "${'$'}{weather.humidity}%"
                )
                
                WeatherDetailItem(
                    label = "Wind",
                    value = "${'$'}{weather.windSpeed} km/h"
                )
            }
        }
    }
}

@Composable
fun WeatherDetailItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
            """.trimIndent()
            
            File(presentationDir, "weather/WeatherScreen.kt").apply {
                parentFile.mkdirs()
                writeText(weatherScreen)
            }
        }
    }
    
    private fun createInitializationSummary(projectDir: File, config: ProjectConfig) {
        println("📋 Creating initialization summary...")
        
        val summary = """
# ${config.projectName} - Initialization Summary

Generated on: ${LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}

## Project Configuration

- **Project Name**: ${config.projectName}
- **Package Name**: ${config.packageName}
- **Organization**: ${config.organizationName}
- **Output Directory**: ${config.outputDirectory}

## Target Platforms

${config.targetPlatforms.joinToString("\n") { "✅ ${it.name.lowercase().replaceFirstChar { it.uppercase() }}" }}

## Enabled Features

${config.features.joinToString("\n") { "✅ ${it.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }}" }}

## Infrastructure

- **Monitoring Provider**: ${config.monitoringProvider.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }}
- **CI/CD Provider**: ${config.cicdProvider.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }}

## Next Steps

1. **Configure API Keys**
   - Add your weather API key to `local.properties`
   - Configure monitoring service credentials

2. **Set Up Development Environment**
   - Install required SDKs and tools
   - Configure IDE settings

3. **Initialize Git Repository**
   ```bash
   cd ${config.projectName}
   git init
   git add .
   git commit -m "Initial project setup with WeatherKMP 2025 standards"
   ```

4. **Run Initial Build**
   ```bash
   ./gradlew build
   ```

5. **Start Development**
   - Review the generated architecture documentation
   - Explore the sample code
   - Begin implementing your specific requirements

## Project Structure

The following structure has been created:

```
${config.projectName}/
├── androidApp/                    # Android application module
├── shared/                        # Shared KMP module
│   ├── src/commonMain/kotlin/     # Common Kotlin code
│   ├── src/androidMain/kotlin/    # Android-specific code
│   └── src/iosMain/kotlin/        # iOS-specific code
${if (config.targetPlatforms.contains(Platform.IOS)) "├── iosApp/                        # iOS application" else ""}
${if (config.targetPlatforms.contains(Platform.DESKTOP)) "├── desktopApp/                    # Desktop application" else ""}
${if (config.targetPlatforms.contains(Platform.WEB)) "├── webApp/                        # Web application" else ""}
├── docs/                          # Project documentation
├── scripts/                       # Build and utility scripts
${if (config.features.contains(Feature.MONITORING_TELEMETRY)) "├── monitoring/                    # Monitoring configuration" else ""}
${if (config.features.contains(Feature.SECURITY_ENCRYPTION)) "├── security/                      # Security configurations" else ""}
├── .github/workflows/             # GitHub Actions CI/CD
├── gradle/                        # Gradle wrapper
└── build-logic/                   # Custom build logic
```

## Key Files Generated

### Configuration
- `build.gradle.kts` - Root build configuration
- `shared/build.gradle.kts` - Shared module configuration
- `androidApp/build.gradle.kts` - Android app configuration
- `gradle.properties` - Project properties and feature flags
- `settings.gradle.kts` - Project structure definition

### Documentation
- `docs/README.md` - Comprehensive project documentation
- `docs/ARCHITECTURE.md` - Architecture and design decisions
- `INITIALIZATION_SUMMARY.md` - This summary document

### Sample Code
- Domain layer entities and use cases
- Data layer repositories and DTOs
- Presentation layer view models and UI components
${if (config.features.contains(Feature.COMPOSE_MULTIPLATFORM)) "- Compose Multiplatform UI screens" else ""}

### Infrastructure
${if (config.features.contains(Feature.SECURITY_ENCRYPTION)) "- Security infrastructure with encryption and certificate pinning" else ""}
${if (config.features.contains(Feature.MONITORING_TELEMETRY)) "- Monitoring and telemetry system" else ""}
- CI/CD pipeline configuration
- Code quality and testing setup

## Support

For questions and support:
- Review the documentation in the `docs/` directory
- Check the GitHub repository for updates
- Contact: ${config.organizationName.lowercase()}@example.com

---

🎉 **Congratulations!** Your WeatherKMP 2025 project has been successfully initialized with modern standards and best practices.

Start building amazing cross-platform weather applications! 🌤️
        """.trimIndent()
        
        File(projectDir, "INITIALIZATION_SUMMARY.md").writeText(summary)
        
        println("✅ Initialization summary created")
    }
}

// Interactive CLI for project configuration
fun main(args: Array<String>) {
    println("🌤️ WeatherKMP 2025 Project Initialization Wizard")
    println("=" * 50)
    
    val config = if (args.isNotEmpty() && args[0] == "--interactive") {
        collectInteractiveConfig()
    } else {
        createDefaultConfig()
    }
    
    val initializer = ProjectInitializer()
    initializer.initializeProject(config)
}

fun collectInteractiveConfig(): ProjectConfig {
    println("\n📝 Let's configure your new WeatherKMP project!\n")
    
    print("Project name: ")
    val projectName = readLine()?.takeIf { it.isNotBlank() } ?: "WeatherKMP"
    
    print("Package name (e.g., com.company.weather): ")
    val packageName = readLine()?.takeIf { it.isNotBlank() } ?: "com.example.weather"
    
    print("Organization name: ")
    val organizationName = readLine()?.takeIf { it.isNotBlank() } ?: "Example Inc"
    
    print("Output directory (default: current directory): ")
    val outputDirectory = readLine()?.takeIf { it.isNotBlank() } ?: "."
    
    println("\n🎯 Select target platforms (comma-separated: android,ios,desktop,web):")
    print("Platforms [android,ios]: ")
    val platformsInput = readLine()?.takeIf { it.isNotBlank() } ?: "android,ios"
    val targetPlatforms = platformsInput.split(",").mapNotNull { platform ->
        when (platform.trim().lowercase()) {
            "android" -> Platform.ANDROID
            "ios" -> Platform.IOS
            "desktop" -> Platform.DESKTOP
            "web" -> Platform.WEB
            else -> null
        }
    }.ifEmpty { listOf(Platform.ANDROID, Platform.IOS) }
    
    println("\n✨ Select features (comma-separated):")
    println("Available: security_encryption, monitoring_telemetry, crash_analytics, offline_support, push_notifications, biometric_auth, feature_toggles, compose_multiplatform")
    print("Features [security_encryption,monitoring_telemetry,crash_analytics,compose_multiplatform]: ")
    val featuresInput = readLine()?.takeIf { it.isNotBlank() } ?: "security_encryption,monitoring_telemetry,crash_analytics,compose_multiplatform"
    val features = featuresInput.split(",").mapNotNull { feature ->
        when (feature.trim().uppercase()) {
            "SECURITY_ENCRYPTION" -> Feature.SECURITY_ENCRYPTION
            "MONITORING_TELEMETRY" -> Feature.MONITORING_TELEMETRY
            "CRASH_ANALYTICS" -> Feature.CRASH_ANALYTICS
            "OFFLINE_SUPPORT" -> Feature.OFFLINE_SUPPORT
            "PUSH_NOTIFICATIONS" -> Feature.PUSH_NOTIFICATIONS
            "BIOMETRIC_AUTH" -> Feature.BIOMETRIC_AUTH
            "FEATURE_TOGGLES" -> Feature.FEATURE_TOGGLES
            "COMPOSE_MULTIPLATFORM" -> Feature.COMPOSE_MULTIPLATFORM
            else -> null
        }
    }.ifEmpty { listOf(Feature.SECURITY_ENCRYPTION, Feature.MONITORING_TELEMETRY, Feature.CRASH_ANALYTICS, Feature.COMPOSE_MULTIPLATFORM) }
    
    println("\n📊 Select monitoring provider:")
    println("1. Prometheus + Grafana (recommended)")
    println("2. DataDog")
    println("3. New Relic")
    println("4. Custom")
    print("Choice [1]: ")
    val monitoringChoice = readLine()?.toIntOrNull() ?: 1
    val monitoringProvider = when (monitoringChoice) {
        2 -> MonitoringProvider.DATADOG
        3 -> MonitoringProvider.NEW_RELIC
        4 -> MonitoringProvider.CUSTOM
        else -> MonitoringProvider.PROMETHEUS_GRAFANA
    }
    
    println("\n🚀 Select CI/CD provider:")
    println("1. GitHub Actions (recommended)")
    println("2. GitLab CI")
    println("3. Azure DevOps")
    println("4. Jenkins")
    print("Choice [1]: ")
    val cicdChoice = readLine()?.toIntOrNull() ?: 1
    val cicdProvider = when (cicdChoice) {
        2 -> CicdProvider.GITLAB_CI
        3 -> CicdProvider.AZURE_DEVOPS
        4 -> CicdProvider.JENKINS
        else -> CicdProvider.GITHUB_ACTIONS
    }
    
    return ProjectConfig(
        projectName = projectName,
        packageName = packageName,
        organizationName = organizationName,
        targetPlatforms = targetPlatforms,
        features = features,
        monitoringProvider = monitoringProvider,
        cicdProvider = cicdProvider,
        outputDirectory = outputDirectory
    )
}

fun createDefaultConfig(): ProjectConfig {
    return ProjectConfig(
        projectName = "WeatherKMP",
        packageName = "com.example.weather",
        organizationName = "Example Inc",
        targetPlatforms = listOf(Platform.ANDROID, Platform.IOS),
        features = listOf(
            Feature.SECURITY_ENCRYPTION,
            Feature.MONITORING_TELEMETRY,
            Feature.CRASH_ANALYTICS,
            Feature.COMPOSE_MULTIPLATFORM,
            Feature.FEATURE_TOGGLES
        ),
        monitoringProvider = MonitoringProvider.PROMETHEUS_GRAFANA,
        cicdProvider = CicdProvider.GITHUB_ACTIONS,
        outputDirectory = "."
    )
}

operator fun String.times(count: Int): String = repeat(count)