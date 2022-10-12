package com.stytch.sdk

public interface OTP {

    /**
     * Data class used for wrapping parameters used with OTP authentication
     * @param token used for authentication
     * @param sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class AuthParameters(
        val token: String,
        val sessionDurationMinutes: UInt = 5u,
    )


    /**
     * Public variable that exposes an instance of SMS OTP
     */
    public val sms: SmsOTP

    /**
     * Public variable that exposes an instance of WhatsApp OTP
     */
    public val whatsapp: WhatsAppOTP

    /**
     * Public variable that exposes an instance of Email OTP
     */
    public val email: EmailOTP

    /**
     * Wraps the OTP authenticate API endpoint which validates the OTP token passed in. If this method succeeds, the user will be logged in, granted an active session
     * @param parameters required to authenticate
     * @return AuthResponse response from backend
     */
    public suspend fun authenticate(
        parameters: AuthParameters,
    ): AuthResponse

    /**
     * Wraps the OTP authenticate API endpoint which validates the OTP token passed in. If this method succeeds, the user will be logged in, granted an active session
     * @param parameters required to authenticate
     * @param callback calls callback with AuthResponse response from backend
     */
    public fun authenticate(
        parameters: AuthParameters,
        callback: (response: AuthResponse) -> Unit,
    )

    /**
     * Provides all possible ways to call SMS OTP endpoints
     */
    public interface SmsOTP {

        /**
         * Data class used for wrapping parameters used with SMS OTP
         * @param phoneNumber the number the OTP code should be sent to via SMS, in E.164 format (i.e. +1XXXXXXXXXX)
         * @param expirationMinutes indicates how long the OTP should last before it expires
         */
        public data class Parameters(
            val phoneNumber: String,
            val expirationMinutes: UInt = 10u,
        )

        /**
         * Wraps Stytch’s SMS OTP login_or_create endpoint. Requests a SMS OTP for a user to log in or create an account depending on the presence and/or status current account.
         * @param parameters required to receive a SMS OTP
         * @return BaseResponse response from backend
         */
        public suspend fun loginOrCreate(parameters: Parameters): BaseResponse

        /**
         * Wraps Stytch’s SMS OTP login_or_create endpoint. Requests a SMS OTP for a user to log in or create an account depending on the presence and/or status current account.
         * @param parameters required to receive a SMS OTP
         * @param callback calls callback with BaseResponse response from backend
         */
        public fun loginOrCreate(
            parameters: Parameters,
            callback: (response: BaseResponse) -> Unit,
        )

    }

    /**
     * Provides all possible ways to call WhatsApp OTP endpoints
     */
    public interface WhatsAppOTP {

        /**
         * @param phoneNumber the number the OTP code should be sent to via WhatsApp, in E.164 format (i.e. +1XXXXXXXXXX)
         * @param expirationMinutes indicates how long the OTP should last before it expires
         */
        public data class Parameters(
            val phoneNumber: String,
            val expirationMinutes: UInt = 10u,
        )

        /**
         * Wraps Stytch’s WhatsApp OTP login_or_create endpoint. Requests a WhatsApp OTP for a user to log in or create an account depending on the presence and/or status current account.
         * @param parameters required to receive a WhatsApp OTP
         * @return BaseResponse response from backend
         */
        public suspend fun loginOrCreate(parameters: Parameters): BaseResponse

        /**
         * Wraps Stytch’s WhatsApp OTP login_or_create endpoint. Requests a WhatsApp OTP for a user to log in or create an account depending on the presence and/or status current account.
         * @param parameters required to receive a WhatsApp OTP
         * @param callback calls callback with BaseResponse response from backend
         */
        public fun loginOrCreate(
            parameters: Parameters,
            callback: (response: BaseResponse) -> Unit,
        )

    }

    /**
     * Provides all possible ways to call Email OTP endpoints
     */
    public interface EmailOTP {

        /**
         * @param email the address the OTP code would be sent to via Email
         * @param expirationMinutes indicates how long the OTP should last before it expires
         */
        public data class Parameters(
            val email: String,
            val expirationMinutes: UInt = 10u,
        )

        /**
         * Wraps Stytch’s Email OTP login_or_create endpoint. Requests an Email OTP for a user to log in or create an account depending on the presence and/or status current account.
         * @param parameters required to receive an Email OTP
         * @return BaseResponse response from backend
         */
        public suspend fun loginOrCreate(parameters: Parameters): BaseResponse

        /**
         * Wraps Stytch’s Email OTP login_or_create endpoint. Requests an Email OTP for a user to log in or create an account depending on the presence and/or status current account.
         * @param parameters required to receive an Email OTP
         * @param callback calls callback with BaseResponse response from backend
         */
        public fun loginOrCreate(
            parameters: Parameters,
            callback: (response: BaseResponse) -> Unit,
        )

    }

}
