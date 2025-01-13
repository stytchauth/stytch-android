package com.stytch.sdk.consumer.oauth

import android.app.Activity
import com.stytch.sdk.common.DEFAULT_SESSION_TIME_MINUTES
import com.stytch.sdk.consumer.NativeOAuthResponse
import com.stytch.sdk.consumer.OAuthAuthenticatedResponse
import java.util.concurrent.CompletableFuture

/**
 * The OAuth interface provides methods for authenticating a user via a native Google OneTap prompt or any of the
 * supported third-party OAuth providers, provided you have configured them within your Stytch Dashboard.
 */
public interface OAuth {
    /**
     * The interface for authenticating a user with Google OneTap
     */
    public val googleOneTap: GoogleOneTap

    /**
     * The interface for authenticating a user with Apple
     */
    public val apple: ThirdParty

    /**
     * The interface for authenticating a user with Amazon
     */
    public val amazon: ThirdParty

    /**
     * The interface for authenticating a user with BitBucket
     */
    public val bitbucket: ThirdParty

    /**
     * The interface for authenticating a user with Coinbase
     */
    public val coinbase: ThirdParty

    /**
     * The interface for authenticating a user with Discord
     */
    public val discord: ThirdParty

    /**
     * The interface for authenticating a user with Facebook
     */
    public val facebook: ThirdParty

    /**
     * The interface for authenticating a user with Figma
     */
    public val figma: ThirdParty

    /**
     * The interface for authenticating a user with GitHub
     */
    public val github: ThirdParty

    /**
     * The interface for authenticating a user with GitLab
     */
    public val gitlab: ThirdParty

    /**
     * The interface for authenticating a user with Google
     */
    public val google: ThirdParty

    /**
     * The interface for authenticating a user with LinkedIn
     */
    public val linkedin: ThirdParty

    /**
     * The interface for authenticating a user with Microsoft
     */
    public val microsoft: ThirdParty

    /**
     * The interface for authenticating a user with Salesforce
     */
    public val salesforce: ThirdParty

    /**
     * The interface for authenticating a user with Slack
     */
    public val slack: ThirdParty

    /**
     * The interface for authenticating a user with Snapchat
     */
    public val snapchat: ThirdParty

    /**
     * The interface for authenticating a user with TikTok
     */
    public val tiktok: ThirdParty

    /**
     * The interface for authenticating a user with Twitch
     */
    public val twitch: ThirdParty

    /**
     * The interface for authenticating a user with Twitter
     */
    public val twitter: ThirdParty

    /**
     * The interface for authenticating a user with Twitter
     */
    public val yahoo: ThirdParty

    /**
     * Provides start, authenticate, and signOut methods for native Google One Tap authentication
     */
    public interface GoogleOneTap {
        /**
         * Data class used for wrapping parameters to start a Google OneTap flow
         * @property context is the calling Activity
         * @property clientId is the Google Cloud OAuth Client Id
         * @property autoSelectEnabled toggles whether or not to autoselect an account if only one Google account exists
         * @property sessionDurationMinutes indicates how long the session should last before it expires
         */
        public data class StartParameters
            @JvmOverloads
            constructor(
                val context: Activity,
                val clientId: String,
                val autoSelectEnabled: Boolean = false,
                val sessionDurationMinutes: Int = DEFAULT_SESSION_TIME_MINUTES,
            )

        /**
         * Begin a Google OneTap login flow. Returns an authenticated session if the flow was successfully initiated.
         * If this returns an error, it means the Google OneTap flow is not available (no play services on device
         * or user signed out), and you can fallback to the ThirdParty/Legacy Google OAuth flow
         * @param parameters required to begin the OneTap flow
         * @return NativeOAuthResponse
         */
        public suspend fun start(parameters: StartParameters): NativeOAuthResponse

        /**
         * Begin a Google OneTap login flow. Returns an authenticated session if the flow was successfully initiated.
         * If this returns an error, it means the Google OneTap flow is not available (no play services on device
         * or user signed out), and you can fallback to the ThirdParty/Legacy Google OAuth flow
         * @param parameters required to begin the OneTap flow
         * @param callback a callback that receives a NativeOAuthResponse
         */
        public fun start(
            parameters: StartParameters,
            callback: (NativeOAuthResponse) -> Unit,
        )

        /**
         * Begin a Google OneTap login flow. Returns an authenticated session if the flow was successfully initiated.
         * If this returns an error, it means the Google OneTap flow is not available (no play services on device
         * or user signed out), and you can fallback to the ThirdParty/Legacy Google OAuth flow
         * @param parameters required to begin the OneTap flow
         * @return NativeOAuthResponse
         */
        public fun startCompletable(parameters: StartParameters): CompletableFuture<NativeOAuthResponse>

        /**
         * Sign a user out of Google Play Services
         */
        public fun signOut(activity: Activity)
    }

    /**
     * Provides a method for starting Third Party OAuth authentications
     */
    public interface ThirdParty {
        /**
         * The Third Party OAuth provider name
         */
        public val providerName: String

        /**
         * Data class used for wrapping parameters to start a third party OAuth flow
         * @property context the calling Activity for launching the browser
         * @property oAuthRequestIdentifier is an ID for the return intent from the OAuth flow
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
        public data class StartParameters
            @JvmOverloads
            constructor(
                val context: Activity,
                val oAuthRequestIdentifier: Int,
                val loginRedirectUrl: String? = null,
                val signupRedirectUrl: String? = null,
                val customScopes: List<String>? = null,
                val providerParams: Map<String, String>? = null,
            )

        /**
         * Data class used for wrapping parameters to authenticate a third party OAuth flow
         * @property token is the token returned from the provider
         * @property sessionDurationMinutes indicates how long the session should last before it expires
         */
        public data class AuthenticateParameters
            @JvmOverloads
            constructor(
                val token: String,
                val sessionDurationMinutes: Int = DEFAULT_SESSION_TIME_MINUTES,
            )

        /**
         * Begin a ThirdParty OAuth flow
         * @param parameters required to start the OAuth flow
         */
        public fun start(parameters: StartParameters)
    }

    /**
     * Authenticate a ThirdParty OAuth flow
     * @param parameters required to authenticate the OAuth flow
     * @return [OAuthAuthenticatedResponse]
     */
    public suspend fun authenticate(parameters: ThirdParty.AuthenticateParameters): OAuthAuthenticatedResponse

    /**
     * Authenticate a ThirdParty OAuth flow
     * @param parameters required to authenticate the ThirdParty OAuth flow
     * @param callback a callback that receives an [OAuthAuthenticatedResponse]
     */
    public fun authenticate(
        parameters: ThirdParty.AuthenticateParameters,
        callback: (OAuthAuthenticatedResponse) -> Unit,
    )

    /**
     * Authenticate a ThirdParty OAuth flow
     * @param parameters required to authenticate the OAuth flow
     * @return [OAuthAuthenticatedResponse]
     */
    public fun authenticateCompletable(
        parameters: ThirdParty.AuthenticateParameters,
    ): CompletableFuture<OAuthAuthenticatedResponse>
}
