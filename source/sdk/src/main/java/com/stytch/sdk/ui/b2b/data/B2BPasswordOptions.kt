package com.stytch.sdk.ui.b2b.data

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
@JsonClass(generateAdapter = true)
@JacocoExcludeGenerated
public data class B2BPasswordOptions(
    val resetPasswordTemplateId: String? = null,
    val verifyEmailTemplateId: String? = null,
) : Parcelable
