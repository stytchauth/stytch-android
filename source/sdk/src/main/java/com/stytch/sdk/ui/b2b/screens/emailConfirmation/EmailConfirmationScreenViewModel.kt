package com.stytch.sdk.ui.b2b.screens.emailConfirmation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.ResetEverything
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.usecases.UseSendCorrectPasswordReset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class EmailConfirmationScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    private val useSendCorrectPasswordReset =
        UseSendCorrectPasswordReset(viewModelScope, state, ::dispatch, productConfig, ::request, ::request)

    internal var route: EmailConfirmationScreenRoute? = null
        set(value) {
            field = value
            emitState()
        }

    private var _emailConfirmationScreenState =
        MutableStateFlow<EmailConfirmationScreenState>(EmailConfirmationScreenState.Loading)

    val emailConfirmationScreenState = _emailConfirmationScreenState.asStateFlow()

    private fun resetEverything() = dispatch(ResetEverything)

    private fun resendPasswordResetEmail() = useSendCorrectPasswordReset()

    private fun emitState() {
        route?.let {
            val formattedEmailAddress =
                AnnotatedString(
                    text = state.value.emailState.emailAddress,
                    spanStyle = SpanStyle(fontWeight = FontWeight.W700),
                )
            _emailConfirmationScreenState.value = it.toState(formattedEmailAddress)
        }
    }

    fun onBottomTextClicked() {
        when (route) {
            EmailConfirmationScreenRoute.EmailConfirmation -> resetEverything()
            EmailConfirmationScreenRoute.PasswordSetNewConfirmation,
            EmailConfirmationScreenRoute.PasswordResetVerifyConfirmation,
            -> resendPasswordResetEmail()
            else -> return
        }
    }
}

internal sealed class EmailConfirmationScreenState {
    data object Loading : EmailConfirmationScreenState()

    @JacocoExcludeGenerated
    data class Ready(
        val title: String,
        val message: AnnotatedString,
        val bottomText: AnnotatedString,
    ) : EmailConfirmationScreenState()
}

internal sealed class EmailConfirmationScreenRoute {
    abstract fun toState(formattedEmailAddress: AnnotatedString): EmailConfirmationScreenState.Ready

    data object EmailConfirmation : EmailConfirmationScreenRoute() {
        override fun toState(formattedEmailAddress: AnnotatedString) =
            EmailConfirmationScreenState.Ready(
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
            )
    }

    data object PasswordSetNewConfirmation : EmailConfirmationScreenRoute() {
        override fun toState(formattedEmailAddress: AnnotatedString) =
            EmailConfirmationScreenState.Ready(
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
            )
    }

    data object PasswordResetVerifyConfirmation : EmailConfirmationScreenRoute() {
        override fun toState(formattedEmailAddress: AnnotatedString) =
            EmailConfirmationScreenState.Ready(
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
            )
    }
}
