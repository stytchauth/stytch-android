package com.stytch.sdk.otp

import com.stytch.sdk.AuthResponse
import com.stytch.sdk.BaseResponse
import com.stytch.sdk.Constants.DEFAULT_OTP_EXPIRATION_TIME_MINUTES
import com.stytch.sdk.Constants.DEFAULT_SESSION_TIME_MINUTES
import com.stytch.sdk.LoginOrCreateOTPResponse
import com.stytch.sdk.UserAttributes

/**
 * OTP interface that encompasses authentication functions as well as other related functionality
 */
public interface OTP {

    /**
     * Data class used for wrapping parameters used with OTP authentication
     * @param token the value sent to the user via the otp delivery method
     * @param methodId the identifier returned from the corresponding loginOrCreate method
     * @param sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class AuthParameters(
        val token: String,
        val methodId: String,
        val sessionDurationMinutes: UInt = DEFAULT_SESSION_TIME_MINUTES,
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
        public data class LoginOrCreateParameters(
            val phoneNumber: String,
            val expirationMinutes: UInt = DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
        )

        /**
         * Wraps Stytch’s SMS OTP login_or_create endpoint. Requests a SMS OTP for a user to log in or create an account depending on the presence and/or status current account.
         * @param parameters required to receive a SMS OTP
         * @return BaseResponse response from backend
         */
        public suspend fun loginOrCreate(parameters: LoginOrCreateParameters): LoginOrCreateOTPResponse

        /**
         * Wraps Stytch’s SMS OTP login_or_create endpoint. Requests a SMS OTP for a user to log in or create an account depending on the presence and/or status current account.
         * @param parameters required to receive a SMS OTP
         * @param callback calls callback with BaseResponse response from backend
         */
        public fun loginOrCreate(
            parameters: LoginOrCreateParameters,
            callback: (response: LoginOrCreateOTPResponse) -> Unit,
        )

        public data class SendParameters(
            val phoneNumber: String,
            val expirationMinutes: UInt?,
            val locale: String?,
            val attributes: UserAttributes?,
            val userId: String?,
            val sessionToken: String?,
            val sessionJwt: String?,
        )

        public suspend fun send(parameters: SendParameters): BaseResponse

        public fun send(parameters: SendParameters, callback: (response: BaseResponse) -> Unit)
    }

    /**
     * Provides all possible ways to call WhatsApp OTP endpoints
     */
    public interface WhatsAppOTP {

        /**
         * @param phoneNumber the number the OTP code should be sent to via WhatsApp, in E.164 format (i.e. +1XXXXXXXXXX)
         * @param expirationMinutes indicates how long the OTP should last before it expires
         */
        public data class LoginOrCreateParameters(
            val phoneNumber: String,
            val expirationMinutes: UInt = DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
        )

        /**
         * Wraps Stytch’s WhatsApp OTP login_or_create endpoint. Requests a WhatsApp OTP for a user to log in or create an account depending on the presence and/or status current account.
         * @param parameters required to receive a WhatsApp OTP
         * @return BaseResponse response from backend
         */
        public suspend fun loginOrCreate(parameters: LoginOrCreateParameters): LoginOrCreateOTPResponse

        /**
         * Wraps Stytch’s WhatsApp OTP login_or_create endpoint. Requests a WhatsApp OTP for a user to log in or create an account depending on the presence and/or status current account.
         * @param parameters required to receive a WhatsApp OTP
         * @param callback calls callback with BaseResponse response from backend
         */
        public fun loginOrCreate(
            parameters: LoginOrCreateParameters,
            callback: (response: LoginOrCreateOTPResponse) -> Unit,
        )

        public data class SendParameters(
            val phoneNumber: String,
            val expirationMinutes: UInt?,
            val locale: String?,
            val attributes: UserAttributes?,
            val userId: String?,
            val sessionToken: String?,
            val sessionJwt: String?,
        )

        public suspend fun send(parameters: SendParameters): BaseResponse

        public fun send(parameters: SendParameters, callback: (response: BaseResponse) -> Unit)
    }

    /**
     * Provides all possible ways to call Email OTP endpoints
     */
    public interface EmailOTP {

        /**
         * @param email the address the OTP code would be sent to via Email
         * @param expirationMinutes indicates how long the OTP should last before it expires
         * @param loginTemplateId Use a custom template for login emails. By default, it will use your default email template. The template must be a template using our built-in customizations or a custom HTML email for Magic links - Login.
         * @param signupTemplateId Use a custom template for sign-up emails. By default, it will use your default email template. The template must be a template using our built-in customizations or a custom HTML email for Magic links - Sign-up.
         */
        public data class LoginOrCreateParameters(
            val email: String,
            val expirationMinutes: UInt = DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
            val loginTemplateId: String? = null,
            val signupTemplateId: String? = null,
        )

        /**
         * Wraps Stytch’s Email OTP login_or_create endpoint. Requests an Email OTP for a user to log in or create an account depending on the presence and/or status current account.
         * @param parameters required to receive an Email OTP
         * @return BaseResponse response from backend
         */
        public suspend fun loginOrCreate(parameters: LoginOrCreateParameters): LoginOrCreateOTPResponse

        /**
         * Wraps Stytch’s Email OTP login_or_create endpoint. Requests an Email OTP for a user to log in or create an account depending on the presence and/or status current account.
         * @param parameters required to receive an Email OTP
         * @param callback calls callback with BaseResponse response from backend
         */
        public fun loginOrCreate(
            parameters: LoginOrCreateParameters,
            callback: (response: LoginOrCreateOTPResponse) -> Unit,
        )

        public data class SendParameters(
            val email: String,
            val expirationMinutes: UInt?,
            val loginTemplateId: String?,
            val signupTemplateId: String?,
            val locale: String?,
            val attributes: UserAttributes?,
            val userId: String?,
            val sessionToken: String?,
            val sessionJwt: String?,
        )

        public suspend fun send(parameters: SendParameters): BaseResponse

        public fun send(parameters: SendParameters, callback: (response: BaseResponse) -> Unit)
    }
}
