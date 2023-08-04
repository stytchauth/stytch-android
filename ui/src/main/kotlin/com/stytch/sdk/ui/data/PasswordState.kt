package com.stytch.sdk.ui.data

import com.stytch.sdk.consumer.network.models.Feedback

internal data class PasswordState(
    val password: String = "",
    val breachedPassword: Boolean = false,
    val feedback: Feedback? = null,
    val score: Int = 0,
    val validPassword: Boolean = false,
    val errorMessage: String? = null,
)
