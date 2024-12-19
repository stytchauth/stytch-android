package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.OAuthAuthenticateResponseData
import com.stytch.sdk.b2b.oauth.OAuth
import com.stytch.sdk.ui.b2b.Dispatch
import com.stytch.sdk.ui.b2b.PerformRequest
import com.stytch.sdk.ui.b2b.data.B2BErrorType
import com.stytch.sdk.ui.b2b.data.SetB2BError
import com.stytch.sdk.ui.b2b.data.SetDeeplinkTokenPair
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class UseOAuthAuthenticate(
    private val scope: CoroutineScope,
    private val productConfig: StytchB2BProductConfig,
    private val dispatch: Dispatch,
    private val request: PerformRequest<OAuthAuthenticateResponseData>,
) {
    operator fun invoke(token: String) {
        scope.launch(Dispatchers.IO) {
            request {
                StytchB2BClient.oauth.authenticate(
                    OAuth.AuthenticateParameters(
                        oauthToken = token,
                        sessionDurationMinutes = productConfig.sessionOptions.sessionDurationMinutes,
                    ),
                )
            }.onSuccess {
                dispatch(SetDeeplinkTokenPair(null))
            }.onFailure {
                dispatch(SetDeeplinkTokenPair(null))
                dispatch(SetB2BError(B2BErrorType.Default))
            }
        }
    }
}
