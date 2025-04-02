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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stytch.sdk.ui.b2b.data.ResetEverything
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.Body2Text
import com.stytch.sdk.ui.shared.components.EmailAndPasswordEntry
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme

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
    BackHandler(enabled = true) {
        dispatch(PasswordAuthenticateAction.DispatchGlobalAction(ResetEverything))
    }
    Column {
        BackButton {
            dispatch(PasswordAuthenticateAction.DispatchGlobalAction(ResetEverything))
        }
        PageTitle(textAlign = TextAlign.Left, text = "Log in with email and password")
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
            text =
                buildAnnotatedString {
                    append("Having trouble signing in? ")
                    append(
                        AnnotatedString(
                            text = "Get help",
                            spanStyle = SpanStyle(fontWeight = FontWeight.W700),
                        ),
                    )
                },
            color = Color(theme.secondaryTextColor),
            modifier =
                Modifier.clickable {
                    dispatch(PasswordAuthenticateAction.DispatchGlobalAction(SetNextRoute(Routes.PasswordForgot)))
                },
        )
    }
}
