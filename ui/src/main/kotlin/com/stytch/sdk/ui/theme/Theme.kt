package com.stytch.sdk.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.stytch.sdk.ui.data.StytchStyles
import com.stytch.sdk.ui.data.StytchTheme

@Composable
internal fun StytchAndroidSDKTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    stytchStyles: StytchStyles,
    content: @Composable () -> Unit,
) {
    val stytchTheme = when {
        darkTheme -> stytchStyles.darkTheme
        else -> stytchStyles.lightTheme
    }
    val view = LocalView.current
    val rememberedStytchTheme = remember { stytchTheme }
    val rememberedStytchTypography = remember { StytchTypography() }

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = stytchTheme.backgroundColor
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    CompositionLocalProvider(
        LocalStytchTheme provides rememberedStytchTheme,
        LocalStytchTypography provides rememberedStytchTypography
    ) {
        content()
    }
}

internal val LocalStytchTheme: ProvidableCompositionLocal<StytchTheme> = staticCompositionLocalOf {
    StytchTheme.defaultLightTheme()
}

internal val LocalStytchTypography: ProvidableCompositionLocal<StytchTypography> = staticCompositionLocalOf {
    StytchTypography()
}
