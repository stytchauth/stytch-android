package com.stytch.exampleapp

import android.app.Application
import android.net.Uri
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

    fun handleUri(uri: Uri) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = StytchClient.handle(uri = uri, codeVerifier = "", sessionDurationInMinutes = 60u)
            _currentResponse.value = result.toString()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }

    fun loginOrCreate() {
        viewModelScope.launch {
            _loadingState.value = true
            val result = StytchClient.magicLinks.email.loginOrCreate(MagicLinks.EmailMagicLinks.Parameters(email = emailTextState.text, "", ""))
            _currentResponse.value = result.toString()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }

}
