package com.stytch.exampleapp.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

val LightThemeColors =
    lightColors(
        primary = Purple200,
        primaryVariant = Purple700,
        onPrimary = White,
        secondary = White,
        onSecondary = Black,
        background = White,
        onBackground = Black,
        surface = White,
        onSurface = Black,
        error = Red800,
        onError = White,
    )

val DarkThemeColors =
    darkColors(
        primary = Purple200,
        primaryVariant = Purple500,
        onPrimary = Black,
        secondary = Black,
        onSecondary = White,
        background = Black,
        onBackground = White,
        surface = Black,
        onSurface = White,
        error = Red300,
        onError = Black,
    )

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content:
        @Composable () -> Unit,
) {
    val colors =
        if (darkTheme) {
            DarkThemeColors
        } else {
            LightThemeColors
        }
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
