package com.stytch.sdk.b2b.discovery

import com.stytch.sdk.b2b.DiscoverOrganizationsResponse
import com.stytch.sdk.b2b.IntermediateSessionExchangeResponse
import com.stytch.sdk.b2b.OrganizationCreateResponse
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.StytchDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class DiscoveryImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: B2BSessionStorage,
    private val api: StytchB2BApi.Discovery,
) : Discovery {
    override suspend fun listOrganizations(): DiscoverOrganizationsResponse {
        return withContext(dispatchers.io) {
            api.discoverOrganizations(sessionStorage.intermediateSessionToken)
        }
    }

    override fun listOrganizations(callback: (DiscoverOrganizationsResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            callback(listOrganizations())
        }
    }

    override suspend fun exchangeIntermediateSession(
        parameters: Discovery.SessionExchangeParameters,
    ): IntermediateSessionExchangeResponse {
        return withContext(dispatchers.io) {
            api.exchangeSession(
                intermediateSessionToken = sessionStorage.intermediateSessionToken,
                organizationId = parameters.organizationId,
                sessionDurationMinutes = parameters.sessionDurationMinutes,
            )
        }
    }

    override fun exchangeIntermediateSession(
        parameters: Discovery.SessionExchangeParameters,
        callback: (IntermediateSessionExchangeResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = exchangeIntermediateSession(parameters)
            callback(result)
        }
    }

    override suspend fun createOrganization(
        parameters: Discovery.CreateOrganizationParameters,
    ): OrganizationCreateResponse {
        return withContext(dispatchers.io) {
            api.createOrganization(
                intermediateSessionToken = sessionStorage.intermediateSessionToken,
                organizationName = parameters.organizationName,
                organizationSlug = parameters.organizationSlug,
                organizationLogoUrl = parameters.organizationLogoUrl,
                sessionDurationMinutes = parameters.sessionDurationMinutes,
                ssoJitProvisioning = parameters.ssoJitProvisioning,
                emailAllowedDomains = parameters.emailAllowedDomains,
                emailInvites = parameters.emailInvites,
                emailJitProvisioning = parameters.emailJitProvisioning,
                authMethods = parameters.authMethods,
                allowedAuthMethods = parameters.allowedAuthMethods,
            )
        }
    }

    override fun createOrganization(
        parameters: Discovery.CreateOrganizationParameters,
        callback: (OrganizationCreateResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = createOrganization(parameters)
            callback(result)
        }
    }
}
