package com.stytch.sdk.b2b.recoveryCodes

import com.stytch.sdk.b2b.RecoveryCodesGetResponse
import com.stytch.sdk.b2b.RecoveryCodesRecoverResponse
import com.stytch.sdk.b2b.RecoveryCodesRotateResponse
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.network.models.RecoveryCodeGetResponseData
import com.stytch.sdk.b2b.network.models.RecoveryCodeRecoverResponseData
import com.stytch.sdk.b2b.network.models.RecoveryCodeRotateResponseData
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

internal class RecoveryCodesImplTest {
    @MockK
    private lateinit var mockApi: StytchB2BApi.RecoveryCodes

    private lateinit var spiedSessionStorage: B2BSessionStorage

    private lateinit var impl: RecoveryCodes
    private val dispatcher = Dispatchers.Unconfined
    private val successfulGetResponse = StytchResult.Success<RecoveryCodeGetResponseData>(mockk(relaxed = true))
    private val successfulRotateResponse = StytchResult.Success<RecoveryCodeRotateResponseData>(mockk(relaxed = true))
    private val successfulRecoverResponse = StytchResult.Success<RecoveryCodeRecoverResponseData>(mockk(relaxed = true))

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
            RecoveryCodesImpl(
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
    fun `RecoveryCodes get delegates to the api`() =
        runTest {
            coEvery { mockApi.get() } returns successfulGetResponse
            val response = impl.get()
            assert(response is StytchResult.Success)
            coVerify { mockApi.get() }
        }

    @Test
    fun `RecoveryCodes get with callback calls callback`() {
        coEvery { mockApi.get() } returns successfulGetResponse
        val mockCallback = spyk<(RecoveryCodesGetResponse) -> Unit>()
        impl.get(mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `RecoveryCodes rotate delegates to the api`() =
        runTest {
            coEvery { mockApi.rotate() } returns successfulRotateResponse
            val response = impl.rotate()
            assert(response is StytchResult.Success)
            coVerify { mockApi.rotate() }
        }

    @Test
    fun `RecoveryCodes rotate with callback calls callback`() {
        coEvery { mockApi.rotate() } returns successfulRotateResponse
        val mockCallback = spyk<(RecoveryCodesRotateResponse) -> Unit>()
        impl.rotate(mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `RecoveryCodes recover delegates to the api and updates the session`() =
        runTest {
            coEvery { mockApi.recover(any(), any(), any(), any()) } returns successfulRecoverResponse
            val response = impl.recover(mockk(relaxed = true))
            assert(response is StytchResult.Success)
            coVerify { mockApi.recover(any(), any(), any(), any()) }
            verify { successfulRecoverResponse.launchSessionUpdater(any(), any()) }
        }

    @Test
    fun `RecoveryCodes recover with callback calls callback`() {
        coEvery { mockApi.recover(any(), any(), any(), any()) } returns successfulRecoverResponse
        val mockCallback = spyk<(RecoveryCodesRecoverResponse) -> Unit>()
        impl.recover(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }
}
