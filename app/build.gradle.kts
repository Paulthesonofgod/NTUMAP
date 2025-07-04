plugins {
    id("com.android.application") // Fixed: Use plugin id string instead of alias
}

android {
    namespace = "com.example.ntumap"
    compileSdk = 36
    // Updated to latest stable version (Android 14)

    defaultConfig {
        applicationId = "com.example.ntumap"
        minSdk = 24
        targetSdk = 34 // Updated to match compileSdk
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // Recommended for Google Maps
    buildFeatures {
        viewBinding = true
    }
    buildToolsVersion = "36.0.0"
}

dependencies {
    implementation(libs.appcompat.v171)
    implementation(libs.material.v1110)
    implementation(libs.activity.ktx)
    implementation(libs.constraintlayout.v221)

    // Google Maps and Location services
    implementation(libs.play.services.maps.v1820)
    implementation(libs.play.services.location) // Fixed extra parenthesis

    // Added dependencies for Google Maps and ARCore
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.ar:core:1.41.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.v121)
    androidTestImplementation(libs.espresso.core.v361)
}