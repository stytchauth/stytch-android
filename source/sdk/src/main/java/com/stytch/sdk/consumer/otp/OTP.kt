package com.stytch.sdk.consumer.otp

import android.os.Parcelable
import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.DEFAULT_OTP_EXPIRATION_TIME_MINUTES
import com.stytch.sdk.common.DEFAULT_SESSION_TIME_MINUTES
import com.stytch.sdk.common.network.models.Locale
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.LoginOrCreateOTPResponse
import com.stytch.sdk.consumer.OTPSendResponse
import kotlinx.parcelize.Parcelize
import java.util.concurrent.CompletableFuture

/**
 * The OTP interface provides methods for sending and authenticating One-Time Passcodes (OTP) via SMS, WhatsApp, and
 * Email.
 */
public interface OTP {
    /**
     * Data class used for wrapping parameters used with OTP authentication
     * @property token the value sent to the user via the otp delivery method
     * @property methodId the identifier returned from the corresponding loginOrCreate or send method
     * @property sessionDurationMinutes indicates how long the session should last before it expires
     */
    public data class AuthParameters
        @JvmOverloads
        constructor(
            val token: String,
            val methodId: String,
            val sessionDurationMinutes: Int = DEFAULT_SESSION_TIME_MINUTES,
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
     * Authenticate a user given a method_id (the associated email_id or phone_id) and a code. This endpoint verifies
     * that the code is valid, hasn't expired or been previously used. A given method_id may only have a single active
     * OTP code at any given time, if a user requests another OTP code before the first one has expired, the first one
     * will be invalidated.
     * @param parameters required to authenticate
     * @return [AuthResponse]
     */
    public suspend fun authenticate(parameters: AuthParameters): AuthResponse

    /**
     * Authenticate a user given a method_id (the associated email_id or phone_id) and a code. This endpoint verifies
     * that the code is valid, hasn't expired or been previously used. A given method_id may only have a single active
     * OTP code at any given time, if a user requests another OTP code before the first one has expired, the first one
     * will be invalidated.
     * @param parameters required to authenticate
     * @param callback that receives an [AuthResponse]
     */
    public fun authenticate(
        parameters: AuthParameters,
        callback: (response: AuthResponse) -> Unit,
    )

    /**
     * Authenticate a user given a method_id (the associated email_id or phone_id) and a code. This endpoint verifies
     * that the code is valid, hasn't expired or been previously used. A given method_id may only have a single active
     * OTP code at any given time, if a user requests another OTP code before the first one has expired, the first one
     * will be invalidated.
     * @param parameters required to authenticate
     * @return [AuthResponse]
     */
    public fun authenticateCompletable(parameters: AuthParameters): CompletableFuture<AuthResponse>

    /**
     * Provides all possible ways to call SMS OTP endpoints
     */
    public interface SmsOTP {
        /**
         * Data class used for wrapping parameters used with SMS OTP
         * @property phoneNumber the number the OTP code should be sent to via SMS, in E.164 format (i.e. +1XXXXXXXXXX)
         * @property expirationMinutes indicates how long the OTP should last before it expires
         * @property enableAutofill indicates whether the SMS message should include autofill metadata
         * @property autofillSessionDurationMinutes indicates how long an autofilled session should last
         * @property locale Used to determine which language to use when sending the user this delivery method.
         * Currently supported languages are English (`"en"`), Spanish (`"es"`), and Brazilian Portuguese (`"pt-br"`);
         * if no value is provided, the copy defaults to English.
         */
        @Parcelize
        public data class Parameters
            @JvmOverloads
            constructor(
                val phoneNumber: String,
                val expirationMinutes: Int = DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
                val enableAutofill: Boolean = false,
                val autofillSessionDurationMinutes: Int = DEFAULT_SESSION_TIME_MINUTES,
                val locale: Locale? = null,
            ) : Parcelable

        /**
         * Send a one-time passcode (OTP) to a user using their phone number via SMS. If the phone number is not
         * associated with a user already, a user will be created.
         * @param parameters required to receive a SMS OTP
         * @return [LoginOrCreateOTPResponse]
         */
        public suspend fun loginOrCreate(parameters: Parameters): LoginOrCreateOTPResponse

        /**
         * Send a one-time passcode (OTP) to a user using their phone number via SMS. If the phone number is not
         * associated with a user already, a user will be created.
         * @param parameters required to receive a SMS OTP
         * @param callback a callback that receives a [LoginOrCreateOTPResponse]
         */
        public fun loginOrCreate(
            parameters: Parameters,
            callback: (response: LoginOrCreateOTPResponse) -> Unit,
        )

        /**
         * Send a one-time passcode (OTP) to a user using their phone number via SMS. If the phone number is not
         * associated with a user already, a user will be created.
         * @param parameters required to receive a SMS OTP
         * @return [LoginOrCreateOTPResponse]
         */
        public fun loginOrCreateCompletable(parameters: Parameters): CompletableFuture<LoginOrCreateOTPResponse>

        /**
         * Send a one-time passcode (OTP) to a user's phone number via SMS. If you'd like to create a user and send them
         * a passcode with one request, use our [loginOrCreate] method.
         * @param parameters required to send OTP
         * @return [OTPSendResponse]
         */
        public suspend fun send(parameters: Parameters): OTPSendResponse

        /**
         * Send a one-time passcode (OTP) to a user's phone number via SMS. If you'd like to create a user and send them
         * a passcode with one request, use our [loginOrCreate] method.
         * @param parameters required to send OTP
         * @param callback a callback that receives a [OTPSendResponse]
         */
        public fun send(
            parameters: Parameters,
            callback: (response: OTPSendResponse) -> Unit,
        )

        /**
         * Send a one-time passcode (OTP) to a user's phone number via SMS. If you'd like to create a user and send them
         * a passcode with one request, use our [loginOrCreate] method.
         * @param parameters required to send OTP
         * @return [OTPSendResponse]
         */
        public fun sendCompletable(parameters: Parameters): CompletableFuture<OTPSendResponse>
    }

    /**
     * Provides all possible ways to call WhatsApp OTP endpoints
     */
    public interface WhatsAppOTP {
        /**
         * Data class used for wrapping parameters used with WhatsApp OTP
         * @property phoneNumber the number the OTP code should be sent to via WhatsApp, in E.164 format
         * (i.e. +1XXXXXXXXXX)
         * @property expirationMinutes indicates how long the OTP should last before it expires
         */
        @Parcelize
        public data class Parameters
            @JvmOverloads
            constructor(
                val phoneNumber: String,
                val expirationMinutes: Int = DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
                val locale: Locale? = null,
            ) : Parcelable

        /**
         * Send a one-time passcode (OTP) to a user using their phone number via WhatsApp. If the phone number is not
         * associated with a user already, a user will be created.
         * @param parameters required to receive a WhatsApp OTP
         * @return [BaseResponse]
         */
        public suspend fun loginOrCreate(parameters: Parameters): LoginOrCreateOTPResponse

        /**
         * Send a one-time passcode (OTP) to a user using their phone number via WhatsApp. If the phone number is not
         * associated with a user already, a user will be created.
         * @param parameters required to receive a WhatsApp OTP
         * @param callback a callback that receives a [LoginOrCreateOTPResponse]
         */
        public fun loginOrCreate(
            parameters: Parameters,
            callback: (response: LoginOrCreateOTPResponse) -> Unit,
        )

        /**
         * Send a one-time passcode (OTP) to a user using their phone number via WhatsApp. If the phone number is not
         * associated with a user already, a user will be created.
         * @param parameters required to receive a WhatsApp OTP
         * @return [BaseResponse]
         */
        public fun loginOrCreateCompletable(parameters: Parameters): CompletableFuture<LoginOrCreateOTPResponse>

        /**
         * Send a one-time passcode (OTP) to a user's phone number via WhatsApp. If you'd like to create a user and send
         * them a passcode with one request, use our [loginOrCreate] method.
         * @param parameters required to send OTP
         * @return [OTPSendResponse]
         */
        public suspend fun send(parameters: Parameters): OTPSendResponse

        /**
         * Send a one-time passcode (OTP) to a user's phone number via WhatsApp. If you'd like to create a user and send
         * them a passcode with one request, use our [loginOrCreate] method.
         * @param parameters required to send OTP
         * @param callback a callback that receives a [OTPSendResponse]
         */
        public fun send(
            parameters: Parameters,
            callback: (response: OTPSendResponse) -> Unit,
        )

        /**
         * Send a one-time passcode (OTP) to a user's phone number via WhatsApp. If you'd like to create a user and send
         * them a passcode with one request, use our [loginOrCreate] method.
         * @param parameters required to send OTP
         * @return [OTPSendResponse]
         */
        public fun sendCompletable(parameters: Parameters): CompletableFuture<OTPSendResponse>
    }

    /**
     * Provides all possible ways to call Email OTP endpoints
     */
    public interface EmailOTP {
        /**
         * Data class used for wrapping parameters used with Email OTP
         * @property email the address the OTP code would be sent to via Email
         * @property expirationMinutes indicates how long the OTP should last before it expires
         * @property loginTemplateId Use a custom template for login emails. By default, it will use your default email
         * template. The template must be a template using our built-in customizations or a custom HTML email for
         * Magic links - Login.
         * @property signupTemplateId Use a custom template for sign-up emails. By default, it will use your default
         * email template. The template must be a template using our built-in customizations or a custom HTML email for
         * Magic links - Sign-up.
         */
        @Parcelize
        public data class Parameters
            @JvmOverloads
            constructor(
                val email: String,
                val expirationMinutes: Int = DEFAULT_OTP_EXPIRATION_TIME_MINUTES,
                val loginTemplateId: String? = null,
                val signupTemplateId: String? = null,
                val locale: Locale? = null,
            ) : Parcelable

        /**
         * Send a one-time passcode (OTP) to a user using their email address. If the email address is not associated
         * with a user already, a user will be created.
         * @param parameters required to receive an Email OTP
         * @return [LoginOrCreateOTPResponse]
         */
        public suspend fun loginOrCreate(parameters: Parameters): LoginOrCreateOTPResponse

        /**
         * Send a one-time passcode (OTP) to a user using their email address. If the email address is not associated
         * with a user already, a user will be created.
         * @param parameters required to receive an Email OTP
         * @param callback a callback that receives a [LoginOrCreateOTPResponse]
         */
        public fun loginOrCreate(
            parameters: Parameters,
            callback: (response: LoginOrCreateOTPResponse) -> Unit,
        )

        /**
         * Send a one-time passcode (OTP) to a user using their email address. If the email address is not associated
         * with a user already, a user will be created.
         * @param parameters required to receive an Email OTP
         * @return [LoginOrCreateOTPResponse]
         */
        public fun loginOrCreateCompletable(parameters: Parameters): CompletableFuture<LoginOrCreateOTPResponse>

        /**
         * Send a one-time passcode (OTP) to a user's email address. If you'd like to create a user and send them a
         * passcode with one request, use our [loginOrCreate] method.
         * @param parameters required to send OTP
         * @return [OTPSendResponse] response from backend
         */
        public suspend fun send(parameters: Parameters): OTPSendResponse

        /**
         * Send a one-time passcode (OTP) to a user's email address. If you'd like to create a user and send them a
         * passcode with one request, use our [loginOrCreate] method.
         * @param parameters required to send OTP
         * @param callback a callback that receives a [OTPSendResponse]
         */
        public fun send(
            parameters: Parameters,
            callback: (response: OTPSendResponse) -> Unit,
        )

        /**
         * Send a one-time passcode (OTP) to a user's email address. If you'd like to create a user and send them a
         * passcode with one request, use our [loginOrCreate] method.
         * @param parameters required to send OTP
         * @return [OTPSendResponse] response from backend
         */
        public fun sendCompletable(parameters: Parameters): CompletableFuture<OTPSendResponse>
    }
}
