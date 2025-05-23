package com.stytch.sdk.ui.b2c.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.common.network.models.Locale
import com.stytch.sdk.consumer.passwords.Passwords
import kotlinx.parcelize.Parcelize

/**
 * A data class representing options for configuring the Password product
 * @property loginExpirationMinutes the number of minutes that a login request token is valid for
 * @property resetPasswordExpirationMinutes the number of minutes that a reset request token is valid for
 * @property resetPasswordTemplateId The ID of an email template (defined in the Stytch Dashboard) for password resets
 */
@Parcelize
@JsonClass(generateAdapter = true)
@JacocoExcludeGenerated
public data class PasswordOptions
    @JvmOverloads
    constructor(
        val loginExpirationMinutes: Int? = null,
        val resetPasswordExpirationMinutes: Int? = null,
        val resetPasswordTemplateId: String? = null,
    ) : Parcelable {
        internal fun toResetByEmailStartParameters(
            emailAddress: String,
            publicToken: String,
            locale: Locale,
        ) = Passwords.ResetByEmailStartParameters(
            email = emailAddress,
            loginRedirectUrl = "$publicToken://b2c-ui",
            loginExpirationMinutes = loginExpirationMinutes,
            resetPasswordRedirectUrl = "$publicToken://b2c-ui",
            resetPasswordExpirationMinutes = resetPasswordExpirationMinutes,
            resetPasswordTemplateId = resetPasswordTemplateId,
            locale = locale,
        )
    }
