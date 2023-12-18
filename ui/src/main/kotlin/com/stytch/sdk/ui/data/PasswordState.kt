package com.stytch.sdk.ui.data

import android.os.Parcelable
import com.stytch.sdk.consumer.network.models.Feedback
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class PasswordState(
    val password: String = "",
    val breachedPassword: Boolean = false,
    val feedback: Feedback? = null,
    val score: Int = 0,
    val validPassword: Boolean = false,
    val errorMessage: String? = null,
) : Parcelable