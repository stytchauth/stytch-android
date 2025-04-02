package com.stytch.sdk.ui.b2b.screens.passwordForgot

import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.usecases.UseSendCorrectPasswordReset
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberEmailAddress
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberEmailShouldBeValidated
import com.stytch.sdk.ui.shared.data.EmailState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class PasswordForgotScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    private val useUpdateMemberEmailAddress = UseUpdateMemberEmailAddress(state, ::dispatch)
    private val useUpdateMemberEmailShouldBeValidated = UseUpdateMemberEmailShouldBeValidated(state, ::dispatch)
    private val useSendCorrectPasswordReset =
        UseSendCorrectPasswordReset(viewModelScope, state, ::dispatch, productConfig, ::request, ::request)
    val passwordForgotScreenState =
        state
            .map {
                PasswordForgotScreenState(
                    emailState = state.value.emailState,
                )
            }.stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                PasswordForgotScreenState(
                    emailState = state.value.emailState,
                ),
            )

    fun handle(action: PasswordForgotAction) {
        when (action) {
            is PasswordForgotAction.DispatchGlobalAction -> dispatch(action.action)
            is PasswordForgotAction.UpdateMemberEmailAddress -> useUpdateMemberEmailAddress(action.emailAddress)
            PasswordForgotAction.SetEmailAddressShouldBeValidated -> useUpdateMemberEmailShouldBeValidated(true)
            PasswordForgotAction.Submit -> useSendCorrectPasswordReset()
        }
    }
}

@JacocoExcludeGenerated
internal data class PasswordForgotScreenState(
    val emailState: EmailState = EmailState(),
)

internal sealed class PasswordForgotAction {
    @JacocoExcludeGenerated
    data class UpdateMemberEmailAddress(
        val emailAddress: String,
    ) : PasswordForgotAction()

    data object SetEmailAddressShouldBeValidated : PasswordForgotAction()

    @JacocoExcludeGenerated
    data class DispatchGlobalAction(
        val action: B2BUIAction,
    ) : PasswordForgotAction()

    data object Submit : PasswordForgotAction()
}
