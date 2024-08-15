package com.stytch.sdk.ui.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class ApplicationUIState(
    val emailState: EmailState = EmailState(),
    val passwordState: PasswordState = PasswordState(),
    val phoneNumberState: PhoneNumberState = PhoneNumberState(),
    val genericErrorMessage: String? = null,
    val showLoadingDialog: Boolean = false,
    val showResendDialog: Boolean = false,
    val expirationTimeFormatted: String = "",
) : Parcelable {
    internal companion object {
        const val SAVED_STATE_KEY = "StytchAuthApplicationUIState"
    }
}
