package com.stytch.stytchexampleapp

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.StytchObjectInfo
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.network.models.SessionData
import com.stytch.sdk.consumer.network.models.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class MainViewModel : ViewModel() {
    private var _authenticationState = MutableStateFlow(AuthenticationState())
    var authenticationState: StateFlow<AuthenticationState> = _authenticationState.asStateFlow()

    init {
        viewModelScope.launch {
            authenticationState =
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
                }.stateIn(viewModelScope, SharingStarted.Lazily, AuthenticationState())
        }
    }
}

@Parcelize
data class AuthenticationState(
    val isInitialized: Boolean = false,
    val userData: UserData? = null,
    val sessionData: SessionData? = null,
) : Parcelable
