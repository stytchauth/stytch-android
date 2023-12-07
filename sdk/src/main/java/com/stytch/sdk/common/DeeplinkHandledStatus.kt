package com.stytch.sdk.common

import com.stytch.sdk.common.errors.StytchError

/**
 * A class representing the three states of a deeplink handled by StytchClient.handle() / StytchB2BClient.handle()
 */
public sealed interface DeeplinkHandledStatus {
    /**
     * Indicates that a deeplink was successfully parsed from the deeplink.
     * @property response A [DeeplinkResponse] representing either the authenticated response or an error.
     */
    public data class Handled(val response: DeeplinkResponse) : DeeplinkHandledStatus

    /**
     * Indicates that a deeplink was not handled by the Stytch client, either because a token could not be parsed from
     * it or it is not a known or supported authentication method.
     *
     * This could happen if you pass a non-Stytch deeplink intent to the StytchClient.handle() method, or are trying to
     * use a Consumer/B2B token in the wrong SDK.
     *
     * @property reason An error explaining why the deeplink was not handled
     */
    public data class NotHandled(val reason: StytchError) : DeeplinkHandledStatus

    /**
     * Indicates that this was a supported Stytch deeplink, but there is something more your application needs to do
     * with the extracted token.
     *
     * Currently, this is only used for handling PasswordResetStart deeplinks, in which you must collect a new password
     * from a user and use this returned token to complete the PasswordReset flow.
     *
     * @property type The [TokenType] that was extracted from the deeplink
     * @property token The token that was extracted from the deeplink
     */
    public data class ManualHandlingRequired(val type: TokenType, val token: String) : DeeplinkHandledStatus
}
