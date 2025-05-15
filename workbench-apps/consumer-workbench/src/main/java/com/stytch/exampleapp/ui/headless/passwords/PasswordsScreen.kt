package com.stytch.exampleapp.ui.headless.passwords

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stytch.exampleapp.ui.headless.HeadlessMethodResponseState
import com.stytch.sdk.common.StytchObjectInfo
import com.stytch.sdk.consumer.StytchClient

@Composable
fun PasswordsScreen(reportState: (HeadlessMethodResponseState) -> Unit) {
    val viewModel = PasswordsScreenViewModel(reportState)
    PasswordsScreenComposable(
        create = viewModel::create,
        authenticate = viewModel::authenticate,
        strengthCheck = viewModel::strengthCheck,
        resetByExisting = viewModel::resetByExisting,
        resetBySession = viewModel::resetBySession,
    )
}

@Composable
fun PasswordsScreenComposable(
    create: (String, String) -> Unit,
    authenticate: (String, String) -> Unit,
    strengthCheck: (String, String) -> Unit,
    resetByExisting: (String, String, String) -> Unit,
    resetBySession: (String) -> Unit,
) {
    var emailAddress by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var isAuthenticated by remember { mutableStateOf(false) }
    val sessionState = StytchClient.sessions.onChange.collectAsState()
    LaunchedEffect(sessionState.value) {
        isAuthenticated = sessionState.value is StytchObjectInfo.Available
    }
    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Current Password") },
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
        HorizontalDivider()
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            OutlinedButton(
                onClick = { create(emailAddress, currentPassword) },
                modifier = Modifier.fillMaxWidth(),
                enabled = emailAddress.isNotEmpty() && currentPassword.isNotEmpty(),
            ) {
                Text(
                    text = "Create User with email/password",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        HorizontalDivider()
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            OutlinedButton(
                onClick = { authenticate(emailAddress, currentPassword) },
                modifier = Modifier.fillMaxWidth(),
                enabled = emailAddress.isNotEmpty() && currentPassword.isNotEmpty(),
            ) {
                Text(
                    text = "Authenticate with email/password",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        HorizontalDivider()
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            OutlinedButton(
                onClick = { strengthCheck(emailAddress, currentPassword) },
                modifier = Modifier.fillMaxWidth(),
                enabled = emailAddress.isNotEmpty() && currentPassword.isNotEmpty(),
            ) {
                Text(
                    text = "Check Password Strength",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        HorizontalDivider()
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            OutlinedButton(
                onClick = { resetByExisting(emailAddress, currentPassword, newPassword) },
                modifier = Modifier.fillMaxWidth(),
                enabled = emailAddress.isNotEmpty() && currentPassword.isNotEmpty() && newPassword.isNotEmpty(),
            ) {
                Text(
                    text = "Reset Password With Existing Password",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        HorizontalDivider()
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            OutlinedButton(
                onClick = { resetBySession(newPassword) },
                modifier = Modifier.fillMaxWidth(),
                enabled = newPassword.isNotEmpty() && isAuthenticated,
            ) {
                Text(
                    text = "Reset Password By Session",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
