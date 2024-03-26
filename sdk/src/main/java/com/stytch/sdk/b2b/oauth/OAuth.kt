package com.stytch.sdk.b2b.oauth

import android.app.Activity
import com.stytch.sdk.b2b.OAuthAuthenticateResponse
import com.stytch.sdk.b2b.OAuthDiscoveryAuthenticateResponse
import com.stytch.sdk.common.Constants

/**
 * The OAuth interface provides methods for authenticating a user, via the supported OAuth providers, provided you have
 * configured them within your Stytch Dashboard.
 */
public interface OAuth {
    /**
     * TODO
     */
    public interface Provider {
        /**
         * TODO
         */
        public data class StartParameters(
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
         * TODO
         */
        public suspend fun start(parameters: StartParameters)

        /**
         * TODO
         */
        public fun start(
            parameters: StartParameters,
            callback: (Unit) -> Unit,
        )

        public val discovery: ProviderDiscovery
    }

    /**
     * TODO
     */
    public interface ProviderDiscovery {
        /**
         * TODO
         */
        public data class DiscoveryStartParameters(
            val context: Activity,
            val oAuthRequestIdentifier: Int,
            val discoveryRedirectUrl: String? = null,
            val customScopes: List<String>? = null,
            val providerParams: Map<String, String>? = null,
        )

        /**
         * TODO
         */
        public suspend fun start(parameters: DiscoveryStartParameters)

        /**
         * TODO
         */
        public fun start(
            parameters: DiscoveryStartParameters,
            callback: (Unit) -> Unit,
        )
    }

    /**
     * TODO
     */
    public interface Discovery {
        /**
         * TODO
         */
        public data class DiscoveryAuthenticateParameters(
            val discoveryOauthToken: String,
        )

        /**
         * TODO
         */
        public suspend fun authenticate(parameters: DiscoveryAuthenticateParameters): OAuthDiscoveryAuthenticateResponse

        /**
         * TODO
         */
        public fun authenticate(
            parameters: DiscoveryAuthenticateParameters,
            callback: (OAuthDiscoveryAuthenticateResponse) -> Unit,
        )
    }

    /**
     * TODO
     */
    public val google: Provider

    /**
     * TODO
     */
    public val microsoft: Provider

    /**
     * TODO
     */
    public val discovery: Discovery

    /**
     * TODO
     */
    public data class AuthenticateParameters(
        val oauthToken: String,
        val locale: String? = null,
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
    )

    /**
     * TODO
     */
    public suspend fun authenticate(parameters: AuthenticateParameters): OAuthAuthenticateResponse

    /**
     * TODO
     */
    public fun authenticate(
        parameters: AuthenticateParameters,
        callback: (OAuthAuthenticateResponse) -> Unit,
    )
}
