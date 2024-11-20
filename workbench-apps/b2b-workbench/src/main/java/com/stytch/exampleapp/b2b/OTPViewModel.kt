package com.stytch.exampleapp.b2b

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.SetMFAEnrollment
import com.stytch.sdk.b2b.otp.OTP
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OTPViewModel : ViewModel() {
    var orgIdState by mutableStateOf(TextFieldValue(BuildConfig.STYTCH_B2B_ORG_ID))
    var memberIdState by mutableStateOf(TextFieldValue(""))
    var phoneNumberState by mutableStateOf(TextFieldValue(""))
    var codeState by mutableStateOf(TextFieldValue(""))
    var memberEmailState by mutableStateOf(TextFieldValue(""))

    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String>
        get() = _currentResponse

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    fun send() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.otp.sms
                    .send(
                        OTP.SMS.SendParameters(
                            organizationId = orgIdState.text,
                            memberId = memberIdState.text,
                            mfaPhoneNumber = phoneNumberState.text,
                        ),
                    ).toFriendlyDisplay()
        }
    }

    fun authenticate() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.otp.sms
                    .authenticate(
                        OTP.SMS.AuthenticateParameters(
                            organizationId = orgIdState.text,
                            memberId = memberIdState.text,
                            code = codeState.text,
                            setMFAEnrollment = SetMFAEnrollment.ENROLL,
                            sessionDurationMinutes = 30,
                        ),
                    ).toFriendlyDisplay()
        }
    }

    fun emailLoginOrCreate() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.otp.email
                    .loginOrSignup(
                        OTP.Email.LoginOrSignupParameters(
                            organizationId = orgIdState.text,
                            emailAddress = memberEmailState.text,
                        ),
                    ).toFriendlyDisplay()
        }
    }

    fun emailAuthenticate() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.otp.email
                    .authenticate(
                        OTP.Email.AuthenticateParameters(
                            code = codeState.text,
                            organizationId = orgIdState.text,
                            emailAddress = memberEmailState.text,
                        ),
                    ).toFriendlyDisplay()
        }
    }

    fun emailDiscoverySend() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.otp.email.discovery
                    .send(
                        OTP.Email.Discovery.SendParameters(
                            emailAddress = memberEmailState.text,
                        ),
                    ).toFriendlyDisplay()
        }
    }

    fun emailDiscoveryAuthenticate() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.otp.email.discovery
                    .authenticate(
                        OTP.Email.Discovery.AuthenticateParameters(
                            code = codeState.text,
                            emailAddress = memberEmailState.text,
                        ),
                    ).toFriendlyDisplay()
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
