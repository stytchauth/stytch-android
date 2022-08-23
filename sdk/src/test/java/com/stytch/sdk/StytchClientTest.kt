package com.stytch.sdk

import android.content.Context
import com.stytch.sdk.network.StytchApi
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import java.security.KeyStore

internal class StytchClientTest {

    var mContextMock = mockk<Context>(relaxed = true)
    val mainThreadSurrogate = newSingleThreadContext("UI thread")
    val dispatcher = Dispatchers.Unconfined

    val magicLinkParams = MagicLinks.EmailMagicLinks.Parameters(
        email = "email@email.com"
    )

    val otpWhatsappParams = OTP.WhatsappOTP.Parameters(
        phoneNumber = "+1200000000",
        expirationMinutes = 60u
    )

    val otpSmsParams = OTP.SmsOTP.Parameters(
        phoneNumber = "+1200000000",
        expirationMinutes = 60u
    )

    val otpEmailParams = OTP.EmailOTP.Parameters(
        email = "email@email.com",
        expirationMinutes = 60u
    )

    @Test
    fun `throw IllegalStateException exception if Sdk was not configured while calling EmailMagicLinks_loginOrCreate`() {
        runBlocking {
            try {
                mockkObject(StytchApi)
                every { StytchApi.isInitialized } returns false
//                Call method without configuration
                StytchClient.magicLinks.email.loginOrCreate(magicLinkParams)
            } catch (exception: IllegalStateException) {
//                if exception was thrown test passed
                return@runBlocking
            }
//          test failed if no exception was thrown
            assert(false)
        }
    }

    @Test
    fun `throw IllegalStateException exception if Sdk was not configured while calling SmsOTP_loginOrCreate`() {
        runBlocking {
            try {
                mockkObject(StytchApi)
                every { StytchApi.isInitialized } returns false
//                Call method without configuration
                StytchClient.otps.sms.loginOrCreate(otpSmsParams)
            } catch (exception: IllegalStateException) {
//                if exception was thrown test passed
                return@runBlocking
            }
//          test failed if no exception was thrown
            assert(false)
        }
    }

    @Test
    fun `throw IllegalStateException exception if Sdk was not configured while calling WhatsappOTP_loginOrCreate`() {
        runBlocking {
            try {
                mockkObject(StytchApi)
                every { StytchApi.isInitialized } returns false
//                Call method without configuration
                StytchClient.otps.whatsapp.loginOrCreate(otpWhatsappParams)
            } catch (exception: IllegalStateException) {
//                if exception was thrown test passed
                return@runBlocking
            }
//          test failed if no exception was thrown
            assert(false)
        }
    }

    @Test
    fun `throw IllegalStateException exception if Sdk was not configured while calling EmailOTP_loginOrCreateUser`() {
        runBlocking {
            try {
                mockkObject(StytchApi)
                every { StytchApi.isInitialized } returns false
//                Call method without configuration
                StytchClient.otps.email.loginOrCreate(otpEmailParams)
            } catch (exception: IllegalStateException) {
//                if exception was thrown test passed
                return@runBlocking
            }
//          test failed if no exception was thrown
            assert(false)
        }
    }

    @Test
    fun `throw IllegalStateException exception if Sdk was not configured while calling EmailMagicLinks_authenticate`() {
        runBlocking {
            try {
                mockkObject(StytchApi)
                every { StytchApi.isInitialized } returns false
//                Call method without configuration
                StytchClient.magicLinks.authenticate(MagicLinks.AuthParameters("token"))
            } catch (exception: IllegalStateException) {
//                if exception was thrown test passed
                return@runBlocking
            }
//          test failed if no exception was thrown
            assert(false)
        }
    }

    @Test
    fun `throw IllegalStateException exception if Sdk was not configured while calling OTP_authenticate`() {
        runBlocking {
            try {
                mockkObject(StytchApi)
                every { StytchApi.isInitialized } returns false
//                Call method without configuration
                StytchClient.otps.authenticate(OTP.AuthParameters(token = "token"))
            } catch (exception: IllegalStateException) {
//                if exception was thrown test passed
                return@runBlocking
            }
//          test failed if no exception was thrown
            assert(false)
        }
    }

    @Test
    fun `should trigger StytchApi configure when calling StytchClient configure`() {
        mockkObject(StytchApi)
        val stytchClientObject = spyk<StytchClient>(recordPrivateCalls = true)
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        val deviceInfo = DeviceInfo()
        every { stytchClientObject["getDeviceInfo"].invoke(mContextMock) }.returns(deviceInfo)
        stytchClientObject.configure(mContextMock, "", "")
        verify { StytchApi.configure("", "", deviceInfo) }
    }

