package com.stytch.sdk.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.stytch.sdk.ui.R
import com.stytch.sdk.ui.theme.LocalStytchTheme

@Composable
internal fun BackButton(
    onClick: () -> Unit
) {
    val theme = LocalStytchTheme.current
    IconButton(
        modifier = Modifier.padding(bottom = 24.dp),
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = Color(theme.primaryTextColor)
        ),
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id = R.drawable.arrow_back),
            contentDescription = stringResource(id = R.string.back)
        )
    }
}