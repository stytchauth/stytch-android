package com.stytch.exampleapp.b2b.ui.headless.sso

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.sso.SSO
import com.stytch.sdk.common.StytchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SSOScreenViewModel(
    private val reportState: (HeadlessMethodResponseState) -> Unit,
) : ViewModel() {
    fun handle(action: SSOAction) =
        when (action) {
            is SSOAction.Authenticate -> startAndAuthenticateSSO(action.connectionId, action.publicToken)
            is SSOAction.CreateOIDCConnection -> createOIDCConnection(action.displayName)
            is SSOAction.CreateSAMLConnection -> createSAMLConnection(action.displayName)
            is SSOAction.DeleteConnection -> deleteConnection(action.connectionId)
            is SSOAction.DeleteVerificationCertificate -> deleteCertificate(action.connectionId, action.certificateId)
            SSOAction.GetConnections -> getConnections()
            is SSOAction.UpdateOIDCConnection -> updateOIDCConnection(action.connectionId, action.displayName)
            is SSOAction.UpdateSAMLConnection -> updateSAMLConnection(action.connectionId, action.displayName)
            is SSOAction.UpdateSAMLConnectionByURL -> updateSAMLConnectionByUrl(action.connectionId, action.metadataUrl)
        }

    private fun startAndAuthenticateSSO(
        connectionId: String,
        publicToken: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val tokenResponse =
                StytchB2BClient.sso.getTokenForProvider(
                    SSO.GetTokenForProviderParams(
                        connectionId = connectionId,
                        loginRedirectUrl = "$publicToken://oauth",
                        signupRedirectUrl = "$publicToken://oauth",
                    ),
                )
            when (tokenResponse) {
                is StytchResult.Success -> {
                    val authenticateResponse =
                        StytchB2BClient.sso
                            .authenticate(
                                SSO.AuthenticateParams(
                                    ssoToken = tokenResponse.value,
                                ),
                            )
                    reportState(HeadlessMethodResponseState.Response(authenticateResponse))
                }
                is StytchResult.Error -> {
                    reportState(HeadlessMethodResponseState.Response(tokenResponse))
                }
            }
        }
    }

    private fun createOIDCConnection(displayName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.sso.oidc.createConnection(
                    SSO.OIDC.CreateParameters(
                        displayName = displayName,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun createSAMLConnection(displayName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.sso.saml.createConnection(
                    SSO.SAML.CreateParameters(
                        displayName = displayName,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun deleteConnection(connectionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response = StytchB2BClient.sso.deleteConnection(connectionId)
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun deleteCertificate(
        connectionId: String,
        certificateId: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.sso.saml.deleteVerificationCertificate(
                    SSO.SAML.DeleteVerificationCertificateParameters(
                        connectionId = connectionId,
                        certificateId = certificateId,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun getConnections() {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response = StytchB2BClient.sso.getConnections()
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun updateOIDCConnection(
        connectionId: String,
        displayName: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.sso.oidc.updateConnection(
                    SSO.OIDC.UpdateParameters(
                        connectionId = connectionId,
                        displayName = displayName,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun updateSAMLConnection(
        connectionId: String,
        displayName: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.sso.saml.updateConnection(
                    SSO.SAML.UpdateParameters(
                        connectionId = connectionId,
                        displayName = displayName,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun updateSAMLConnectionByUrl(
        connectionId: String,
        metadataUrl: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.sso.saml.updateConnectionByUrl(
                    SSO.SAML.UpdateByURLParameters(
                        connectionId = connectionId,
                        metadataUrl = metadataUrl,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }
}

sealed interface SSOAction {
    data object GetConnections : SSOAction

    data class DeleteConnection(
        val connectionId: String,
    ) : SSOAction

    data class CreateSAMLConnection(
        val displayName: String,
    ) : SSOAction

    data class UpdateSAMLConnection(
        val connectionId: String,
        val displayName: String,
    ) : SSOAction

    data class UpdateSAMLConnectionByURL(
        val connectionId: String,
        val metadataUrl: String,
    ) : SSOAction

    data class DeleteVerificationCertificate(
        val connectionId: String,
        val certificateId: String,
    ) : SSOAction

    data class CreateOIDCConnection(
        val displayName: String,
    ) : SSOAction

    data class UpdateOIDCConnection(
        val connectionId: String,
        val displayName: String,
    ) : SSOAction

    data class Authenticate(
        val connectionId: String,
        val publicToken: String,
    ) : SSOAction
}
