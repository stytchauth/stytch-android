package com.stytch.sdk.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.stytch.sdk.ui.components.BackButton
import com.stytch.sdk.ui.data.OTPMethods
import com.stytch.sdk.ui.theme.LocalStytchTheme
import com.stytch.sdk.ui.theme.LocalStytchTypography

internal data class OTPConfirmationScreen(
    val recipient: String,
    val method: OTPMethods
) : Screen {
    @Composable
    override fun Content() {
        val viewModel = viewModel<OTPConfirmationScreenViewModel>()
        OTPConfirmationScreenComposable(
            viewModel = viewModel,
            recipient = recipient,
            method = method,
        )
    }
}

@Composable
private fun OTPConfirmationScreenComposable(
    viewModel: OTPConfirmationScreenViewModel,
    recipient: String,
    method: OTPMethods
) {
    val navigator = LocalNavigator.currentOrThrow
    val theme = LocalStytchTheme.current
    val type = LocalStytchTypography.current
    Column {
        BackButton {
            navigator.pop()
        }
    }
}
