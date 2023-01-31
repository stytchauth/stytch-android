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
            val request = StytchRequests.MagicLinks.Email.LoginOrCreateUserRequest(
                EMAIL,
                LOGIN_MAGIC_LINK,
                "123",
                "method2"
            )
            try {
                apiService.loginOrCreateUserByEmail(request)
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/magic_links/email/login_or_create",
                expectedBody = mapOf(
                    "email" to request.email,
                    "login_magic_link_url" to request.loginMagicLinkUrl,
                    "code_challenge" to request.codeChallenge,
                    "code_challenge_method" to request.codeChallengeMethod
                )
            )
        }
    }

    @Test
    fun `check magic links authenticate request`() {
        runBlocking {
            val request = StytchRequests.MagicLinks.AuthenticateRequest(
                "token",
                "123",
                60
            )
            try {
                apiService.authenticate(request)
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/magic_links/authenticate",
                expectedBody = mapOf(
                    "token" to request.token,
                    "session_duration_minutes" to request.sessionDurationMinutes,
                    "code_verifier" to request.codeVerifier,
                )
            )
        }
    }

    // endregion MagicLinks

    // region OTP
    @Test
    fun `check OTP email loginOrCreate with default expiration request`() {
        runBlocking {
            val request = StytchRequests.OTP.Email(
                EMAIL,
                60
            )
            try {
                apiService.loginOrCreateUserByOTPWithEmail(request)
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/otps/email/login_or_create",
                expectedBody = mapOf(
                    "email" to request.email,
                    "expiration_minutes" to request.expirationMinutes
                )
            )
        }
    }

    @Test
    fun `check OTP sms loginOrCreate request`() {
        runBlocking {
            val request = StytchRequests.OTP.SMS(
                "000",
                24
            )
            try {
                apiService.loginOrCreateUserByOTPWithSMS(request)
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/otps/sms/login_or_create",
                expectedBody = mapOf(
                    "phone_number" to request.phoneNumber,
                    "expiration_minutes" to request.expirationMinutes
                )
            )
        }
    }

    @Test
    fun `check OTP whatsapp loginOrCreate with default expiration request`() {
        runBlocking {
            val request = StytchRequests.OTP.WhatsApp(
                "000",
                60
            )
            try {
                apiService.loginOrCreateUserByOTPWithWhatsApp(request)
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/otps/whatsapp/login_or_create",
                expectedBody = mapOf(
                    "phone_number" to request.phoneNumber,
                    "expiration_minutes" to request.expirationMinutes
                )
            )
        }
    }

    @Test
    fun `check OTP authenticate request`() {
        runBlocking {
            val request = StytchRequests.OTP.Authenticate(
                "token",
                "methodId",
                60
            )
            try {
                apiService.authenticateWithOTP(request)
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/otps/authenticate",
                expectedBody = mapOf(
                    "token" to request.token,
                    "method_id" to request.methodId,
                    "session_duration_minutes" to request.sessionDurationMinutes,
                )
            )
        }
    }

    // endregion OTP

    // region Passwords

    @Test
    fun `check Passwords create request`() {
        runBlocking {
            val request = StytchRequests.Passwords.CreateRequest(
                EMAIL,
                "123asd",
                60
            )
            try {
                apiService.passwords(request)
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/passwords",
                expectedBody = mapOf(
                    "email" to request.email,
                    "password" to request.password,
                    "session_duration_minutes" to request.sessionDurationMinutes
                )
            )
        }
    }

    @Test
    fun `check Passwords strenghtCheck request`() {
        runBlocking {
            val request = StytchRequests.Passwords.StrengthCheckRequest(
                EMAIL,
                "123asd"
            )
            try {
                apiService.strengthCheck(request)
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/passwords/strength_check",
                expectedBody = mapOf(
                    "email" to request.email,
                    "password" to request.password
                )
            )
        }
    }

    @Test
    fun `check Passwords resetbyEmail request`() {
        runBlocking {
            val request = StytchRequests.Passwords.ResetByEmailRequest(
                "token",
                "123asd",
                60,
                "ver1"
            )
            try {
                apiService.resetByEmail(request)
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/passwords/email/reset",
                expectedBody = mapOf(
                    "token" to request.token,
                    "password" to request.password,
                    "session_duration_minutes" to request.sessionDurationMinutes,
                    "code_verifier" to request.codeVerifier
                )
            )
        }
    }

    @Test
    fun `check Passwords resetbyEmailStart request`() {
        runBlocking {
            val request = StytchRequests.Passwords.ResetByEmailStartRequest(
                EMAIL,
                "123",
                "method2",
                "loginRedirect",
                24,
                "resetPasswordUrl",
                23
            )
            try {
                apiService.resetByEmailStart(request)
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/passwords/email/reset/start",
                expectedBody = mapOf(
                    "email" to request.email,
                    "code_challenge" to request.codeChallenge,
                    "code_challenge_method" to request.codeChallengeMethod,
                    "login_redirect_url" to request.loginRedirectUrl,
                    "reset_password_redirect_url" to request.resetPasswordRedirectUrl,
                    "login_expiration_minutes" to request.loginExpirationMinutes,
                    "reset_password_expiration_minutes" to request.resetPasswordExpirationMinutes
                )
            )
        }
    }

    @Test
    fun `check Passwords authenticate request`() {
        runBlocking {
            val request = StytchRequests.Passwords.AuthenticateRequest(
                EMAIL,
                "123asd",
                46
            )
            try {
                apiService.authenticateWithPasswords(request)
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/passwords/authenticate",
                expectedBody = mapOf(
                    "email" to request.email,
                    "password" to request.password,
                    "session_duration_minutes" to request.sessionDurationMinutes
                )
            )
        }
    }

    // endregion Passwords

    // region Sessions

    @Test
    fun `check Sessions authenticate request`() {
        runBlocking {
            val request = StytchRequests.Sessions.AuthenticateRequest(24)
            try {
                apiService.authenticateSessions(request)
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/sessions/authenticate",
                expectedBody = mapOf("session_duration_minutes" to request.sessionDurationMinutes)
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
            mockWebServer.takeRequest().verifyPost(expectedPath = "/sessions/revoke")
        }
    }

    // endregion Sessions

    // region Biometrics
    @Test
    fun `check biometricsRegisterStart request`() {
        runBlocking {
            val request = StytchRequests.Biometrics.RegisterStartRequest(publicKey = "publicKey")
            try {
                apiService.biometricsRegisterStart(request)
            } catch (_: Exception) {}
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/biometrics/register/start",
                expectedBody = mapOf("public_key" to request.publicKey)
            )
        }
    }

    @Test
    fun `check biometricsRegister request`() {
        runBlocking {
            val request = StytchRequests.Biometrics.RegisterRequest(
                signature = "signature",
                biometricRegistrationId = "biometricRegistrationId",
                sessionDurationMinutes = 30
            )
            try {
                apiService.biometricsRegister(request)
            } catch (_: Exception) {}
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/biometrics/register",
                expectedBody = mapOf(
                    "signature" to request.signature,
                    "biometric_registration_id" to request.biometricRegistrationId,
                    "session_duration_minutes" to request.sessionDurationMinutes
                )
            )
        }
    }

    @Test
    fun `check biometricsAuthenticateStart request`() {
        runBlocking {
            val request = StytchRequests.Biometrics.AuthenticateStartRequest(publicKey = "publicKey")
            try {
                apiService.biometricsAuthenticateStart(request)
            } catch (_: Exception) {}
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/biometrics/authenticate/start",
                expectedBody = mapOf("public_key" to request.publicKey)
            )
        }
    }

    @Test
    fun `check biometricsAuthenticate request`() {
        runBlocking {
            val request = StytchRequests.Biometrics.AuthenticateRequest(
                signature = "signature",
                biometricRegistrationId = "biometricRegistrationId",
                sessionDurationMinutes = 30
            )
            try {
                apiService.biometricsAuthenticate(request)
            } catch (_: Exception) {}
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/biometrics/authenticate",
                expectedBody = mapOf(
                    "signature" to request.signature,
                    "biometric_registration_id" to request.biometricRegistrationId,
                    "session_duration_minutes" to request.sessionDurationMinutes
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
            val request = StytchRequests.OAuth.Google.AuthenticateRequest(
                idToken = "id_token",
                nonce = "nonce",
                sessionDurationMinutes = 30
            )
            try {
                apiService.authenticateWithGoogleIdToken(request)
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/oauth/google/id_token/authenticate",
                expectedBody = mapOf(
                    "id_token" to request.idToken,
                    "nonce" to request.nonce,
                    "session_duration_minutes" to request.sessionDurationMinutes
                )
            )
        }
    }

    @Test
    fun `check authenticateWithThirdPartyToken request`() {
        runBlocking {
            val request = StytchRequests.OAuth.ThirdParty.AuthenticateRequest(
                token = "id_token",
                sessionDurationMinutes = 30,
                codeVerifier = "code_challenge"
            )
            try {
                apiService.authenticateWithThirdPartyToken(request)
            } catch (_: Exception) {
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/oauth/authenticate",
                expectedBody = mapOf(
                    "token" to request.token,
                    "session_duration_minutes" to request.sessionDurationMinutes,
                    "code_verifier" to request.codeVerifier
                )
            )
        }
    }
    // endregion OAuth
}
