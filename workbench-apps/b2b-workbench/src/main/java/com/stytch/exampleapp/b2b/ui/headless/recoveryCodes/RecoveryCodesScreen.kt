package com.stytch.exampleapp.b2b.ui.headless.recoveryCodes

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
fun RecoveryCodesScreen(reportState: (HeadlessMethodResponseState) -> Unit) {
    val viewModel = RecoveryCodesScreenViewModel(reportState)
    RecoveryCodesScreenComposable(dispatch = viewModel::handle)
}

@Composable
fun RecoveryCodesScreenComposable(dispatch: (RecoveryCodesAction) -> Unit) {
    var organizationId by remember { mutableStateOf(StytchB2BClient.organization.getSync()?.organizationId ?: "") }
    var memberId by remember { mutableStateOf(StytchB2BClient.member.getSync()?.memberId ?: "") }
    var code by remember { mutableStateOf("") }
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
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = memberId,
                onValueChange = { memberId = it },
                label = { Text("Member Id") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = code,
                onValueChange = { code = it },
                label = { Text("Recovery Code") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(RecoveryCodesAction.Get) },
            ) {
                Text("Get Recovery Codes")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(RecoveryCodesAction.Rotate) },
            ) {
                Text("Rotate Recovery Codes")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(RecoveryCodesAction.Recover(organizationId, memberId, code)) },
            ) {
                Text("Use Recovery Code")
            }
        }
    }
}
