package com.stytch.sdk.ui.b2b

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.ui.b2b.domain.B2BUIStateMachine
import com.stytch.sdk.ui.b2b.usecases.UseCases

internal class B2BAuthenticationViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val stateMachine = B2BUIStateMachine.getInstance(savedStateHandle)
    val state = stateMachine.state
    val useCases = UseCases.getInstance(viewModelScope, stateMachine)
}
