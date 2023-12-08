@file:Suppress("MaxLineLength")
/* ktlint-disable max-line-length */

package com.stytch.sdk.common.errors

/**
 * A base class representing SDK specific errors or exceptions that may occur. This class should not be used directly,
 * rather we should be creating implementations for each of the known/expected errors we return.
 */
public sealed class StytchSDKError(
    name: String,
    description: String,
    url: String? = null,
    exception: Throwable? = null,
) : StytchError(
    name = name,
    description = description,
    url = url,
    exception = exception,
)

/**
 * Thrown when you try to use the SDK before it has been configured
 */
public data class StytchSDKNotConfiguredError(val clientName: String) : StytchSDKError(
    name = "sdk_not_configured",
    description = "$clientName not configured. You must call `$clientName.configure(...)` before using any functionality of the SDK."
)

/**
 * Thrown when all else fails. We should audit our usage of this and see if it makes sense to create classes for these
 */
public data class StytchInternalError(
    public override val exception: Throwable? = null,
    public override val description: String = "An internal error has occurred. Please contact Stytch if this occurs.",
) : StytchSDKError(
    name = "stytch_internal_error",
    description = description,
    exception = exception,
)

/**
 * Thrown when we couldn't find a code verifier on device
 */
public data class StytchMissingPKCEError(
    public override val exception: Throwable?
) : StytchSDKError(
    name = "missing_pkce",
    description = "The PKCE code challenge or code verifier is missing. Make sure this flow is completed on the same device on which it was started.",
    exception = exception,
)

/**
 * Thrown when we couldn't create a code challenge on device
 */
public data class StytchFailedToCreateCodeChallengeError(
    public override val exception: Throwable,
) : StytchSDKError(
    name = "failed_code_challenge",
    description = "Failed to generate a PKCE code challenge",
    exception = exception
)

/**
 * A type of error that can occur during deeplink handling
 */
public interface StytchDeeplinkError {
    public val name: String
    public val description: String
}

/**
 * Thrown when we were passed an unknown deeplink token type
 */
public object StytchDeeplinkUnkownTokenTypeError : StytchDeeplinkError, StytchSDKError(
    name = "deeplink_unknown_token_type",
    description = "The deeplink received has an unknown token type.",
)

/**
 * Thrown when we attempted to handle a deeplink, but no token was found
 */
public object StytchDeeplinkMissingTokenError : StytchDeeplinkError, StytchSDKError(
    name = "deeplink_missing_token",
    description = "The deeplink received has a missing token value.",
)

/**
 * Thrown when there is no current session persisted on device
 */
public object StytchNoCurrentSessionError : StytchSDKError(
    name = "no_current_session",
    description = "There is no session currently available.",
)

/**
 * Thrown when there are no biometric registrations present on the device
 */
public object StytchNoBiometricsRegistrationError : StytchSDKError(
    name = "no_biometrics_registration",
    description = "There is no biometric registration available. Authenticate with another method and add a new biometric registration first.",
)

/* These Biometric errors are not thrown on Android
public object StytchBiometricsUnavailableError : StytchSDKError(
    name = "biometrics_unavailable",
    description = "Biometrics is not available on the device.",
)

public object StytchKeyInvalidatedError : StytchSDKError(
    name = "key_invalidated",
    description = "The biometrics enrollment on the device has changed.",
)

public object StytchNoBiometricsEnrolledError : StytchSDKError(
    name = "no_biometrics_enrolled",
    description = "There is no biometric factor enrolled on the device. Add a biometric factor in the device settings.",
)

public object StytchUserCancellationError : StytchSDKError(
    name = "user_cancellation",
    description = "The user canceled the prompt. Ask the user to try again.",
)

public object StytchUserLockedOutError : StytchSDKError(
    name = "user_locked_out",
    description = "The user has been locked out due to too many failed attempts. Ask the user to try again later.",
)

public object StytchDeviceCredentialsNotAllowedError : StytchSDKError(
    name = "device_credentials_not_allowed",
    description = "The device credentials allowment is mismatched. Change the allowDeviceCredentials parameter to be the same in both the register and authenticate methods.",
)
*/

/**
 * Thrown when the keystore is unavailable, but the developer did not pass allowFallbackToCleartext=true
 */
public object StytchKeystoreUnavailableError : StytchSDKError(
    name = "keystore_unavailable",
    description = "The Android keystore is unavailable on the device. Consider setting allowFallbackToCleartext to true.",
)

/**
 * Thrown when we are unable to retrieve a biometric key
 */
public data class StytchMissingPublicKeyError(
    override val exception: Throwable?
) : StytchSDKError(
    name = "missing_public_key",
    description = "Failed to retrieve the public key. Add a new biometric registration.",
    exception = exception,
)

/**
 * Thrown when a challenge signing (biometrics, passkeys) has failed
 */
public data class StytchChallengeSigningFailed(
    public override val exception: Throwable?
) : StytchSDKError(
    name = "challenge_signing_failed",
    description = "Failed to sign the challenge with the key.",
    exception = exception,
)

/**
 * Thrown when the Google OneTap authorization credential was missing an id_token
 */
public object StytchMissingAuthorizationCredentialIdTokenError : StytchSDKError(
    name = "missing_authorization_credential_id_token",
    description = "The authorization credential is missing an ID token.",
)

/**
 * Thrown when the Google OneTap client or nonce is missing
 */
public object StytchInvalidAuthorizationCredentialError : StytchSDKError(
    name = "invalid_authorization_credential",
    description = "The authorization credential is invalid.",
)

/**
 * Thrown when you attempt to perform a passkey flow on a device that does not support passkeys
 */
public object StytchPasskeysNotSupportedError : StytchSDKError(
    name = "passkeys_unsupported",
    description = "Passkeys are not supported on this device.",
)

/**
 * Thrown when there was an error decrypting data from persistent storage
 */
public data class StytchFailedToDecryptDataError(
    public override val exception: Throwable?,
) : StytchSDKError(
    name = "failed_to_decrypt_data",
    description = "Failed to decrypt user data",
    exception = exception,
)

/**
 * Thrown when biometric authentication has failed, providing a reason if available
 */
public data class StytchBiometricAuthenticationFailed(
    val reason: String,
) : StytchSDKError(
    name = "biometrics_failed",
    description = "Biometric authentication failed"
)
