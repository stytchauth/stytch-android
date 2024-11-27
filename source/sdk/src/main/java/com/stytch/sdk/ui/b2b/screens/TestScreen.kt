package com.stytch.sdk.ui.b2b.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.NavigateTo
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.b2b.usecases.UsePasswordsStrengthCheck
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberEmailAddress
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberPassword
import com.stytch.sdk.ui.shared.components.EmailAndPasswordEntry
import com.stytch.sdk.ui.shared.components.PageTitle
import kotlinx.coroutines.flow.StateFlow

internal class TestScreenViewModel(
    state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
) : BaseViewModel(state, dispatchAction) {
    val useUpdateMemberEmailAddress = UseUpdateMemberEmailAddress(state, ::dispatch)
    val useUpdateMemberPassword = UseUpdateMemberPassword(state, ::dispatch)
    val usePasswordsStrengthCheck = UsePasswordsStrengthCheck(viewModelScope, state, ::dispatch, ::request)
}

@Composable
internal fun TestScreen(
    state: State<B2BUIState>,
    createViewModel: CreateViewModel<TestScreenViewModel>,
    viewModel: TestScreenViewModel = createViewModel(TestScreenViewModel::class.java),
) {
    Column {
        PageTitle(text = "Test Screen 1")
        EmailAndPasswordEntry(
            emailState = state.value.emailState,
            onEmailAddressChanged = { viewModel.useUpdateMemberEmailAddress(it) },
            passwordState = state.value.passwordState,
            onPasswordChanged = {
                viewModel.useUpdateMemberPassword(it)
                viewModel.usePasswordsStrengthCheck()
            },
            onSubmit = {
                viewModel.dispatch(NavigateTo(Routes.Success))
            },
        )
    }
}
