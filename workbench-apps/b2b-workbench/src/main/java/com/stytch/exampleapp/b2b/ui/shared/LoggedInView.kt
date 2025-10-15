package com.stytch.exampleapp.b2b.ui.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stytch.exampleapp.b2b.ui.B2BWorkbenchAppUIState

@Composable
fun LoggedInView(state: B2BWorkbenchAppUIState.LoggedIn) {
    Column {
        Text(
            style = MaterialTheme.typography.titleLarge,
            text = "You are currently logged in.",
        )
        KeyValueRow(key = "Member Id", value = state.memberData.memberId)
        KeyValueRow(key = "Session Id", value = state.sessionData.memberSessionId)
        KeyValueRow(key = "Session Start", value = state.sessionData.startedAt.toString())
        KeyValueRow(key = "Session Expires", value = state.sessionData.expiresAt.toString())
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
