package com.stytch.sdk.b2b.otp

import com.stytch.sdk.b2b.BasicResponse
import com.stytch.sdk.b2b.EmailOTPAuthenticateResponse
import com.stytch.sdk.b2b.EmailOTPDiscoveryAuthenticateResponse
import com.stytch.sdk.b2b.EmailOTPDiscoverySendResponse
import com.stytch.sdk.b2b.EmailOTPLoginOrSignupResponse
import com.stytch.sdk.b2b.SMSAuthenticateResponse
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.SetMFAEnrollment
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.common.network.models.Locale
import java.util.concurrent.CompletableFuture

/**
 * The OTP interface provides methods for sending and authenticating One-Time Passcodes (OTP) via SMS
 */

public interface OTP {
    /**
     * Public variable that exposes an instance of SMS OTP
     */
    public val sms: SMS

    /**
     * Public variable that exposes an instance of Email OTP
     */
    public val email: Email

    /**
     * Provides all possible ways to call SMS OTP endpoints
     */

    public interface SMS {
        /**
         * A data class wrapping the parameters needed to send an SMS OTP
         * @property organizationId The ID of the organization the member belongs to
         * @property memberId The ID of the member to send the OTP to
         * @property mfaPhoneNumber The phone number to send the OTP to. If the member already has a phone number,
         * this argument is not needed. If the member does not have a phone number and this argument is not provided,
         * an error will be thrown.
         * @property locale The locale is used to determine which language to use in the email. Parameter is a
         * []IETF BCP 47 language tag](https://www.w3.org/International/articles/language-tags/), e.g. "en".
         * Currently supported languages are English ("en"), Spanish ("es"), and Brazilian Portuguese ("pt-br"); if no
         * value is provided, the copy defaults to English.
         * @property enableAutofill indicates whether the SMS message should include autofill metadata
         * @property autofillSessionDurationMinutes indicates how long an autofilled session should last
         */
        @JacocoExcludeGenerated
        public data class SendParameters
            @JvmOverloads
            constructor(
                val organizationId: String,
                val memberId: String,
                val mfaPhoneNumber: String? = null,
                val locale: Locale? = null,
                val enableAutofill: Boolean = false,
                val autofillSessionDurationMinutes: Int =
                    StytchB2BClient.configurationManager.options.sessionDurationMinutes,
            )

        /**
         * Send a one-time passcode (OTP) to a user using their phone number via SMS.
         * @param parameters required to receive a SMS OTP
         * @return [BasicResponse]
         */
        public suspend fun send(parameters: SendParameters): BasicResponse

        /**
         * Send a one-time passcode (OTP) to a user using their phone number via SMS.
         * @param parameters required to receive a SMS OTP
         * @param callback a callback that receives a [BasicResponse]
         */
        public fun send(
            parameters: SendParameters,
            callback: (BasicResponse) -> Unit,
        )

        /**
         * Send a one-time passcode (OTP) to a user using their phone number via SMS.
         * @param parameters required to receive a SMS OTP
         * @return [BasicResponse]
         */
        public fun sendCompletable(parameters: SendParameters): CompletableFuture<BasicResponse>

        /**
         * A data class wrapping the parameters needed to authenticate an SMS OTP
         * @property organizationId The ID of the organization the member belongs to
         * @property memberId The ID of the member to send the OTP to
         * @property code The OTP to authenticate
         * @property setMFAEnrollment If set to 'enroll', enrolls the member in MFA by setting the "mfa_enrolled"
         * boolean to true. If set to 'unenroll', unenrolls the member in MFA by setting the "mfa_enrolled" boolean to
         * false. If not set, does not affect the member's MFA enrollment.
         * @property sessionDurationMinutes indicates how long the session should last before it expires
         */
        @JacocoExcludeGenerated
        public data class AuthenticateParameters
            @JvmOverloads
            constructor(
                val organizationId: String,
                val memberId: String,
                val code: String,
                val setMFAEnrollment: SetMFAEnrollment? = null,
                val sessionDurationMinutes: Int = StytchB2BClient.configurationManager.options.sessionDurationMinutes,
            )

        /**
         * Authenticate a one-time passcode (OTP) sent to a user via SMS.
         * @param parameters required to authenticate an SMS OTP
         * @return [SMSAuthenticateResponse]
         */
        public suspend fun authenticate(parameters: AuthenticateParameters): SMSAuthenticateResponse

