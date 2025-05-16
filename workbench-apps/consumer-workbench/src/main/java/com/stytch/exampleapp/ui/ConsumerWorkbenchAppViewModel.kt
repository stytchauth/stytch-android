package com.stytch.exampleapp.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.StytchObjectInfo
import com.stytch.sdk.consumer.ConsumerTokenType
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.magicLinks.MagicLinks
import com.stytch.sdk.consumer.network.models.SessionData
import com.stytch.sdk.consumer.network.models.UserData
import com.stytch.sdk.consumer.oauth.OAuth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    fun logout() {
        viewModelScope.launch {
            StytchClient.sessions.revoke()
        }
    }

    fun handleDeeplink(uri: Uri) {
        val (tokenType, token) = StytchClient.parseDeeplink(uri)
        token ?: return
        viewModelScope.launch {
            when (tokenType as ConsumerTokenType) {
                ConsumerTokenType.MAGIC_LINKS -> {
                    StytchClient.magicLinks.authenticate(
                        MagicLinks.AuthParameters(
                            token = token,
                        ),
                    )
                }
                ConsumerTokenType.OAUTH -> {
                    StytchClient.oauth.authenticate(
                        OAuth.ThirdParty.AuthenticateParameters(
                            token = token,
                        ),
                    )
                }
                ConsumerTokenType.RESET_PASSWORD -> {}
                ConsumerTokenType.UNKNOWN -> {}
            }
        }
    }
}

sealed interface ConsumerWorkbenchAppUIState {
    data object Loading : ConsumerWorkbenchAppUIState

    data object LoggedOut : ConsumerWorkbenchAppUIState

    data class LoggedIn(
        val userData: UserData,
        val sessionData: SessionData,
    ) : ConsumerWorkbenchAppUIState
}
