plugins {
    alias(libs.plugins.androidApplication)
    kotlin("android")
    alias(libs.plugins.kotlinPluginCompose)
}

android {
    compileSdk = 35

    defaultConfig {
        applicationId = "com.stytch.exampleapp"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Placeholders for OAuth redirect
        manifestPlaceholders["stytchOAuthRedirectScheme"] = "app"
        manifestPlaceholders["stytchOAuthRedirectHost"] = "oauth"
        manifestPlaceholders["STYTCH_PUBLIC_TOKEN"] = rootProject.ext["STYTCH_PUBLIC_TOKEN"] as String
        manifestPlaceholders["STYTCH_B2B_PUBLIC_TOKEN"] = ""
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        all {
            val publicToken = rootProject.ext["STYTCH_PUBLIC_TOKEN"] as String
            buildConfigField("String", "STYTCH_PUBLIC_TOKEN", "\"$publicToken\"")

            val googleOAuthClientId = rootProject.ext["GOOGLE_OAUTH_CLIENT_ID"] as String
            buildConfigField("String", "GOOGLE_OAUTH_CLIENT_ID", "\"$googleOAuthClientId\"")

            val passkeysDomain = rootProject.ext["PASSKEYS_DOMAIN"] as String
            buildConfigField("String", "PASSKEYS_DOMAIN", "\"$passkeysDomain\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinCompilerExtension.get()
    }
    namespace = "com.stytch.exampleapp"
}

dependencies {
    implementation(project(":source:sdk"))

    implementation(libs.kotlin.stdlib)
    implementation(libs.biometric.ktx)
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.compose.material)
    implementation(libs.play.services.auth.api.phone)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    debugImplementation(libs.leakCanary)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.activity.compose)
    implementation(libs.androidx.animation)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.navigation.compose)

    implementation(libs.timber)
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)
}
