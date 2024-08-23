package com.stytch.stytchexampleapp

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.network.models.SessionData
import com.stytch.sdk.consumer.network.models.UserData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.parcelize.Parcelize

class MainViewModel : ViewModel() {
    val authenticationState =
        combine(
            StytchClient.isInitialized,
            StytchClient.user.onChange,
            StytchClient.sessions.onChange,
        ) { isInitialized, userData, sessionData ->
            AuthenticationState(
                isInitialized = isInitialized,
                userData = userData,
                sessionData = sessionData,
            )
        }.stateIn(viewModelScope, SharingStarted.Lazily, AuthenticationState())
}

@Parcelize
data class AuthenticationState(
    val isInitialized: Boolean = false,
    val userData: UserData? = null,
    val sessionData: SessionData? = null,
) : Parcelable
