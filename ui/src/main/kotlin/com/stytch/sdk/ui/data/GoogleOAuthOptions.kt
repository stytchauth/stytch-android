package com.stytch.sdk.ui.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
public data class GoogleOAuthOptions(
    val clientId: String? = null,
) : Parcelable
