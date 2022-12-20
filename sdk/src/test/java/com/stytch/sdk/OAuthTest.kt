package com.stytch.sdk

import android.app.Activity
import android.content.Intent
import io.mockk.mockk
import org.junit.Test

internal class OAuthTest {
    @Test
    fun `OAuth Google StartParameters has correct defaults`() {
        val mockActivity: Activity = mockk()
        val params = OAuth.Google.StartParameters(
            context = mockActivity,
            clientId = "clientId",
            oAuthRequestIdentifier = 1,
        )
        val expected = OAuth.Google.StartParameters(
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
        val params = OAuth.Google.AuthenticateParameters(
            data = mockIntent,
        )
        val expected = OAuth.Google.AuthenticateParameters(
            data = mockIntent,
            sessionDurationMinutes = Constants.DEFAULT_SESSION_TIME_MINUTES
        )
        assert(params == expected)
    }
}
