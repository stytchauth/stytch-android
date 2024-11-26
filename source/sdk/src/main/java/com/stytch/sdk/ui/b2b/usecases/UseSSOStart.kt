package com.stytch.sdk.ui.b2b.usecases
import android.app.Activity
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.sso.SSO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal const val SSO_START_IDENTIFIER = 5552

internal class UseSSOStart(
    private val scope: CoroutineScope,
) {
    operator fun invoke(
        context: Activity,
        connectionId: String,
    ) {
        scope.launch(Dispatchers.IO) {
            StytchB2BClient.sso.start(
                SSO.StartParams(
                    context = context,
                    ssoAuthRequestIdentifier = SSO_START_IDENTIFIER,
                    connectionId = connectionId,
                    loginRedirectUrl = getRedirectUrl(),
                    signupRedirectUrl = getRedirectUrl(),
                ),
            )
        }
    }
}
