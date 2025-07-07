# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Kotlin Multiplatform
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.**

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.weather.**$$serializer { *; }
-keepclassmembers class com.weather.** {
    *** Companion;
}
-keepclasseswithmembers class com.weather.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# SQLDelight
-keep class app.cash.sqldelight.** { *; }
-keep class com.weather.database.** { *; }

# Ktor
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.atomicfu.**
-dontwarn io.netty.**
-dontwarn com.typesafe.**
-dontwarn org.slf4j.**
-dontwarn java.lang.management.**
-dontwarn io.ktor.util.debug.**

# Koin
-keep class org.koin.** { *; }
-keep class com.weather.di.** { *; }

# Weather App Models
-keep class com.weather.domain.model.** { *; }
-keep class com.weather.data.remote.dto.** { *; }
-keep class com.weather.presentation.state.** { *; }

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Datetime
-keep class kotlinx.datetime.** { *; }
-dontwarn kotlinx.datetime.**

# General Android
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Remove logging in release builds - Enhanced for 2025
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
}

# Remove custom logger calls in release
-assumenosideeffects class com.weather.domain.common.AppLogger {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Preserve the special static methods that are required in all enumeration classes.
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 2025 R8 Optimizations
# Aggressive optimization settings
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable
-optimizationpasses 5
-allowaccessmodification
-repackageclasses ''

# Remove unused code more aggressively
-dontwarn javax.annotation.**
-dontwarn org.checkerframework.**
-dontwarn org.codehaus.mojo.animal_sniffer.**

# Optimize for size and performance
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Enhanced R8 full mode optimizations
-allowaccessmodification
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

# Kotlin optimization improvements
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# Enhanced Compose optimizations
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.foundation.** { *; }
-keepclassmembers class androidx.compose.** {
    <init>(...);
}

# Navigation component optimizations
-keep class androidx.navigation.** { *; }
-keepclassmembers class androidx.navigation.** {
    <init>(...);
}

# Performance optimization for reflection
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Memory optimization
-optimizations !code/simplification/cast,!field/*,!class/merging/*

# Network optimization
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Weather app specific optimizations
-keep class com.weather.domain.** { *; }
-keep class com.weather.presentation.ui.** { *; }

# Remove debug-only code
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void checkParameterIsNotNull(...);
    public static void checkNotNullParameter(...);
    public static void checkReturnedValueIsNotNull(...);
}