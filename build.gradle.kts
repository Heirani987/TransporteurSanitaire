import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion

buildscript {
    repositories {
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
    alias(libs.plugins.kotlin.compose) apply false
    id("base") // Plugin pour activer la tâche clean
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