package com.stytch.sdk.b2b.sso

import android.app.Activity
import com.stytch.sdk.b2b.SSOAuthenticateResponse
import com.stytch.sdk.common.Constants

/**
 * Single-Sign On (SSO) refers to the ability for a user to use a single identity to authenticate and gain access to
 * multiple apps and service. In the case of B2B, it generally refers for the ability to use a workplace identity
 * managed by their company. Read our [blog post](https://stytch.com/blog/single-sign-on-sso/) for more information
 * about SSO.
 *
 * Stytch supports the following SSO protocols:
 * - SAML
 */
public interface SSO {
    /**
     * Data class used for wrapping parameters used in SSO start calls
     * @property context
     * @property ssoAuthRequestIdentifier
     * @property connectionId The ID of the SSO connection to use for the login flow.
     * @property loginRedirectUrl The URL Stytch redirects to after the SSO flow is completed for a Member that already
     * exists. This URL should be a route in your application which will run sso.authenticate (see below) and finish the
     * login. The URL must be configured as a Login URL in the Redirect URL page. If the field is not specified, the
     * default Login URL will be used.
     * @property signupRedirectUrl The URL Stytch redirects to after the SSO flow is completed for a Member that does
     * not yet exist. This URL should be a route in your application which will run sso.authenticate (see below) and
     * finish the login. The URL must be configured as a Sign Up URL in the Redirect URL page. If the field is not
     * specified, the default Sign Up URL will be used.
     */
    public data class StartParams(
        val context: Activity,
        val ssoAuthRequestIdentifier: Int,
        val connectionId: String,
        val loginRedirectUrl: String? = null,
        val signupRedirectUrl: String? = null,
    )

    /**
     * Start an SSO authentication flow
     * @param params required for beginning an SSO authentication flow
     */
    public fun start(params: StartParams)

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
