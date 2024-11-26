package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.OAuthAuthenticateResponseData
import com.stytch.sdk.b2b.oauth.OAuth
import com.stytch.sdk.ui.b2b.PerformRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class UseOAuthAuthenticate(
    private val scope: CoroutineScope,
    private val request: PerformRequest<OAuthAuthenticateResponseData>,
) {
    operator fun invoke(token: String) {
        scope.launch(Dispatchers.IO) {
            request {
                StytchB2BClient.oauth.authenticate(
                    OAuth.AuthenticateParameters(
                        oauthToken = token,
                    ),
                )
            }
        }
    }
}
