package com.stytch.sdk.ui.b2b.screens.passwordReset

import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.B2BTokenType
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.usecases.UsePasswordDiscoveryResetByEmail
import com.stytch.sdk.ui.b2b.usecases.UsePasswordResetByEmail
import com.stytch.sdk.ui.b2b.usecases.UsePasswordsStrengthCheck
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberPassword
import com.stytch.sdk.ui.shared.data.PasswordState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class PasswordResetScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    private val usePasswordResetByEmail =
        UsePasswordResetByEmail(viewModelScope, productConfig, state, ::dispatch, ::request)
    private val useUpdateMemberPassword = UseUpdateMemberPassword(state, ::dispatch)
    private val usePasswordStrengthCheck = UsePasswordsStrengthCheck(viewModelScope, state, ::dispatch, ::request)
    private val usePasswordDiscoveryResetByEmail =
        UsePasswordDiscoveryResetByEmail(viewModelScope, state, ::dispatch, productConfig, ::request)

    private fun handlePasswordReset() {
        if (state.value.deeplinkTokenPair?.tokenType == B2BTokenType.DISCOVERY) {
            usePasswordDiscoveryResetByEmail()
        } else {
            usePasswordResetByEmail()
        }
    }

    val passwordResetScreenState =
        state
            .map { PasswordResetScreenState(passwordState = state.value.passwordState) }
            .stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                PasswordResetScreenState(passwordState = state.value.passwordState),
            )

    fun handle(action: PasswordResetAction) {
        when (action) {
            is PasswordResetAction.UpdateMemberPassword -> useUpdateMemberPassword(action.password)
            PasswordResetAction.CallStrengthCheck -> usePasswordStrengthCheck()
            is PasswordResetAction.DispatchGlobalAction -> dispatch(action.action)
            PasswordResetAction.Submit -> handlePasswordReset()
        }
    }
}

@JacocoExcludeGenerated
internal data class PasswordResetScreenState(
    val passwordState: PasswordState,
)

internal sealed class PasswordResetAction {
    @JacocoExcludeGenerated
    data class UpdateMemberPassword(
        val password: String,
    ) : PasswordResetAction()

    data object CallStrengthCheck : PasswordResetAction()

    @JacocoExcludeGenerated
    data class DispatchGlobalAction(
        val action: B2BUIAction,
    ) : PasswordResetAction()

    data object Submit : PasswordResetAction()
}
