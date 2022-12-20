package com.stytch.sdk

import android.app.Activity
import android.content.Context
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient

internal interface GoogleOAuthProvider {
    fun getSignInClient(context: Context): SignInClient
    fun getSignInClient(context: Activity): SignInClient
    fun getSignInRequest(clientId: String, nonce: String, autoSelectEnabled: Boolean): BeginSignInRequest
}

internal class GoogleOAuthProviderImpl : GoogleOAuthProvider {
    override fun getSignInClient(context: Context): SignInClient = Identity.getSignInClient(context)

    override fun getSignInClient(context: Activity): SignInClient = Identity.getSignInClient(context)

    override fun getSignInRequest(clientId: String, nonce: String, autoSelectEnabled: Boolean): BeginSignInRequest =
        BeginSignInRequest.builder().apply {
            setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(clientId)
                    .setFilterByAuthorizedAccounts(false)
                    .setNonce(nonce)
                    .build()
            )
            setAutoSelectEnabled(autoSelectEnabled)
        }.build()

}
