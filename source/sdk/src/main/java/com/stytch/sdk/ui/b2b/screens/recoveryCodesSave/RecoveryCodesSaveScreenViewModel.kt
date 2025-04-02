package com.stytch.sdk.ui.b2b.screens.recoveryCodesSave

import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.navigation.Routes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class RecoveryCodesSaveScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
) : BaseViewModel(state, dispatchAction) {
    val recoveryCodesSaveState =
        state
            .map {
                RecoveryCodesSaveScreenState(
                    backupCodes =
                        state.value.mfaTOTPState
                            ?.enrollmentState
                            ?.recoveryCodes ?: emptyList(),
                )
            }.stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                RecoveryCodesSaveScreenState(),
            )

    fun handle(action: RecoveryCodesSaveAction) {
        when (action) {
            RecoveryCodesSaveAction.AcknowledgeSave -> dispatch(SetNextRoute(Routes.Success))
        }
    }
}

@JacocoExcludeGenerated
internal data class RecoveryCodesSaveScreenState(
    val backupCodes: List<String> = emptyList(),
)

internal sealed class RecoveryCodesSaveAction {
    data object AcknowledgeSave : RecoveryCodesSaveAction()
}
