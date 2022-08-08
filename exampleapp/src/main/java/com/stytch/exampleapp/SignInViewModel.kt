package com.stytch.exampleapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.StytchClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(val repository: SignInRepository) : ViewModel() {

    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String>
        get() = _currentResponse
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    fun authenticate() {
        viewModelScope.launch {
            _loadingState.value = true
            val result = repository.authenticate(BuildConfig.STYTCH_PUBLIC_TOKEN, 60)
            _currentResponse.value = result.toString()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }

    fun loginOrCreate() {
        viewModelScope.launch {
            _loadingState.value = true
            val result = repository.loginOrCreate(StytchClient.MagicLinks.Parameters("test@stytch.com"))
            _currentResponse.value = result.toString()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }

}
