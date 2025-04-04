package com.stytch.sdk.ui.b2b.screens.passwordSetNew

import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.ResetEverything
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.usecases.UsePasswordResetByEmailStart
import com.stytch.sdk.ui.shared.data.EmailState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class PasswordSetNewScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    private val usePasswordResetByEmailStart =
        UsePasswordResetByEmailStart(viewModelScope, state, ::dispatch, productConfig, ::request)
    val passwordSetNewScreenState =
        state
            .map { PasswordSetNewScreenState(emailState = state.value.emailState) }
            .stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                PasswordSetNewScreenState(emailState = state.value.emailState),
            )

    fun handle(action: PasswordSetNewAction) {
        when (action) {
            PasswordSetNewAction.ResetByEmailStart -> usePasswordResetByEmailStart()
            PasswordSetNewAction.ResetEverything -> dispatch(ResetEverything)
        }
    }
}

@JacocoExcludeGenerated
internal data class PasswordSetNewScreenState(
    val emailState: EmailState,
)

internal sealed class PasswordSetNewAction {
    data object ResetByEmailStart : PasswordSetNewAction()

    data object ResetEverything : PasswordSetNewAction()
}
