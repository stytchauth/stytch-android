package com.stytch.exampleapp.b2b.ui.headless

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.StytchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HeadlessScreenViewModel : ViewModel() {
    private val _responseState = MutableStateFlow<HeadlessMethodResponseState>(HeadlessMethodResponseState.None)
    val responseState: StateFlow<HeadlessMethodResponseState> = _responseState.asStateFlow()

    fun setResponseState(state: HeadlessMethodResponseState) {
        viewModelScope.launch {
            _responseState.emit(state)
        }
    }
}

sealed interface HeadlessMethodResponseState {
    data object None : HeadlessMethodResponseState

    data object Loading : HeadlessMethodResponseState

    data class Response(
        val result: StytchResult<Any>,
    ) : HeadlessMethodResponseState
}
