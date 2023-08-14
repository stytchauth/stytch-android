package com.stytch.sdk.b2b.network

import com.stytch.sdk.b2b.network.models.AllowedAuthMethods
import com.stytch.sdk.b2b.network.models.AuthMethods
import com.stytch.sdk.b2b.network.models.B2BRequests
import com.stytch.sdk.b2b.network.models.EmailInvites
import com.stytch.sdk.b2b.network.models.EmailJitProvisioning
import com.stytch.sdk.b2b.network.models.SsoJitProvisioning
import com.stytch.sdk.common.network.ApiService
import com.stytch.sdk.common.network.models.CommonRequests
import com.stytch.sdk.utils.verifyGet
import com.stytch.sdk.utils.verifyPost
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

internal class StytchB2BApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: StytchB2BApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start(12345)
        mockWebServer.enqueue(MockResponse().setResponseCode(200))
        apiService = ApiService.createApiService(
            mockWebServer.url("/").toString(),
            null,
            {},
            StytchB2BApiService::class.java
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
            val parameters = B2BRequests.MagicLinks.Email.LoginOrSignupRequest(
                email = EMAIL,
                organizationId = "organizationId",
                loginRedirectUrl = LOGIN_MAGIC_LINK,
                signupRedirectUrl = SIGNUP_MAGIC_LINK,
                codeChallenge = "123",
                loginTemplateId = "loginTemplateId",
                signupTemplateId = "signUpTemplateId",
            )
            requestIgnoringResponseException {
                apiService.loginOrSignupByEmail(parameters)
            }.verifyPost(
                expectedPath = "/b2b/magic_links/email/login_or_signup",
                expectedBody = mapOf(
                    "email_address" to parameters.email,
                    "organization_id" to parameters.organizationId,
                    "login_redirect_url" to parameters.loginRedirectUrl,
                    "signup_redirect_url" to parameters.signupRedirectUrl,
                    "pkce_code_challenge" to parameters.codeChallenge,
                    "login_template_id" to parameters.loginTemplateId,
                    "signup_template_id" to parameters.signupTemplateId,
                )
            )
        }
    }

    @Test
    fun `check magic links authenticate request`() {
        runBlocking {
            val parameters = B2BRequests.MagicLinks.AuthenticateRequest(
                token = "token",
                codeVerifier = "123",
                sessionDurationMinutes = 60
            )
            requestIgnoringResponseException {
                apiService.authenticate(parameters)
            }.verifyPost(
                expectedPath = "/b2b/magic_links/authenticate",
                expectedBody = mapOf(
                    "magic_links_token" to parameters.token,
                    "session_duration_minutes" to parameters.sessionDurationMinutes,
                    "pkce_code_verifier" to parameters.codeVerifier,
                )
            )
        }
    }

    @Test
    fun `check magic links discovery send request`() {
        runBlocking {
            val parameters = B2BRequests.MagicLinks.Discovery.SendRequest(
                email = "email@address.com",
                discoveryRedirectUrl = LOGIN_MAGIC_LINK,
                codeChallenge = "code-challenge",
                loginTemplateId = "login-template-id"
            )
            requestIgnoringResponseException {
                apiService.sendDiscoveryMagicLink(parameters)
            }.verifyPost(
                expectedPath = "/b2b/magic_links/email/discovery/send",
                expectedBody = mapOf(
                    "email_address" to parameters.email,
                    "discovery_redirect_url" to parameters.discoveryRedirectUrl,
                    "pkce_code_challenge" to parameters.codeChallenge,
                    "login_template_id" to parameters.loginTemplateId,
                )
            )
        }
    }

    @Test
    fun `check magic links discovery authenticate request`() {
        runBlocking {
            val parameters = B2BRequests.MagicLinks.Discovery.AuthenticateRequest(
                token = "token",
                codeVerifier = "123",
            )
            requestIgnoringResponseException {
                apiService.authenticateDiscoveryMagicLink(parameters)
            }.verifyPost(
                expectedPath = "/b2b/magic_links/discovery/authenticate",
                expectedBody = mapOf(
                    "discovery_magic_links_token" to parameters.token,
                    "pkce_code_verifier" to parameters.codeVerifier,
                )
            )
        }
    }

    // endregion MagicLinks

    // region Sessions

    @Test
    fun `check Sessions authenticate request`() {
        runBlocking {
            val parameters = CommonRequests.Sessions.AuthenticateRequest(sessionDurationMinutes = 24)
            requestIgnoringResponseException {
                apiService.authenticateSessions(parameters)
            }.verifyPost(
                expectedPath = "/b2b/sessions/authenticate",
                expectedBody = mapOf("session_duration_minutes" to parameters.sessionDurationMinutes)
            )
        }
    }

    @Test
    fun `check Sessions revoke request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.revokeSessions()
            }.verifyPost(expectedPath = "/b2b/sessions/revoke")
        }
    }

    // endregion Sessions

    // region Organizations

    @Test
    fun `check Organizations getOrganizationById request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.getOrganization()
            }.verifyGet("/b2b/organizations/me")
        }
    }

    @Test
    fun `check Organizations getMember request`() {
        runBlocking {
            requestIgnoringResponseException {
                apiService.getMember()
            }.verifyGet("/b2b/organizations/members/me")
        }
    }

    // endregion Organizations

    //region Passwords
    @Test
    fun `check Passwords authenticatePassword request`() {
        runBlocking {
            val parameters = B2BRequests.Passwords.AuthenticateRequest(
                organizationId = "my-organization-id",
                emailAddress = "my@email.address",
                password = "my-password",
                sessionDurationMinutes = 30
            )
            requestIgnoringResponseException {
                apiService.authenticatePassword(parameters)
            }.verifyPost(
                expectedPath = "/b2b/passwords/authenticate",
                expectedBody = mapOf(
                    "organization_id" to parameters.organizationId,
                    "email_address" to parameters.emailAddress,
                    "password" to parameters.password,
                    "session_duration_minutes" to parameters.sessionDurationMinutes,
                )
            )
        }
    }

    @Test
    fun `check Passwords resetPasswordByEmailStart request`() {
        runBlocking {
            val parameters = B2BRequests.Passwords.ResetByEmailStartRequest(
                organizationId = "my-organization-id",
                emailAddress = "my@email.address",
                codeChallenge = "code-challenge-string",
                loginRedirectUrl = "login://redirect",
                resetPasswordExpirationMinutes = 30,
                resetPasswordTemplateId = "reset-password-template-id",
                resetPasswordRedirectUrl = "reset://redirect",
            )
            requestIgnoringResponseException {
                apiService.resetPasswordByEmailStart(parameters)
            }.verifyPost(
                expectedPath = "/b2b/passwords/email/reset/start",
                expectedBody = mapOf(
                    "organization_id" to parameters.organizationId,
                    "email_address" to parameters.emailAddress,
                    "code_challenge" to parameters.codeChallenge,
                    "login_redirect_url" to parameters.loginRedirectUrl,
                    "reset_password_expiration_minutes" to parameters.resetPasswordExpirationMinutes,
                    "reset_password_template_id" to parameters.resetPasswordTemplateId,
                    "reset_password_redirect_url" to parameters.resetPasswordRedirectUrl,
                )
            )
        }
    }

    @Test
    fun `check Passwords resetPasswordByEmail request`() {
        runBlocking {
            val parameters = B2BRequests.Passwords.ResetByEmailRequest(
                passwordResetToken = "password-reset-token",
                password = "my-password",
                sessionDurationMinutes = 30,
                codeVerifier = "code-verifier"
            )
            requestIgnoringResponseException {
                apiService.resetPasswordByEmail(parameters)
            }.verifyPost(
                expectedPath = "/b2b/passwords/email/reset",
                expectedBody = mapOf(
                    "password_reset_token" to parameters.passwordResetToken,
                    "password" to parameters.password,
                    "session_duration_minutes" to parameters.sessionDurationMinutes,
                    "code_verifier" to parameters.codeVerifier,
                )
            )
        }
    }

    @Test
    fun `check Passwords resetPasswordByExisting request`() {
        runBlocking {
            val parameters = B2BRequests.Passwords.ResetByExistingPasswordRequest(
                organizationId = "my-organization-id",
                emailAddress = "my@email.address",
                existingPassword = "existing-password",
                newPassword = "new-password",
                sessionDurationMinutes = 30
            )
            requestIgnoringResponseException {
                apiService.resetPasswordByExisting(parameters)
            }.verifyPost(
                expectedPath = "/b2b/passwords/existing_password/reset",
                expectedBody = mapOf(
                    "organization_id" to parameters.organizationId,
                    "email_address" to parameters.emailAddress,
                    "existing_password" to parameters.existingPassword,
                    "new_password" to parameters.newPassword,
                    "session_duration_minutes" to parameters.sessionDurationMinutes,
                )
            )
        }
    }

    @Test
    fun `check Passwords resetPasswordBySession request`() {
        runBlocking {
            val parameters = B2BRequests.Passwords.ResetBySessionRequest(
                organizationId = "my-organization-id",
                password = "my-password",
            )
            requestIgnoringResponseException {
                apiService.resetPasswordBySession(parameters)
            }.verifyPost(
                expectedPath = "/b2b/passwords/session/reset",
                expectedBody = mapOf(
                    "organization_id" to parameters.organizationId,
                    "password" to parameters.password,
                )
            )
        }
    }

    @Test
    fun `check Passwords passwordStrengthCheck request`() {
        runBlocking {
            val parameters = B2BRequests.Passwords.StrengthCheckRequest(
                email = "my@email.address",
                password = "my-password",
            )
            requestIgnoringResponseException {
                apiService.passwordStrengthCheck(parameters)
            }.verifyPost(
                expectedPath = "/b2b/passwords/strength_check",
                expectedBody = mapOf(
                    "email" to parameters.email,
                    "password" to parameters.password,
                )
            )
        }
    }
    //endregion Passwords

    //region Discovery
    @Test
    fun `check Discovery discoverOrganizations request`() {
        runBlocking {
            val parameters = B2BRequests.Discovery.MembershipsRequest(
                intermediateSessionToken = "intermediate-session-token"
            )
            requestIgnoringResponseException {
                apiService.discoverOrganizations(parameters)
            }.verifyPost(
                expectedPath = "/b2b/discovery/organizations",
                expectedBody = mapOf(
                    "intermediate_session_token" to parameters.intermediateSessionToken
                )
            )
        }
    }

    @Test
    fun `check Discovery intermediateSessionExchange request`() {
        runBlocking {
            val parameters = B2BRequests.Discovery.SessionExchangeRequest(
                intermediateSessionToken = "intermediate-session-token",
                organizationId = "organization-id",
                sessionDurationMinutes = 30
            )
            requestIgnoringResponseException {
                apiService.intermediateSessionExchange(parameters)
            }.verifyPost(
                expectedPath = "/b2b/discovery/intermediate_sessions/exchange",
                expectedBody = mapOf(
                    "intermediate_session_token" to parameters.intermediateSessionToken,
                    "organization_id" to parameters.organizationId,
                    "session_duration_minutes" to parameters.sessionDurationMinutes,
                )
            )
        }
    }

    @Test
    fun `check Discovery createOrganization request`() {
        runBlocking {
            val parameters = B2BRequests.Discovery.CreateRequest(
                intermediateSessionToken = "intermediate-session-token",
                organizationName = "organization-name",
                organizationSlug = "organization-slug",
                organizationLogoUrl = "organization-logo-url",
                sessionDurationMinutes = 30,
                ssoJitProvisioning = SsoJitProvisioning.ALL_ALLOWED,
                emailAllowedDomains = listOf("alloweddomain.com"),
                emailInvites = EmailInvites.NOT_ALLOWED,
                emailJitProvisioning = EmailJitProvisioning.RESTRICTED,
                authMethods = AuthMethods.RESTRICTED,
                allowedAuthMethods = listOf(
                    AllowedAuthMethods.MAGIC_LINK,
                    AllowedAuthMethods.PASSWORD
                )
            )
            requestIgnoringResponseException {
                apiService.createOrganization(parameters)
            }.verifyPost(
                expectedPath = "/b2b/discovery/organizations/create",
                expectedBody = mapOf(
                    "intermediate_session_token" to parameters.intermediateSessionToken,
                    "organization_name" to parameters.organizationName,
                    "organization_slug" to parameters.organizationSlug,
                    "organization_logo_url" to parameters.organizationLogoUrl,
                    "session_duration_minutes" to parameters.sessionDurationMinutes,
                    "sso_jit_provisioning" to parameters.ssoJitProvisioning,
                    "email_allowed_domains" to parameters.emailAllowedDomains,
                    "email_jit_provisioning" to parameters.emailJitProvisioning,
                    "email_invites" to parameters.emailInvites,
                    "auth_methods" to parameters.authMethods,
                    "allowed_auth_methods" to parameters.allowedAuthMethods?.map { it.jsonName },
                )
            )
        }
    }
    //endregion Discovery

    //region SSO
    @Test
    fun `check SSO authenticate request`() {
        runBlocking {
            val parameters = B2BRequests.SSO.AuthenticateRequest(
                ssoToken = "sso-token",
                sessionDurationMinutes = 30,
                codeVerifier = "code-verifier"
            )
            requestIgnoringResponseException {
                apiService.ssoAuthenticate(parameters)
            }.verifyPost(
                expectedPath = "/b2b/sso/authenticate",
                expectedBody = mapOf(
                    "sso_token" to parameters.ssoToken,
                    "session_duration_minutes" to parameters.sessionDurationMinutes,
                    "pkce_code_verifier" to parameters.codeVerifier,
                )
            )
        }
    }
    //endregion

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

    private suspend fun requestIgnoringResponseException(block: suspend () -> Unit): RecordedRequest {
        try {
            block()
        } catch (_: EOFException) {
            // OkHTTP throws EOFException because it expects a response body, but we're intentionally not creating them
        }
        return mockWebServer.takeRequest()
    }
}
