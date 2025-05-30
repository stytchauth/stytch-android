package com.stytch.exampleapp.b2b.ui.headless.sso

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

@Composable
fun SSOScreen(reportState: (HeadlessMethodResponseState) -> Unit) {
    val viewModel = SSOScreenViewModel(reportState)
    SSOScreenComposable(dispatch = viewModel::handle)
}

@Composable
fun SSOScreenComposable(dispatch: (SSOAction) -> Unit) {
    var publicToken = stringResource(R.string.STYTCH_PUBLIC_TOKEN)
    var connectionId by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var metadataUrl by remember { mutableStateOf("") }
    var certificateId by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = connectionId,
                onValueChange = { connectionId = it },
                label = { Text("Connection ID") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Display Name") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = metadataUrl,
                onValueChange = { metadataUrl = it },
                label = { Text("Metadata URL") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = certificateId,
                onValueChange = { certificateId = it },
                label = { Text("Verification Certificate ID") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(SSOAction.Authenticate(connectionId = connectionId, publicToken = publicToken)) },
            ) {
                Text("Authenticate SSO Connection")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(SSOAction.DeleteConnection(connectionId = connectionId)) },
            ) {
                Text("Delete Connection")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(SSOAction.GetConnections) },
            ) {
                Text("Get Connections")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(SSOAction.CreateOIDCConnection(displayName)) },
            ) {
                Text("Create OIDC with name")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(SSOAction.CreateSAMLConnection(displayName)) },
            ) {
                Text("Create SAML with name")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    dispatch(
                        SSOAction.UpdateOIDCConnection(connectionId = connectionId, displayName = displayName),
                    )
                },
            ) {
                Text("Update OIDC Connection Name")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    dispatch(
                        SSOAction.UpdateSAMLConnection(connectionId = connectionId, displayName = displayName),
                    )
                },
            ) {
                Text("Update SAML Connection Name")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    dispatch(
                        SSOAction.UpdateSAMLConnectionByURL(connectionId = connectionId, metadataUrl = metadataUrl),
                    )
                },
            ) {
                Text("Update SAML Connection by URL")
            }
        }
    }
}
