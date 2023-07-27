package com.stytch.sdk.ui.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
public data class EmailMagicLinksOptions(
    val loginRedirectURL: String,
    val loginExpirationMinutes: Int? = null,
    val signupRedirectURL: String,
    val signupExpirationMinutes: Int? = null,
    val loginTemplateId: String? = null,
    val signupTemplateId: String? = null,
    val createUserAsPending: Boolean = false,
    val domainHint: String? = null,
) : Parcelable