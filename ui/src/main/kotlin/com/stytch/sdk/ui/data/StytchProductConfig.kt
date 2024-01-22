package com.stytch.sdk.ui.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * A data class representing the overall product configuration that you can pass to the UI SDK
 * @property products a list of [StytchProduct]s
 * @property emailMagicLinksOptions an instance of [EmailMagicLinksOptions]
 * @property oAuthOptions an instance of [OAuthOptions]
 * @property otpOptions an instance of [OTPOptions]
 * @property sessionOptions an instance of [SessionOptions]
 * @property passwordOptions an instance of [PasswordOptions]
 * @property googleOauthOptions an instance of [GoogleOAuthOptions]
 */
@Parcelize
@JsonClass(generateAdapter = true)
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
