package com.stytch.sdk.consumer.network

import com.stytch.sdk.common.network.ApiService
import com.stytch.sdk.common.network.models.CommonRequests
import com.stytch.sdk.common.network.models.NameData
import com.stytch.sdk.consumer.network.models.ConsumerRequests
import com.stytch.sdk.utils.verifyDelete
import com.stytch.sdk.utils.verifyGet
import com.stytch.sdk.utils.verifyPost
import com.stytch.sdk.utils.verifyPut
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okio.EOFException
import org.junit.After
import org.junit.Before
import org.junit.Test

private const val EMAIL = "email@email.com"
private const val LOGIN_MAGIC_LINK = "loginMagicLink://"
private const val SIGNUP_MAGIC_LINK = "signupMagicLink://"

internal class StytchApiServiceTests {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: StytchApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start(12345)
        mockWebServer.enqueue(MockResponse().setResponseCode(200))
        apiService = ApiService.createApiService(
            mockWebServer.url("/").toString(),
            null,
            null,
            {},
            StytchApiService::class.java
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    // region MagicLinks

    @Test
    fun `check magic links email loginOrCreate request`() {
        runBlocking {
            val parameters = ConsumerRequests.MagicLinks.Email.LoginOrCreateUserRequest(
                email = EMAIL,
                loginMagicLinkUrl = LOGIN_MAGIC_LINK,
                codeChallenge = "123",
                codeChallengeMethod = "method2",
                loginTemplateId = "loginTemplateId",
                signupTemplateId = "signUpTemplateId",
            )
            requestIgnoringResponseException {
                apiService.loginOrCreateUserByEmail(parameters)
            }.verifyPost(
                expectedPath = "/magic_links/email/login_or_create",
                expectedBody = mapOf(
                    "email" to parameters.email,
                    "login_magic_link_url" to parameters.loginMagicLinkUrl,
                    "code_challenge" to parameters.codeChallenge,
                    "code_challenge_method" to parameters.codeChallengeMethod,
                    "login_template_id" to parameters.loginTemplateId,
                    "signup_template_id" to parameters.signupTemplateId,
                )
            )
        }
    }

    @Test
    fun `check magic links email send primary request`() {
        runBlocking {
            val parameters = ConsumerRequests.MagicLinks.SendRequest(
                email = EMAIL,
                loginMagicLinkUrl = LOGIN_MAGIC_LINK,
                codeChallenge = "123",
                loginTemplateId = "loginTemplateId",
                signupTemplateId = "signUpTemplateId",
                loginExpirationMinutes = 30,
                signupExpirationMinutes = 30,
                signupMagicLinkUrl = SIGNUP_MAGIC_LINK,
            )
            requestIgnoringResponseException {
                apiService.sendEmailMagicLinkPrimary(parameters)
            }.verifyPost(
                expectedPath = "/magic_links/email/send/primary",
                expectedBody = mapOf(
                    "email" to parameters.email,
                    "login_magic_link_url" to parameters.loginMagicLinkUrl,
                    "code_challenge" to parameters.codeChallenge,
                    "login_template_id" to parameters.loginTemplateId,
                    "signup_template_id" to parameters.signupTemplateId,
                    "login_expiration_minutes" to parameters.loginExpirationMinutes,
                    "signup_expiration_minutes" to parameters.loginExpirationMinutes,
                    "signup_magic_link_url" to parameters.signupMagicLinkUrl,
                )
            )
        }
    }

    @Test
    fun `check magic links email send secondary request`() {
        runBlocking {
            val parameters = ConsumerRequests.MagicLinks.SendRequest(
                email = EMAIL,
                loginMagicLinkUrl = LOGIN_MAGIC_LINK,
                codeChallenge = "123",
                loginTemplateId = "loginTemplateId",
                signupTemplateId = "signUpTemplateId",
                loginExpirationMinutes = 30,
                signupExpirationMinutes = 30,
                signupMagicLinkUrl = SIGNUP_MAGIC_LINK,
            )
            requestIgnoringResponseException {
                apiService.sendEmailMagicLinkSecondary(parameters)
            }.verifyPost(
                expectedPath = "/magic_links/email/send/secondary",
                expectedBody = mapOf(
                    "email" to parameters.email,
                    "login_magic_link_url" to parameters.loginMagicLinkUrl,
                    "code_challenge" to parameters.codeChallenge,
                    "login_template_id" to parameters.loginTemplateId,
                    "signup_template_id" to parameters.signupTemplateId,
                    "login_expiration_minutes" to parameters.loginExpirationMinutes,
                    "signup_expiration_minutes" to parameters.loginExpirationMinutes,
                    "signup_magic_link_url" to parameters.signupMagicLinkUrl,
                )
            )
        }
    }

    @Test
    fun `check magic links authenticate request`() {
        runBlocking {
            val parameters = ConsumerRequests.MagicLinks.AuthenticateRequest(
                token = "token",
                codeVerifier = "123",
                sessionDurationMinutes = 60
            )
            requestIgnoringResponseException {
                apiService.authenticate(parameters)
            }.verifyPost(
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
            val parameters = ConsumerRequests.OTP.Email(
                email = EMAIL,
                expirationMinutes = 60,
                loginTemplateId = "loginTemplateId",
                signupTemplateId = "signUpTemplateId",
            )
            requestIgnoringResponseException {
                apiService.loginOrCreateUserByOTPWithEmail(parameters)
            }.verifyPost(
                expectedPath = "/otps/email/login_or_create",
                expectedBody = mapOf(
                    "email" to parameters.email,
                    "expiration_minutes" to parameters.expirationMinutes,
                    "login_template_id" to parameters.loginTemplateId,
                    "signup_template_id" to parameters.signupTemplateId,
                )
            )
        }
    }

    @Test
    fun `check OTP email send primary with default expiration request`() {
        runBlocking {
            val parameters = ConsumerRequests.OTP.Email(
                email = EMAIL,
                expirationMinutes = 60,
                loginTemplateId = "loginTemplateId",
                signupTemplateId = "signUpTemplateId",
            )
            requestIgnoringResponseException {
                apiService.sendOTPWithEmailPrimary(parameters)
            }.verifyPost(
                expectedPath = "/otps/email/send/primary",
                expectedBody = mapOf(
                    "email" to parameters.email,
                    "expiration_minutes" to parameters.expirationMinutes,
                    "login_template_id" to parameters.loginTemplateId,
                    "signup_template_id" to parameters.signupTemplateId,
                )
            )
        }
    }

    @Test
    fun `check OTP email send secondary with default expiration request`() {
        runBlocking {
            val parameters = ConsumerRequests.OTP.Email(
                email = EMAIL,
                expirationMinutes = 60,
                loginTemplateId = "loginTemplateId",
                signupTemplateId = "signUpTemplateId",
            )
            requestIgnoringResponseException {
                apiService.sendOTPWithEmailSecondary(parameters)
            }.verifyPost(
                expectedPath = "/otps/email/send/secondary",
                expectedBody = mapOf(
                    "email" to parameters.email,
                    "expiration_minutes" to parameters.expirationMinutes,
                    "login_template_id" to parameters.loginTemplateId,
                    "signup_template_id" to parameters.signupTemplateId,
                )
            )
        }
    }

    @Test
    fun `check OTP sms loginOrCreate request`() {
        runBlocking {
            val parameters = ConsumerRequests.OTP.SMS(
                phoneNumber = "000",
                expirationMinutes = 24
            )
            requestIgnoringResponseException {
                apiService.loginOrCreateUserByOTPWithSMS(parameters)
            }.verifyPost(
                expectedPath = "/otps/sms/login_or_create",
                expectedBody = mapOf(
                    "phone_number" to parameters.phoneNumber,
                    "expiration_minutes" to parameters.expirationMinutes
                )
            )
        }
    }

    @Test
    fun `check OTP sms send primary request`() {
        runBlocking {
            val parameters = ConsumerRequests.OTP.SMS(
                phoneNumber = "000",
                expirationMinutes = 24
            )
            requestIgnoringResponseException {
                apiService.sendOTPWithSMSPrimary(parameters)
            }.verifyPost(
                expectedPath = "/otps/sms/send/primary",
                expectedBody = mapOf(
                    "phone_number" to parameters.phoneNumber,
                    "expiration_minutes" to parameters.expirationMinutes
                )
            )
        }
    }

    @Test
    fun `check OTP sms send secondary request`() {
        runBlocking {
            val parameters = ConsumerRequests.OTP.SMS(
                phoneNumber = "000",
                expirationMinutes = 24
            )
            requestIgnoringResponseException {
                apiService.sendOTPWithSMSSecondary(parameters)
            }.verifyPost(
                expectedPath = "/otps/sms/send/secondary",
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
            val parameters = ConsumerRequests.OTP.WhatsApp(
                phoneNumber = "000",
                expirationMinutes = 60
            )
            requestIgnoringResponseException {
                apiService.loginOrCreateUserByOTPWithWhatsApp(parameters)
            }.verifyPost(
                expectedPath = "/otps/whatsapp/login_or_create",
                expectedBody = mapOf(
                    "phone_number" to parameters.phoneNumber,
                    "expiration_minutes" to parameters.expirationMinutes
                )
            )
        }
    }

    @Test
    fun `check OTP whatsapp send primary with default expiration request`() {
        runBlocking {
            val parameters = ConsumerRequests.OTP.WhatsApp(
                phoneNumber = "000",
                expirationMinutes = 60
            )
            requestIgnoringResponseException {
                apiService.sendOTPWithWhatsAppPrimary(parameters)
            }.verifyPost(
                expectedPath = "/otps/whatsapp/send/primary",
                expectedBody = mapOf(
                    "phone_number" to parameters.phoneNumber,
                    "expiration_minutes" to parameters.expirationMinutes
                )
            )
        }
    }

    @Test
    fun `check OTP whatsapp send secondary with default expiration request`() {
        runBlocking {
            val parameters = ConsumerRequests.OTP.WhatsApp(
                phoneNumber = "000",
                expirationMinutes = 60
            )
            requestIgnoringResponseException {
                apiService.sendOTPWithWhatsAppSecondary(parameters)
            }.verifyPost(
                expectedPath = "/otps/whatsapp/send/secondary",
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
            val parameters = ConsumerRequests.OTP.Authenticate(
                token = "token",
                methodId = "methodId",
                sessionDurationMinutes = 60
            )
            requestIgnoringResponseException {
                apiService.authenticateWithOTP(parameters)
            }.verifyPost(
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
            val parameters = ConsumerRequests.Passwords.CreateRequest(
                email = EMAIL,
                password = "123asd",
                sessionDurationMinutes = 60
            )
            requestIgnoringResponseException {
                apiService.passwords(parameters)
            }.verifyPost(
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
    fun `check Passwords strengthCheck request`() {
        runBlocking {
            val parameters = ConsumerRequests.Passwords.StrengthCheckRequest(
                email = EMAIL,
                password = "123asd"
            )
            requestIgnoringResponseException {
                apiService.strengthCheck(parameters)
            }.verifyPost(
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
            val parameters = ConsumerRequests.Passwords.ResetByEmailRequest(
                token = "token",
                password = "123asd",
                sessionDurationMinutes = 60,
                codeVerifier = "ver1"
            )
            requestIgnoringResponseException {
                apiService.resetByEmail(parameters)
            }.verifyPost(
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
            val parameters = ConsumerRequests.Passwords.ResetByEmailStartRequest(
                email = EMAIL,
                codeChallenge = "123",
                codeChallengeMethod = "method2",
                loginRedirectUrl = "loginRedirect",
                loginExpirationMinutes = 24,
                resetPasswordRedirectUrl = "resetPasswordUrl",
                resetPasswordExpirationMinutes = 23,
                resetPasswordTemplateId = "resetPasswordTemplateId"
            )
            requestIgnoringResponseException {
                apiService.resetByEmailStart(parameters)
            }.verifyPost(
                expectedPath = "/passwords/email/reset/start",
                expectedBody = mapOf(
                    "email" to parameters.email,
                    "code_challenge" to parameters.codeChallenge,
                    "code_challenge_method" to parameters.codeChallengeMethod,
                    "login_redirect_url" to parameters.loginRedirectUrl,
                    "reset_password_redirect_url" to parameters.resetPasswordRedirectUrl,
                    "login_expiration_minutes" to parameters.loginExpirationMinutes,
                    "reset_password_expiration_minutes" to parameters.resetPasswordExpirationMinutes,
                    "reset_password_template_id" to parameters.resetPasswordTemplateId,
                )
            )
        }
    }

    @Test
    fun `check Passwords resetPasswordBySession request`() {
        runBlocking {
            val parameters = ConsumerRequests.Passwords.ResetBySessionRequest(
                password = "my-password",
                sessionDurationMinutes = 10
            )
            requestIgnoringResponseException {
                apiService.resetBySession(parameters)
            }.verifyPost(
                expectedPath = "/passwords/session/reset",
                expectedBody = mapOf(
                    "password" to parameters.password,
                    "session_duration_minutes" to parameters.sessionDurationMinutes,
                )
            )
        }
    }

    @Test
    fun `check Passwords authenticate request`() {
        runBlocking {
            val parameters = ConsumerRequests.Passwords.AuthenticateRequest(
                email = EMAIL,
                password = "123asd",
                sessionDurationMinutes = 46
            )
            requestIgnoringResponseException {
                apiService.authenticateWithPasswords(parameters)
            }.verifyPost(
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
            val parameters = CommonRequests.Sessions.AuthenticateRequest(sessionDurationMinutes = 24)
            requestIgnoringResponseException {
                apiService.authenticateSessions(parameters)
            }.verifyPost(
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
            }.verifyPost(expectedPath = "/sessions/revoke")
        }
    }

    // endregion Sessions

    // region Biometrics
    @Test
    fun `check biometricsRegisterStart request`() {
        runBlocking {
            val parameters = ConsumerRequests.Biometrics.RegisterStartRequest(publicKey = "publicKey")
            requestIgnoringResponseException {
                apiService.biometricsRegisterStart(parameters)
            }.verifyPost(
                expectedPath = "/biometrics/register/start",
                expectedBody = mapOf("public_key" to parameters.publicKey)
            )
        }
    }

    @Test
    fun `check biometricsRegister request`() {
        runBlocking {
            val parameters = ConsumerRequests.Biometrics.RegisterRequest(
                signature = "signature",
                biometricRegistrationId = "biometricRegistrationId",
                sessionDurationMinutes = 30
            )
            requestIgnoringResponseException {
                apiService.biometricsRegister(parameters)
            }.verifyPost(
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
            val parameters = ConsumerRequests.Biometrics.AuthenticateStartRequest(publicKey = "publicKey")
            requestIgnoringResponseException {
                apiService.biometricsAuthenticateStart(parameters)
            }.verifyPost(
                expectedPath = "/biometrics/authenticate/start",
                expectedBody = mapOf("public_key" to parameters.publicKey)
            )
        }
    }

    @Test
    fun `check biometricsAuthenticate request`() {
        runBlocking {
            val parameters = ConsumerRequests.Biometrics.AuthenticateRequest(
                signature = "signature",
                biometricRegistrationId = "biometricRegistrationId",
                sessionDurationMinutes = 30
            )
            requestIgnoringResponseException {
                apiService.biometricsAuthenticate(parameters)
            }.verifyPost(
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
            }.verifyGet("/users/me")
        }
    }

    @Test
    fun `check deleteEmailById request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.deleteEmailById(id = "email_id")
            }.verifyDelete("/users/emails/email_id")
        }
    }

    @Test
    fun `check deletePhoneNumberById request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.deletePhoneNumberById(id = "phone_number_id")
            }.verifyDelete("/users/phone_numbers/phone_number_id")
        }
    }

    @Test
    fun `check deleteBiometricRegistrationById request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.deleteBiometricRegistrationById(id = "biometrics_registration_id")
            }.verifyDelete("/users/biometric_registrations/biometrics_registration_id")
        }
    }

    @Test
    fun `check deleteCryptoWalletById request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.deleteCryptoWalletById(id = "crypto_wallet_id")
            }.verifyDelete("/users/crypto_wallets/crypto_wallet_id")
        }
    }

    @Test
    fun `check deleteWebAuthnById request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.deleteWebAuthnById(id = "webauthn_registration_id")
            }.verifyDelete("/users/webauthn_registrations/webauthn_registration_id")
        }
    }

    @Test
    fun `check updateUser request`() {
        val parameters = ConsumerRequests.User.UpdateRequest(
            name = NameData(firstName = "Test", middleName = "Tester", lastName = "Testington"),
            untrustedMetadata = mapOf("parameter 1" to "value 1")
        )
        runBlocking {
            requestIgnoringResponseException {
                apiService.updateUser(request = parameters)
            }.verifyPut(
                expectedPath = "/users/me",
                expectedBody = mapOf(
                    "name" to mapOf(
                        "first_name" to parameters.name?.firstName,
                        "middle_name" to parameters.name?.middleName,
                        "last_name" to parameters.name?.lastName
                    ),
                    "untrusted_metadata" to parameters.untrustedMetadata
                )
            )
        }
    }

    @Test
    fun `check searchUsers request`() {
        val parameters = ConsumerRequests.User.SearchRequest(
            email = "user@domain.com"
        )
        runBlocking {
            requestIgnoringResponseException {
                apiService.searchUsers(request = parameters)
            }.verifyPost(
                expectedPath = "/users/search",
                expectedBody = mapOf(
                    "email" to parameters.email
                )
            )
        }
    }
    // endregion UserManagement

    // region OAuth
    @Test
    fun `check authenticateWithGoogleIdToken request`() {
        runBlocking {
            val parameters = ConsumerRequests.OAuth.Google.AuthenticateRequest(
                idToken = "id_token",
                nonce = "nonce",
                sessionDurationMinutes = 30
            )
            requestIgnoringResponseException {
                apiService.authenticateWithGoogleIdToken(parameters)
            }.verifyPost(
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
            val parameters = ConsumerRequests.OAuth.ThirdParty.AuthenticateRequest(
                token = "id_token",
                sessionDurationMinutes = 30,
                codeVerifier = "code_challenge",
                sessionCustomClaims = mapOf(
                    "custom_claim_1" to "custom_claim_1_value",
                    "custom_claim_2" to "custom_claim_2_value",
                )
            )
            requestIgnoringResponseException {
                apiService.authenticateWithThirdPartyToken(parameters)
            }.verifyPost(
                expectedPath = "/oauth/authenticate",
                expectedBody = mapOf(
                    "token" to parameters.token,
                    "session_duration_minutes" to parameters.sessionDurationMinutes,
                    "code_verifier" to parameters.codeVerifier,
                    "session_custom_claims" to parameters.sessionCustomClaims
                )
            )
        }
    }
    // endregion OAuth

    // region Bootstrap
    @Test
    fun `check getBootstrapData request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.getBootstrapData("mock-public-token")
            }.verifyGet("/projects/bootstrap/mock-public-token")
        }
    }
    // endregion Bootstrap

    //region Webauthn
    @Test
    fun `check webAuthnRegisterStart request`() {
        val parameters = ConsumerRequests.WebAuthn.RegisterStartRequest(
            domain = "test.domain.com",
            userAgent = "My Test UserAgent",
            authenticatorType = "platform",
            isPasskey = true
        )
        runBlocking {
            requestIgnoringResponseException {
                apiService.webAuthnRegisterStart(parameters)
            }.verifyPost(
                expectedPath = "/webauthn/register/start",
                expectedBody = mapOf(
                    "domain" to parameters.domain,
                    "user_agent" to parameters.userAgent,
                    "authenticator_type" to parameters.authenticatorType,
                    "return_passkey_credential_options" to parameters.isPasskey,
                )
            )
        }
    }

    @Test
    fun `check webAuthnRegister request`() {
        val parameters = ConsumerRequests.WebAuthn.RegisterRequest(
            publicKeyCredential = "my-public-key-credential"
        )
        runBlocking {
            requestIgnoringResponseException {
                apiService.webAuthnRegister(parameters)
            }.verifyPost(
                expectedPath = "/webauthn/register",
                expectedBody = mapOf(
                    "public_key_credential" to parameters.publicKeyCredential,
                )
            )
        }
    }

    @Test
    fun `check webAuthnAuthenticateStartPrimary request`() {
        val parameters = ConsumerRequests.WebAuthn.AuthenticateStartRequest(
            domain = "test.domain.com",
            isPasskey = true,
        )
        runBlocking {
            requestIgnoringResponseException {
                apiService.webAuthnAuthenticateStartPrimary(parameters)
            }.verifyPost(
                expectedPath = "/webauthn/authenticate/start/primary",
                expectedBody = mapOf(
                    "domain" to parameters.domain,
                    "return_passkey_credential_options" to parameters.isPasskey,
                )
            )
        }
    }

    @Test
    fun `check webAuthnAuthenticateStartSecondary request`() {
        val parameters = ConsumerRequests.WebAuthn.AuthenticateStartRequest(
            domain = "test.domain.com",
            isPasskey = true,
        )
        runBlocking {
            requestIgnoringResponseException {
                apiService.webAuthnAuthenticateStartSecondary(parameters)
            }.verifyPost(
                expectedPath = "/webauthn/authenticate/start/secondary",
                expectedBody = mapOf(
                    "domain" to parameters.domain,
                    "return_passkey_credential_options" to parameters.isPasskey,
                )
            )
        }
    }

    @Test
    fun `check webAuthnAuthenticate request`() {
        val parameters = ConsumerRequests.WebAuthn.AuthenticateRequest(
            publicKeyCredential = "my-public-key-credential",
            sessionDurationMinutes = 30,
            sessionCustomClaims = mapOf("test" to true),
            sessionJwt = "my-session-jwt",
            sessionToken = "my-session-token",
        )
        runBlocking {
            requestIgnoringResponseException {
                apiService.webAuthnAuthenticate(parameters)
            }.verifyPost(
                expectedPath = "/webauthn/authenticate",
                expectedBody = mapOf(
                    "public_key_credential" to parameters.publicKeyCredential,
                    "session_duration_minutes" to parameters.sessionDurationMinutes,
                    "session_custom_claims" to parameters.sessionCustomClaims,
                    "session_jwt" to parameters.sessionJwt,
                    "session_token" to parameters.sessionToken,
                )
            )
        }
    }

    @Test
    fun `check webAuthnUpdate request`() {
        val id = "webauthn-registration-id"
        val parameters = ConsumerRequests.WebAuthn.UpdateRequest(
            name = "Cool Memorable Name",
        )
        runBlocking {
            requestIgnoringResponseException {
                apiService.webAuthnUpdate(id, parameters)
            }.verifyPut(
                expectedPath = "/webauthn/update/$id",
                expectedBody = mapOf(
                    "name" to parameters.name,
                )
            )
        }
    }
    //endregion

    //region Events
    @Test
    fun `check Events logEvent request`() {
        runBlocking {
            val parameters: CommonRequests.Events.Event = CommonRequests.Events.Event(
                telemetry = CommonRequests.Events.EventTelemetry(
                    eventId = "event-id",
                    appSessionId = "app-session-id",
                    persistentId = "persistent-id",
                    clientSentAt = "client sent at",
                    timezone = "timezone",
                    app = CommonRequests.Events.VersionIdentifier(
                        identifier = "app-id",
                        version = "app-version"
                    ),
                    os = CommonRequests.Events.VersionIdentifier(
                        identifier = "os-id",
                        version = "os-version"
                    ),
                    sdk = CommonRequests.Events.VersionIdentifier(
                        identifier = "sdk-id",
                        version = "sdk-version"
                    ),
                    device = CommonRequests.Events.DeviceIdentifier(
                        model = "device-model",
                        screenSize = "screen-size"
                    ),
                ),
                event = CommonRequests.Events.EventEvent(
                    publicToken = "public-token",
                    eventName = "event name",
                    details = mapOf("test-key" to "test value"),
                )
            )
            requestIgnoringResponseException {
                apiService.logEvent(parameters)
            }.verifyPost(
                expectedPath = "/events",
                expectedBody = mapOf(
                    "telemetry" to mapOf(
                        "event_id" to parameters.telemetry.eventId,
                        "app_session_id" to parameters.telemetry.appSessionId,
                        "persistent_id" to parameters.telemetry.persistentId,
                        "client_sent_at" to parameters.telemetry.clientSentAt,
                        "timezone" to parameters.telemetry.timezone,
                        "app" to mapOf(
                            "identifier" to parameters.telemetry.app.identifier,
                            "version" to parameters.telemetry.app.version
                        ),
                        "sdk" to mapOf(
                            "identifier" to parameters.telemetry.sdk.identifier,
                            "version" to parameters.telemetry.sdk.version
                        ),
                        "os" to mapOf(
                            "identifier" to parameters.telemetry.os.identifier,
                            "version" to parameters.telemetry.os.version
                        ),
                        "device" to mapOf(
                            "model" to parameters.telemetry.device.model,
                            "screen_size" to parameters.telemetry.device.screenSize,
                        )
                    ),
                    "event" to mapOf(
                        "public_token" to parameters.event.publicToken,
                        "event_name" to parameters.event.eventName,
                        "details" to parameters.event.details
                    )
                )
            )
        }
    }
    //endregion Events

    private suspend fun requestIgnoringResponseException(block: suspend () -> Unit): RecordedRequest {
        try {
            block()
        } catch (_: EOFException) {
            // OkHTTP throws EOFException because it expects a response body, but we're intentionally not creating them
        }
        return mockWebServer.takeRequest()
    }
}
