package com.stytch.exampleapp.b2b.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StytchButton(
    modifier: Modifier = Modifier,
    text: String = "",
    onClick: () -> Unit = { },
) {
    Button(modifier = modifier.padding(horizontal = 10.dp, vertical = 4.dp), onClick = onClick) {
        Text(text = text)
    }
}
