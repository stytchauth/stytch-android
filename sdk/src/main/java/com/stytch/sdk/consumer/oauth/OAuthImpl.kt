package com.stytch.sdk.consumer.oauth

import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.StytchErrorType
import com.stytch.sdk.common.oauth.GoogleOneTapProviderImpl
import com.stytch.sdk.consumer.OAuthAuthenticatedResponse
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
    private val storageHelper: StorageHelper,
    private val api: StytchApi.OAuth,
) : OAuth {
    override val googleOneTap: OAuth.GoogleOneTap = GoogleOneTapImpl(
        externalScope,
        dispatchers,
        sessionStorage,
        api,
        GoogleOneTapProviderImpl()
    )
    override val apple: OAuth.ThirdParty = ThirdPartyOAuthImpl(storageHelper, providerName = "apple")
    override val amazon: OAuth.ThirdParty = ThirdPartyOAuthImpl(storageHelper, providerName = "amazon")
    override val bitbucket: OAuth.ThirdParty = ThirdPartyOAuthImpl(storageHelper, providerName = "bitbucket")
    override val coinbase: OAuth.ThirdParty = ThirdPartyOAuthImpl(storageHelper, providerName = "coinbase")
    override val discord: OAuth.ThirdParty = ThirdPartyOAuthImpl(storageHelper, providerName = "discord")
    override val facebook: OAuth.ThirdParty = ThirdPartyOAuthImpl(storageHelper, providerName = "facebook")
    override val github: OAuth.ThirdParty = ThirdPartyOAuthImpl(storageHelper, providerName = "github")
    override val gitlab: OAuth.ThirdParty = ThirdPartyOAuthImpl(storageHelper, providerName = "gitlab")
    override val google: OAuth.ThirdParty = ThirdPartyOAuthImpl(storageHelper, providerName = "google")
    override val linkedin: OAuth.ThirdParty = ThirdPartyOAuthImpl(storageHelper, providerName = "linkedin")
    override val microsoft: OAuth.ThirdParty = ThirdPartyOAuthImpl(storageHelper, providerName = "microsoft")
    override val slack: OAuth.ThirdParty = ThirdPartyOAuthImpl(storageHelper, providerName = "slack")
    override val twitch: OAuth.ThirdParty = ThirdPartyOAuthImpl(storageHelper, providerName = "twitch")

    override suspend fun authenticate(parameters: OAuth.ThirdParty.AuthenticateParameters): OAuthAuthenticatedResponse {
        return withContext(dispatchers.io) {
            val pkce = storageHelper.retrieveCodeVerifier()
                ?: return@withContext StytchResult.Error(
                    StytchExceptions.Input(StytchErrorType.OAUTH_MISSING_PKCE.message)
                )
            api.authenticateWithThirdPartyToken(
                token = parameters.token,
                sessionDurationMinutes = parameters.sessionDurationMinutes,
                codeVerifier = pkce
            ).apply {
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