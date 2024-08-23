package com.stytch.stytchexampleapp.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.sessions.Sessions
import com.stytch.stytchexampleapp.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel internal class ProfileViewModel
    @Inject
    constructor() : ViewModel() {
        val screenStateFlow = MutableStateFlow<ScreenState>(ScreenState.Idle)

        fun logout() {
            viewModelScope.launch {
                screenStateFlow.value = ScreenState.Loading
                StytchClient.sessions.revoke()
            }
        }

        fun refreshSession() {
            viewModelScope.launch {
                screenStateFlow.value = ScreenState.Loading
                StytchClient.sessions.authenticate(Sessions.AuthParams(sessionDurationMinutes = 5U))
                screenStateFlow.value = ScreenState.Idle
            }
        }
    }
