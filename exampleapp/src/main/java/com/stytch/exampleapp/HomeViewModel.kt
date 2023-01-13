package com.stytch.exampleapp

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.LoginOrCreateOTPResponse
import com.stytch.sdk.magicLinks.MagicLinks
import com.stytch.sdk.otp.OTP
import com.stytch.sdk.StytchClient
import com.stytch.sdk.StytchResult
import java.util.regex.Pattern
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private val EMAIL_ADDRESS_PATTERN = Pattern.compile(
    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
        "\\@" +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
        "(" +
        "\\." +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
        ")+"
)

private val PHONE_NUMBER_PATTERN = Pattern.compile("^\\+[1-9]\\d{1,14}\$")

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    init {
        // Initialize StytchClient
        StytchClient.configure(
            context = application.applicationContext,
            publicToken = BuildConfig.STYTCH_PUBLIC_TOKEN
        )
    }

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
            val result = StytchClient.handle(uri = uri, sessionDurationMinutes = 60u)
            _currentResponse.value = result.toFriendlyDisplay()
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

    private fun isValidEmail(str: String): Boolean {
        return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
    }

    private fun isPhoneNumberValid(str: String): Boolean {
        return PHONE_NUMBER_PATTERN.matcher(str).matches()
    }

    private fun handleLoginOrCreateOtp(response: LoginOrCreateOTPResponse) = when (response) {
        is StytchResult.Success -> otpMethodId = response.value.methodId
        is StytchResult.Error -> {}
    }
}
