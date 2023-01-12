plugins {
    kotlin("android")
    id("com.android.application")
    id("kotlin-kapt")
}

android {
    namespace = "com.begoml.androidconcurrency"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.begoml.uistatedelegate"
        minSdk = 28
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        getByName("debug") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }

    lint.abortOnError = false
}

dependencies {
    implementation(project(":core"))

    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("androidx.fragment:fragment:1.5.5")
    implementation("androidx.fragment:fragment-ktx:1.5.5")

    implementation("androidx.activity:activity-compose:1.6.1")

    implementation(platform("androidx.compose:compose-bom:2022.10.00"))

    implementation("androidx.compose.material:material")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui")

    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.0-alpha03")

    implementation("com.google.dagger:dagger:2.43.2")
    kapt("com.google.dagger:dagger-compiler:2.43.2")

    implementation("androidx.navigation:navigation-compose:2.5.3")
}
