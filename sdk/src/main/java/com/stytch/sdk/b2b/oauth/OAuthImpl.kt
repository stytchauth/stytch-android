package com.stytch.sdk.b2b.oauth

import android.net.Uri
import com.google.gson.JsonParser
import com.stytch.sdk.b2b.OAuthAuthenticateResponse
import com.stytch.sdk.b2b.OAuthDiscoveryAuthenticateResponse
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchMissingPKCEError
import com.stytch.sdk.common.sso.SSOManagerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class OAuthImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: B2BSessionStorage,
    private val storageHelper: StorageHelper,
    private val api: StytchB2BApi.OAuth,
) : OAuth {
    override val google: OAuth.Provider = ProviderImpl("google")
    override val microsoft: OAuth.Provider = ProviderImpl("microsoft")
    override val discovery: OAuth.Discovery = DiscoveryImpl()

    override suspend fun authenticate(parameters: OAuth.AuthenticateParameters): OAuthAuthenticateResponse =
        withContext(dispatchers.io) {
            val pkce =
                storageHelper.retrieveCodeVerifier()
                    ?: run {
                        StytchB2BClient.events.logEvent("b2b_oauth_failure", null, StytchMissingPKCEError(null))
                        return@withContext StytchResult.Error(StytchMissingPKCEError(null))
                    }
            api.authenticate(
                oauthToken = parameters.oauthToken,
                locale = parameters.locale,
                sessionDurationMinutes = parameters.sessionDurationMinutes.toInt(),
                pkceCodeVerifier = pkce,
                intermediateSessionToken = sessionStorage.intermediateSessionToken,
            ).apply {
                storageHelper.clearPKCE()
                when (this) {
                    is StytchResult.Success -> StytchB2BClient.events.logEvent("b2b_oauth_success")
                    is StytchResult.Error -> StytchB2BClient.events.logEvent("b2b_oauth_failure", null, this.exception)
                }
                launchSessionUpdater(dispatchers, sessionStorage)
            }
        }

    override fun authenticate(
        parameters: OAuth.AuthenticateParameters,
        callback: (OAuthAuthenticateResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            callback(authenticate(parameters))
        }
    }

    private inner class ProviderImpl(private val providerName: String) : OAuth.Provider {
        override fun start(parameters: OAuth.Provider.StartParameters) {
            val pkce = storageHelper.generateHashedCodeChallenge().second
            val host =
                StytchB2BClient.bootstrapData.cnameDomain?.let {
                    "https://$it/"
                } ?: if (StytchB2BApi.isTestToken) Constants.TEST_API_URL else Constants.LIVE_API_URL
            val baseUrl = "${host}b2b/public/oauth/$providerName/start"
            val urlParams =
                mapOf(
                    "public_token" to StytchB2BApi.publicToken,
                    "pkce_code_challenge" to pkce,
                    "organization_id" to parameters.organizationId,
                    "slug" to parameters.organizationSlug,
                    "custom_scopes" to parameters.customScopes,
                    "provider_params" to parameters.providerParams,
                    "login_redirect_url" to parameters.loginRedirectUrl,
                    "signup_redirect_url" to parameters.signupRedirectUrl,
                )
            val requestUri = buildUri(baseUrl, urlParams)
            val intent = SSOManagerActivity.createBaseIntent(parameters.context)
            intent.putExtra(SSOManagerActivity.URI_KEY, requestUri.toString())
            parameters.context.startActivityForResult(intent, parameters.oAuthRequestIdentifier)
        }

        override val discovery: OAuth.ProviderDiscovery = ProviderDiscoveryImpl(providerName)
    }

    private inner class ProviderDiscoveryImpl(private val providerName: String) : OAuth.ProviderDiscovery {
        override fun start(parameters: OAuth.ProviderDiscovery.DiscoveryStartParameters) {
            val pkce = storageHelper.generateHashedCodeChallenge().second
            val host = if (StytchB2BApi.isTestToken) Constants.TEST_API_URL else Constants.LIVE_API_URL
            val baseUrl = "${host}b2b/public/oauth/$providerName/discovery/start"
            val urlParams =
                mapOf(
                    "public_token" to StytchB2BApi.publicToken,
                    "pkce_code_challenge" to pkce,
                    "discovery_redirect_url" to parameters.discoveryRedirectUrl,
                    "custom_scopes" to parameters.customScopes,
                    "provider_params" to parameters.providerParams,
                )
            val requestUri = buildUri(baseUrl, urlParams)
            val intent = SSOManagerActivity.createBaseIntent(parameters.context)
            intent.putExtra(SSOManagerActivity.URI_KEY, requestUri.toString())
            parameters.context.startActivityForResult(intent, parameters.oAuthRequestIdentifier)
        }
    }

    private inner class DiscoveryImpl : OAuth.Discovery {
        override suspend fun authenticate(
            parameters: OAuth.Discovery.DiscoveryAuthenticateParameters,
        ): OAuthDiscoveryAuthenticateResponse =
            withContext(dispatchers.io) {
                val pkce =
                    storageHelper.retrieveCodeVerifier()
                        ?: run {
                            StytchB2BClient.events.logEvent(
                                "b2b_discovery_oauth_failure",
                                null,
                                StytchMissingPKCEError(null),
                            )
                            return@withContext StytchResult.Error(StytchMissingPKCEError(null))
                        }
                api.discoveryAuthenticate(
                    pkceCodeVerifier = pkce,
                    discoveryOauthToken = parameters.discoveryOauthToken,
                ).apply {
                    storageHelper.clearPKCE()
                    when (this) {
                        is StytchResult.Success -> StytchB2BClient.events.logEvent("b2b_discovery_oauth_success")
                        is StytchResult.Error ->
                            StytchB2BClient.events.logEvent(
                                "b2b_discovery_oauth_failure",
                                null,
                                this.exception,
                            )
                    }
                }
            }

        override fun authenticate(
            parameters: OAuth.Discovery.DiscoveryAuthenticateParameters,
            callback: (OAuthDiscoveryAuthenticateResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                callback(authenticate(parameters))
            }
        }
    }

    private fun buildUri(
        url: String,
        parameters: Map<String, Any?>,
    ): Uri =
        Uri.parse(url)
            .buildUpon()
            .apply {
                parameters.forEach {
                    if (it.value != null) {
                        when (it.value) {
                            is String -> appendQueryParameter(it.key, it.value.toString())
                            else -> appendQueryParameter(it.key, JsonParser.parseString(it.value.toString()).asString)
                        }
                    }
                }
            }
            .build()
}
