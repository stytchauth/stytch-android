// ktlint-disable max-line-length
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
import java.security.KeyStore
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any

internal class StytchClientTest {

    var mContextMock = mockk<Context>(relaxed = true)
    val mainThreadSurrogate = newSingleThreadContext("UI thread")
    val dispatcher = Dispatchers.Unconfined

    val magicLinkParams = MagicLinks.EmailMagicLinks.Parameters(
        email = "email@email.com"
    )

    val otpWhatsAppParams = OTP.WhatsAppOTP.Parameters(
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
    fun `throw IllegalStateException exception if Sdk was not configured while calling WhatsAppOTP_loginOrCreate`() {
        runBlocking {
            try {
                mockkObject(StytchApi)
                every { StytchApi.isInitialized } returns false
//                Call method without configuration
                StytchClient.otps.whatsapp.loginOrCreate(otpWhatsAppParams)
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
                StytchClient.otps.authenticate(
                    OTP.AuthParameters(token = "token", methodId = "method_id_123")
                )
            } catch (exception: IllegalStateException) {
//                if exception was thrown test passed
                return@runBlocking
            }
//          test failed if no exception was thrown
            assert(false)
        }
    }

    @Test
    fun `throw IllegalStateException exception if Sdk was not configured while calling Passwords_authenticate`() {
        runBlocking {
            try {
                mockkObject(StytchApi)
                every { StytchApi.isInitialized } returns false
//                Call method without configuration
                StytchClient.passwords.authenticate(
                    Passwords.AuthParameters(
                        email = any(),
                        password = any(),
                        sessionDurationMinutes = any()
                    )
                )
            } catch (exception: IllegalStateException) {
//                if exception was thrown test passed
                return@runBlocking
            }
//          test failed if no exception was thrown
            assert(false)
        }
    }

    @Test
    fun `throw IllegalStateException exception if Sdk was not configured while calling Passwords_create`() {
        runBlocking {
            try {
                mockkObject(StytchApi)
                every { StytchApi.isInitialized } returns false
//                Call method without configuration
                StytchClient.passwords.create(
                    Passwords.CreateParameters(
                        email = any(),
                        password = any(),
                        sessionDurationMinutes = any()
                    )
                )
            } catch (exception: IllegalStateException) {
//                if exception was thrown test passed
                return@runBlocking
            }
//          test failed if no exception was thrown
            assert(false)
        }
    }

    @Test
    fun `throw IllegalStateException exception if Sdk was not configured while calling Passwords_resetByEmailStart`() {
        runBlocking {
            try {
                mockkObject(StytchApi)
                every { StytchApi.isInitialized } returns false
//                Call method without configuration
                StytchClient.passwords.resetByEmailStart(
                    Passwords.ResetByEmailStartParameters(
                        email = any(),
                        loginRedirectUrl = any(),
                        loginExpirationMinutes = any(),
                        resetPasswordRedirectUrl = any(),
                        resetPasswordExpirationMinutes = any()
                    )
                )
            } catch (exception: IllegalStateException) {
//                if exception was thrown test passed
                return@runBlocking
            }
//          test failed if no exception was thrown
            assert(false)
        }
    }

    @Test
    fun `throw IllegalStateException exception if Sdk was not configured while calling Passwords_resetByEmail`() {
        runBlocking {
            try {
                mockkObject(StytchApi)
                every { StytchApi.isInitialized } returns false
//                Call method without configuration
                StytchClient.passwords.resetByEmail(
                    Passwords.ResetByEmailParameters(
                        token = any(),
                        password = any(),
                        sessionDurationMinutes = any()
                    )
                )
            } catch (exception: IllegalStateException) {
//                if exception was thrown test passed
                return@runBlocking
            }
//          test failed if no exception was thrown
            assert(false)
        }
    }

    @Test
    fun `throw IllegalStateException exception if Sdk was not configured while calling Passwords_strengthCheck`() {
        runBlocking {
            try {
                mockkObject(StytchApi)
                every { StytchApi.isInitialized } returns false
//                Call method without configuration
                StytchClient.passwords.strengthCheck(
                    Passwords.StrengthCheckParameters(email = any(), password = any())
                )
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
        stytchClientObject.configure(mContextMock, "")
        verify { StytchApi.configure("", deviceInfo) }
    }

    @Before
    fun before() {
        mockkConstructor(StorageHelper::class)
        mockkStatic(KeyStore::class)
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any()) } returns Unit
        mContextMock = mockk<Context>(relaxed = true)
        Dispatchers.setMain(mainThreadSurrogate)
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        every { anyConstructed<StorageHelper>().loadValue(any()) } returns ""
        every { anyConstructed<StorageHelper>().generateHashedCodeChallenge() } returns Pair("", "")
    }

    @After
    fun after() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
        unmockkAll()
    }

