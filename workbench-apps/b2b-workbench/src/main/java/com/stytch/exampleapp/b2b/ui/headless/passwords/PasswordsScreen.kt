package com.stytch.exampleapp.b2b.ui.headless.passwords

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState

@Composable
fun PasswordsScreen(reportState: (HeadlessMethodResponseState) -> Unit) {
    val viewModel = PasswordsScreenViewModel(reportState)
    PasswordsScreenComposable()
}

@Composable
fun PasswordsScreenComposable() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("TODO")
    }
}
