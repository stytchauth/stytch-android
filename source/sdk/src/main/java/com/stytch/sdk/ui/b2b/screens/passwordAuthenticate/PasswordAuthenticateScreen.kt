package com.stytch.sdk.ui.b2b.screens.passwordAuthenticate

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stytch.sdk.R
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.Body2Text
import com.stytch.sdk.ui.shared.components.EmailAndPasswordEntry
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import com.stytch.sdk.ui.shared.utils.getStyledText

@Composable
internal fun PasswordAuthenticateScreen(viewModel: PasswordAuthenticateScreenViewModel) {
    val state = viewModel.passwordAuthenticateScreenState.collectAsState()
    PasswordAuthenticateScreenComposable(
        state = state.value,
        dispatch = viewModel::handle,
    )
}

@Composable
private fun PasswordAuthenticateScreenComposable(
    state: PasswordAuthenticateScreenState,
    dispatch: (PasswordAuthenticateAction) -> Unit,
) {
    val theme = LocalStytchTheme.current
    val context = LocalContext.current
    BackHandler(enabled = true) {
        dispatch(PasswordAuthenticateAction.ResetEverything)
    }
    Column {
        BackButton {
            dispatch(PasswordAuthenticateAction.ResetEverything)
        }
        PageTitle(textAlign = TextAlign.Left, text = stringResource(R.string.stytch_b2b_password_login_title))
        EmailAndPasswordEntry(
            emailState = state.emailState,
            onEmailAddressChanged = { dispatch(PasswordAuthenticateAction.UpdateMemberEmailAddress(it)) },
            passwordState = state.passwordState,
            onPasswordChanged = { dispatch(PasswordAuthenticateAction.UpdateMemberPassword(it)) },
            allowInvalidSubmission = true,
            onSubmit = { dispatch(PasswordAuthenticateAction.Authenticate) },
        )
        Spacer(modifier = Modifier.height(16.dp))
        Body2Text(
            text = context.getStyledText(R.string.stytch_b2b_trouble_signing_in),
            color = Color(theme.secondaryTextColor),
            modifier =
                Modifier.clickable {
                    dispatch(PasswordAuthenticateAction.GoToPasswordForgot)
                },
        )
    }
}
