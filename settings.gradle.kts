pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") } // Dépôt Compose
    }
    // Forcer la version du plugin Safe Args via une stratégie de résolution
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "androidx.navigation.safeargs.kotlin") {
                useVersion("2.9.0")
            }
        }
    }
    plugins {
        id("com.android.application") version "8.1.2"
        id("org.jetbrains.kotlin.android") version "2.0.21"
        // Même si nous utilisons la stratégie, vous pouvez aussi déclarer le plugin ici
        id("androidx.navigation.safeargs.kotlin") version "2.9.0"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
//        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        google()
        mavenCentral()
    }
}

rootProject.name = "Transporteur Sanitaire"
include(":app")