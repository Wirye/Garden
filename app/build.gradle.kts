import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.example.garden"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.garden"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    buildFeatures {
        compose = true
    }
}

//configurations.all {
//    resolutionStrategy {
//        force("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
//        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")
//        force("org.jetbrains.kotlin:kotlin-reflect:2.0.21")
//    }
//}

dependencies {
    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0") {
            because("Замена kotlin-stdlib 2.3.21 на версию, совместимую с KSP 2.1.0")
        }
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.3") {
            because("Замена serialization-core 1.11.0 на версию под Kotlin 2.1.0")
        }
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3") {
            because("Замена serialization-json 1.11.0 на версию под Kotlin 2.1.0")
        }
    }

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.window)
    implementation(libs.material)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.palette.ktx)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.androidx.constraintlayout.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.json)
    implementation(libs.activity.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.datastore.preferences)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.common)
    implementation(libs.fuzzywuzzy)
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.haze)
    implementation(libs.haze.materials)
    implementation(libs.reorderable)
    implementation(libs.crypto)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.pagging.runtime)
    implementation(libs.androidx.pagging.compose)
    implementation(libs.org.jetbrains.kotlinx.serialization.json)
    implementation(libs.androidx.room.paging)
    implementation(libs.anilibria.kt)
}
