package com.stytch.sdk.ui.shared.data

import android.os.Parcelable
import com.stytch.sdk.common.network.models.Feedback
import com.stytch.sdk.common.network.models.StrengthPolicy
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class PasswordState(
    val strengthPolicy: StrengthPolicy = StrengthPolicy.ZXCVBN,
    val password: String = "",
    val breachedPassword: Boolean = false,
    val feedback: Feedback? = null,
    val score: Int = 0,
    val validPassword: Boolean = false,
    val errorMessage: String? = null,
    val errorMessageId: Int? = null,
) : Parcelable
