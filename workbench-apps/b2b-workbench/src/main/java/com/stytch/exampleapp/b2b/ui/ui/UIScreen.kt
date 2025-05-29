package com.stytch.exampleapp.b2b.ui.ui

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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stytch.exampleapp.b2b.ui.B2BWorkbenchAppUIState
import com.stytch.exampleapp.b2b.ui.shared.LoggedInView
import com.stytch.sdk.ui.b2b.StytchB2BUI
import com.stytch.sdk.ui.b2b.data.AuthFlowType
import com.stytch.sdk.ui.b2b.data.B2BOAuthOptions
import com.stytch.sdk.ui.b2b.data.B2BOAuthProviderConfig
import com.stytch.sdk.ui.b2b.data.B2BOAuthProviders
import com.stytch.sdk.ui.b2b.data.StytchB2BProduct
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig

@Composable
fun UIScreen(state: B2BWorkbenchAppUIState) {
    val defaultConfig =
        StytchB2BProductConfig(
            products = StytchB2BProduct.entries,
            oauthOptions =
                B2BOAuthOptions(
                    providers =
                        listOf(
                            B2BOAuthProviderConfig(B2BOAuthProviders.GOOGLE),
                            B2BOAuthProviderConfig(B2BOAuthProviders.GITHUB),
                        ),
                ),
        )
    val activity = LocalActivity.current ?: return
    var orgSlug by remember { mutableStateOf<String>("") }

    fun launchUi() {
        val productConfig =
            defaultConfig.copy(
                organizationSlug = orgSlug,
                authFlowType = if (orgSlug.isNotEmpty()) AuthFlowType.ORGANIZATION else AuthFlowType.DISCOVERY,
            )
        val stytchUi =
            StytchB2BUI
                .Builder()
                .apply {
                    activity(activity as ComponentActivity)
                    productConfig(productConfig)
                    onAuthenticated { }
                }.build()
        stytchUi.authenticate()
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            style = MaterialTheme.typography.headlineLarge,
            text = "Stytch UI",
        )
        if (state is B2BWorkbenchAppUIState.LoggedIn) {
            LoggedInView(state)
        }
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = orgSlug,
                onValueChange = { orgSlug = it },
                label = { Text("Org Slug (Optional)") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = ::launchUi,
            ) {
                Text("Launch Stytch UI")
            }
        }
    }
}
