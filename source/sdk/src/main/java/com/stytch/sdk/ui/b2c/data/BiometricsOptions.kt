package com.stytch.sdk.ui.b2c.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
@JacocoExcludeGenerated
public data class BiometricsOptions
    @JvmOverloads
    constructor(
        val showBiometricRegistrationOnLogin: Boolean = false,
    ) : Parcelable
