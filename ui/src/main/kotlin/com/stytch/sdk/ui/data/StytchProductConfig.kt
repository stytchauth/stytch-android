package com.stytch.sdk.ui.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
public data class StytchProductConfig(
    val products: List<StytchProduct>,
    val emailMagicLinksOptions: EmailMagicLinksOptions? = null,
    val oAuthOptions: OAuthOptions? = null,
    val otpOptions: OTPOptions? = null,
    val sessionOptions: SessionOptions? = null,
    val passwordOptions: PasswordOptions? = null,
) : Parcelable