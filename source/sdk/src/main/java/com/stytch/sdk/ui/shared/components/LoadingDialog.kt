package com.stytch.sdk.ui.shared.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.window.Dialog
import com.stytch.sdk.R
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme

@Composable
internal fun LoadingDialog() {
    val theme = LocalStytchTheme.current
    val semantics = stringResource(id = R.string.semantics_loading_dialog)
    Dialog(onDismissRequest = {}) {
        CircularProgressIndicator(
            modifier = Modifier.semantics { contentDescription = semantics },
            color = Color(theme.inputTextColor),
        )
    }
}
