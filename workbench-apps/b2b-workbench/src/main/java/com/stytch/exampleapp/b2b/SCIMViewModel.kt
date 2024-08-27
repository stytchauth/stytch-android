package com.stytch.exampleapp.b2b

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.scim.SCIM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SCIMViewModel : ViewModel() {
    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String>
        get() = _currentResponse

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    var connectionIdState by mutableStateOf(TextFieldValue(""))
    var displayNameState by mutableStateOf(TextFieldValue(""))
    var identityProviderState by mutableStateOf(TextFieldValue(""))

    fun createConnection() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.scim
                    .createConnection(
                        SCIM.CreateConnectionParameters(
                            displayName = displayNameState.text,
                            identityProvider = identityProviderState.text,
                        ),
                    ).toFriendlyDisplay()
        }
    }

    fun updateConnection() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.scim
                    .updateConnection(
                        SCIM.UpdateConnectionParameters(
                            connectionId = connectionIdState.text,
                            displayName = displayNameState.text,
                            identityProvider = identityProviderState.text,
                        ),
                    ).toFriendlyDisplay()
        }
    }

    fun deleteConnection() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.scim
                    .deleteConnection(
                        connectionId = connectionIdState.text,
                    ).toFriendlyDisplay()
        }
    }

    fun getConnection() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.scim
                    .getConnection(
                        connectionId = connectionIdState.text,
                    ).toFriendlyDisplay()
        }
    }

    fun getConnectionGroups() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.scim
                    .getConnectionGroups(
                        SCIM.GetConnectionGroupsParameters(),
                    ).toFriendlyDisplay()
        }
    }

    fun rotateStart() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.scim
                    .rotateStart(
                        connectionId = connectionIdState.text,
                    ).toFriendlyDisplay()
        }
    }

    fun rotateComplete() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.scim
                    .rotateComplete(
                        connectionId = connectionIdState.text,
                    ).toFriendlyDisplay()
        }
    }

    fun rotateCancel() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.scim
                    .rotateCancel(
                        connectionId = connectionIdState.text,
                    ).toFriendlyDisplay()
        }
    }

    private fun CoroutineScope.launchAndToggleLoadingState(block: suspend () -> Unit): DisposableHandle =
        launch {
            _loadingState.value = true
            block()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
}
