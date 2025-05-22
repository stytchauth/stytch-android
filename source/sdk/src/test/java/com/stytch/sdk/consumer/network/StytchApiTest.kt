package com.stytch.sdk.consumer.network

import android.app.Application
import android.content.Context
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.EndpointOptions
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchAPIError
import com.stytch.sdk.common.errors.StytchAPIErrorType
import com.stytch.sdk.common.errors.StytchSDKNotConfiguredError
import com.stytch.sdk.common.network.InfoHeaderModel
import com.stytch.sdk.common.network.StytchDataResponse
import com.stytch.sdk.common.network.models.CommonRequests
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.network.models.ConsumerRequests
import com.stytch.sdk.consumer.network.models.CryptoWalletType
import com.stytch.sdk.consumer.sessions.ConsumerSessionStorage
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
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.security.KeyStore

internal class StytchApiTest {
    var mContextMock = mockk<Context>(relaxed = true)

    @MockK
    private lateinit var mockConsumerSessionStorage: ConsumerSessionStorage

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
        mockkObject(StytchApi)
        MockKAnnotations.init(this, true, true)
        every { EncryptionManager.createNewKeys(any()) } returns Unit
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(StorageHelper)
        StytchClient.sessionStorage = mockConsumerSessionStorage
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `StytchApi isInitialized returns correctly based on configuration state`() {
        StytchApi.configure("publicToken", DeviceInfo(), EndpointOptions(), { null }, mockk())
        assert(StytchApi.isInitialized)
    }

    @Test
    fun `StytchApi apiService is available when configured`() {
        StytchClient.configure(mContextMock, "")
        StytchApi.apiService
    }

    // TODO every method calls safeApi

