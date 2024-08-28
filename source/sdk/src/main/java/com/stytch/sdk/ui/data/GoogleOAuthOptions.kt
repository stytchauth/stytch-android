package com.stytch.sdk.ui.data

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * A data class that represents the configuration for Google OAuth
 * @property clientId the client ID you used to configure Google OAuth
 */
@Parcelize
@Keep
@JsonClass(generateAdapter = true)
public data class GoogleOAuthOptions
    @JvmOverloads
    constructor(
        val clientId: String? = null,
    ) : Parcelable
