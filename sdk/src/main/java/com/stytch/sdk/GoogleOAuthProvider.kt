package com.stytch.sdk

import android.app.Activity
import android.content.Context
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient

internal interface GoogleOAuthProvider {
    var nonce: String?
    var oneTapClient: SignInClient?
    fun createNonce(): String
    fun createSignInClient(context: Context): SignInClient
    fun createSignInClient(context: Activity): SignInClient
    fun getSignInRequest(clientId: String, nonce: String, autoSelectEnabled: Boolean): BeginSignInRequest
}

internal class GoogleOAuthProviderImpl : GoogleOAuthProvider {
    override var nonce: String? = null

    override var oneTapClient: SignInClient? = null

    override fun createNonce(): String {
        return EncryptionManager.encryptCodeChallenge(EncryptionManager.generateCodeChallenge()).also {
            nonce = it
        }
    }

    override fun createSignInClient(context: Context): SignInClient {
        return Identity.getSignInClient(context).also {
            oneTapClient = it
        }
    }

    override fun createSignInClient(context: Activity): SignInClient {
        return Identity.getSignInClient(context).also {
            oneTapClient = it
        }
    }

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
