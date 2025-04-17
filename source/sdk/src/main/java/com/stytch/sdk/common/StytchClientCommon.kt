package com.stytch.sdk.common

import com.stytch.sdk.common.network.CommonApi
import com.stytch.sdk.common.network.models.Vertical
import kotlinx.coroutines.Job

internal interface StytchClientCommon {
    val expectedVertical: Vertical

    val commonApi: CommonApi

    fun logEvent(
        eventName: String,
        details: Map<String, Any>?,
        error: Exception?,
    )

    fun rehydrateSession(): Job

    fun smsAutofillCallback(
        code: String?,
        sessionDurationMinutes: Int?,
    )

    var onFinishedInitialization: () -> Unit

    fun getSessionToken(): String?
}
