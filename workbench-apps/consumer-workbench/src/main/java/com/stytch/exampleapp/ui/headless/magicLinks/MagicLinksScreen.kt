package com.stytch.exampleapp.ui.headless.magicLinks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import com.stytch.exampleapp.ui.headless.HeadlessMethodResponseState

@Composable
fun MagicLinksScreen(reportState: (HeadlessMethodResponseState) -> Unit) {
    val viewModel = MagicLinksScreenViewModel(reportState)
    var emailAddress by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = emailAddress,
                onValueChange = { emailAddress = it },
                label = { Text("Email Address") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            OutlinedButton(
                onClick = { viewModel.emlLoginOrCreate(emailAddress) },
                modifier = Modifier.fillMaxWidth(),
                enabled = emailAddress.isNotBlank(),
            ) {
                Text(
                    text = "Send EML loginOrCreate",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
