package com.stytch.sdk.ui.data

import android.os.Parcelable
import com.stytch.sdk.consumer.passwords.Passwords
import kotlinx.parcelize.Parcelize

@Parcelize
public data class PasswordOptions(
    val loginRedirectURL: String? = null,
    val loginExpirationMinutes: UInt? = null,
    val resetPasswordRedirectURL: String? = null,
    val resetPasswordExpirationMinutes: UInt? = null,
    val resetPasswordTemplateId: String? = null,
) : Parcelable {
    internal fun toResetByEmailStartParameters(emailAddress: String) = Passwords.ResetByEmailStartParameters(
        email = emailAddress,
        loginRedirectUrl = loginRedirectURL,
        loginExpirationMinutes = loginExpirationMinutes,
        resetPasswordRedirectUrl = resetPasswordRedirectURL,
        resetPasswordExpirationMinutes = resetPasswordExpirationMinutes,
        resetPasswordTemplateId = resetPasswordTemplateId,
    )
}
