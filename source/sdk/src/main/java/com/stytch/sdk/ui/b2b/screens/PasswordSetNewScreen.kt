package com.stytch.sdk.ui.b2b.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.ResetEverything
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.usecases.UsePasswordResetByEmailStart
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchTextButton
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import kotlinx.coroutines.flow.StateFlow

internal class PasswordSetNewScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    val usePasswordResetByEmailStart =
        UsePasswordResetByEmailStart(viewModelScope, state, ::dispatch, productConfig, ::request)
}

@Composable
internal fun PasswordSetNewScreen(
    state: State<B2BUIState>,
    createViewModel: CreateViewModel<PasswordSetNewScreenViewModel>,
    viewModel: PasswordSetNewScreenViewModel = createViewModel(PasswordSetNewScreenViewModel::class.java),
) {
    val theme = LocalStytchTheme.current
    BackHandler(enabled = true) {
        viewModel.dispatch(ResetEverything)
    }
    Column {
        BackButton {
            viewModel.dispatch(ResetEverything)
        }
        PageTitle(textAlign = TextAlign.Left, text = "Check your email!")
        BodyText(
            text = "A login link was sent to you at ${state.value.emailState.emailAddress}",
            color = Color(theme.secondaryTextColor),
        )
        StytchTextButton(
            text = "Didn't get it? Resend email",
            color = theme.secondaryTextColor,
            onClick = { viewModel.usePasswordResetByEmailStart() },
        )
    }
}
