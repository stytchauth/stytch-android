package com.stytch.stytchexampleapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal sealed class ScreenState : Parcelable {
    object Idle : ScreenState()

    object Loading : ScreenState()

    data class Error(
        val error: Exception,
    ) : ScreenState()
}
