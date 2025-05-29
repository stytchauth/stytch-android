package com.stytch.exampleapp.b2b.ui.headless.otp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState

@Composable
fun OTPScreen(reportState: (HeadlessMethodResponseState) -> Unit) {
    val viewModel = OTPScreenViewModel(reportState)
    OTPScreenComposable()
}

@Composable
fun OTPScreenComposable() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("TODO")
    }
}
