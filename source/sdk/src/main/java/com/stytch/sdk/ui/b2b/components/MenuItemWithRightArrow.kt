package com.stytch.sdk.ui.b2b.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import com.stytch.sdk.ui.shared.theme.LocalStytchTypography

@Composable
internal fun MenuItemWithRightArrow(
    title: String,
    onClick: () -> Unit,
) {
    val theme = LocalStytchTheme.current
    val type = LocalStytchTypography.current
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style =
                type.body.copy(
                    color = Color(theme.primaryTextColor),
                    fontWeight = FontWeight.Bold,
                ),
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(theme.primaryTextColor),
        )
    }
}
