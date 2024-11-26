package com.stytch.sdk.ui.b2b

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchUIInvalidConfiguration
import com.stytch.sdk.ui.b2b.data.StytchB2BUIConfig
import com.stytch.sdk.ui.b2b.theme.StytchB2BThemeProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi

internal class B2BAuthenticationActivity : ComponentActivity() {
    private val viewModel: B2BAuthenticationViewModel by viewModels { B2BAuthenticationViewModel.Factory }
    private lateinit var uiConfig: StytchB2BUIConfig

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiConfig = intent.getParcelableExtra(STYTCH_UI_CONFIG_KEY)
            ?: savedInstanceState?.getParcelable(STYTCH_UI_CONFIG_KEY)
            ?: run {
                returnAuthenticationResult(
                    StytchResult.Error(StytchUIInvalidConfiguration("No UI Configuration Provided")),
                )
                return@onCreate
            }
        if (StytchB2BClient.isInitialized.value) {
            StytchB2BClient.events.logEvent(
                eventName = "render_b2b_login_screen",
                details = mapOf("options" to uiConfig.productConfig),
            )
        }
        setContent {
            val state = viewModel.state.collectAsState()
            StytchB2BThemeProvider(config = uiConfig) {
                StytchB2BAuthenticationApp(
                    state = state,
                    dispatch = viewModel::dispatch,
                    savedStateHandle = viewModel.savedStateHandle,
                )
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(STYTCH_UI_CONFIG_KEY, uiConfig)
        super.onSaveInstanceState(outState)
    }

    private fun returnAuthenticationResult(result: StytchResult<*>) {
        if (StytchB2BClient.isInitialized.value) {
            when (result) {
                is StytchResult.Success -> StytchB2BClient.events.logEvent("ui_authentication_success")
                is StytchResult.Error ->
                    StytchB2BClient.events.logEvent(
                        eventName = "ui_authentication_failure",
                        details = null,
                        error = result.exception,
                    )
            }
        }
        val data =
            Intent().apply {
                putExtra(STYTCH_RESULT_KEY, result)
            }
        setResult(RESULT_OK, data)
        finish()
    }

    internal fun exitWithoutAuthenticating() {
        setResult(RESULT_CANCELED)
        finish()
    }

    internal companion object {
        internal const val STYTCH_UI_CONFIG_KEY = "STYTCH_B2B_UI_CONFIG"
        internal const val STYTCH_RESULT_KEY = "STYTCH_RESULT"

        internal fun createIntent(
            context: Context,
            uiConfig: StytchB2BUIConfig,
        ) = Intent(context, B2BAuthenticationActivity::class.java).apply {
            putExtra(STYTCH_UI_CONFIG_KEY, uiConfig)
        }
    }
}
