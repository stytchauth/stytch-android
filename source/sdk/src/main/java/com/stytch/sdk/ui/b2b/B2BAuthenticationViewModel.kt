package com.stytch.sdk.ui.b2b

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.network.models.DiscoveredOrganization
import com.stytch.sdk.b2b.network.models.IB2BAuthData
import com.stytch.sdk.b2b.network.models.IB2BAuthDataWithMFA
import com.stytch.sdk.b2b.network.models.OrganizationData
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchError
import com.stytch.sdk.common.network.models.CommonAuthenticationData
import com.stytch.sdk.ui.b2b.data.AuthFlowType
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.shared.data.EmailState
import com.stytch.sdk.ui.shared.data.PasswordState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class B2BAuthenticationViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val uiState = savedStateHandle.getStateFlow(B2BUIState.SAVED_STATE_KEY, B2BUIState())

    fun performAuthenticationRequest(
        request: suspend () -> StytchResult<CommonAuthenticationData>,
        scope: CoroutineScope = viewModelScope,
    ) {
        setLoading(true)
        scope
            .launch(Dispatchers.IO) {
                handleAuthenticationResponse(request())
            }.invokeOnCompletion {
                setLoading(false)
            }
    }

    suspend fun performGenericRequest(request: suspend () -> StytchResult<Any>): StytchResult<Any> {
        setLoading(true)
        return withContext(Dispatchers.IO) {
            val response = request()
            setLoading(false)
            response
        }
    }

    fun updateEmailState(state: EmailState) = uiState.value.copy(emailState = state).emit()

    fun updatePasswordState(state: PasswordState) = uiState.value.copy(passwordState = state).emit()

    fun setAuthFlowType(authFlowType: AuthFlowType) = uiState.value.copy(authFlowType = authFlowType).emit()

    fun setActiveOrganization(organization: OrganizationData) =
        uiState.value.copy(activeOrganization = organization).emit()

    fun setDiscoveredOrganizations(discoveredOrganizations: List<DiscoveredOrganization>) =
        uiState.value.copy(discoveredOrganizations = discoveredOrganizations).emit()

    private fun setLoading(value: Boolean) = uiState.value.copy(isLoading = value).emit()

    private fun <T : CommonAuthenticationData> handleAuthenticationResponse(result: StytchResult<T>) {
        when (result) {
            is StytchResult.Success -> handleAuthenticationSuccessResponse(result.value)
            is StytchResult.Error -> handleAuthenticationErrorResponse(result.exception)
        }
    }

    private fun <T : CommonAuthenticationData> handleAuthenticationSuccessResponse(data: T) {
        when (data) {
            is IB2BAuthData -> handleMemberIsFullyAuthenticated(data)
            is IB2BAuthDataWithMFA -> handleMemberNeedsMFA(data)
        }
    }

    private fun handleAuthenticationErrorResponse(error: StytchError) = uiState.value.copy(stytchError = error).emit()

    private fun handleMemberIsFullyAuthenticated(data: IB2BAuthData) {}

    private fun handleMemberNeedsMFA(data: IB2BAuthDataWithMFA) {}

    private fun B2BUIState.emit() {
        savedStateHandle[B2BUIState.SAVED_STATE_KEY] = this
    }
}
