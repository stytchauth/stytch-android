package com.stytch.sdk.b2b.otp

import com.stytch.sdk.b2b.BasicResponse
import com.stytch.sdk.b2b.SMSAuthenticateResponse
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.network.models.SMSAuthenticateResponseData
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.models.BasicData
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

internal class OTPImplTest {
    @MockK
    private lateinit var mockApi: StytchB2BApi.OTP

    private lateinit var spiedSessionStorage: B2BSessionStorage

    private lateinit var impl: OTPImpl
    private val dispatcher = Dispatchers.Unconfined
    private val successfulBaseResponse = StytchResult.Success<BasicData>(mockk(relaxed = true))
    private val successfulAuthResponse = StytchResult.Success<SMSAuthenticateResponseData>(mockk(relaxed = true))

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
        spiedSessionStorage = spyk(B2BSessionStorage(StorageHelper), recordPrivateCalls = true)
        impl =
            OTPImpl(
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
    fun `OTP SMS Send delegates to the API`() =
        runTest {
            coEvery { mockApi.sendSMSOTP(any(), any(), any(), any()) } returns successfulBaseResponse
            val response = impl.sms.send(mockk(relaxed = true))
            assert(response is StytchResult.Success)
            coVerify { mockApi.sendSMSOTP(any(), any(), any(), any()) }
        }

    @Test
    fun `OTP SMS Send with callback calls callback`() {
        coEvery { mockApi.sendSMSOTP(any(), any(), any(), any()) } returns successfulBaseResponse
        val mockCallback = spyk<(BasicResponse) -> Unit>()
        impl.sms.send(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `OTP SMS Authenticate delegates to the API and updates session`() =
        runTest {
            coEvery { mockApi.authenticateSMSOTP(any(), any(), any(), any(), any()) } returns successfulAuthResponse
            val response = impl.sms.authenticate(mockk(relaxed = true))
            assert(response is StytchResult.Success)
            coVerify { mockApi.authenticateSMSOTP(any(), any(), any(), any(), any()) }
            verify { successfulAuthResponse.launchSessionUpdater(any(), any()) }
        }

    @Test
    fun `OTP SMS Authenticate with callback calls callback`() {
        coEvery { mockApi.authenticateSMSOTP(any(), any(), any(), any(), any()) } returns successfulAuthResponse
        val mockCallback = spyk<(SMSAuthenticateResponse) -> Unit>()
        impl.sms.authenticate(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }
}
