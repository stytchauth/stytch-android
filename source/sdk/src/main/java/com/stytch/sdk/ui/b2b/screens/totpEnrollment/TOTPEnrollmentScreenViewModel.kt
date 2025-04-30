package com.stytch.sdk.ui.b2b.screens.totpEnrollment

import androidx.lifecycle.viewModelScope
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.data.SetPostAuthScreen
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.b2b.usecases.UseTOTPCreate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class TOTPEnrollmentScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
) : BaseViewModel(state, dispatchAction) {
    private val _totpState = MutableStateFlow(state.value.mfaTOTPState)
    val totpState = _totpState.asStateFlow()

    private val useTOTPCreate = UseTOTPCreate(viewModelScope, state, ::dispatch, ::request)

    fun onScreenLoad() {
        // if we're enrolling, make sure we always set the postauthscreen to recoverycodesave
        dispatch(SetPostAuthScreen(Routes.RecoveryCodeSave))
        if (state.value.mfaTOTPState == null) {
            // kick off account creation
            useTOTPCreate()
        }
    }

    fun handle(action: TOTPEnrollmentScreenAction) {
        when (action) {
            TOTPEnrollmentScreenAction.GoToCodeEntry -> dispatch(SetNextRoute(Routes.TOTPEntry))
            TOTPEnrollmentScreenAction.GoToMFAEnrollment -> dispatch(SetNextRoute(Routes.MFAEnrollmentSelection))
        }
    }
}

internal sealed class TOTPEnrollmentScreenAction {
    data object GoToCodeEntry : TOTPEnrollmentScreenAction()

    data object GoToMFAEnrollment : TOTPEnrollmentScreenAction()
}
