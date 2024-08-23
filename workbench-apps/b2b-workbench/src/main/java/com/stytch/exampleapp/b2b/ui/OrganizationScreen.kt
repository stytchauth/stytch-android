package com.stytch.exampleapp.b2b.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.stytch.exampleapp.b2b.OrganizationViewModel
import com.stytch.exampleapp.b2b.R

@Composable
fun OrganizationScreen(viewModel: OrganizationViewModel) {
    val responseState = viewModel.currentResponse.collectAsState()
    val loading = viewModel.loadingState.collectAsState()
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).weight(1F, false),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.memberIdState,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.member_id_label))
                },
                onValueChange = {
                    viewModel.memberIdState = it
                },
                shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.memberNameState,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.member_name))
                },
                onValueChange = {
                    viewModel.memberNameState = it
                },
                shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.memberEmailState,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.member_email))
                },
                onValueChange = {
                    viewModel.memberEmailState = it
                },
                shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.memberPhoneState,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.member_phone))
                },
                onValueChange = {
                    viewModel.memberPhoneState = it
                },
                shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.newOrgNameState,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.new_org_name))
                },
                onValueChange = {
                    viewModel.newOrgNameState = it
                },
                shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.delete_organization),
                onClick = viewModel::deleteOrganization,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.update_organization_name),
                onClick = viewModel::updateOrganizationName,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.delete_org_member),
                onClick = viewModel::deleteOrganizationMember,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.reactivate_org_member),
                onClick = viewModel::reactivateOrganizationMember,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.delete_org_member_phone),
                onClick = viewModel::deleteMemberMfaPhoneNumber,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.create_org_member),
                onClick = viewModel::createOrganizationMember,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.update_org_member_phone),
                onClick = viewModel::updateOrganizationMemberPhone,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.search_org_member_by_email),
                onClick = viewModel::searchOrganizationMembersByEmail,
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).weight(1F, false),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (loading.value) {
                CircularProgressIndicator()
            } else {
                Text(text = responseState.value, modifier = Modifier.padding(8.dp))
            }
        }
    }
}