        /**
         * Authenticate a one-time passcode (OTP) sent to a user via SMS.
         * @param parameters required to authenticate an SMS OTP
         * @param callback a callback that receives a [SMSAuthenticateResponse]
         */
        public fun authenticate(
            parameters: AuthenticateParameters,
            callback: (SMSAuthenticateResponse) -> Unit,
        )

        /**
         * Authenticate a one-time passcode (OTP) sent to a user via SMS.
         * @param parameters required to authenticate an SMS OTP
         * @return [SMSAuthenticateResponse]
         */
        public fun authenticateCompletable(
            parameters: AuthenticateParameters,
        ): CompletableFuture<SMSAuthenticateResponse>
    }

    /**
     * Provides all possible ways to call Email OTP endpoints
     */

    public interface Email {
        public val discovery: Discovery

        /**
         * A data class wrapping the parameters needed to send an Email OTP LoginOrSignup request
         * @property organizationId The ID of the organization the member belongs to
         * @property emailAddress The email address of the member
         * @property loginTemplateId Use a custom template for login emails. By default, it will use your default email
         * template. The template must be a template using our built-in customizations or a custom HTML email for
         * @property signupTemplateId Use a custom template for sign-up emails. By default, it will use your default
         * OTP - Login.
         * email template. The template must be a template using our built-in customizations or a custom HTML email for
         * OTP - Sign-up.
         * @property locale The locale is used to determine which language to use in the email. Parameter is a
         * []IETF BCP 47 language tag](https://www.w3.org/International/articles/language-tags/), e.g. "en".
         * Currently supported languages are English ("en"), Spanish ("es"), and Brazilian Portuguese ("pt-br"); if no
         * value is provided, the copy defaults to English.
         */
        @JacocoExcludeGenerated
        public data class LoginOrSignupParameters
            @JvmOverloads
            constructor(
                val organizationId: String,
                val emailAddress: String,
                val loginTemplateId: String? = null,
                val signupTemplateId: String? = null,
                val locale: Locale? = null,
            )

        /**
         * Send a one-time passcode (OTP) to a user using email address
         * @param parameters required to receive an email loginOrSignup OTP
         * @return [EmailOTPLoginOrSignupResponse]
         */
        public suspend fun loginOrSignup(parameters: LoginOrSignupParameters): EmailOTPLoginOrSignupResponse

        /**
         * Send a one-time passcode (OTP) to a user using email address
         * @param parameters required to receive an email loginOrSignup OTP
         * @param callback a callback that receives a [EmailOTPLoginOrSignupResponse]
         */
        public fun loginOrSignup(
            parameters: LoginOrSignupParameters,
            callback: (EmailOTPLoginOrSignupResponse) -> Unit,
        )

        /**
         * Send a one-time passcode (OTP) to a user using email address
         * @param parameters required to receive an email loginOrSignup OTP
         * @return [EmailOTPLoginOrSignupResponse]
         */
        public fun loginOrSignupCompletable(
            parameters: LoginOrSignupParameters,
        ): CompletableFuture<EmailOTPLoginOrSignupResponse>

        /**
         * A data class wrapping the parameters needed to authenticate an Email OTP
         * @property code The code to authenticate
         * @property organizationId The ID of the organization the member belongs to
         * @property emailAddress The email address of the member
         * @property locale The locale is used to determine which language to use in the email. Parameter is a
         * []IETF BCP 47 language tag](https://www.w3.org/International/articles/language-tags/), e.g. "en".
         * Currently supported languages are English ("en"), Spanish ("es"), and Brazilian Portuguese ("pt-br"); if no
         * value is provided, the copy defaults to English.
         * @property sessionDurationMinutes indicates how long the session should last before it expires
         */
        @JacocoExcludeGenerated
        public data class AuthenticateParameters
            @JvmOverloads
            constructor(
                val code: String,
                val organizationId: String,
                val emailAddress: String,
                val locale: Locale? = null,
                val sessionDurationMinutes: Int = StytchB2BClient.configurationManager.options.sessionDurationMinutes,
            )

