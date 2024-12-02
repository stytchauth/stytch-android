package com.stytch.sdk.ui.b2b

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.network.models.IB2BAuthData
import com.stytch.sdk.b2b.network.models.IB2BAuthDataWithMFA
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.models.CommonAuthenticationData
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.HandleStepUpAuthentication
import com.stytch.sdk.ui.b2b.data.NavigateTo
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.domain.B2BUIStateMachine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal open class BaseViewModel(
    savedStateHandle: SavedStateHandle,
    internal val productConfig: StytchB2BProductConfig,
) : ViewModel() {
    val state =
        savedStateHandle.getStateFlow(
            B2BUIState.SAVED_STATE_KEY,
            B2BUIState(
                uiIncludedMfaMethods = productConfig.mfaProductInclude,
            ),
        )
    private val stateMachine = B2BUIStateMachine(state.value)

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

    internal suspend inline fun <T> request(crossinline request: suspend () -> StytchResult<T>): Result<T> =
        withContext(Dispatchers.IO) {
            when (val response = request()) {
                is StytchResult.Success -> {
                    if (response.value is CommonAuthenticationData) {
                        handleAuthenticationSuccessResponse(response.value)
                    }
                    Result.success(response.value)
                }
                is StytchResult.Error -> Result.failure(response.exception)
            }
        }

    private fun <T : CommonAuthenticationData> handleAuthenticationSuccessResponse(data: T) {
        when (data) {
            is IB2BAuthData -> dispatch(NavigateTo(state.value.postAuthScreen))
            is IB2BAuthDataWithMFA -> dispatch(HandleStepUpAuthentication(data))
        }
    }
}
