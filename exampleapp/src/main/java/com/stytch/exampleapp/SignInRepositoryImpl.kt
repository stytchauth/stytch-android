package com.stytch.exampleapp

import com.stytch.sdk.BaseResponse
import com.stytch.sdk.LoginOrCreateUserByEmailResponse
import com.stytch.sdk.StytchClient
import javax.inject.Inject

class SignInRepositoryImpl @Inject constructor(var magicLinks: StytchClient.MagicLinks) : SignInRepository {

    override suspend fun authenticate(token: String, expirationTime: Int): BaseResponse {
        return magicLinks.authenticate(token = token, sessionExpirationMinutes = expirationTime)
    }

    override suspend fun loginOrCreate(params: StytchClient.MagicLinks.Parameters): LoginOrCreateUserByEmailResponse {
        return magicLinks.loginOrCreate(params)
    }

}
