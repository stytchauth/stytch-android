package com.stytch.sdk.ui.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import com.stytch.sdk.ui.theme.LocalStytchTheme

@Composable
internal fun LoadingDialog() {
    val theme = LocalStytchTheme.current
    Dialog(onDismissRequest = {}) {
        CircularProgressIndicator(
            color = Color(theme.inputTextColor)
        )
    }
}
