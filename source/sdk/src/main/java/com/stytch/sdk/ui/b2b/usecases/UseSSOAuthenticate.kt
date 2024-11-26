package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.SSOAuthenticateResponseData
import com.stytch.sdk.b2b.sso.SSO
import com.stytch.sdk.ui.b2b.PerformRequest
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class UseSSOAuthenticate(
    private val scope: CoroutineScope,
    private val productConfig: StytchB2BProductConfig,
    private val request: PerformRequest<SSOAuthenticateResponseData>,
) {
    operator fun invoke(token: String) {
        scope.launch(Dispatchers.IO) {
            request {
                StytchB2BClient.sso.authenticate(
                    SSO.AuthenticateParams(
                        ssoToken = token,
                        sessionDurationMinutes = productConfig.sessionOptions.sessionDurationMinutes,
                    ),
                )
            }
        }
    }
}
