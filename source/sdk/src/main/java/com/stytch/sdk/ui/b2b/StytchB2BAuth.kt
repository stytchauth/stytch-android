package com.stytch.sdk.ui.b2b

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.stytch.sdk.ui.b2b.data.AuthenticationResult
import com.stytch.sdk.ui.b2b.data.StytchB2BUIConfig

internal interface StytchB2BAuth {
    fun authenticate(options: StytchB2BUIConfig)
}

internal class StytchB2BAuthHandler(
    private val activity: ComponentActivity,
    private val onAuthenticated: (AuthenticationResult) -> Unit,
) : StytchB2BAuth by StytchB2BAuthImpl(
        registry = activity.activityResultRegistry,
        lifecycleOwner = activity,
        onAuthenticated = onAuthenticated,
    )

private class StytchB2BAuthImpl(
    private val registry: ActivityResultRegistry,
    lifecycleOwner: LifecycleOwner,
    private val onAuthenticated: (AuthenticationResult) -> Unit,
) : DefaultLifecycleObserver,
    StytchB2BAuth {
    private lateinit var resultLauncher: ActivityResultLauncher<StytchB2BUIConfig>

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        resultLauncher =
            registry.register(REGISTRY_KEY, StytchB2BAuthenticationContract()) { result ->
                onAuthenticated(result)
            }
    }

    override fun authenticate(options: StytchB2BUIConfig) {
        resultLauncher.launch(options)
    }

    companion object {
        private const val REGISTRY_KEY = "STYTCH_AUTHENTICATION_REGISTRY_KEY"
    }
}
