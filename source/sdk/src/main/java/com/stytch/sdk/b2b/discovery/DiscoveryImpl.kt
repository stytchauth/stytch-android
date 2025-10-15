package com.stytch.sdk.b2b.discovery

import com.stytch.sdk.b2b.B2BAuthMethod
import com.stytch.sdk.b2b.DiscoverOrganizationsResponse
import com.stytch.sdk.b2b.IntermediateSessionExchangeResponse
import com.stytch.sdk.b2b.OrganizationCreateResponse
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.StytchDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

internal class DiscoveryImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: B2BSessionStorage,
    private val api: StytchB2BApi.Discovery,
) : Discovery {
    override suspend fun listOrganizations(): DiscoverOrganizationsResponse =
        withContext(dispatchers.io) {
            api.discoverOrganizations(sessionStorage.intermediateSessionToken)
        }

    override fun listOrganizations(callback: (DiscoverOrganizationsResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            callback(listOrganizations())
        }
    }

    override fun listOrganizationsCompletable(): CompletableFuture<DiscoverOrganizationsResponse> =
        externalScope
            .async {
                listOrganizations()
            }.asCompletableFuture()

    override suspend fun exchangeIntermediateSession(
        parameters: Discovery.SessionExchangeParameters,
    ): IntermediateSessionExchangeResponse =
        withContext(dispatchers.io) {
            api
                .exchangeSession(
                    intermediateSessionToken = sessionStorage.intermediateSessionToken,
                    organizationId = parameters.organizationId,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                ).apply {
                    launchSessionUpdater(dispatchers, sessionStorage)
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

    override fun exchangeIntermediateSessionCompletable(
        parameters: Discovery.SessionExchangeParameters,
    ): CompletableFuture<IntermediateSessionExchangeResponse> =
        externalScope
            .async {
                exchangeIntermediateSession(parameters)
            }.asCompletableFuture()

    override suspend fun createOrganization(
        parameters: Discovery.CreateOrganizationParameters,
    ): OrganizationCreateResponse =
        withContext(dispatchers.io) {
            api
                .createOrganization(
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
                ).apply {
                    launchSessionUpdater(dispatchers, sessionStorage)
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

    override fun createOrganizationCompletable(
        parameters: Discovery.CreateOrganizationParameters,
    ): CompletableFuture<OrganizationCreateResponse> =
        externalScope
            .async {
                createOrganization(parameters)
            }.asCompletableFuture()
}
