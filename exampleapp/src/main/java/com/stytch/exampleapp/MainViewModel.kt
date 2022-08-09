package com.stytch.exampleapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.StytchClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val HOST_URL = "https://web.stytch.com/sdk/v1/"

class SignInViewModel(application: Application) : AndroidViewModel(application) {

    init {
        // Initialize StytchClient
        StytchClient.configure(
            context = application.applicationContext,
            publicToken = BuildConfig.STYTCH_PUBLIC_TOKEN,
            hostUrl = HOST_URL
        )
    }

    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String>
        get() = _currentResponse
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    fun authenticate() {
        viewModelScope.launch {
            _loadingState.value = true
            val result = StytchClient.MagicLinks.authenticate(BuildConfig.STYTCH_PUBLIC_TOKEN, 60)
            _currentResponse.value = result.toString()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }

    fun loginOrCreate() {
        viewModelScope.launch {
            _loadingState.value = true
            val result = StytchClient.MagicLinks.loginOrCreate(StytchClient.MagicLinks.Parameters("test@stytch.com"))
            _currentResponse.value = result.toString()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }

}