    @Test
    fun `StytchApi MagicLinks Email loginOrCreate calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.loginOrCreateUserByEmail(any()) } returns mockk(relaxed = true)
            StytchApi.MagicLinks.Email.loginOrCreate("", null, null, "", null, null)
            coVerify { StytchApi.apiService.loginOrCreateUserByEmail(any()) }
        }

    @Test
    fun `StytchApi MagicLinks Email sendPrimary calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.sendEmailMagicLinkPrimary(any()) } returns mockk(relaxed = true)
            StytchApi.MagicLinks.Email.sendPrimary("", null, null, null, null, null, null, null)
            coVerify { StytchApi.apiService.sendEmailMagicLinkPrimary(any()) }
        }

    @Test
    fun `StytchApi MagicLinks Email sendSecondar calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.sendEmailMagicLinkSecondary(any()) } returns mockk(relaxed = true)
            StytchApi.MagicLinks.Email.sendSecondary("", null, null, null, null, null, null, null)
            coVerify { StytchApi.apiService.sendEmailMagicLinkSecondary(any()) }
        }

    @Test
    fun `StytchApi MagicLinks Email authenticate calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.authenticate(any()) } returns mockk(relaxed = true)
            StytchApi.MagicLinks.Email.authenticate("", 30, "")
            coVerify { StytchApi.apiService.authenticate(any()) }
        }

    @Test
    fun `StytchApi OTP loginOrCreateByOTPWithSMS calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.loginOrCreateUserByOTPWithSMS(any()) } returns mockk(relaxed = true)
            StytchApi.OTP.loginOrCreateByOTPWithSMS("", 30)
            coVerify { StytchApi.apiService.loginOrCreateUserByOTPWithSMS(any()) }
        }

    @Test
    fun `StytchApi OTP sendOTPWithSMSPrimary calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.sendOTPWithSMSPrimary(any()) } returns mockk(relaxed = true)
            StytchApi.OTP.sendOTPWithSMSPrimary("", 30)
            coVerify { StytchApi.apiService.sendOTPWithSMSPrimary(any()) }
        }

    @Test
    fun `StytchApi OTP sendOTPWithSMSSecondary calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.sendOTPWithSMSSecondary(any()) } returns mockk(relaxed = true)
            StytchApi.OTP.sendOTPWithSMSSecondary("", 30)
            coVerify { StytchApi.apiService.sendOTPWithSMSSecondary(any()) }
        }

    @Test
    fun `StytchApi OTP loginOrCreateUserByOTPWithWhatsApp calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.loginOrCreateUserByOTPWithWhatsApp(any()) } returns mockk(relaxed = true)
            StytchApi.OTP.loginOrCreateUserByOTPWithWhatsApp("", 30)
            coVerify { StytchApi.apiService.loginOrCreateUserByOTPWithWhatsApp(any()) }
        }

    @Test
    fun `StytchApi OTP sendByOTPWithWhatsAppPrimary calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.sendOTPWithWhatsAppPrimary(any()) } returns mockk(relaxed = true)
            StytchApi.OTP.sendOTPWithWhatsAppPrimary("", 30, null)
            coVerify { StytchApi.apiService.sendOTPWithWhatsAppPrimary(any()) }
        }

    @Test
    fun `StytchApi OTP sendByOTPWithWhatsAppSecondary calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.sendOTPWithWhatsAppSecondary(any()) } returns mockk(relaxed = true)
            StytchApi.OTP.sendOTPWithWhatsAppSecondary("", 30, null)
            coVerify { StytchApi.apiService.sendOTPWithWhatsAppSecondary(any()) }
        }

    @Test
    fun `StytchApi OTP loginOrCreateUserByOTPWithEmail calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.loginOrCreateUserByOTPWithEmail(any()) } returns mockk(relaxed = true)
            StytchApi.OTP.loginOrCreateUserByOTPWithEmail("", 30, "", "", null)
            coVerify { StytchApi.apiService.loginOrCreateUserByOTPWithEmail(any()) }
        }

    @Test
    fun `StytchApi OTP sendOTPWithEmailPrimary calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.sendOTPWithEmailPrimary(any()) } returns mockk(relaxed = true)
            StytchApi.OTP.sendOTPWithEmailPrimary("", 30, null, null)
            coVerify { StytchApi.apiService.sendOTPWithEmailPrimary(any()) }
        }

    @Test
    fun `StytchApi OTP sendOTPWithEmailSecondary calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.sendOTPWithEmailSecondary(any()) } returns mockk(relaxed = true)
            StytchApi.OTP.sendOTPWithEmailSecondary("", 30, null, null)
            coVerify { StytchApi.apiService.sendOTPWithEmailSecondary(any()) }
        }

    @Test
    fun `StytchApi OTP authenticateWithOTP calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.authenticateWithOTP(any()) } returns mockk(relaxed = true)
            StytchApi.OTP.authenticateWithOTP("", "")
            coVerify { StytchApi.apiService.authenticateWithOTP(any()) }
        }

    @Test
    fun `StytchApi Passwords authenticate calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.authenticateWithPasswords(any()) } returns mockk(relaxed = true)
            StytchApi.Passwords.authenticate("", "", 30)
            coVerify { StytchApi.apiService.authenticateWithPasswords(any()) }
        }

    @Test
    fun `StytchApi Passwords create calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.passwords(any()) } returns mockk(relaxed = true)
            StytchApi.Passwords.create("", "", 30)
            coVerify { StytchApi.apiService.passwords(any()) }
        }

    @Test
    fun `StytchApi Passwords resetByEmailStart calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.resetByEmailStart(any()) } returns mockk(relaxed = true)
            StytchApi.Passwords.resetByEmailStart("", "", "", 30, "", 30, "", null)
            coVerify { StytchApi.apiService.resetByEmailStart(any()) }
        }

    @Test
    fun `StytchApi Passwords resetByEmail calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.resetByEmail(any()) } returns mockk(relaxed = true)
            StytchApi.Passwords.resetByEmail("", "", 30, "", null)
            coVerify { StytchApi.apiService.resetByEmail(any()) }
        }

    @Test
    fun `StytchApi Passwords resetBySession calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.resetBySession(any()) } returns mockk(relaxed = true)
            StytchApi.Passwords.resetBySession(password = "", sessionDurationMinutes = 30, locale = null)
            coVerify { StytchApi.apiService.resetBySession(any()) }
        }

    @Test
    fun `StytchApi Passwords resetByExisting calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.resetByExistingPassword(any()) } returns mockk(relaxed = true)
            StytchApi.Passwords.resetByExisting(
                email = "",
                existingPassword = "",
                newPassword = "",
                sessionDurationMinutes = 30,
            )
            coVerify { StytchApi.apiService.resetByExistingPassword(any()) }
        }

    @Test
    fun `StytchApi Passwords strengthCheck calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.strengthCheck(any()) } returns mockk(relaxed = true)
            StytchApi.Passwords.strengthCheck("", "")
            coVerify { StytchApi.apiService.strengthCheck(any()) }
        }

    @Test
    fun `StytchApi Sessions authenticate calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.authenticateSessions(any()) } returns mockk(relaxed = true)
            StytchApi.Sessions.authenticate(30)
            coVerify { StytchApi.apiService.authenticateSessions(any()) }
        }

    @Test
    fun `StytchApi Sessions revoke calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.revokeSessions() } returns mockk(relaxed = true)
            StytchApi.Sessions.revoke()
            coVerify { StytchApi.apiService.revokeSessions() }
        }

    @Test
    fun `StytchApi Biometrics registerStart calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.biometricsRegisterStart(any()) } returns mockk(relaxed = true)
            StytchApi.Biometrics.registerStart("")
            coVerify { StytchApi.apiService.biometricsRegisterStart(any()) }
        }

    @Test
    fun `StytchApi Biometrics register calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.biometricsRegister(any()) } returns mockk(relaxed = true)
            StytchApi.Biometrics.register("", "", 30)
            coVerify { StytchApi.apiService.biometricsRegister(any()) }
        }

    @Test
    fun `StytchApi Biometrics authenticateStart calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.biometricsAuthenticateStart(any()) } returns mockk(relaxed = true)
            StytchApi.Biometrics.authenticateStart("")
            coVerify { StytchApi.apiService.biometricsAuthenticateStart(any()) }
        }

    @Test
    fun `StytchApi Biometrics authenticate calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.biometricsAuthenticate(any()) } returns mockk(relaxed = true)
            StytchApi.Biometrics.authenticate("", "", 30)
            coVerify { StytchApi.apiService.biometricsAuthenticate(any()) }
        }

    @Test
    fun `StytchApi User getUser calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.getUser() } returns mockk(relaxed = true)
            StytchApi.UserManagement.getUser()
            coVerify { StytchApi.apiService.getUser() }
        }

    @Test
    fun `StytchApi User deleteEmailById calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.deleteEmailById("emailAddressId") } returns mockk(relaxed = true)
            StytchApi.UserManagement.deleteEmailById("emailAddressId")
            coVerify { StytchApi.apiService.deleteEmailById("emailAddressId") }
        }

    @Test
    fun `StytchApi User deletePhoneNumberById calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.deletePhoneNumberById("phoneNumberId") } returns mockk(relaxed = true)
            StytchApi.UserManagement.deletePhoneNumberById("phoneNumberId")
            coVerify { StytchApi.apiService.deletePhoneNumberById("phoneNumberId") }
        }

    @Test
    fun `StytchApi User deleteBiometricRegistrationById calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery {
                StytchApi.apiService.deleteBiometricRegistrationById("biometricsRegistrationId")
            } returns mockk(relaxed = true)
            StytchApi.UserManagement.deleteBiometricRegistrationById("biometricsRegistrationId")
            coVerify { StytchApi.apiService.deleteBiometricRegistrationById("biometricsRegistrationId") }
        }

    @Test
    fun `StytchApi User deleteCryptoWalletById calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery {
                StytchApi.apiService.deleteCryptoWalletById(any())
            } returns mockk(relaxed = true)
            StytchApi.UserManagement.deleteCryptoWalletById("crypto-registration-id")
            coVerify { StytchApi.apiService.deleteCryptoWalletById("crypto-registration-id") }
        }

    @Test
    fun `StytchApi User deleteWebAuthnById calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery {
                StytchApi.apiService.deleteWebAuthnById(any())
            } returns mockk(relaxed = true)
            StytchApi.UserManagement.deleteWebAuthnById("webauthn-registration-id")
            coVerify { StytchApi.apiService.deleteWebAuthnById("webauthn-registration-id") }
        }

    @Test
    fun `StytchApi User deleteTOTPById calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery {
                StytchApi.apiService.deleteTOTPById(any())
            } returns mockk(relaxed = true)
            StytchApi.UserManagement.deleteTotpById("totp-registration-id")
            coVerify { StytchApi.apiService.deleteTOTPById("totp-registration-id") }
        }

    @Test
    fun `StytchApi User deleteOAuthById calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery {
                StytchApi.apiService.deleteOAuthById(any())
            } returns mockk(relaxed = true)
            StytchApi.UserManagement.deleteOAuthById("oauth-registration-id")
            coVerify { StytchApi.apiService.deleteOAuthById("oauth-registration-id") }
        }

    @Test
    fun `StytchApi User updateUser calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.updateUser(any()) } returns mockk(relaxed = true)
            StytchApi.UserManagement.updateUser(mockk(), mockk())
            coVerify { StytchApi.apiService.updateUser(any()) }
        }

    @Test
    fun `StytchApi OAuth authenticateWithGoogleIdToken calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.authenticateWithGoogleIdToken(any()) } returns mockk(relaxed = true)
            StytchApi.OAuth.authenticateWithGoogleIdToken(
                idToken = "id_token",
                nonce = "nonce",
                sessionDurationMinutes = 30,
            )
            coVerify {
                StytchApi.apiService.authenticateWithGoogleIdToken(
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
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.authenticateWithThirdPartyToken(any()) } returns mockk(relaxed = true)
            StytchApi.OAuth.authenticateWithThirdPartyToken(
                token = "id_token",
                sessionDurationMinutes = 30,
                codeVerifier = "code_challenge",
            )
            coVerify {
                StytchApi.apiService.authenticateWithThirdPartyToken(
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
            every { StytchApi.isInitialized } returns true
            every { StytchApi.publicToken } returns "mock-public-token"
            coEvery { StytchApi.apiService.getBootstrapData("mock-public-token") } returns mockk(relaxed = true)
            StytchApi.getBootstrapData()
            coVerify { StytchApi.apiService.getBootstrapData("mock-public-token") }
        }

    @Test
    fun `StytchApi User searchUsers calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.searchUsers(any()) } returns mockk(relaxed = true)
            StytchApi.UserManagement.searchUsers("user@domain.com")
            coVerify {
                StytchApi.apiService.searchUsers(
                    ConsumerRequests.User.SearchRequest(
                        email = "user@domain.com",
                    ),
                )
            }
        }

    @Test
    fun `StytchApi Webauthn registerStart calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.webAuthnRegisterStart(mockk(relaxed = true)) } returns mockk(relaxed = true)
            StytchApi.WebAuthn.registerStart("")
            coVerify { StytchApi.apiService.webAuthnRegisterStart(any()) }
        }

    @Test
    fun `StytchApi Webauthn register calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.webAuthnRegister(mockk(relaxed = true)) } returns mockk(relaxed = true)
            StytchApi.WebAuthn.register("")
            coVerify { StytchApi.apiService.webAuthnRegister(any()) }
        }

    @Test
    fun `StytchApi Webauthn webAuthnAuthenticateStartPrimary calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery {
                StytchApi.apiService.webAuthnAuthenticateStartPrimary(mockk(relaxed = true))
            } returns mockk(relaxed = true)
            StytchApi.WebAuthn.authenticateStartPrimary("", false)
            coVerify { StytchApi.apiService.webAuthnAuthenticateStartPrimary(any()) }
        }

    @Test
    fun `StytchApi Webauthn webAuthnAuthenticateStartSecondary calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery {
                StytchApi.apiService.webAuthnAuthenticateStartSecondary(mockk(relaxed = true))
            } returns mockk(relaxed = true)
            StytchApi.WebAuthn.authenticateStartSecondary("", true)
            coVerify { StytchApi.apiService.webAuthnAuthenticateStartSecondary(any()) }
        }

    @Test
    fun `StytchApi Webauthn webAuthnAuthenticate calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.webAuthnAuthenticate(mockk(relaxed = true)) } returns mockk(relaxed = true)
            StytchApi.WebAuthn.authenticate("", 30)
            coVerify { StytchApi.apiService.webAuthnAuthenticate(any()) }
        }

    @Test
    fun `StytchApi Webauthn webAuthnUpdate calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.webAuthnUpdate(any(), any()) } returns mockk(relaxed = true)
            StytchApi.WebAuthn.update("", "my new name")
            coVerify { StytchApi.apiService.webAuthnUpdate("", any()) }
        }

    @Test
    fun `StytchApi Crypto authenticateStartPrimary calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.cryptoWalletAuthenticateStartPrimary(any()) } returns mockk(relaxed = true)
            StytchApi.Crypto.authenticateStartPrimary(
                cryptoWalletAddress = "",
                cryptoWalletType = CryptoWalletType.ETHEREUM,
            )
            coVerify { StytchApi.apiService.cryptoWalletAuthenticateStartPrimary(any()) }
        }

    @Test
    fun `StytchApi Crypto authenticateStartSecondary calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.cryptoWalletAuthenticateStartSecondary(any()) } returns mockk(relaxed = true)
            StytchApi.Crypto.authenticateStartSecondary(
                cryptoWalletAddress = "",
                cryptoWalletType = CryptoWalletType.ETHEREUM,
            )
            coVerify { StytchApi.apiService.cryptoWalletAuthenticateStartSecondary(any()) }
        }

    @Test
    fun `StytchApi Crypto authenticate calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.cryptoWalletAuthenticate(any()) } returns mockk(relaxed = true)
            StytchApi.Crypto.authenticate(
                cryptoWalletAddress = "",
                cryptoWalletType = CryptoWalletType.ETHEREUM,
                signature = "",
                sessionDurationMinutes = 30,
            )
            coVerify { StytchApi.apiService.cryptoWalletAuthenticate(any()) }
        }

    @Test
    fun `StytchApi TOTP create calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.totpsCreate(any()) } returns mockk(relaxed = true)
            StytchApi.TOTP.create(expirationMinutes = 5)
            coVerify { StytchApi.apiService.totpsCreate(any()) }
        }

    @Test
    fun `StytchApi TOTP authenticate calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.totpsAuthenticate(any()) } returns mockk(relaxed = true)
            StytchApi.TOTP.authenticate(
                totpCode = "123456",
                sessionDurationMinutes = 30,
            )
            coVerify { StytchApi.apiService.totpsAuthenticate(any()) }
        }

    @Test
    fun `StytchApi TOTP recoveryCodes calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.totpsRecoveryCodes() } returns mockk(relaxed = true)
            StytchApi.TOTP.recoveryCodes()
            coVerify { StytchApi.apiService.totpsRecoveryCodes() }
        }

    @Test
    fun `StytchApi TOTP recover calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            coEvery { StytchApi.apiService.totpsRecover(any()) } returns mockk(relaxed = true)
            StytchApi.TOTP.recover(
                recoveryCode = "recovery-code",
                sessionDurationMinutes = 30,
            )
            coVerify { StytchApi.apiService.totpsRecover(any()) }
        }

    @Test
    fun `StytchApi Events logEvent calls appropriate apiService method`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true
            every { StytchApi.publicToken } returns "mock-public-token"
            coEvery { StytchApi.apiService.logEvent(any()) } returns mockk(relaxed = true)
            val details = mapOf("test-key" to "test value")
            val header = InfoHeaderModel.fromDeviceInfo(mockDeviceInfo)
            val result =
                StytchApi.Events.logEvent(
                    eventId = "event-id",
                    appSessionId = "app-session-id",
                    persistentId = "persistent-id",
                    clientSentAt = "ISO date string",
                    timezone = "Timezone/Identifier",
                    eventName = "event-name",
                    infoHeaderModel = header,
                    details = details,
                )
            coVerify(exactly = 1) {
                StytchApi.apiService.logEvent(
                    listOf(
                        CommonRequests.Events.Event(
                            telemetry =
                                CommonRequests.Events.EventTelemetry(
                                    eventId = "event-id",
                                    appSessionId = "app-session-id",
                                    persistentId = "persistent-id",
                                    clientSentAt = "ISO date string",
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
                                    publicToken = "mock-public-token",
                                    eventName = "event-name",
                                    details = details,
                                ),
                        ),
                    ),
                )
            }
        }

    @Test(expected = StytchSDKNotConfiguredError::class)
    fun `safeApiCall throws exception when StytchClient is not initialized`(): Unit =
        runBlocking {
            every { StytchApi.isInitialized } returns false
            val mockApiCall: suspend () -> StytchDataResponse<Boolean> = mockk()
            StytchApi.safeConsumerApiCall { mockApiCall() }
        }

    @Test
    fun `safeApiCall returns success when call succeeds`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true

            fun mockApiCall(): StytchDataResponse<Boolean> = StytchDataResponse(true)
            val result = StytchApi.safeConsumerApiCall { mockApiCall() }
            assert(result is StytchResult.Success)
        }

    @Test
    fun `safeApiCall returns correct error for HttpException`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true

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
            every { StytchApi.isInitialized } returns true

            fun mockApiCall(): StytchDataResponse<Boolean> =
                throw StytchAPIError(errorType = StytchAPIErrorType.UNKNOWN_ERROR, message = "", statusCode = 400)
            val result = StytchApi.safeConsumerApiCall { mockApiCall() }
            assert(result is StytchResult.Error)
        }

    @Test
    fun `safeApiCall returns correct error for other exceptions`() =
        runBlocking {
            every { StytchApi.isInitialized } returns true

            fun mockApiCall(): StytchDataResponse<Boolean> {
                error("Test")
            }
            val result = StytchApi.safeConsumerApiCall { mockApiCall() }
            assert(result is StytchResult.Error)
        }
}
