package com.stytch.sdk.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import cafe.adriel.voyager.androidx.AndroidScreen
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchUIInvalidConfiguration
import com.stytch.sdk.ui.data.EventState
import com.stytch.sdk.ui.data.StytchUIConfig
import com.stytch.sdk.ui.screens.MainScreen
import com.stytch.sdk.ui.theme.StytchTheme
import kotlinx.coroutines.launch

public class AuthenticationActivity : ComponentActivity() {
    private val viewModel: AuthenticationViewModel by viewModels { AuthenticationViewModel.Factory }
    private lateinit var uiConfig: StytchUIConfig
    internal lateinit var savedStateHandle: SavedStateHandle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiConfig = intent.getParcelableExtra(STYTCH_UI_CONFIG_KEY)
            ?: savedInstanceState?.getParcelable(STYTCH_UI_CONFIG_KEY)
            ?: throw StytchUIInvalidConfiguration("No UI Configuration Provided")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel
                    .eventFlow
                    .collect {
                        when (it) {
                            is EventState.Authenticated -> returnAuthenticationResult(it.result)
                            is EventState.Exit -> exitWithoutAuthenticating()
                            is EventState.NavigationRequested -> renderApplication(it.navigationRoute.screen)
                        }
                    }
            }
        }
        savedStateHandle = viewModel.savedStateHandle
        setContent {
            StytchTheme(config = uiConfig) {
                StytchAuthenticationApp(
                    bootstrapData = uiConfig.bootstrapData,
                )
            }
        }
    }

    private fun renderApplication(screen: AndroidScreen) {
        setContent {
            StytchTheme(config = uiConfig) {
                StytchAuthenticationApp(
                    bootstrapData = uiConfig.bootstrapData,
                    screen = screen,
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
        val data = Intent().apply {
            putExtra(STYTCH_RESULT_KEY, result)
        }
        setResult(RESULT_OK, data)
        finish()
    }

    internal fun exitWithoutAuthenticating() {
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (requestCode) {
            STYTCH_GOOGLE_OAUTH_REQUEST_ID -> intent?.let {
                viewModel.authenticateGoogleOneTapLogin(it, uiConfig.productConfig.sessionOptions)
            }
            STYTCH_THIRD_PARTY_OAUTH_REQUEST_ID -> intent?.let {
                viewModel.authenticateThirdPartyOAuth(resultCode, it, uiConfig.productConfig.sessionOptions)
            }
        }
    }

    internal companion object {
        internal const val STYTCH_UI_CONFIG_KEY = "STYTCH_UI_CONFIG"
        internal const val STYTCH_RESULT_KEY = "STYTCH_RESULT"
        internal const val STYTCH_GOOGLE_OAUTH_REQUEST_ID = 5551
        internal const val STYTCH_THIRD_PARTY_OAUTH_REQUEST_ID = 5552
        internal fun createIntent(context: Context, uiConfig: StytchUIConfig) =
            Intent(context, AuthenticationActivity::class.java).apply {
                putExtra(STYTCH_UI_CONFIG_KEY, uiConfig)
            }
    }
}