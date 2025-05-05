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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stytch.sdk.R
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
        dispatch(PasswordForgotAction.ResetEverything)
    }
    Column {
        BackButton {
            dispatch(PasswordForgotAction.ResetEverything)
        }
        PageTitle(
            textAlign = TextAlign.Left,
            text = stringResource(R.string.stytch_b2b_check_your_email_for_help_signing_in),
        )
        BodyText(text = stringResource(R.string.stytch_b2b_we_ll_email_you_a_login_link))
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
            text = stringResource(R.string.stytch_continue_button_text),
            onClick = { dispatch(PasswordForgotAction.Submit) },
        )
    }
}
