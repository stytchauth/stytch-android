package com.stytch.sdk.consumer.oauth

import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchMissingPKCEError
import com.stytch.sdk.common.pkcePairManager.PKCEPairManager
import com.stytch.sdk.consumer.OAuthAuthenticatedResponse
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.sessions.ConsumerSessionStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class OAuthImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: ConsumerSessionStorage,
    private val api: StytchApi.OAuth,
    private val pkcePairManager: PKCEPairManager,
) : OAuth {
    override val googleOneTap: OAuth.GoogleOneTap =
        GoogleOneTapImpl(
            externalScope,
            dispatchers,
            sessionStorage,
            api,
            GoogleCredentialManagerProviderImpl(),
            pkcePairManager,
        )
    override val apple: OAuth.ThirdParty = ThirdPartyOAuthImpl(pkcePairManager, providerName = "apple")
    override val amazon: OAuth.ThirdParty = ThirdPartyOAuthImpl(pkcePairManager, providerName = "amazon")
    override val bitbucket: OAuth.ThirdParty = ThirdPartyOAuthImpl(pkcePairManager, providerName = "bitbucket")
    override val coinbase: OAuth.ThirdParty = ThirdPartyOAuthImpl(pkcePairManager, providerName = "coinbase")
    override val discord: OAuth.ThirdParty = ThirdPartyOAuthImpl(pkcePairManager, providerName = "discord")
    override val facebook: OAuth.ThirdParty = ThirdPartyOAuthImpl(pkcePairManager, providerName = "facebook")
    override val figma: OAuth.ThirdParty = ThirdPartyOAuthImpl(pkcePairManager, providerName = "figma")
    override val github: OAuth.ThirdParty = ThirdPartyOAuthImpl(pkcePairManager, providerName = "github")
    override val gitlab: OAuth.ThirdParty = ThirdPartyOAuthImpl(pkcePairManager, providerName = "gitlab")
    override val google: OAuth.ThirdParty = ThirdPartyOAuthImpl(pkcePairManager, providerName = "google")
    override val linkedin: OAuth.ThirdParty = ThirdPartyOAuthImpl(pkcePairManager, providerName = "linkedin")
    override val microsoft: OAuth.ThirdParty = ThirdPartyOAuthImpl(pkcePairManager, providerName = "microsoft")
    override val salesforce: OAuth.ThirdParty = ThirdPartyOAuthImpl(pkcePairManager, providerName = "salesforce")
    override val slack: OAuth.ThirdParty = ThirdPartyOAuthImpl(pkcePairManager, providerName = "slack")
    override val snapchat: OAuth.ThirdParty = ThirdPartyOAuthImpl(pkcePairManager, providerName = "snapchat")
    override val tiktok: OAuth.ThirdParty = ThirdPartyOAuthImpl(pkcePairManager, providerName = "tiktok")
    override val twitch: OAuth.ThirdParty = ThirdPartyOAuthImpl(pkcePairManager, providerName = "twitch")
    override val twitter: OAuth.ThirdParty = ThirdPartyOAuthImpl(pkcePairManager, providerName = "twitter")
    override val yahoo: OAuth.ThirdParty = ThirdPartyOAuthImpl(pkcePairManager, providerName = "yahoo")

    override suspend fun authenticate(parameters: OAuth.ThirdParty.AuthenticateParameters): OAuthAuthenticatedResponse {
        return withContext(dispatchers.io) {
            val pkce =
                pkcePairManager.getPKCECodePair()?.codeVerifier
                    ?: run {
                        StytchClient.events.logEvent("oauth_failure", null, StytchMissingPKCEError(null))
                        return@withContext StytchResult.Error(StytchMissingPKCEError(null))
                    }
            api.authenticateWithThirdPartyToken(
                token = parameters.token,
                sessionDurationMinutes = parameters.sessionDurationMinutes,
                codeVerifier = pkce,
            ).apply {
                pkcePairManager.clearPKCECodePair()
                when (this) {
                    is StytchResult.Success -> StytchClient.events.logEvent("oauth_success")
                    is StytchResult.Error -> StytchClient.events.logEvent("oauth_failure", null, this.exception)
                }
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
}
