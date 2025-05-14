package com.stytch.exampleapp.ui.ui

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.stytch.exampleapp.R
import com.stytch.exampleapp.ui.ConsumerWorkbenchAppUIState
import com.stytch.exampleapp.ui.shared.LoggedInView
import com.stytch.sdk.ui.b2c.StytchUI
import com.stytch.sdk.ui.b2c.data.GoogleOAuthOptions
import com.stytch.sdk.ui.b2c.data.OAuthOptions
import com.stytch.sdk.ui.b2c.data.OAuthProvider
import com.stytch.sdk.ui.b2c.data.OTPMethods
import com.stytch.sdk.ui.b2c.data.OTPOptions
import com.stytch.sdk.ui.b2c.data.StytchProduct
import com.stytch.sdk.ui.b2c.data.StytchProductConfig

@Composable
fun UIScreen(state: ConsumerWorkbenchAppUIState) {
    val stytchProductConfig =
        StytchProductConfig(
            products =
                listOf(
                    StytchProduct.OAUTH,
                    StytchProduct.EMAIL_MAGIC_LINKS,
                    StytchProduct.OTP,
                    StytchProduct.PASSWORDS,
                ),
            otpOptions =
                OTPOptions(
                    methods = listOf(OTPMethods.SMS, OTPMethods.WHATSAPP),
                ),
            oAuthOptions =
                OAuthOptions(
                    providers = listOf(OAuthProvider.GOOGLE, OAuthProvider.GITHUB),
                ),
            googleOauthOptions =
                GoogleOAuthOptions(
                    clientId = stringResource(R.string.GOOGLE_CLIENT_ID),
                ),
        )
    val activity = LocalActivity.current ?: return
    val stytchUi =
        StytchUI
            .Builder()
            .apply {
                activity(activity as ComponentActivity)
                productConfig(stytchProductConfig)
                onAuthenticated { }
            }.build()
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            style = MaterialTheme.typography.headlineLarge,
            text = "Stytch UI",
        )
        if (state is ConsumerWorkbenchAppUIState.LoggedIn) {
            LoggedInView(state)
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { stytchUi.authenticate() },
            ) {
                Text("Try Stytch UI")
            }
        }
    }
}
