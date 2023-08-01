package com.stytch.sdk.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stytch.sdk.ui.theme.LocalStytchTheme
import com.stytch.sdk.ui.theme.LocalStytchTypography

@Composable
internal fun Body2Text(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    textAlign: TextAlign = TextAlign.Start,
    color: Color? = null,
) {
    val theme = LocalStytchTheme.current
    val type = LocalStytchTypography.current
    Text(
        text = text,
        style = type.body2.copy(
            textAlign = textAlign,
            color = color ?: Color(theme.primaryTextColor)
        ),
        modifier = modifier.fillMaxWidth().padding(bottom = 32.dp),
    )
}