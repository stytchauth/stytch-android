package com.stytch.sdk.ui.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize internal enum class PasswordResetType : Parcelable {
    NO_PASSWORD_SET,
    FORGOT_PASSWORD,
    BREACHED,
    DEDUPE,
}
