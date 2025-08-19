buildscript {
    dependencies {
        classpath(libs.gradle)
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.versioning.plugin)
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group.startsWith("com.fasterxml.jackson")) {
                useVersion("2.19.1")
            }
            if (requested.group.startsWith("io.netty")) {
                useVersion("4.2.4.Final")
            }
            if (requested.name == "jose4j") {
                useVersion("0.9.6")
            }
            if (requested.name == "jimfs") {
                useVersion("1.3.0")
            }
            if (requested.name == "commons-compress") {
                useVersion("1.27.1")
            }
        }
    }
}

plugins {
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka) apply (false)
    alias(libs.plugins.androidLibrary) apply (false)
    alias(libs.plugins.androidApplication) apply (false)
    alias(libs.plugins.kotlinPluginCompose) apply (false)
    alias(libs.plugins.hilt.android) apply (false)
    alias(libs.plugins.ksp) apply (false)
}

subprojects {
    tasks.withType<Javadoc>().all { enabled = false }
    apply(
        plugin =
            rootProject.libs.plugins.ktlint
                .get()
                .pluginId,
    )
    apply(
        plugin =
            rootProject.libs.plugins.detekt
                .get()
                .pluginId,
    )
}

allprojects {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
    // Optionally configure plugin
    ktlint {
        android = true
        version = "1.2.1"
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

tasks.register("runOnGitHub") {
    dependsOn(
        ":source:sdk:lint",
        ":source:sdk:ktlintCheck",
        ":source:sdk:testDebugUnitTest",
    )
    group = "custom"
    description = "\$ ./gradlew runOnGitHub # runs on GitHub Action"
}
