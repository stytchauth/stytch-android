package com.stytch.sdk.b2b.sessions

import org.junit.Test

internal class B2BSessionsTest {
    @Test
    fun `Sessions AuthParams have correct default values`() {
        val params = B2BSessions.AuthParams()
        val expected = B2BSessions.AuthParams(sessionDurationMinutes = null)
        assert(params == expected)
    }

    @Test
    fun `Sessions ExchangeParameters have correct default values`() {
        val params =
            B2BSessions.ExchangeParameters(
                organizationId = "test-123",
                sessionDurationMinutes = 30,
            )
        val expected =
            B2BSessions.ExchangeParameters(
                organizationId = "test-123",
                locale = null,
                sessionDurationMinutes = 30,
            )
        assert(params == expected)
    }
}
