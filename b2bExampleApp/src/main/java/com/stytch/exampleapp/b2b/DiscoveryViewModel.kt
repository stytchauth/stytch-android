package com.stytch.exampleapp.b2b

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.discovery.Discovery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DiscoveryViewModel : ViewModel() {
    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String>
        get() = _currentResponse

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    fun organizations() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.discovery.organizations(Discovery.DiscoverOrganizationsParameters()).toFriendlyDisplay()
        }
    }

    private fun CoroutineScope.launchAndToggleLoadingState(block: suspend () -> Unit): DisposableHandle {
        return launch {
            _loadingState.value = true
            block()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }
}
