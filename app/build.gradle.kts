// Importations obligatoires en début de fichier
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt") // Ajout du plugin KAPT pour data binding en Kotlin
}

    apply(plugin = "androidx.navigation.safeargs")

android {
    namespace = "com.transporteursanitaire"
    compileSdk = 36

    buildFeatures {
        compose = true
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

    // Configuration Java & Kotlin (JDK 17)
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Dépendances Android de base
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)

    // Dépendances Compose (avec BOM pour centraliser les versions)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Fragment et RecyclerView
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.recyclerview)

    // Room & Lifecycle
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
// Configuration pour le JDK 17 (Test Instrumentés)
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