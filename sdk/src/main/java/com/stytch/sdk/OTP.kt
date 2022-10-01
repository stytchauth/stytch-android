package com.stytch.sdk

public interface OTP {

    public data class AuthParameters(
        val token: String,
        val sessionDurationMinutes: UInt = 5u,
    )

    public val sms: SmsOTP
    public val whatsapp: WhatsAppOTP
    public val email: EmailOTP

    public suspend fun authenticate(
        parameters: AuthParameters,
    ): AuthResponse

    public fun authenticate(
        parameters: AuthParameters,
        callback: (response: AuthResponse) -> Unit,
    )

    public interface SmsOTP {

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

    public interface WhatsAppOTP {

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

    public interface EmailOTP {

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
