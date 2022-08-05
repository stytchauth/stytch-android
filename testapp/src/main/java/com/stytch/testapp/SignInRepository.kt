package com.stytch.testapp

import com.stytch.sdk.StytchClient
import javax.inject.Inject

class SignInRepository {

    @Inject
    lateinit var stytchClient: StytchClient

    @Inject
    lateinit var magicLinks: StytchClient.MagicLinks

    suspend fun authenticate(token: String, expirationTime: Int) {
        magicLinks.authenticate(token = token, sessionExpirationMinutes = expirationTime)
    }

    suspend fun loginOrCreate(params: StytchClient.MagicLinks.Parameters) {
        magicLinks.loginOrCreate(params)
    }

}
