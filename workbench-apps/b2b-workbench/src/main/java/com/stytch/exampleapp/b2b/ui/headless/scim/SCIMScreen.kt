package com.stytch.exampleapp.b2b.ui.headless.scim

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState

@Composable
fun SCIMScreen(reportState: (HeadlessMethodResponseState) -> Unit) {
    val viewModel = SCIMScreenViewModel(reportState)
    SCIMScreenComposable(dispatch = viewModel::handle)
}

@Composable
fun SCIMScreenComposable(dispatch: (SCIMAction) -> Unit) {
    var connectionId by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var identityProvider by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = connectionId,
                onValueChange = { connectionId = it },
                label = { Text("Connection Id") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Display Name") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = identityProvider,
                onValueChange = { identityProvider = it },
                label = { Text("Identity Provider") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(SCIMAction.GetConnection) },
            ) {
                Text("Get Connection")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(SCIMAction.GetConnectionGroups) },
            ) {
                Text("Get Connection Groups")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(SCIMAction.CreateConnection(displayName, identityProvider)) },
            ) {
                Text("Create Connection")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(SCIMAction.UpdateConnection(connectionId, displayName, identityProvider)) },
            ) {
                Text("Update Connection")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(SCIMAction.DeleteConnection(connectionId)) },
            ) {
                Text("Delete Connection By Id")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(SCIMAction.RotateStart(connectionId)) },
            ) {
                Text("Rotate Start")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(SCIMAction.RotateCancel(connectionId)) },
            ) {
                Text("Rotate Cancel")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(SCIMAction.RotateComplete(connectionId)) },
            ) {
                Text("Rotate Complete")
            }
        }
    }
}
