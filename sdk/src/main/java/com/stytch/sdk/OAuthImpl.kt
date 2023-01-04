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
    override val amazon: OAuth.ThirdParty = ThirdPartyOauthImpl(
        storageHelper,
        "amazon"
    )
    override val bitbucket: OAuth.ThirdParty = ThirdPartyOauthImpl(
        storageHelper,
        "bitbucket"
    )
    override val coinbase: OAuth.ThirdParty = ThirdPartyOauthImpl(
        storageHelper,
        "coinbase"
    )
    override val discord: OAuth.ThirdParty = ThirdPartyOauthImpl(
        storageHelper,
        "discord"
    )
    override val facebook: OAuth.ThirdParty = ThirdPartyOauthImpl(
        storageHelper,
        "facebook"
    )
    override val github: OAuth.ThirdParty = ThirdPartyOauthImpl(
        storageHelper,
        "github"
    )
    override val gitlab: OAuth.ThirdParty = ThirdPartyOauthImpl(
        storageHelper,
        "gitlab"
    )
    override val google: OAuth.ThirdParty = ThirdPartyOauthImpl(
        storageHelper,
        "google"
    )
    override val linkedin: OAuth.ThirdParty = ThirdPartyOauthImpl(
        storageHelper,
        "linkedin"
    )
    override val microsoft: OAuth.ThirdParty = ThirdPartyOauthImpl(
        storageHelper,
        "microsoft"
    )
    override val slack: OAuth.ThirdParty = ThirdPartyOauthImpl(
        storageHelper,
        "slack"
    )
    override val twitch: OAuth.ThirdParty = ThirdPartyOauthImpl(
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
