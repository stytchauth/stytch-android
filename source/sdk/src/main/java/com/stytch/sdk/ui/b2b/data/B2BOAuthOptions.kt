package com.stytch.sdk.ui.b2b.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
@JacocoExcludeGenerated
public data class B2BOAuthOptions(
    val providers: List<B2BOAuthProviderConfig> = emptyList(),
) : Parcelable