    @Test
    fun `should return result success loginOrCreate called without callback`() = runTest {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.MagicLinks.Email)
        coEvery {
            StytchApi.MagicLinks.Email.loginOrCreate(
                email = magicLinkParams.email,
                codeChallenge = "",
                codeChallengeMethod = "",
                loginMagicLinkUrl = null
            )
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "")

        val result = withContext(Dispatchers.Default) {
            StytchClient.magicLinks.email.loginOrCreate(magicLinkParams)
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success loginOrCreate called with callback`() = runTest {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.MagicLinks.Email)
        coEvery {
            StytchApi.MagicLinks.Email.loginOrCreate(
                email = magicLinkParams.email,
                loginMagicLinkUrl = null,
                codeChallenge = any(),
                codeChallengeMethod = any()
            )
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "")
        val result = suspendCoroutine { continuation ->
            StytchClient.magicLinks.email.loginOrCreate(magicLinkParams) {
                continuation.resume(it)
            }
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success MagicLinks_authenticate called without callback`() = runTest {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.MagicLinks.Email)
        coEvery {
            StytchApi.MagicLinks.Email.authenticate(
                any(),
                codeVerifier = any(),
                sessionDurationMinutes = any()
            )
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "")

        val result = withContext(Dispatchers.Default) {
            StytchClient.magicLinks.authenticate(MagicLinks.AuthParameters("token"))
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success MagicLinks_authenticate called with callback`() = runTest {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.MagicLinks.Email)
        coEvery {
            StytchApi.MagicLinks.Email.authenticate(
                "token",
                sessionDurationMinutes = 60u,
                codeVerifier = ""
            )
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "")
        val result = suspendCoroutine { continuation ->
            StytchClient.magicLinks.authenticate(
                MagicLinks.AuthParameters("token", sessionDurationMinutes = 60u)
            ) {
                continuation.resume(it)
            }
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success OTP_authenticate called with callback`() = runTest {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.OTP)
        coEvery {
            StytchApi.OTP.authenticateWithOTP("token", methodId = "method_id_123", 60u)
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "")
        val result = suspendCoroutine { continuation ->
            StytchClient.otps.authenticate(
                OTP.AuthParameters("token", methodId = "method_id_123", 60u)
            ) {
                continuation.resume(it)
            }
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success OTP_authenticate called without callback`() = runTest {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.OTP)
        coEvery {
            StytchApi.OTP.authenticateWithOTP("token", methodId = "method_id_123", 60u)
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "")

        val result = withContext(Dispatchers.Default) {
            StytchClient.otps.authenticate(
                OTP.AuthParameters("token", methodId = "method_id_123", 60u)
            )
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success SmsOTP_loginOrCreateUser called with callback`() = runTest {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.OTP)
        coEvery {
            StytchApi.OTP.loginOrCreateByOTPWithSMS("+12000000", 60u)
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "")
        val result = suspendCoroutine { continuation ->
            StytchClient.otps.sms.loginOrCreate(OTP.SmsOTP.Parameters("+12000000", 60u)) {
                continuation.resume(it)
            }
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success SmsOTP_loginOrCreate called without callback`() = runTest {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.OTP)
        coEvery {
            StytchApi.OTP.loginOrCreateByOTPWithSMS("+12000000", 60u)
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "")

        val result = withContext(Dispatchers.Default) {
            StytchClient.otps.sms.loginOrCreate(OTP.SmsOTP.Parameters("+12000000", 60u))
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success WhatsAppOTP_loginOrCreate called with callback`() = runTest {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.OTP)
        coEvery {
            StytchApi.OTP.loginOrCreateUserByOTPWithWhatsApp("+12000000", 60u)
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "")
        val result = suspendCoroutine { continuation ->
            StytchClient.otps.whatsapp.loginOrCreate(OTP.WhatsAppOTP.Parameters("+12000000", 60u)) {
                continuation.resume(it)
            }
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success WhatsAppOTP_loginOrCreate called without callback`() = runTest {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.OTP)
        coEvery {
            StytchApi.OTP.loginOrCreateUserByOTPWithWhatsApp("+12000000", 60u)
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "")

        val result = withContext(Dispatchers.Default) {
            StytchClient.otps.whatsapp.loginOrCreate(OTP.WhatsAppOTP.Parameters("+12000000", 60u))
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success EmailOTP_loginOrCreate called with callback`() = runTest {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.OTP)
        coEvery {
            StytchApi.OTP.loginOrCreateUserByOTPWithEmail("email@email.com", 60u)
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "")
        val result = suspendCoroutine { continuation ->
            StytchClient.otps.email.loginOrCreate(OTP.EmailOTP.Parameters("email@email.com", 60u)) {
                continuation.resume(it)
            }
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success EmailOTP_loginOrCreate called without callback`() = runTest {
        val stytchClientObject = spyk<StytchClient>()
        mockkObject(StytchApi.OTP)
        coEvery {
            StytchApi.OTP.loginOrCreateUserByOTPWithEmail("email@email.com", 60u)
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "")

        val result = withContext(Dispatchers.Default) {
            StytchClient.otps.email.loginOrCreate(OTP.EmailOTP.Parameters("email@email.com", 60u))
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success Passwords_authenticate called with callback`() = runTest {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.Passwords)
        coEvery {
            StytchApi.Passwords.authenticate(
                email = "",
                password = "",
                sessionDurationMinutes = 10u
            )
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "")
        val result = suspendCoroutine { continuation ->
            StytchClient.passwords.authenticate(
                Passwords.AuthParameters(email = "", password = "", sessionDurationMinutes = 10u)
            ) {
                continuation.resume(it)
            }
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success Passwords_authenticate called without callback`() = runTest {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.Passwords)
        coEvery {
            StytchApi.Passwords.authenticate(
                email = "",
                password = "",
                sessionDurationMinutes = 10u
            )
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "")

        val result = withContext(Dispatchers.Default) {
            StytchClient.passwords.authenticate(
                Passwords.AuthParameters(email = "", password = "", sessionDurationMinutes = 10u)
            )
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success Passwords_create called with callback`() = runTest {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.Passwords)
        coEvery {
            StytchApi.Passwords.create(email = "", password = "", sessionDurationMinutes = 10u)
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "")
        val result = suspendCoroutine { continuation ->
            StytchClient.passwords.create(
                Passwords.CreateParameters(email = "", password = "", sessionDurationMinutes = 10u)
            ) {
                continuation.resume(it)
            }
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success Passwords_create called without callback`() = runTest {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.Passwords)
        coEvery {
            StytchApi.Passwords.create(email = "", password = "", sessionDurationMinutes = 10u)
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "")

        val result = withContext(Dispatchers.Default) {
            StytchClient.passwords.create(
                Passwords.CreateParameters(email = "", password = "", sessionDurationMinutes = 10u)
            )
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success Passwords_resetByEmailStart called with callback`() = runTest {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.Passwords)
        coEvery {
            StytchApi.Passwords.resetByEmailStart(
                email = "",
                codeChallenge = "",
                codeChallengeMethod = "",
                loginRedirectUrl = "",
                loginExpirationMinutes = 10,
                resetPasswordRedirectUrl = "",
                resetPasswordExpirationMinutes = 10
            )
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "")

        val result = suspendCoroutine { continuation ->
            StytchClient.passwords.resetByEmailStart(
                Passwords.ResetByEmailStartParameters(
                    email = "",
                    loginRedirectUrl = "",
                    loginExpirationMinutes = 10,
                    resetPasswordRedirectUrl = "",
                    resetPasswordExpirationMinutes = 10
                )
            ) {
                continuation.resume(it)
            }
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success Passwords_resetByEmailStart called without callback`() = runTest {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.Passwords)
        coEvery {
            StytchApi.Passwords.resetByEmailStart(
                email = "",
                codeChallenge = "",
                codeChallengeMethod = "",
                loginRedirectUrl = "",
                loginExpirationMinutes = 10,
                resetPasswordRedirectUrl = "",
                resetPasswordExpirationMinutes = 10
            )
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "")

        val result = withContext(Dispatchers.Default) {
            StytchClient.passwords.resetByEmailStart(
                Passwords.ResetByEmailStartParameters(
                    email = "",
                    loginRedirectUrl = "",
                    loginExpirationMinutes = 10,
                    resetPasswordRedirectUrl = "",
                    resetPasswordExpirationMinutes = 10
                )
            )
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success Passwords_resetByEmail called with callback`() = runTest {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.Passwords)
        coEvery {
            StytchApi.Passwords.resetByEmail(
                token = "",
                password = "",
                sessionDurationMinutes = 10u,
                codeVerifier = ""
            )
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "")
        val result = suspendCoroutine { continuation ->
            StytchClient.passwords.resetByEmail(
                Passwords.ResetByEmailParameters(
                    token = "",
                    password = "",
                    sessionDurationMinutes = 10u
                )
            ) {
                continuation.resume(it)
            }
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success Passwords_resetByEmail called without callback`() = runTest {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.Passwords)
        coEvery {
            StytchApi.Passwords.resetByEmail(
                token = "",
                password = "",
                sessionDurationMinutes = 10u,
                codeVerifier = ""
            )
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "")

        val result = withContext(Dispatchers.Default) {
            StytchClient.passwords.resetByEmail(
                Passwords.ResetByEmailParameters(
                    token = "",
                    password = "",
                    sessionDurationMinutes = 10u
                )
            )
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success Passwords_strengthCheck called with callback`() = runTest {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.Passwords)
        coEvery {
            StytchApi.Passwords.strengthCheck(email = "", password = "")
        }.returns(StytchResult.Success(any()))

        stytchClientObject.configure(mContextMock, "")
        val result = suspendCoroutine { continuation ->
            StytchClient.passwords.strengthCheck(
                Passwords.StrengthCheckParameters(email = "", password = "")
            ) {
                continuation.resume(it)
            }
        }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `should return result success Passwords_strengthCheck called without callback`() = runTest {
        val stytchClientObject = spyk<StytchClient>()
        stytchClientObject.setDispatchers(dispatcher, dispatcher)
        mockkObject(StytchApi.Passwords)
        coEvery {
            StytchApi.Passwords.strengthCheck(email = "", password = "")
        }.returns(StytchResult.Success(any()))
        stytchClientObject.configure(mContextMock, "")

        val result = withContext(Dispatchers.Default) {
            StytchClient.passwords.strengthCheck(
                Passwords.StrengthCheckParameters(email = "", password = "")
            )
        }
        assert(result is StytchResult.Success)
    }
}
