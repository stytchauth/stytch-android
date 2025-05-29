package com.stytch.exampleapp.b2b.ui.headless.totp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState

@Composable
fun TOTPScreen(reportState: (HeadlessMethodResponseState) -> Unit) {
    val viewModel = TOTPScreenViewModel(reportState)
    TOTPScreenComposable()
}

@Composable
fun TOTPScreenComposable() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("TODO")
    }
}
