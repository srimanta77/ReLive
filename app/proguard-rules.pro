# ReLive ProGuard Rules

# Keep app's main classes
-keep class in.srimantamondal.relive.** { *; }

# Room Database
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# DataStore
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite* {
   <fields>;
}

# Kotlin Coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Compose
-keep class androidx.compose.** { *; }

# Security Crypto
-keep class androidx.security.crypto.** { *; }

# Keep data models
-keep class in.srimantamondal.relive.data.model.** { *; }
-keep class in.srimantamondal.relive.data.usage.** { *; }

# Prevent obfuscation of enums
-keepclassmembers enum * { *; }

# Keep line numbers for crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile