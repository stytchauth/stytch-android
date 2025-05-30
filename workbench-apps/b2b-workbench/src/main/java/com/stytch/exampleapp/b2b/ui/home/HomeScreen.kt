package com.stytch.exampleapp.b2b.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.stytch.exampleapp.b2b.ui.B2BWorkbenchAppUIState
import com.stytch.exampleapp.b2b.ui.shared.LoggedInView

@Composable
fun HomeScreen(state: B2BWorkbenchAppUIState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (state) {
            is B2BWorkbenchAppUIState.Loading -> CircularProgressIndicator()
            is B2BWorkbenchAppUIState.LoggedOut -> {
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = "You are currently logged out.",
                )
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = "Try logging in using the Headless or UI methods.",
                )
            }

            is B2BWorkbenchAppUIState.LoggedIn -> LoggedInView(state)
        }
    }
}
