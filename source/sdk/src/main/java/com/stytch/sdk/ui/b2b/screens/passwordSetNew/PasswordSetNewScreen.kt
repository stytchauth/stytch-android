package com.stytch.sdk.ui.b2b.screens.passwordSetNew

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.stytch.sdk.ui.b2b.data.ResetEverything
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchTextButton
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme

@Composable
internal fun PasswordSetNewScreen(viewModel: PasswordSetNewScreenViewModel) {
    val state = viewModel.passwordSetNewScreenState.collectAsState()
    PasswordSetNewScreenComposable(
        state = state.value,
        dispatch = viewModel::handle,
    )
}

@Composable
private fun PasswordSetNewScreenComposable(
    state: PasswordSetNewScreenState,
    dispatch: (PasswordSetNewAction) -> Unit,
) {
    val theme = LocalStytchTheme.current
    BackHandler(enabled = true) {
        dispatch(PasswordSetNewAction.DispatchGlobalAction(ResetEverything))
    }
    Column {
        BackButton {
            dispatch(PasswordSetNewAction.DispatchGlobalAction(ResetEverything))
        }
        PageTitle(textAlign = TextAlign.Left, text = "Check your email!")
        BodyText(
            text = "A login link was sent to you at ${state.emailState.emailAddress}",
            color = Color(theme.secondaryTextColor),
        )
        StytchTextButton(
            text = "Didn't get it? Resend email",
            color = theme.secondaryTextColor,
            onClick = { dispatch(PasswordSetNewAction.ResetByEmailStart) },
        )
    }
}
