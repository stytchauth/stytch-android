package com.stytch.exampleapp.b2b.ui.headless.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.discovery.Discovery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiscoveryScreenViewModel(
    private val reportState: (HeadlessMethodResponseState) -> Unit,
) : ViewModel() {
    fun handle(action: DiscoveryAction) =
        when (action) {
            DiscoveryAction.ListOrganizations -> listOrganizations()
            is DiscoveryAction.CreateOrganization -> createOrganization(action.name)
        }

    private fun listOrganizations() {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            reportState(HeadlessMethodResponseState.Response(StytchB2BClient.discovery.listOrganizations()))
        }
    }

    private fun createOrganization(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.discovery.createOrganization(
                    Discovery.CreateOrganizationParameters(
                        organizationName = name,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }
}

sealed interface DiscoveryAction {
    data object ListOrganizations : DiscoveryAction

    data class CreateOrganization(
        val name: String,
    ) : DiscoveryAction
}
