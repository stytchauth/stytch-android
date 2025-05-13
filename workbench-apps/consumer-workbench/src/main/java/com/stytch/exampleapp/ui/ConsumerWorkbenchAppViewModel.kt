package com.stytch.exampleapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.StytchObjectInfo
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.network.models.SessionData
import com.stytch.sdk.consumer.network.models.UserData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ConsumerWorkbenchAppViewModel : ViewModel() {
    val uiState =
        combine(
            StytchClient.isInitialized,
            StytchClient.user.onChange,
            StytchClient.sessions.onChange,
        ) { isInitialized, userData, sessionData ->
            val userIsAvailable = userData is StytchObjectInfo.Available
            val sessionIsAvailable = sessionData is StytchObjectInfo.Available
            when {
                !isInitialized -> ConsumerWorkbenchAppUIState.Loading
                isInitialized && userIsAvailable && sessionIsAvailable ->
                    ConsumerWorkbenchAppUIState.LoggedIn(
                        userData = userData.value,
                        sessionData = sessionData.value,
                    )
                else -> ConsumerWorkbenchAppUIState.LoggedOut
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ConsumerWorkbenchAppUIState.Loading)
}

sealed interface ConsumerWorkbenchAppUIState {
    data object Loading : ConsumerWorkbenchAppUIState

    data object LoggedOut : ConsumerWorkbenchAppUIState

    data class LoggedIn(
        val userData: UserData,
        val sessionData: SessionData,
    ) : ConsumerWorkbenchAppUIState
}
