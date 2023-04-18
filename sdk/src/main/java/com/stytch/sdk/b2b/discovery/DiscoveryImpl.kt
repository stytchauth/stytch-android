package com.stytch.sdk.b2b.discovery

import com.stytch.sdk.b2b.DiscoverOrganizationsResponse
import com.stytch.sdk.b2b.IntermediateSessionExchangeResponse
import com.stytch.sdk.b2b.OrganizationCreateResponse
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.common.StytchDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class DiscoveryImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val api: StytchB2BApi.Discovery,
) : Discovery {
    override suspend fun organizations(
        parameters: Discovery.DiscoverOrganizationsParameters
    ): DiscoverOrganizationsResponse {
        return withContext(dispatchers.io) {
            api.discoverOrganizations(parameters.intermediateSessionToken)
        }
    }

    override suspend fun organizations(
        parameters: Discovery.DiscoverOrganizationsParameters,
        callback: (DiscoverOrganizationsResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = organizations(parameters)
            callback(result)
        }
    }

    override suspend fun exchangeSession(
        parameters: Discovery.SessionExchangeParameters
    ): IntermediateSessionExchangeResponse {
        return withContext(dispatchers.io) {
            api.exchangeSession(
                intermediateSessionToken = parameters.intermediateSessionToken,
                organizationId = parameters.organizationId,
                sessionDurationMinutes = parameters.sessionDurationMinutes
            )
        }
    }

    override suspend fun exchangeSession(
        parameters: Discovery.SessionExchangeParameters,
        callback: (IntermediateSessionExchangeResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = exchangeSession(parameters)
            callback(result)
        }
    }

    override suspend fun create(parameters: Discovery.CreateOrganizationParameters): OrganizationCreateResponse {
        return withContext(dispatchers.io) {
            api.createOrganization(
                intermediateSessionToken = parameters.intermediateSessionToken,
                organizationName = parameters.organizationName,
                organizationSlug = parameters.organizationSlug,
                organizationLogoUrl = parameters.organizationLogoUrl,
                sessionDurationMinutes = parameters.sessionDurationMinutes
            )
        }
    }

    override suspend fun create(
        parameters: Discovery.CreateOrganizationParameters,
        callback: (OrganizationCreateResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = create(parameters)
            callback(result)
        }
    }
}
