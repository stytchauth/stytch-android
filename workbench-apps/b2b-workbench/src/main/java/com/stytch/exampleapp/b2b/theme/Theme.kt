package com.stytch.exampleapp.b2b.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

val LightThemeColors =
    lightColorScheme(
        primary = Purple200,
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
    darkColorScheme(
        primary = Purple200,
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
        colorScheme = colors,
        typography = Typography(),
        shapes = Shapes,
        content = content,
    )
}
