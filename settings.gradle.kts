pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("com.gradle.develocity") version ("3.17.5")
}

rootProject.name = "Stytch Android SDK"
include(":source:sdk")
include(":workbench-apps:consumer-workbench")
include(":workbench-apps:b2b-workbench")
include(":workbench-apps:uiworkbench")
include(":example-apps:stytchexampleapp")
include(":example-apps:javademoapp")

develocity {
    buildScan {
        termsOfUseUrl.set("https://gradle.com/help/legal-terms-of-use")
        termsOfUseAgree.set("yes")
    }
}
