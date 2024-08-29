package com.stytch.stytchexampleapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal sealed class ScreenState : Parcelable {
    data object Idle : ScreenState()

    data object Loading : ScreenState()

    data class Error(
        val error: Exception,
    ) : ScreenState()
}
