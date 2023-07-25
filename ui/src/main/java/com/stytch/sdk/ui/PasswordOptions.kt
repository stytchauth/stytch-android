package com.stytch.sdk.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
public data class PasswordOptions(
    val loginRedirectURL: String,
    val loginExpirationMinutes: Int? = null,
    val resetPasswordRedirectURL: String,
    val resetPasswordExpirationMinutes: Int? = null,
    val resetPasswordTemplateId: String? = null,
) : Parcelable
