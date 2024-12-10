package com.stytch.sdk.ui.shared.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import com.stytch.sdk.ui.shared.theme.LocalStytchTypography

@Composable
internal fun StytchTextButton(
    modifier: Modifier = Modifier.fillMaxWidth(),
    text: String,
    color: Int? = null,
    onClick: () -> Unit,
) {
    StytchTextButton(modifier = modifier, text = AnnotatedString(text), color = color, onClick = onClick)
}

@Composable
internal fun StytchTextButton(
    modifier: Modifier = Modifier.fillMaxWidth(),
    text: AnnotatedString,
    color: Int? = null,
    onClick: () -> Unit,
) {
    val type = LocalStytchTypography.current
    val theme = LocalStytchTheme.current
    TextButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Text(
            text = text,
            style =
                type.buttonLabel.copy(
                    color = Color(color ?: theme.primaryTextColor),
                    textAlign = TextAlign.Center,
                ),
        )
    }
}
