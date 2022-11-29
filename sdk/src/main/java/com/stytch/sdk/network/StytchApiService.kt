package com.stytch.sdk.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

@Suppress("TooManyFunctions")
internal interface StytchApiService {

    //region Magic Links
    @POST("magic_links/email/login_or_create")
    suspend fun loginOrCreateUserByEmail(
        @Body request: StytchRequests.MagicLinks.Email.LoginOrCreateUserRequest
    ): StytchResponses.MagicLinks.Email.LoginOrCreateUserResponse

    @POST("magic_links/authenticate")
    suspend fun authenticate(
        @Body request: StytchRequests.MagicLinks.AuthenticateRequest
    ): StytchResponses.AuthenticateResponse
    //endregion Magic Links

    //region Sessions
    @POST("sessions/authenticate")
    suspend fun authenticateSessions(
        @Body request: StytchRequests.Sessions.AuthenticateRequest
    ): StytchResponses.AuthenticateResponse

    @POST("sessions/revoke")
    suspend fun revokeSessions(): StytchResponses.Sessions.RevokeResponse
    //endregion Sessions

    //region OTP
    @POST("otps/sms/login_or_create")
    suspend fun loginOrCreateUserByOTPWithSMS(
        @Body request: StytchRequests.OTP.SMS
    ): StytchResponses.LoginOrCreateOTPResponse

    @POST("otps/whatsapp/login_or_create")
    suspend fun loginOrCreateUserByOTPWithWhatsApp(
        @Body request: StytchRequests.OTP.WhatsApp
    ): StytchResponses.LoginOrCreateOTPResponse

    @POST("otps/email/login_or_create")
    suspend fun loginOrCreateUserByOTPWithEmail(
        @Body request: StytchRequests.OTP.Email
    ): StytchResponses.LoginOrCreateOTPResponse

    @POST("otps/authenticate") // TODO Need to create a proper name to differentiate fom magiclinks authenticate
    suspend fun authenticateWithOTP(
        @Body request: StytchRequests.OTP.Authenticate
    ): StytchResponses.AuthenticateResponse
    //endregionOTP

    //region passwords
    @POST("passwords")
    suspend fun passwords(
        @Body request: StytchRequests.Passwords.CreateRequest
    ): StytchResponses.Passwords.PasswordsCreateResponse

    @POST("passwords/authenticate")
    suspend fun authenticateWithPasswords(
        @Body request: StytchRequests.Passwords.AuthenticateRequest
    ): StytchResponses.AuthenticateResponse

    @POST("passwords/email/reset/start")
    suspend fun resetByEmailStart(
        @Body request: StytchRequests.Passwords.ResetByEmailStartRequest
    ): StytchResponses.BasicResponse

    @POST("passwords/email/reset")
    suspend fun resetByEmail(
        @Body request: StytchRequests.Passwords.RestByEmailRequest
    ): StytchResponses.AuthenticateResponse

    @POST("passwords/strength_check")
    suspend fun strengthCheck(
        @Body request: StytchRequests.Passwords.StrengthCheckRequest
    ): StytchResponses.Passwords.PasswordsStrengthCheckResponse
    //endregion passwords

    //region User Management
    @GET("users/me")
    suspend fun getUser(): StytchResponses.User.UserResponse

    @DELETE("users/emails/{id}")
    suspend fun deleteEmailById(@Path(value ="id") id: String): StytchResponses.BasicResponse

    @DELETE("users/phone_numbers/{id}")
    suspend fun deletePhoneNumberById(@Path(value = "id") id: String): StytchResponses.BasicResponse

    @DELETE("users/biometric_registrations/{id}")
    suspend fun deleteBiometricRegistrationById(@Path(value = "id") id: String): StytchResponses.BasicResponse

    //endregion User Management
}