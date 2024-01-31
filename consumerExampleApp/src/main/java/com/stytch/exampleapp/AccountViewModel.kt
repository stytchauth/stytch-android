package com.stytch.exampleapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.sessions.Sessions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AccountViewModel: ViewModel() {
    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String>
        get() = _currentResponse

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    init {
        viewModelScope.launch {
            _loadingState.emit(true)
            val result = StytchClient.sessions.authenticate(Sessions.AuthParams())
            _currentResponse.value = result.toFriendlyDisplay()
            _loadingState.emit(false)
        }
    }
}