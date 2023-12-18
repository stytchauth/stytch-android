package com.stytch.exampleapp

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.DeeplinkHandledStatus
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.LoginOrCreateOTPResponse
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.magicLinks.MagicLinks
import com.stytch.sdk.consumer.otp.OTP
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String>
        get() = _currentResponse

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    var emailTextState by mutableStateOf(TextFieldValue(""))
    var phoneNumberTextState by mutableStateOf(TextFieldValue(""))
    var otpTokenTextState by mutableStateOf(TextFieldValue(""))

    val emailIsValid
        get() = isValidEmail(emailTextState.text)

    val phoneNumberIsValid
        get() = isPhoneNumberValid(phoneNumberTextState.text)

    var showEmailError by mutableStateOf(false)
    var showPhoneError by mutableStateOf(false)
    var showOTPError by mutableStateOf(false)

    private var otpMethodId = ""

    fun handleUri(uri: Uri) {
        viewModelScope.launch {
            _loadingState.value = true
            _currentResponse.value = when (val result = StytchClient.handle(uri = uri, sessionDurationMinutes = 60u)) {
                is DeeplinkHandledStatus.NotHandled -> result.reason.message
                is DeeplinkHandledStatus.Handled -> result.response.result.toFriendlyDisplay()
                // This only happens for password reset deeplinks
                is DeeplinkHandledStatus.ManualHandlingRequired ->
                    "Password reset token retrieved, initiate password reset flow"
            }
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }

    fun loginOrCreate() {
        showPhoneError = false
        if (emailIsValid) {
            showEmailError = false
            viewModelScope.launch {
                _loadingState.value = true
                val result = StytchClient.magicLinks.email.loginOrCreate(
                    MagicLinks.EmailMagicLinks.Parameters(email = emailTextState.text)
                )
                _currentResponse.value = result.toFriendlyDisplay()
            }.invokeOnCompletion {
                _loadingState.value = false
            }
        } else {
            showEmailError = true
        }
    }

    fun loginOrCreateSMS() {
        showEmailError = false
        if (phoneNumberIsValid) {
            showPhoneError = false
            viewModelScope.launch {
                _loadingState.value = true
                val result = StytchClient.otps.sms.loginOrCreate(OTP.SmsOTP.Parameters(phoneNumberTextState.text))
                handleLoginOrCreateOtp(result)
                _currentResponse.value = result.toFriendlyDisplay()
            }.invokeOnCompletion {
                _loadingState.value = false
            }
        } else {
            showPhoneError = true
        }
    }

    fun loginOrCreateWhatsApp() {
        showEmailError = false
        if (phoneNumberIsValid) {
            showPhoneError = false
            viewModelScope.launch {
                _loadingState.value = true
                val result = StytchClient.otps.whatsapp.loginOrCreate(
                    OTP.WhatsAppOTP.Parameters(phoneNumberTextState.text)
                )
                handleLoginOrCreateOtp(result)
                _currentResponse.value = result.toFriendlyDisplay()
            }.invokeOnCompletion {
                _loadingState.value = false
            }
        } else {
            showPhoneError = true
        }
    }

    fun loginOrCreateEmail() {
        showPhoneError = false
        if (emailIsValid) {
            showEmailError = false
            viewModelScope.launch {
                _loadingState.value = true
                val result = StytchClient.otps.email.loginOrCreate(OTP.EmailOTP.Parameters(emailTextState.text))
                handleLoginOrCreateOtp(result)
                _currentResponse.value = result.toFriendlyDisplay()
            }.invokeOnCompletion {
                _loadingState.value = false
            }
        } else {
            showEmailError = true
        }
    }

    fun authenticateOTP() {
        if (otpTokenTextState.text.isEmpty()) {
            showOTPError = true
        } else {
            showOTPError = false
            viewModelScope.launch {
                _loadingState.value = true
                val result = StytchClient.otps.authenticate(OTP.AuthParameters(otpTokenTextState.text, otpMethodId))
                _currentResponse.value = result.toFriendlyDisplay()
            }.invokeOnCompletion {
                _loadingState.value = false
            }
        }
    }

    fun revokeSession() {
        viewModelScope.launch {
            _loadingState.value = true
            val result = StytchClient.sessions.revoke()
            _currentResponse.value = result.toFriendlyDisplay()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }

    private fun handleLoginOrCreateOtp(response: LoginOrCreateOTPResponse) = when (response) {
        is StytchResult.Success -> otpMethodId = response.value.methodId
        is StytchResult.Error -> {}
    }
}
