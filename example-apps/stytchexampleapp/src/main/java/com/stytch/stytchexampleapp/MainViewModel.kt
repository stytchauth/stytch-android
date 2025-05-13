package com.stytch.stytchexampleapp

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.StytchObjectInfo
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.network.models.SessionData
import com.stytch.sdk.consumer.network.models.UserData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.parcelize.Parcelize

class MainViewModel : ViewModel() {
    var authenticationState: StateFlow<AuthenticationState> =
        combine(
            StytchClient.isInitialized,
            StytchClient.user.onChange,
            StytchClient.sessions.onChange,
        ) { isInitialized, stytchUser, stytchSession ->
            val userData =
                if (stytchUser is StytchObjectInfo.Available) {
                    stytchUser.value
                } else {
                    null
                }
            val sessionData =
                if (stytchSession is StytchObjectInfo.Available) {
                    stytchSession.value
                } else {
                    null
                }
            AuthenticationState(
                isInitialized = isInitialized,
                userData = userData,
                sessionData = sessionData,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthenticationState())
}

@Parcelize
data class AuthenticationState(
    val isInitialized: Boolean = false,
    val userData: UserData? = null,
    val sessionData: SessionData? = null,
) : Parcelable
