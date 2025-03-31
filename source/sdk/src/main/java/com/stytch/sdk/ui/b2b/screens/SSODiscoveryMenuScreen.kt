package com.stytch.sdk.ui.b2b.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.ResetEverything
import com.stytch.sdk.ui.b2b.usecases.UseSSOStart
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.SocialLoginButton
import kotlinx.coroutines.flow.StateFlow

internal class SSODiscoveryMenuScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
) : BaseViewModel(state, dispatchAction) {
    val useSSOStart = UseSSOStart()
}

@Composable
internal fun SSODiscoveryMenuScreen(
    createViewModel: CreateViewModel<SSODiscoveryMenuScreenViewModel>,
    viewModel: SSODiscoveryMenuScreenViewModel = createViewModel(SSODiscoveryMenuScreenViewModel::class.java),
) {
    val state = viewModel.state.collectAsState()
    val activity = LocalActivity.current as Activity
    BackHandler(enabled = true) {
        viewModel.dispatch(ResetEverything)
    }
    Column {
        BackButton {
            viewModel.dispatch(ResetEverything)
        }
        PageTitle(textAlign = TextAlign.Left, text = "Select a connection to continue")
        state.value.ssoDiscoveryState.connections.map { provider ->
            SocialLoginButton(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                text = "Continue with ${provider.displayName}",
                iconDrawable = provider.toPainterResource(),
                iconDescription = provider.displayName,
                onClick = { viewModel.useSSOStart(activity, provider.connectionId) },
            )
        }
    }
}
