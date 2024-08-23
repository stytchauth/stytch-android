package com.stytch.sdk.consumer.oauth

import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.pkcePairManager.PKCEPairManager
import com.stytch.sdk.common.sso.SSOManagerActivity
import com.stytch.sdk.common.sso.SSOManagerActivity.Companion.URI_KEY
import com.stytch.sdk.common.utils.buildUri
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.network.StytchApi

internal class ThirdPartyOAuthImpl(
    private val pkcePairManager: PKCEPairManager,
    override val providerName: String,
) : OAuth.ThirdParty {
    override fun start(parameters: OAuth.ThirdParty.StartParameters) {
        val pkce = pkcePairManager.generateAndReturnPKCECodePair().codeChallenge
        val token = StytchApi.publicToken
        val host =
            StytchClient.bootstrapData.cnameDomain?.let {
                "https://$it/v1/"
            } ?: if (StytchApi.isTestToken) Constants.TEST_API_URL else Constants.LIVE_API_URL
        val baseUrl = "${host}public/oauth/$providerName/start"
        val potentialParameters =
            mapOf(
                "public_token" to token,
                "code_challenge" to pkce,
                "login_redirect_url" to parameters.loginRedirectUrl,
                "signup_redirect_url" to parameters.signupRedirectUrl,
                "custom_scopes" to parameters.customScopes,
            )
        val requestUri = buildUri(baseUrl, potentialParameters)
        val intent = SSOManagerActivity.createBaseIntent(parameters.context)
        intent.putExtra(URI_KEY, requestUri.toString())
        parameters.context.startActivityForResult(intent, parameters.oAuthRequestIdentifier)
    }
}
