package com.stytch.sdk.ui.data

import android.os.Parcelable
import com.stytch.sdk.common.network.models.BootstrapData
import kotlinx.parcelize.Parcelize

@Parcelize
public data class StytchUIConfig(
    val productConfig: StytchProductConfig,
    val styles: StytchStyles,
    val bootstrapData: BootstrapData,
    val publicToken: String,
) : Parcelable
