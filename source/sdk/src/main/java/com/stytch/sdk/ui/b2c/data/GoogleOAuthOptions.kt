package com.stytch.sdk.ui.b2c.data

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import kotlinx.parcelize.Parcelize

/**
 * A data class that represents the configuration for Google OAuth
 * @property clientId the client ID you used to configure Google OAuth
 */
@Parcelize
@Keep
@JsonClass(generateAdapter = true)
@JacocoExcludeGenerated
public data class GoogleOAuthOptions
    @JvmOverloads
    constructor(
        val clientId: String? = null,
    ) : Parcelable
