plugins {
    alias(libs.plugins.androidApplication)
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlinPluginCompose)
}

val publicToken: String = rootProject.ext["STYTCH_PUBLIC_TOKEN"] as String

android {
    namespace = "com.stytch.stytchexampleapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.stytch.stytchexampleapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        manifestPlaceholders["stytchOAuthRedirectScheme"] = ""
        manifestPlaceholders["stytchOAuthRedirectHost"] = ""
        manifestPlaceholders["STYTCH_PUBLIC_TOKEN"] = publicToken

        buildConfigField("String", "STYTCH_PUBLIC_TOKEN", "\"$publicToken\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":source:sdk"))
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(libs.compose.bom)
    implementation(libs.navigation.compose)
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.libphonenumber)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}

kapt {
    correctErrorTypes = true
}
