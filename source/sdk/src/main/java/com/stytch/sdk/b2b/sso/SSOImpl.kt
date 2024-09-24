package com.stytch.sdk.b2b.sso

import com.stytch.sdk.b2b.B2BSSODeleteConnectionResponse
import com.stytch.sdk.b2b.B2BSSOGetConnectionsResponse
import com.stytch.sdk.b2b.B2BSSOOIDCCreateConnectionResponse
import com.stytch.sdk.b2b.B2BSSOOIDCUpdateConnectionResponse
import com.stytch.sdk.b2b.B2BSSOSAMLCreateConnectionResponse
import com.stytch.sdk.b2b.B2BSSOSAMLDeleteVerificationCertificateResponse
import com.stytch.sdk.b2b.B2BSSOSAMLUpdateConnectionByURLResponse
import com.stytch.sdk.b2b.B2BSSOSAMLUpdateConnectionResponse
import com.stytch.sdk.b2b.SSOAuthenticateResponse
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.LIVE_API_URL
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.TEST_API_URL
import com.stytch.sdk.common.errors.StytchMissingPKCEError
import com.stytch.sdk.common.pkcePairManager.PKCEPairManager
import com.stytch.sdk.common.sso.SSOManagerActivity
import com.stytch.sdk.common.utils.buildUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

internal class SSOImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: B2BSessionStorage,
    private val api: StytchB2BApi.SSO,
    private val pkcePairManager: PKCEPairManager,
) : SSO {
    override fun start(params: SSO.StartParams) {
        val host =
            StytchB2BClient.bootstrapData.cnameDomain?.let {
                "https://$it/v1/"
            } ?: if (StytchB2BApi.isTestToken) TEST_API_URL else LIVE_API_URL
        val potentialParameters =
            mapOf(
                "connection_id" to params.connectionId,
                "public_token" to StytchB2BApi.publicToken,
                "pkce_code_challenge" to pkcePairManager.generateAndReturnPKCECodePair().codeChallenge,
                "login_redirect_url" to params.loginRedirectUrl,
                "signup_redirect_url" to params.signupRedirectUrl,
            )
        val requestUri = buildUri("${host}public/sso/start", potentialParameters)
        val intent = SSOManagerActivity.createBaseIntent(params.context)
        intent.putExtra(SSOManagerActivity.URI_KEY, requestUri.toString())
        params.context.startActivityForResult(intent, params.ssoAuthRequestIdentifier)
    }

    override suspend fun authenticate(params: SSO.AuthenticateParams): SSOAuthenticateResponse {
        val codeVerifier =
            pkcePairManager.getPKCECodePair()?.codeVerifier ?: return StytchResult.Error(StytchMissingPKCEError(null))
        return withContext(dispatchers.io) {
            api
                .authenticate(
                    ssoToken = params.ssoToken,
                    sessionDurationMinutes = params.sessionDurationMinutes,
                    codeVerifier = codeVerifier,
                    intermediateSessionToken = sessionStorage.intermediateSessionToken,
                    locale = params.locale,
                ).apply {
                    pkcePairManager.clearPKCECodePair()
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
        }
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

    override fun authenticateCompletable(params: SSO.AuthenticateParams): CompletableFuture<SSOAuthenticateResponse> =
        externalScope
            .async {
                authenticate(params)
            }.asCompletableFuture()

    override suspend fun getConnections(): B2BSSOGetConnectionsResponse =
        withContext(dispatchers.io) {
            api.getConnections()
        }

    override fun getConnections(callback: (B2BSSOGetConnectionsResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            callback(getConnections())
        }
    }

    override fun getConnectionsCompletable(): CompletableFuture<B2BSSOGetConnectionsResponse> =
        externalScope
            .async {
                getConnections()
            }.asCompletableFuture()

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

    override fun deleteConnectionCompletable(connectionId: String): CompletableFuture<B2BSSODeleteConnectionResponse> =
        externalScope
            .async {
                deleteConnection(connectionId)
            }.asCompletableFuture()

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

        override fun createConnectionCompletable(
            parameters: SSO.SAML.CreateParameters,
        ): CompletableFuture<B2BSSOSAMLCreateConnectionResponse> =
            externalScope
                .async {
                    createConnection(parameters)
                }.asCompletableFuture()

        override suspend fun updateConnection(
            parameters: SSO.SAML.UpdateParameters,
        ): B2BSSOSAMLUpdateConnectionResponse =
            withContext(dispatchers.io) {
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

        override fun updateConnectionCompletable(
            parameters: SSO.SAML.UpdateParameters,
        ): CompletableFuture<B2BSSOSAMLUpdateConnectionResponse> =
            externalScope
                .async {
                    updateConnection(parameters)
                }.asCompletableFuture()

        override suspend fun updateConnectionByUrl(
            parameters: SSO.SAML.UpdateByURLParameters,
        ): B2BSSOSAMLUpdateConnectionByURLResponse =
            withContext(dispatchers.io) {
                api.samlUpdateByUrl(
                    connectionId = parameters.connectionId,
                    metadataUrl = parameters.metadataUrl,
                )
            }

        override fun updateConnectionByUrl(
            parameters: SSO.SAML.UpdateByURLParameters,
            callback: (B2BSSOSAMLUpdateConnectionByURLResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                callback(updateConnectionByUrl(parameters))
            }
        }

        override fun updateConnectionByUrlCompletable(
            parameters: SSO.SAML.UpdateByURLParameters,
        ): CompletableFuture<B2BSSOSAMLUpdateConnectionByURLResponse> =
            externalScope
                .async {
                    updateConnectionByUrl(parameters)
                }.asCompletableFuture()

        override suspend fun deleteVerificationCertificate(
            parameters: SSO.SAML.DeleteVerificationCertificateParameters,
        ): B2BSSOSAMLDeleteVerificationCertificateResponse =
            withContext(dispatchers.io) {
                api.samlDeleteVerificationCertificate(
                    connectionId = parameters.connectionId,
                    certificateId = parameters.certificateId,
                )
            }

        override fun deleteVerificationCertificate(
            parameters: SSO.SAML.DeleteVerificationCertificateParameters,
            callback: (B2BSSOSAMLDeleteVerificationCertificateResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                callback(deleteVerificationCertificate(parameters))
            }
        }

        override fun deleteVerificationCertificateCompletable(
            parameters: SSO.SAML.DeleteVerificationCertificateParameters,
        ): CompletableFuture<B2BSSOSAMLDeleteVerificationCertificateResponse> =
            externalScope
                .async {
                    deleteVerificationCertificate(parameters)
                }.asCompletableFuture()
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

        override fun createConnectionCompletable(
            parameters: SSO.OIDC.CreateParameters,
        ): CompletableFuture<B2BSSOOIDCCreateConnectionResponse> =
            externalScope
                .async {
                    createConnection(parameters)
                }.asCompletableFuture()

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

        override fun updateConnectionCompletable(
            parameters: SSO.OIDC.UpdateParameters,
        ): CompletableFuture<B2BSSOOIDCUpdateConnectionResponse> =
            externalScope
                .async {
                    updateConnection(parameters)
                }.asCompletableFuture()
    }
}
