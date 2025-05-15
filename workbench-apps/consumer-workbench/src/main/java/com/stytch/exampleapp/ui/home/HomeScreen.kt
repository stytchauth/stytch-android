package com.stytch.exampleapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.stytch.exampleapp.ui.ConsumerWorkbenchAppUIState
import com.stytch.exampleapp.ui.shared.LoggedInView

@Composable
fun HomeScreen(state: ConsumerWorkbenchAppUIState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (state) {
            is ConsumerWorkbenchAppUIState.Loading -> CircularProgressIndicator()
            is ConsumerWorkbenchAppUIState.LoggedOut -> {
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    text = "You are currently logged out.",
                )
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = "Try logging in using the Headless or UI methods.",
                )
            }

            is ConsumerWorkbenchAppUIState.LoggedIn -> LoggedInView(state)
        }
    }
}
