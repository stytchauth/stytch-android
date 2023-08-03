package com.stytch.sdk.ui.data

import android.os.Parcelable
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
) : Parcelable
