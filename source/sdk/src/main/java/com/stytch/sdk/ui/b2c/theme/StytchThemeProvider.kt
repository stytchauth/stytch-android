package com.stytch.sdk.ui.b2c.theme

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
import com.stytch.sdk.common.network.models.BootstrapData
import com.stytch.sdk.ui.b2c.data.StytchProductConfig
import com.stytch.sdk.ui.b2c.data.StytchUIConfig
import com.stytch.sdk.ui.shared.data.StytchTheme
import com.stytch.sdk.ui.shared.theme.StytchTypography

@Composable
internal fun StytchThemeProvider(
    darkTheme: Boolean = isSystemInDarkTheme(),
    config: StytchUIConfig,
    content: @Composable () -> Unit,
) {
    val stytchTheme =
        when {
            darkTheme -> config.styles.darkTheme
            else -> config.styles.lightTheme
        }
    val view = LocalView.current
    val rememberedStytchTheme = remember { stytchTheme }
    val rememberedStytchTypography = remember { StytchTypography() }
    val rememberedStytchProductConfig = remember { config.productConfig }
    val rememberedStytchBootstrapData = remember { config.bootstrapData }

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = stytchTheme.backgroundColor
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    CompositionLocalProvider(
        LocalStytchTheme provides rememberedStytchTheme,
        LocalStytchTypography provides rememberedStytchTypography,
        LocalStytchProductConfig provides rememberedStytchProductConfig,
        LocalStytchBootstrapData provides rememberedStytchBootstrapData,
    ) {
        content()
    }
}

internal val LocalStytchTheme: ProvidableCompositionLocal<StytchTheme> =
    staticCompositionLocalOf {
        StytchTheme.defaultLightTheme()
    }

internal val LocalStytchTypography: ProvidableCompositionLocal<StytchTypography> =
    staticCompositionLocalOf {
        StytchTypography()
    }

internal val LocalStytchProductConfig: ProvidableCompositionLocal<StytchProductConfig> =
    staticCompositionLocalOf {
        StytchProductConfig()
    }

internal val LocalStytchBootstrapData: ProvidableCompositionLocal<BootstrapData> =
    staticCompositionLocalOf {
        BootstrapData()
    }
