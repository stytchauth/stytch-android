package com.stytch.sdk.b2b.otp

import com.stytch.sdk.b2b.AuthResponse
import com.stytch.sdk.b2b.BasicResponse
import com.stytch.sdk.b2b.network.models.SetMFAEnrollment
import com.stytch.sdk.common.Constants

public interface OTP {
    /**
     * Public variable that exposes an instance of SMS OTP
     */
    public val sms: SMS

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
         */
        public data class SendParameters(
            val organizationId: String,
            val memberId: String,
            val mfaPhoneNumber: String? = null,
            val locale: String? = null,
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
         * A data class wrapping the parameters needed to authenticate an SMS OTP
         * @property organizationId The ID of the organization the member belongs to
         * @property memberId The ID of the member to send the OTP to
         * @property code The OTP to authenticate
         * @property setMFAEnrollment If set to 'enroll', enrolls the member in MFA by setting the "mfa_enrolled"
         * boolean to true. If set to 'unenroll', unenrolls the member in MFA by setting the "mfa_enrolled" boolean to
         * false. If not set, does not affect the member's MFA enrollment.
         */
        public data class AuthenticateParameters(
            val organizationId: String,
            val memberId: String,
            val code: String,
            val setMFAEnrollment: SetMFAEnrollment? = null,
            val sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES,
        )

        /**
         * Authenticate a one-time passcode (OTP) sent to a user via SMS.
         * @param parameters required to authenticate an SMS OTP
         * @return [AuthResponse]
         */
        public suspend fun authenticate(parameters: AuthenticateParameters): AuthResponse

        /**
         * Authenticate a one-time passcode (OTP) sent to a user via SMS.
         * @param parameters required to authenticate an SMS OTP
         * @param callback a callback that receives a [AuthResponse]
         */
        public fun authenticate(
            parameters: AuthenticateParameters,
            callback: (AuthResponse) -> Unit,
        )
    }
}
