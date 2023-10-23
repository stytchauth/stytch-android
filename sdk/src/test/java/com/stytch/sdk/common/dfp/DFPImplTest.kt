package com.stytch.sdk.common.dfp

import com.stytch.sdk.common.StytchDispatchers
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class DFPImplTest {
    @MockK
    private lateinit var dfpProvider: DFPProvider

    private lateinit var impl: DFPImpl

    @Before
    fun before() {
        MockKAnnotations.init(this, true, true)
        coEvery { dfpProvider.getTelemetryId() } returns "dfp-telemetry-id"
        impl = DFPImpl(
            dfpProvider = dfpProvider,
            dispatchers = StytchDispatchers(Dispatchers.Unconfined, Dispatchers.Unconfined),
            externalScope = TestScope(),
        )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `getTelemetryId delegates to provider`() = runTest {
        impl.getTelemetryId()
        coVerify(exactly = 1) { dfpProvider.getTelemetryId() }
    }

    @Test
    fun `getTelemetryId with callback calls callback`() {
        val callback = spyk<(String) -> Unit>()
        impl.getTelemetryId(callback)
        verify(exactly = 1) { callback.invoke("dfp-telemetry-id") }
    }
}
