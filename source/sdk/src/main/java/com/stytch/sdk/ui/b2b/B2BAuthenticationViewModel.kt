package com.stytch.sdk.ui.b2b

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.domain.B2BUIStateMachine
import com.stytch.sdk.ui.b2b.utils.B2BUIViewModelFactory
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class B2BAuthenticationViewModel(
    savedStateHandle: SavedStateHandle,
    private val stateMachine: B2BUIStateMachine,
    val stateFlow: StateFlow<B2BUIState>,
) : ViewModel() {
    private val viewModelHelperFactory: ViewModelProvider.AndroidViewModelFactory =
        B2BUIViewModelFactory(
            stateFlow,
            stateMachine::dispatch,
        )

    init {
        viewModelScope.launch {
            stateMachine.state.collect { newState ->
                savedStateHandle[B2BUIState.SAVED_STATE_KEY] = newState
            }
        }
    }

    internal fun dispatch(action: B2BUIAction) {
        viewModelScope.launch {
            stateMachine.dispatch(action)
        }
    }

    internal fun <T : ViewModel> createViewModel(modelClass: Class<T>): T = viewModelHelperFactory.create(modelClass)

    companion object {
        fun create(productConfig: StytchB2BProductConfig): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val savedStateHandle = createSavedStateHandle()
                    val stateFlow =
                        savedStateHandle.getStateFlow(
                            B2BUIState.SAVED_STATE_KEY,
                            B2BUIState(uiIncludedMfaMethods = productConfig.mfaProductInclude),
                        )
                    val stateMachine = B2BUIStateMachine(stateFlow.value)
                    B2BAuthenticationViewModel(savedStateHandle, stateMachine, stateFlow)
                }
            }
    }
}
