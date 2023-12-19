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
internal fun PageTitle(
    modifier: Modifier = Modifier,
    text: String,
    textAlign: TextAlign = TextAlign.Center,
    color: Color? = null,
) {
    val theme = LocalStytchTheme.current
    val type = LocalStytchTypography.current
    Text(
        text = text,
        style = type.title.copy(
            textAlign = textAlign,
            color = color ?: Color(theme.primaryTextColor),
        ),
        modifier = modifier.fillMaxWidth().padding(bottom = 32.dp),
    )
}
