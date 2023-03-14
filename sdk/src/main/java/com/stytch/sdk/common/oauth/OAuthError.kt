package com.stytch.sdk.common.oauth

/**
 * Represents one of three potential errors encountered during the Third party OAuth authentication flow.
 * @property message A friendly string indicating what went wrong during the OAuth authentication flow.
 */
public sealed class OAuthError(override val message: String) : Exception(message) {
    /**
     * Indicates that no suitable browser was found on this device, and OAuth authentication cannot proceed.
     */
    public object NoBrowserFound : OAuthError("No supported browser was found on this device")

    /**
     * Indicates that no URI was found in the activity state, and OAuth authentication cannot proceed.
     */
    public object NoURIFound : OAuthError("No OAuth URI could be found in the bundle")

    /**
     * Indicates that the user canceled the OAuth flow. This is safe to ignore.
     */
    public object UserCanceled : OAuthError("The user canceled the OAuth flow")

    public companion object {
        /**
         * A string identifying the class of this Exception for serializing/deserializing the error within an Intent
         */
        public const val OAUTH_EXCEPTION: String = "com.stytch.sdk.common.oauth.OAuthError"
    }
}
