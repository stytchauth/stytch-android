package com.stytch.sdk.ui.data

internal data class EmailState(
    val emailAddress: String = "",
    val validEmail: Boolean? = null,
    val errorMessage: String? = null,
    val readOnly: Boolean = false,
)