    @Before
    fun before() {
        mockkConstructor(StorageHelper::class)
        mockkStatic(KeyStore::class)
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any(), any()) } returns Unit
        mContextMock = mockk<Context>(relaxed = true)
        Dispatchers.setMain(mainThreadSurrogate)
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        every { anyConstructed<StorageHelper>().loadValue(any()) } returns ""
        every { anyConstructed<StorageHelper>().getHashedCodeChallenge(any()) } returns Pair("", "")
    }

    @After
    fun after() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
        unmockkAll()
    }

    @Test
    fun `should return result success loginOrCreate called without callback`() {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.MagicLinks.Email)
        coEvery {
            StytchApi.MagicLinks.Email.loginOrCreateEmail(
                email = magicLinkParams.email,
                codeChallenge = "",
                codeChallengeMethod = "",
                loginMagicLinkUrl = null
            )
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "", "")

        val result = runBlocking {
            StytchClient.magicLinks.email.loginOrCreate(magicLinkParams)
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success loginOrCreate called with callback`() {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.MagicLinks.Email)
        coEvery {
            StytchApi.MagicLinks.Email.loginOrCreateEmail(
                email = magicLinkParams.email,
                loginMagicLinkUrl = null,
                codeChallenge = any(),
                codeChallengeMethod = any())
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "", "")
        StytchClient.magicLinks.email.loginOrCreate(magicLinkParams) {
            assert(it is StytchResult.Success)
        }
    }

    @Test
    fun `should return result success authenticate called without callback`() {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.MagicLinks.Email)
        coEvery {
            StytchApi.MagicLinks.Email.authenticate(any(), codeVerifier = any(), sessionDurationMinutes = any())
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "", "")

        val result = runBlocking {
            StytchClient.magicLinks.authenticate(MagicLinks.AuthParameters("token"))
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success authenticate called with callback`() {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.MagicLinks.Email)
        coEvery {
            StytchApi.MagicLinks.Email.authenticate("token", sessionDurationMinutes = 60u, codeVerifier = "")
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "", "")
        StytchClient.magicLinks.authenticate(MagicLinks.AuthParameters("token")) {
            assert(it is StytchResult.Success)
        }
    }

    @Test
    fun `should return result success OTP_authenticate called with callback`() {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.OTP)
        coEvery {
            StytchApi.OTP.authenticateWithOTP("token", 60u)
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "", "")
        StytchClient.otps.authenticate(OTP.AuthParameters("token", 60u)) {
            assert(it is StytchResult.Success)
        }
    }

    @Test
    fun `should return result success OTP_authenticate called without callback`() {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.OTP)
        coEvery {
            StytchApi.OTP.authenticateWithOTP("token", 60u)
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "", "")

        val result = runBlocking {
            StytchClient.otps.authenticate(OTP.AuthParameters("token", 60u))
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success SmsOTP_loginOrCreateUser called with callback`() {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.OTP)
        coEvery {
            StytchApi.OTP.loginOrCreateByOTPWithSMS("+12000000", 60u)
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "", "")
        StytchClient.otps.sms.loginOrCreate(OTP.SmsOTP.Parameters("+12000000", 60u)) {
            assert(it is StytchResult.Success)
        }
    }

    @Test
    fun `should return result success SmsOTP_loginOrCreate called without callback`() {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.OTP)
        coEvery {
            StytchApi.OTP.loginOrCreateByOTPWithSMS("+12000000", 60u)
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "", "")

        val result = runBlocking {
            StytchClient.otps.sms.loginOrCreate(OTP.SmsOTP.Parameters("+12000000", 60u))
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success WhatsappOTP_loginOrCreate called with callback`() {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.OTP)
        coEvery {
            StytchApi.OTP.loginOrCreateUserByOTPWithWhatsapp("+12000000", 60u)
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "", "")
        StytchClient.otps.whatsapp.loginOrCreate(OTP.WhatsappOTP.Parameters("+12000000", 60u)) {
            assert(it is StytchResult.Success)
        }
    }

    @Test
    fun `should return result success WhatsappOTP_loginOrCreate called without callback`() {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.OTP)
        coEvery {
            StytchApi.OTP.loginOrCreateUserByOTPWithWhatsapp("+12000000", 60u)
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "", "")

        val result = runBlocking {
            StytchClient.otps.whatsapp.loginOrCreate(OTP.WhatsappOTP.Parameters("+12000000", 60u))
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success EmailOTP_loginOrCreate called with callback`() {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.OTP)
        coEvery {
            StytchApi.OTP.loginOrCreateUserByOTPWithEmail("+12000000", 60u)
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "", "")
        StytchClient.otps.email.loginOrCreate(OTP.EmailOTP.Parameters("email@email.com", 60u)) {
            assert(it is StytchResult.Success)
        }
    }

    @Test
    fun `should return result success EmailOTP_loginOrCreate called without callback`() {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.OTP)
        coEvery {
            StytchApi.OTP.loginOrCreateUserByOTPWithEmail("+12000000", 60u)
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "", "")

        val result = runBlocking {
            StytchClient.otps.email.loginOrCreate(OTP.EmailOTP.Parameters("email@email.com", 60u))
        }
        assert(result is StytchResult.Success)
    }
}
