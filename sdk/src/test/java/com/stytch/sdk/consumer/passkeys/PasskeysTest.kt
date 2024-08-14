package com.stytch.sdk.consumer.passkeys

import android.app.Activity
import com.stytch.sdk.common.DEFAULT_SESSION_TIME_MINUTES
import io.mockk.mockk
import org.junit.Test

internal class PasskeysTest {
    @Test
    fun `Passkeys AuthenticateParameters have correct default values`() {
        val mockActivity: Activity = mockk()
        val params =
            Passkeys.AuthenticateParameters(
                activity = mockActivity,
                domain = "test.domain.com",
            )
        val expected =
            Passkeys.AuthenticateParameters(
                activity = mockActivity,
                domain = "test.domain.com",
                sessionDurationMinutes = DEFAULT_SESSION_TIME_MINUTES,
            )
        assert(params == expected)
    }
}
