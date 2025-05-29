package com.stytch.exampleapp.b2b.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.B2BSessionData
import com.stytch.sdk.b2b.network.models.MemberData
import com.stytch.sdk.common.StytchObjectInfo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class B2BWorkbenchAppViewModel : ViewModel() {
    val uiState =
        combine(
            StytchB2BClient.isInitialized,
            StytchB2BClient.member.onChange,
            StytchB2BClient.sessions.onChange,
        ) { isInitialized, memberData, sessionData ->
            val memberIsAvailabe = memberData is StytchObjectInfo.Available
            val sessionIsAvailable = sessionData is StytchObjectInfo.Available
            when {
                !isInitialized -> B2BWorkbenchAppUIState.Loading
                isInitialized && memberIsAvailabe && sessionIsAvailable ->
                    B2BWorkbenchAppUIState.LoggedIn(
                        memberData = memberData.value,
                        sessionData = sessionData.value,
                    )
                else -> B2BWorkbenchAppUIState.LoggedOut
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), B2BWorkbenchAppUIState.Loading)

    fun logout() {
        viewModelScope.launch {
            StytchB2BClient.sessions.revoke()
        }
    }

    fun handleDeeplink(uri: Uri) {
        val (tokenType, token) = StytchB2BClient.parseDeeplink(uri)
        token ?: return
        viewModelScope.launch {
            /* TODO
            when (tokenType as B2BTokenType) {
            }
             */
        }
    }
}

sealed interface B2BWorkbenchAppUIState {
    data object Loading : B2BWorkbenchAppUIState

    data object LoggedOut : B2BWorkbenchAppUIState

    data class LoggedIn(
        val memberData: MemberData,
        val sessionData: B2BSessionData,
    ) : B2BWorkbenchAppUIState
}
