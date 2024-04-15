package com.stytch.sdk.b2b.sso

import android.app.Activity
import com.stytch.sdk.b2b.B2BSSODeleteConnectionResponse
import com.stytch.sdk.b2b.B2BSSOGetConnectionsResponse
import com.stytch.sdk.b2b.B2BSSOSAMLCreateConnectionResponse
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
    public fun authenticate(
        params: AuthenticateParams,
        callback: (SSOAuthenticateResponse) -> Unit,
    )

    /**
     *  Get all SSO Connections owned by the organization.
     *  This method wraps the {@link https://stytch.com/docs/b2b/api/get-sso-connections Get SSO Connections} API
     *  endpoint.
     *  The caller must have permission to call this endpoint via the project's RBAC policy & their role assignments.
     *  @return [B2BSSOGetConnectionsResponse]
     */
    public suspend fun getConnections(): B2BSSOGetConnectionsResponse

    /**
     *  Get all SSO Connections owned by the organization.
     *  This method wraps the {@link https://stytch.com/docs/b2b/api/get-sso-connections Get SSO Connections} API
     *  endpoint.
     *  The caller must have permission to call this endpoint via the project's RBAC policy & their role assignments.
     *  @param callback a callback that receives a [B2BSSOGetConnectionsResponse]
     */
    public fun getConnections(callback: (B2BSSOGetConnectionsResponse) -> Unit)

    /**
     * Delete an existing SSO connection.
     * @param connectionId The ID of the connection to delete
     * @return [B2BSSODeleteConnectionResponse]
     */
    public suspend fun deleteConnection(connectionId: String): B2BSSODeleteConnectionResponse

    /**
     * Delete an existing SSO connection.
     * @param connectionId The ID of the connection to delete
     * @param callback a callback that receives a [B2BSSODeleteConnectionResponse]
     */
    public fun deleteConnection(
        connectionId: String,
        callback: (B2BSSODeleteConnectionResponse) -> Unit,
    )

    public val saml: SAML

    public interface SAML {
        /**
         * Data class used for wrapping the parameters for an SSO SAML creation request
         * @property displayName A human-readable display name for the connection.
         */
        public data class CreateParameters(
            val displayName: String? = null,
        )

        /**
         * Create a new SAML Connection.
         * @param parameters The parameters required to create a new SAML connection
         * @return [B2BSSOSAMLCreateConnectionResponse]
         */
        public suspend fun createConnection(parameters: CreateParameters): B2BSSOSAMLCreateConnectionResponse

        /**
         * Create a new SAML Connection.
         * @param parameters The parameters required to create a new SAML connection
         * @param callback a callback that receives a [B2BSSOSAMLCreateConnectionResponse]
         */
        public fun createConnection(
            parameters: CreateParameters,
            callback: (B2BSSOSAMLCreateConnectionResponse) -> Unit,
        )
    }
}
