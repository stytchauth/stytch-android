package com.stytch.sdk.ui.b2b.screens.ssoDiscoveryEmail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2b.data.ResetEverything
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.EmailInput
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchButton
import com.stytch.sdk.ui.shared.data.EmailState

@Composable
internal fun SSODiscoveryEmailScreen(viewModel: SSODiscoveryEmailScreenViewModel) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    SSODiscoveryEmailScreenComposable(
        state = state.value.emailState,
        dispatch = viewModel::handle,
    )
}

@Composable
private fun SSODiscoveryEmailScreenComposable(
    state: EmailState,
    dispatch: (SSODiscoveryEmailScreenAction) -> Unit,
) {
    BackHandler(enabled = true) {
        dispatch(SSODiscoveryEmailScreenAction.ResetEverything)
    }
    Column {
        BackButton {
            dispatch(SSODiscoveryEmailScreenAction.ResetEverything)
        }
        PageTitle(textAlign = TextAlign.Left, text = stringResource(R.string.stytch_b2b_sso_discovery_email_title))
        EmailInput(
            modifier = Modifier.fillMaxWidth(),
            emailState = state,
            onEmailAddressChanged = {
                dispatch(
                    SSODiscoveryEmailScreenAction.UpdateMemberEmailAddress(
                        it,
                    ),
                )
            },
            keyboardActions =
                KeyboardActions(onDone = {
                    dispatch(SSODiscoveryEmailScreenAction.SetEmailShouldBeValidated)
                    dispatch(SSODiscoveryEmailScreenAction.UseSSODiscoveryConnections)
                }),
        )
        Spacer(modifier = Modifier.height(16.dp))
        StytchButton(
            enabled = state.validEmail == true,
            text = stringResource(R.string.stytch_continue_button_text),
            onClick = { dispatch(SSODiscoveryEmailScreenAction.UseSSODiscoveryConnections) },
        )
    }
}
