pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        id("com.android.application") version "8.13.1" apply false
        id("org.jetbrains.kotlin.android") version "2.0.0" apply false
        id("org.jetbrains.kotlin.kapt") version "2.0.0" apply false
        // Add the new Compose compiler plugin here as well (version matches Kotlin)
        id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ReLive"
include(":app")
