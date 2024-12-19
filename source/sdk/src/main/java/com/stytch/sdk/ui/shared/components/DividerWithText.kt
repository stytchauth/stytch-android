package com.stytch.sdk.ui.shared.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import com.stytch.sdk.ui.shared.theme.LocalStytchTypography

@Composable
internal fun DividerWithText(
    modifier: Modifier = Modifier,
    text: String,
) {
    val theme = LocalStytchTheme.current
    val type = LocalStytchTypography.current
    Row(
        modifier = modifier.fillMaxWidth(1f).height(25.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color(theme.disabledTextColor))
        Text(
            text = text,
            modifier = Modifier.weight(0.5f),
            style =
                type.body.copy(
                    textAlign = TextAlign.Center,
                    color = Color(theme.disabledTextColor),
                ),
        )
        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color(theme.disabledTextColor))
    }
}
