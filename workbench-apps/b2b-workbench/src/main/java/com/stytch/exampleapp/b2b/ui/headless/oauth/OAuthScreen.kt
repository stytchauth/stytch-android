package com.stytch.exampleapp.b2b.ui.headless.oauth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState

@Composable
fun OAuthScreen(reportState: (HeadlessMethodResponseState) -> Unit) {
    val viewModel = OAuthScreenViewModel(reportState)
    OAuthScreenComposable()
}

@Composable
fun OAuthScreenComposable() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("TODO")
    }
}
