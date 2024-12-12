package com.stytch.sdk.ui.b2b.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
internal sealed interface Route : Parcelable

internal object Routes {
    @Serializable
    @Parcelize
    internal data object Loading : Route, Parcelable

    @Serializable
    @Parcelize
    internal data object DeeplinkParser : Route, Parcelable

    @Serializable
    @Parcelize
    internal data object Discovery : Route, Parcelable

    @Serializable
    @Parcelize
    internal data object EmailConfirmation : Route, Parcelable

    @Serializable
    @Parcelize
    internal data object Error : Route, Parcelable

    @Serializable
    @Parcelize
    internal data object Main : Route, Parcelable

    @Serializable
    @Parcelize
    internal data object MFAEnrollmentSelection : Route, Parcelable

    @Serializable
    @Parcelize
    internal data object PasswordAuthenticate : Route, Parcelable

    @Serializable
    @Parcelize
    internal data object PasswordForgot : Route, Parcelable

    @Serializable
    @Parcelize
    internal data object PasswordReset : Route, Parcelable

    @Serializable
    @Parcelize
    internal data object PasswordResetVerifyConfirmation : Route, Parcelable

    @Serializable
    @Parcelize
    internal data object PasswordSetNew : Route, Parcelable

    @Serializable
    @Parcelize
    internal data object PasswordSetNewConfirmation : Route, Parcelable

    @Serializable
    @Parcelize
    internal data object RecoveryCodeEntry : Route, Parcelable

    @Serializable
    @Parcelize
    internal data object RecoveryCodeSave : Route, Parcelable

    @Serializable
    @Parcelize
    internal data object SMSOTPEnrollment : Route, Parcelable

    @Serializable
    @Parcelize
    internal data object SMSOTPEntry : Route, Parcelable

    @Serializable
    @Parcelize
    internal data object Success : Route, Parcelable

    @Serializable
    @Parcelize
    internal data object TOTPEnrollment : Route, Parcelable

    @Serializable
    @Parcelize
    internal data object TOTPEntry : Route, Parcelable
}
