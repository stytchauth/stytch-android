package com.stytch.sdk.ui.b2b.screens.passwordForgot

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stytch.sdk.ui.b2b.data.ResetEverything
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.EmailInput
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchButton

@Composable
internal fun PasswordForgotScreen(viewModel: PasswordForgotScreenViewModel) {
    val state = viewModel.passwordForgotScreenState.collectAsState()
    PasswordForgotScreenComposable(
        state = state.value,
        dispatch = viewModel::handle,
    )
}

@Composable
private fun PasswordForgotScreenComposable(
    state: PasswordForgotScreenState,
    dispatch: (PasswordForgotAction) -> Unit,
) {
    BackHandler(enabled = true) {
        dispatch(PasswordForgotAction.DispatchGlobalAction(ResetEverything))
    }
    Column {
        BackButton {
            dispatch(PasswordForgotAction.DispatchGlobalAction(ResetEverything))
        }
        PageTitle(textAlign = TextAlign.Left, text = "Check your email for help signing in!")
        BodyText(
            text =
                "We'll email you a login link to sign in to your account directly or reset your password " +
                    "if you have one.",
        )
        EmailInput(
            modifier = Modifier.fillMaxWidth(),
            emailState = state.emailState,
            onEmailAddressChanged = { dispatch(PasswordForgotAction.UpdateMemberEmailAddress(it)) },
            keyboardActions =
                KeyboardActions(onDone = {
                    dispatch(PasswordForgotAction.SetEmailAddressShouldBeValidated)
                    dispatch(PasswordForgotAction.Submit)
                }),
        )
        Spacer(modifier = Modifier.height(16.dp))
        StytchButton(
            enabled = !state.emailState.shouldValidateEmail || state.emailState.validEmail == true,
            text = "Continue",
            onClick = { dispatch(PasswordForgotAction.Submit) },
        )
    }
}
