package com.stytch.sdk.ui.b2b

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.network.models.IB2BAuthData
import com.stytch.sdk.b2b.network.models.IB2BAuthDataWithMFA
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.models.CommonAuthenticationData
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.HandleStepUpAuthentication
import com.stytch.sdk.ui.b2b.data.SetGenericError
import com.stytch.sdk.ui.b2b.data.SetLoading
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal open class BaseViewModel(
    private val state: StateFlow<B2BUIState>,
    private val dispatchAction: suspend (B2BUIAction) -> Unit,
) : ViewModel() {
    internal fun dispatch(action: B2BUIAction) {
        viewModelScope.launch {
            dispatchAction(action)
        }
    }

    internal suspend inline fun <T> request(crossinline clientRequest: suspend () -> StytchResult<T>): Result<T> =
        request(true, clientRequest)

    internal suspend inline fun <T> request(
        shouldSetLoadingIndicator: Boolean,
        crossinline request: suspend () -> StytchResult<T>,
    ): Result<T> =
        withContext(Dispatchers.IO) {
            if (shouldSetLoadingIndicator) {
                dispatch(SetLoading(true))
            }
            when (val response = request()) {
                is StytchResult.Success -> {
                    if (shouldSetLoadingIndicator) {
                        dispatch(SetLoading(false))
                    }
                    if (response.value is CommonAuthenticationData) {
                        handleAuthenticationSuccessResponse(response.value)
                    }
                    Result.success(response.value)
                }
                is StytchResult.Error -> {
                    if (shouldSetLoadingIndicator) {
                        dispatch(SetLoading(false))
                    }
                    dispatch(SetGenericError(response.exception.message))
                    Result.failure(response.exception)
                }
            }
        }

    private fun <T : CommonAuthenticationData> handleAuthenticationSuccessResponse(data: T) {
        when (data) {
            is IB2BAuthData -> dispatch(SetNextRoute(state.value.postAuthScreen))
            is IB2BAuthDataWithMFA -> dispatch(HandleStepUpAuthentication(data))
        }
    }
}
