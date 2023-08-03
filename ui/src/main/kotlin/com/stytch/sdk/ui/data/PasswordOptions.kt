package com.stytch.sdk.ui.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
public data class PasswordOptions(
    val loginRedirectURL: String? = null,
    val loginExpirationMinutes: UInt? = null,
    val resetPasswordRedirectURL: String? = null,
    val resetPasswordExpirationMinutes: UInt? = null,
    val resetPasswordTemplateId: String? = null,
) : Parcelable
