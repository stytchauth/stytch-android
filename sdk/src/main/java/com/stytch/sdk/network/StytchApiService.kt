package com.stytch.sdk.network

import retrofit2.http.Body
import retrofit2.http.POST

internal interface StytchApiService {

    @POST("magic_links/email/login_or_create")
    suspend fun loginOrCreateUserByEmail(
        @Body request: StytchRequests.MagicLinks.Email.LoginOrCreateUserRequest,
    ): StytchResponses.MagicLinks.Email.LoginOrCreateUserResponse

    @POST("magic_links/authenticate")
    suspend fun authenticate(
        @Body request: StytchRequests.MagicLinks.AuthenticateRequest
    ): StytchResponses.BasicResponse

    @POST("otps/sms/login_or_create")
    suspend fun loginOrCreateUserByOTPWithSMS(
        @Body request: StytchRequests.OTP.SMS,
    ): StytchResponses.BasicResponse

    @POST("otps/whatsapp/login_or_create")
    suspend fun loginOrCreateUserByOTPWithWhatsapp(
        @Body request: StytchRequests.OTP.Whatsapp,
    ): StytchResponses.BasicResponse

    @POST("otps/email/login_or_create")
    suspend fun loginOrCreateUserByOTPWithEmail(
        @Body request: StytchRequests.OTP.Email,
    ): StytchResponses.BasicResponse

    @POST("otps/authenticate") // TODO Need to create a proper name to differentiate fom magiclinks authenticate
    suspend fun authenticateWithOTP(
        @Body request: StytchRequests.OTP.Authenticate,
    ): StytchResponses.BasicResponse
}
