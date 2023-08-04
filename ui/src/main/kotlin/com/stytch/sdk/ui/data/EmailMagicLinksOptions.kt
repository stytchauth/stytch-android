package com.stytch.sdk.ui.data

import android.os.Parcelable
import com.stytch.sdk.consumer.magicLinks.MagicLinks
import kotlinx.parcelize.Parcelize

@Parcelize
public data class EmailMagicLinksOptions(
    val loginRedirectURL: String? = null,
    val loginExpirationMinutes: UInt? = null,
    val signupRedirectURL: String? = null,
    val signupExpirationMinutes: UInt? = null,
    val loginTemplateId: String? = null,
    val signupTemplateId: String? = null,
    val createUserAsPending: Boolean = false,
    val domainHint: String? = null,
) : Parcelable {
    internal fun toParameters(emailAddress: String) = MagicLinks.EmailMagicLinks.Parameters(
        email = emailAddress,
        loginMagicLinkUrl = loginRedirectURL,
        signupMagicLinkUrl = signupRedirectURL,
        loginExpirationMinutes = loginExpirationMinutes,
        signupExpirationMinutes = signupExpirationMinutes,
        loginTemplateId = loginTemplateId,
        signupTemplateId = signupTemplateId,
    )
}
