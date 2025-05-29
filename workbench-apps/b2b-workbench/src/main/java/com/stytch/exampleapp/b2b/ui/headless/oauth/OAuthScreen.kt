package com.stytch.exampleapp.b2b.ui.headless.oauth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.stytch.exampleapp.b2b.R
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState
import com.stytch.sdk.b2b.StytchB2BClient

@Composable
fun OAuthScreen(reportState: (HeadlessMethodResponseState) -> Unit) {
    val viewModel = OAuthScreenViewModel(reportState)
    OAuthScreenComposable(dispatch = viewModel::handle)
}

@Composable
fun OAuthScreenComposable(dispatch: (OAuthAction) -> Unit) {
    var publicToken = stringResource(R.string.STYTCH_PUBLIC_TOKEN)
    var organizationId by remember { mutableStateOf(StytchB2BClient.organization.getSync()?.organizationId ?: "") }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = organizationId,
                onValueChange = { organizationId = it },
                label = { Text("Organization Id") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(OAuthAction.GoogleOrganizationFlow(publicToken, organizationId)) },
            ) {
                Text("Launch Google Organization Flow")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(OAuthAction.GoogleDiscoveryFlow(publicToken)) },
            ) {
                Text("Launch Google Discovery Flow")
            }
        }
    }
}
