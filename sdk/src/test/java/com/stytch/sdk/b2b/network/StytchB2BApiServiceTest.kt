package com.stytch.sdk.b2b.network

import com.stytch.sdk.common.network.ApiService
import com.stytch.sdk.common.network.CommonRequests
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
            val parameters = B2BRequests.MagicLinks.Email.LoginOrCreateUserRequest(
                email = EMAIL,
                organizationId = "organizationId",
                loginRedirectUrl = LOGIN_MAGIC_LINK,
                signupRedirectUrl = SIGNUP_MAGIC_LINK,
                codeChallenge = "123",
                loginTemplateId = "loginTemplateId",
                signupTemplateId = "signUpTemplateId",
            )
            requestIgnoringResponseException {
                apiService.loginOrCreateUserByEmail(parameters)
            }.verifyPost(
                expectedPath = "/b2b/magic_links/email/login_or_signup",
                expectedBody = mapOf(
                    "email" to parameters.email,
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
                apiService.getOrganizationById("organizationId")
            }.verifyGet("/b2b/organizations/organizationId")
        }
    }

    // endregion Organizations

    private suspend fun requestIgnoringResponseException(block: suspend () -> Unit): RecordedRequest {
        try {
            block()
        } catch (_: EOFException) {
            // OkHTTP throws EOFException because it expects a response body, but we're intentionally not creating them
        }
        return mockWebServer.takeRequest()
    }
}
