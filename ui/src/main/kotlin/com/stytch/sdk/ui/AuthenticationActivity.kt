package com.stytch.sdk.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.ui.data.AuthenticationState
import com.stytch.sdk.ui.data.StytchUIConfig
import com.stytch.sdk.ui.theme.StytchTheme
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

public class AuthenticationActivity : ComponentActivity() {
    private val viewModel: AuthenticationViewModel by viewModels()
    private lateinit var uiConfig: StytchUIConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiConfig = intent.getParcelableExtra(STYTCH_UI_CONFIG_KEY) ?: error("No UI Configuration Provided")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel
                    .state
                    .filter { it is AuthenticationState.Result }
                    .collect { returnAuthenticationResult((it as AuthenticationState.Result).response) }
            }
        }
        setContent {
            StytchTheme(stytchStyles = uiConfig.styles) {
                StytchAuthenticationApp(
                    productConfig = uiConfig.productConfig,
                    disableWatermark = uiConfig.bootstrapData.disableSDKWatermark,
                )
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(STYTCH_UI_CONFIG_KEY, uiConfig)
        super.onSaveInstanceState(outState)
    }

    private fun returnAuthenticationResult(result: StytchResult<*>) {
        val data = Intent().apply {
            putExtra(STYTCH_RESULT_KEY, result)
        }
        setResult(RESULT_OK, data)
        finish()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
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
