package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.discovery.Discovery.CreateOrganizationParameters
import com.stytch.sdk.b2b.network.models.OrganizationCreateResponseData
import com.stytch.sdk.ui.b2b.PerformRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class UseDiscoveryOrganizationCreate(
    private val scope: CoroutineScope,
    private val request: PerformRequest<OrganizationCreateResponseData>,
) {
    operator fun invoke() {
        scope.launch(Dispatchers.IO) {
            request {
                StytchB2BClient.discovery.createOrganization(CreateOrganizationParameters())
            }
        }
    }
}
