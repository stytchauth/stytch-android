plugins {
    alias(libs.plugins.androidApplication)
}
val publicToken = rootProject.ext["STYTCH_PUBLIC_TOKEN"] as String

android {
    namespace = "com.stytch.javademoapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.stytch.javademoapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["stytchOAuthRedirectScheme"] = "stytchjavademoapp"
        manifestPlaceholders["stytchOAuthRedirectHost"] = "oauth"
        manifestPlaceholders["STYTCH_PUBLIC_TOKEN"] = publicToken

        buildConfigField("String", "STYTCH_PUBLIC_TOKEN", "\"$publicToken\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":source:sdk"))
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.viewmodel)
}
