package com.stytch.uiworkbench

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.ui.b2c.StytchUI
import com.stytch.sdk.ui.b2c.data.EmailMagicLinksOptions
import com.stytch.sdk.ui.b2c.data.GoogleOAuthOptions
import com.stytch.sdk.ui.b2c.data.OAuthOptions
import com.stytch.sdk.ui.b2c.data.OAuthProvider
import com.stytch.sdk.ui.b2c.data.OTPMethods
import com.stytch.sdk.ui.b2c.data.OTPOptions
import com.stytch.sdk.ui.b2c.data.PasswordOptions
import com.stytch.sdk.ui.b2c.data.StytchProduct
import com.stytch.sdk.ui.b2c.data.StytchProductConfig
import com.stytch.uiworkbench.ui.theme.StytchAndroidSDKTheme

class UiWorkbenchActivity : ComponentActivity() {
    private val stytchUi =
        StytchUI
            .Builder()
            .apply {
                activity(this@UiWorkbenchActivity)
                productConfig(
                    StytchProductConfig(
                        products =
                            listOf(
                                StytchProduct.OAUTH,
                                StytchProduct.EMAIL_MAGIC_LINKS,
                                StytchProduct.OTP,
                                StytchProduct.PASSWORDS,
                            ),
                        emailMagicLinksOptions = EmailMagicLinksOptions(),
                        passwordOptions = PasswordOptions(),
                        googleOauthOptions =
                            GoogleOAuthOptions(
                                clientId = BuildConfig.GOOGLE_OAUTH_CLIENT_ID,
                            ),
                        oAuthOptions =
                            OAuthOptions(
                                providers = listOf(OAuthProvider.GOOGLE, OAuthProvider.APPLE, OAuthProvider.GITHUB),
                            ),
                        otpOptions =
                            OTPOptions(
                                methods = listOf(OTPMethods.SMS, OTPMethods.WHATSAPP),
                            ),
                    ),
                )
                onAuthenticated {
                    when (it) {
                        is StytchResult.Success -> {
                            Toast
                                .makeText(
                                    this@UiWorkbenchActivity,
                                    "Authentication Succeeded",
                                    Toast.LENGTH_LONG,
                                ).show()
                        }
                        is StytchResult.Error -> {
                            Toast.makeText(this@UiWorkbenchActivity, it.exception.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }.build()

    private val viewModel: UiWorkbenchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val userState = viewModel.userState.collectAsState()
            StytchAndroidSDKTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize().padding(0.dp, 64.dp),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Column {
                        if (userState.value != null) {
                            Button(onClick = viewModel::logout) {
                                Text("Log Out")
                            }
                        } else {
                            Button(onClick = stytchUi::authenticate) {
                                Text("Launch Authentication")
                            }
                        }
                    }
                }
            }
        }
    }
}
