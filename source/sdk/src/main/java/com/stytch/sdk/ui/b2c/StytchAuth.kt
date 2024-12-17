package com.stytch.sdk.ui.b2c

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.ui.b2c.data.StytchUIConfig

internal interface StytchAuth {
    fun authenticate(options: StytchUIConfig)
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
) : DefaultLifecycleObserver,
    StytchAuth {
    private lateinit var resultLauncher: ActivityResultLauncher<StytchUIConfig>

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        resultLauncher =
            registry.register(REGISTRY_KEY, StytchAuthenticationContract()) { result ->
                onAuthenticated(result)
            }
    }

    override fun authenticate(options: StytchUIConfig) {
        resultLauncher.launch(options)
    }

    companion object {
        private const val REGISTRY_KEY = "STYTCH_AUTHENTICATION_REGISTRY_KEY"
    }
}
