package com.stytch.sdk.ui.b2b.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.ResetEverything
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.navigation.Route
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.b2b.usecases.UsePasswordResetByEmailStart
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchTextButton
import kotlinx.coroutines.flow.StateFlow

internal class EmailConfirmationScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    val usePasswordResetByEmailStart = UsePasswordResetByEmailStart(viewModelScope, state, productConfig, ::request)

    fun resetEverything() = dispatch(ResetEverything)
}

@Composable
internal fun EmailConfirmationScreen(
    state: State<B2BUIState>,
    route: Route,
    createViewModel: CreateViewModel<EmailConfirmationScreenViewModel>,
    viewModel: EmailConfirmationScreenViewModel = createViewModel(EmailConfirmationScreenViewModel::class.java),
) {
    val formattedEmailAddress =
        AnnotatedString(
            text = state.value.emailState.emailAddress,
            spanStyle = SpanStyle(fontWeight = FontWeight.W700),
        )
    if (route == Routes.EmailConfirmation) {
        return EmailVerification(
            title = "Check your email",
            message =
                buildAnnotatedString {
                    append("An email was sent to ")
                    append(formattedEmailAddress)
                },
            bottomText =
                buildAnnotatedString {
                    append("Didn't get it? ")
                    append(
                        AnnotatedString(
                            text = "Try Again.",
                            spanStyle = SpanStyle(fontWeight = FontWeight.W700),
                        ),
                    )
                },
            onBottomTextClicked = viewModel::resetEverything,
        )
    }

    if (route == Routes.PasswordSetNewConfirmation) {
        return EmailVerification(
            title = "Check your email!",
            message =
                buildAnnotatedString {
                    append("A login link was sent to you at ")
                    append(formattedEmailAddress)
                },
            bottomText =
                buildAnnotatedString {
                    append("Didn't get it? ")
                    append(
                        AnnotatedString(
                            text = "Resend email.",
                            spanStyle = SpanStyle(fontWeight = FontWeight.W700),
                        ),
                    )
                },
            onBottomTextClicked = { viewModel.usePasswordResetByEmailStart() },
        )
    }

    if (route == Routes.PasswordResetVerifyConfirmation) {
        return EmailVerification(
            title = "Please verify your email",
            message =
                buildAnnotatedString {
                    append("A login link was sent to you at ")
                    append(formattedEmailAddress)
                },
            bottomText =
                buildAnnotatedString {
                    append("Didn't get it? ")
                    append(
                        AnnotatedString(
                            text = "Resend email.",
                            spanStyle = SpanStyle(fontWeight = FontWeight.W700),
                        ),
                    )
                },
            onBottomTextClicked = viewModel::resetEverything,
        )
    }
}

@Composable
private fun EmailVerification(
    title: String,
    message: AnnotatedString,
    bottomText: AnnotatedString,
    onBottomTextClicked: () -> Unit,
) {
    Column {
        PageTitle(text = title)
        BodyText(text = message)
        StytchTextButton(text = bottomText.text, onClick = onBottomTextClicked)
    }
}
