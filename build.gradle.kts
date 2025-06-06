import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion

buildscript {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public") } // Miroir Aliyun ajouté
        google()
        mavenCentral()
    }
    dependencies {
        // Plugin pour Navigation Safe Args
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.9.0")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("base") // Plugin pour activer la tâche clean
}

// Ajoute le miroir à tous les sous-projets
allprojects {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public") } // Miroir Aliyun ajouté
        google()
        mavenCentral()
    }
}

// Configure la toolchain pour tous les sous-projets qui appliquent le plugin Java
subprojects {
    plugins.withType<JavaPlugin> {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(17))
            }
        }
    }
}