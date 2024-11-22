package com.stytch.sdk.ui.b2b.data

import android.os.Parcelable
import com.stytch.sdk.common.network.models.BootstrapData
import com.stytch.sdk.ui.shared.data.StytchStyles
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class StytchB2BUIConfig(
    val productConfig: StytchB2BProductConfig,
    val styles: StytchStyles,
    val bootstrapData: BootstrapData,
) : Parcelable
