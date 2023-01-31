package com.stytch.sdk.network

import com.stytch.sdk.utils.verifyDelete
import com.stytch.sdk.utils.verifyGet
import com.stytch.sdk.utils.verifyPost
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test

private const val EMAIL = "email@email.com"
private const val LOGIN_MAGIC_LINK = "loginMagicLink://"

internal class StytchApiServiceTests {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: StytchApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start(12345)
        mockWebServer.enqueue(MockResponse().setResponseCode(200))
        apiService = StytchApiService.createApiService(mockWebServer.url("/").toString(), null)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    // region MagicLinks

    @Test
    fun `check magic links email loginOrCreate request`() {
        runBlocking {
            try {
                apiService.loginOrCreateUserByEmail(
                    StytchRequests.MagicLinks.Email.LoginOrCreateUserRequest(
                        EMAIL,
                        LOGIN_MAGIC_LINK,
                        "123",
                        "method2"
                    )
                )
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/magic_links/email/login_or_create",
                expectedBody = mapOf(
                    "email" to EMAIL,
                    "login_magic_link_url" to LOGIN_MAGIC_LINK,
                    "code_challenge" to "123",
                    "code_challenge_method" to "method2"
                )
            )
        }
    }

    @Test
    fun `check magic links authenticate request`() {
        runBlocking {
            try {
                apiService.authenticate(
                    StytchRequests.MagicLinks.AuthenticateRequest(
                        "token",
                        "123",
                        60
                    )
                )
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/magic_links/authenticate",
                expectedBody = mapOf(
                    "token" to "token",
                    "session_duration_minutes" to 60
                )
            )
        }
    }

    // endregion MagicLinks

    // region OTP
    @Test
    fun `check OTP email loginOrCreate with default expiration request`() {
        runBlocking {
            try {
                apiService.loginOrCreateUserByOTPWithEmail(
                    StytchRequests.OTP.Email(
                        EMAIL,
                        60
                    )
                )
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/otps/email/login_or_create",
                expectedBody = mapOf(
                    "email" to EMAIL,
                    "expiration_minutes" to 60
                )
            )
        }
    }

    @Test
    fun `check OTP sms loginOrCreate request`() {
        runBlocking {
            try {
                apiService.loginOrCreateUserByOTPWithSMS(
                    StytchRequests.OTP.SMS(
                        "000",
                        24
                    )
                )
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/otps/sms/login_or_create",
                expectedBody = mapOf(
                    "phone_number" to "000",
                    "expiration_minutes" to 24
                )
            )
        }
    }

    @Test
    fun `check OTP whatsapp loginOrCreate with default expiration request`() {
        runBlocking {
            try {
                apiService.loginOrCreateUserByOTPWithWhatsApp(
                    StytchRequests.OTP.WhatsApp(
                        "000",
                        60
                    )
                )
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/otps/whatsapp/login_or_create",
                expectedBody = mapOf(
                    "phone_number" to "000",
                    "expiration_minutes" to 60
                )
            )
        }
    }

    @Test
    fun `check OTP authenticate request`() {
        runBlocking {
            try {
                apiService.authenticateWithOTP(
                    StytchRequests.OTP.Authenticate(
                        "token",
                        "methodId",
                        60
                    )
                )
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/otps/authenticate",
                expectedBody = mapOf(
                    "token" to "token",
                    "method_id" to "methodId",
                    "session_duration_minutes" to 60
                )
            )
        }
    }

    // endregion OTP

    // region Passwords

    @Test
    fun `check Passwords create request`() {
        runBlocking {
            try {
                apiService.passwords(
                    StytchRequests.Passwords.CreateRequest(
                        EMAIL,
                        "123asd",
                        60
                    )
                )
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/passwords",
                expectedBody = mapOf(
                    "email" to EMAIL,
                    "password" to "123asd",
                    "session_duration_minutes" to 60
                )
            )
        }
    }

    @Test
    fun `check Passwords strenghtCheck request`() {
        runBlocking {
            try {
                apiService.strengthCheck(
                    StytchRequests.Passwords.StrengthCheckRequest(
                        EMAIL,
                        "123asd"
                    )
                )
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/passwords/strength_check",
                expectedBody = mapOf(
                    "email" to EMAIL,
                    "password" to "123asd"
                )
            )
        }
    }

    @Test
    fun `check Passwords resetbyEmail request`() {
        runBlocking {
            try {
                apiService.resetByEmail(
                    StytchRequests.Passwords.ResetByEmailRequest(
                        "token",
                        "123asd",
                        60,
                        "ver1"
                    )
                )
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/passwords/email/reset",
                expectedBody = mapOf(
                    "token" to "token",
                    "password" to "123asd",
                    "session_duration_minutes" to 60,
                    "code_verifier" to "ver1"
                )
            )
        }
    }

    @Test
    fun `check Passwords resetbyEmailStart request`() {
        runBlocking {
            try {
                apiService.resetByEmailStart(
                    StytchRequests.Passwords.ResetByEmailStartRequest(
                        EMAIL,
                        "123",
                        "method2",
                        "loginRedirect",
                        24,
                        "resetPasswordUrl",
                        23
                    )
                )
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/passwords/email/reset/start",
                expectedBody = mapOf(
                    "email" to EMAIL,
                    "code_challenge" to "123",
                    "code_challenge_method" to "method2",
                    "login_redirect_url" to "loginRedirect",
                    "reset_password_redirect_url" to "resetPasswordUrl",
                    "login_expiration_minutes" to 24,
                    "reset_password_expiration_minutes" to 23
                )
            )
        }
    }

