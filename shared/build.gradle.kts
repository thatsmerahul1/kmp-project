plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kover)
    alias(libs.plugins.dokka)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            
            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            
            // SQLDelight
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)
            
            // Koin
            implementation(libs.koin.core)
        }
        
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }
        
        // Android-specific test dependencies would go here
        // Note: Android test source sets need to be properly configured for KMP
        
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.koin.android)
            
            // Android Security
            implementation("androidx.security:security-crypto:1.1.0-alpha06")
            implementation("androidx.biometric:biometric:1.1.0")
        }
        
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }
    }
}

android {
    namespace = "com.weather.shared"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

sqldelight {
    databases {
        create("WeatherDatabase") {
            packageName.set("com.weather.database")
        }
    }
}

// Dokka configuration for API documentation
tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    dokkaSourceSets {
        configureEach {
            // Include all source sets
            includes.from("docs/dokka-includes.md")
            
            // External documentation links
            externalDocumentationLink {
                url.set(uri("https://kotlinlang.org/api/kotlinx.coroutines/").toURL())
                packageListUrl.set(uri("https://kotlinlang.org/api/kotlinx.coroutines/package-list").toURL())
            }
            
            externalDocumentationLink {
                url.set(uri("https://kotlinlang.org/api/kotlinx.serialization/").toURL())
                packageListUrl.set(uri("https://kotlinlang.org/api/kotlinx.serialization/package-list").toURL())
            }
            
            externalDocumentationLink {
                url.set(uri("https://kotlinlang.org/api/kotlinx-datetime/").toURL())
                packageListUrl.set(uri("https://kotlinlang.org/api/kotlinx-datetime/package-list").toURL())
            }
            
            // Source linking
            sourceLink {
                localDirectory.set(file("src/commonMain/kotlin"))
                remoteUrl.set(uri("https://github.com/weather-kmp/weatherkmp/tree/main/shared/src/commonMain/kotlin").toURL())
                remoteLineSuffix.set("#L")
            }
        }
    }
}

// Configure specific Dokka output
tasks.register("dokkaHtmlMultiModule", org.jetbrains.dokka.gradle.DokkaMultiModuleTask::class) {
    outputDirectory.set(file("${buildDir}/dokka/htmlMultiModule"))
    moduleName.set("WeatherKMP Shared")
}

// Enhanced Kover configuration for 2025 standards
tasks.register("generateCoverageBadges") {
    group = "verification"
    description = "Generate coverage badges from Kover XML reports"
    dependsOn("koverXmlReport")
    
    doLast {
        exec {
            commandLine("bash", "../scripts/generate-coverage-badges.sh", "--skip-tests")
        }
    }
}

tasks.named("koverXmlReport") {
    doLast {
        println("📊 Coverage report generated")
    }
}