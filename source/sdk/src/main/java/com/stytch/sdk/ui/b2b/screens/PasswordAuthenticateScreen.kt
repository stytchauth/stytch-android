package com.stytch.sdk.ui.b2b.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.ResetEverything
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.b2b.usecases.UsePasswordAuthenticate
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberEmailAddress
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberPassword
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.Body2Text
import com.stytch.sdk.ui.shared.components.EmailAndPasswordEntry
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import kotlinx.coroutines.flow.StateFlow

internal class PasswordAuthenticateScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    val useUpdateMemberPassword = UseUpdateMemberPassword(state, ::dispatch)
    val useUpdateMemberEmailAddress = UseUpdateMemberEmailAddress(state, ::dispatch)
    val usePasswordAuthenticate = UsePasswordAuthenticate(viewModelScope, state, ::dispatch, productConfig, ::request)
}

@Composable
internal fun PasswordAuthenticateScreen(
    state: State<B2BUIState>,
    createViewModel: CreateViewModel<PasswordAuthenticateScreenViewModel>,
    viewModel: PasswordAuthenticateScreenViewModel = createViewModel(PasswordAuthenticateScreenViewModel::class.java),
) {
    val theme = LocalStytchTheme.current
    Column {
        BackButton { viewModel.dispatch(ResetEverything) }
        PageTitle(textAlign = TextAlign.Left, text = "Log in with email and password")
        EmailAndPasswordEntry(
            emailState = state.value.emailState,
            onEmailAddressChanged = { viewModel.useUpdateMemberEmailAddress(it) },
            passwordState = state.value.passwordState,
            onPasswordChanged = { viewModel.useUpdateMemberPassword(it) },
            allowInvalidSubmission = true,
            onSubmit = { viewModel.usePasswordAuthenticate() },
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
            modifier = Modifier.clickable { viewModel.dispatch(SetNextRoute(Routes.PasswordForgot)) },
        )
    }
}
