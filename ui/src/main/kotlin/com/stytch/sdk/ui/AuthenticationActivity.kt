package com.stytch.sdk.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.ui.data.StytchUIConfig
import com.stytch.sdk.ui.theme.LocalStytchTheme
import com.stytch.sdk.ui.theme.StytchAndroidSDKTheme

public class AuthenticationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val (productConfig, styles) = intent.getParcelableExtra<StytchUIConfig>(STYTCH_UI_CONFIG_KEY)
            ?: error("No UI Configuration Provided")
        setContent {
            StytchAndroidSDKTheme(stytchStyles = styles) {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(LocalStytchTheme.current.backgroundColor)) {
                    Text("This is the stytch authentication activity")
                    Button(onClick = {
                        val x = StytchResult.Error(StytchExceptions.Input("This is just a test of passing data"))
                        returnAuthenticationResult(x)
                    }) {
                        Text("Click to return to calling activity")
                    }
                }
            }
        }
    }

    private fun returnAuthenticationResult(result: StytchResult<*>) {
        val data = Intent().apply {
            putExtra(STYTCH_RESULT_KEY, result)
        }
        setResult(RESULT_OK, data)
        finish()
    }

    internal companion object {
        internal const val STYTCH_UI_CONFIG_KEY = "STYTCH_UI_CONFIG"
        internal const val STYTCH_RESULT_KEY = "STYTCH_RESULT"
        internal fun createIntent(context: Context, uiConfig: StytchUIConfig) =
            Intent(context, AuthenticationActivity::class.java).apply {
                putExtra(STYTCH_UI_CONFIG_KEY, uiConfig)
            }
    }
}
