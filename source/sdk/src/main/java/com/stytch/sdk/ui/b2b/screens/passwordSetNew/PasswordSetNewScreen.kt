package com.stytch.sdk.ui.b2b.screens.passwordSetNew

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.stytch.sdk.R
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
        dispatch(PasswordSetNewAction.ResetEverything)
    }
    Column {
        BackButton {
            dispatch(PasswordSetNewAction.ResetEverything)
        }
        PageTitle(textAlign = TextAlign.Left, text = stringResource(R.string.stytch_b2b_check_your_email))
        BodyText(
            text = stringResource(R.string.stytch_b2b_a_login_link_was_sent_to_you_at, state.emailState.emailAddress),
            color = Color(theme.secondaryTextColor),
        )
        StytchTextButton(
            text = stringResource(R.string.stytch_b2b_didn_t_get_it_resend_email),
            color = theme.secondaryTextColor,
            onClick = { dispatch(PasswordSetNewAction.ResetByEmailStart) },
        )
    }
}
