package com.stytch.exampleapp.ui.headless.passkeys

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stytch.exampleapp.R
import com.stytch.exampleapp.ui.headless.HeadlessMethodResponseState

@Composable
fun PasskeysScreen(reportState: (HeadlessMethodResponseState) -> Unit) {
    val viewModel = PasskeysScreenViewModel(reportState)
    PasskeysScreenComposable(
        register = viewModel::registerPasskey,
        authenticate = viewModel::authenticatePasskey,
        update = viewModel::updatePasskey,
        clear = viewModel::clearPasskeyRegistrations,
    )
}

@Composable
fun PasskeysScreenComposable(
    register: (FragmentActivity, String) -> Unit,
    authenticate: (FragmentActivity, String) -> Unit,
    update: (String) -> Unit,
    clear: () -> Unit,
) {
    val passkeysDomain = stringResource(R.string.PASSKEYS_DOMAIN)
    var newPasskeyName by remember { mutableStateOf("") }
    val activity = LocalActivity.current as FragmentActivity

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            OutlinedButton(
                onClick = { register(activity, passkeysDomain) },
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
            ) {
                Text(
                    text = "Register Passkey",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            OutlinedButton(
                onClick = { authenticate(activity, passkeysDomain) },
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
            ) {
                Text(
                    text = "Authenticate Passkey",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        HorizontalDivider()
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = newPasskeyName,
                onValueChange = { newPasskeyName = it },
                label = { Text("New Passkey Name") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            OutlinedButton(
                onClick = { update(newPasskeyName) },
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
            ) {
                Text(
                    text = "Update Passkey Name",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        HorizontalDivider()
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            OutlinedButton(
                onClick = clear,
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
            ) {
                Text(
                    text = "Delete all passkey registrations",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
