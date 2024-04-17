package com.stytch.sdk.ui.data

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass
import com.stytch.sdk.consumer.magicLinks.MagicLinks
import kotlinx.parcelize.Parcelize

/**
 * A data class that defines ptions for configuring Email Magic Links
 * @property loginExpirationMinutes The number of minutes before a login link expires
 * @property signupExpirationMinutes The number of minutes before a signup link expires
 * @property loginTemplateId The ID of an email template (defined in the Stytch Dashboard) for login emails
 * @property signupTemplateId The ID of an email template (defined in the Stytch Dashboard) for signup emails
 */
@Parcelize
@Keep
@JsonClass(generateAdapter = true)
public data class EmailMagicLinksOptions(
    val loginExpirationMinutes: Int? = null,
    val signupExpirationMinutes: Int? = null,
    val loginTemplateId: String? = null,
    val signupTemplateId: String? = null,
) : Parcelable {
    internal fun toParameters(
        emailAddress: String,
        publicToken: String,
    ) = MagicLinks.EmailMagicLinks.Parameters(
        email = emailAddress,
        loginMagicLinkUrl = "stytchui-$publicToken://deeplink",
        signupMagicLinkUrl = "stytchui-$publicToken://deeplink",
        loginExpirationMinutes = loginExpirationMinutes?.toUInt(),
        signupExpirationMinutes = signupExpirationMinutes?.toUInt(),
        loginTemplateId = loginTemplateId,
        signupTemplateId = signupTemplateId,
    )
}
