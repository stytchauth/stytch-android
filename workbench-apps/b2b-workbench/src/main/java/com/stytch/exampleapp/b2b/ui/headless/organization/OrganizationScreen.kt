package com.stytch.exampleapp.b2b.ui.headless.organization

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
fun OrganizationScreen(reportState: (HeadlessMethodResponseState) -> Unit) {
    val viewModel = OrganizationScreenViewModel(reportState)
    OrganizationScreenComposable(dispatch = viewModel::handle)
}

@Composable
fun OrganizationScreenComposable(dispatch: (OrganizationAction) -> Unit) {
    var memberId by remember { mutableStateOf(StytchB2BClient.member.getSync()?.memberId ?: "") }
    var organizationName by remember { mutableStateOf("") }
    var recipient by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = memberId,
                onValueChange = { memberId = it },
                label = { Text("Member Id") },
            )
        }
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = recipient,
                onValueChange = { recipient = it },
                label = { Text("Email Address Or Phone Number") },
            )
        }
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
                onClick = { dispatch(OrganizationAction.DeleteOrganization) },
            ) {
                Text("Delete Organization")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(OrganizationAction.UpdateOrganizationName(organizationName)) },
            ) {
                Text("Update Organization Name")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(OrganizationAction.CreateOrganizationMember(recipient)) },
            ) {
                Text("Create Organization Member by email")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(OrganizationAction.DeleteOrganizationMember(memberId)) },
            ) {
                Text("Delete Organization Member by ID")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(OrganizationAction.ReactivateOrganizationMember(memberId)) },
            ) {
                Text("Reactivate Organization Member by ID")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(OrganizationAction.DeleteMemberMFANumber(memberId)) },
            ) {
                Text("Delete Member MFA Phone Number")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(OrganizationAction.UpdateMemberPhoneNumber(memberId, recipient)) },
            ) {
                Text("Update Member Phone Number")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { dispatch(OrganizationAction.SearchOrganizationMembersByEmail(recipient)) },
            ) {
                Text("Search Organization Members By Email")
            }
        }
    }
}
