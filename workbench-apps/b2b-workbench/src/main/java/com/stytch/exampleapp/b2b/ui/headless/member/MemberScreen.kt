package com.stytch.exampleapp.b2b.ui.headless.member

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState

@Composable
fun MemberScreen(reportState: (HeadlessMethodResponseState) -> Unit) {
    val viewModel = MemberScreenViewModel(reportState)
    MemberScreenComposable()
}

@Composable
fun MemberScreenComposable() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("TODO")
    }
}
