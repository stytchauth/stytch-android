package com.stytch.exampleapp.b2b.ui.headless.oauth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.oauth.OAuth
import com.stytch.sdk.common.StytchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OAuthScreenViewModel(
    private val reportState: (HeadlessMethodResponseState) -> Unit,
) : ViewModel() {
    fun handle(action: OAuthAction) =
        when (action) {
            is OAuthAction.GoogleDiscoveryFlow -> googleDiscoveryStart(action.publicToken)
            is OAuthAction.GoogleOrganizationFlow -> googleOrgStart(action.publicToken, action.organizationId)
        }

    private fun googleDiscoveryStart(publicToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val tokenResponse =
                StytchB2BClient.oauth.google.discovery.getTokenForProvider(
                    OAuth.ProviderDiscovery.GetTokenForProviderParams(
                        discoveryRedirectUrl = "$publicToken://oauth",
                    ),
                )
            when (tokenResponse) {
                is StytchResult.Success -> {
                    val authenticateResponse =
                        StytchB2BClient.oauth.discovery
                            .authenticate(
                                OAuth.Discovery.DiscoveryAuthenticateParameters(
                                    discoveryOauthToken = tokenResponse.value,
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

    private fun googleOrgStart(
        publicToken: String,
        organizationId: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val tokenResponse =
                StytchB2BClient.oauth.google.getTokenForProvider(
                    OAuth.Provider.GetTokenForProviderParams(
                        organizationId = organizationId,
                        loginRedirectUrl = "$publicToken://oauth",
                        signupRedirectUrl = "$publicToken://oauth",
                    ),
                )
            when (tokenResponse) {
                is StytchResult.Success -> {
                    val authenticateResponse =
                        StytchB2BClient.oauth.authenticate(
                            OAuth.AuthenticateParameters(oauthToken = tokenResponse.value),
                        )
                    reportState(HeadlessMethodResponseState.Response(authenticateResponse))
                }
                is StytchResult.Error -> {
                    reportState(HeadlessMethodResponseState.Response(tokenResponse))
                }
            }
        }
    }
}

sealed interface OAuthAction {
    data class GoogleOrganizationFlow(
        val publicToken: String,
        val organizationId: String,
    ) : OAuthAction

    data class GoogleDiscoveryFlow(
        val publicToken: String,
    ) : OAuthAction
}
