package com.stytch.sdk.ui.b2b.screens.recoveryCodesEntry

import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.usecases.UseRecoveryCodesRecover
import kotlinx.coroutines.flow.StateFlow

internal class RecoveryCodesEntryScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    private val useRecoveryCodesRecover = UseRecoveryCodesRecover(viewModelScope, state, productConfig, ::request)

    fun handle(action: RecoveryCodesEntryAction) {
        when (action) {
            is RecoveryCodesEntryAction.Recover -> useRecoveryCodesRecover(action.code)
        }
    }
}

internal sealed class RecoveryCodesEntryAction {
    @JacocoExcludeGenerated
    data class Recover(
        val code: String,
    ) : RecoveryCodesEntryAction()
}
