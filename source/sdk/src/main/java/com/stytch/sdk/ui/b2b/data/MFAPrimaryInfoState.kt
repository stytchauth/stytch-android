package com.stytch.sdk.ui.b2b.data

import android.os.Parcelable
import com.stytch.sdk.b2b.network.models.MfaMethod
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class MFAPrimaryInfoState(
    val enrolledMfaMethods: List<MfaMethod>,
    val memberId: String,
    val memberPhoneNumber: String? = null,
    val organizationId: String,
    val organizationMfaOptionsSupported: List<MfaMethod>,
) : Parcelable
