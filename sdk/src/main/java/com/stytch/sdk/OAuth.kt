package com.stytch.sdk

import android.app.Activity
import android.content.Intent

public interface OAuth {
    /**
     * The interface for authenticating a user with Google.
     */
    public val google: Google

    public interface Google {
        /**
         * Data class used for wrapping parameters to start a Google OneTap flow
         * @param context is the calling Activity
         * @param clientId is the Google Cloud OAuth Client Id
         * @param oAuthRequestIdentifier is an ID associated with the Google Sign In intent
         * @param autoSelectEnabled toggles whether or not to autoselect an account if only one Google account exists
         */
        public data class StartParameters(
            val context: Activity,
            val clientId: String,
            val oAuthRequestIdentifier: Int,
            val autoSelectEnabled: Boolean = false,
        )

        /**
         * Data class used for wrapping parameters to authenticate a Google OneTap flow
         * @param data is the resulting intent returned by the Google OneTap flow
         * @param sessionDurationMinutes indicates how long the session should last before it expires
         */
        public data class AuthenticateParameters(
            val data: Intent,
            val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
        )

        /**
         * Begin a Google OneTap login flow. Returns true if the flow was successfully initiated, false if not
         * @param parameters required to begin the OneTap flow
         */
        public suspend fun start(parameters: StartParameters): Boolean

        /**
         * Begin a Google OneTap login flow.
         * @param parameters required to begin the OneTap flow
         * @param callback a callback that receives the result of starting the OneTap flow
         */
        public fun start(parameters: StartParameters, callback: (Boolean) -> Unit)

        /**
         * Authenticate a Google OneTap login
         * @param parameters required to authenticate the Google OneTap login
         * @return StytchResult<AuthData>
         */
        public suspend fun authenticate(parameters: AuthenticateParameters): AuthResponse

        /**
         * Authenticate a Google OneTap login
         * @param parameters required to authenticate the Google OneTap login
         * @param callback a callback that receives the result of authenticating the OneTap login
         */
        public fun authenticate(parameters: AuthenticateParameters, callback: (AuthResponse) -> Unit)

        /**
         * Sign a user out of Google Play Services
         */
        public fun signOut()
    }
}
