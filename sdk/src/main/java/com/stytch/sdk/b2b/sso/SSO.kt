package com.stytch.sdk.b2b.sso

import com.stytch.sdk.b2b.SSOAuthenticateResponse
import com.stytch.sdk.common.Constants

/**
 * Single-Sign On (SSO) refers to the ability for a user to use a single identity to authenticate and gain access to
 * multiple apps and service. In the case of B2B, it generally refers for the ability to use a workplace identity
 * managed by their company. Read our [blog post](https://stytch.com/blog/single-sign-on-sso/) for more information
 * about SSO.
 *
 * Stytch supports the following SSO protocols:
 * - OIDC
 * - SAML
 */
public interface SSO {
    /**
     * Data class used for wrapping parameters used in SSO Authenticate calls
     * @property ssoToken the SSO token to authenticate
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class AuthenticateParams(
        val ssoToken: String,
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
    )

    /**
     * Authenticate a user given a token. This endpoint verifies that the user completed the SSO Authentication flow by
     * verifying that the token is valid and hasn't expired.
     * @param params required for making an authenticate call
     * @return [SSOAuthenticateResponse]
     */
    public suspend fun authenticate(params: AuthenticateParams): SSOAuthenticateResponse

    /**
     * Authenticate a user given a token. This endpoint verifies that the user completed the SSO Authentication flow by
     * verifying that the token is valid and hasn't expired.
     * @param params required for making an authenticate call
     * @param callback a callback that receives a [SSOAuthenticateResponse]
     */
    public fun authenticate(params: AuthenticateParams, callback: (SSOAuthenticateResponse) -> Unit)
}
