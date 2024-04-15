package com.stytch.sdk.b2b.sso

import android.net.Uri
import com.stytch.sdk.b2b.B2BSSODeleteConnectionResponse
import com.stytch.sdk.b2b.B2BSSOGetConnectionsResponse
import com.stytch.sdk.b2b.B2BSSOOIDCCreateConnectionResponse
import com.stytch.sdk.b2b.B2BSSOOIDCUpdateConnectionResponse
import com.stytch.sdk.b2b.B2BSSOSAMLCreateConnectionResponse
import com.stytch.sdk.b2b.B2BSSOSAMLUpdateConnectionResponse
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
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers.x509Certificate

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

    override suspend fun deleteConnection(connectionId: String): B2BSSODeleteConnectionResponse =
        withContext(dispatchers.io) {
            api.deleteConnection(connectionId = connectionId)
        }

    override fun deleteConnection(
        connectionId: String,
        callback: (B2BSSODeleteConnectionResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            callback(deleteConnection(connectionId))
        }
    }

    override val saml: SSO.SAML = SAMLImpl()

    internal inner class SAMLImpl : SSO.SAML {
        override suspend fun createConnection(
            parameters: SSO.SAML.CreateParameters,
        ): B2BSSOSAMLCreateConnectionResponse =
            withContext(dispatchers.io) {
                api.samlCreateConnection(displayName = parameters.displayName)
            }

        override fun createConnection(
            parameters: SSO.SAML.CreateParameters,
            callback: (B2BSSOSAMLCreateConnectionResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                callback(createConnection(parameters))
            }
        }

        override suspend fun updateConnection(
            parameters: SSO.SAML.UpdateParameters,
        ): B2BSSOSAMLUpdateConnectionResponse =
            withContext(dispatchers.ui) {
                api.samlUpdateConnection(
                    connectionId = parameters.connectionId,
                    idpEntityId = parameters.idpEntityId,
                    displayName = parameters.displayName,
                    attributeMapping = parameters.attributeMapping,
                    idpSsoUrl = parameters.idpSsoUrl,
                    x509Certificate = parameters.x509Certificate,
                    samlConnectionImplicitRoleAssignment = parameters.samlConnectionImplicitRoleAssignment,
                    samlGroupImplicitRoleAssignment = parameters.samlGroupImplicitRoleAssignment,
                )
            }

        override fun updateConnection(
            parameters: SSO.SAML.UpdateParameters,
            callback: (B2BSSOSAMLUpdateConnectionResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                callback(updateConnection(parameters))
            }
        }
    }

    override val oidc: SSO.OIDC = OIDCImpl()

    internal inner class OIDCImpl : SSO.OIDC {
        override suspend fun createConnection(
            parameters: SSO.OIDC.CreateParameters,
        ): B2BSSOOIDCCreateConnectionResponse =
            withContext(dispatchers.io) {
                api.oidcCreateConnection(displayName = parameters.displayName)
            }

        override fun createConnection(
            parameters: SSO.OIDC.CreateParameters,
            callback: (B2BSSOOIDCCreateConnectionResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                callback(createConnection(parameters))
            }
        }

        override suspend fun updateConnection(
            parameters: SSO.OIDC.UpdateParameters,
        ): B2BSSOOIDCUpdateConnectionResponse =
            withContext(dispatchers.io) {
                api.oidcUpdateConnection(
                    connectionId = parameters.connectionId,
                    displayName = parameters.displayName,
                    issuer = parameters.issuer,
                    clientId = parameters.clientId,
                    clientSecret = parameters.clientSecret,
                    authorizationUrl = parameters.authorizationUrl,
                    tokenUrl = parameters.tokenUrl,
                    userInfoUrl = parameters.userInfoUrl,
                    jwksUrl = parameters.jwksUrl,
                )
            }

        override fun updateConnection(
            parameters: SSO.OIDC.UpdateParameters,
            callback: (B2BSSOOIDCUpdateConnectionResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                callback(updateConnection(parameters))
            }
        }
    }
}
