package com.stytch.exampleapp.ui.headless.otp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.stytch.exampleapp.ui.headless.HeadlessMethodResponseState

@Composable
fun OTPScreen(reportState: (HeadlessMethodResponseState) -> Unit) {
    val viewModel = OTPScreenViewModel(reportState)
    OTPScreenComposable(
        sendOtp = viewModel::sendOtp,
        authenticateOtp = viewModel::authenticateOtp,
    )
}

@Composable
fun OTPScreenComposable(
    sendOtp: (String, OTPType) -> Unit,
    authenticateOtp: (String) -> Unit,
) {
    var destination by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = destination,
                onValueChange = { destination = it },
                label = { Text("Email Address Or Phone Number") },
            )
        }
        OTPType.entries.map {
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                OutlinedButton(
                    onClick = { sendOtp(destination, it) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = destination.isNotBlank(),
                ) {
                    Text(
                        text = "Send ${it.name} OTP",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = otpCode,
                onValueChange = { otpCode = it },
                label = { Text("OTP Code") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            OutlinedButton(
                onClick = { authenticateOtp(otpCode) },
                modifier = Modifier.fillMaxWidth(),
                enabled = otpCode.isNotBlank(),
            ) {
                Text(
                    text = "Authenticate OTP",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
