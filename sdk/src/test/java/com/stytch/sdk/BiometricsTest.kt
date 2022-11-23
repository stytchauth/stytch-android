package com.stytch.sdk

import org.junit.Test

internal class BiometricsTest {
    @Test
    fun `Biometrics StartParameters have correct default values`() {
        val params = Biometrics.StartParameters()
        val expected = Biometrics.StartParameters(
            sessionDurationMinutes = Constants.DEFAULT_SESSION_TIME_MINUTES,
        )
        assert(params == expected)
    }

    @Test
    fun `Biometrics RegisterParameters have correct default values`() {
        val params = Biometrics.RegisterParameters("signature", "biometricsRegistrationId")
        val expected = Biometrics.RegisterParameters(
            signature = "signature",
            biometricRegistrationId = "biometricsRegistrationId",
            sessionDurationMinutes = Constants.DEFAULT_SESSION_TIME_MINUTES,
        )
        assert(params == expected)
    }

    @Test
    fun `Biometrics AuthenticateStartParameters have correct default values`() {
        val params = Biometrics.AuthenticateStartParameters("publicKey")
        val expected = Biometrics.AuthenticateStartParameters(
            publicKey = "publicKey",
            sessionDurationMinutes = Constants.DEFAULT_SESSION_TIME_MINUTES,
        )
        assert(params == expected)
    }

    @Test
    fun `Biometrics AuthenticateParameters have correct default values`() {
        val params = Biometrics.AuthenticateParameters("signature", "biometricsRegistrationId")
        val expected = Biometrics.AuthenticateParameters(
            signature = "signature",
            biometricRegistrationId = "biometricsRegistrationId",
            sessionDurationMinutes = Constants.DEFAULT_SESSION_TIME_MINUTES,
        )
        assert(params == expected)
    }
}
