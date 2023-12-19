package com.stytch.sdk.ui.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
public data class StytchProductConfig(
    val products: List<StytchProduct> = listOf(
        StytchProduct.EMAIL_MAGIC_LINKS,
        StytchProduct.PASSWORDS,
        StytchProduct.OTP,
    ),
    val emailMagicLinksOptions: EmailMagicLinksOptions = EmailMagicLinksOptions(),
    val oAuthOptions: OAuthOptions = OAuthOptions(),
    val otpOptions: OTPOptions = OTPOptions(),
    val sessionOptions: SessionOptions = SessionOptions(),
    val passwordOptions: PasswordOptions = PasswordOptions(),
    val googleOauthOptions: GoogleOAuthOptions = GoogleOAuthOptions(),
) : Parcelable
