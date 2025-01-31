package com.stytch.sdk.consumer.oauth

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.stytch.sdk.common.QUERY_TOKEN
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.NoURIFound
import com.stytch.sdk.common.errors.StytchMissingPKCEError
import com.stytch.sdk.common.errors.UserCanceled
import com.stytch.sdk.common.pkcePairManager.PKCEPairManager
import com.stytch.sdk.consumer.OAuthAuthenticatedResponse
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.sessions.ConsumerSessionStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

internal class OAuthImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: ConsumerSessionStorage,
    private val api: StytchApi.OAuth,
    private val pkcePairManager: PKCEPairManager,
) : OAuth {
    private var oauthReceiverActivity: ComponentActivity? = null

    private var continuation: Continuation<StytchResult<String>>? = null
    private var launcher: ActivityResultLauncher<Intent>? = null

    private fun getOAuthReceiver(
        continuation: Continuation<StytchResult<String>>,
    ): Pair<ComponentActivity?, ActivityResultLauncher<Intent>?> {
        this.continuation = continuation
        return Pair(oauthReceiverActivity, launcher)
    }

    override fun setOAuthReceiverActivity(activity: ComponentActivity?) {
        oauthReceiverActivity = activity
        launcher =
            activity?.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val response =
                    when (result.resultCode) {
                        RESULT_OK -> {
                            result.data?.data?.getQueryParameter(QUERY_TOKEN)?.let {
                                StytchResult.Success(it)
                            } ?: StytchResult.Error(NoURIFound)
                        }

                        RESULT_CANCELED -> {
                            StytchResult.Error(UserCanceled)
                        }

                        else -> {
                            StytchResult.Error(UserCanceled) // TODO: Fix this
                        }
                    }
                continuation?.resume(response)
            }
    }

    override val googleOneTap: OAuth.GoogleOneTap =
        GoogleOneTapImpl(
            externalScope,
            dispatchers,
            sessionStorage,
            api,
            GoogleCredentialManagerProviderImpl(),
            pkcePairManager,
        )
    override val apple: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "apple",
            ::getOAuthReceiver,
        )
    override val amazon: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "amazon",
            ::getOAuthReceiver,
        )
    override val bitbucket: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "bitbucket",
            ::getOAuthReceiver,
        )
    override val coinbase: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "coinbase",
            ::getOAuthReceiver,
        )
    override val discord: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "discord",
            ::getOAuthReceiver,
        )
    override val facebook: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "facebook",
            ::getOAuthReceiver,
        )
    override val figma: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "figma",
            ::getOAuthReceiver,
        )
    override val github: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "github",
            ::getOAuthReceiver,
        )
    override val gitlab: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "gitlab",
            ::getOAuthReceiver,
        )
    override val google: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "google",
            ::getOAuthReceiver,
        )
    override val linkedin: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "linkedin",
            ::getOAuthReceiver,
        )
    override val microsoft: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "microsoft",
            ::getOAuthReceiver,
        )
    override val salesforce: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "salesforce",
            ::getOAuthReceiver,
        )
    override val slack: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "slack",
            ::getOAuthReceiver,
        )
    override val snapchat: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "snapchat",
            ::getOAuthReceiver,
        )
    override val tiktok: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "tiktok",
            ::getOAuthReceiver,
        )
    override val twitch: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "twitch",
            ::getOAuthReceiver,
        )
    override val twitter: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "twitter",
            ::getOAuthReceiver,
        )
    override val yahoo: OAuth.ThirdParty =
        ThirdPartyOAuthImpl(
            externalScope,
            dispatchers,
            pkcePairManager,
            providerName = "yahoo",
            ::getOAuthReceiver,
        )

    override suspend fun authenticate(parameters: OAuth.ThirdParty.AuthenticateParameters): OAuthAuthenticatedResponse {
        return withContext(dispatchers.io) {
            val pkce =
                pkcePairManager.getPKCECodePair()?.codeVerifier
                    ?: run {
                        StytchClient.events.logEvent("oauth_failure", null, StytchMissingPKCEError(null))
                        return@withContext StytchResult.Error(StytchMissingPKCEError(null))
                    }
            api
                .authenticateWithThirdPartyToken(
                    token = parameters.token,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                    codeVerifier = pkce,
                ).apply {
                    pkcePairManager.clearPKCECodePair()
                    when (this) {
                        is StytchResult.Success -> StytchClient.events.logEvent("oauth_success")
                        is StytchResult.Error -> StytchClient.events.logEvent("oauth_failure", null, this.exception)
                    }
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
        }
    }

    override fun authenticate(
        parameters: OAuth.ThirdParty.AuthenticateParameters,
        callback: (OAuthAuthenticatedResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = authenticate(parameters)
            callback(result)
        }
    }

    override fun authenticateCompletable(
        parameters: OAuth.ThirdParty.AuthenticateParameters,
    ): CompletableFuture<OAuthAuthenticatedResponse> =
        externalScope
            .async {
                authenticate(parameters)
            }.asCompletableFuture()
}
