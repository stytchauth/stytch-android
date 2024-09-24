package com.stytch.sdk.b2b.oauth

import android.app.Activity
import com.stytch.sdk.b2b.OAuthAuthenticateResponse
import com.stytch.sdk.b2b.OAuthDiscoveryAuthenticateResponse
import com.stytch.sdk.common.DEFAULT_SESSION_TIME_MINUTES
import com.stytch.sdk.common.network.models.Locale
import java.util.concurrent.CompletableFuture

/**
 * The OAuth interface provides methods for authenticating a user, via the supported OAuth providers, provided you have
 * configured them within your Stytch Dashboard.
 */
public interface OAuth {
    /**
     * An interface describing the methods and parameters available for starting an OAuth or OAuth discovery flow
     * for a specific provider
     */
    public interface Provider {
        /**
         * A data class wrapping the parameters necessary to start an OAuth flow for a specific provider
         * @property context the calling Activity for launching the browser
         * @property oAuthRequestIdentifier is an ID for the return intent from the OAuth flow
         * @property organizationId The id of the organization the member belongs to.
         * @property organizationSlug The slug of the organization the member belongs to
         * @property loginRedirectUrl The url an existing user is redirected to after authenticating with the identity
         * provider. This should be a url that redirects back to your app. If this value is not passed, the default
         * login redirect URL set in the Stytch Dashboard is used. If you have not set a default login redirect URL,
         * an error is returned.
         * @property signupRedirectUrl The url a new user is redirected to after authenticating with the identity
         * provider.
         * This should be a url that redirects back to your app. If this value is not passed, the default sign-up
         * redirect URL set in the Stytch Dashboard is used. If you have not set a default sign-up redirect URL, an
         * error is returned.
         * @property customScopes Any additional scopes to be requested from the identity provider.
         * @property providerParams An optional mapping of provider specific values to pass through to the OAuth
         * provider
         */
        public data class StartParameters
            @JvmOverloads
            constructor(
                val context: Activity,
                val oAuthRequestIdentifier: Int,
                val organizationId: String? = null,
                val organizationSlug: String? = null,
                val loginRedirectUrl: String? = null,
                val signupRedirectUrl: String? = null,
                val customScopes: List<String>? = null,
                val providerParams: Map<String, String>? = null,
            )

        /**
         * Start a provider OAuth flow
         * @param parameters the parameters necessary to start the flow
         */
        public fun start(parameters: StartParameters)

        /**
         * Exposes an instance of the [ProviderDiscovery] interface
         */
        public val discovery: ProviderDiscovery
    }

    /**
     * An interface describing the methods and parameters available for starting an OAuth discovery flow
     * for a specific provider
     */
    public interface ProviderDiscovery {
        /**
         * A data class wrapping the parameters necessary to start an OAuth flow for a specific provider
         * @property context the calling Activity for launching the browser
         * @property oAuthRequestIdentifier is an ID for the return intent from the OAuth flow
         * @property discoveryRedirectUrl The URL that Stytch redirects to after the OAuth flow is completed for the
         * member to perform discovery actions. This URL should be an endpoint in the backend server that verifies the
         * request by querying Stytch's /oauth/discovery/authenticate endpoint and finishes the login. The URL should be
         * configured as a Discovery URL in the Stytch Dashboard's Redirect URL page. If the field is not specified,
         * the default in the Dashboard is used.
         * @property customScopes Any additional scopes to be requested from the identity provider.
         * @property providerParams An optional mapping of provider specific values to pass through to the OAuth
         * provider
         */
        public data class DiscoveryStartParameters
            @JvmOverloads
            constructor(
                val context: Activity,
                val oAuthRequestIdentifier: Int,
                val discoveryRedirectUrl: String? = null,
                val customScopes: List<String>? = null,
                val providerParams: Map<String, String>? = null,
            )

        /**
         * Start a provider OAuth Discovery flow
         * @param parameters the parameters necessary to start the flow
         */
        public fun start(parameters: DiscoveryStartParameters)
    }

    /**
     * An interface describing the parameters and methods available for authenticating an OAuth Discovery flow
     */
    public interface Discovery {
        /**
         * A data class wrapping the parameters necessary to authenticate an OAuth Discovery flow
         * @property discoveryOauthToken The oauth token used to finish the discovery flow
         */
        public data class DiscoveryAuthenticateParameters(
            val discoveryOauthToken: String,
        )

        /**
         * Authenticate an OAuth Discovery flow
         * @param parameters required to authenticate the OAuth Discovery flow
         * @return [OAuthDiscoveryAuthenticateResponse]
         */
        public suspend fun authenticate(parameters: DiscoveryAuthenticateParameters): OAuthDiscoveryAuthenticateResponse

        /**
         * Authenticate an OAuth Discovery flow
         * @param parameters required to authenticate the OAuth Discovery flow
         * @param callback a callback that receives an [OAuthDiscoveryAuthenticateResponse]
         */
        public fun authenticate(
            parameters: DiscoveryAuthenticateParameters,
            callback: (OAuthDiscoveryAuthenticateResponse) -> Unit,
        )

        /**
         * Authenticate an OAuth Discovery flow
         * @param parameters required to authenticate the OAuth Discovery flow
         * @return [OAuthDiscoveryAuthenticateResponse]
         */
        public fun authenticateCompletable(
            parameters: DiscoveryAuthenticateParameters,
        ): CompletableFuture<OAuthDiscoveryAuthenticateResponse>
    }

    /**
     * Exposes an instance of the Google OAuth implementation
     */
    public val google: Provider

    /**
     * Exposes an instance of the Microsoft OAuth implementation
     */
    public val microsoft: Provider

    /**
     * Exposes an instance of the Discovery OAuth implementation
     */
    public val discovery: Discovery

    /**
     * A data class wrapping the parameters necessary to authenticate an OAuth flow
     * @property oauthToken The oauth token used to finish the discovery flow
     * @property locale The locale will be used if an OTP code is sent to the member's phone number as part of a
     * secondary authentication requirement.
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class AuthenticateParameters
        @JvmOverloads
        constructor(
            val oauthToken: String,
            val locale: Locale? = null,
            val sessionDurationMinutes: Int = DEFAULT_SESSION_TIME_MINUTES,
        )

    /**
     * Authenticate an OAuth flow
     * @param parameters required to authenticate the OAuth flow
     * @return [OAuthAuthenticateResponse]
     */
    public suspend fun authenticate(parameters: AuthenticateParameters): OAuthAuthenticateResponse

    /**
     * Authenticate an OAuth flow
     * @param parameters required to authenticate the OAuth flow
     * @param callback a callback that receives an [OAuthAuthenticateResponse]
     */
    public fun authenticate(
        parameters: AuthenticateParameters,
        callback: (OAuthAuthenticateResponse) -> Unit,
    )

    /**
     * Authenticate an OAuth flow
     * @param parameters required to authenticate the OAuth flow
     * @return [OAuthAuthenticateResponse]
     */
    public fun authenticateCompletable(
        parameters: AuthenticateParameters,
    ): CompletableFuture<OAuthAuthenticateResponse>
}
