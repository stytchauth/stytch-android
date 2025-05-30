package com.stytch.exampleapp.b2b.ui.headless.discovery

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
fun DiscoveryScreen(reportState: (HeadlessMethodResponseState) -> Unit) {
    val viewModel = DiscoveryScreenViewModel(reportState)
    DiscoveryScreenComposable(dispatch = viewModel::handle)
}

@Composable
fun DiscoveryScreenComposable(dispatch: (DiscoveryAction) -> Unit) {
    var organizationName by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = organizationName,
                onValueChange = { organizationName = it },
                label = { Text("Organization Name") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(DiscoveryAction.ListOrganizations) },
            ) {
                Text("List Discovered Organizations")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(DiscoveryAction.CreateOrganization(organizationName)) },
            ) {
                Text("Create Organization")
            }
        }
    }
}
