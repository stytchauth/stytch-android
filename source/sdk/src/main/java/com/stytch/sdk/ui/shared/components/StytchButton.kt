package com.stytch.sdk.ui.shared.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import com.stytch.sdk.ui.shared.theme.LocalStytchTypography

@Composable
internal fun StytchButton(
    modifier: Modifier = Modifier,
    text: String = "",
    enabled: Boolean,
    onClick: () -> Unit = { },
) {
    val theme = LocalStytchTheme.current
    val type = LocalStytchTypography.current
    Button(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        enabled = enabled,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Color(theme.buttonBackgroundColor),
                disabledContainerColor = Color(theme.disabledButtonBackgroundColor),
                contentColor = Color(theme.buttonTextColor),
                disabledContentColor = Color(theme.disabledButtonTextColor),
            ),
        shape = RoundedCornerShape(theme.buttonBorderRadius),
    ) {
        Text(
            text = text,
            color = Color(if (enabled) theme.buttonTextColor else theme.disabledButtonTextColor),
            style = type.buttonLabel,
        )
    }
}
