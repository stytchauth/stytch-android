package com.stytch.exampleapp.b2b

import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.sso.SSO
import com.stytch.sdk.common.DeeplinkHandledStatus
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.sso.SSOError
import com.stytch.sdk.consumer.StytchClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.random.Random

class SSOViewModel : ViewModel() {
    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String>
        get() = _currentResponse

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    var ssoConnectionId by mutableStateOf(TextFieldValue(""))
    var metadataUrl by mutableStateOf(TextFieldValue(""))
    var certificateId by mutableStateOf(TextFieldValue(""))

    fun getConnections() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value = StytchB2BClient.sso.getConnections().toFriendlyDisplay()
        }
    }

    fun deleteConnection() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.sso
                    .deleteConnection(
                        connectionId = ssoConnectionId.text,
                    ).toFriendlyDisplay()
        }
    }

    fun createSamlConnection() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.sso.saml
                    .createConnection(
                        SSO.SAML.CreateParameters(
                            displayName = "Cool New SAML Connection ${Random(Date().time).nextInt()}",
                        ),
                    ).toFriendlyDisplay()
        }
    }

    fun updateSamlConnection() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.sso.saml
                    .updateConnection(
                        SSO.SAML.UpdateParameters(
                            connectionId = ssoConnectionId.text,
                            displayName = "Updated SAML Connection Name",
                        ),
                    ).toFriendlyDisplay()
        }
    }

    fun updateSamlConnectionByUrl() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.sso.saml
                    .updateConnectionByUrl(
                        SSO.SAML.UpdateByURLParameters(
                            connectionId = ssoConnectionId.text,
                            metadataUrl = metadataUrl.text,
                        ),
                    ).toFriendlyDisplay()
        }
    }

    fun deleteVerificationCertificate() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.sso.saml
                    .deleteVerificationCertificate(
                        SSO.SAML.DeleteVerificationCertificateParameters(
                            connectionId = ssoConnectionId.text,
                            certificateId = certificateId.text,
                        ),
                    ).toFriendlyDisplay()
        }
    }

    fun createOidcConnection() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.sso.oidc
                    .createConnection(
                        SSO.OIDC.CreateParameters(
                            displayName = "Cool New OIDC Connection ${Random(Date().time).nextInt()}",
                        ),
                    ).toFriendlyDisplay()
        }
    }

    fun updateOidcConnection() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.sso.oidc
                    .updateConnection(
                        SSO.OIDC.UpdateParameters(
                            connectionId = ssoConnectionId.text,
                            displayName = "Updated OIDC Connection Name",
                        ),
                    ).toFriendlyDisplay()
        }
    }

    fun startSSO(context: Activity) {
        val params =
            SSO.StartParams(
                context = context,
                ssoAuthRequestIdentifier = SSO_REQUEST_ID,
                connectionId = ssoConnectionId.text,
            )
        StytchB2BClient.sso.start(params)
    }

    fun startSSOOneShot() {
        viewModelScope.launchAndToggleLoadingState {
            val response =
                StytchB2BClient.sso.getTokenForProvider(
                    SSO.GetTokenForProviderParams(connectionId = ssoConnectionId.text),
                )
            _currentResponse.value =
                when (response) {
                    is StytchResult.Success -> {
                        StytchB2BClient.sso
                            .authenticate(
                                SSO.AuthenticateParams(
                                    ssoToken = response.value,
                                    sessionDurationMinutes = 30,
                                ),
                            ).toFriendlyDisplay()
                    }
                    is StytchResult.Error -> response.toFriendlyDisplay()
                }
        }
    }

    fun authenticateSSO(
        resultCode: Int,
        intent: Intent?,
    ) {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        intent?.data?.let {
                            when (val result = StytchClient.handle(it, 60)) {
                                is DeeplinkHandledStatus.NotHandled -> result.reason.message
                                is DeeplinkHandledStatus.Handled -> result.response.result.toFriendlyDisplay()
                                // This only happens for password reset deeplinks
                                is DeeplinkHandledStatus.ManualHandlingRequired -> ""
                            }
                        } ?: "No URI found"
                    }
                    else -> {
                        intent?.extras?.getSerializable(SSOError.SSO_EXCEPTION)?.let {
                            when (it as SSOError) {
                                is SSOError.UserCanceled -> "User Canceled"
                                is SSOError.NoBrowserFound,
                                is SSOError.NoURIFound,
                                -> it.message
                            }
                        } ?: "Unknown Error"
                    }
                }
        }
    }

    private fun CoroutineScope.launchAndToggleLoadingState(block: suspend () -> Unit): DisposableHandle =
        launch {
            _loadingState.value = true
            block()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
}
