package com.stytch.sdk.ui.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * A data class that represents the configuration for Google OAuth
 * @property clientId the client ID you used to configure Google OAuth
 */
@Parcelize
@JsonClass(generateAdapter = true)
public data class GoogleOAuthOptions(
    val clientId: String? = null,
) : Parcelable
