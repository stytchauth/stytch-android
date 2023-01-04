package com.stytch.sdk

import com.stytch.sdk.network.StytchApi
import com.stytch.sdk.network.StytchErrorType
import com.stytch.sessions.SessionStorage
import com.stytch.sessions.launchSessionUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class OAuthImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: SessionStorage,
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
    override val amazon: OAuth.ThirdParty = ThirdPartyOAuthImpl(
        storageHelper,
        "amazon"
    )
    override val bitbucket: OAuth.ThirdParty = ThirdPartyOAuthImpl(
        storageHelper,
        "bitbucket"
    )
    override val coinbase: OAuth.ThirdParty = ThirdPartyOAuthImpl(
        storageHelper,
        "coinbase"
    )
    override val discord: OAuth.ThirdParty = ThirdPartyOAuthImpl(
        storageHelper,
        "discord"
    )
    override val facebook: OAuth.ThirdParty = ThirdPartyOAuthImpl(
        storageHelper,
        "facebook"
    )
    override val github: OAuth.ThirdParty = ThirdPartyOAuthImpl(
        storageHelper,
        "github"
    )
    override val gitlab: OAuth.ThirdParty = ThirdPartyOAuthImpl(
        storageHelper,
        "gitlab"
    )
    override val google: OAuth.ThirdParty = ThirdPartyOAuthImpl(
        storageHelper,
        "google"
    )
    override val linkedin: OAuth.ThirdParty = ThirdPartyOAuthImpl(
        storageHelper,
        "linkedin"
    )
    override val microsoft: OAuth.ThirdParty = ThirdPartyOAuthImpl(
        storageHelper,
        "microsoft"
    )
    override val slack: OAuth.ThirdParty = ThirdPartyOAuthImpl(
        storageHelper,
        "slack"
    )
    override val twitch: OAuth.ThirdParty = ThirdPartyOAuthImpl(
        storageHelper,
        "twitch"
    )

    override suspend fun authenticate(parameters: OAuth.ThirdParty.AuthenticateParameters): OAuthAuthenticatedResponse {
        return withContext(dispatchers.io) {
            val pkce = storageHelper.retrieveHashedCodeChallenge()
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
