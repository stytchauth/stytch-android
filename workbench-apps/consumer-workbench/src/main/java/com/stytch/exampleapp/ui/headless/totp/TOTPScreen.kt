package com.stytch.exampleapp.ui.headless.totp

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
import androidx.compose.ui.unit.dp
import com.stytch.exampleapp.ui.headless.HeadlessMethodResponseState

@Composable
fun TOTPScreen(reportState: (HeadlessMethodResponseState) -> Unit) {
    val viewModel = TOTPScreenViewModel(reportState)
    TOTPScreenComposable(
        create = viewModel::create,
        authenticate = viewModel::authenticate,
        getRecoveryCodes = viewModel::getRecoveryCodes,
        useRecoveryCode = viewModel::useRecoveryCode,
    )
}

@Composable
fun TOTPScreenComposable(
    create: () -> Unit,
    authenticate: (String) -> Unit,
    getRecoveryCodes: () -> Unit,
    useRecoveryCode: (String) -> Unit,
) {
    var totpCode by remember { mutableStateOf("") }
    var recoveryCode by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            OutlinedButton(
                onClick = create,
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
            ) {
                Text(
                    text = "Create TOTP",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        HorizontalDivider()
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = totpCode,
                onValueChange = { totpCode = it },
                label = { Text("TOTP Code") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            OutlinedButton(
                onClick = { authenticate(totpCode) },
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
            ) {
                Text(
                    text = "Authenticate TOTP",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        HorizontalDivider()
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            OutlinedButton(
                onClick = getRecoveryCodes,
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
            ) {
                Text(
                    text = "Get Recovery Codes",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        HorizontalDivider()
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = recoveryCode,
                onValueChange = { recoveryCode = it },
                label = { Text("Recovery Code") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            OutlinedButton(
                onClick = { useRecoveryCode(recoveryCode) },
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
            ) {
                Text(
                    text = "Use Recovery Code",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
