buildscript {
    dependencies {
        classpath(libs.gradle)
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.versioning.plugin)
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files

        constraints {
            // Force upgrades of AGP dependencies
            classpath(libs.commonsCompress)
            classpath(libs.jose4j)
            classpath(libs.netty.handler)
            classpath(libs.netty.handler.proxy)
            classpath(libs.netty.codec.http)
            classpath(libs.netty.codec.http2)
            classpath(libs.bcpkix.jdk18on)
            classpath(libs.jimfs)
            classpath(libs.grpcNetty)
            classpath(libs.guava)
        }
    }
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group.startsWith("com.fasterxml.jackson")) {
                useVersion("2.19.1")
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
    configurations.all {
        resolutionStrategy {
            force(rootProject.libs.woodstox)
            force(rootProject.libs.grpcNetty)
            force(rootProject.libs.guava)
            force(rootProject.libs.netty.codec.http)
        }
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
