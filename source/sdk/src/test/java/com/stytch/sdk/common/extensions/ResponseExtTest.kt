package com.stytch.sdk.common.extensions

import io.mockk.every
import io.mockk.mockk
import okhttp3.Response
import org.junit.Test

internal class ResponseExtTest {
    @Test
    fun `non-403 codes return false`() {
        val response: Response =
            mockk {
                every { code } returns 200
            }
        assert(!response.requiresCaptcha())
    }

    @Test
    fun `403 code returns true`() {
        val response: Response =
            mockk {
                every { code } returns 403
            }
        assert(response.requiresCaptcha())
    }
}
