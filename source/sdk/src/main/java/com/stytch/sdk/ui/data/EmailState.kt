package com.stytch.sdk.ui.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class EmailState(
    val emailAddress: String = "",
    val validEmail: Boolean? = null,
    val errorMessage: String? = null,
    val readOnly: Boolean = false,
) : Parcelable
