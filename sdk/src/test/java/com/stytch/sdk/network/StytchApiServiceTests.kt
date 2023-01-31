package com.stytch.sdk.network

import com.stytch.sdk.utils.verifyDelete
import com.stytch.sdk.utils.verifyGet
import com.stytch.sdk.utils.verifyPost
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.EOFException
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
            val parameters = StytchRequests.MagicLinks.Email.LoginOrCreateUserRequest(
                EMAIL,
                LOGIN_MAGIC_LINK,
                "123",
                "method2"
            )
            requestIgnoringResponseException {
                apiService.loginOrCreateUserByEmail(parameters)
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/magic_links/email/login_or_create",
                expectedBody = mapOf(
                    "email" to parameters.email,
                    "login_magic_link_url" to parameters.loginMagicLinkUrl,
                    "code_challenge" to parameters.codeChallenge,
                    "code_challenge_method" to parameters.codeChallengeMethod
                )
            )
        }
    }

    @Test
    fun `check magic links authenticate request`() {
        runBlocking {
            val parameters = StytchRequests.MagicLinks.AuthenticateRequest(
                "token",
                "123",
                60
            )
            requestIgnoringResponseException {
                apiService.authenticate(parameters)
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/magic_links/authenticate",
                expectedBody = mapOf(
                    "token" to parameters.token,
                    "session_duration_minutes" to parameters.sessionDurationMinutes,
                    "code_verifier" to parameters.codeVerifier,
                )
            )
        }
    }

    // endregion MagicLinks

    // region OTP
    @Test
    fun `check OTP email loginOrCreate with default expiration request`() {
        runBlocking {
            val parameters = StytchRequests.OTP.Email(
                EMAIL,
                60
            )
            requestIgnoringResponseException {
                apiService.loginOrCreateUserByOTPWithEmail(parameters)
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/otps/email/login_or_create",
                expectedBody = mapOf(
                    "email" to parameters.email,
                    "expiration_minutes" to parameters.expirationMinutes
                )
            )
        }
    }

    @Test
    fun `check OTP sms loginOrCreate request`() {
        runBlocking {
            val parameters = StytchRequests.OTP.SMS(
                "000",
                24
            )
            requestIgnoringResponseException {
                apiService.loginOrCreateUserByOTPWithSMS(parameters)
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/otps/sms/login_or_create",
                expectedBody = mapOf(
                    "phone_number" to parameters.phoneNumber,
                    "expiration_minutes" to parameters.expirationMinutes
                )
            )
        }
    }

    @Test
    fun `check OTP whatsapp loginOrCreate with default expiration request`() {
        runBlocking {
            val parameters = StytchRequests.OTP.WhatsApp(
                "000",
                60
            )
            requestIgnoringResponseException {
                apiService.loginOrCreateUserByOTPWithWhatsApp(parameters)
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/otps/whatsapp/login_or_create",
                expectedBody = mapOf(
                    "phone_number" to parameters.phoneNumber,
                    "expiration_minutes" to parameters.expirationMinutes
                )
            )
        }
    }

    @Test
    fun `check OTP authenticate request`() {
        runBlocking {
            val parameters = StytchRequests.OTP.Authenticate(
                "token",
                "methodId",
                60
            )
            requestIgnoringResponseException {
                apiService.authenticateWithOTP(parameters)
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/otps/authenticate",
                expectedBody = mapOf(
                    "token" to parameters.token,
                    "method_id" to parameters.methodId,
                    "session_duration_minutes" to parameters.sessionDurationMinutes,
                )
            )
        }
    }

    // endregion OTP

    // region Passwords

    @Test
    fun `check Passwords create request`() {
        runBlocking {
            val parameters = StytchRequests.Passwords.CreateRequest(
                EMAIL,
                "123asd",
                60
            )
            requestIgnoringResponseException {
                apiService.passwords(parameters)
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/passwords",
                expectedBody = mapOf(
                    "email" to parameters.email,
                    "password" to parameters.password,
                    "session_duration_minutes" to parameters.sessionDurationMinutes
                )
            )
        }
    }

    @Test
    fun `check Passwords strenghtCheck request`() {
        runBlocking {
            val parameters = StytchRequests.Passwords.StrengthCheckRequest(
                EMAIL,
                "123asd"
            )
            requestIgnoringResponseException {
                apiService.strengthCheck(parameters)
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/passwords/strength_check",
                expectedBody = mapOf(
                    "email" to parameters.email,
                    "password" to parameters.password
                )
            )
        }
    }

    @Test
    fun `check Passwords resetbyEmail request`() {
        runBlocking {
            val parameters = StytchRequests.Passwords.ResetByEmailRequest(
                "token",
                "123asd",
                60,
                "ver1"
            )
            requestIgnoringResponseException {
                apiService.resetByEmail(parameters)
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/passwords/email/reset",
                expectedBody = mapOf(
                    "token" to parameters.token,
                    "password" to parameters.password,
                    "session_duration_minutes" to parameters.sessionDurationMinutes,
                    "code_verifier" to parameters.codeVerifier
                )
            )
        }
    }

    @Test
    fun `check Passwords resetbyEmailStart request`() {
        runBlocking {
            val parameters = StytchRequests.Passwords.ResetByEmailStartRequest(
                EMAIL,
                "123",
                "method2",
                "loginRedirect",
                24,
                "resetPasswordUrl",
                23
            )
            requestIgnoringResponseException {
                apiService.resetByEmailStart(parameters)
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/passwords/email/reset/start",
                expectedBody = mapOf(
                    "email" to parameters.email,
                    "code_challenge" to parameters.codeChallenge,
                    "code_challenge_method" to parameters.codeChallengeMethod,
                    "login_redirect_url" to parameters.loginRedirectUrl,
                    "reset_password_redirect_url" to parameters.resetPasswordRedirectUrl,
                    "login_expiration_minutes" to parameters.loginExpirationMinutes,
                    "reset_password_expiration_minutes" to parameters.resetPasswordExpirationMinutes
                )
            )
        }
    }

    @Test
    fun `check Passwords authenticate request`() {
        runBlocking {
            val parameters = StytchRequests.Passwords.AuthenticateRequest(
                EMAIL,
                "123asd",
                46
            )
            requestIgnoringResponseException {
                apiService.authenticateWithPasswords(parameters)
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/passwords/authenticate",
                expectedBody = mapOf(
                    "email" to parameters.email,
                    "password" to parameters.password,
                    "session_duration_minutes" to parameters.sessionDurationMinutes
                )
            )
        }
    }

    // endregion Passwords

    // region Sessions

    @Test
    fun `check Sessions authenticate request`() {
        runBlocking {
            val parameters = StytchRequests.Sessions.AuthenticateRequest(24)
            requestIgnoringResponseException {
                apiService.authenticateSessions(parameters)
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/sessions/authenticate",
                expectedBody = mapOf("session_duration_minutes" to parameters.sessionDurationMinutes)
            )
        }
    }

    @Test
    fun `check Sessions revoke request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.revokeSessions()
            }
            mockWebServer.takeRequest().verifyPost(expectedPath = "/sessions/revoke")
        }
    }

    // endregion Sessions

    // region Biometrics
    @Test
    fun `check biometricsRegisterStart request`() {
        runBlocking {
            val parameters = StytchRequests.Biometrics.RegisterStartRequest(publicKey = "publicKey")
            requestIgnoringResponseException {
                apiService.biometricsRegisterStart(parameters)
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/biometrics/register/start",
                expectedBody = mapOf("public_key" to parameters.publicKey)
            )
        }
    }

    @Test
    fun `check biometricsRegister request`() {
        runBlocking {
            val parameters = StytchRequests.Biometrics.RegisterRequest(
                signature = "signature",
                biometricRegistrationId = "biometricRegistrationId",
                sessionDurationMinutes = 30
            )
            requestIgnoringResponseException {
                apiService.biometricsRegister(parameters)
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/biometrics/register",
                expectedBody = mapOf(
                    "signature" to parameters.signature,
                    "biometric_registration_id" to parameters.biometricRegistrationId,
                    "session_duration_minutes" to parameters.sessionDurationMinutes
                )
            )
        }
    }

    @Test
    fun `check biometricsAuthenticateStart request`() {
        runBlocking {
            val parameters = StytchRequests.Biometrics.AuthenticateStartRequest(publicKey = "publicKey")
            requestIgnoringResponseException {
                apiService.biometricsAuthenticateStart(parameters)
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/biometrics/authenticate/start",
                expectedBody = mapOf("public_key" to parameters.publicKey)
            )
        }
    }

    @Test
    fun `check biometricsAuthenticate request`() {
        runBlocking {
            val parameters = StytchRequests.Biometrics.AuthenticateRequest(
                signature = "signature",
                biometricRegistrationId = "biometricRegistrationId",
                sessionDurationMinutes = 30
            )
            requestIgnoringResponseException {
                apiService.biometricsAuthenticate(parameters)
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/biometrics/authenticate",
                expectedBody = mapOf(
                    "signature" to parameters.signature,
                    "biometric_registration_id" to parameters.biometricRegistrationId,
                    "session_duration_minutes" to parameters.sessionDurationMinutes
                )
            )
        }
    }
    // endregion Biometrics

    // region UserManagement
    @Test
    fun `check getUser request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.getUser()
            }
            mockWebServer.takeRequest().verifyGet("/users/me")
        }
    }

    @Test
    fun `check deleteEmailById request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.deleteEmailById("email_id")
            }
            mockWebServer.takeRequest().verifyDelete("/users/emails/email_id")
        }
    }

    @Test
    fun `check deletePhoneNumberById request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.deletePhoneNumberById("phone_number_id")
            }
            mockWebServer.takeRequest().verifyDelete("/users/phone_numbers/phone_number_id")
        }
    }

    @Test
    fun `check deleteBiometricRegistrationById request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.deleteBiometricRegistrationById("biometrics_registration_id")
            }
            mockWebServer.takeRequest().verifyDelete("/users/biometric_registrations/biometrics_registration_id")
        }
    }

    @Test
    fun `check deleteCryptoWalletById request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.deleteCryptoWalletById("crypto_wallet_id")
            }
            mockWebServer.takeRequest().verifyDelete("/users/crypto_wallets/crypto_wallet_id")
        }
    }

    @Test
    fun `check deleteWebAuthnById request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.deleteWebAuthnById("webauthn_registration_id")
            }
            mockWebServer.takeRequest().verifyDelete("/users/webauthn_registrations/webauthn_registration_id")
        }
    }
    // endregion UserManagement

    // region OAuth
    @Test
    fun `check authenticateWithGoogleIdToken request`() {
        runBlocking {
            val parameters = StytchRequests.OAuth.Google.AuthenticateRequest(
                idToken = "id_token",
                nonce = "nonce",
                sessionDurationMinutes = 30
            )
            requestIgnoringResponseException {
                apiService.authenticateWithGoogleIdToken(parameters)
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/oauth/google/id_token/authenticate",
                expectedBody = mapOf(
                    "id_token" to parameters.idToken,
                    "nonce" to parameters.nonce,
                    "session_duration_minutes" to parameters.sessionDurationMinutes
                )
            )
        }
    }

    @Test
    fun `check authenticateWithThirdPartyToken request`() {
        runBlocking {
            val parameters = StytchRequests.OAuth.ThirdParty.AuthenticateRequest(
                token = "id_token",
                sessionDurationMinutes = 30,
                codeVerifier = "code_challenge"
            )
            requestIgnoringResponseException {
                apiService.authenticateWithThirdPartyToken(parameters)
            }
            mockWebServer.takeRequest().verifyPost(
                expectedPath = "/oauth/authenticate",
                expectedBody = mapOf(
                    "token" to parameters.token,
                    "session_duration_minutes" to parameters.sessionDurationMinutes,
                    "code_verifier" to parameters.codeVerifier
                )
            )
        }
    }
    // endregion OAuth

    private suspend fun requestIgnoringResponseException(block: suspend () -> Unit) = try {
        block()
    } catch (_: EOFException) {
        // OkHTTP throws EOFException because it expects a response body, but we're intentionally not creating them
    }
}
