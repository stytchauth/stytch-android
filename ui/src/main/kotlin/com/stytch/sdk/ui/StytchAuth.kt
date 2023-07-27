package com.stytch.sdk.ui

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.stytch.sdk.common.StytchResult

public interface StytchAuth {
    public fun authenticate(options: StytchUIConfig)
}

internal class StytchAuthHandler(
    private val activity: ComponentActivity,
    private val onAuthenticated: (StytchResult<*>) -> Unit,
) : StytchAuth by StytchAuthImpl(
    registry = activity.activityResultRegistry,
    lifecycleOwner = activity,
    onAuthenticated = onAuthenticated,
)

private class StytchAuthImpl(
    private val registry: ActivityResultRegistry,
    lifecycleOwner: LifecycleOwner,
    private val onAuthenticated: (StytchResult<*>) -> Unit,
) : DefaultLifecycleObserver, StytchAuth {
    private lateinit var resultLauncher: ActivityResultLauncher<StytchUIConfig>

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        resultLauncher = registry.register(
            "stytchAuth",
            StytchAuthenticationContract()
        ) { result -> onAuthenticated(result) }
    }

    override fun authenticate(options: StytchUIConfig) {
        resultLauncher.launch(options)
    }
}
