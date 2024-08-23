package com.stytch.stytchexampleapp.ui.screens.loading

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stytch.stytchexampleapp.AuthenticationState
import com.stytch.stytchexampleapp.Route
import kotlinx.coroutines.delay

private val SPINNER_SIZE = 100.dp

@Composable
internal fun LoadingScreen(
    authenticationState: AuthenticationState,
    navigateTo: (Route) -> Unit,
) {
    LaunchedEffect(authenticationState.isInitialized) {
        if (authenticationState.isInitialized) {
            delay(500L)
            if (authenticationState.userData == null) {
                navigateTo(Route.Login)
            } else {
                navigateTo(Route.Profile)
            }
        }
    }
    Box(
        modifier = Modifier.width(SPINNER_SIZE).height(SPINNER_SIZE),
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(SPINNER_SIZE),
        )
    }
}
