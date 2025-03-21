package com.stytch.sdk.ui.b2b.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.ResetEverything
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.usecases.UseSendCorrectPasswordReset
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberEmailAddress
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberEmailShouldBeValidated
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.EmailInput
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchButton
import kotlinx.coroutines.flow.StateFlow

internal class PasswordForgotScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    val useUpdateMemberEmailAddress = UseUpdateMemberEmailAddress(state, ::dispatch)
    val useUpdateMemberEmailShouldBeValidated = UseUpdateMemberEmailShouldBeValidated(state, ::dispatch)
    private val useSendCorrectPasswordReset =
        UseSendCorrectPasswordReset(viewModelScope, state, ::dispatch, productConfig, ::request, ::request)

    fun onSubmit() = useSendCorrectPasswordReset()
}

@Composable
internal fun PasswordForgotScreen(
    state: State<B2BUIState>,
    createViewModel: CreateViewModel<PasswordForgotScreenViewModel>,
    viewModel: PasswordForgotScreenViewModel = createViewModel(PasswordForgotScreenViewModel::class.java),
) {
    BackHandler(enabled = true) {
        viewModel.dispatch(ResetEverything)
    }
    Column {
        BackButton {
            viewModel.dispatch(ResetEverything)
        }
        PageTitle(textAlign = TextAlign.Left, text = "Check your email for help signing in!")
        BodyText(
            text =
                "We'll email you a login link to sign in to your account directly or reset your password " +
                    "if you have one.",
        )
        EmailInput(
            modifier = Modifier.fillMaxWidth(),
            emailState = state.value.emailState,
            onEmailAddressChanged = { viewModel.useUpdateMemberEmailAddress(it) },
            keyboardActions =
                KeyboardActions(onDone = {
                    viewModel.useUpdateMemberEmailShouldBeValidated(true)
                    viewModel.onSubmit()
                }),
        )
        Spacer(modifier = Modifier.height(16.dp))
        StytchButton(
            enabled = !state.value.emailState.shouldValidateEmail || state.value.emailState.validEmail == true,
            text = "Continue",
            onClick = { viewModel.onSubmit() },
        )
    }
}
