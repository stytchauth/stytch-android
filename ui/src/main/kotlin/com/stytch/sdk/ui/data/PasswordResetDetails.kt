package com.stytch.sdk.ui.data

import android.os.Parcelable
import com.stytch.sdk.consumer.passwords.Passwords
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class PasswordResetDetails(
    val parameters: Passwords.ResetByEmailStartParameters
) : Parcelable
