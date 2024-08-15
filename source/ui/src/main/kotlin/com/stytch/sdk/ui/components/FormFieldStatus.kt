package com.stytch.sdk.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stytch.sdk.ui.theme.LocalStytchTheme
import com.stytch.sdk.ui.theme.LocalStytchTypography

@Composable
internal fun FormFieldStatus(
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    text: String,
    textAlign: TextAlign = TextAlign.Start,
) {
    val theme = LocalStytchTheme.current
    val type = LocalStytchTypography.current
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
