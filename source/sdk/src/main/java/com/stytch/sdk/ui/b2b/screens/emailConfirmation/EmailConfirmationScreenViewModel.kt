package com.stytch.sdk.ui.b2b.screens.emailConfirmation

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.R
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
            _emailConfirmationScreenState.value = it.toState(state.value.emailState.emailAddress)
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
        @StringRes val title: Int,
        @StringRes val message: Int,
        @StringRes val bottomText: Int,
        val emailAddress: String,
    ) : EmailConfirmationScreenState()
}

internal sealed class EmailConfirmationScreenRoute {
    abstract fun toState(emailAddress: String): EmailConfirmationScreenState.Ready

    data object EmailConfirmation : EmailConfirmationScreenRoute() {
        override fun toState(emailAddress: String) =
            EmailConfirmationScreenState.Ready(
                title = R.string.stytch_b2b_check_your_email_title,
                emailAddress = emailAddress,
                message = R.string.stytch_b2b_email_confirmation_body,
                bottomText = R.string.stytch_b2b_email_confirmation_try_again,
            )
    }

    data object PasswordSetNewConfirmation : EmailConfirmationScreenRoute() {
        override fun toState(emailAddress: String) =
            EmailConfirmationScreenState.Ready(
                title = R.string.stytch_b2b_check_your_email_title,
                emailAddress = emailAddress,
                message = R.string.stytch_b2b_email_sent_body,
                bottomText = R.string.stytch_b2b_email_confirmation_resend,
            )
    }

    data object PasswordResetVerifyConfirmation : EmailConfirmationScreenRoute() {
        override fun toState(emailAddress: String) =
            EmailConfirmationScreenState.Ready(
                title = R.string.stytch_b2b_email_verification_title,
                emailAddress = emailAddress,
                message = R.string.stytch_b2b_email_sent_body,
                bottomText = R.string.stytch_b2b_email_confirmation_resend,
            )
    }
}
