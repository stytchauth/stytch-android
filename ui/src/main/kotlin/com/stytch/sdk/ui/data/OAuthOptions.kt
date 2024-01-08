package com.stytch.sdk.ui.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * A data class representing the configuration options for the OAuth product
 * @property loginRedirectURL The redirect url for logins. This should use the same scheme/host set in manifest
 * @property signupRedirectURL The redirect url for signups. This should use the same scheme/host set in your manifest
 * @property providers A list of [OAuthProvider]s that you would like to support
 */
@Parcelize
public data class OAuthOptions(
    val loginRedirectURL: String? = null,
    val signupRedirectURL: String? = null,
    val providers: List<OAuthProvider> = emptyList(),
) : Parcelable
