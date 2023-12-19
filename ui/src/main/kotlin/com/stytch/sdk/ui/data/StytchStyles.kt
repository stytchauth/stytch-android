package com.stytch.sdk.ui.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
public data class StytchStyles(
    val darkTheme: StytchTheme = StytchTheme.defaultDarkTheme(),
    val lightTheme: StytchTheme = StytchTheme.defaultLightTheme(),
) : Parcelable
