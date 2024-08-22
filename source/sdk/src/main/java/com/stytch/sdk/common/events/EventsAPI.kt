package com.stytch.sdk.common.events

import com.stytch.sdk.common.NoResponseResponse
import com.stytch.sdk.common.network.InfoHeaderModel

internal interface EventsAPI {
    suspend fun logEvent(
        eventId: String,
        appSessionId: String,
        persistentId: String,
        clientSentAt: String,
        timezone: String,
        eventName: String,
        infoHeaderModel: InfoHeaderModel,
        details: Map<String, Any>? = null,
        error: Exception? = null,
    ): NoResponseResponse
}
