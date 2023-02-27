package com.stytch.sdk.consumer.network

import com.stytch.sdk.common.network.ApiService
import com.stytch.sdk.common.network.models.CommonRequests
import com.stytch.sdk.common.network.models.CommonResponses
import com.stytch.sdk.consumer.network.models.ConsumerRequests
import com.stytch.sdk.consumer.network.models.ConsumerResponses
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

@Suppress("TooManyFunctions")
internal interface StytchApiService : ApiService {

    //region Magic Links
    @POST("magic_links/email/login_or_create")
    suspend fun loginOrCreateUserByEmail(
        @Body request: ConsumerRequests.MagicLinks.Email.LoginOrCreateUserRequest
    ): CommonResponses.MagicLinks.Email.LoginOrCreateUserResponse

    @POST("magic_links/email/send/primary")
    suspend fun sendEmailMagicLinkPrimary(
        @Body request: ConsumerRequests.MagicLinks.SendRequest
    ): CommonResponses.SendResponse

    @POST("magic_links/email/send/secondary")
    suspend fun sendEmailMagicLinkSecondary(
        @Body request: ConsumerRequests.MagicLinks.SendRequest
    ): CommonResponses.SendResponse

    @POST("magic_links/authenticate")
    suspend fun authenticate(
        @Body request: ConsumerRequests.MagicLinks.AuthenticateRequest
    ): ConsumerResponses.AuthenticateResponse
    //endregion Magic Links

    //region Sessions
    @POST("sessions/authenticate")
    suspend fun authenticateSessions(
        @Body request: CommonRequests.Sessions.AuthenticateRequest
    ): ConsumerResponses.AuthenticateResponse

    @POST("sessions/revoke")
    suspend fun revokeSessions(): CommonResponses.Sessions.RevokeResponse
    //endregion Sessions

    //region OTP
    @POST("otps/sms/login_or_create")
    suspend fun loginOrCreateUserByOTPWithSMS(
        @Body request: ConsumerRequests.OTP.SMS
    ): ConsumerResponses.LoginOrCreateOTPResponse

    @POST("otps/sms/send/primary")
    suspend fun sendOTPWithSMSPrimary(
        @Body request: ConsumerRequests.OTP.SMS
    ): CommonResponses.SendResponse

    @POST("otps/sms/send/secondary")
    suspend fun sendOTPWithSMSSecondary(
        @Body request: ConsumerRequests.OTP.SMS
    ): CommonResponses.SendResponse

    @POST("otps/whatsapp/login_or_create")
    suspend fun loginOrCreateUserByOTPWithWhatsApp(
        @Body request: ConsumerRequests.OTP.WhatsApp
    ): ConsumerResponses.LoginOrCreateOTPResponse

    @POST("otps/whatsapp/send/primary")
    suspend fun sendOTPWithWhatsAppPrimary(
        @Body request: ConsumerRequests.OTP.WhatsApp
    ): CommonResponses.SendResponse

    @POST("otps/whatsapp/send/secondary")
    suspend fun sendOTPWithWhatsAppSecondary(
        @Body request: ConsumerRequests.OTP.WhatsApp
    ): CommonResponses.SendResponse

    @POST("otps/email/login_or_create")
    suspend fun loginOrCreateUserByOTPWithEmail(
        @Body request: ConsumerRequests.OTP.Email
    ): ConsumerResponses.LoginOrCreateOTPResponse

    @POST("otps/email/send/primary")
    suspend fun sendOTPWithEmailPrimary(
        @Body request: ConsumerRequests.OTP.Email
    ): CommonResponses.SendResponse

    @POST("otps/email/send/secondary")
    suspend fun sendOTPWithEmailSecondary(
        @Body request: ConsumerRequests.OTP.Email
    ): CommonResponses.SendResponse

