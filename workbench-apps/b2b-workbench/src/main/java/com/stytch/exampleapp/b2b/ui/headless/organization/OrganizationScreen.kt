package com.stytch.exampleapp.b2b.ui.headless.organization

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState

@Composable
fun OrganizationScreen(reportState: (HeadlessMethodResponseState) -> Unit) {
    val viewModel = OrganizationScreenViewModel(reportState)
    OrganizationScreenComposable()
}

@Composable
fun OrganizationScreenComposable() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("TODO")
    }
}
