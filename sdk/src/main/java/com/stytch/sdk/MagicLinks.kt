package com.stytch.sdk

public interface MagicLinks {

    /**
     * @param token is the unique sequence of characters used to log in
     * @param sessionDurationMinutes is the duration after which a session needs to be renewed
     */
    public data class AuthParameters(
        val token: String,
        val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
    )

    public val email: EmailMagicLinks

    /**
     * Wraps the magic link authenticate API endpoint which validates the magic link token passed in. If this method succeeds, the user will be logged in, granted an active session
     * @param parameters required to authenticate
     * @return AuthResponse response from backend
     */
    public suspend fun authenticate(
        parameters: AuthParameters,
    ): AuthResponse

    /**
     * Wraps the magic link authenticate API endpoint which validates the magic link token passed in. If this method succeeds, the user will be logged in, granted an active session
     * @param parameters required to authenticate
     * @param callback calls callback with AuthResponse response from backend
     */
    public fun authenticate(
        parameters: AuthParameters,
        callback: (response: AuthResponse) -> Unit,
    )

    public interface EmailMagicLinks {

        /**
         * @param email is the account identifier for the account in the form of an Email address where you wish to receive a magic link to authenticate
         * @param loginMagicLinkUrl is the url where you should be redirected for login
         * @param signupMagicLinkUrl is the url where you should be redirected for signup
         * @param loginExpirationMinutes is the duration after which the login url should expire
         * @param signupExpirationMinutes is the duration after which the signup url should expire
         */
        public data class Parameters(
            val email: String,
            val loginMagicLinkUrl: String? = null,
            val signupMagicLinkUrl: String? = null,
            val loginExpirationMinutes: UInt? = null,
            val signupExpirationMinutes: UInt? = null,
        )

        /**
         * Wraps Stytch’s email magic link login_or_create endpoint. Requests an email magic link for a user to log in or create an account depending on the presence and/or status current account.
         * @param parameters required to receive magic link
         * @return LoginOrCreateUserByEmailResponse response from backend
         */
        public suspend fun loginOrCreate(parameters: Parameters): LoginOrCreateUserByEmailResponse

        /**
         * Wraps Stytch’s email magic link login_or_create endpoint. Requests an email magic link for a user to log in or create an account depending on the presence and/or status current account.
         * @param parameters required to receive magic link
         * @param callback calls callback with LoginOrCreateUserByEmailResponse response from backend
         */
        public fun loginOrCreate(
            parameters: Parameters,
            callback: (response: LoginOrCreateUserByEmailResponse) -> Unit,
        )
    }
}
