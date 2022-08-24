package com.stytch.sdk

public interface OTP {

    public data class AuthParameters(
        val token: String,
        val sessionDurationMinutes: UInt = 60u,
    )

    public val sms: SmsOTP
    public val whatsapp: WhatsappOTP
    public val email: EmailOTP

    public suspend fun authenticate(
        parameters: AuthParameters,
    ): BaseResponse

    public fun authenticate(
        parameters: AuthParameters,
        callback: (response: BaseResponse) -> Unit,
    )

    public interface SmsOTP {

        public data class Parameters(
            val phoneNumber: String,
            val expirationMinutes: UInt = 60u,
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

    public interface WhatsappOTP {

        public data class Parameters(
            val phoneNumber: String,
            val expirationMinutes: UInt = 60u,
        )

        /**
         * Wraps Stytch’s Whatsapp OTP login_or_create endpoint. Requests a Whatsapp OTP for a user to log in or create an account depending on the presence and/or status current account.
         * @param parameters required to receive a Whatsapp OTP
         * @return BaseResponse response from backend
         */
        public suspend fun loginOrCreate(parameters: Parameters): BaseResponse

        /**
         * Wraps Stytch’s Whatsapp OTP login_or_create endpoint. Requests a Whatsapp OTP for a user to log in or create an account depending on the presence and/or status current account.
         * @param parameters required to receive a Whatsapp OTP
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
            val expirationMinutes: UInt = 60u,
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
