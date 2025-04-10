package com.stytch.sdk.common.sso

import com.stytch.sdk.common.annotations.JacocoExcludeGenerated

/**
 * Represents one of three potential errors encountered during the Third party OAuth or SSO authentication flow.
 * @property message A friendly string indicating what went wrong during the OAuth or SSO authentication flow.
 */
@JacocoExcludeGenerated
public sealed class SSOError(
    override val message: String,
) : Exception(message) {
    /**
     * Indicates that no suitable browser was found on this device, and OAuth authentication cannot proceed.
     */

    @JacocoExcludeGenerated public data object NoBrowserFound : SSOError(
        "No supported browser was found on this device",
    )

    /**
     * Indicates that no URI was found in the activity state, and OAuth authentication cannot proceed.
     */

    @JacocoExcludeGenerated public data object NoURIFound : SSOError("No OAuth URI could be found in the bundle")

    /**
     * Indicates that the user canceled the OAuth flow. This is safe to ignore.
     */

    @JacocoExcludeGenerated public data object UserCanceled : SSOError("The user canceled the OAuth flow")

    /**
     * A helper object with properties that can help you interact with errors of this type
     */
    public companion object {
        /**
         * A string identifying the class of this Exception for serializing/deserializing the error within an Intent
         */

        public const val SSO_EXCEPTION: String = "com.stytch.sdk.common.sso.SSOError"
    }
}
