package com.stytch.sdk.consumer.oauth

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import com.stytch.sdk.common.LIVE_API_URL
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.TEST_API_URL
import com.stytch.sdk.common.errors.NoActivityProvided
import com.stytch.sdk.common.pkcePairManager.PKCEPairManager
import com.stytch.sdk.common.sso.SSOManagerActivity
import com.stytch.sdk.common.sso.SSOManagerActivity.Companion.URI_KEY
import com.stytch.sdk.common.utils.buildUri
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.network.StytchApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.Continuation

internal class ThirdPartyOAuthImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val pkcePairManager: PKCEPairManager,
    override val providerName: String,
    private val getOAuthReceiver: (
        Continuation<StytchResult<String>>,
    ) -> Pair<ComponentActivity?, ActivityResultLauncher<Intent>?>,
) : OAuth.ThirdParty {
    override fun start(parameters: OAuth.ThirdParty.StartParameters) {
        val pkce = pkcePairManager.generateAndReturnPKCECodePair().codeChallenge
        val token = StytchApi.publicToken
        val host =
            StytchClient.configurationManager.bootstrapData.cnameDomain?.let {
                "https://$it/v1/"
            } ?: if (StytchApi.isTestToken) TEST_API_URL else LIVE_API_URL
        val baseUrl = "${host}public/oauth/$providerName/start"
        val potentialParameters =
            mapOf(
                "public_token" to token,
                "code_challenge" to pkce,
                "login_redirect_url" to parameters.loginRedirectUrl,
                "signup_redirect_url" to parameters.signupRedirectUrl,
                "custom_scopes" to parameters.customScopes,
                "provider_params" to parameters.providerParams,
            )
        val requestUri = buildUri(baseUrl, potentialParameters)
        val intent = SSOManagerActivity.createBaseIntent(parameters.context)
        intent.putExtra(URI_KEY, requestUri.toString())
        parameters.context.startActivityForResult(intent, parameters.oAuthRequestIdentifier)
    }

    override suspend fun getTokenForProvider(
        parameters: OAuth.ThirdParty.GetTokenForProviderParams,
    ): StytchResult<String> =
        suspendCancellableCoroutine { continuation ->
            getOAuthReceiver(continuation).let { (activity, launcher) ->
                if (activity == null || launcher == null) {
                    continuation.resume(
                        StytchResult.Error(NoActivityProvided),
                    ) { cause, _, _ -> continuation.cancel(cause) }
                    return@suspendCancellableCoroutine
                }
                val pkce = pkcePairManager.generateAndReturnPKCECodePair().codeChallenge
                val token = StytchApi.publicToken
                val host =
                    StytchClient.configurationManager.bootstrapData.cnameDomain?.let {
                        "https://$it/v1/"
                    } ?: if (StytchApi.isTestToken) TEST_API_URL else LIVE_API_URL
                val baseUrl = "${host}public/oauth/$providerName/start"
                val potentialParameters =
                    mapOf(
                        "public_token" to token,
                        "code_challenge" to pkce,
                        "login_redirect_url" to parameters.loginRedirectUrl,
                        "signup_redirect_url" to parameters.signupRedirectUrl,
                        "custom_scopes" to parameters.customScopes,
                        "provider_params" to parameters.providerParams,
                    )
                val requestUri = buildUri(baseUrl, potentialParameters)
                val intent = SSOManagerActivity.createBaseIntent(activity)
                intent.putExtra(URI_KEY, requestUri.toString())
                launcher.launch(intent)
            }
        }

    override fun getTokenForProvider(
        parameters: OAuth.ThirdParty.GetTokenForProviderParams,
        callback: (StytchResult<String>) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            callback(getTokenForProvider(parameters))
        }
    }

    override fun getTokenForProviderCompletable(
        parameters: OAuth.ThirdParty.GetTokenForProviderParams,
    ): CompletableFuture<StytchResult<String>> =
        externalScope
            .async {
                getTokenForProvider(parameters)
            }.asCompletableFuture()
}
