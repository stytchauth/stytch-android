package com.stytch.sdk.ui.shared.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.stytch.sdk.R
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme

@Composable
internal fun BackButton(onClick: () -> Unit) {
    val theme = LocalStytchTheme.current
    val backButtonText = stringResource(id = R.string.stytch_back_button)
    IconButton(
        modifier = Modifier.padding(bottom = 24.dp).semantics { contentDescription = backButtonText },
        colors =
            IconButtonDefaults.iconButtonColors(
                contentColor = Color(theme.primaryTextColor),
            ),
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.arrow_back),
            contentDescription = backButtonText,
        )
    }
}
