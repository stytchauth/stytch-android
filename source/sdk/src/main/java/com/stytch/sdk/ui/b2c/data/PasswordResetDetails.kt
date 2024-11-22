package com.stytch.sdk.ui.b2c.data

import android.os.Parcelable
import com.stytch.sdk.consumer.passwords.Passwords
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class PasswordResetDetails(
    val parameters: Passwords.ResetByEmailStartParameters,
    val resetType: PasswordResetType,
) : Parcelable
