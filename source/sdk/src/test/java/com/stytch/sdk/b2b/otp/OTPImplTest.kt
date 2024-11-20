package com.stytch.sdk.b2b.otp

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.stytch.sdk.b2b.BasicResponse
import com.stytch.sdk.b2b.EmailOTPAuthenticateResponse
import com.stytch.sdk.b2b.EmailOTPDiscoveryAuthenticateResponse
import com.stytch.sdk.b2b.EmailOTPDiscoverySendResponse
import com.stytch.sdk.b2b.EmailOTPLoginOrSignupResponse
import com.stytch.sdk.b2b.SMSAuthenticateResponse
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.network.models.B2BDiscoveryOTPEmailAuthenticateResponseData
import com.stytch.sdk.b2b.network.models.B2BDiscoveryOTPEmailSendResponseData
import com.stytch.sdk.b2b.network.models.B2BOTPsEmailAuthenticateResponseData
import com.stytch.sdk.b2b.network.models.B2BOTPsEmailLoginOrSignupResponseData
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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.security.KeyStore

internal class OTPImplTest {
    @MockK
    private lateinit var mockApi: StytchB2BApi.OTP

    @MockK
    private lateinit var mockSharedPreferences: SharedPreferences

    @MockK
    private lateinit var mockSharedPreferencesEditor: Editor

    private lateinit var spiedSessionStorage: B2BSessionStorage

    private lateinit var impl: OTPImpl
    private val dispatcher = Dispatchers.Unconfined
    private val successfulBaseResponse = StytchResult.Success<BasicData>(mockk(relaxed = true))
    private val successfulAuthResponse = StytchResult.Success<SMSAuthenticateResponseData>(mockk(relaxed = true))
    private val mockEmailOTPLoginOrSignupResponse =
        StytchResult.Success<B2BOTPsEmailLoginOrSignupResponseData>(
            mockk(relaxed = true),
        )
    private val mockEmailOTPAuthenticateResponse =
        StytchResult.Success<B2BOTPsEmailAuthenticateResponseData>(
            mockk(relaxed = true),
        )
    private val mockEmailOTPDiscoverySendResponse =
        StytchResult.Success<B2BDiscoveryOTPEmailSendResponseData>(
            mockk(relaxed = true),
        )
    private val mockEmailOTPDiscoveryAuthenticateResponse =
        StytchResult
            .Success<B2BDiscoveryOTPEmailAuthenticateResponseData>(
                mockk(relaxed = true),
            )

    @Before
    fun before() {
        MockKAnnotations.init(this, true, true)
        mockkStatic(KeyStore::class)
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(StorageHelper)
        every { mockSharedPreferences.edit() } returns mockSharedPreferencesEditor
        every { mockSharedPreferencesEditor.putString(any(), any()) } returns mockSharedPreferencesEditor
        every { mockSharedPreferencesEditor.putLong(any(), any()) } returns mockSharedPreferencesEditor
        every { mockSharedPreferences.getLong(any(), any()) } returns 0L
        every { mockSharedPreferencesEditor.apply() } just runs
        StorageHelper.sharedPreferences = mockSharedPreferences
        mockkObject(SessionAutoUpdater)
        mockkStatic("com.stytch.sdk.b2b.extensions.StytchResultExtKt")
        every { SessionAutoUpdater.startSessionUpdateJob(any(), any(), any()) } just runs
        spiedSessionStorage = spyk(B2BSessionStorage(StorageHelper, TestScope()), recordPrivateCalls = true)
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
        runBlocking {
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
        runBlocking {
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

    @Test
    fun `OTP Email loginOrSignup delegates to the API`() =
        runBlocking {
            coEvery { mockApi.otpEmailLoginOrSignup(any(), any(), any(), any(), any()) } returns
                mockEmailOTPLoginOrSignupResponse
            val response = impl.email.loginOrSignup(mockk(relaxed = true))
            assert(response is StytchResult.Success)
            coVerify { mockApi.otpEmailLoginOrSignup(any(), any(), any(), any(), any()) }
        }

    @Test
    fun `OTP Email loginOrSignup with callback calls callback`() {
        coEvery { mockApi.otpEmailLoginOrSignup(any(), any(), any(), any(), any()) } returns
            mockEmailOTPLoginOrSignupResponse
        val mockCallback = spyk<(EmailOTPLoginOrSignupResponse) -> Unit>()
        impl.email.loginOrSignup(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `OTP Email authenticate delegates to the API`() =
        runBlocking {
            coEvery { mockApi.otpEmailAuthenticate(any(), any(), any(), any(), any()) } returns
                mockEmailOTPAuthenticateResponse
            val response = impl.email.authenticate(mockk(relaxed = true))
            assert(response is StytchResult.Success)
            coVerify { mockApi.otpEmailAuthenticate(any(), any(), any(), any(), any()) }
        }

    @Test
    fun `OTP Email authenticate with callback calls callback`() {
        coEvery { mockApi.otpEmailAuthenticate(any(), any(), any(), any(), any()) } returns
            mockEmailOTPAuthenticateResponse
        val mockCallback = spyk<(EmailOTPAuthenticateResponse) -> Unit>()
        impl.email.authenticate(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `OTP Email Discovery Send delegates to the API`() =
        runBlocking {
            coEvery { mockApi.otpEmailDiscoverySend(any(), any(), any()) } returns
                mockEmailOTPDiscoverySendResponse
            val response = impl.email.discovery.send(mockk(relaxed = true))
            assert(response is StytchResult.Success)
            coVerify { mockApi.otpEmailDiscoverySend(any(), any(), any()) }
        }

    @Test
    fun `OTP Email Discovery Send with callback calls callback`() {
        coEvery { mockApi.otpEmailDiscoverySend(any(), any(), any()) } returns
            mockEmailOTPDiscoverySendResponse
        val mockCallback = spyk<(EmailOTPDiscoverySendResponse) -> Unit>()
        impl.email.discovery.send(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `OTP Email Discovery authenticate delegates to the API`() =
        runBlocking {
            coEvery { mockApi.otpEmailDiscoveryAuthenticate(any(), any()) } returns
                mockEmailOTPDiscoveryAuthenticateResponse
            val response = impl.email.discovery.authenticate(mockk(relaxed = true))
            assert(response is StytchResult.Success)
            coVerify { mockApi.otpEmailDiscoveryAuthenticate(any(), any()) }
        }

    @Test
    fun `OTP Email Discovery authenticate with callback calls callback`() {
        coEvery { mockApi.otpEmailDiscoveryAuthenticate(any(), any()) } returns
            mockEmailOTPDiscoveryAuthenticateResponse
        val mockCallback = spyk<(EmailOTPDiscoveryAuthenticateResponse) -> Unit>()
        impl.email.discovery.authenticate(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }
}
