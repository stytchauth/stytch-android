package com.stytch.sdk.consumer.oauth

import android.net.Uri
import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.pkcePairManager.PKCEPairManager
import com.stytch.sdk.common.sso.SSOManagerActivity
import com.stytch.sdk.common.sso.SSOManagerActivity.Companion.URI_KEY
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.network.StytchApi

internal class ThirdPartyOAuthImpl(
    private val pkcePairManager: PKCEPairManager,
    override val providerName: String,
) : OAuth.ThirdParty {
    internal fun buildUri(
        host: String,
        parameters: Map<String, String?>,
        pkce: String,
        token: String,
    ): Uri =
        Uri.parse("${host}public/oauth/$providerName/start")
            .buildUpon()
            .apply {
                appendQueryParameter("code_challenge", pkce)
                appendQueryParameter("public_token", token)
                parameters.forEach {
                    if (it.value != null) {
                        appendQueryParameter(it.key, it.value)
                    }
                }
            }
            .build()

    override fun start(parameters: OAuth.ThirdParty.StartParameters) {
        val pkce = pkcePairManager.generateAndReturnPKCECodePair().codeChallenge
        val token = StytchApi.publicToken
        val host =
            StytchClient.bootstrapData.cnameDomain?.let {
                "https://$it/v1/"
            } ?: if (StytchApi.isTestToken) Constants.TEST_API_URL else Constants.LIVE_API_URL
        val potentialParameters =
            mapOf(
                "login_redirect_url" to parameters.loginRedirectUrl,
                "signup_redirect_url" to parameters.signupRedirectUrl,
                "custom_scopes" to parameters.customScopes?.joinToString(" "),
            )
        val requestUri = buildUri(host, potentialParameters, pkce, token)
        val intent = SSOManagerActivity.createBaseIntent(parameters.context)
        intent.putExtra(URI_KEY, requestUri.toString())
        parameters.context.startActivityForResult(intent, parameters.oAuthRequestIdentifier)
    }
}
