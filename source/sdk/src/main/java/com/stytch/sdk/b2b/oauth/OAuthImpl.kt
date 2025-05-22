package com.stytch.sdk.b2b.oauth

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import com.stytch.sdk.b2b.B2BAuthMethod
import com.stytch.sdk.b2b.OAuthAuthenticateResponse
import com.stytch.sdk.b2b.OAuthDiscoveryAuthenticateResponse
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.NoActivityProvided
import com.stytch.sdk.common.errors.StytchMissingPKCEError
import com.stytch.sdk.common.pkcePairManager.PKCEPairManager
import com.stytch.sdk.common.sso.ProvidedReceiverManager
import com.stytch.sdk.common.sso.SSOManagerActivity
import com.stytch.sdk.common.utils.buildUri
import com.stytch.sdk.common.utils.getApiUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.Continuation

internal class OAuthImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: B2BSessionStorage,
    private val api: StytchB2BApi.OAuth,
    private val pkcePairManager: PKCEPairManager,
    private val providedReceiverManager: ProvidedReceiverManager = ProvidedReceiverManager,
) : OAuth {
    override fun setOAuthReceiverActivity(activity: ComponentActivity?) {
        ProvidedReceiverManager.configureReceiver(activity)
    }

    override val google: OAuth.Provider = ProviderImpl("google", providedReceiverManager::getReceiverConfiguration)
    override val microsoft: OAuth.Provider =
        ProviderImpl("microsoft", providedReceiverManager::getReceiverConfiguration)
    override val hubspot: OAuth.Provider = ProviderImpl("hubspot", providedReceiverManager::getReceiverConfiguration)
    override val slack: OAuth.Provider = ProviderImpl("slack", providedReceiverManager::getReceiverConfiguration)
    override val github: OAuth.Provider = ProviderImpl("github", providedReceiverManager::getReceiverConfiguration)
    override val discovery: OAuth.Discovery = DiscoveryImpl()

    override suspend fun authenticate(parameters: OAuth.AuthenticateParameters): OAuthAuthenticateResponse {
        val pkce =
            pkcePairManager.getPKCECodePair()?.codeVerifier
                ?: run {
                    StytchB2BClient.events.logEvent("b2b_oauth_failure", null, StytchMissingPKCEError(null))
                    return StytchResult.Error(StytchMissingPKCEError(null))
                }
        return withContext(dispatchers.io) {
            api
                .authenticate(
                    oauthToken = parameters.oauthToken,
                    locale = parameters.locale,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                    pkceCodeVerifier = pkce,
                    intermediateSessionToken = sessionStorage.intermediateSessionToken,
                ).apply {
                    pkcePairManager.clearPKCECodePair()
                    when (this) {
                        is StytchResult.Success -> StytchB2BClient.events.logEvent("b2b_oauth_success")
                        is StytchResult.Error ->
                            StytchB2BClient.events.logEvent(
                                "b2b_oauth_failure",
                                null,
                                this.exception,
                            )
                    }
                    sessionStorage.lastAuthMethodUsed = B2BAuthMethod.OAUTH
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
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

    override fun authenticateCompletable(
        parameters: OAuth.AuthenticateParameters,
    ): CompletableFuture<OAuthAuthenticateResponse> =
        externalScope
            .async {
                authenticate(parameters)
            }.asCompletableFuture()

    private inner class ProviderImpl(
        private val providerName: String,
        private val getOAuthReceiver: (
            Continuation<StytchResult<String>>,
        ) -> Pair<ComponentActivity?, ActivityResultLauncher<Intent>?>,
    ) : OAuth.Provider {
        override fun start(parameters: OAuth.Provider.StartParameters) {
            val pkce = pkcePairManager.generateAndReturnPKCECodePair().codeChallenge
            val host =
                getApiUrl(
                    StytchB2BClient.configurationManager.bootstrapData.cnameDomain,
                    StytchB2BClient.configurationManager.options.endpointOptions,
                    StytchB2BApi.isTestToken,
                )
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

        override suspend fun getTokenForProvider(
            parameters: OAuth.Provider.GetTokenForProviderParams,
        ): StytchResult<String> =
            suspendCancellableCoroutine { continuation ->
                getOAuthReceiver(continuation).let { (activity, launcher) ->
                    if (activity == null || launcher == null) {
                        continuation.resume(
                            StytchResult.Error(NoActivityProvided),
                        ) { cause, _, _ -> continuation.cancel(cause) }
                        return@suspendCancellableCoroutine
                    }
                    val pkce = pkcePairManager.generateAndReturnPKCECodePair().codeChallenge
                    val host =
                        getApiUrl(
                            StytchB2BClient.configurationManager.bootstrapData.cnameDomain,
                            StytchB2BClient.configurationManager.options.endpointOptions,
                            StytchB2BApi.isTestToken,
                        )
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
                    val intent = SSOManagerActivity.createBaseIntent(activity)
                    intent.putExtra(SSOManagerActivity.URI_KEY, requestUri.toString())
                    launcher.launch(intent)
                }
            }

        override fun getTokenForProvider(
            parameters: OAuth.Provider.GetTokenForProviderParams,
            callback: (StytchResult<String>) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                callback(getTokenForProvider(parameters))
            }
        }

        override fun getTokenForProviderCompletable(
            parameters: OAuth.Provider.GetTokenForProviderParams,
        ): CompletableFuture<StytchResult<String>> =
            externalScope
                .async {
                    getTokenForProvider(parameters)
                }.asCompletableFuture()

        override val discovery: OAuth.ProviderDiscovery =
            ProviderDiscoveryImpl(providerName, providedReceiverManager::getReceiverConfiguration)
    }

    private inner class ProviderDiscoveryImpl(
        private val providerName: String,
        private val getOAuthReceiver: (
            Continuation<StytchResult<String>>,
        ) -> Pair<ComponentActivity?, ActivityResultLauncher<Intent>?>,
    ) : OAuth.ProviderDiscovery {
        override fun start(parameters: OAuth.ProviderDiscovery.DiscoveryStartParameters) {
            val pkce = pkcePairManager.generateAndReturnPKCECodePair().codeChallenge
            val host =
                getApiUrl(
                    StytchB2BClient.configurationManager.bootstrapData.cnameDomain,
                    StytchB2BClient.configurationManager.options.endpointOptions,
                    StytchB2BApi.isTestToken,
                )
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

        override suspend fun getTokenForProvider(
            parameters: OAuth.ProviderDiscovery.GetTokenForProviderParams,
        ): StytchResult<String> =
            suspendCancellableCoroutine { continuation ->
                getOAuthReceiver(continuation).let { (activity, launcher) ->
                    if (activity == null || launcher == null) {
                        continuation.resume(
                            StytchResult.Error(NoActivityProvided),
                        ) { cause, _, _ -> continuation.cancel(cause) }
                        return@suspendCancellableCoroutine
                    }
                    val pkce = pkcePairManager.generateAndReturnPKCECodePair().codeChallenge
                    val host =
                        getApiUrl(
                            StytchB2BClient.configurationManager.bootstrapData.cnameDomain,
                            StytchB2BClient.configurationManager.options.endpointOptions,
                            StytchB2BApi.isTestToken,
                        )
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
                    val intent = SSOManagerActivity.createBaseIntent(activity)
                    intent.putExtra(SSOManagerActivity.URI_KEY, requestUri.toString())
                    launcher.launch(intent)
                }
            }

        override fun getTokenForProvider(
            parameters: OAuth.ProviderDiscovery.GetTokenForProviderParams,
            callback: (StytchResult<String>) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                callback(getTokenForProvider(parameters))
            }
        }

        override fun getTokenForProviderCompletable(
            parameters: OAuth.ProviderDiscovery.GetTokenForProviderParams,
        ): CompletableFuture<StytchResult<String>> =
            externalScope
                .async {
                    getTokenForProvider(parameters)
                }.asCompletableFuture()
    }

    private inner class DiscoveryImpl : OAuth.Discovery {
        override suspend fun authenticate(
            parameters: OAuth.Discovery.DiscoveryAuthenticateParameters,
        ): OAuthDiscoveryAuthenticateResponse =
            withContext(dispatchers.io) {
                val pkce =
                    pkcePairManager.getPKCECodePair()?.codeVerifier
                        ?: run {
                            StytchB2BClient.events.logEvent(
                                "b2b_discovery_oauth_failure",
                                null,
                                StytchMissingPKCEError(null),
                            )
                            return@withContext StytchResult.Error(StytchMissingPKCEError(null))
                        }
                api
                    .discoveryAuthenticate(
                        pkceCodeVerifier = pkce,
                        discoveryOauthToken = parameters.discoveryOauthToken,
                    ).apply {
                        pkcePairManager.clearPKCECodePair()
                        sessionStorage.lastAuthMethodUsed = B2BAuthMethod.OAUTH
                        when (this) {
                            is StytchResult.Success -> {
                                StytchB2BClient.events.logEvent("b2b_discovery_oauth_success")
                                sessionStorage.intermediateSessionToken = value.intermediateSessionToken
                            }
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

        override fun authenticateCompletable(
            parameters: OAuth.Discovery.DiscoveryAuthenticateParameters,
        ): CompletableFuture<OAuthDiscoveryAuthenticateResponse> =
            externalScope
                .async {
                    authenticate(parameters)
                }.asCompletableFuture()
    }
}
