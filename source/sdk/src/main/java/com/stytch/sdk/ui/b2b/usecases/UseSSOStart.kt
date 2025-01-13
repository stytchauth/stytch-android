package com.stytch.sdk.ui.b2b.usecases
import android.app.Activity
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.sso.SSO

internal const val SSO_START_IDENTIFIER = 5552

internal class UseSSOStart {
    operator fun invoke(
        context: Activity,
        connectionId: String,
    ) {
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
