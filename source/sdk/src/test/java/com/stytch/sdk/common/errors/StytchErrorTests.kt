@file:Suppress("MaxLineLength", "ktlint:standard:max-line-length")

package com.stytch.sdk.common.errors

import org.junit.Test

internal class StytchErrorTests {
    @Test
    fun `StytchAPIError has expected properties`() {
        val error =
            StytchAPIError(
                requestId = "request-id-1234",
                errorType = "error_name",
                message = "error_description",
                url = "https://stytch.com",
                statusCode = 400,
            )
        assert(error.requestId == "request-id-1234")
        assert(error.errorType == "error_name")
        assert(error.message == "error_description")
        assert(error.url == "https://stytch.com")
    }

    @Test
    fun `StytchAPISchemaError has expected properties`() {
        val error = StytchAPISchemaError(message = "a schema error occurred")
        assert(error.message == "a schema error occurred")
    }

    @Test
    fun `StytchAPIUnreachableError has expected properties`() {
        val underlyingException = RuntimeException("testing")
        val error =
            StytchAPIUnreachableError(
                message = "a schema error occurred",
                exception = underlyingException,
            )
        assert(error.message == "a schema error occurred")
        assert(error.exception == underlyingException)
    }

    @Test
    fun `StytchSDKNotConfiguredError has expected properties`() {
        val error = StytchSDKNotConfiguredError("Test")
        assert(
            error.message ==
                "Test not configured. You must call `Test.configure(...)` before using any functionality of the SDK.",
        )
    }

    @Test
    fun `StytchInternalError has expected properties`() {
        val underlyingException = RuntimeException("testing")
        val error1 = StytchInternalError(exception = underlyingException)
        assert(error1.exception == underlyingException)
        assert(error1.message == "An internal error has occurred. Please contact Stytch if this occurs.")
        val error2 = StytchInternalError(message = "test")
        assert(error2.exception == null)
        assert(error2.message == "test")
    }

    @Test
    fun `StytchMissingPKCEError has expected properties`() {
        val underlyingException = RuntimeException("testing")
        val error = StytchMissingPKCEError(underlyingException)
        assert(
            error.message ==
                "The PKCE code challenge or code verifier is missing. Make sure this flow is completed on the same device on which it was started.",
        )
        assert(error.exception == underlyingException)
    }

    @Test
    fun `StytchFailedToCreateCodeChallengeError has expected properties`() {
        val underlyingException = RuntimeException("testing")
        val error = StytchFailedToCreateCodeChallengeError(underlyingException)
        assert(error.message == "Failed to generate a PKCE code challenge")
        assert(error.exception == underlyingException)
    }

    @Test
    fun `StytchDeeplinkUnkownTokenTypeError has expected properties`() {
        val error = StytchDeeplinkUnkownTokenTypeError()
        assert(error.message == "The deeplink received has an unknown token type.")
    }

    @Test
    fun `StytchDeeplinkMissingTokenError has expected properties`() {
        val error = StytchDeeplinkMissingTokenError()
        assert(error.message == "The deeplink received has a missing token value.")
    }

    @Test
    fun `StytchNoCurrentSessionError has expected properties`() {
        val error = StytchNoCurrentSessionError()
        assert(error.message == "There is no session currently available.")
    }

    @Test
    fun `StytchNoBiometricsRegistrationError has expected properties`() {
        val error = StytchNoBiometricsRegistrationError()
        assert(
            error.message ==
                "There is no biometric registration available. Authenticate with another method and add a new biometric registration first.",
        )
    }

    @Test
    fun `StytchKeystoreUnavailableError has expected properties`() {
        val error = StytchKeystoreUnavailableError()
        assert(
            error.message ==
                "The Android keystore is unavailable on the device. Consider setting allowFallbackToCleartext to true.",
        )
    }

    @Test
    fun `StytchMissingPublicKeyError has expected properties`() {
        val underlyingException = RuntimeException("test")
        val error = StytchMissingPublicKeyError(underlyingException)
        assert(error.message == "Failed to retrieve the public key. Add a new biometric registration.")
        assert(error.exception == underlyingException)
    }

    @Test
    fun `StytchChallengeSigningFailed has expected properties`() {
        val underlyingException = RuntimeException("test")
        val error = StytchChallengeSigningFailed(underlyingException)
        assert(error.message == "Failed to sign the challenge with the key.")
        assert(error.exception == underlyingException)
    }

    @Test
    fun `StytchMissingAuthorizationCredentialIdTokenError has expected properties`() {
        val error = StytchMissingAuthorizationCredentialIdTokenError()
        assert(error.message == "The authorization credential is missing an ID token.")
    }

    @Test
    fun `StytchInvalidAuthorizationCredentialError has expected properties`() {
        val error = StytchInvalidAuthorizationCredentialError()
        assert(error.message == "The authorization credential is invalid.")
    }

    @Test
    fun `StytchPasskeysNotSupportedError has expected properties`() {
        val error = StytchPasskeysNotSupportedError()
        assert(error.message == "Passkeys are not supported on this device.")
    }

    @Test
    fun `StytchFailedToDecryptDataError has expected properties`() {
        val underlyingException = RuntimeException("test")
        val error = StytchFailedToDecryptDataError(underlyingException)
        assert(error.message == "Failed to decrypt user data")
        assert(error.exception == underlyingException)
    }

    @Test
    fun `StytchBiometricAuthenticationFailed has expected properties`() {
        val error = StytchBiometricAuthenticationFailed("Some reason from the device")
        assert(error.message == "Biometric authentication failed")
        assert(error.reason == "Some reason from the device")
    }
}
