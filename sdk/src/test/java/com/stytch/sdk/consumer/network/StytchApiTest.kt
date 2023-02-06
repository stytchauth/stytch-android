package com.stytch.sdk.consumer.network

import android.content.Context
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.StytchDataResponse
import com.stytch.sdk.consumer.StytchClient
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.security.KeyStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

@OptIn(ExperimentalCoroutinesApi::class)
internal class StytchApiTest {
    var mContextMock = mockk<Context>(relaxed = true)

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        mockkObject(EncryptionManager)
        mockkObject(StytchApi)
        every { EncryptionManager.createNewKeys(any(), any()) } returns Unit
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(StorageHelper)
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `StytchApi isInitialized returns correctly based on configuration state`() {
        StytchApi.configure("publicToken", DeviceInfo())
        assert(StytchApi.isInitialized)
    }

    @Test(expected = IllegalStateException::class)
    fun `StytchApi apiService throws exception when not configured`() {
        every { StytchApi.isInitialized } returns false
        StytchApi.apiService
    }

    @Test
    fun `StytchApi apiService is available when configured`() {
        StytchClient.configure(mContextMock, "")
        StytchApi.apiService
    }

    // TODO every method calls safeApi

    @Test
    fun `StytchApi MagicLinks Email loginOrCreate calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.loginOrCreateUserByEmail(any()) } returns mockk(relaxed = true)
        StytchApi.MagicLinks.Email.loginOrCreate("", "", "", "", "", "")
        coVerify { StytchApi.apiService.loginOrCreateUserByEmail(any()) }
    }

    @Test
    fun `StytchApi MagicLinks Email sendPrimary calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.sendEmailMagicLinkPrimary(any()) } returns mockk(relaxed = true)
        StytchApi.MagicLinks.Email.sendPrimary("", null, null, null, null, null, null, null)
        coVerify { StytchApi.apiService.sendEmailMagicLinkPrimary(any()) }
    }

    @Test
    fun `StytchApi MagicLinks Email sendSecondar calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.sendEmailMagicLinkSecondary(any()) } returns mockk(relaxed = true)
        StytchApi.MagicLinks.Email.sendSecondary("", null, null, null, null, null, null, null)
        coVerify { StytchApi.apiService.sendEmailMagicLinkSecondary(any()) }
    }

    @Test
    fun `StytchApi MagicLinks Email authenticate calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.authenticate(any()) } returns mockk(relaxed = true)
        StytchApi.MagicLinks.Email.authenticate("", 30U, "")
        coVerify { StytchApi.apiService.authenticate(any()) }
    }

    @Test
    fun `StytchApi OTP loginOrCreateByOTPWithSMS calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.loginOrCreateUserByOTPWithSMS(any()) } returns mockk(relaxed = true)
        StytchApi.OTP.loginOrCreateByOTPWithSMS("", 30U)
        coVerify { StytchApi.apiService.loginOrCreateUserByOTPWithSMS(any()) }
    }

    @Test
    fun `StytchApi OTP sendOTPWithSMSPrimary calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.sendOTPWithSMSPrimary(any()) } returns mockk(relaxed = true)
        StytchApi.OTP.sendOTPWithSMSPrimary("", 30U)
        coVerify { StytchApi.apiService.sendOTPWithSMSPrimary(any()) }
    }

    @Test
    fun `StytchApi OTP sendOTPWithSMSSecondary calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.sendOTPWithSMSSecondary(any()) } returns mockk(relaxed = true)
        StytchApi.OTP.sendOTPWithSMSSecondary("", 30U)
        coVerify { StytchApi.apiService.sendOTPWithSMSSecondary(any()) }
    }

    @Test
    fun `StytchApi OTP loginOrCreateUserByOTPWithWhatsApp calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.loginOrCreateUserByOTPWithWhatsApp(any()) } returns mockk(relaxed = true)
        StytchApi.OTP.loginOrCreateUserByOTPWithWhatsApp("", 30U)
        coVerify { StytchApi.apiService.loginOrCreateUserByOTPWithWhatsApp(any()) }
    }

    @Test
    fun `StytchApi OTP sendByOTPWithWhatsAppPrimary calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.sendOTPWithWhatsAppPrimary(any()) } returns mockk(relaxed = true)
        StytchApi.OTP.sendOTPWithWhatsAppPrimary("", 30U)
        coVerify { StytchApi.apiService.sendOTPWithWhatsAppPrimary(any()) }
    }

    @Test
    fun `StytchApi OTP sendByOTPWithWhatsAppSecondary calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.sendOTPWithWhatsAppSecondary(any()) } returns mockk(relaxed = true)
        StytchApi.OTP.sendOTPWithWhatsAppSecondary("", 30U)
        coVerify { StytchApi.apiService.sendOTPWithWhatsAppSecondary(any()) }
    }

    @Test
    fun `StytchApi OTP loginOrCreateUserByOTPWithEmail calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.loginOrCreateUserByOTPWithEmail(any()) } returns mockk(relaxed = true)
        StytchApi.OTP.loginOrCreateUserByOTPWithEmail("", 30U, "", "")
        coVerify { StytchApi.apiService.loginOrCreateUserByOTPWithEmail(any()) }
    }

    @Test
    fun `StytchApi OTP sendOTPWithEmailPrimary calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.sendOTPWithEmailPrimary(any()) } returns mockk(relaxed = true)
        StytchApi.OTP.sendOTPWithEmailPrimary("", 30U, null, null)
        coVerify { StytchApi.apiService.sendOTPWithEmailPrimary(any()) }
    }

    @Test
    fun `StytchApi OTP sendOTPWithEmailSecondary calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.sendOTPWithEmailSecondary(any()) } returns mockk(relaxed = true)
        StytchApi.OTP.sendOTPWithEmailSecondary("", 30U, null, null)
        coVerify { StytchApi.apiService.sendOTPWithEmailSecondary(any()) }
    }

    @Test
    fun `StytchApi OTP authenticateWithOTP calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.authenticateWithOTP(any()) } returns mockk(relaxed = true)
        StytchApi.OTP.authenticateWithOTP("", "")
        coVerify { StytchApi.apiService.authenticateWithOTP(any()) }
    }

    @Test
    fun `StytchApi Passwords authenticate calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.authenticateWithPasswords(any()) } returns mockk(relaxed = true)
        StytchApi.Passwords.authenticate("", "", 30U)
        coVerify { StytchApi.apiService.authenticateWithPasswords(any()) }
    }

    @Test
    fun `StytchApi Passwords create calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.passwords(any()) } returns mockk(relaxed = true)
        StytchApi.Passwords.create("", "", 30U)
        coVerify { StytchApi.apiService.passwords(any()) }
    }

    @Test
    fun `StytchApi Passwords resetByEmailStart calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.resetByEmailStart(any()) } returns mockk(relaxed = true)
        StytchApi.Passwords.resetByEmailStart("", "", "", "", 30, "", 30)
        coVerify { StytchApi.apiService.resetByEmailStart(any()) }
    }

    @Test
    fun `StytchApi Passwords resetByEmail calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.resetByEmail(any()) } returns mockk(relaxed = true)
        StytchApi.Passwords.resetByEmail("", "", 30U, "")
        coVerify { StytchApi.apiService.resetByEmail(any()) }
    }

    @Test
    fun `StytchApi Passwords strengthCheck calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.strengthCheck(any()) } returns mockk(relaxed = true)
        StytchApi.Passwords.strengthCheck("", "")
        coVerify { StytchApi.apiService.strengthCheck(any()) }
    }

    @Test
    fun `StytchApi Sessions authenticate calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.authenticateSessions(any()) } returns mockk(relaxed = true)
        StytchApi.Sessions.authenticate(30U)
        coVerify { StytchApi.apiService.authenticateSessions(any()) }
    }

    @Test
    fun `StytchApi Sessions revoke calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.revokeSessions() } returns mockk(relaxed = true)
        StytchApi.Sessions.revoke()
        coVerify { StytchApi.apiService.revokeSessions() }
    }

    @Test
    fun `StytchApi Biometrics registerStart calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.biometricsRegisterStart(any()) } returns mockk(relaxed = true)
        StytchApi.Biometrics.registerStart("")
        coVerify { StytchApi.apiService.biometricsRegisterStart(any()) }
    }

    @Test
    fun `StytchApi Biometrics register calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.biometricsRegister(any()) } returns mockk(relaxed = true)
        StytchApi.Biometrics.register("", "", 30U)
        coVerify { StytchApi.apiService.biometricsRegister(any()) }
    }

    @Test
    fun `StytchApi Biometrics authenticateStart calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.biometricsAuthenticateStart(any()) } returns mockk(relaxed = true)
        StytchApi.Biometrics.authenticateStart("")
        coVerify { StytchApi.apiService.biometricsAuthenticateStart(any()) }
    }

    @Test
    fun `StytchApi Biometrics authenticate calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.biometricsAuthenticate(any()) } returns mockk(relaxed = true)
        StytchApi.Biometrics.authenticate("", "", 30U)
        coVerify { StytchApi.apiService.biometricsAuthenticate(any()) }
    }

    @Test
    fun `StytchApi User getUser calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.getUser() } returns mockk(relaxed = true)
        StytchApi.UserManagement.getUser()
        coVerify { StytchApi.apiService.getUser() }
    }

    @Test
    fun `StytchApi User deleteEmailById calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.deleteEmailById("emailAddressId") } returns mockk(relaxed = true)
        StytchApi.UserManagement.deleteEmailById("emailAddressId")
        coVerify { StytchApi.apiService.deleteEmailById("emailAddressId") }
    }

    @Test
    fun `StytchApi User deletePhoneNumberById calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.deletePhoneNumberById("phoneNumberId") } returns mockk(relaxed = true)
        StytchApi.UserManagement.deletePhoneNumberById("phoneNumberId")
        coVerify { StytchApi.apiService.deletePhoneNumberById("phoneNumberId") }
    }

    @Test
    fun `StytchApi User deleteBiometricRegistrationById calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery {
            StytchApi.apiService.deleteBiometricRegistrationById("biometricsRegistrationId")
        } returns mockk(relaxed = true)
        StytchApi.UserManagement.deleteBiometricRegistrationById("biometricsRegistrationId")
        coVerify { StytchApi.apiService.deleteBiometricRegistrationById("biometricsRegistrationId") }
    }

    @Test
    fun `StytchApi OAuth authenticateWithGoogleIdToken calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.authenticateWithGoogleIdToken(any()) } returns mockk(relaxed = true)
        StytchApi.OAuth.authenticateWithGoogleIdToken(
            idToken = "id_token",
            nonce = "nonce",
            sessionDurationMinutes = 30U
        )
        coVerify {
            StytchApi.apiService.authenticateWithGoogleIdToken(
                ConsumerRequests.OAuth.Google.AuthenticateRequest(
                    idToken = "id_token",
                    nonce = "nonce",
                    sessionDurationMinutes = 30
                )
            )
        }
    }

    @Test
    fun `StytchApi OAuth authenticateWithThirdPartyToken calls appropriate apiService method`() = runTest {
        every { StytchApi.isInitialized } returns true
        coEvery { StytchApi.apiService.authenticateWithThirdPartyToken(any()) } returns mockk(relaxed = true)
        StytchApi.OAuth.authenticateWithThirdPartyToken(
            token = "id_token",
            sessionDurationMinutes = 30U,
            codeVerifier = "code_challenge"
        )
        coVerify {
            StytchApi.apiService.authenticateWithThirdPartyToken(
                ConsumerRequests.OAuth.ThirdParty.AuthenticateRequest(
                    token = "id_token",
                    sessionDurationMinutes = 30,
                    codeVerifier = "code_challenge"
                )
            )
        }
    }

    @Test(expected = IllegalStateException::class)
    fun `safeApiCall throws exception when StytchClient is not initialized`() = runTest {
        every { StytchApi.isInitialized } returns false
        val mockApiCall: suspend () -> StytchDataResponse<Boolean> = mockk()
        StytchApi.safeConsumerApiCall { mockApiCall() }
    }

    @Test
    fun `safeApiCall returns success when call succeeds`() = runTest {
        every { StytchApi.isInitialized } returns true
        fun mockApiCall(): StytchDataResponse<Boolean> {
            return StytchDataResponse(true)
        }
        val result = StytchApi.safeConsumerApiCall { mockApiCall() }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `safeApiCall returns correct error for HttpException`() = runTest {
        every { StytchApi.isInitialized } returns true
        fun mockApiCall(): StytchDataResponse<Boolean> {
            throw HttpException(mockk(relaxed = true))
        }
        val result = StytchApi.safeConsumerApiCall { mockApiCall() }
        assert(result is StytchResult.Error)
    }

    @Test
    fun `safeApiCall returns correct error for StytchExceptions`() = runTest {
        every { StytchApi.isInitialized } returns true
        fun mockApiCall(): StytchDataResponse<Boolean> {
            throw StytchExceptions.Critical(RuntimeException("Test"))
        }
        val result = StytchApi.safeConsumerApiCall { mockApiCall() }
        assert(result is StytchResult.Error)
    }

    @Test
    fun `safeApiCall returns correct error for other exceptions`() = runTest {
        every { StytchApi.isInitialized } returns true
        fun mockApiCall(): StytchDataResponse<Boolean> {
            error("Test")
        }
        val result = StytchApi.safeConsumerApiCall { mockApiCall() }
        assert(result is StytchResult.Error)
    }
}
