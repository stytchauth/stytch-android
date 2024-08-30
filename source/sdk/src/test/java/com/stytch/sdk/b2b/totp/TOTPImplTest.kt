package com.stytch.sdk.b2b.totp

import com.stytch.sdk.b2b.TOTPAuthenticateResponse
import com.stytch.sdk.b2b.TOTPCreateResponse
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.network.models.TOTPAuthenticateResponseData
import com.stytch.sdk.b2b.network.models.TOTPCreateResponseData
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.sessions.SessionAutoUpdater
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.security.KeyStore

internal class TOTPImplTest {
    @MockK
    private lateinit var mockApi: StytchB2BApi.TOTP

    private lateinit var spiedSessionStorage: B2BSessionStorage

    private lateinit var impl: TOTP
    private val dispatcher = Dispatchers.Unconfined
    private val successfulCreateResponse = StytchResult.Success<TOTPCreateResponseData>(mockk(relaxed = true))
    private val successfulAuthResponse = StytchResult.Success<TOTPAuthenticateResponseData>(mockk(relaxed = true))

    @Before
    fun before() {
        MockKAnnotations.init(this, true, true)
        mockkStatic(KeyStore::class)
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(StorageHelper)
        every { StorageHelper.saveValue(any(), any()) } just runs
        mockkObject(SessionAutoUpdater)
        mockkStatic("com.stytch.sdk.b2b.extensions.StytchResultExtKt")
        every { SessionAutoUpdater.startSessionUpdateJob(any(), any(), any()) } just runs
        spiedSessionStorage = spyk(B2BSessionStorage(StorageHelper, TestScope()), recordPrivateCalls = true)
        impl =
            TOTPImpl(
                externalScope = TestScope(),
                dispatchers = StytchDispatchers(dispatcher, dispatcher),
                sessionStorage = spiedSessionStorage,
                api = mockApi,
            )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `TOTP create delegates to the API`() =
        runTest {
            coEvery { mockApi.create(any(), any(), any()) } returns successfulCreateResponse
            val response = impl.create(TOTP.CreateParameters("", "", 30))
            assert(response is StytchResult.Success)
            coVerify { mockApi.create(any(), any(), any()) }
        }

    @Test
    fun `TOTP create with callback calls callback`() {
        coEvery { mockApi.create(any(), any(), any()) } returns successfulCreateResponse
        val mockCallback = spyk<(TOTPCreateResponse) -> Unit>()
        impl.create(TOTP.CreateParameters("", "", 30), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `TOTP Authenticate delegates to the API and updates session`() =
        runTest {
            coEvery { mockApi.authenticate(any(), any(), any(), any(), any(), any()) } returns successfulAuthResponse
            val response = impl.authenticate(mockk(relaxed = true))
            assert(response is StytchResult.Success)
            coVerify { mockApi.authenticate(any(), any(), any(), any(), any(), any()) }
            verify { successfulAuthResponse.launchSessionUpdater(any(), any()) }
        }

    @Test
    fun `TOTP Authenticate with callback calls callback`() {
        coEvery { mockApi.authenticate(any(), any(), any(), any(), any(), any()) } returns successfulAuthResponse
        val mockCallback = spyk<(TOTPAuthenticateResponse) -> Unit>()
        impl.authenticate(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }
}
