package com.stytch.sdk.consumer.sessions

import org.junit.Test

internal class SessionsTest {
    @Test
    fun `Sessions AuthParams have correct default values`() {
        val params = Sessions.AuthParams()
        val expected = Sessions.AuthParams(sessionDurationMinutes = null)
        assert(params == expected)
    }
}
