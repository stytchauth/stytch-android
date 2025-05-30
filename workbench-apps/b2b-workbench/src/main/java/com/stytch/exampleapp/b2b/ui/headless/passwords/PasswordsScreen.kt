package com.stytch.exampleapp.b2b.ui.headless.passwords

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
import androidx.compose.ui.unit.dp
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState
import com.stytch.sdk.b2b.StytchB2BClient

@Composable
fun PasswordsScreen(reportState: (HeadlessMethodResponseState) -> Unit) {
    val viewModel = PasswordsScreenViewModel(reportState)
    PasswordsScreenComposable(dispatch = viewModel::handle)
}

@Composable
fun PasswordsScreenComposable(dispatch: (PasswordsAction) -> Unit) {
    var organizationId by remember { mutableStateOf(StytchB2BClient.organization.getSync()?.organizationId ?: "") }
    var emailAddress by remember { mutableStateOf("") }
    var existingPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = organizationId,
                onValueChange = { organizationId = it },
                label = { Text("Organization ID") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = emailAddress,
                onValueChange = { emailAddress = it },
                label = { Text("Email Address") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = existingPassword,
                onValueChange = { existingPassword = it },
                label = { Text("Existing Password") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    dispatch(
                        PasswordsAction.Authenticate(
                            organizationId = organizationId,
                            emailAddress = emailAddress,
                            password = existingPassword,
                        ),
                    )
                },
            ) {
                Text("Authenticate")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    dispatch(
                        PasswordsAction.DiscoveryAuthenticate(emailAddress = emailAddress, password = existingPassword),
                    )
                },
            ) {
                Text("Discovery Authenticate")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    dispatch(
                        PasswordsAction.ResetByExistingPassword(
                            organizationId = organizationId,
                            emailAddress = emailAddress,
                            existingPassword = existingPassword,
                            newPassword = newPassword,
                        ),
                    )
                },
            ) {
                Text("Reset By Existing Password")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    dispatch(
                        PasswordsAction.ResetBySession(organizationId = organizationId, newPassword = newPassword),
                    )
                },
            ) {
                Text("Reset By Session")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    dispatch(
                        PasswordsAction.ResetByEmailStart(organizationId = organizationId, emailAddress = emailAddress),
                    )
                },
            ) {
                Text("Reset By Email (start)")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(PasswordsAction.DiscoveryResetByEmailStart(emailAddress = emailAddress)) },
            ) {
                Text("Discovery Reset By Email (start)")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    dispatch(
                        PasswordsAction.StrengthCheck(emailAddress = emailAddress, password = existingPassword),
                    )
                },
            ) {
                Text("Strength Check")
            }
        }
    }
}
