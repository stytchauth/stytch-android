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
import androidx.compose.ui.unit.dp
import com.stytch.exampleapp.ui.ConsumerWorkbenchAppUIState
import com.stytch.exampleapp.ui.shared.LoggedInView
import com.stytch.sdk.ui.b2c.StytchUI
import com.stytch.sdk.ui.b2c.data.OTPMethods
import com.stytch.sdk.ui.b2c.data.OTPOptions
import com.stytch.sdk.ui.b2c.data.StytchProduct
import com.stytch.sdk.ui.b2c.data.StytchProductConfig

val STYTCH_PRODUCT_CONFIG_EDIT_ME =
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
    )

@Composable
fun UIScreen(state: ConsumerWorkbenchAppUIState) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            style = MaterialTheme.typography.headlineLarge,
            text = "Stytch UI",
        )
        when (state) {
            is ConsumerWorkbenchAppUIState.Loading -> {}
            is ConsumerWorkbenchAppUIState.LoggedOut -> LoggedOutView()
            is ConsumerWorkbenchAppUIState.LoggedIn -> LoggedInView(state)
        }
    }
}

@Composable
fun LoggedOutView() {
    val activity = LocalActivity.current ?: return
    val stytchUi =
        StytchUI
            .Builder()
            .apply {
                activity(activity as ComponentActivity)
                productConfig(STYTCH_PRODUCT_CONFIG_EDIT_ME)
                onAuthenticated { }
            }.build()
    Row(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { stytchUi.authenticate() },
        ) {
            Text("Try Stytch UI")
        }
    }
}
