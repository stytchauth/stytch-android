package com.stytch.uiworkbench

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.ui.StytchUI
import com.stytch.sdk.ui.data.EmailMagicLinksOptions
import com.stytch.sdk.ui.data.GoogleOAuthOptions
import com.stytch.sdk.ui.data.StytchProduct
import com.stytch.sdk.ui.data.StytchProductConfig
import com.stytch.uiworkbench.ui.theme.StytchAndroidSDKTheme

class UiWorkbenchActivity : ComponentActivity() {
    private val stytchUi = StytchUI.Builder().apply {
        activity(this@UiWorkbenchActivity)
        isB2B(false)
        productConfig(
            StytchProductConfig(
                products = listOf(StytchProduct.EMAIL_MAGIC_LINKS),
                emailMagicLinksOptions = EmailMagicLinksOptions(
                    loginRedirectURL = "uiworkbench://Authenticate",
                    signupRedirectURL = "uiworkbench://Authenticate",
                ),
                googleOauthOptions = GoogleOAuthOptions(
                    clientId = BuildConfig.GOOGLE_OAUTH_CLIENT_ID
                )
            )
        )
        onAuthenticated {
            when (it) {
                is StytchResult.Success -> println("Authentication Succeeded: ${it.value}")
                is StytchResult.Error -> println("Authentication Failed: ${it.exception}")
            }
        }
    }.build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StytchAndroidSDKTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Button(modifier = Modifier.fillMaxSize(), onClick = stytchUi::authenticate) {
                        Text("Launch Authentication")
                    }
                }
            }
        }
    }
}
