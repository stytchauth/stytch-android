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
import com.stytch.sdk.ui.data.OAuthOptions
import com.stytch.sdk.ui.data.OAuthProvider
import com.stytch.sdk.ui.data.OTPMethods
import com.stytch.sdk.ui.data.OTPOptions
import com.stytch.sdk.ui.data.PasswordOptions
import com.stytch.sdk.ui.data.StytchProduct
import com.stytch.sdk.ui.data.StytchProductConfig
import com.stytch.uiworkbench.ui.theme.StytchAndroidSDKTheme

class UiWorkbenchActivity : ComponentActivity() {
    private val stytchUi = StytchUI.Builder().apply {
        activity(this@UiWorkbenchActivity)
        publicToken(BuildConfig.STYTCH_PUBLIC_TOKEN)
        productConfig(
            StytchProductConfig(
                products = listOf(
                    StytchProduct.OAUTH,
                    StytchProduct.EMAIL_MAGIC_LINKS,
                    StytchProduct.OTP,
                    StytchProduct.PASSWORDS,
                ),
                emailMagicLinksOptions = EmailMagicLinksOptions(
                    loginRedirectURL = "stytchui-${BuildConfig.STYTCH_PUBLIC_TOKEN}://deeplink",
                    signupRedirectURL = "stytchui-${BuildConfig.STYTCH_PUBLIC_TOKEN}://deeplink",
                ),
                passwordOptions = PasswordOptions(
                    loginRedirectURL = "stytchui-${BuildConfig.STYTCH_PUBLIC_TOKEN}://deeplink",
                    resetPasswordRedirectURL = "stytchui-${BuildConfig.STYTCH_PUBLIC_TOKEN}://deeplink",
                ),
                googleOauthOptions = GoogleOAuthOptions(
                    clientId = BuildConfig.GOOGLE_OAUTH_CLIENT_ID
                ),
                oAuthOptions = OAuthOptions(
                    loginRedirectURL = "uiworkbench://oauth",
                    signupRedirectURL = "uiworkbench://oauth",
                    providers = listOf(OAuthProvider.GOOGLE, OAuthProvider.APPLE)
                ),
                otpOptions = OTPOptions(
                    methods = listOf(OTPMethods.SMS, OTPMethods.WHATSAPP),
                )
            )
        )
        onAuthenticated {
            when (it) {
                is StytchResult.Success -> println("Authentication Succeeded: ${it.value}")
                is StytchResult.Error -> println("Authentication Failed: ${it.exception.message}")
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
