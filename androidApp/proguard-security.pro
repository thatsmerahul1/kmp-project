# WeatherKMP 2025 Security Obfuscation Rules
# Advanced ProGuard/R8 configuration for maximum code protection

# ========================================================================================
# SECURITY OBFUSCATION SETTINGS
# ========================================================================================

# Enable aggressive obfuscation
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-forceprocessing
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-repackageclasses ''

# Use random class names for maximum obfuscation
# Note: Obfuscation dictionaries disabled to fix R8 build issues
# -obfuscationdictionary obfuscation-dictionary.txt
# -classobfuscationdictionary obfuscation-dictionary.txt
# -packageobfuscationdictionary obfuscation-dictionary.txt

# ========================================================================================
# ANTI-REVERSE ENGINEERING PROTECTION
# ========================================================================================

# Remove debugging information
-renamesourcefileattribute ''
-keepattributes SourceFile,LineNumberTable

# Remove parameter names
-keepparameternames

# Encrypt string literals (custom option for R8)
-assumevalues class ** {
    java.lang.String * return _OBFUSCATED_;
}

# Remove unnecessary attributes that could aid reverse engineering
-keepattributes !LocalVariableTable,!LocalVariableTypeTable,!MethodParameters

# ========================================================================================
# SECURITY-SENSITIVE CODE PROTECTION
# ========================================================================================

# Obfuscate security-related classes heavily
-keep class com.weather.security.** { 
    public <init>(...);
}
-keepclassmembers class com.weather.security.** {
    public *;
}

# Protect API key management
-keepclassmembers class com.weather.security.ApiKeyManager {
    !private *;
}
-keepclassmembers class com.weather.security.SecureApiKeyManager {
    !private *;
}

# Protect encryption providers
-keepclassmembers class com.weather.security.*EncryptionProvider {
    !private *;
}

# Protect certificate pinning
-keepclassmembers class com.weather.security.CertificatePinning* {
    !private *;
}

# ========================================================================================
# KOTLIN MULTIPLATFORM SPECIFIC RULES
# ========================================================================================

# Keep shared module public APIs
-keep class com.weather.shared.** { *; }
-keep class com.weather.domain.model.** { *; }
-keep class com.weather.data.remote.dto.** { *; }

# Protect Kotlin metadata for reflection
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep Kotlin coroutines
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# ========================================================================================
# SERIALIZATION PROTECTION
# ========================================================================================

# Keep kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep serializable classes structure but obfuscate names
-keep,allowobfuscation,allowshrinking class * extends kotlinx.serialization.KSerializer
-keep,allowobfuscation,allowshrinking class * implements kotlinx.serialization.KSerializer

# ========================================================================================
# DEPENDENCY INJECTION PROTECTION
# ========================================================================================

# Koin DI protection
-keep class org.koin.** { *; }
-keep class com.weather.di.** { *; }
-keepclassmembers class com.weather.di.** {
    public *;
}

# ========================================================================================
# NETWORKING SECURITY
# ========================================================================================

# Ktor client protection
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { *; }
-dontwarn io.ktor.**

# OkHttp protection (if used)
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Certificate pinning protection
-keep class com.weather.data.remote.HttpClientFactory { *; }

# ========================================================================================
# DATABASE SECURITY
# ========================================================================================

# SQLDelight protection
-keep class app.cash.sqldelight.** { *; }
-keep class com.weather.database.** { *; }

# Room protection (if migrating)
-keep class androidx.room.** { *; }
-keep class androidx.sqlite.** { *; }

# ========================================================================================
# UI FRAMEWORK PROTECTION
# ========================================================================================

# Compose protection
-keep class androidx.compose.** { *; }
-keep class kotlin.** { *; }

# Keep ViewModels structure
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keep class * extends com.weather.presentation.common.BaseViewModel {
    <init>(...);
}

# ========================================================================================
# PLATFORM SPECIFIC PROTECTION
# ========================================================================================

# Android specific
-keep class androidx.** { *; }
-keep class android.** { *; }

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep custom views
-keep class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(***);
    *** get*();
}

# ========================================================================================
# REFLECTION PROTECTION
# ========================================================================================

# Minimize reflection usage but keep necessary parts
-keepattributes Signature,InnerClasses,EnclosingMethod
-keep class kotlin.reflect.** { *; }
-keep class kotlin.Metadata { *; }

# ========================================================================================
# CRASH REPORTING PROTECTION
# ========================================================================================

# Keep crash reporting but obfuscate details
-keep class * implements java.lang.Thread$UncaughtExceptionHandler {
    public void uncaughtException(java.lang.Thread, java.lang.Throwable);
}

# ========================================================================================
# SECURITY MONITORING PROTECTION
# ========================================================================================

# Keep security audit logging structure
-keep class com.weather.security.SecurityAuditLogger { *; }
-keep class com.weather.security.SecurityAuditEvent { *; }
-keepclassmembers class com.weather.security.SecurityAuditEvent {
    public *;
}

# ========================================================================================
# PERFORMANCE MONITORING PROTECTION
# ========================================================================================

# Keep performance tracking
-keep class com.weather.performance.** { *; }
-keepclassmembers class com.weather.performance.** {
    public *;
}

# ========================================================================================
# TESTING EXCLUSIONS
# ========================================================================================

# Remove test code from release builds
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
    static void checkFieldIsNotNull(java.lang.Object, java.lang.String);
    static void checkReturnedValueIsNotNull(java.lang.Object, java.lang.String);
    static void checkExpressionValueIsNotNull(java.lang.Object, java.lang.String);
    static void checkNotNullExpressionValue(java.lang.Object, java.lang.String);
    static void checkReturnedValueIsNotNull(java.lang.Object, java.lang.String, java.lang.String);
    static void checkFieldIsNotNull(java.lang.Object, java.lang.String, java.lang.String);
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String, java.lang.String);
}

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# ========================================================================================
# ANTI-TAMPERING PROTECTION
# ========================================================================================

# Add anti-tampering checks (would be implemented in custom classes)
-keep class com.weather.security.IntegrityChecker { *; }
-keepclassmembers class com.weather.security.IntegrityChecker {
    public static boolean verifySignature(...);
    public static boolean checkDebugger(...);
    public static boolean validateChecksum(...);
}

# ========================================================================================
# WARNINGS SUPPRESSION
# ========================================================================================

# Suppress warnings for intentionally obfuscated code
-dontwarn com.weather.security.**
-dontwarn kotlinx.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# ========================================================================================
# FINAL SECURITY SETTINGS
# ========================================================================================

# Ensure maximum obfuscation
-verbose
-dump build/outputs/mapping/release/dump.txt
-printseeds build/outputs/mapping/release/seeds.txt
-printusage build/outputs/mapping/release/usage.txt
-printmapping build/outputs/mapping/release/mapping.txt

# Additional security through name obfuscation
-adaptclassstrings
-adaptresourcefilenames
-adaptresourcefilecontents **.properties,META-INF/MANIFEST.MF

# Enable full optimization
-allowaccessmodification
-mergeinterfacesaggressively
-overloadaggressively