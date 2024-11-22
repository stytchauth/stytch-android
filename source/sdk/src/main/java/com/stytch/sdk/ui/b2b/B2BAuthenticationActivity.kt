package com.stytch.sdk.ui.b2b

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.SavedStateHandle
import cafe.adriel.voyager.androidx.AndroidScreen
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.common.StytchObjectInfo
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchUIInvalidConfiguration
import com.stytch.sdk.ui.b2b.data.StytchB2BUIConfig
import com.stytch.sdk.ui.b2b.theme.StytchB2BThemeProvider

internal class B2BAuthenticationActivity : ComponentActivity() {
    private val viewModel: B2BAuthenticationViewModel by viewModels { B2BAuthenticationViewModel.Factory }
    private lateinit var uiConfig: StytchB2BUIConfig
    internal lateinit var savedStateHandle: SavedStateHandle

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
        // log render_login_screen
        if (StytchB2BClient.isInitialized.value) {
            StytchB2BClient.events.logEvent(
                eventName = "render_login_screen",
                details = mapOf("options" to uiConfig.productConfig),
            )
            StytchB2BClient.member.onChange {
                if (it is StytchObjectInfo.Available) {
                    returnAuthenticationResult(StytchResult.Success(it.value))
                }
            }
        }
        /* TBD
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel
                    .eventFlow
                    .collect {
                        when (it) {
                            is EventState.Authenticated -> returnAuthenticationResult(it.result)
                            is EventState.Exit -> exitWithoutAuthenticating()
                            is EventState.NavigationRequested -> renderApplicationAtScreen(it.navigationRoute.screen)
                        }
                    }
            }
        }
         */
        savedStateHandle = viewModel.savedStateHandle
        renderApplicationAtScreen()
    }

    private fun renderApplicationAtScreen(screen: AndroidScreen? = null) {
        setContent {
            StytchB2BThemeProvider(config = uiConfig) {
                StytchB2BAuthenticationApp(
                    bootstrapData = uiConfig.bootstrapData,
                    screen = screen,
                    productConfig = uiConfig.productConfig,
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == Intent.ACTION_VIEW) {
            intent.data?.let {
                viewModel.handleDeepLink(it, uiConfig.productConfig.sessionOptions)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(STYTCH_UI_CONFIG_KEY, uiConfig)
        super.onSaveInstanceState(outState)
    }

    internal fun returnAuthenticationResult(result: StytchResult<*>) {
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

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        intent: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (requestCode) {
            STYTCH_THIRD_PARTY_OAUTH_REQUEST_ID ->
                intent?.let {
                    viewModel.authenticateThirdPartyOAuth(resultCode, it, uiConfig.productConfig.sessionOptions)
                }
        }
    }

    internal companion object {
        internal const val STYTCH_UI_CONFIG_KEY = "STYTCH_B2B_UI_CONFIG"
        internal const val STYTCH_RESULT_KEY = "STYTCH_RESULT"
        internal const val STYTCH_THIRD_PARTY_OAUTH_REQUEST_ID = 5552

        internal fun createIntent(
            context: Context,
            uiConfig: StytchB2BUIConfig,
        ) = Intent(context, B2BAuthenticationActivity::class.java).apply {
            putExtra(STYTCH_UI_CONFIG_KEY, uiConfig)
        }
    }
}
