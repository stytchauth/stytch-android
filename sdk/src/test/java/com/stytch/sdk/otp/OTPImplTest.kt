package com.stytch.sdk.otp

import com.stytch.sdk.AuthResponse
import com.stytch.sdk.BaseResponse
import com.stytch.sdk.LoginOrCreateOTPResponse
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.network.StytchApi
import com.stytch.sdk.network.responseData.AuthData
import com.stytch.sdk.sessions.SessionAutoUpdater
import com.stytch.sdk.sessions.SessionStorage
import com.stytch.sdk.sessions.launchSessionUpdater
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
import java.security.KeyStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class OTPImplTest {
    @MockK
    private lateinit var mockApi: StytchApi.OTP

    @MockK
    private lateinit var mockSessionStorage: SessionStorage

    private lateinit var impl: OTPImpl
    private val dispatcher = Dispatchers.Unconfined

    private val successfulAuthResponse = StytchResult.Success<AuthData>(mockk(relaxed = true))
    private val authParameters = OTP.AuthParameters("", "", sessionDurationMinutes = 30U)

    @Before
    fun before() {
        MockKAnnotations.init(this, true, true)
        mockkStatic(KeyStore::class)
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any()) } returns Unit
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(SessionAutoUpdater)
        every { SessionAutoUpdater.startSessionUpdateJob(any(), any()) } just runs
        impl = OTPImpl(
            externalScope = TestScope(),
            dispatchers = StytchDispatchers(dispatcher, dispatcher),
            sessionStorage = mockSessionStorage,
            api = mockApi
        )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `OTPImpl authenticate delegates to api`() = runTest {
        coEvery { mockApi.authenticateWithOTP(any(), any()) } returns successfulAuthResponse
        val response = impl.authenticate(authParameters)
        assert(response is StytchResult.Success)
        coVerify { mockApi.authenticateWithOTP(any(), any()) }
        verify { successfulAuthResponse.launchSessionUpdater(any(), any()) }
    }

    @Test
    fun `OTPImpl authenticate with callback calls callback method`() {
        coEvery { mockApi.authenticateWithOTP(any(), any()) } returns successfulAuthResponse
        val mockCallback = spyk<(AuthResponse) -> Unit>()
        impl.authenticate(authParameters, mockCallback)
        verify { mockCallback.invoke(eq(successfulAuthResponse)) }
    }

    @Test
    fun `OTPImpl sms loginOrCreate delegates to api`() = runTest {
        coEvery { mockApi.loginOrCreateByOTPWithSMS(any(), any()) } returns mockk(relaxed = true)
        impl.sms.loginOrCreate(mockk(relaxed = true))
        coVerify { mockApi.loginOrCreateByOTPWithSMS(any(), any()) }
    }

    @Test
    fun `OTPImpl sms loginOrCreate with callback calls callback method`() {
        coEvery { mockApi.loginOrCreateByOTPWithSMS(any(), any()) } returns mockk(relaxed = true)
        val mockCallback = spyk<(LoginOrCreateOTPResponse) -> Unit>()
        impl.sms.loginOrCreate(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `OTPImpl sms send with active session delegates to api`() = runTest {
        every { mockSessionStorage.activeSessionExists } returns true
        coEvery { mockApi.sendOTPWithSMSSecondary(any(), any()) } returns mockk(relaxed = true)
        impl.sms.send(
            OTP.SmsOTP.Parameters(
                phoneNumber = "phoneNumber",
                expirationMinutes = 10U,
            )
        )
        coVerify { mockApi.sendOTPWithSMSSecondary(any(), any()) }
    }

    @Test
    fun `OTPImpl sms send with no active session delegates to api`() = runTest {
        every { mockSessionStorage.activeSessionExists } returns false
        coEvery { mockApi.sendOTPWithSMSPrimary(any(), any()) } returns mockk(relaxed = true)
        impl.sms.send(
            OTP.SmsOTP.Parameters(
                phoneNumber = "phoneNumber",
                expirationMinutes = 10U,
            )
        )
        coVerify { mockApi.sendOTPWithSMSPrimary(any(), any()) }
    }

    @Test
    fun `OTPImpl sms send with callback calls callback method`() {
        every { mockSessionStorage.activeSessionExists } returns false
        coEvery { mockApi.sendOTPWithSMSPrimary(any(), any()) } returns mockk(relaxed = true)
        val mockCallback = spyk<(BaseResponse) -> Unit>()
        impl.sms.send(
            OTP.SmsOTP.Parameters(
                phoneNumber = "phoneNumber",
                expirationMinutes = 10U,
            ),
            mockCallback
        )
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `OTPImpl whatsapp loginOrCreate delegates to api`() = runTest {
        coEvery { mockApi.loginOrCreateUserByOTPWithWhatsApp(any(), any()) } returns mockk(relaxed = true)
        impl.whatsapp.loginOrCreate(mockk(relaxed = true))
        coVerify { mockApi.loginOrCreateUserByOTPWithWhatsApp(any(), any()) }
    }

    @Test
    fun `OTPImpl whatsapp loginOrCreate with callback calls callback method`() {
        coEvery { mockApi.loginOrCreateUserByOTPWithWhatsApp(any(), any()) } returns mockk(relaxed = true)
        val mockCallback = spyk<(LoginOrCreateOTPResponse) -> Unit>()
        impl.whatsapp.loginOrCreate(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `OTPImpl whatsapp send with no active session delegates to api`() = runTest {
        every { mockSessionStorage.activeSessionExists } returns false
        coEvery { mockApi.sendOTPWithWhatsAppPrimary(any(), any()) } returns mockk(relaxed = true)
        impl.whatsapp.send(
            OTP.WhatsAppOTP.Parameters(
                phoneNumber = "phoneNumber",
                expirationMinutes = 10U,
            )
        )
        coVerify { mockApi.sendOTPWithWhatsAppPrimary(any(), any()) }
    }

    @Test
    fun `OTPImpl whatsapp send with active session delegates to api`() = runTest {
        every { mockSessionStorage.activeSessionExists } returns true
        coEvery { mockApi.sendOTPWithWhatsAppSecondary(any(), any()) } returns mockk(relaxed = true)
        impl.whatsapp.send(
            OTP.WhatsAppOTP.Parameters(
                phoneNumber = "phoneNumber",
                expirationMinutes = 10U,
            )
        )
        coVerify { mockApi.sendOTPWithWhatsAppSecondary(any(), any()) }
    }

    @Test
    fun `OTPImpl whatsapp send with callback calls callback method`() {
        every { mockSessionStorage.activeSessionExists } returns false
        coEvery { mockApi.sendOTPWithWhatsAppPrimary(any(), any()) } returns mockk(relaxed = true)
        val mockCallback = spyk<(BaseResponse) -> Unit>()
        impl.whatsapp.send(
            OTP.WhatsAppOTP.Parameters(
                phoneNumber = "phoneNumber",
                expirationMinutes = 10U,
            ),
            mockCallback
        )
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `OTPImpl email loginOrCreate delegates to api`() = runTest {
        coEvery { mockApi.loginOrCreateUserByOTPWithEmail(any(), any(), any(), any()) } returns mockk(relaxed = true)
        impl.email.loginOrCreate(mockk(relaxed = true))
        coVerify { mockApi.loginOrCreateUserByOTPWithEmail(any(), any(), any(), any()) }
    }

    @Test
    fun `OTPImpl email loginOrCreate with callback calls callback method`() {
        coEvery { mockApi.loginOrCreateUserByOTPWithEmail(any(), any(), any(), any()) } returns mockk(relaxed = true)
        val mockCallback = spyk<(LoginOrCreateOTPResponse) -> Unit>()
        impl.email.loginOrCreate(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `OTPImpl email send with no active session delegates to api`() = runTest {
        every { mockSessionStorage.activeSessionExists } returns false
        coEvery { mockApi.sendOTPWithEmailPrimary(any(), any(), any(), any()) } returns mockk(relaxed = true)
        impl.email.send(
            OTP.EmailOTP.Parameters(
                email = "emailAddress",
                expirationMinutes = 10U,
                loginTemplateId = null,
                signupTemplateId = null
            )
        )
        coVerify { mockApi.sendOTPWithEmailPrimary(any(), any(), any(), any()) }
    }

    @Test
    fun `OTPImpl email send with active session delegates to api`() = runTest {
        every { mockSessionStorage.activeSessionExists } returns true
        coEvery { mockApi.sendOTPWithEmailSecondary(any(), any(), any(), any()) } returns mockk(relaxed = true)
        impl.email.send(
            OTP.EmailOTP.Parameters(
                email = "emailAddress",
                expirationMinutes = 10U,
                loginTemplateId = null,
                signupTemplateId = null
            )
        )
        coVerify { mockApi.sendOTPWithEmailSecondary(any(), any(), any(), any()) }
    }

    @Test
    fun `OTPImpl email send with callback calls callback method`() {
        every { mockSessionStorage.activeSessionExists } returns false
        coEvery { mockApi.sendOTPWithEmailPrimary(any(), any(), any(), any()) } returns mockk(relaxed = true)
        val mockCallback = spyk<(BaseResponse) -> Unit>()
        impl.email.send(
            OTP.EmailOTP.Parameters(
                email = "emailAddress",
                expirationMinutes = 10U,
                loginTemplateId = null,
                signupTemplateId = null
            ),
            mockCallback
        )
        verify { mockCallback.invoke(any()) }
    }
}
