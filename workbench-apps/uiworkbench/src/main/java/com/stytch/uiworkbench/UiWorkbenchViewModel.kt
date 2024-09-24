package com.stytch.uiworkbench

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.network.models.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UiWorkbenchViewModel : ViewModel() {
    private val _userState = MutableStateFlow<UserData?>(null)
    val userState: StateFlow<UserData?> = _userState.asStateFlow()

    init {
        _userState.value = StytchClient.user.getSyncUser()
        StytchClient.user.onChange {
            _userState.value = it
        }
    }

    fun logout() {
        viewModelScope.launch {
            StytchClient.sessions.revoke()
        }
    }
}
