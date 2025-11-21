package com.stytch.sdk.consumer.oauth

import androidx.activity.ComponentActivity
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchMissingPKCEError
import com.stytch.sdk.common.pkcePairManager.PKCEPairManager
import com.stytch.sdk.common.sso.ProvidedReceiverManager
import com.stytch.sdk.consumer.ConsumerAuthMethod
import com.stytch.sdk.consumer.OAuthAttachResponse
import com.stytch.sdk.consumer.OAuthAuthenticatedResponse
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.sessions.ConsumerSessionStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

internal class OAuthImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: ConsumerSessionStorage,
    private val api: StytchApi.OAuth,
    private val pkcePairManager: PKCEPairManager,
    private val providedReceiverManager: ProvidedReceiverManager = ProvidedReceiverManager,
) : OAuth {
    override fun setOAuthReceiverActivity(activity: ComponentActivity?) {
        ProvidedReceiverManager.configureReceiver(activity)
    }

    override val googleOneTap: OAuth.GoogleOneTap =
        GoogleOneTapImpl(
            externalScope,
            dispatchers,
            sessionStorage,
            api,
            GoogleCredentialManagerProviderImpl(),
            pkcePairManager,
        )
    override val apple: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "apple",
            providedReceiverManager::getReceiverConfiguration,
        )
    override val amazon: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "amazon",
            providedReceiverManager::getReceiverConfiguration,
        )
    override val bitbucket: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "bitbucket",
            providedReceiverManager::getReceiverConfiguration,
        )
    override val coinbase: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "coinbase",
            providedReceiverManager::getReceiverConfiguration,
        )
    override val discord: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "discord",
            providedReceiverManager::getReceiverConfiguration,
        )
    override val facebook: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "facebook",
            providedReceiverManager::getReceiverConfiguration,
        )
    override val figma: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "figma",
            providedReceiverManager::getReceiverConfiguration,
        )
    override val github: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "github",
            providedReceiverManager::getReceiverConfiguration,
        )
    override val gitlab: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "gitlab",
            providedReceiverManager::getReceiverConfiguration,
        )
    override val google: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "google",
            providedReceiverManager::getReceiverConfiguration,
        )
    override val linkedin: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "linkedin",
            providedReceiverManager::getReceiverConfiguration,
        )
    override val microsoft: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "microsoft",
            providedReceiverManager::getReceiverConfiguration,
        )
    override val salesforce: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "salesforce",
            providedReceiverManager::getReceiverConfiguration,
        )
    override val slack: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "slack",
            providedReceiverManager::getReceiverConfiguration,
        )
    override val snapchat: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "snapchat",
            providedReceiverManager::getReceiverConfiguration,
        )
    override val tiktok: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "tiktok",
            providedReceiverManager::getReceiverConfiguration,
        )
    override val twitch: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "twitch",
            providedReceiverManager::getReceiverConfiguration,
        )
    override val twitter: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "twitter",
            providedReceiverManager::getReceiverConfiguration,
        )
    override val yahoo: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "yahoo",
            providedReceiverManager::getReceiverConfiguration,
        )

    override suspend fun authenticate(parameters: OAuth.ThirdParty.AuthenticateParameters): OAuthAuthenticatedResponse {
        return withContext(dispatchers.io) {
            val pkce =
                pkcePairManager.getPKCECodePair()?.codeVerifier
                    ?: run {
                        StytchClient.events.logEvent("oauth_failure", null, StytchMissingPKCEError(null))
                        return@withContext StytchResult.Error(StytchMissingPKCEError(null))
                    }
            api
                .authenticateWithThirdPartyToken(
                    token = parameters.token,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                    codeVerifier = pkce,
                ).apply {
                    pkcePairManager.clearPKCECodePair()
                    when (this) {
                        is StytchResult.Success -> StytchClient.events.logEvent("oauth_success")
                        is StytchResult.Error -> StytchClient.events.logEvent("oauth_failure", null, this.exception)
                    }
                    sessionStorage.lastAuthMethodUsed = ConsumerAuthMethod.OAUTH
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
        }
    }

    override fun authenticate(
        parameters: OAuth.ThirdParty.AuthenticateParameters,
        callback: (OAuthAuthenticatedResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = authenticate(parameters)
            callback(result)
        }
    }

    override fun authenticateCompletable(
        parameters: OAuth.ThirdParty.AuthenticateParameters,
    ): CompletableFuture<OAuthAuthenticatedResponse> =
        externalScope
            .async {
                authenticate(parameters)
            }.asCompletableFuture()

    override suspend fun attach(parameters: OAuth.AttachParameters): OAuthAttachResponse =
        withContext(dispatchers.io) {
            api.attach(
                provider = parameters.provider,
            )
        }

    override fun attach(
        parameters: OAuth.AttachParameters,
        callback: (OAuthAttachResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            attach(parameters)
        }
    }

    override fun attachCompletable(parameters: OAuth.AttachParameters): CompletableFuture<OAuthAttachResponse> =
        externalScope
            .async {
                attach(parameters)
            }.asCompletableFuture()
}
