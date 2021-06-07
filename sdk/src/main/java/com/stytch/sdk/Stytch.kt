package com.stytch.sdk

import android.util.Base64

public object Stytch {
    internal var isInitialized = false
    internal lateinit var authorizationHeader: String
    internal lateinit var environment: StytchEnvironment

    @JvmStatic
    public fun configure(
        projectId: String,
        secret: String,
        environment: StytchEnvironment,
    ) {
        isInitialized = true
        authorizationHeader = generateAuthorizationHeader(projectId = projectId, secret = secret)
        this.environment = environment
    }

    private fun generateAuthorizationHeader(projectId: String, secret: String): String {
        return "Basic " + Base64.encodeToString(
            "$projectId:$secret".toByteArray(),
            Base64.NO_WRAP,
        )
    }

    internal fun assertInitialized() {
        if (!isInitialized) {
            error("Stytch Error: Stytch not initialized. You must call 'Stytch.configure(...)' before using any functionality of the Stytch SDK.")
        }
    }
}

public enum class StytchEnvironment(internal val baseUrl: String) {
    LIVE("https://api.stytch.com/v1/"),
    TEST("https://test.stytch.com/v1/"),
}
