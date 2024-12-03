package com.stytch.sdk.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * A data class representing a concrete token type and corresponding token parsed from a deeplink
 */
@Parcelize public data class DeeplinkTokenPair(
    val tokenType: @RawValue TokenType,
    val token: String?,
) : Parcelable
