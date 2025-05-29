package com.stytch.exampleapp.b2b.ui.headless.recoveryCodes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.recoveryCodes.RecoveryCodes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecoveryCodesScreenViewModel(
    private val reportState: (HeadlessMethodResponseState) -> Unit,
) : ViewModel() {
    fun handle(action: RecoveryCodesAction) =
        when (action) {
            RecoveryCodesAction.Get -> getRecoveryCodes()
            RecoveryCodesAction.Rotate -> rotateRecoveryCodes()
            is RecoveryCodesAction.Recover -> useRecoveryCode(action.organizationId, action.memberId, action.code)
        }

    private fun getRecoveryCodes() {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            reportState(HeadlessMethodResponseState.Response(StytchB2BClient.recoveryCodes.get()))
        }
    }

    private fun rotateRecoveryCodes() {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            reportState(HeadlessMethodResponseState.Response(StytchB2BClient.recoveryCodes.rotate()))
        }
    }

    private fun useRecoveryCode(
        organizationId: String,
        memberId: String,
        recoveryCode: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.recoveryCodes.recover(
                    RecoveryCodes.RecoverParameters(
                        organizationId = organizationId,
                        memberId = memberId,
                        recoveryCode = recoveryCode,
                        sessionDurationMinutes = 30,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }
}

sealed interface RecoveryCodesAction {
    data object Get : RecoveryCodesAction

    data object Rotate : RecoveryCodesAction

    data class Recover(
        val organizationId: String,
        val memberId: String,
        val code: String,
    ) : RecoveryCodesAction
}
