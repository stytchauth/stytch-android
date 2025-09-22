package com.stytch.sdk.b2b.sso

import android.app.Activity
import androidx.activity.ComponentActivity
import com.stytch.sdk.b2b.B2BSSODeleteConnectionResponse
import com.stytch.sdk.b2b.B2BSSODiscoveryConnectionResponse
import com.stytch.sdk.b2b.B2BSSOGetConnectionsResponse
import com.stytch.sdk.b2b.B2BSSOOIDCCreateConnectionResponse
import com.stytch.sdk.b2b.B2BSSOOIDCUpdateConnectionResponse
import com.stytch.sdk.b2b.B2BSSOSAMLCreateConnectionResponse
import com.stytch.sdk.b2b.B2BSSOSAMLDeleteVerificationCertificateResponse
import com.stytch.sdk.b2b.B2BSSOSAMLUpdateConnectionByURLResponse
import com.stytch.sdk.b2b.B2BSSOSAMLUpdateConnectionResponse
import com.stytch.sdk.b2b.SSOAuthenticateResponse
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.ConnectionRoleAssignment
import com.stytch.sdk.b2b.network.models.GroupRoleAssignment
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.common.network.models.Locale
import java.util.concurrent.CompletableFuture

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
    public fun setSSOReceiverActivity(activity: ComponentActivity?)

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
    @JacocoExcludeGenerated
    public data class StartParams
        @JvmOverloads
        constructor(
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
     * Data class used for wrapping parameters to start a third party OAuth flow and retrieve a token
     * @property loginRedirectUrl The url an existing user is redirected to after authenticating with the identity
     * provider. This should be a url that redirects back to your app. If this value is not passed, the default
     * login redirect URL set in the Stytch Dashboard is used. If you have not set a default login redirect URL,
     * an error is returned.
     * @property signupRedirectUrl The url a new user is redirected to after authenticating with the identity
     * provider.
     * This should be a url that redirects back to your app. If this value is not passed, the default sign-up
     * redirect URL set in the Stytch Dashboard is used. If you have not set a default sign-up redirect URL, an
     * error is returned.
     * @property customScopes Any additional scopes to be requested from the identity provider
     * @property providerParams An optional mapping of provider specific values to pass through to the OAuth provider.
     */
    @JacocoExcludeGenerated
    public data class GetTokenForProviderParams
        @JvmOverloads
        constructor(
            val connectionId: String,
            val loginRedirectUrl: String? = null,
            val signupRedirectUrl: String? = null,
        )

    public suspend fun getTokenForProvider(parameters: GetTokenForProviderParams): StytchResult<String>

    public fun getTokenForProvider(
        parameters: GetTokenForProviderParams,
        callback: (StytchResult<String>) -> Unit,
    )

    public fun getTokenForProviderCompletable(
        parameters: GetTokenForProviderParams,
    ): CompletableFuture<StytchResult<String>>

    /**
     * Data class used for wrapping parameters used in SSO Authenticate calls
     * @property ssoToken the SSO token to authenticate
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     * @property locale Used to determine which language to use when sending the user this delivery method.
     * Currently supported languages are English (`"en"`), Spanish (`"es"`), and Brazilian Portuguese (`"pt-br"`);
     * if no value is provided, the copy defaults to English.
     */
    @JacocoExcludeGenerated
    public data class AuthenticateParams
        @JvmOverloads
        constructor(
            val ssoToken: String,
            val sessionDurationMinutes: Int = StytchB2BClient.configurationManager.options.defaultSessionDuration,
            val locale: Locale? = null,
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
     * Authenticate a user given a token. This endpoint verifies that the user completed the SSO Authentication flow by
     * verifying that the token is valid and hasn't expired.
     * @param params required for making an authenticate call
     * @return [SSOAuthenticateResponse]
     */
    public fun authenticateCompletable(params: AuthenticateParams): CompletableFuture<SSOAuthenticateResponse>

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
     *  Get all SSO Connections owned by the organization.
     *  This method wraps the {@link https://stytch.com/docs/b2b/api/get-sso-connections Get SSO Connections} API
     *  endpoint.
     *  The caller must have permission to call this endpoint via the project's RBAC policy & their role assignments.
     *  @return [B2BSSOGetConnectionsResponse]
     */
    public fun getConnectionsCompletable(): CompletableFuture<B2BSSOGetConnectionsResponse>

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

    /**
     * Delete an existing SSO connection.
     * @param connectionId The ID of the connection to delete
     * @return [B2BSSODeleteConnectionResponse]
     */
    public fun deleteConnectionCompletable(connectionId: String): CompletableFuture<B2BSSODeleteConnectionResponse>

    /** The SSO Discovery method surfaces SSO connections for the end user. It will return all active SSO connections
     * for an end user or, if no active connections are found, it will return all SSO connections that the end user
     * could JIT provision into based on the provided email address.
     * @param emailAddress Email address to return SSO connection IDs for.
     * @return [B2BSSODiscoveryConnectionResponse]
     */
    public suspend fun discoverConnections(emailAddress: String): B2BSSODiscoveryConnectionResponse

    /** The SSO Discovery method surfaces SSO connections for the end user. It will return all active SSO connections
     * for an end user or, if no active connections are found, it will return all SSO connections that the end user
     * could JIT provision into based on the provided email address.
     * @param emailAddress Email address to return SSO connection IDs for.
     * @param callback A callback that receives a [B2BSSODiscoveryConnectionResponse]
     */
    public fun discoverConnections(
        emailAddress: String,
        callback: (B2BSSODiscoveryConnectionResponse) -> Unit,
    )

    /** The SSO Discovery method surfaces SSO connections for the end user. It will return all active SSO connections
     * for an end user or, if no active connections are found, it will return all SSO connections that the end user
     * could JIT provision into based on the provided email address.
     * @param emailAddress Email address to return SSO connection IDs for.
     * @return [B2BSSODiscoveryConnectionResponse]
     */
    public suspend fun discoverConnectionsCompletable(
        emailAddress: String,
    ): CompletableFuture<B2BSSODiscoveryConnectionResponse>

    /**
     * Exposes an instance of the [SAML] interface
     */
    public val saml: SAML

    /**
     * Exposes an instance of the [OIDC] interface
     */
    public val oidc: OIDC

    /**
     * The SAML interface provides methods for creating, updating, and deleting a SAML connection
     */

    public interface SAML {
        /**
         * Data class used for wrapping the parameters for a SAML creation request
         * @property displayName A human-readable display name for the connection.
         */
        @JacocoExcludeGenerated
        public data class CreateParameters
            @JvmOverloads
            constructor(
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

        /**
         * Create a new SAML Connection.
         * @param parameters The parameters required to create a new SAML connection
         * @return [B2BSSOSAMLCreateConnectionResponse]
         */
        public fun createConnectionCompletable(
            parameters: CreateParameters,
        ): CompletableFuture<B2BSSOSAMLCreateConnectionResponse>

        /**
         * Data class used for wrapping the parameters for a SAML update request
         * @property connectionId Globally unique UUID that identifies a specific SAML Connection.
         * @property idpEntityId A globally unique name for the IdP. This will be provided by the IdP.
         * @property displayName A human-readable display name for the connection.
         * @property attributeMapping An object that represents the attributes used to identify a Member. This object
         * will map the IdP-defined User attributes to Stytch-specific values. Required attributes: `email` and one of
         * `full_name` or `first_name` and `last_name`
         * @property idpSsoUrl The URL for which assertions for login requests will be sent. This will be provided by
         * the IdP.
         * @property x509Certificate A certificate that Stytch will use to verify the sign-in assertion sent by the IdP,
         * in {@link https://en.wikipedia.org/wiki/Privacy-Enhanced_Mail PEM} format.
         * @property samlConnectionImplicitRoleAssignment An array of implicit role assignments granted to members in
         * this organization who log in with this SAML connection.
         * @property samlGroupImplicitRoleAssignment An array of implicit role assignments granted to members in this
         * organization who log in with this SAML connection and belong to the specified group. Before adding any group
         * implicit role assignments, you must add a "groups" key to your SAML connection's attribute_mapping. Make sure
         * that your IdP is configured to correctly send the group information.
         */
        @JacocoExcludeGenerated
        public data class UpdateParameters
            @JvmOverloads
            constructor(
                val connectionId: String,
                val idpEntityId: String? = null,
                val displayName: String? = null,
                val attributeMapping: Map<String, String>? = null,
                val idpSsoUrl: String? = null,
                val x509Certificate: String? = null,
                val samlConnectionImplicitRoleAssignment: List<ConnectionRoleAssignment>? = null,
                val samlGroupImplicitRoleAssignment: List<GroupRoleAssignment>? = null,
            )

        /**
         * Update a SAML Connection.
         * @param parameters The parameters required to update SAML connection
         * @return [B2BSSOSAMLUpdateConnectionResponse]
         */
        public suspend fun updateConnection(parameters: UpdateParameters): B2BSSOSAMLUpdateConnectionResponse

        /**
         * Update a SAML Connection.
         * @param parameters The parameters required to update SAML connection
         * @param callback a callback that receives a [B2BSSOSAMLUpdateConnectionResponse]
         */
        public fun updateConnection(
            parameters: UpdateParameters,
            callback: (B2BSSOSAMLUpdateConnectionResponse) -> Unit,
        )

        /**
         * Update a SAML Connection.
         * @param parameters The parameters required to update SAML connection
         * @return [B2BSSOSAMLUpdateConnectionResponse]
         */
        public fun updateConnectionCompletable(
            parameters: UpdateParameters,
        ): CompletableFuture<B2BSSOSAMLUpdateConnectionResponse>

        /**
         * Data class used for wrapping the parameters for a SAML update by URL request
         * @property connectionId Globally unique UUID that identifies a specific SAML Connection.
         * @property metadataUrl A URL that points to the IdP metadata. This will be provided by the IdP.
         */
        @JacocoExcludeGenerated
        public data class UpdateByURLParameters(
            val connectionId: String,
            val metadataUrl: String,
        )

        /**
         * Update a SAML Connection by URL.
         * @param parameters The parameters required to update a SAML connection by URL
         * @return [B2BSSOSAMLUpdateConnectionByURLResponse]
         */
        public suspend fun updateConnectionByUrl(
            parameters: UpdateByURLParameters,
        ): B2BSSOSAMLUpdateConnectionByURLResponse

        /**
         * Update a SAML Connection by URL.
         * @param parameters The parameters required to update a SAML connection by URL
         * @param callback a callback that receives a [B2BSSOSAMLUpdateConnectionByURLResponse]
         */
        public fun updateConnectionByUrl(
            parameters: UpdateByURLParameters,
            callback: (B2BSSOSAMLUpdateConnectionByURLResponse) -> Unit,
        )

        /**
         * Update a SAML Connection by URL.
         * @param parameters The parameters required to update a SAML connection by URL
         * @return [B2BSSOSAMLUpdateConnectionByURLResponse]
         */
        public fun updateConnectionByUrlCompletable(
            parameters: UpdateByURLParameters,
        ): CompletableFuture<B2BSSOSAMLUpdateConnectionByURLResponse>

        /**
         * Data class used for wrapping the parameters to delete a SAML verification certificate
         * @property connectionId Globally unique UUID that identifies a specific SAML Connection.
         * @property certificateId The ID of the certificate to be deleted.
         */
        @JacocoExcludeGenerated
        public data class DeleteVerificationCertificateParameters(
            val connectionId: String,
            val certificateId: String,
        )

        /**
         * Delete a SAML verification certificate.
         * You may need to do this when rotating certificates from your IdP, since Stytch allows a maximum of 5
         * certificates per connection. There must always be at least one certificate per active connection.
         * @param parameters The parameters required to delete a SAML verification certificate.
         * @return [B2BSSOSAMLDeleteVerificationCertificateResponse]
         */
        public suspend fun deleteVerificationCertificate(
            parameters: DeleteVerificationCertificateParameters,
        ): B2BSSOSAMLDeleteVerificationCertificateResponse

        /**
         * Delete a SAML verification certificate.
         * You may need to do this when rotating certificates from your IdP, since Stytch allows a maximum of 5
         * certificates per connection. There must always be at least one certificate per active connection.
         * @param parameters The parameters required to delete a SAML verification certificate.
         * @param callback a callback that receives a [B2BSSOSAMLDeleteVerificationCertificateResponse]
         */
        public fun deleteVerificationCertificate(
            parameters: DeleteVerificationCertificateParameters,
            callback: (B2BSSOSAMLDeleteVerificationCertificateResponse) -> Unit,
        )

        /**
         * Delete a SAML verification certificate.
         * You may need to do this when rotating certificates from your IdP, since Stytch allows a maximum of 5
         * certificates per connection. There must always be at least one certificate per active connection.
         * @param parameters The parameters required to delete a SAML verification certificate.
         * @return [B2BSSOSAMLDeleteVerificationCertificateResponse]
         */
        public fun deleteVerificationCertificateCompletable(
            parameters: DeleteVerificationCertificateParameters,
        ): CompletableFuture<B2BSSOSAMLDeleteVerificationCertificateResponse>
    }

    /**
     * The OIDC interface provides methods for creating and updating an OIDC connection
     */

    public interface OIDC {
        /**
         * Data class used for wrapping the parameters for an OIDC creation request
         * @property displayName A human-readable display name for the connection.
         */
        @JacocoExcludeGenerated
        public data class CreateParameters
            @JvmOverloads
            constructor(
                val displayName: String? = null,
            )

        /**
         * Create a new OIDC Connection.
         * @param parameters The parameters required to create a new OIDC connection
         * @return [B2BSSOOIDCCreateConnectionResponse]
         */
        public suspend fun createConnection(parameters: CreateParameters): B2BSSOOIDCCreateConnectionResponse

        /**
         * Create a new OIDC Connection.
         * @param parameters The parameters required to create a new OIDC connection
         * @param callback a callback that receives a [B2BSSOOIDCCreateConnectionResponse]
         */
        public fun createConnection(
            parameters: CreateParameters,
            callback: (B2BSSOOIDCCreateConnectionResponse) -> Unit,
        )

        /**
         * Create a new OIDC Connection.
         * @param parameters The parameters required to create a new OIDC connection
         * @return [B2BSSOOIDCCreateConnectionResponse]
         */
        public fun createConnectionCompletable(
            parameters: CreateParameters,
        ): CompletableFuture<B2BSSOOIDCCreateConnectionResponse>

        /**
         * Data class used for wrapping the parameters for an OIDC update request
         * @property connectionId Globally unique UUID that identifies a specific OIDC Connection.
         * @property displayName  A human-readable display name for the connection.
         * @property issuer A case-sensitive `https://` URL that uniquely identifies the IdP. This will be provided by
         * the IdP.
         * @property clientId The OAuth2.0 client ID used to authenticate login attempts. This will be provided by the
         * IdP.
         * @property clientSecret The secret belonging to the OAuth2.0 client used to authenticate login attempts. This
         * will be provided by the IdP.
         * @property authorizationUrl The location of the URL that starts an OAuth login at the IdP. This will be
         * provided by the IdP.
         * @property tokenUrl The location of the URL that issues OAuth2.0 access tokens and OIDC ID tokens. This will
         * be provided by the IdP.
         * @property userInfoUrl The location of the IDP's UserInfo Endpoint. This will be provided by the IdP.
         * @property jwksUrl The location of the IdP's JSON Web Key Set, used to verify credentials issued by the IdP.
         * This will be provided by the IdP.
         */
        @JacocoExcludeGenerated
        public data class UpdateParameters
            @JvmOverloads
            constructor(
                val connectionId: String,
                val displayName: String? = null,
                val issuer: String? = null,
                val clientId: String? = null,
                val clientSecret: String? = null,
                val authorizationUrl: String? = null,
                val tokenUrl: String? = null,
                val userInfoUrl: String? = null,
                val jwksUrl: String? = null,
            )

        /**
         * Update an OIDC Connection.
         * @param parameters The parameters required to update an OIDC connection
         * @return [B2BSSOOIDCUpdateConnectionResponse]
         */
        public suspend fun updateConnection(parameters: UpdateParameters): B2BSSOOIDCUpdateConnectionResponse

        /**
         * Update an OIDC Connection.
         * @param parameters The parameters required to update an OIDC connection
         * @param callback a callback that receives a [B2BSSOOIDCUpdateConnectionResponse]
         */
        public fun updateConnection(
            parameters: UpdateParameters,
            callback: (B2BSSOOIDCUpdateConnectionResponse) -> Unit,
        )

        /**
         * Update an OIDC Connection.
         * @param parameters The parameters required to update an OIDC connection
         * @return [B2BSSOOIDCUpdateConnectionResponse]
         */
        public fun updateConnectionCompletable(
            parameters: UpdateParameters,
        ): CompletableFuture<B2BSSOOIDCUpdateConnectionResponse>
    }
}
