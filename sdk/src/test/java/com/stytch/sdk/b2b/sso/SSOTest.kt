package com.stytch.sdk.b2b.sso

import com.stytch.sdk.common.Constants
import org.junit.Test

internal class SSOTest {
    @Test
    fun `SSO AuthenticateParameters have correct default values`() {
        val params = SSO.AuthenticateParams(ssoToken = "sso-token")
        val expected = SSO.AuthenticateParams(
            ssoToken = "sso-token",
            sessionDurationMinutes = Constants.DEFAULT_SESSION_TIME_MINUTES
        )
        assert(params == expected)
    }
}
