import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.kotlin.dsl.archives
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    alias(libs.plugins.androidLibrary)
    kotlin("android")
    alias(libs.plugins.dokka)
    id("kotlin-parcelize")
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinPluginCompose)
    alias(libs.plugins.serialization)
    id("jacoco")
    id("maven-publish")
    id("org.jreleaser").version("1.20.0")
}

val publishGroupId = "com.stytch.sdk"
val publishVersion = "0.60.1"
val publishArtifactId = "sdk"

android {
    compileSdk = 35

    defaultConfig {
        minSdk = 23
        consumerProguardFiles("consumer-rules.pro")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("Boolean", "DEBUG_MODE", "false")
            buildConfigField("String", "STYTCH_SDK_VERSION", "\"$publishVersion\"")
        }
        debug {
            isMinifyEnabled = false
            buildConfigField("Boolean", "DEBUG_MODE", "true")
            buildConfigField("String", "STYTCH_SDK_VERSION", "\"$publishVersion\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += "-Xexplicit-api=strict"
    }
    testOptions {
        targetSdk = 35
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
    namespace = "com.stytch.sdk"
    lint {
        targetSdk = 35
        disable.add("OldTargetApi")
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinCompilerExtension.get()
    }
}

tasks.named<DokkaTask>("dokkaJavadoc").configure {
    dependsOn(tasks.named("kspDebugKotlin"))
    dependsOn(tasks.named("kspReleaseKotlin"))
    outputDirectory.set(file("${rootProject.projectDir}/docs"))
    dependencies {
        dokkaPlugin(libs.versioning.plugin)
    }
    moduleName.set("Stytch Android")
    suppressInheritedMembers.set(true)
    dokkaSourceSets {
        configureEach {
            includes.from(
                listOf(
                    "module.md",
                    "src/main/java/com/stytch/sdk/b2b/README.md",
                    "src/main/java/com/stytch/sdk/b2b/discovery/README.md",
                    "src/main/java/com/stytch/sdk/b2b/magicLinks/README.md",
                    "src/main/java/com/stytch/sdk/b2b/member/README.md",
                    "src/main/java/com/stytch/sdk/b2b/oauth/README.md",
                    "src/main/java/com/stytch/sdk/b2b/organization/README.md",
                    "src/main/java/com/stytch/sdk/b2b/otp/README.md",
                    "src/main/java/com/stytch/sdk/b2b/passwords/README.md",
                    "src/main/java/com/stytch/sdk/b2b/recoveryCodes/README.md",
                    "src/main/java/com/stytch/sdk/b2b/sessions/README.md",
                    "src/main/java/com/stytch/sdk/b2b/sso/README.md",
                    "src/main/java/com/stytch/sdk/b2b/totp/README.md",
                    "src/main/java/com/stytch/sdk/common/README.md",
                    "src/main/java/com/stytch/sdk/common/sso/README.md",
                    "src/main/java/com/stytch/sdk/consumer/README.md",
                    "src/main/java/com/stytch/sdk/consumer/biometrics/README.md",
                    "src/main/java/com/stytch/sdk/consumer/crypto/README.md",
                    "src/main/java/com/stytch/sdk/consumer/magicLinks/README.md",
                    "src/main/java/com/stytch/sdk/consumer/oauth/README.md",
                    "src/main/java/com/stytch/sdk/consumer/otp/README.md",
                    "src/main/java/com/stytch/sdk/consumer/passkeys/README.md",
                    "src/main/java/com/stytch/sdk/consumer/passwords/README.md",
                    "src/main/java/com/stytch/sdk/consumer/sessions/README.md",
                    "src/main/java/com/stytch/sdk/consumer/userManagement/README.md",
                ),
            )
            perPackageOption {
                matchingRegex.set(".*\\.network.*")
                suppress.set(true)
            }
            reportUndocumented.set(true)
        }
    }
}

dependencies {
    // Headless SDK dependencies
    implementation(libs.kotlin.stdlib)
    implementation(libs.biometric.ktx)
    implementation(libs.browser)
    implementation(libs.core.ktx)
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.okhttp)
    implementation(libs.moshi)
    implementation(libs.moshi.adapters)
    ksp(libs.moshi.kotlin.codegen)
    runtimeOnly(libs.kotlinx.coroutines.android)
    implementation(libs.tink.android)
    implementation(libs.bcprov.jdk18on)
    implementation(libs.recaptcha)
    implementation(libs.credentials)
    runtimeOnly(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.play.services.auth.api.phone)
    implementation(libs.stytch.dfp)
    implementation(libs.lifecycle.common)
    implementation(libs.lifecycle.process)

    // UI SDK dependencies
    implementation(libs.activity.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.voyager.transitions)
    implementation(libs.voyager.androidx)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.material.icons.extended)
    debugImplementation(libs.ui.tooling)
    implementation(libs.libphonenumber)
    implementation(libs.navigation.compose)
    implementation(libs.serialization)
    implementation(libs.flowRedux.jvm)
    implementation(libs.flowRedux.compose)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)

    // Test depdendencies
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.json)
    androidTestRuntimeOnly(libs.runner)
    // androidTestImplementation(libs.rules)
    androidTestImplementation(libs.core.testing)
    // Test rules and transitive dependencies:
    androidTestImplementation(libs.ui.test.junit4)
    // Needed for createAndroidComposeRule, but not createComposeRule:
    debugRuntimeOnly(libs.ui.test.manifest)

    testImplementation(libs.mockwebserver)
}

