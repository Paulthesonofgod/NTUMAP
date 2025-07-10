plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.ntumap"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.ntumap"
        minSdk = 24
        targetSdk = 34
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
    // REMOVE kotlinOptions block if present!
}

dependencies {
    implementation("androidx.core:core-ktx:1.16.0")
    implementation(libs.appcompat.v171)
    implementation(libs.material.v180)
    implementation(libs.constraintlayout.v221)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    // Remove or fix this if you don't have a version catalog:
    // implementation(libs.appcompat.v171)
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.ktx)
    androidTestImplementation(libs.espresso.core.v361)
    implementation(libs.activity.v172)
}