package com.stytch.sdk.b2b.events

import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.network.InfoHeaderModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date
import java.util.TimeZone
import java.util.UUID

internal class EventsImpl(
    deviceInfo: DeviceInfo,
    private val appSessionId: String,
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val api: StytchB2BApi.Events,
) : Events {
    private val infoHeaderModel = InfoHeaderModel.fromDeviceInfo(deviceInfo)

    override fun logEvent(
        eventName: String,
        details: Map<String, Any>?,
        error: Exception?,
    ) {
        externalScope.launch(dispatchers.io) {
            api.logEvent(
                eventId = "event-id-${UUID.randomUUID()}",
                appSessionId = appSessionId,
                persistentId = "persistent-id-${UUID.randomUUID()}",
                clientSentAt = Date().toString(),
                timezone = TimeZone.getDefault().id,
                eventName = eventName,
                infoHeaderModel = infoHeaderModel,
                details = details,
                error = error,
            )
        }
    }
}
