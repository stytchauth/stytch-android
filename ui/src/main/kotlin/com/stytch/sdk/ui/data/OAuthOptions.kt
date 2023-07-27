package com.stytch.sdk.ui.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
public data class OAuthOptions(
    val loginRedirectURL: String? = null,
    val signupRedirectURL: String? = null,
    val providers: List<OAuthProviders>,
) : Parcelable