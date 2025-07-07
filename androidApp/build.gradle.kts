plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kover)
}

android {
    namespace = "com.weather.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.weather.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false  // Temporarily disabled due to R8 issues
            isShrinkResources = false
            
            // Enhanced R8 optimization for 2025 with security obfuscation
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "proguard-security.pro"  // 2025 Security obfuscation rules
            )
            
            // Additional optimizations
            isDebuggable = false
            isJniDebuggable = false
            isRenderscriptDebuggable = false
            isPseudoLocalesEnabled = false
            
            // Baseline profile optimization
            signingConfig = signingConfigs.getByName("debug") // Use debug signing for now
        }
        
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            
            // Debug optimizations
            isDebuggable = true
            isJniDebuggable = true
            // isTestCoverageEnabled = true // Temporarily disabled for DEX optimization
        }
        
        // Create benchmark build type for performance testing
        create("benchmark") {
            initWith(getByName("release"))
            isMinifyEnabled = false  // Temporarily disabled due to R8 issues
            isShrinkResources = false
            isDebuggable = false
            applicationIdSuffix = ".benchmark"
            versionNameSuffix = "-benchmark"
            
            // Benchmark-specific optimizations
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    
    kotlinOptions {
        jvmTarget = "1.8"
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(projects.shared)
    
    // DateTime
    implementation(libs.kotlinx.datetime)
    
    // Compose BOM
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.foundation)
    
    // Activity Compose
    implementation(libs.androidx.activity.compose)
    
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    
    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    
    // Testing
    testImplementation(libs.kotlin.test)
    androidTestImplementation(platform(libs.compose.bom))
    
    // Debug
    debugImplementation(libs.compose.ui.tooling)
}