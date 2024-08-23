package com.stytch.stytchexampleapp.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.stytch.sdk.consumer.network.models.SessionData
import com.stytch.sdk.consumer.network.models.UserData
import com.stytch.stytchexampleapp.AuthenticationState
import com.stytch.stytchexampleapp.Route
import com.stytch.stytchexampleapp.ScreenState

@Composable
internal fun ProfileRoute(
    authenticationState: AuthenticationState,
    navigateTo: (Route) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val (isInitialized, userData, sessionData) = authenticationState
    if (!isInitialized) return
    if (userData == null || sessionData == null) return navigateTo(Route.Login)
    val screenState = viewModel.screenStateFlow.collectAsState()
    ProfileScreen(
        userData = userData,
        sessionData = sessionData,
        logout = viewModel::logout,
        extendSession = viewModel::refreshSession,
        screenState = screenState.value,
    )
}

@Composable
private fun KeyValueRow(
    key: String,
    value: String,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
    ) {
        Text(
            text = key,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Black),
        )
        Text(
            text = value.ifBlank { "Unknown" },
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun ProfileScreen(
    screenState: ScreenState,
    userData: UserData,
    sessionData: SessionData,
    logout: () -> Unit,
    extendSession: () -> Unit,
) {
    Column {
        KeyValueRow(key = "User Id", value = userData.userId)
        KeyValueRow(key = "Phone Number", value = userData.phoneNumbers.first().phoneNumber)
        KeyValueRow(key = "Session Id", value = sessionData.sessionId)
        KeyValueRow(key = "Session Start", value = sessionData.startedAt)
        KeyValueRow(key = "Session Expires", value = sessionData.expiresAt)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (screenState is ScreenState.Loading) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = "Refresh Session",
                    style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.Underline),
                    modifier = Modifier.clickable { extendSession() },
                )
            }
            Text(
                text = "Logout",
                style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.Underline),
                modifier = Modifier.clickable { logout() },
            )
        }
    }
}
