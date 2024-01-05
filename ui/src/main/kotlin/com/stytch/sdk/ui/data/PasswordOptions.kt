package com.stytch.sdk.ui.data

import android.os.Parcelable
import com.stytch.sdk.consumer.passwords.Passwords
import kotlinx.parcelize.Parcelize

/**
 * A data class representing options for configuring the Password product
 * @property loginExpirationMinutes the number of minutes that a login request token is valid for
 * @property resetPasswordExpirationMinutes the number of minutes that a reset request token is valid for
 * @property resetPasswordTemplateId The ID of an email template (defined in the Stytch Dashboard) for password resets
 */
@Parcelize
public data class PasswordOptions(
    val loginExpirationMinutes: UInt? = null,
    val resetPasswordExpirationMinutes: UInt? = null,
    val resetPasswordTemplateId: String? = null,
) : Parcelable {
    internal fun toResetByEmailStartParameters(emailAddress: String, publicToken: String) =
        Passwords.ResetByEmailStartParameters(
            email = emailAddress,
            loginRedirectUrl = "stytchui-$publicToken://deeplink",
            loginExpirationMinutes = loginExpirationMinutes,
            resetPasswordRedirectUrl = "stytchui-$publicToken://deeplink",
            resetPasswordExpirationMinutes = resetPasswordExpirationMinutes,
            resetPasswordTemplateId = resetPasswordTemplateId,
        )
}
