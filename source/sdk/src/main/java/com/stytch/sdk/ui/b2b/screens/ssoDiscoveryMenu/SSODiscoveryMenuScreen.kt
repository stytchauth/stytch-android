package com.stytch.sdk.ui.b2b.screens.ssoDiscoveryMenu

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
import com.stytch.sdk.ui.b2b.data.ResetEverything
import com.stytch.sdk.ui.b2b.data.SSODiscoveryState
import com.stytch.sdk.ui.b2b.extensions.toPainterResource
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.SocialLoginButton

@Composable
internal fun SSODiscoveryMenuScreen(viewModel: SSODiscoveryMenuScreenViewModel) {
    val state = viewModel.state.collectAsState()
    SSODiscoveryMenuScreenComposable(
        state = state.value.ssoDiscoveryState,
        dispatch = viewModel::handle,
    )
}

@Composable
private fun SSODiscoveryMenuScreenComposable(
    state: SSODiscoveryState,
    dispatch: (SSODiscoveryMenuScreenAction) -> Unit,
) {
    val activity = LocalActivity.current as Activity
    BackHandler(enabled = true) {
        dispatch(SSODiscoveryMenuScreenAction.ResetEverything)
    }
    Column {
        BackButton {
            dispatch(SSODiscoveryMenuScreenAction.ResetEverything)
        }
        PageTitle(textAlign = TextAlign.Left, text = "Select a connection to continue")
        state.connections.map { provider ->
            SocialLoginButton(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                text = "Continue with ${provider.displayName}",
                iconDrawable = provider.toPainterResource(),
                iconDescription = provider.displayName,
                onClick = { dispatch(SSODiscoveryMenuScreenAction.SSOStart(activity, provider.connectionId)) },
            )
        }
    }
}
