package com.stytch.sdk.oauth

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.stytch.sdk.Constants
import com.stytch.sdk.StorageHelper
import com.stytch.sdk.network.StytchApi

internal class ThirdPartyOAuthImpl(
    private val storageHelper: StorageHelper,
    override val providerName: String,
) : OAuth.ThirdParty {
    private fun buildUri(host: String, parameters: Map<String, String?>, pkce: String, token: String): Uri {
        return Uri.parse("${host}public/oauth/$providerName/start")
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
    }

    override fun start(parameters: OAuth.ThirdParty.StartParameters) {
        val pkce = storageHelper.generateHashedCodeChallenge().second
        val token = StytchApi.publicToken
        val host = if (StytchApi.isTestToken) Constants.TEST_API_URL else Constants.LIVE_API_URL
        val potentialParameters = mapOf(
            "login_redirect_url" to parameters.loginRedirectUrl,
            "signup_redirect_url" to parameters.signupRedirectUrl,
            "custom_scopes" to parameters.customScopes?.joinToString(" ")
        )
        val requestUri = buildUri(host, potentialParameters, pkce, token)
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(parameters.context, requestUri)
    }
}
