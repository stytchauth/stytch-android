package com.stytch.sdk

import android.content.Context
import io.mockk.mockk
import org.junit.Test

internal class BiometricsTest {
    @Test
    fun `Biometrics StartParameters have correct default values`() {
        val mockContext: Context = mockk()
        val params = Biometrics.StartParameters(context = mockContext)
        val expected = Biometrics.StartParameters(
            context = mockContext,
            sessionDurationMinutes = Constants.DEFAULT_SESSION_TIME_MINUTES,
            allowFallbackToCleartext = false,
        )
        assert(params == expected)
    }
}
