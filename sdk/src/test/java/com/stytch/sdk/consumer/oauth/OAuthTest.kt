package com.stytch.sdk.consumer.oauth

import android.app.Activity
import com.stytch.sdk.common.Constants
import io.mockk.mockk
import org.junit.Test

internal class OAuthTest {
    @Test
    fun `OAuth Google StartParameters has correct defaults`() {
        val mockActivity: Activity = mockk()
        val params =
            OAuth.GoogleOneTap.StartParameters(
                context = mockActivity,
                clientId = "clientId",
            )
        val expected =
            OAuth.GoogleOneTap.StartParameters(
                context = mockActivity,
                clientId = "clientId",
                autoSelectEnabled = false,
                sessionDurationMinutes = Constants.DEFAULT_SESSION_TIME_MINUTES,
            )
        assert(params == expected)
    }

    @Test
    fun `OAuth ThirdParty StartParameters has correct defaults`() {
        val mockContext: Activity = mockk()
        val params =
            OAuth.ThirdParty.StartParameters(
                context = mockContext,
                oAuthRequestIdentifier = 1234,
            )
        val expected =
            OAuth.ThirdParty.StartParameters(
                context = mockContext,
                oAuthRequestIdentifier = 1234,
                loginRedirectUrl = null,
                signupRedirectUrl = null,
                customScopes = null,
            )
        assert(params == expected)
    }

    @Test
    fun `OAuth ThirdParty AuthenticateParameters has correct defaults`() {
        val params = OAuth.ThirdParty.AuthenticateParameters(token = "token")
        val expected =
            OAuth.ThirdParty.AuthenticateParameters(
                token = "token",
                sessionDurationMinutes = Constants.DEFAULT_SESSION_TIME_MINUTES,
            )
        assert(params == expected)
    }
}
