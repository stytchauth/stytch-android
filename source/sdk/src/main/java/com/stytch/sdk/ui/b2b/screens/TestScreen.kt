package com.stytch.sdk.ui.b2b.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.usecases.UsePasswordsStrengthCheck
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberEmailAddress
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberPassword
import com.stytch.sdk.ui.b2b.utils.B2BUIViewModelFactory
import com.stytch.sdk.ui.shared.components.EmailAndPasswordEntry
import com.stytch.sdk.ui.shared.components.PageTitle

internal class TestScreenViewModel(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(savedStateHandle) {
    val useUpdateMemberEmailAddress = UseUpdateMemberEmailAddress(state, ::dispatch)
    val useUpdateMemberPassword = UseUpdateMemberPassword(state, ::dispatch)
    val usePasswordsStrengthCheck = UsePasswordsStrengthCheck(viewModelScope, state, ::dispatch, ::request)
}

@Composable
internal fun TestScreen(savedStateHandle: SavedStateHandle) {
    val viewModel: TestScreenViewModel =
        remember {
            B2BUIViewModelFactory(savedStateHandle).create(TestScreenViewModel::class.java)
        }
    val currentState = viewModel.state.collectAsState()
    Column {
        PageTitle(text = "Testing VMs and UseCases")
        EmailAndPasswordEntry(
            emailState = currentState.value.emailState,
            onEmailAddressChanged = { viewModel.useUpdateMemberEmailAddress(it) },
            passwordState = currentState.value.passwordState,
            onPasswordChanged = {
                viewModel.useUpdateMemberPassword(it)
                viewModel.usePasswordsStrengthCheck()
            },
            onSubmit = {
                println("DONE")
            },
        )
    }
}