    @POST("otps/authenticate") // TODO Need to create a proper name to differentiate fom magiclinks authenticate
    suspend fun authenticateWithOTP(
        @Body request: ConsumerRequests.OTP.Authenticate
    ): ConsumerResponses.AuthenticateResponse
    //endregionOTP

    //region passwords
    @POST("passwords")
    suspend fun passwords(
        @Body request: ConsumerRequests.Passwords.CreateRequest
    ): ConsumerResponses.Passwords.PasswordsCreateResponse

    @POST("passwords/authenticate")
    suspend fun authenticateWithPasswords(
        @Body request: ConsumerRequests.Passwords.AuthenticateRequest
    ): ConsumerResponses.AuthenticateResponse

    @POST("passwords/email/reset/start")
    suspend fun resetByEmailStart(
        @Body request: ConsumerRequests.Passwords.ResetByEmailStartRequest
    ): CommonResponses.BasicResponse

    @POST("passwords/email/reset")
    suspend fun resetByEmail(
        @Body request: ConsumerRequests.Passwords.ResetByEmailRequest,
    ): ConsumerResponses.AuthenticateResponse

    @POST("passwords/strength_check")
    suspend fun strengthCheck(
        @Body request: ConsumerRequests.Passwords.StrengthCheckRequest
    ): CommonResponses.Passwords.PasswordsStrengthCheckResponse
    //endregion passwords

    // region biometrics
    @POST("biometrics/register/start")
    suspend fun biometricsRegisterStart(
        @Body request: ConsumerRequests.Biometrics.RegisterStartRequest
    ): CommonResponses.Biometrics.RegisterStartResponse

    @POST("biometrics/register")
    suspend fun biometricsRegister(
        @Body request: ConsumerRequests.Biometrics.RegisterRequest
    ): ConsumerResponses.Biometrics.RegisterResponse

    @POST("biometrics/authenticate/start")
    suspend fun biometricsAuthenticateStart(
        @Body request: ConsumerRequests.Biometrics.AuthenticateStartRequest
    ): CommonResponses.Biometrics.AuthenticateStartResponse

    @POST("biometrics/authenticate")
    suspend fun biometricsAuthenticate(
        @Body request: ConsumerRequests.Biometrics.AuthenticateRequest
    ): ConsumerResponses.Biometrics.AuthenticateResponse
    // endregion biometrics

    //region User Management
    @GET("users/me")
    suspend fun getUser(): ConsumerResponses.User.UserResponse

    @DELETE("users/emails/{id}")
    suspend fun deleteEmailById(@Path(value = "id") id: String): ConsumerResponses.User.DeleteFactorResponse

    @DELETE("users/phone_numbers/{id}")
    suspend fun deletePhoneNumberById(@Path(value = "id") id: String): ConsumerResponses.User.DeleteFactorResponse

    @DELETE("users/biometric_registrations/{id}")
    suspend fun deleteBiometricRegistrationById(@Path(value = "id") id: String):
        ConsumerResponses.User.DeleteFactorResponse

    @DELETE("users/crypto_wallets/{id}")
    suspend fun deleteCryptoWalletById(@Path(value = "id") id: String): ConsumerResponses.User.DeleteFactorResponse

    @DELETE("users/webauthn_registrations/{id}")
    suspend fun deleteWebAuthnById(@Path(value = "id") id: String): ConsumerResponses.User.DeleteFactorResponse
    //endregion User Management

    //region OAuth
    @POST("oauth/google/id_token/authenticate")
    suspend fun authenticateWithGoogleIdToken(
        @Body request: ConsumerRequests.OAuth.Google.AuthenticateRequest
    ): ConsumerResponses.AuthenticateResponse

    @POST("oauth/authenticate")
    suspend fun authenticateWithThirdPartyToken(
        @Body request: ConsumerRequests.OAuth.ThirdParty.AuthenticateRequest
    ): ConsumerResponses.OAuth.OAuthAuthenticateResponse
    //endregion OAuth
}
