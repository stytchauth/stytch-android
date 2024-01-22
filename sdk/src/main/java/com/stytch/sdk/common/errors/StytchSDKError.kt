@file:Suppress("MaxLineLength")
/* ktlint-disable max-line-length */

package com.stytch.sdk.common.errors

/**
 * A base class representing SDK specific errors or exceptions that may occur. This class should not be used directly,
 * rather we should be creating implementations for each of the known/expected errors we return.
 */
public sealed class StytchSDKError(
    message: String,
    public open val exception: Throwable? = null,
) : StytchError(message = message)

/**
 * Thrown when you try to use the SDK before it has been configured
 */
public data class StytchSDKNotConfiguredError(val clientName: String) : StytchSDKError(
    message = "$clientName not configured. You must call `$clientName.configure(...)` before using any functionality of the SDK."
)

/**
 * Thrown when all else fails. We should audit our usage of this and see if it makes sense to create classes for these
 */
public data class StytchInternalError(
    public override val exception: Throwable? = null,
    public override val message: String = "An internal error has occurred. Please contact Stytch if this occurs.",
) : StytchSDKError(
    message = message,
    exception = exception,
)

/**
 * Thrown when we couldn't find a code verifier on device
 */
public data class StytchMissingPKCEError(
    public override val exception: Throwable?
) : StytchSDKError(
    message = "The PKCE code challenge or code verifier is missing. Make sure this flow is completed on the same device on which it was started.",
    exception = exception,
)

/**
 * Thrown when we couldn't create a code challenge on device
 */
public data class StytchFailedToCreateCodeChallengeError(
    public override val exception: Throwable,
) : StytchSDKError(
    message = "Failed to generate a PKCE code challenge",
    exception = exception
)

/**
 * A type of error that can occur during deeplink handling
 */
public interface StytchDeeplinkError {
    public val message: String
}

/**
 * Thrown when we were passed an unknown deeplink token type
 */
public object StytchDeeplinkUnkownTokenTypeError : StytchDeeplinkError, StytchSDKError(
    message = "The deeplink received has an unknown token type.",
)

/**
 * Thrown when we attempted to handle a deeplink, but no token was found
 */
public object StytchDeeplinkMissingTokenError : StytchDeeplinkError, StytchSDKError(
    message = "The deeplink received has a missing token value.",
)

/**
 * Thrown when there is no current session persisted on device
 */
public object StytchNoCurrentSessionError : StytchSDKError(
    message = "There is no session currently available.",
)

/**
 * Thrown when there are no biometric registrations present on the device
 */
public object StytchNoBiometricsRegistrationError : StytchSDKError(
    message = "There is no biometric registration available. Authenticate with another method and add a new biometric registration first.",
)

/**
 * Thrown when the keystore is unavailable, but the developer did not pass allowFallbackToCleartext=true
 */
public object StytchKeystoreUnavailableError : StytchSDKError(
    message = "The Android keystore is unavailable on the device. Consider setting allowFallbackToCleartext to true.",
)

/**
 * Thrown when we are unable to retrieve a biometric key
 */
public data class StytchMissingPublicKeyError(
    override val exception: Throwable?
) : StytchSDKError(
    message = "Failed to retrieve the public key. Add a new biometric registration.",
    exception = exception,
)

/**
 * Thrown when a challenge signing (biometrics, passkeys) has failed
 */
public data class StytchChallengeSigningFailed(
    public override val exception: Throwable?
) : StytchSDKError(
    message = "Failed to sign the challenge with the key.",
    exception = exception,
)

/**
 * Thrown when the Google OneTap authorization credential was missing an id_token
 */
public object StytchMissingAuthorizationCredentialIdTokenError : StytchSDKError(
    message = "The authorization credential is missing an ID token.",
)

/**
 * Thrown when the Google OneTap client or nonce is missing
 */
public object StytchInvalidAuthorizationCredentialError : StytchSDKError(
    message = "The authorization credential is invalid.",
)

/**
 * Thrown when you attempt to perform a passkey flow on a device that does not support passkeys
 */
public object StytchPasskeysNotSupportedError : StytchSDKError(
    message = "Passkeys are not supported on this device.",
)

/**
 * Thrown when there was an error decrypting data from persistent storage
 */
public data class StytchFailedToDecryptDataError(
    public override val exception: Throwable?,
) : StytchSDKError(
    message = "Failed to decrypt user data",
    exception = exception,
)

/**
 * Thrown when biometric authentication has failed, providing a reason if available
 */
public data class StytchBiometricAuthenticationFailed(
    val reason: String,
) : StytchSDKError(
    message = "Biometric authentication failed"
)

/**
 * Thrown when we encounter an SSO error
 */
public data class StytchSSOError(
    public override val exception: Throwable? = null
) : StytchSDKError(
    message = "",
    exception = exception
)
