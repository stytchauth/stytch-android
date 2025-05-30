package com.stytch.exampleapp.b2b.ui.headless.magicLinks

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
import com.stytch.sdk.b2b.StytchB2BClient

@Composable
fun MagicLinksScreen(reportState: (HeadlessMethodResponseState) -> Unit) {
    val viewModel = MagicLinksScreenViewModel(reportState)
    MagicLinksScreenComposable(dispatch = viewModel::handle)
}

@Composable
fun MagicLinksScreenComposable(dispatch: (MagicLinksAction) -> Unit) {
    var organizationId by remember { mutableStateOf(StytchB2BClient.organization.getSync()?.organizationId ?: "") }
    var emailAddress by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = organizationId,
                onValueChange = { organizationId = it },
                label = { Text("Organization ID") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = emailAddress,
                onValueChange = { emailAddress = it },
                label = { Text("Email Address") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(MagicLinksAction.SendEmailInvite(emailAddress)) },
            ) {
                Text("Send Email Invite")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    dispatch(
                        MagicLinksAction.SendEmailLoginOrSignup(
                            organizationId = organizationId,
                            emailAddress = emailAddress,
                        ),
                    )
                },
            ) {
                Text("Send Login or Signup")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(MagicLinksAction.SendEmailDiscovery(emailAddress = emailAddress)) },
            ) {
                Text("Send Discovery")
            }
        }
    }
}
