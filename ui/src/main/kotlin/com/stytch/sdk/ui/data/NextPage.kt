package com.stytch.sdk.ui.data

internal sealed class NextPage {
    data class OTPConfirmation(val details: OTPDetails) : NextPage()

    data class EMLConfirmation(val details: EMLDetails) : NextPage()

    data class NewUserChooser(val emailAddress: String) : NextPage()

    data class NewUserCreatePassword(val emailAddress: String) : NextPage()

    data class ReturningUserNoPassword(val emailAddress: String) : NextPage()

    data class ReturningUserWithPassword(val emailAddress: String) : NextPage()
}
