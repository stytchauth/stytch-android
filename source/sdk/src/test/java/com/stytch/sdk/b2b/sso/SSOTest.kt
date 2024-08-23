package com.stytch.sdk.b2b.sso

import android.app.Activity
import com.stytch.sdk.common.DEFAULT_SESSION_TIME_MINUTES
import io.mockk.mockk
import org.junit.Test

internal class SSOTest {
    @Test
    fun `SSO AuthenticateParams have correct default values`() {
        val params = SSO.AuthenticateParams(ssoToken = "sso-token")
        val expected =
            SSO.AuthenticateParams(
                ssoToken = "sso-token",
                sessionDurationMinutes = DEFAULT_SESSION_TIME_MINUTES,
            )
        assert(params == expected)
    }

    @Test
    fun `SSO StartParams have correct default values`() {
        val mockActivity = mockk<Activity>(relaxed = true)
        val params =
            SSO.StartParams(
                context = mockActivity,
                ssoAuthRequestIdentifier = 1234,
                connectionId = "connection-id",
            )
        val expected =
            SSO.StartParams(
                context = mockActivity,
                ssoAuthRequestIdentifier = 1234,
                connectionId = "connection-id",
                loginRedirectUrl = null,
                signupRedirectUrl = null,
            )
        assert(params == expected)
    }
}
