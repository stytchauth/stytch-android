package com.stytch.sdk

public object Stytch {
    internal var isInitialized = false
    internal lateinit var publicToken: String
    internal lateinit var environment: StytchEnvironment

    /**
     * When called configures the Stytch instance with the provided parameters
     * @param publicToken is the public token provided in Stytch dashboard
     * @param environment is one of the possible given environments to do the endpoint calls on
     */
    @JvmStatic
    public fun configure(
        publicToken: String,
        environment: StytchEnvironment,
    ) {
        isInitialized = true
        this.publicToken = publicToken
        this.environment = environment
    }

    internal fun assertInitialized() {
        if (!isInitialized) {
            stytchError("Stytch not initialized. You must call 'Stytch.configure(...)' before using any functionality of the Stytch SDK.")
        }
    }
}

public enum class StytchEnvironment(internal val baseUrl: String) {
    LIVE("https://api.stytch.com/v1/"),
    TEST("https://test.stytch.com/v1/"),
}


