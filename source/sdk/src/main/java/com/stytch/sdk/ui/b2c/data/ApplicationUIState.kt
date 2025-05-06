package com.stytch.sdk.ui.b2c.data

import android.os.Parcelable
import com.stytch.sdk.ui.shared.data.EmailState
import com.stytch.sdk.ui.shared.data.PasswordState
import com.stytch.sdk.ui.shared.data.PhoneNumberState
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
    val showBiometricRegistrationOnLogin: Boolean = false,
) : Parcelable {
    internal companion object {
        const val SAVED_STATE_KEY = "StytchAuthApplicationUIState"
    }
}
