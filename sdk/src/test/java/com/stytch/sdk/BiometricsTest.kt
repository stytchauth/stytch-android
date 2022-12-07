package com.stytch.sdk

import androidx.fragment.app.FragmentActivity
import io.mockk.mockk
import org.junit.Test

internal class BiometricsTest {
    @Test
    fun `Biometrics StartParameters have correct default values`() {
        val mockContext: FragmentActivity = mockk()
        val params = Biometrics.RegisterParameters(context = mockContext)
        val expected = Biometrics.RegisterParameters(
            context = mockContext,
            sessionDurationMinutes = Constants.DEFAULT_SESSION_TIME_MINUTES,
            allowFallbackToCleartext = false,
            promptInfo = null,
            showBiometricPrompt = true,
            failOnExistingRegistration = false,
        )
        assert(params == expected)
    }
}
