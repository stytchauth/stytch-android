package com.stytch.sdk.ui.b2b.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.usecases.UseNonMemberPasswordReset
import com.stytch.sdk.ui.b2b.usecases.UsePasswordResetByEmailStart
import com.stytch.sdk.ui.b2b.usecases.UseSearchMember
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberEmailAddress
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.EmailInput
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchButton
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class PasswordForgotScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    val useSearchMember = UseSearchMember(::request)
    val usePasswordResetByEmailStart = UsePasswordResetByEmailStart(viewModelScope, state, productConfig, ::request)
    val useNonMemberPasswordReset =
        UseNonMemberPasswordReset(viewModelScope, state, ::dispatch, productConfig, ::request)
    val useUpdateMemberEmailAddress = UseUpdateMemberEmailAddress(state, ::dispatch)

    fun onSubmit() {
        viewModelScope.launch {
            val organizationId = state.value.activeOrganization?.organizationId ?: return@launch
            useSearchMember(
                emailAddress = state.value.emailState.emailAddress,
                organizationId = organizationId,
            ).onSuccess {
                if (it.member?.memberPasswordId.isNullOrEmpty()) {
                    // no memberPasswordId == no password, so drop them in the nonMemberReset flow
                    return@onSuccess useNonMemberPasswordReset()
                }
                // there IS a password for this user, so send them a reset
                usePasswordResetByEmailStart()
            }.onFailure { }
        }
    }
}

@Composable
internal fun PasswordForgotScreen(
    state: State<B2BUIState>,
    createViewModel: CreateViewModel<PasswordForgotScreenViewModel>,
    viewModel: PasswordForgotScreenViewModel = createViewModel(PasswordForgotScreenViewModel::class.java),
) {
    val theme = LocalStytchTheme.current
    Column {
        PageTitle(text = "Check your email for help signing in!")
        BodyText(
            text =
                "We'll email you a login link to sign in to your account directly or reset your password " +
                    "if you have one.",
        )
        EmailInput(
            emailState = state.value.emailState,
            onEmailAddressChanged = { viewModel.useUpdateMemberEmailAddress(it) },
        )
        StytchButton(
            enabled = state.value.emailState.validEmail == true,
            text = "Continue",
            onClick = { viewModel.onSubmit() },
        )
    }
}
