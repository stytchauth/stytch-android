package com.stytch.sdk.ui.shared.data

import android.os.Parcelable
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import kotlinx.parcelize.Parcelize

/**
 * A data class that specifies the styles used by the UI components
 * @param darkTheme a [StytchTheme] instance that defines the styles when in dark mode
 * @param lightTheme a [StytchTheme] instance that defines the styles when in light mode
 */
@Parcelize
@JacocoExcludeGenerated
public data class StytchStyles
    @JvmOverloads
    constructor(
        val darkTheme: StytchTheme = StytchTheme.defaultDarkTheme(),
        val lightTheme: StytchTheme = StytchTheme.defaultLightTheme(),
    ) : Parcelable
