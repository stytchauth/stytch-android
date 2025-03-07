package com.stytch.sdk.ui.b2b.data

import android.os.Parcelable
import com.stytch.sdk.b2b.network.models.SSOActiveConnection
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class SSODiscoveryState(
    val connections: List<SSOActiveConnection> = emptyList(),
) : Parcelable
