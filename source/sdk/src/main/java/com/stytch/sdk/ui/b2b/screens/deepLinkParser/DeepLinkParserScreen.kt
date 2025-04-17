package com.stytch.sdk.ui.b2b.screens.deepLinkParser

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.stytch.sdk.common.DeeplinkTokenPair
import com.stytch.sdk.ui.shared.components.LoadingDialog

@Composable
internal fun DeepLinkParserScreen(
    viewModel: DeepLinkParserScreenViewModel,
    deepLinkTokenPair: DeeplinkTokenPair? = null,
) {
    LaunchedEffect(deepLinkTokenPair) {
        deepLinkTokenPair?.let {
            viewModel.handleDeepLink(it)
        }
    }
    LoadingDialog()
}
