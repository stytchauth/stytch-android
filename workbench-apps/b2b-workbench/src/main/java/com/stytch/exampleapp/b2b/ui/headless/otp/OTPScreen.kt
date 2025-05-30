package com.stytch.exampleapp.b2b.ui.headless.otp

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
fun OTPScreen(reportState: (HeadlessMethodResponseState) -> Unit) {
    val viewModel = OTPScreenViewModel(reportState)
    OTPScreenComposable(dispatch = viewModel::handle)
}

@Composable
fun OTPScreenComposable(dispatch: (OTPAction) -> Unit) {
    var organizationId by remember { mutableStateOf(StytchB2BClient.organization.getSync()?.organizationId ?: "") }
    var memberId by remember { mutableStateOf(StytchB2BClient.member.getSync()?.memberId ?: "") }
    var code by remember { mutableStateOf("") }
    var recipient by remember { mutableStateOf("") }
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
                value = recipient,
                onValueChange = { recipient = it },
                label = { Text("Email Address Or Phone Number") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = code,
                onValueChange = { code = it },
                label = { Text("OTP Code") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(OTPAction.EmailLoginOrCreate(organizationId, recipient)) },
            ) {
                Text("Email Login Or Signup")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(OTPAction.EmailDiscoverySend(recipient)) },
            ) {
                Text("Email Discovery Send")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(OTPAction.EmailAuthenticate(organizationId, recipient, code)) },
            ) {
                Text("Email Authenticate")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(OTPAction.EmailDiscoveryAuthenticate(recipient, code)) },
            ) {
                Text("Email Discovery Authenticate")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(OTPAction.SMSSend(organizationId, memberId, recipient)) },
            ) {
                Text("SMS Send")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(OTPAction.SMSAuthenticate(organizationId, memberId, code)) },
            ) {
                Text("SMS Authenticate")
            }
        }
    }
}
