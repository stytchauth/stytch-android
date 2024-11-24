package com.stytch.sdk.ui.b2b.domain

import androidx.lifecycle.SavedStateHandle
import com.stytch.sdk.b2b.network.models.DiscoveredOrganization
import com.stytch.sdk.b2b.network.models.IB2BAuthData
import com.stytch.sdk.b2b.network.models.IB2BAuthDataWithMFA
import com.stytch.sdk.b2b.network.models.OrganizationData
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchError
import com.stytch.sdk.common.network.models.CommonAuthenticationData
import com.stytch.sdk.ui.b2b.data.AuthFlowType
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.MFAPrimaryInfoState
import com.stytch.sdk.ui.b2b.data.MFASMSState
import com.stytch.sdk.ui.b2b.data.MFATOTPState
import com.stytch.sdk.ui.b2b.navigation.Route
import com.stytch.sdk.ui.shared.data.EmailState
import com.stytch.sdk.ui.shared.data.PasswordState

internal class B2BUIStateMachine private constructor(
    private val savedStateHandle: SavedStateHandle,
) {
    val state = savedStateHandle.getStateFlow(B2BUIState.SAVED_STATE_KEY, B2BUIState())

    fun updateEmailState(emailState: EmailState) = state.value.copy(emailState = emailState).emit()

    fun updatePasswordState(passwordState: PasswordState) = state.value.copy(passwordState = passwordState).emit()

    fun setAuthFlowType(authFlowType: AuthFlowType) = state.value.copy(authFlowType = authFlowType).emit()

    fun setActiveOrganization(organization: OrganizationData) =
        state.value.copy(activeOrganization = organization).emit()

    fun setDiscoveredOrganizations(discoveredOrganizations: List<DiscoveredOrganization>) =
        state.value.copy(discoveredOrganizations = discoveredOrganizations).emit()

    fun updateMfaPrimaryInfoState(mfaPrimaryInfoState: MFAPrimaryInfoState) =
        state.value.copy(mfaPrimaryInfoState = mfaPrimaryInfoState).emit()

    fun updateMfaSmsState(mfaSmsState: MFASMSState) = state.value.copy(mfaSSMSState = mfaSmsState).emit()

    fun updateMfaTotpState(mfaTotpState: MFATOTPState) = state.value.copy(mfaTOTPState = mfaTotpState).emit()

    fun setPostAuthScreen(route: Route) = state.value.copy(postAuthScreen = route).emit()

    fun setLoading(value: Boolean) = state.value.copy(isLoading = value).emit()

    fun setStytchError(stytchError: StytchError) = state.value.copy(stytchError = stytchError).emit()

    fun setCurrentRoute(route: Route) = state.value.copy(currentRoute = route).emit()

    fun <T : CommonAuthenticationData> handleAuthenticationResponse(result: StytchResult<T>) {
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

    private fun handleAuthenticationErrorResponse(error: StytchError) = setStytchError(error)

    private fun handleMemberIsFullyAuthenticated(data: IB2BAuthData) = setCurrentRoute(state.value.postAuthScreen)

    private fun handleMemberNeedsMFA(data: IB2BAuthDataWithMFA) {
        // MFA Reducer logic goes here
    }

    private fun B2BUIState.emit() {
        savedStateHandle[B2BUIState.SAVED_STATE_KEY] = this
    }

    companion object {
        @Volatile
        private var instance: B2BUIStateMachine? = null

        fun getInstance(savedStateHandle: SavedStateHandle): B2BUIStateMachine {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = B2BUIStateMachine(savedStateHandle)
                    }
                }
            }
            return instance!!
        }
    }
}
