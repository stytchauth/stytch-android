package com.stytch.sdk.consumer.events

import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.network.InfoHeaderModel
import com.stytch.sdk.consumer.network.StytchApi
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.security.KeyStore
import java.util.TimeZone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class EventsImplTest {
    @MockK
    private lateinit var mockEventsAPI: StytchApi.Events

    private lateinit var impl: EventsImpl

    private val dispatcher = Dispatchers.Unconfined

    private val mockDeviceInfo = DeviceInfo(
        applicationPackageName = "com.stytch.test",
        applicationVersion = "1.0.0",
        osName = "Android",
        osVersion = "14",
        deviceName = "Test Device",
        screenSize = ""
    )

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any()) } returns Unit
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        MockKAnnotations.init(this, true, true)
        mockkStatic("com.stytch.sdk.b2b.extensions.StytchResultExtKt")
        impl = EventsImpl(
            deviceInfo = mockDeviceInfo,
            appSessionId = "app-session-id",
            dispatchers = StytchDispatchers(dispatcher, dispatcher),
            externalScope = TestScope(),
            api = mockEventsAPI
        )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `EventsImpl logEvent delegates to api`() = runTest {
        coEvery { mockEventsAPI.logEvent(any(), any(), any(), any(), any(), any(), any()) } returns mockk()
        val mockDetails = mapOf("test-key" to "test value")
        impl.logEvent("test-event", mockDetails)
        coVerify(exactly = 1) {
            mockEventsAPI.logEvent(
                eventId = any(),
                appSessionId = "app-session-id",
                persistentId = any(),
                clientSentAt = any(),
                timezone = TimeZone.getDefault().id,
                eventName = "test-event",
                infoHeaderModel = InfoHeaderModel.fromDeviceInfo(mockDeviceInfo),
                details = mockDetails,
            )
        }
    }
}
