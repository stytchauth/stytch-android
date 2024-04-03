package com.stytch.sdk.ui.data

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass
import com.stytch.sdk.consumer.passwords.Passwords
import kotlinx.parcelize.Parcelize

/**
 * A data class representing options for configuring the Password product
 * @property loginExpirationMinutes the number of minutes that a login request token is valid for
 * @property resetPasswordExpirationMinutes the number of minutes that a reset request token is valid for
 * @property resetPasswordTemplateId The ID of an email template (defined in the Stytch Dashboard) for password resets
 */
@Parcelize
@Keep
@JsonClass(generateAdapter = true)
public data class PasswordOptions(
    val loginExpirationMinutes: Int? = null,
    val resetPasswordExpirationMinutes: Int? = null,
    val resetPasswordTemplateId: String? = null,
) : Parcelable {
    internal fun toResetByEmailStartParameters(
        emailAddress: String,
        publicToken: String,
    ) = Passwords.ResetByEmailStartParameters(
        email = emailAddress,
        loginRedirectUrl = "stytchui-$publicToken://deeplink",
        loginExpirationMinutes = loginExpirationMinutes?.toUInt(),
        resetPasswordRedirectUrl = "stytchui-$publicToken://deeplink",
        resetPasswordExpirationMinutes = resetPasswordExpirationMinutes?.toUInt(),
        resetPasswordTemplateId = resetPasswordTemplateId,
    )
}
