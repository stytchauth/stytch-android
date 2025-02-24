package com.stytch.sdk.ui.b2b.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.ResetEverything
import com.stytch.sdk.ui.b2b.usecases.UseSSODiscoveryConnections
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberEmailAddress
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberEmailShouldBeValidated
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.EmailInput
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchButton
import kotlinx.coroutines.flow.StateFlow

internal class SSODiscoveryEmailScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
) : BaseViewModel(state, dispatchAction) {
    val useUpdateMemberEmailAddress = UseUpdateMemberEmailAddress(state, ::dispatch)
    val useUpdateMemberEmailShouldBeValidated = UseUpdateMemberEmailShouldBeValidated(state, ::dispatch)
    val useSSODiscoveryConnections = UseSSODiscoveryConnections(viewModelScope, state, ::dispatch, ::request)
}

@Composable
internal fun SSODiscoveryEmailScreen(
    state: State<B2BUIState>,
    createViewModel: CreateViewModel<SSODiscoveryEmailScreenViewModel>,
    viewModel: SSODiscoveryEmailScreenViewModel = createViewModel(SSODiscoveryEmailScreenViewModel::class.java),
) {
    BackHandler(enabled = true) {
        viewModel.dispatch(ResetEverything)
    }
    Column {
        BackButton {
            viewModel.dispatch(ResetEverything)
        }
        PageTitle(textAlign = TextAlign.Left, text = "Enter your email to continue")
        EmailInput(
            modifier = Modifier.fillMaxWidth(),
            emailState = state.value.emailState,
            onEmailAddressChanged = { viewModel.useUpdateMemberEmailAddress(it) },
            keyboardActions =
                KeyboardActions(onDone = {
                    viewModel.useUpdateMemberEmailShouldBeValidated(true)
                    viewModel.useSSODiscoveryConnections()
                }),
        )
        Spacer(modifier = Modifier.height(16.dp))
        StytchButton(
            enabled = state.value.emailState.validEmail == true,
            text = "Continue",
            onClick = { viewModel.useSSODiscoveryConnections() },
        )
    }
}
