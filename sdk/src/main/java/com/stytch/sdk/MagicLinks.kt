package com.stytch.sdk

public interface MagicLinks {
    public data class Parameters(
        val email: String,
        val loginMagicLinkUrl: String? = null
    )

    public val email: EmailMagicLinks

    /**
     * Wraps the magic link authenticate API endpoint which validates the magic link token passed in. If this method succeeds, the user will be logged in, granted an active session
     * @return LoginOrCreateUserByEmailResponse response from backend
     */
    public suspend fun authenticate(token: String, sessionDurationInMinutes: UInt = 60u): BaseResponse

    public fun authenticate(
        token: String,
        sessionDurationInMinutes: UInt = 60u,
        callback: (response: BaseResponse) -> Unit,
    )

    public interface EmailMagicLinks {
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