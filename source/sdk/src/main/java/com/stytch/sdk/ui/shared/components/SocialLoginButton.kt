package com.stytch.sdk.ui.shared.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import com.stytch.sdk.ui.shared.theme.LocalStytchTypography

@Composable
internal fun SocialLoginButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    iconDrawable: Painter? = null,
    iconDescription: String? = null,
    text: String,
) {
    val theme = LocalStytchTheme.current
    val type = LocalStytchTypography.current
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, Color(theme.inputBorderColor)),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Color(theme.socialButtonBackgroundColor),
                disabledContainerColor = Color(theme.socialButtonBackgroundColor),
            ),
        shape = RoundedCornerShape(theme.buttonBorderRadius),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(45.dp),
        ) {
            if (iconDrawable != null) {
                Image(
                    painter = iconDrawable,
                    contentDescription = iconDescription,
                    modifier = Modifier.width(24.dp).padding(end = 4.dp),
                )
            }
        }
        Text(
            text = text,
            style =
                type.buttonLabel.copy(
                    lineHeight = 45.sp,
                    color = Color(theme.socialButtonTextColor),
                ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
