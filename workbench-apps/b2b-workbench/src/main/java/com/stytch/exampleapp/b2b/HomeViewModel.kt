package com.stytch.exampleapp.b2b

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.magicLinks.B2BMagicLinks
import com.stytch.sdk.b2b.searchManager.SearchManager
import com.stytch.sdk.b2b.sessions.B2BSessions
import com.stytch.sdk.common.DeeplinkHandledStatus
import com.stytch.sdk.common.DeeplinkResponse
import com.stytch.sdk.common.StytchResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String>
        get() = _currentResponse

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    private val _intermediateSessionToken = MutableStateFlow("")
    val intermediateSessionToken: StateFlow<String>
        get() = _intermediateSessionToken

    var orgIdState by mutableStateOf(TextFieldValue(BuildConfig.STYTCH_B2B_ORG_ID))
    var emailState by mutableStateOf(TextFieldValue(""))
    var orgSlugState by mutableStateOf(TextFieldValue(""))
    var showEmailError by mutableStateOf(false)
    private val emailIsValid
        get() = isValidEmail(emailState.text)

    fun getOrganizationDetails() {
        _loadingState.value = true
        viewModelScope
            .launch {
                val details = StytchB2BClient.organization.get()
                _currentResponse.value = details.toFriendlyDisplay()
            }.invokeOnCompletion {
                _loadingState.value = false
            }
    }

    fun sendEmailMagicLink() {
        if (emailIsValid) {
            showEmailError = false
            viewModelScope
                .launch {
                    _loadingState.value = true
                    _currentResponse.value =
                        StytchB2BClient.magicLinks.email
                            .loginOrSignup(
                                B2BMagicLinks.EmailMagicLinks.Parameters(
                                    email = emailState.text,
                                    organizationId = orgIdState.text,
                                    loginRedirectUrl = "app://b2bexampleapp.com",
                                    signupRedirectUrl = "app://b2bexampleapp.com",
                                ),
                            ).toFriendlyDisplay()
                }.invokeOnCompletion {
                    _loadingState.value = false
                }
        } else {
            showEmailError = true
        }
    }

    fun sendDiscoveryMagicLink() {
        if (emailIsValid) {
            showEmailError = false
            viewModelScope
                .launch {
                    _loadingState.value = true
                    _currentResponse.value =
                        StytchB2BClient.magicLinks.email
                            .discoverySend(
                                B2BMagicLinks.EmailMagicLinks.DiscoverySendParameters(
                                    emailAddress = emailState.text,
                                ),
                            ).toFriendlyDisplay()
                }.invokeOnCompletion {
                    _loadingState.value = false
                }
        } else {
            showEmailError = true
        }
    }

    fun sendInviteMagicLink() {
        if (emailIsValid) {
            showEmailError = false
            viewModelScope
                .launch {
                    _loadingState.value = true
                    _currentResponse.value =
                        StytchB2BClient.magicLinks.email
                            .invite(
                                B2BMagicLinks.EmailMagicLinks.InviteParameters(
                                    emailAddress = emailState.text,
                                ),
                            ).toFriendlyDisplay()
                }.invokeOnCompletion {
                    _loadingState.value = false
                }
        } else {
            showEmailError = true
        }
    }

    fun revokeSession() {
        viewModelScope
            .launch {
                _loadingState.value = true
                _currentResponse.value = StytchB2BClient.sessions.revoke().toFriendlyDisplay()
            }.invokeOnCompletion {
                _loadingState.value = false
            }
    }

    fun exchangeSession() {
        viewModelScope
            .launch {
                _loadingState.value = true
                _currentResponse.value =
                    StytchB2BClient.sessions
                        .exchange(
                            B2BSessions.ExchangeParameters(
                                organizationId = orgIdState.text,
                                sessionDurationMinutes = 30,
                            ),
                        ).toFriendlyDisplay()
            }.invokeOnCompletion {
                _loadingState.value = false
            }
    }

    fun handleUri(uri: Uri) {
        viewModelScope
            .launch {
                _loadingState.value = true
                val result = StytchB2BClient.handle(uri = uri, sessionDurationMinutes = 60)
                _currentResponse.value =
                    when (result) {
                        is DeeplinkHandledStatus.NotHandled -> result.reason.message
                        is DeeplinkHandledStatus.Handled -> {
                            // Hacking this in for organization discovery stuff
                            (result.response as? DeeplinkResponse.Discovery)
                                ?.let {
                                    (it.result as? StytchResult.Success)?.value?.let { response ->
                                        _intermediateSessionToken.value = response.intermediateSessionToken
                                    }
                                }
                            result.response.result.toFriendlyDisplay()
                        }
                        // This only happens for password reset deeplinks
                        is DeeplinkHandledStatus.ManualHandlingRequired ->
                            "Password reset token retrieved, initiate password reset flow"
                    }
            }.invokeOnCompletion {
                _loadingState.value = false
            }
    }

    fun searchOrganizationBySlug() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.searchManager
                    .searchOrganization(
                        SearchManager.SearchOrganizationParameters(
                            organizationSlug = orgSlugState.text,
                        ),
                    ).toFriendlyDisplay()
        }
    }

    fun searchOrganizationMembers() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.searchManager
                    .searchMember(
                        SearchManager.SearchMemberParameters(
                            emailAddress = emailState.text,
                            organizationId = orgIdState.text,
                        ),
                    ).toFriendlyDisplay()
        }
    }

    fun getPKCECodePair() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value = StytchB2BClient.getPKCECodePair().toString()
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
