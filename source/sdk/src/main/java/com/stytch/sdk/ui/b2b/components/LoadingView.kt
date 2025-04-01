package com.stytch.sdk.ui.b2b.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
internal fun LoadingView(color: Color) {
    CircularProgressIndicator(color = color)
}
