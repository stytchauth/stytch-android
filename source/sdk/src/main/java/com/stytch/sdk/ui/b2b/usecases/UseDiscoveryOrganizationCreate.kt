package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.discovery.Discovery.CreateOrganizationParameters
import com.stytch.sdk.b2b.network.models.OrganizationCreateResponseData
import com.stytch.sdk.ui.b2b.PerformRequest

internal class UseDiscoveryOrganizationCreate(
    private val request: PerformRequest<OrganizationCreateResponseData>,
) {
    suspend operator fun invoke(): Result<OrganizationCreateResponseData> =
        request {
            StytchB2BClient.discovery.createOrganization(CreateOrganizationParameters())
        }
}
