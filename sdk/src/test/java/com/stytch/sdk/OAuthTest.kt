package com.stytch.sdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import io.mockk.mockk
import org.junit.Test

internal class OAuthTest {
    @Test
    fun `OAuth Google StartParameters has correct defaults`() {
        val mockActivity: Activity = mockk()
        val params = OAuth.GoogleOneTap.StartParameters(
            context = mockActivity,
            clientId = "clientId",
            oAuthRequestIdentifier = 1,
        )
        val expected = OAuth.GoogleOneTap.StartParameters(
            context = mockActivity,
            clientId = "clientId",
            oAuthRequestIdentifier = 1,
            autoSelectEnabled = false
        )
        assert(params == expected)
    }

    @Test
    fun `OAuth Google AuthenticateParameters has correct defaults`() {
        val mockIntent: Intent = mockk()
        val params = OAuth.GoogleOneTap.AuthenticateParameters(
            data = mockIntent,
        )
        val expected = OAuth.GoogleOneTap.AuthenticateParameters(
            data = mockIntent,
            sessionDurationMinutes = Constants.DEFAULT_SESSION_TIME_MINUTES
        )
        assert(params == expected)
    }

    @Test
    fun `OAuth ThirdParty StartParameters has correct defaults`() {
        val mockContext: Context = mockk()
        val params = OAuth.ThirdParty.StartParameters(
            context = mockContext
        )
        val expected = OAuth.ThirdParty.StartParameters(
            context = mockContext,
            loginRedirectUrl = null,
            signupRedirectUrl = null,
            customScopes = null
        )
        assert(params == expected)
    }

    @Test
    fun `OAuth ThirdParty AuthenticateParameters has correct defaults`() {
        val params = OAuth.ThirdParty.AuthenticateParameters(token = "token")
        val expected = OAuth.ThirdParty.AuthenticateParameters(
            token = "token",
            sessionDurationMinutes = Constants.DEFAULT_SESSION_TIME_MINUTES,
        )
        assert(params == expected)
    }

    // TODO authenticate tests
}