        /**
         * Authenticate a one-time passcode (OTP) sent to a user via Email.
         * @param parameters required to authenticate an Email OTP
         * @return [EmailOTPAuthenticateResponse]
         */
        public suspend fun authenticate(parameters: AuthenticateParameters): EmailOTPAuthenticateResponse

        /**
         * Authenticate a one-time passcode (OTP) sent to a user via Email.
         * @param parameters required to authenticate an Email OTP
         * @param callback a callback that receives a [EmailOTPAuthenticateResponse]
         */
        public fun authenticate(
            parameters: AuthenticateParameters,
            callback: (EmailOTPAuthenticateResponse) -> Unit,
        )

        /**
         * Authenticate a one-time passcode (OTP) sent to a user via Email.
         * @param parameters required to authenticate an Email OTP
         * @return [EmailOTPAuthenticateResponse]
         */
        public fun authenticateCompletable(
            parameters: AuthenticateParameters,
        ): CompletableFuture<EmailOTPAuthenticateResponse>

        /**
         * Provides all possible ways to call Email OTP Discovery endpoints
         */

        public interface Discovery {
            /**
             * A data class wrapping the parameters needed to send an Email Discovery OTP
             * @property emailAddress The email address of the member
             * @property loginTemplateId Use a custom template for login emails. By default, it will use your default
             * email template. The template must be a template using our built-in customizations or a custom HTML email
             * for OTP - Login.
             * @property locale The locale is used to determine which language to use in the email. Parameter is a
             * []IETF BCP 47 language tag](https://www.w3.org/International/articles/language-tags/), e.g. "en".
             * Currently supported languages are English ("en"), Spanish ("es"), and Brazilian Portuguese ("pt-br");
             * if no value is provided, the copy defaults to English.
             */
            @JacocoExcludeGenerated
            public data class SendParameters
                @JvmOverloads
                constructor(
                    val emailAddress: String,
                    val loginTemplateId: String? = null,
                    val locale: Locale? = null,
                )

            /**
             * Send a one-time passcode (OTP) to a user using their email address.
             * @param parameters required to receive a Email OTP
             * @return [EmailOTPDiscoverySendResponse]
             */
            public suspend fun send(parameters: SendParameters): EmailOTPDiscoverySendResponse

            /**
             * Send a one-time passcode (OTP) to a user using their email address.
             * @param parameters required to receive a Email OTP
             * @param callback a callback that receives a [EmailOTPDiscoverySendResponse]
             */
            public fun send(
                parameters: SendParameters,
                callback: (EmailOTPDiscoverySendResponse) -> Unit,
            )

            /**
             * Send a one-time passcode (OTP) to a user using their email address.
             * @param parameters required to receive a Email OTP
             * @return [EmailOTPDiscoverySendResponse]
             */
            public fun sendCompletable(parameters: SendParameters): CompletableFuture<EmailOTPDiscoverySendResponse>

            /**
             * A data class wrapping the parameters needed to authenticate an Email Discovery OTP
             * @property code The OTP to authenticate
             * @property emailAddress The email address of the member
             */
            @JacocoExcludeGenerated
            public data class AuthenticateParameters(
                val code: String,
                val emailAddress: String,
            )

            /**
             * Authenticate a one-time passcode (OTP) sent to a user via email.
             * @param parameters required to authenticate an Email Discovery OTP
             * @return [EmailOTPDiscoveryAuthenticateResponse]
             */
            public suspend fun authenticate(parameters: AuthenticateParameters): EmailOTPDiscoveryAuthenticateResponse

            /**
             * Authenticate a one-time passcode (OTP) sent to a user via email.
             * @param parameters required to authenticate an Email Discovery OTP
             * @param callback a callback that receives a [EmailOTPDiscoveryAuthenticateResponse]
             */
            public fun authenticate(
                parameters: AuthenticateParameters,
                callback: (EmailOTPDiscoveryAuthenticateResponse) -> Unit,
            )

            /**
             * Authenticate a one-time passcode (OTP) sent to a user via email.
             * @param parameters required to authenticate an Email Discovery OTP
             * @return [EmailOTPDiscoveryAuthenticateResponse]
             */
            public fun authenticateCompletable(
                parameters: AuthenticateParameters,
            ): CompletableFuture<EmailOTPDiscoveryAuthenticateResponse>
        }
    }
}
