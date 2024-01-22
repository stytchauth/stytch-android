package com.stytch.sdk.consumer.events

import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.network.InfoHeaderModel
import com.stytch.sdk.consumer.network.StytchApi
import java.util.Date
import java.util.TimeZone
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class EventsImpl(
    deviceInfo: DeviceInfo,
    private val appSessionId: String,
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val api: StytchApi.Events,
) : Events {
    private val infoHeaderModel = InfoHeaderModel.fromDeviceInfo(deviceInfo)

    override fun logEvent(eventName: String, details: Map<String, Any>?, error: Exception?) {
        externalScope.launch(dispatchers.io) {
            api.logEvent(
                eventId = "event-id-${UUID.randomUUID()}",
                appSessionId = appSessionId,
                persistentId = "persistent-id-${UUID.randomUUID()}",
                clientSentAt = Date().toString(),
                timezone = TimeZone.getDefault().id,
                eventName = eventName,
                infoHeaderModel = infoHeaderModel,
                details = details
            )
        }
    }
}
