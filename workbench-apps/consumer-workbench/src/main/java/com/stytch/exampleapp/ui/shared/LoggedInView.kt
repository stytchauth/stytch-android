package com.stytch.exampleapp.ui.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stytch.exampleapp.ui.ConsumerWorkbenchAppUIState
import com.stytch.sdk.consumer.StytchClient
import kotlinx.coroutines.launch

@Composable
fun LoggedInView(state: ConsumerWorkbenchAppUIState.LoggedIn) {
    val scope = rememberCoroutineScope()

    fun logout() {
        scope.launch {
            StytchClient.sessions.revoke()
        }
    }

    Column {
        KeyValueRow(key = "User Id", value = state.userData.userId)
        KeyValueRow(key = "Session Id", value = state.sessionData.sessionId)
        KeyValueRow(key = "Session Start", value = state.sessionData.startedAt)
        KeyValueRow(key = "Session Expires", value = state.sessionData.expiresAt)
    }
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