tasks.withType<Test>().configureEach {
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        showStackTraces = true
    }
}

tasks.register("printVersion") {
    group = "Documentation"
    description = "Prints the version of the SDK. Used for autoreleasing the SDK from GitHub"
    doLast {
        println(publishVersion)
    }
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.register<JacocoReport>("jacocoTestReport")

tasks.register<Jar>("androidSourcesJar") {
    archiveClassifier.set("sources")
    from(
        android.sourceSets
            .get("main")
            .java.srcDirs,
    )
    from(
        android.sourceSets
            .get("main")
            .kotlin.directories,
    )
}

artifacts {
    archives(tasks.getByName("androidSourcesJar"))
}

project.afterEvaluate {
    tasks.named<Test>("testDebugUnitTest").configure {
        finalizedBy(tasks["jacocoTestReport"])
        doLast {
            tasks.named<JacocoReport>("jacocoTestReport") {
                dependsOn(tasks.getByName("testDebugUnitTest"))
                reports {
                    xml.required.set(true)
                }
                executionData(file(layout.buildDirectory.dir("jacoco/testDebugUnitTest.exec")))
                val kotlinTree =
                    fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug")) {
                        exclude(
                            listOf(
                                // ignore internal network request/response models
                                "**/network/models/**",
                                // ignore common ui components
                                "**/ui/**/components/**",
                                "**/ui/**/data/**",
                                "**/ui/**/theme/**",
                                "**/ui/**/utils/**",
                                "**/ui/**/screens/**",
                                "**/ui/**/navigation/**",
                                "**/ui/**/usecases/**",
                                // ignore b2b specific UI components and helpers/utils/impls
                                "**/ui/b2b/B2BAuthenticationActivity*",
                                "**/ui/b2b/B2BAuthenticationViewModel*",
                                "**/ui/b2b/BaseViewModel*",
                                "**/ui/b2b/StytchB2BAuth*",
                                "**/ui/b2b/StytchB2BAuthenticationApp*",
                                "**/ui/b2b/StytchB2BAuthenticationContract*",
                                "**/ui/b2b/domain/B2BUIStateMachine*",
                                // ignore b2c specific UI components and helpers/utils/impls
                                "**/ui/b2c/StytchAuthenticationApp*",
                                "**/ui/b2c/AuthenticationActivity*",
                                "**/ui/b2c/AuthenticationViewModel*",
                                "**/ui/b2c/StytchAuth*",
                                "**/ui/b2c/StytchAuthenticationApp*",
                                "**/ui/b2c/StytchAuthenticationContract*",
                            ),
                        )
                    }.files
                classDirectories.from(kotlinTree)
                sourceDirectories.from(files(layout.projectDirectory.dir("src")))
            }
        }
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = publishGroupId
                artifactId = publishArtifactId
                version = publishVersion
                from(components["release"])
                pom {
                    name = publishArtifactId
                    description = "Stytch Android SDK"
                    url = "https://github.com/stytchauth/stytch-android"
                    licenses {
                        license {
                            name = "Stytch License"
                            url = "https://github.com/stytchauth/stytch-android/blob/main/LICENSE"
                        }
                    }
                    developers {
                        developer {
                            id = "Stytch"
                            name = "Stytch Developers"
                            email = "developers@stytch.com"
                        }
                    }
                    scm {
                        connection = "scm:git:github.com/stytchauth/stytch-android.git"
                        developerConnection = "scm:git:ssh://github.com/stytchauth/stytch-android.git"
                        url = "https://github.com/stytchauth/stytch-android/tree/main"
                    }
                }
            }
            repositories {
                maven {
                    url =
                        layout.buildDirectory
                            .dir("staging-deploy")
                            .get()
                            .asFile
                            .toURI()
                }
            }
        }
    }
}

jreleaser {
    gitRootSearch = true
    signing {
        setActive("ALWAYS")
        armored = true
    }
    deploy {
        maven {
            mavenCentral {
                create("sonatype") {
                    setActive("ALWAYS")
                    setStage("FULL")
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository(
                        layout.buildDirectory
                            .dir("staging-deploy")
                            .get()
                            .asFile.absolutePath,
                    )
                    verifyPom = false
                }
            }
        }
    }
    project {
        name = publishArtifactId
        description = "Stytch Android SDK"
        version = publishVersion
        authors.add("Stytch Developers")
        license.set("Stytch License")
    }
    release {
        github {
            enabled.set(false)
        }
    }
}
