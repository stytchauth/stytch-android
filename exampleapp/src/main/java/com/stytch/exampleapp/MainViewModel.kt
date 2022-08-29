package com.stytch.exampleapp

import android.app.Application
import android.net.Uri
import android.telephony.PhoneNumberUtils
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.MagicLinks
import com.stytch.sdk.StytchClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern

private val EMAIL_ADDRESS_PATTERN = Pattern.compile(
    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
)

private val PHONE_NUMBER_PATTERN = Pattern.compile("^\\+[0-9]{1,3}\\.[0-9]{4,14}(?:x.+)?$")

class MainViewModel(application: Application) : AndroidViewModel(application) {

    init {
        // Initialize StytchClient
        StytchClient.configure(
            context = application.applicationContext,
            publicToken = BuildConfig.STYTCH_PUBLIC_TOKEN,
            hostUrl = "https://${application.getString(R.string.host)}"
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
    val emailIsValid
        get() = isValidEmail(emailTextState.text)
    val phoneNumberIsValid
        get() = isPhoneNumberValid(phoneNumberTextState.text)
    var showEmailError by mutableStateOf(false)
    var showPhoneError by mutableStateOf(false)

    fun handleUri(uri: Uri) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = StytchClient.handle(uri = uri, sessionDurationMinutes = 60u)
            _currentResponse.value = result.toString()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }

    fun loginOrCreate() {
        if (emailIsValid) {
            showEmailError = false
            viewModelScope.launch {
                _loadingState.value = true
                val result = StytchClient.magicLinks.email.loginOrCreate(MagicLinks.EmailMagicLinks.Parameters(email = emailTextState.text, "", ""))
                _currentResponse.value = result.toString()
            }.invokeOnCompletion {
                _loadingState.value = false
            }
        } else {
            showEmailError = true
        }
    }

    private fun isValidEmail(str: String): Boolean {
        return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
    }

    private fun isPhoneNumberValid(str: String): Boolean {
        return PHONE_NUMBER_PATTERN.matcher(str).matches()
    }

}
