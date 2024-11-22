package com.stytch.sdk.ui.b2b.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class ApplicationUIState(
    val expirationTimeFormatted: String = "", // this is just a placeholder for compilation purposes
) : Parcelable {
    internal companion object {
        const val SAVED_STATE_KEY = "StytchB2BAuthApplicationUIState"
    }
}
