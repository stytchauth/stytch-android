package com.stytch.sdk.common

import android.os.Parcelable
import com.stytch.sdk.b2b.B2BRedirectType
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * A data class representing a concrete token type and corresponding token parsed from a deeplink
 */
@Parcelize
@JacocoExcludeGenerated
public data class DeeplinkTokenPair(
    val tokenType: @RawValue TokenType,
    val token: String?,
    val redirectType: B2BRedirectType? = null,
) : Parcelable
