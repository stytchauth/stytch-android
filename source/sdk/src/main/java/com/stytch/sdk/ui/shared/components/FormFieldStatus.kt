package com.stytch.sdk.ui.shared.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import com.stytch.sdk.ui.shared.theme.LocalStytchTypography
import kotlinx.coroutines.delay

@Composable
internal fun FormFieldStatus(
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    text: String,
    textAlign: TextAlign = TextAlign.Start,
    autoDismiss: (() -> Unit)? = null,
) {
    val theme = LocalStytchTheme.current
    val type = LocalStytchTypography.current
    LaunchedEffect(Unit) {
        if (autoDismiss != null) {
            delay(5000L)
            autoDismiss.invoke()
        }
    }
    Text(
        text = text,
        style =
            type.caption.copy(
                textAlign = textAlign,
                color = Color(if (isError) theme.errorColor else theme.successColor),
            ),
        modifier = modifier.fillMaxWidth().padding(bottom = 24.dp),
    )
}
