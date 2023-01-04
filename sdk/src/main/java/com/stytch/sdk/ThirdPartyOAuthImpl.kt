package com.stytch.sdk

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.stytch.sdk.network.StytchApi

internal class ThirdPartyOAuthImpl(
    private val storageHelper: StorageHelper,
    override val providerName: String,
) : OAuth.ThirdParty {
    override fun start(parameters: OAuth.ThirdParty.StartParameters) {
        val pkce = storageHelper.generateHashedCodeChallenge().second
        val token = StytchApi.publicToken
        val host = if (StytchApi.isTestToken) Constants.TEST_API_URL else Constants.LIVE_API_URL
        val baseUrl = "${host}public/oauth/$providerName/start"
        val potentialParameters = mapOf(
            "login_redirect_url" to parameters.loginRedirectUrl,
            "signup_redirect_url" to parameters.signupRedirectUrl,
            "custom_scopes" to parameters.customScopes?.joinToString(" ")
        )
        val requestUri = Uri.parse(baseUrl)
            .buildUpon()
            .apply {
                appendQueryParameter("code_challenge", pkce)
                appendQueryParameter("public_token", token)
                potentialParameters.forEach {
                    if (it.value != null) {
                        appendQueryParameter(it.key, it.value)
                    }
                }
            }
            .build()
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(parameters.context, requestUri)
    }
}
