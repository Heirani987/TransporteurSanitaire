// Importations obligatoires en début de fichier
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt") // Ajout du plugin KAPT pour data binding en Kotlin
//    alias(libs.plugins.androidx.navigation.safeargs)
}

android {
    namespace = "com.transporteursanitaire"
    compileSdk = 36

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    defaultConfig {
        applicationId = "com.transporteursanitaire"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Pour le runtime Android, on reste sur Java 8
        // test avec java 17 - aligner compilateur Java avec KAPT. sinon passer jvmTarget "1.8"
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Kotlin se compile avec jvmTarget 17
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Dépendances Android de base
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)

    // Dépendances Compose en utilisant la BOM pour centraliser les versions
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)              // Compose UI
    implementation(libs.androidx.compose.foundation) // Compose Foundation
    implementation(libs.androidx.compose.material)   // Compose Material
    implementation(libs.androidx.material3)          // Material 3
    implementation(libs.androidx.ui.tooling.preview)   // Tooling Preview
    debugImplementation(libs.androidx.ui.tooling)      // Debug Tooling

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Fragment et RecyclerView (ajout des 2 dépendances supplémentaires)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.recyclerview)

    // Room et Lifecycle (avec KSP pour Room si besoin)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Biometric
    implementation(libs.androidx.biometric)

    // Apache POI pour le parsing des fichiers Excel (*.xlsm)
    implementation(libs.poi)
    implementation(libs.poi.ooxml)

    // API StAX et Aalto XML pour POI
    implementation(libs.stax.api)
    implementation(libs.aalto.xml)

    // SwipeRefreshLayout
    implementation(libs.androidx.swiperefreshlayout)

    // Dépendances pour les tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

// ------------------------------
// Configuration pour forcer l'utilisation du JDK 17 pour la compilation des tests instrumentés.
// Cette configuration se place à la fin du fichier.
tasks.withType<JavaCompile>().matching {
    it.name.startsWith("compileDebugAndroidTest")
}.configureEach {
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(17))
    })
}

tasks.withType<JavaCompile>().matching {
    it.name.startsWith("compileReleaseAndroidTest")
}.configureEach {
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(17))
    })
}