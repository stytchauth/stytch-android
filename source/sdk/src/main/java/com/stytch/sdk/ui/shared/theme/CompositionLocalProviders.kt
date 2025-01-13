package com.stytch.sdk.ui.shared.theme

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.stytch.sdk.common.network.models.BootstrapData
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2c.data.StytchProductConfig
import com.stytch.sdk.ui.shared.data.StytchTheme

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

internal val LocalStytchB2BProductConfig: ProvidableCompositionLocal<StytchB2BProductConfig> =
    staticCompositionLocalOf {
        StytchB2BProductConfig()
    }

internal val LocalStytchBootstrapData: ProvidableCompositionLocal<BootstrapData> =
    staticCompositionLocalOf {
        BootstrapData()
    }
