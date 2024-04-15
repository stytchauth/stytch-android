package com.stytch.sdk.b2b.sso

import android.net.Uri
import com.stytch.sdk.b2b.B2BSSOGetConnectionsResponse
import com.stytch.sdk.b2b.SSOAuthenticateResponse
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchMissingPKCEError
import com.stytch.sdk.common.sso.SSOManagerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class SSOImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: B2BSessionStorage,
    private val storageHelper: StorageHelper,
    private val api: StytchB2BApi.SSO,
) : SSO {
    internal fun buildUri(
        host: String,
        parameters: Map<String, String?>,
    ): Uri =
        Uri.parse("${host}public/sso/start")
            .buildUpon()
            .apply {
                parameters.forEach {
                    if (it.value != null) {
                        appendQueryParameter(it.key, it.value)
                    }
                }
            }
            .build()

    override fun start(params: SSO.StartParams) {
        val host = if (StytchB2BApi.isTestToken) Constants.TEST_API_URL else Constants.LIVE_API_URL
        val potentialParameters =
            mapOf(
                "connection_id" to params.connectionId,
                "public_token" to StytchB2BApi.publicToken,
                "pkce_code_challenge" to storageHelper.generateHashedCodeChallenge().second,
                "login_redirect_url" to params.loginRedirectUrl,
                "signup_redirect_url" to params.signupRedirectUrl,
            )
        val requestUri = buildUri(host, potentialParameters)
        val intent = SSOManagerActivity.createBaseIntent(params.context)
        intent.putExtra(SSOManagerActivity.URI_KEY, requestUri.toString())
        params.context.startActivityForResult(intent, params.ssoAuthRequestIdentifier)
    }

    override suspend fun authenticate(params: SSO.AuthenticateParams): SSOAuthenticateResponse {
        val result: SSOAuthenticateResponse
        withContext(dispatchers.io) {
            val codeVerifier: String
            try {
                codeVerifier = storageHelper.retrieveCodeVerifier()!!
            } catch (ex: Exception) {
                result = StytchResult.Error(StytchMissingPKCEError(ex))
                return@withContext
            }
            result =
                api.authenticate(
                    ssoToken = params.ssoToken,
                    sessionDurationMinutes = params.sessionDurationMinutes,
                    codeVerifier = codeVerifier,
                ).apply {
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
        }
        return result
    }

    override fun authenticate(
        params: SSO.AuthenticateParams,
        callback: (SSOAuthenticateResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = authenticate(params)
            callback(result)
        }
    }

    override suspend fun getConnections(): B2BSSOGetConnectionsResponse =
        withContext(dispatchers.io) {
            api.getConnections()
        }

    override fun getConnections(callback: (B2BSSOGetConnectionsResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            callback(getConnections())
        }
    }
}