    @Test
    fun `check Passwords authenticate request`() {
        runBlocking {
            try {
                apiService.authenticateWithPasswords(
                    StytchRequests.Passwords.AuthenticateRequest(
                        EMAIL,
                        "123asd",
                        46
                    )
                )
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/passwords/authenticate",
                expectedBody = mapOf(
                    "email" to EMAIL,
                    "password" to "123asd",
                    "session_duration_minutes" to 46
                )
            )
        }
    }

    // endregion Passwords

    // region Sessions

    @Test
    fun `check Sessions authenticate request`() {
        runBlocking {
            try {
                apiService.authenticateSessions(
                    StytchRequests.Sessions.AuthenticateRequest(
                        24
                    )
                )
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/sessions/authenticate",
                expectedBody = mapOf(
                    "session_duration_minutes" to 24
                )
            )
        }
    }

    @Test
    fun `check Sessions revoke request`() {
        runBlocking {
            try {
                apiService.revokeSessions()
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/sessions/revoke"
            )
        }
    }

    // endregion Sessions

    // region Biometrics
    @Test
    fun `check biometricsRegisterStart request`() {
        runBlocking {
            try {
                apiService.biometricsRegisterStart(
                    StytchRequests.Biometrics.RegisterStartRequest(publicKey = "publicKey")
                )
            } catch (_: Exception) {}
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/biometrics/register/start",
                expectedBody = mapOf(
                    "public_key" to "publicKey"
                )
            )
        }
    }

    @Test
    fun `check biometricsRegister request`() {
        runBlocking {
            try {
                apiService.biometricsRegister(
                    StytchRequests.Biometrics.RegisterRequest(
                        signature = "signature",
                        biometricRegistrationId = "biometricRegistrationId",
                        sessionDurationMinutes = 30
                    )
                )
            } catch (_: Exception) {}
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/biometrics/register",
                expectedBody = mapOf(
                    "signature" to "signature",
                    "biometric_registration_id" to "biometricRegistrationId",
                    "session_duration_minutes" to 30
                )
            )
        }
    }

    @Test
    fun `check biometricsAuthenticateStart request`() {
        runBlocking {
            try {
                apiService.biometricsAuthenticateStart(
                    StytchRequests.Biometrics.AuthenticateStartRequest(publicKey = "publicKey")
                )
            } catch (_: Exception) {}
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/biometrics/authenticate/start",
                expectedBody = mapOf(
                    "public_key" to "publicKey"
                )
            )
        }
    }

    @Test
    fun `check biometricsAuthenticate request`() {
        runBlocking {
            try {
                apiService.biometricsAuthenticate(
                    StytchRequests.Biometrics.AuthenticateRequest(
                        signature = "signature",
                        biometricRegistrationId = "biometricRegistrationId",
                        sessionDurationMinutes = 30
                    )
                )
            } catch (_: Exception) {}
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/biometrics/authenticate",
                expectedBody = mapOf(
                    "signature" to "signature",
                    "biometric_registration_id" to "biometricRegistrationId",
                    "session_duration_minutes" to 30
                )
            )
        }
    }
    // endregion Biometrics

    // region UserManagement
    @Test
    fun `check getUser request`() {
        runBlocking {
            try {
                apiService.getUser()
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyGet("/users/me")
        }
    }

    @Test
    fun `check deleteEmailById request`() {
        runBlocking {
            try {
                apiService.deleteEmailById("email_id")
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyDelete("/users/emails/email_id")
        }
    }

    @Test
    fun `check deletePhoneNumberById request`() {
        runBlocking {
            try {
                apiService.deletePhoneNumberById("phone_number_id")
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyDelete("/users/phone_numbers/phone_number_id")
        }
    }

    @Test
    fun `check deleteBiometricRegistrationById request`() {
        runBlocking {
            try {
                apiService.deleteBiometricRegistrationById("biometrics_registration_id")
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyDelete("/users/biometric_registrations/biometrics_registration_id")
        }
    }

    @Test
    fun `check deleteCryptoWalletById request`() {
        runBlocking {
            try {
                apiService.deleteCryptoWalletById("crypto_wallet_id")
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyDelete("/users/crypto_wallets/crypto_wallet_id")
        }
    }

    @Test
    fun `check deleteWebAuthnById request`() {
        runBlocking {
            try {
                apiService.deleteWebAuthnById("webauthn_registration_id")
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyDelete("/users/webauthn_registrations/webauthn_registration_id")
        }
    }
    // endregion UserManagement

    // region OAuth
    @Test
    fun `check authenticateWithGoogleIdToken request`() {
        runBlocking {
            try {
                apiService.authenticateWithGoogleIdToken(
                    StytchRequests.OAuth.Google.AuthenticateRequest(
                        idToken = "id_token",
                        nonce = "nonce",
                        sessionDurationMinutes = 30
                    )
                )
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/oauth/google/id_token/authenticate",
                expectedBody = mapOf(
                    "id_token" to "id_token",
                    "nonce" to "nonce",
                    "session_duration_minutes" to 30
                )
            )
        }
    }

    @Test
    fun `check authenticateWithThirdPartyToken request`() {
        runBlocking {
            try {
                apiService.authenticateWithThirdPartyToken(
                    StytchRequests.OAuth.ThirdParty.AuthenticateRequest(
                        token = "id_token",
                        sessionDurationMinutes = 30,
                        codeVerifier = "code_challenge"
                    )
                )
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/oauth/authenticate",
                expectedBody = mapOf(
                    "token" to "id_token",
                    "session_duration_minutes" to 30,
                    "code_verifier" to "code_challenge"
                )
            )
        }
    }
    // endregion OAuth
}
