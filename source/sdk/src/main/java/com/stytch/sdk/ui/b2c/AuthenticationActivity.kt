package com.stytch.sdk.ui.b2c

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import cafe.adriel.voyager.androidx.AndroidScreen
import com.stytch.sdk.common.StytchObjectInfo
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchUIInvalidConfiguration
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.biometrics.BiometricAvailability.AvailableNoRegistrations
import com.stytch.sdk.ui.b2c.data.EventState
import com.stytch.sdk.ui.b2c.data.StytchUIConfig
import com.stytch.sdk.ui.b2c.screens.BiometricRegistrationScreen
import com.stytch.sdk.ui.b2c.theme.StytchThemeProvider
import kotlinx.coroutines.launch

internal class AuthenticationActivity : FragmentActivity() {
    private val viewModel: AuthenticationViewModel by viewModels { AuthenticationViewModel.Factory }
    private lateinit var uiConfig: StytchUIConfig
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
        viewModel.enableBiometricRegistrationOnAuthentication(
            uiConfig.productConfig.biometricsOptions.showBiometricRegistrationOnLogin &&
                StytchClient.biometrics.areBiometricsAvailable(this) == AvailableNoRegistrations,
        )
        // log render_login_screen
        if (StytchClient.isInitialized.value) {
            StytchClient.events.logEvent(
                eventName = "render_login_screen",
                details =
                    mapOf(
                        "options" to uiConfig.productConfig,
                        "bootstrap" to uiConfig.bootstrapData,
                    ),
            )
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                StytchClient.user.onChange.collect {
                    if (it is StytchObjectInfo.Available) {
                        returnAuthenticationResult(StytchResult.Success(it.value))
                    }
                }
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
        savedStateHandle = viewModel.savedStateHandle
        renderApplicationAtScreen()
    }

    private fun renderApplicationAtScreen(screen: AndroidScreen? = null) {
        setContent {
            StytchThemeProvider(config = uiConfig) {
                StytchAuthenticationApp(
                    bootstrapData = uiConfig.bootstrapData,
                    screen = screen,
                    productConfig = uiConfig.productConfig,
                    onInvalidConfig = { returnAuthenticationResult(StytchResult.Error(it)) },
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

    internal fun returnAuthenticationResult(
        result: StytchResult<*>,
        enforceBiometricsCheck: Boolean = true,
    ) {
        if (
            enforceBiometricsCheck &&
            viewModel.uiState.value.showBiometricRegistrationOnLogin &&
            StytchClient.biometrics.areBiometricsAvailable(this) == AvailableNoRegistrations &&
            result is StytchResult.Success
        ) {
            renderApplicationAtScreen(BiometricRegistrationScreen(result))
        } else {
            if (StytchClient.isInitialized.value) {
                when (result) {
                    is StytchResult.Success -> StytchClient.events.logEvent("ui_authentication_success")
                    is StytchResult.Error ->
                        StytchClient.events.logEvent(
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
    }

    internal fun exitWithoutAuthenticating() {
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            STYTCH_THIRD_PARTY_OAUTH_REQUEST_ID ->
                data?.let {
                    viewModel.authenticateThirdPartyOAuth(resultCode, it, uiConfig.productConfig.sessionOptions)
                }
        }
    }

    internal companion object {
        internal const val STYTCH_UI_CONFIG_KEY = "STYTCH_UI_CONFIG"
        internal const val STYTCH_RESULT_KEY = "STYTCH_RESULT"
        internal const val STYTCH_THIRD_PARTY_OAUTH_REQUEST_ID = 5552

        internal fun createIntent(
            context: Context,
            uiConfig: StytchUIConfig,
        ) = Intent(context, AuthenticationActivity::class.java).apply {
            putExtra(STYTCH_UI_CONFIG_KEY, uiConfig)
        }
    }
}
