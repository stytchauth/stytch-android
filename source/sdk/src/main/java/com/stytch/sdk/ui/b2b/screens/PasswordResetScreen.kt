package com.stytch.sdk.ui.b2b.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.usecases.UsePasswordResetByEmail
import com.stytch.sdk.ui.b2b.usecases.UsePasswordsStrengthCheck
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberPassword
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.PasswordInput
import com.stytch.sdk.ui.shared.components.StytchButton
import kotlinx.coroutines.flow.StateFlow

internal class PasswordResetScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
) : BaseViewModel(state, dispatchAction) {
    val usePasswordResetByEmail = UsePasswordResetByEmail(viewModelScope, state, ::request)
    val useUpdateMemberPassword = UseUpdateMemberPassword(state, ::dispatch)
    val usePasswordStrengthCheck = UsePasswordsStrengthCheck(viewModelScope, state, ::dispatch, ::request)
}

@Composable
internal fun PasswordResetScreen(
    state: State<B2BUIState>,
    createViewModel: CreateViewModel<PasswordResetScreenViewModel>,
    viewModel: PasswordResetScreenViewModel = createViewModel(PasswordResetScreenViewModel::class.java),
) {
    Column {
        PageTitle(text = "Set a new password")
        PasswordInput(
            label = "Password",
            passwordState = state.value.passwordState,
            onPasswordChanged = {
                viewModel.useUpdateMemberPassword(it)
                viewModel.usePasswordStrengthCheck()
            },
        )
        StytchButton(
            onClick = { viewModel.usePasswordResetByEmail() },
            modifier = Modifier.height(45.dp),
            text = stringResource(id = R.string.button_continue),
            enabled = state.value.passwordState.validPassword,
        )
    }
}