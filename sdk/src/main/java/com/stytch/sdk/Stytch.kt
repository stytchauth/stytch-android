package com.stytch.sdk

import android.util.Log

public object Stytch {
    internal var isInitialized = false
    internal lateinit var publicToken: String
    internal lateinit var environment: StytchEnvironment

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

internal object StytchLog {
    fun e(message: String) = Log.e("StytchLog", "Stytch error: $message")
    fun w(message: String) = Log.w("StytchLog", "Stytch warning: $message")
    fun i(message: String) = Log.i("StytchLog", message)
    fun d(message: String) = Log.d("StytchLog", message)
    fun v(message: String) = Log.v("StytchLog", message)
}

internal fun stytchError(message: String): Nothing {
    error("Stytch error: $message")
}
