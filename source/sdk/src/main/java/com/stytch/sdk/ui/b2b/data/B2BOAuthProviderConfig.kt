package com.stytch.sdk.ui.b2b.data

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
public data class B2BOAuthProviderConfig(
    val type: B2BOAuthProviders,
    val customScopes: List<String> = emptyList(),
    val providerParams: Map<String, String> = emptyMap(),
) : Parcelable