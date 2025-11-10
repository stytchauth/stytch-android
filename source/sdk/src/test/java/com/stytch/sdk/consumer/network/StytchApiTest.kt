package com.stytch.sdk.consumer.network

import android.app.Application
import android.content.Context
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchAPIError
import com.stytch.sdk.common.errors.StytchAPIErrorType
import com.stytch.sdk.common.network.InfoHeaderModel
import com.stytch.sdk.common.network.StytchDataResponse
import com.stytch.sdk.common.network.models.CommonRequests
import com.stytch.sdk.consumer.network.models.ConsumerRequests
import com.stytch.sdk.consumer.network.models.CryptoWalletType
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.security.KeyStore
import java.util.Date

internal class StytchApiTest {
    var mContextMock = mockk<Context>(relaxed = true)

    @MockK
    private lateinit var mockApiService: StytchApiService

    private val mockDeviceInfo =
        DeviceInfo(
            applicationPackageName = "com.stytch.test",
            applicationVersion = "1.0.0",
            osName = "Android",
            osVersion = "14",
            deviceName = "Test Device",
            screenSize = "",
        )

    @Before
    fun before() {
        val mockApplication: Application =
            mockk {
                every { packageName } returns "Stytch"
            }
        mContextMock =
            mockk(relaxed = true) {
                every { applicationContext } returns mockApplication
            }
        mockkStatic(KeyStore::class)
        mockkObject(EncryptionManager)
        MockKAnnotations.init(this, true, true)
        every { EncryptionManager.createNewKeys(any()) } returns Unit
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(StorageHelper)
        StytchApi.apiService = mockApiService
        StytchApi.publicToken = ""
        StytchApi.deviceInfo = mockDeviceInfo
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    // TODO every method calls safeApi

    @Test
    fun `StytchApi MagicLinks Email loginOrCreate calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.loginOrCreateUserByEmail(any()) } returns mockk(relaxed = true)
            StytchApi.MagicLinks.Email.loginOrCreate("", null, null, "", null, null)
            coVerify { mockApiService.loginOrCreateUserByEmail(any()) }
        }

    @Test
    fun `StytchApi MagicLinks Email sendPrimary calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.sendEmailMagicLinkPrimary(any()) } returns mockk(relaxed = true)
            StytchApi.MagicLinks.Email.sendPrimary("", null, null, null, null, null, null, null)
            coVerify { mockApiService.sendEmailMagicLinkPrimary(any()) }
        }

    @Test
    fun `StytchApi MagicLinks Email sendSecondar calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.sendEmailMagicLinkSecondary(any()) } returns mockk(relaxed = true)
            StytchApi.MagicLinks.Email.sendSecondary("", null, null, null, null, null, null, null)
            coVerify { mockApiService.sendEmailMagicLinkSecondary(any()) }
        }

    @Test
    fun `StytchApi MagicLinks Email authenticate calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.authenticate(any()) } returns mockk(relaxed = true)
            StytchApi.MagicLinks.Email.authenticate("", 30, "")
            coVerify { mockApiService.authenticate(any()) }
        }

    @Test
    fun `StytchApi OTP loginOrCreateByOTPWithSMS calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.loginOrCreateUserByOTPWithSMS(any()) } returns mockk(relaxed = true)
            StytchApi.OTP.loginOrCreateByOTPWithSMS("", 30)
            coVerify { mockApiService.loginOrCreateUserByOTPWithSMS(any()) }
        }

    @Test
    fun `StytchApi OTP sendOTPWithSMSPrimary calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.sendOTPWithSMSPrimary(any()) } returns mockk(relaxed = true)
            StytchApi.OTP.sendOTPWithSMSPrimary("", 30)
            coVerify { mockApiService.sendOTPWithSMSPrimary(any()) }
        }

    @Test
    fun `StytchApi OTP sendOTPWithSMSSecondary calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.sendOTPWithSMSSecondary(any()) } returns mockk(relaxed = true)
            StytchApi.OTP.sendOTPWithSMSSecondary("", 30)
            coVerify { mockApiService.sendOTPWithSMSSecondary(any()) }
        }

    @Test
    fun `StytchApi OTP loginOrCreateUserByOTPWithWhatsApp calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.loginOrCreateUserByOTPWithWhatsApp(any()) } returns mockk(relaxed = true)
            StytchApi.OTP.loginOrCreateUserByOTPWithWhatsApp("", 30)
            coVerify { mockApiService.loginOrCreateUserByOTPWithWhatsApp(any()) }
        }

    @Test
    fun `StytchApi OTP sendByOTPWithWhatsAppPrimary calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.sendOTPWithWhatsAppPrimary(any()) } returns mockk(relaxed = true)
            StytchApi.OTP.sendOTPWithWhatsAppPrimary("", 30, null)
            coVerify { mockApiService.sendOTPWithWhatsAppPrimary(any()) }
        }

    @Test
    fun `StytchApi OTP sendByOTPWithWhatsAppSecondary calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.sendOTPWithWhatsAppSecondary(any()) } returns mockk(relaxed = true)
            StytchApi.OTP.sendOTPWithWhatsAppSecondary("", 30, null)
            coVerify { mockApiService.sendOTPWithWhatsAppSecondary(any()) }
        }

    @Test
    fun `StytchApi OTP loginOrCreateUserByOTPWithEmail calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.loginOrCreateUserByOTPWithEmail(any()) } returns mockk(relaxed = true)
            StytchApi.OTP.loginOrCreateUserByOTPWithEmail("", 30, "", "", null)
            coVerify { mockApiService.loginOrCreateUserByOTPWithEmail(any()) }
        }

    @Test
    fun `StytchApi OTP sendOTPWithEmailPrimary calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.sendOTPWithEmailPrimary(any()) } returns mockk(relaxed = true)
            StytchApi.OTP.sendOTPWithEmailPrimary("", 30, null, null)
            coVerify { mockApiService.sendOTPWithEmailPrimary(any()) }
        }

    @Test
    fun `StytchApi OTP sendOTPWithEmailSecondary calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.sendOTPWithEmailSecondary(any()) } returns mockk(relaxed = true)
            StytchApi.OTP.sendOTPWithEmailSecondary("", 30, null, null)
            coVerify { mockApiService.sendOTPWithEmailSecondary(any()) }
        }

    @Test
    fun `StytchApi OTP authenticateWithOTP calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.authenticateWithOTP(any()) } returns mockk(relaxed = true)
            StytchApi.OTP.authenticateWithOTP("", "")
            coVerify { mockApiService.authenticateWithOTP(any()) }
        }

    @Test
    fun `StytchApi Passwords authenticate calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.authenticateWithPasswords(any()) } returns mockk(relaxed = true)
            StytchApi.Passwords.authenticate("", "", 30)
            coVerify { mockApiService.authenticateWithPasswords(any()) }
        }

    @Test
    fun `StytchApi Passwords create calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.passwords(any()) } returns mockk(relaxed = true)
            StytchApi.Passwords.create("", "", 30)
            coVerify { mockApiService.passwords(any()) }
        }

    @Test
    fun `StytchApi Passwords resetByEmailStart calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.resetByEmailStart(any()) } returns mockk(relaxed = true)
            StytchApi.Passwords.resetByEmailStart("", "", "", 30, "", 30, "", null)
            coVerify { mockApiService.resetByEmailStart(any()) }
        }

    @Test
    fun `StytchApi Passwords resetByEmail calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.resetByEmail(any()) } returns mockk(relaxed = true)
            StytchApi.Passwords.resetByEmail("", "", 30, "", null)
            coVerify { mockApiService.resetByEmail(any()) }
        }

    @Test
    fun `StytchApi Passwords resetBySession calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.resetBySession(any()) } returns mockk(relaxed = true)
            StytchApi.Passwords.resetBySession(password = "", sessionDurationMinutes = 30, locale = null)
            coVerify { mockApiService.resetBySession(any()) }
        }

    @Test
    fun `StytchApi Passwords resetByExisting calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.resetByExistingPassword(any()) } returns mockk(relaxed = true)
            StytchApi.Passwords.resetByExisting(
                email = "",
                existingPassword = "",
                newPassword = "",
                sessionDurationMinutes = 30,
            )
            coVerify { mockApiService.resetByExistingPassword(any()) }
        }

    @Test
    fun `StytchApi Passwords strengthCheck calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.strengthCheck(any()) } returns mockk(relaxed = true)
            StytchApi.Passwords.strengthCheck("", "")
            coVerify { mockApiService.strengthCheck(any()) }
        }

    @Test
    fun `StytchApi Sessions authenticate calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.authenticateSessions(any()) } returns mockk(relaxed = true)
            StytchApi.Sessions.authenticate(30)
            coVerify { mockApiService.authenticateSessions(any()) }
        }

    @Test
    fun `StytchApi Sessions revoke calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.revokeSessions() } returns mockk(relaxed = true)
            StytchApi.Sessions.revoke()
            coVerify { mockApiService.revokeSessions() }
        }

    @Test
    fun `StytchApi Sessions attest calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.sessionAttest(any()) } returns mockk(relaxed = true)
            StytchApi.Sessions.attest("", "")
            coVerify { mockApiService.sessionAttest(any()) }
        }

    @Test
    fun `StytchApi Biometrics registerStart calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.biometricsRegisterStart(any()) } returns mockk(relaxed = true)
            StytchApi.Biometrics.registerStart("")
            coVerify { mockApiService.biometricsRegisterStart(any()) }
        }

    @Test
    fun `StytchApi Biometrics register calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.biometricsRegister(any()) } returns mockk(relaxed = true)
            StytchApi.Biometrics.register("", "", 30)
            coVerify { mockApiService.biometricsRegister(any()) }
        }

    @Test
    fun `StytchApi Biometrics authenticateStart calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.biometricsAuthenticateStart(any()) } returns mockk(relaxed = true)
            StytchApi.Biometrics.authenticateStart("")
            coVerify { mockApiService.biometricsAuthenticateStart(any()) }
        }

    @Test
    fun `StytchApi Biometrics authenticate calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.biometricsAuthenticate(any()) } returns mockk(relaxed = true)
            StytchApi.Biometrics.authenticate("", "", 30)
            coVerify { mockApiService.biometricsAuthenticate(any()) }
        }

    @Test
    fun `StytchApi User getUser calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.getUser() } returns mockk(relaxed = true)
            StytchApi.UserManagement.getUser()
            coVerify { mockApiService.getUser() }
        }

    @Test
    fun `StytchApi User deleteEmailById calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.deleteEmailById("emailAddressId") } returns mockk(relaxed = true)
            StytchApi.UserManagement.deleteEmailById("emailAddressId")
            coVerify { mockApiService.deleteEmailById("emailAddressId") }
        }

    @Test
    fun `StytchApi User deletePhoneNumberById calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.deletePhoneNumberById("phoneNumberId") } returns mockk(relaxed = true)
            StytchApi.UserManagement.deletePhoneNumberById("phoneNumberId")
            coVerify { mockApiService.deletePhoneNumberById("phoneNumberId") }
        }

    @Test
    fun `StytchApi User deleteBiometricRegistrationById calls appropriate apiService method`() =
        runBlocking {
            coEvery {
                mockApiService.deleteBiometricRegistrationById("biometricsRegistrationId")
            } returns mockk(relaxed = true)
            StytchApi.UserManagement.deleteBiometricRegistrationById("biometricsRegistrationId")
            coVerify { mockApiService.deleteBiometricRegistrationById("biometricsRegistrationId") }
        }

    @Test
    fun `StytchApi User deleteCryptoWalletById calls appropriate apiService method`() =
        runBlocking {
            coEvery {
                mockApiService.deleteCryptoWalletById(any())
            } returns mockk(relaxed = true)
            StytchApi.UserManagement.deleteCryptoWalletById("crypto-registration-id")
            coVerify { mockApiService.deleteCryptoWalletById("crypto-registration-id") }
        }

    @Test
    fun `StytchApi User deleteWebAuthnById calls appropriate apiService method`() =
        runBlocking {
            coEvery {
                mockApiService.deleteWebAuthnById(any())
            } returns mockk(relaxed = true)
            StytchApi.UserManagement.deleteWebAuthnById("webauthn-registration-id")
            coVerify { mockApiService.deleteWebAuthnById("webauthn-registration-id") }
        }

    @Test
    fun `StytchApi User deleteTOTPById calls appropriate apiService method`() =
        runBlocking {
            coEvery {
                mockApiService.deleteTOTPById(any())
            } returns mockk(relaxed = true)
            StytchApi.UserManagement.deleteTotpById("totp-registration-id")
            coVerify { mockApiService.deleteTOTPById("totp-registration-id") }
        }

    @Test
    fun `StytchApi User deleteOAuthById calls appropriate apiService method`() =
        runBlocking {
            coEvery {
                mockApiService.deleteOAuthById(any())
            } returns mockk(relaxed = true)
            StytchApi.UserManagement.deleteOAuthById("oauth-registration-id")
            coVerify { mockApiService.deleteOAuthById("oauth-registration-id") }
        }

    @Test
    fun `StytchApi User updateUser calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.updateUser(any()) } returns mockk(relaxed = true)
            StytchApi.UserManagement.updateUser(mockk(), mockk())
            coVerify { mockApiService.updateUser(any()) }
        }

    @Test
    fun `StytchApi OAuth authenticateWithGoogleIdToken calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.authenticateWithGoogleIdToken(any()) } returns mockk(relaxed = true)
            StytchApi.OAuth.authenticateWithGoogleIdToken(
                idToken = "id_token",
                nonce = "nonce",
                sessionDurationMinutes = 30,
            )
            coVerify {
                mockApiService.authenticateWithGoogleIdToken(
                    ConsumerRequests.OAuth.Google.AuthenticateRequest(
                        idToken = "id_token",
                        nonce = "nonce",
                        sessionDurationMinutes = 30,
                    ),
                )
            }
        }

    @Test
    fun `StytchApi OAuth authenticateWithThirdPartyToken calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.authenticateWithThirdPartyToken(any()) } returns mockk(relaxed = true)
            StytchApi.OAuth.authenticateWithThirdPartyToken(
                token = "id_token",
                sessionDurationMinutes = 30,
                codeVerifier = "code_challenge",
            )
            coVerify {
                mockApiService.authenticateWithThirdPartyToken(
                    ConsumerRequests.OAuth.ThirdParty.AuthenticateRequest(
                        token = "id_token",
                        sessionDurationMinutes = 30,
                        codeVerifier = "code_challenge",
                    ),
                )
            }
        }

    @Test
    fun `StytchApi Bootstrap getBootstrapData calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.getBootstrapData(any()) } returns mockk(relaxed = true)
            StytchApi.getBootstrapData()
            coVerify { mockApiService.getBootstrapData(any()) }
        }

    @Test
    fun `StytchApi User searchUsers calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.searchUsers(any()) } returns mockk(relaxed = true)
            StytchApi.UserManagement.searchUsers("user@domain.com")
            coVerify {
                mockApiService.searchUsers(
                    ConsumerRequests.User.SearchRequest(
                        email = "user@domain.com",
                    ),
                )
            }
        }

    @Test
    fun `StytchApi Webauthn registerStart calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.webAuthnRegisterStart(any()) } returns mockk(relaxed = true)
            StytchApi.WebAuthn.registerStart("")
            coVerify { mockApiService.webAuthnRegisterStart(any()) }
        }

    @Test
    fun `StytchApi Webauthn register calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.webAuthnRegister(any()) } returns mockk(relaxed = true)
            StytchApi.WebAuthn.register("")
            coVerify { mockApiService.webAuthnRegister(any()) }
        }

    @Test
    fun `StytchApi Webauthn webAuthnAuthenticateStartPrimary calls appropriate apiService method`() =
        runBlocking {
            coEvery {
                mockApiService.webAuthnAuthenticateStartPrimary(any())
            } returns mockk(relaxed = true)
            StytchApi.WebAuthn.authenticateStartPrimary("", false)
            coVerify { mockApiService.webAuthnAuthenticateStartPrimary(any()) }
        }

    @Test
    fun `StytchApi Webauthn webAuthnAuthenticateStartSecondary calls appropriate apiService method`() =
        runBlocking {
            coEvery {
                mockApiService.webAuthnAuthenticateStartSecondary(any())
            } returns mockk(relaxed = true)
            StytchApi.WebAuthn.authenticateStartSecondary("", true)
            coVerify { mockApiService.webAuthnAuthenticateStartSecondary(any()) }
        }

    @Test
    fun `StytchApi Webauthn webAuthnAuthenticate calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.webAuthnAuthenticate(any()) } returns mockk(relaxed = true)
            StytchApi.WebAuthn.authenticate("", 30)
            coVerify { mockApiService.webAuthnAuthenticate(any()) }
        }

    @Test
    fun `StytchApi Webauthn webAuthnUpdate calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.webAuthnUpdate(any(), any()) } returns mockk(relaxed = true)
            StytchApi.WebAuthn.update("", "my new name")
            coVerify { mockApiService.webAuthnUpdate("", any()) }
        }

    @Test
    fun `StytchApi Crypto authenticateStartPrimary calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.cryptoWalletAuthenticateStartPrimary(any()) } returns mockk(relaxed = true)
            StytchApi.Crypto.authenticateStartPrimary(
                cryptoWalletAddress = "",
                cryptoWalletType = CryptoWalletType.ETHEREUM,
            )
            coVerify { mockApiService.cryptoWalletAuthenticateStartPrimary(any()) }
        }

    @Test
    fun `StytchApi Crypto authenticateStartSecondary calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.cryptoWalletAuthenticateStartSecondary(any()) } returns mockk(relaxed = true)
            StytchApi.Crypto.authenticateStartSecondary(
                cryptoWalletAddress = "",
                cryptoWalletType = CryptoWalletType.ETHEREUM,
            )
            coVerify { mockApiService.cryptoWalletAuthenticateStartSecondary(any()) }
        }

    @Test
    fun `StytchApi Crypto authenticate calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.cryptoWalletAuthenticate(any()) } returns mockk(relaxed = true)
            StytchApi.Crypto.authenticate(
                cryptoWalletAddress = "",
                cryptoWalletType = CryptoWalletType.ETHEREUM,
                signature = "",
                sessionDurationMinutes = 30,
            )
            coVerify { mockApiService.cryptoWalletAuthenticate(any()) }
        }

    @Test
    fun `StytchApi TOTP create calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.totpsCreate(any()) } returns mockk(relaxed = true)
            StytchApi.TOTP.create(expirationMinutes = 5)
            coVerify { mockApiService.totpsCreate(any()) }
        }

    @Test
    fun `StytchApi TOTP authenticate calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.totpsAuthenticate(any()) } returns mockk(relaxed = true)
            StytchApi.TOTP.authenticate(
                totpCode = "123456",
                sessionDurationMinutes = 30,
            )
            coVerify { mockApiService.totpsAuthenticate(any()) }
        }

    @Test
    fun `StytchApi TOTP recoveryCodes calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.totpsRecoveryCodes() } returns mockk(relaxed = true)
            StytchApi.TOTP.recoveryCodes()
            coVerify { mockApiService.totpsRecoveryCodes() }
        }

    @Test
    fun `StytchApi TOTP recover calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.totpsRecover(any()) } returns mockk(relaxed = true)
            StytchApi.TOTP.recover(
                recoveryCode = "recovery-code",
                sessionDurationMinutes = 30,
            )
            coVerify { mockApiService.totpsRecover(any()) }
        }

    @Test
    fun `StytchApi Events logEvent calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockApiService.logEvent(any()) } returns mockk(relaxed = true)
            val details = mapOf("test-key" to "test value")
            val header = InfoHeaderModel.fromDeviceInfo(mockDeviceInfo)
            val now = Date()
            val result =
                StytchApi.Events.logEvent(
                    eventId = "event-id",
                    appSessionId = "app-session-id",
                    persistentId = "persistent-id",
                    clientSentAt = now,
                    timezone = "Timezone/Identifier",
                    eventName = "event-name",
                    infoHeaderModel = header,
                    details = details,
                )
            coVerify(exactly = 1) {
                mockApiService.logEvent(
                    listOf(
                        CommonRequests.Events.Event(
                            telemetry =
                                CommonRequests.Events.EventTelemetry(
                                    eventId = "event-id",
                                    appSessionId = "app-session-id",
                                    persistentId = "persistent-id",
                                    clientSentAt = now,
                                    timezone = "Timezone/Identifier",
                                    app =
                                        CommonRequests.Events.VersionIdentifier(
                                            identifier = header.app.identifier,
                                            version = header.app.version,
                                        ),
                                    os =
                                        CommonRequests.Events.VersionIdentifier(
                                            identifier = header.os.identifier,
                                            version = header.os.version,
                                        ),
                                    sdk =
                                        CommonRequests.Events.VersionIdentifier(
                                            identifier = header.sdk.identifier,
                                            version = header.sdk.version,
                                        ),
                                    device =
                                        CommonRequests.Events.DeviceIdentifier(
                                            model = header.device.identifier,
                                            screenSize = header.device.version,
                                        ),
                                ),
                            event =
                                CommonRequests.Events.EventEvent(
                                    publicToken = "",
                                    eventName = "event-name",
                                    details = details,
                                ),
                        ),
                    ),
                )
            }
        }

    @Test
    fun `safeApiCall returns success when call succeeds`() =
        runBlocking {
            fun mockApiCall(): StytchDataResponse<Boolean> = StytchDataResponse(true)
            val result = StytchApi.safeConsumerApiCall { mockApiCall() }
            assert(result is StytchResult.Success)
        }

    @Test
    fun `safeApiCall returns correct error for HttpException`() =
        runBlocking {
            fun mockApiCall(): StytchDataResponse<Boolean> =
                throw HttpException(
                    mockk(relaxed = true) {
                        every { errorBody() } returns null
                    },
                )
            val result = StytchApi.safeConsumerApiCall { mockApiCall() }
            assert(result is StytchResult.Error)
        }

    @Test
    fun `safeApiCall returns correct error for StytchErrors`() =
        runBlocking {
            fun mockApiCall(): StytchDataResponse<Boolean> =
                throw StytchAPIError(errorType = StytchAPIErrorType.UNKNOWN_ERROR, message = "", statusCode = 400)
            val result = StytchApi.safeConsumerApiCall { mockApiCall() }
            assert(result is StytchResult.Error)
        }

    @Test
    fun `safeApiCall returns correct error for other exceptions`() =
        runBlocking {
            fun mockApiCall(): StytchDataResponse<Boolean> {
                error("Test")
            }
            try {
                val result = StytchApi.safeConsumerApiCall { mockApiCall() }
                assert(result is StytchResult.Error)
            } catch (_: Exception) {
                fail("safeApiCall should not throw an exception")
            }
        }
}
