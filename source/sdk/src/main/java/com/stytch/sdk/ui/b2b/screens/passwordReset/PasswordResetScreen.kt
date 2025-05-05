package com.stytch.sdk.ui.b2b.screens.passwordReset

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stytch.sdk.R
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.PasswordInput
import com.stytch.sdk.ui.shared.components.StytchButton

@Composable
internal fun PasswordResetScreen(viewModel: PasswordResetScreenViewModel) {
    val state = viewModel.passwordResetScreenState.collectAsState()
    PasswordResetScreenComposable(
        state = state.value,
        dispatch = viewModel::handle,
    )
}

@Composable
private fun PasswordResetScreenComposable(
    state: PasswordResetScreenState,
    dispatch: (PasswordResetAction) -> Unit,
) {
    BackHandler(enabled = true) {
        dispatch(PasswordResetAction.ResetEverything)
    }
    Column {
        BackButton {
            dispatch(PasswordResetAction.ResetEverything)
        }
        PageTitle(textAlign = TextAlign.Left, text = "Set a new password")
        PasswordInput(
            label = "Password",
            passwordState = state.passwordState,
            onPasswordChanged = {
                dispatch(PasswordResetAction.UpdateMemberPassword(it))
                dispatch(PasswordResetAction.CallStrengthCheck)
            },
        )
        StytchButton(
            onClick = { dispatch(PasswordResetAction.Submit) },
            modifier = Modifier.height(45.dp),
            text = stringResource(id = R.string.stytch_continue_button_text),
            enabled = state.passwordState.validPassword,
        )
    }
}
