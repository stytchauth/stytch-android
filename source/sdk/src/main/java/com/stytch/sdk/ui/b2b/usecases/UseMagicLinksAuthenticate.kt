package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.magicLinks.B2BMagicLinks
import com.stytch.sdk.b2b.network.models.B2BEMLAuthenticateData
import com.stytch.sdk.ui.b2b.PerformRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class UseMagicLinksAuthenticate(
    private val scope: CoroutineScope,
    private val request: PerformRequest<B2BEMLAuthenticateData>,
) {
    operator fun invoke(token: String) {
        scope.launch(Dispatchers.IO) {
            request {
                StytchB2BClient.magicLinks.authenticate(
                    B2BMagicLinks.AuthParameters(
                        token = token,
                    ),
                )
            }
        }
    }
}
