package com.stytch.sdk.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
public data class StytchProductConfig(
    val products: List<StytchProduct>,
    val emailMagicLinksOptions: EmailMagicLinksOptions,
    val oAuthOptions: OAuthOptions,
    val otpOptions: OTPOptions,
    val sessionOptions: SessionOptions,
    val passwordOptions: PasswordOptions,
) : Parcelable
