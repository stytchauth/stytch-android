package com.stytch.sdk.network

import com.stytch.sdk.DeviceInfo
import com.stytch.sdk.StytchExceptions
import io.mockk.every
import io.mockk.mockkObject
import org.junit.Test

internal class StytchApiTest {
    @Test
    fun `StytchApi isInitialized returns correctly based on configuration state`() {
        mockkObject(StytchApi)
        assert(!StytchApi.isInitialized)
        StytchApi.configure("publicToken", DeviceInfo())
        assert(StytchApi.isInitialized)
    }

    @Test(expected = StytchExceptions.Critical::class)
    fun `StytchApi authHeaderInterceptor throws when not configured`() {
        mockkObject(StytchApi)
        every { StytchApi.isInitialized } returns false
        StytchApi.authHeaderInterceptor
    }

    // TODO every method calls safeApi
    // TODO every method calls appropriate apiService method
}
