import java.util.Properties

buildscript {
    dependencies {
        classpath(libs.gradle)
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.dokka.gradle.plugin)
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
}

plugins {
    alias(libs.plugins.nexusPublishPlugin) apply (true)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
    alias(libs.plugins.androidLibrary) apply (false)
    alias(libs.plugins.androidApplication) apply (false)
    alias(libs.plugins.kotlinPluginCompose) apply (false)
    alias(libs.plugins.hilt.android) apply (false)
    alias(libs.plugins.ksp) apply (false)
}

apply("$rootDir/scripts/publish-root.gradle")

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

tasks.dokkaHtmlMultiModule.configure {
    outputDirectory.set(file("$projectDir/docs"))
}

// Create variables with empty default values
extra["STYTCH_PUBLIC_TOKEN"] = ""
extra["GOOGLE_OAUTH_CLIENT_ID"] = ""
extra["STYTCH_B2B_PUBLIC_TOKEN"] = ""
extra["STYTCH_B2B_ORG_ID"] = ""
extra["UI_GOOGLE_CLIENT_ID"] = ""
extra["PASSKEYS_DOMAIN"] = ""

val localProperties = project.rootProject.file("local.properties")
if (localProperties.exists()) {
    val p = Properties()
    localProperties.inputStream().use {
        p.load(it)
    }
    p.forEach { name, value ->
        extra[name as String] = value as String
    }
} else {
    // Use envvars
    extra["STYTCH_PUBLIC_TOKEN"] = System.getenv("STYTCH_PUBLIC_TOKEN")
    extra["GOOGLE_OAUTH_CLIENT_ID"] = System.getenv("GOOGLE_OAUTH_CLIENT_ID")
    extra["STYTCH_B2B_PUBLIC_TOKEN"] = System.getenv("STYTCH_B2B_PUBLIC_TOKEN")
    extra["STYTCH_B2B_ORG_ID"] = System.getenv("STYTCH_B2B_ORG_ID")
    extra["UI_GOOGLE_CLIENT_ID"] = System.getenv("UI_GOOGLE_CLIENT_ID")
    extra["PASSKEYS_DOMAIN"] = System.getenv("PASSKEYS_DOMAIN")
}
