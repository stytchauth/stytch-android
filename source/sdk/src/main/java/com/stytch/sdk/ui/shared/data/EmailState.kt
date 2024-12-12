package com.stytch.sdk.ui.shared.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class EmailState(
    val emailAddress: String = "",
    val emailVerified: Boolean? = null,
    val validEmail: Boolean? = null,
    val errorMessage: String? = null,
    val readOnly: Boolean = false,
    val shouldValidateEmail: Boolean = true,
) : Parcelable
