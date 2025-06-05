pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }  // Ajout du dépôt Compose
    }
    // Ici, vous pouvez déclarer explicitement les plugins et leurs versions si besoin
     plugins {
         id("com.android.application") version "8.10.1"
         id("org.jetbrains.kotlin.android") version "2.0.21"
         id("androidx.navigation.safeargs.kotlin") version "2.9.0"
     }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }  // Ajout du dépôt Compose
    }
}

rootProject.name = "Transporteur Sanitaire"
include(":app")