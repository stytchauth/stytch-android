package com.stytch.sdk.ui.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
public data class StytchUIConfig(
    val productConfig: StytchProductConfig,
    val styles: StytchStyles
) : Parcelable