@file:Suppress("MaxLineLength", "ktlint:standard:max-line-length")

package com.stytch.sdk.common.errors

import com.stytch.sdk.common.annotations.JacocoExcludeGenerated

/**
 * A base class representing SDK specific errors or exceptions that may occur. This class should not be used directly,
 * rather we should be creating implementations for each of the known/expected errors we return.
 * @property exception an optional [Throwable] that caused this error to occur
 */
@JacocoExcludeGenerated
public sealed class StytchSDKError(
    message: String,
    public open val exception: Throwable? = null,
) : StytchError(message = message)

/**
 * Thrown when you try to use the SDK before it has been configured
 * @property clientName the client type that must be configured (either `StytchClient` or `StytchB2BClient`)
 */
@JacocoExcludeGenerated
public data class StytchSDKNotConfiguredError(
    val clientName: String,
) : StytchSDKError(
        message = "$clientName not configured. You must call `$clientName.configure(...)` before using any functionality of the SDK.",
    )

/**
 * Thrown when all else fails. We should audit our usage of this and see if it makes sense to create classes for these
 * @property exception an optional [Throwable] that caused this error to occur
 * @property message a string describing what went wrong
 */
@JacocoExcludeGenerated
public data class StytchInternalError(
    public override val exception: Throwable? = null,
    public override val message: String = "An internal error has occurred. Please contact Stytch if this occurs.",
) : StytchSDKError(
        message = message,
        exception = exception,
    )

/**
 * Thrown when we couldn't find a code verifier on device
 * @property exception an optional [Throwable] that caused this error to occur
 */
@JacocoExcludeGenerated
public data class StytchMissingPKCEError(
    public override val exception: Throwable?,
) : StytchSDKError(
        message = "The PKCE code challenge or code verifier is missing. Make sure this flow is completed on the same device on which it was started.",
        exception = exception,
    )

/**
 * Thrown when we couldn't create a code challenge on device
 * @property exception the [Throwable] that caused this error to occur
 */
@JacocoExcludeGenerated
public data class StytchFailedToCreateCodeChallengeError(
    public override val exception: Throwable,
) : StytchSDKError(
        message = "Failed to generate a PKCE code challenge",
        exception = exception,
    )

/**
 * A type of error that can occur during deeplink handling
 * @property message a string describing what went wrong when handling the deeplink
 */

public interface StytchDeeplinkError {
    public val message: String
}

/**
 * Thrown when we were passed an unknown deeplink token type
 */

public class StytchDeeplinkUnkownTokenTypeError :
    StytchSDKError(
        message = "The deeplink received has an unknown token type.",
    ),
    StytchDeeplinkError

/**
 * Thrown when we attempted to handle a deeplink, but no token was found
 */

public class StytchDeeplinkMissingTokenError :
    StytchSDKError(
        message = "The deeplink received has a missing token value.",
    ),
    StytchDeeplinkError

/**
 * Thrown when there is no current session persisted on device
 */

public class StytchNoCurrentSessionError :
    StytchSDKError(
        message = "There is no session currently available.",
    )

/**
 * Thrown when there are no biometric registrations present on the device
 */

public class StytchNoBiometricsRegistrationError :
    StytchSDKError(
        message = "There is no biometric registration available. Authenticate with another method and add a new biometric registration first.",
    )

/**
 * Thrown when the keystore is unavailable, but the developer did not pass allowFallbackToCleartext=true
 */

public class StytchKeystoreUnavailableError :
    StytchSDKError(
        message = "The Android keystore is unavailable on the device. Consider setting allowFallbackToCleartext to true.",
    )

/**
 * Thrown when we are unable to retrieve a biometric key
 * @property exception an optional [Throwable] that caused this error to occur
 */
@JacocoExcludeGenerated
public data class StytchMissingPublicKeyError(
    override val exception: Throwable?,
) : StytchSDKError(
        message = "Failed to retrieve the public key. Add a new biometric registration.",
        exception = exception,
    )

/**
 * Thrown when a challenge signing (biometrics, passkeys) has failed
 * @property exception an optional [Throwable] that caused this error to occur
 */

public data class StytchChallengeSigningFailed(
    public override val exception: Throwable?,
) : StytchSDKError(
        message = "Failed to sign the challenge with the key.",
        exception = exception,
    )

/**
 * Thrown when the Google OneTap authorization credential was missing an id_token
 */

public class StytchMissingAuthorizationCredentialIdTokenError :
    StytchSDKError(
        message = "The authorization credential is missing an ID token.",
    )

/**
 * Thrown when the Google OneTap client or nonce is missing
 */

public class StytchInvalidAuthorizationCredentialError :
    StytchSDKError(
        message = "The authorization credential is invalid.",
    )

/**
 * Thrown when you attempt to perform a passkey flow on a device that does not support passkeys
 */

public class StytchPasskeysNotSupportedError :
    StytchSDKError(
        message = "Passkeys are not supported on this device.",
    )

/**
 * Thrown when there was an error decrypting data from persistent storage
 * @property exception an optional [Throwable] that caused this error to occur
 */
public data class StytchFailedToDecryptDataError(
    public override val exception: Throwable?,
) : StytchSDKError(
        message = "Failed to decrypt user data",
        exception = exception,
    )

/**
 * Thrown when biometric authentication has failed, providing a reason if available
 * @property reason a string explaining why biometric authentication has failed
 */
public data class StytchBiometricAuthenticationFailed(
    val reason: String,
) : StytchSDKError(
        message = "Biometric authentication failed",
    )

/**
 * Thrown when we encounter an SSO error
 * @property exception an optional [Throwable] that caused this error to occur
 */
public data class StytchSSOError(
    public override val exception: Throwable? = null,
) : StytchSDKError(
        message = "",
        exception = exception,
    )

/**
 * Thrown when we receive an unexpected token type from Sign In With Google
 * @property credentialType the type of credential that Google Credential Manager returned
 */
public data class UnexpectedCredentialType(
    val credentialType: String,
) : StytchSDKError(
        message = "Unexpected type of credential: $credentialType",
    )

@JacocoExcludeGenerated public data object NoBrowserFound : StytchSDKError(
    "No supported browser was found on this device",
)

@JacocoExcludeGenerated public data object NoURIFound : StytchSDKError("No OAuth URI could be found in the bundle")

@JacocoExcludeGenerated public data object UserCanceled : StytchSDKError("The user canceled the OAuth flow")

@JacocoExcludeGenerated public data object NoActivityProvided : StytchSDKError(
    "You must supply a receiver activity before calling this method",
)

@JacocoExcludeGenerated public data object UnknownOAuthOrSSOError : StytchSDKError(
    "The OAuth or SSO flow completed unexpectedly",
)

@JacocoExcludeGenerated public data object BiometricsAlreadyEnrolledError : StytchSDKError(
    "There is already a biometric factor enrolled on this device. Fully authenticate with all factors and remove the existing registration before attempting to register again.",
)
