package com.stytch.sdk.ui.b2b.data

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class ErrorDetails(
    val errorText: String? = null,
    @StringRes val errorMessageId: Int? = null,
    val arguments: List<String> = emptyList(),
) : Parcelable
