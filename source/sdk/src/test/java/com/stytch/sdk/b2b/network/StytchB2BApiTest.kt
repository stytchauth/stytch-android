package com.stytch.sdk.b2b.network

import android.app.Application
import android.content.Context
import com.stytch.sdk.b2b.network.models.AllowedAuthMethods
import com.stytch.sdk.b2b.network.models.AuthMethods
import com.stytch.sdk.b2b.network.models.B2BRequests
import com.stytch.sdk.b2b.network.models.EmailInvites
import com.stytch.sdk.b2b.network.models.EmailJitProvisioning
import com.stytch.sdk.b2b.network.models.MfaMethod
import com.stytch.sdk.b2b.network.models.SsoJitProvisioning
import com.stytch.sdk.common.AppLifecycleListener
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.NetworkChangeListener
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchAPIError
import com.stytch.sdk.common.errors.StytchAPIErrorType
import com.stytch.sdk.common.network.InfoHeaderModel
import com.stytch.sdk.common.network.StytchDataResponse
import com.stytch.sdk.common.network.models.CommonRequests
import com.stytch.sdk.common.network.models.Locale
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkAll
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.security.KeyStore
import java.util.Date

internal class StytchB2BApiTest {
    var mContextMock = mockk<Context>(relaxed = true)

    @MockK
    private lateinit var mockB2BApiService: StytchB2BApiService

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
        mockkObject(NetworkChangeListener)
        every { NetworkChangeListener.configure(any(), any()) } just runs
        every { NetworkChangeListener.networkIsAvailable } returns true
        mockkObject(AppLifecycleListener)
        every { AppLifecycleListener.configure(any()) } just runs
        MockKAnnotations.init(this, true, true)
        every { EncryptionManager.createNewKeys(any()) } returns Unit
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        StytchB2BApi.apiService = mockB2BApiService
        StytchB2BApi.publicToken = ""
        StytchB2BApi.deviceInfo = mockDeviceInfo
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    // TODO every method calls safeApi

    @Test
    fun `StytchB2BApi MagicLinks Email loginOrCreate calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.loginOrSignupByEmail(any()) } returns mockk(relaxed = true)
            StytchB2BApi.MagicLinks.Email.loginOrSignupByEmail("", "", "", "", "", "", "", null)
            coVerify { mockB2BApiService.loginOrSignupByEmail(any()) }
        }

    @Test
    fun `StytchB2BApi MagicLinks Email authenticate calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.authenticate(any()) } returns mockk(relaxed = true)
            StytchB2BApi.MagicLinks.Email.authenticate("", 30, "")
            coVerify { mockB2BApiService.authenticate(any()) }
        }

    @Test
    fun `StytchB2BApi MagicLinks Email invite calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.sendInviteMagicLink(any()) } returns mockk(relaxed = true)
            StytchB2BApi.MagicLinks.Email.invite("email@address.com")
            coVerify { mockB2BApiService.sendInviteMagicLink(any()) }
        }

    @Test
    fun `StytchB2BApi MagicLinks Discovery send calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.sendDiscoveryMagicLink(any()) } returns mockk(relaxed = true)
            StytchB2BApi.MagicLinks.Discovery.send("", "", "", "", Locale.EN)
            coVerify { mockB2BApiService.sendDiscoveryMagicLink(any()) }
        }

    @Test
    fun `StytchB2BApi MagicLinks Discovery authenticate calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.authenticateDiscoveryMagicLink(any()) } returns mockk(relaxed = true)
            StytchB2BApi.MagicLinks.Discovery.authenticate("", "")
            coVerify { mockB2BApiService.authenticateDiscoveryMagicLink(any()) }
        }

    @Test
    fun `StytchB2BApi Sessions authenticate calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.authenticateSessions(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Sessions.authenticate(30)
            coVerify { mockB2BApiService.authenticateSessions(any()) }
        }

    @Test
    fun `StytchB2BApi Sessions revoke calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.revokeSessions() } returns mockk(relaxed = true)
            StytchB2BApi.Sessions.revoke()
            coVerify { mockB2BApiService.revokeSessions() }
        }

    @Test
    fun `StytchB2BApi Sessions exchange calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.exchangeSession(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Sessions.exchange(organizationId = "test-123", sessionDurationMinutes = 30)
            coVerify { mockB2BApiService.exchangeSession(any()) }
        }

    @Test
    fun `StytchApi Sessions attest calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.attestSession(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Sessions.attest("", "")
            coVerify { mockB2BApiService.attestSession(any()) }
        }

    @Test
    fun `StytchB2BApi Organizations getOrganization calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.getOrganization() } returns mockk(relaxed = true)
            StytchB2BApi.Organization.getOrganization()
            coVerify { mockB2BApiService.getOrganization() }
        }

    @Test
    fun `StytchB2BApi Organizations update calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.updateOrganization(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Organization.updateOrganization()
            coVerify { mockB2BApiService.updateOrganization(any()) }
        }

    @Test
    fun `StytchB2BApi Organizations delete calls appropriate apiService method()`() =
        runBlocking {
            coEvery { mockB2BApiService.deleteOrganization() } returns mockk(relaxed = true)
            StytchB2BApi.Organization.deleteOrganization()
            coVerify { mockB2BApiService.deleteOrganization() }
        }

    @Test
    fun `StytchB2BApi Organizations member delete calls appropriate apiService method()`() =
        runBlocking {
            coEvery { mockB2BApiService.deleteOrganizationMember(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Organization.deleteOrganizationMember("my-member-id")
            coVerify { mockB2BApiService.deleteOrganizationMember(any()) }
        }

    @Test
    fun `StytchB2BApi Organizations member reactivate calls appropriate apiService method()`() =
        runBlocking {
            coEvery { mockB2BApiService.reactivateOrganizationMember(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Organization.reactivateOrganizationMember("my-member-id")
            coVerify { mockB2BApiService.reactivateOrganizationMember(any()) }
        }

    @Test
    fun `StytchB2BApi Organization deleteOrganizationMemberMFAPhoneNumber calls appropriate apiService method`() =
        runBlocking {
            coEvery {
                StytchB2BApi.apiService.deleteOrganizationMemberMFAPhoneNumber(
                    any(),
                )
            } returns mockk(relaxed = true)
            StytchB2BApi.Organization.deleteOrganizationMemberMFAPhoneNumber("my-member-id")
            coVerify { mockB2BApiService.deleteOrganizationMemberMFAPhoneNumber("my-member-id") }
        }

    @Test
    fun `StytchB2BApi Organization deleteOrganizationMemberMFATOTP calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.deleteOrganizationMemberMFATOTP(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Organization.deleteOrganizationMemberMFATOTP("my-member-id")
            coVerify { mockB2BApiService.deleteOrganizationMemberMFATOTP("my-member-id") }
        }

    @Test
    fun `StytchB2BApi Organization deleteOrganizationMemberPassword calls appropriate apiService method`() =
        runBlocking {
            coEvery {
                StytchB2BApi.apiService.deleteOrganizationMemberPassword(
                    any(),
                )
            } returns mockk(relaxed = true)
            StytchB2BApi.Organization.deleteOrganizationMemberPassword("password-id")
            coVerify { mockB2BApiService.deleteOrganizationMemberPassword("password-id") }
        }

    @Test
    fun `StytchB2BApi Organization createOrganizationMember calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.createMember(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Organization.createOrganizationMember(
                emailAddress = "robot@stytch.com",
                name = "Stytch Robot",
                isBreakGlass = true,
                mfaEnrolled = true,
                mfaPhoneNumber = "+15551235555",
                untrustedMetadata = mapOf("key 1" to "value 1"),
                createMemberAsPending = true,
                roles = listOf("my-role", "my-other-role"),
            )
            coVerify { mockB2BApiService.createMember(any()) }
        }

    @Test
    fun `StytchB2BApi Organization updateOrganizationMember calls appropriate apiService method`() =
        runBlocking {
            coEvery {
                StytchB2BApi.apiService.updateOrganizationMember(
                    "my-member-id",
                    any(),
                )
            } returns mockk(relaxed = true)
            StytchB2BApi.Organization.updateOrganizationMember(
                memberId = "my-member-id",
                emailAddress = "robot@stytch.com",
                name = "Stytch Robot",
                isBreakGlass = true,
                mfaEnrolled = true,
                mfaPhoneNumber = "+15551235555",
                untrustedMetadata = mapOf("key 1" to "value 1"),
                roles = listOf("my-role", "my-other-role"),
                preserveExistingSessions = true,
                defaultMfaMethod = MfaMethod.SMS,
            )
            coVerify { mockB2BApiService.updateOrganizationMember("my-member-id", any()) }
        }

    @Test
    fun `StytchB2BApi Organization searchMembers calls appropriate apiService method`() =
        runBlocking {
            coEvery {
                StytchB2BApi.apiService.searchMembers(any())
            } returns mockk(relaxed = true)
            StytchB2BApi.Organization.search()
            coVerify { mockB2BApiService.searchMembers(any()) }
        }

    @Test
    fun `StytchB2BApi Member get calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.getMember() } returns mockk(relaxed = true)
            StytchB2BApi.Member.getMember()
            coVerify { mockB2BApiService.getMember() }
        }

    @Test
    fun `StytchB2BApi Member update calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.updateMember(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Member.updateMember("", emptyMap(), false, "", MfaMethod.SMS)
            coVerify { mockB2BApiService.updateMember(any()) }
        }

    @Test
    fun `StytchB2BApi Member deleteMFAPhoneNumber calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.deleteMFAPhoneNumber() } returns mockk(relaxed = true)
            StytchB2BApi.Member.deleteMFAPhoneNumber()
            coVerify { mockB2BApiService.deleteMFAPhoneNumber() }
        }

    @Test
    fun `StytchB2BApi Member deleteMFATOTP calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.deleteMFATOTP() } returns mockk(relaxed = true)
            StytchB2BApi.Member.deleteMFATOTP()
            coVerify { mockB2BApiService.deleteMFATOTP() }
        }

    @Test
    fun `StytchB2BApi Member deletePassword calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.deletePassword("passwordId") } returns mockk(relaxed = true)
            StytchB2BApi.Member.deletePassword("passwordId")
            coVerify { mockB2BApiService.deletePassword("passwordId") }
        }

    @Test
    fun `StytchB2BApi Passwords authenticate calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.authenticatePassword(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Passwords.authenticate("", "", "")
            coVerify { mockB2BApiService.authenticatePassword(any()) }
        }

    @Test
    fun `StytchB2BApi Passwords resetByEmailStart calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.resetPasswordByEmailStart(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Passwords.resetByEmailStart(
                organizationId = "",
                emailAddress = "",
                codeChallenge = "",
                loginRedirectUrl = null,
                resetPasswordRedirectUrl = null,
                resetPasswordExpirationMinutes = null,
                resetPasswordTemplateId = null,
                locale = null,
                verifyEmailTemplateId = null,
            )
            coVerify { mockB2BApiService.resetPasswordByEmailStart(any()) }
        }

    @Test
    fun `StytchB2BApi Passwords resetByEmail calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.resetPasswordByEmail(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Passwords.resetByEmail(passwordResetToken = "", password = "", codeVerifier = "")
            coVerify { mockB2BApiService.resetPasswordByEmail(any()) }
        }

    @Test
    fun `StytchB2BApi Passwords resetByExisting calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.resetPasswordByExisting(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Passwords.resetByExisting(
                organizationId = "",
                emailAddress = "",
                existingPassword = "",
                newPassword = "",
            )
            coVerify { mockB2BApiService.resetPasswordByExisting(any()) }
        }

    @Test
    fun `StytchB2BApi Passwords resetBySession calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.resetPasswordBySession(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Passwords.resetBySession(organizationId = "", password = "", locale = null)
            coVerify { mockB2BApiService.resetPasswordBySession(any()) }
        }

    @Test
    fun `StytchB2BApi Passwords strengthCheck calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.passwordStrengthCheck(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Passwords.strengthCheck(email = "", password = "")
            coVerify { mockB2BApiService.passwordStrengthCheck(any()) }
        }

    @Test
    fun `StytchB2BApi Passwords Discovery ResetByEmailStart calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.passwordDiscoveryResetByEmailStart(any()) } returns
                mockk(relaxed = true)
            StytchB2BApi.Passwords.Discovery.resetByEmailStart("", null, null, null, null, "", null, null)
            coVerify { mockB2BApiService.passwordDiscoveryResetByEmailStart(any()) }
        }

    @Test
    fun `StytchB2BApi Passwords Discovery ResetByEmail calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.passwordDiscoveryResetByEmail(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Passwords.Discovery.resetByEmail("", "", null, "", null)
            coVerify { mockB2BApiService.passwordDiscoveryResetByEmail(any()) }
        }

    @Test
    fun `StytchB2BApi Passwords Discovery Authenticate calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.passwordDiscoveryAuthenticate(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Passwords.Discovery.authenticate("", "")
            coVerify { mockB2BApiService.passwordDiscoveryAuthenticate(any()) }
        }

    @Test
    fun `StytchB2BApi Discovery discoverOrganizations calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.discoverOrganizations(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Discovery.discoverOrganizations(null)
            coVerify { mockB2BApiService.discoverOrganizations(any()) }
        }

    @Test
    fun `StytchB2BApi Discovery exchangeSession calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.intermediateSessionExchange(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Discovery.exchangeSession(
                intermediateSessionToken = "",
                organizationId = "",
                sessionDurationMinutes = 30,
            )
            coVerify { mockB2BApiService.intermediateSessionExchange(any()) }
        }

    @Test
    fun `StytchB2BApi Discovery createOrganization calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.createOrganization(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Discovery.createOrganization(
                intermediateSessionToken = "",
                organizationLogoUrl = "",
                organizationSlug = "",
                organizationName = "",
                sessionDurationMinutes = 30,
                ssoJitProvisioning = SsoJitProvisioning.ALL_ALLOWED,
                emailJitProvisioning = EmailJitProvisioning.RESTRICTED,
                emailInvites = EmailInvites.ALL_ALLOWED,
                emailAllowedDomains = listOf("allowed-domain.com"),
                authMethods = AuthMethods.RESTRICTED,
                allowedAuthMethods = listOf(AllowedAuthMethods.PASSWORD, AllowedAuthMethods.MAGIC_LINK),
            )
            val expectedParams =
                B2BRequests.Discovery.CreateRequest(
                    intermediateSessionToken = "",
                    organizationLogoUrl = "",
                    organizationSlug = "",
                    organizationName = "",
                    sessionDurationMinutes = 30,
                    ssoJitProvisioning = SsoJitProvisioning.ALL_ALLOWED,
                    emailJitProvisioning = EmailJitProvisioning.RESTRICTED,
                    emailInvites = EmailInvites.ALL_ALLOWED,
                    emailAllowedDomains = listOf("allowed-domain.com"),
                    authMethods = AuthMethods.RESTRICTED,
                    allowedAuthMethods = listOf(AllowedAuthMethods.PASSWORD, AllowedAuthMethods.MAGIC_LINK),
                )
            coVerify { mockB2BApiService.createOrganization(expectedParams) }
        }

    @Test
    fun `StytchB2BApi SSO authenticate calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.ssoAuthenticate(any()) } returns mockk(relaxed = true)
            StytchB2BApi.SSO.authenticate(
                ssoToken = "",
                sessionDurationMinutes = 30,
                codeVerifier = "",
            )
            coVerify { mockB2BApiService.ssoAuthenticate(any()) }
        }

    @Test
    fun `StytchB2BApi SSO getConnections calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.ssoGetConnections() } returns mockk(relaxed = true)
            StytchB2BApi.SSO.getConnections()
            coVerify { mockB2BApiService.ssoGetConnections() }
        }

    @Test
    fun `StytchB2BApi SSO deleteConnection calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.ssoDeleteConnection(any()) } returns mockk(relaxed = true)
            val connectionId = "my-connection-id"
            StytchB2BApi.SSO.deleteConnection(connectionId = connectionId)
            coVerify { mockB2BApiService.ssoDeleteConnection(connectionId) }
        }

    @Test
    fun `StytchB2BApi SSO SAML createConnection calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.ssoSamlCreate(any()) } returns mockk(relaxed = true)
            val displayName = "my cool saml connection"
            StytchB2BApi.SSO.samlCreateConnection(displayName = displayName)
            coVerify {
                StytchB2BApi.apiService.ssoSamlCreate(
                    B2BRequests.SSO.SAMLCreateRequest(displayName = displayName),
                )
            }
        }

    @Test
    fun `StytchB2BApi SSO SAML updateConnection calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.ssoSamlUpdate(any(), any()) } returns mockk(relaxed = true)
            val connectionId = "my-connection-id"
            StytchB2BApi.SSO.samlUpdateConnection(connectionId = connectionId)
            coVerify {
                StytchB2BApi.apiService.ssoSamlUpdate(connectionId = connectionId, any())
            }
        }

    @Test
    fun `StytchB2BApi SSO SAML updateConnectionByUrl calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.ssoSamlUpdateByUrl(any(), any()) } returns mockk(relaxed = true)
            val connectionId = "my-connection-id"
            val metadataUrl = "metadata.url"
            StytchB2BApi.SSO.samlUpdateByUrl(connectionId = connectionId, metadataUrl = metadataUrl)
            coVerify {
                StytchB2BApi.apiService.ssoSamlUpdateByUrl(connectionId = connectionId, any())
            }
        }

    @Test
    fun `StytchB2BApi SSO SAML samlDeleteVerificationCertificate calls appropriate apiService method`() =
        runBlocking {
            coEvery {
                StytchB2BApi.apiService.ssoSamlDeleteVerificationCertificate(
                    any(),
                    any(),
                )
            } returns mockk(relaxed = true)
            val connectionId = "my-connection-id"
            val certificateId = "mt-certificate-id"
            StytchB2BApi.SSO.samlDeleteVerificationCertificate(
                connectionId = connectionId,
                certificateId = certificateId,
            )
            coVerify {
                StytchB2BApi.apiService.ssoSamlDeleteVerificationCertificate(
                    connectionId = connectionId,
                    certificateId = certificateId,
                )
            }
        }

    @Test
    fun `StytchB2BApi SSO OIDC createConnection calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.ssoOidcCreate(any()) } returns mockk(relaxed = true)
            val displayName = "my cool oidc connection"
            StytchB2BApi.SSO.oidcCreateConnection(displayName = displayName)
            coVerify {
                StytchB2BApi.apiService.ssoOidcCreate(
                    B2BRequests.SSO.OIDCCreateRequest(displayName = displayName),
                )
            }
        }

    @Test
    fun `StytchB2BApi SSO OIDC updateConnection calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.ssoOidcUpdate(any(), any()) } returns mockk(relaxed = true)
            val connectionId = "my-cool-oidc-connection"
            StytchB2BApi.SSO.oidcUpdateConnection(connectionId = connectionId)
            coVerify {
                StytchB2BApi.apiService.ssoOidcUpdate(
                    connectionId = connectionId,
                    request = B2BRequests.SSO.OIDCUpdateRequest(connectionId = connectionId),
                )
            }
        }

    @Test
    fun `StytchB2BApi Bootstrap getBootstrapData calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.getBootstrapData(any()) } returns mockk(relaxed = true)
            StytchB2BApi.getBootstrapData()
            coVerify { mockB2BApiService.getBootstrapData(any()) }
        }

    @Test
    fun `StytchB2BApi Events logEvent calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.logEvent(any()) } returns mockk(relaxed = true)
            val details = mapOf("test-key" to "test value")
            val header = InfoHeaderModel.fromDeviceInfo(mockDeviceInfo)
            val now = Date()
            StytchB2BApi.Events.logEvent(
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
                mockB2BApiService.logEvent(
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
    fun `StytchB2BApi OTP sendSMSOTP calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.sendSMSOTP(any()) } returns mockk(relaxed = true)
            StytchB2BApi.OTP.sendSMSOTP("", "")
            coVerify { mockB2BApiService.sendSMSOTP(any()) }
        }

    @Test
    fun `StytchB2BApi OTP authenticateSMSOTP calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.authenticateSMSOTP(any()) } returns mockk(relaxed = true)
            StytchB2BApi.OTP.authenticateSMSOTP("", "", "", null, 30)
            coVerify { mockB2BApiService.authenticateSMSOTP(any()) }
        }

    @Test
    fun `StytchB2BApi TOTP create calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.createTOTP(any()) } returns mockk(relaxed = true)
            StytchB2BApi.TOTP.create("", "")
            coVerify { mockB2BApiService.createTOTP(any()) }
        }

    @Test
    fun `StytchB2BApi TOTP authenticate calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.authenticateTOTP(any()) } returns mockk(relaxed = true)
            StytchB2BApi.TOTP.authenticate("", "", "", null, null, 30)
            coVerify { mockB2BApiService.authenticateTOTP(any()) }
        }

    @Test
    fun `StytchB2BApi RecoveryCodes get calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.getRecoveryCodes() } returns mockk(relaxed = true)
            StytchB2BApi.RecoveryCodes.get()
            coVerify { mockB2BApiService.getRecoveryCodes() }
        }

    @Test
    fun `StytchB2BApi RecoveryCodes rotate calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.rotateRecoveryCodes() } returns mockk(relaxed = true)
            StytchB2BApi.RecoveryCodes.rotate()
            coVerify { mockB2BApiService.rotateRecoveryCodes() }
        }

    @Test
    fun `StytchB2BApi RecoveryCodes recover calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.recoverRecoveryCodes(any()) } returns mockk(relaxed = true)
            StytchB2BApi.RecoveryCodes.recover("", "", 30, "")
            coVerify { mockB2BApiService.recoverRecoveryCodes(any()) }
        }

    @Test
    fun `StytchB2BApi OAuth authenticate calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.oauthAuthenticate(any()) } returns mockk(relaxed = true)
            StytchB2BApi.OAuth.authenticate("", Locale.EN, 30, "", "")
            coVerify { mockB2BApiService.oauthAuthenticate(any()) }
        }

    @Test
    fun `StytchB2BApi OAuth discoveryAuthenticate calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.oauthDiscoveryAuthenticate(any()) } returns mockk(relaxed = true)
            StytchB2BApi.OAuth.discoveryAuthenticate("", "")
            coVerify { mockB2BApiService.oauthDiscoveryAuthenticate(any()) }
        }

    @Test
    fun `StytchB2BApi SearchManager searchOrganizations calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.searchOrganizations(any()) } returns mockk(relaxed = true)
            StytchB2BApi.SearchManager.searchOrganizations("organization-slug")
            coVerify { mockB2BApiService.searchOrganizations(any()) }
        }

    @Test
    fun `StytchB2BApi SearchManager searchOrganizationMembers calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.searchOrganizationMembers(any()) } returns mockk(relaxed = true)
            StytchB2BApi.SearchManager.searchMembers("email@example.com", "organization-id")
            coVerify { mockB2BApiService.searchOrganizationMembers(any()) }
        }

    @Test
    fun `StytchB2BApi SCIM createConnection calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.scimCreateConnection(any()) } returns mockk(relaxed = true)
            StytchB2BApi.SCIM.createConnection("", "")
            coVerify { mockB2BApiService.scimCreateConnection(any()) }
        }

    @Test
    fun `StytchB2BApi SCIM updateConnection calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.scimUpdateConnection(any(), any()) } returns mockk(relaxed = true)
            StytchB2BApi.SCIM.updateConnection("connection-id", "", "", emptyList())
            coVerify { mockB2BApiService.scimUpdateConnection(eq("connection-id"), any()) }
        }

    @Test
    fun `StytchB2BApi SCIM deleteConection calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.scimDeleteConnection(any()) } returns mockk(relaxed = true)
            StytchB2BApi.SCIM.deleteConection("connection-id")
            coVerify { mockB2BApiService.scimDeleteConnection(eq("connection-id")) }
        }

    @Test
    fun `StytchB2BApi SCIM getConnection calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.scimGetConnection() } returns mockk(relaxed = true)
            StytchB2BApi.SCIM.getConnection()
            coVerify { mockB2BApiService.scimGetConnection() }
        }

    @Test
    fun `StytchB2BApi SCIM getConnectionGroups calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.scimGetConnectionGroups(any()) } returns mockk(relaxed = true)
            StytchB2BApi.SCIM.getConnectionGroups("", 1000)
            coVerify { mockB2BApiService.scimGetConnectionGroups(any()) }
        }

    @Test
    fun `StytchB2BApi SCIM rotateStart calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.scimRotateStart(any()) } returns mockk(relaxed = true)
            StytchB2BApi.SCIM.rotateStart("connection-id")
            coVerify { mockB2BApiService.scimRotateStart(any()) }
        }

    @Test
    fun `StytchB2BApi SCIM rotateCancel calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.scimRotateCancel(any()) } returns mockk(relaxed = true)
            StytchB2BApi.SCIM.rotateCancel("connection-id")
            coVerify { mockB2BApiService.scimRotateCancel(any()) }
        }

    @Test
    fun `StytchB2BApi SCIM rotateComplete calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.scimRotateComplete(any()) } returns mockk(relaxed = true)
            StytchB2BApi.SCIM.rotateComplete("connection-id")
            coVerify { mockB2BApiService.scimRotateComplete(any()) }
        }

    @Test
    fun `StytchB2BApi OTP Email otpEmailLoginOrSignup calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.otpEmailLoginOrSignup(any()) } returns mockk(relaxed = true)
            StytchB2BApi.OTP.otpEmailLoginOrSignup("", "", null, null, null)
            coVerify { mockB2BApiService.otpEmailLoginOrSignup(any()) }
        }

    @Test
    fun `StytchB2BApi OTP Email otpEmailAuthenticate calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.otpEmailAuthenticate(any()) } returns mockk(relaxed = true)
            StytchB2BApi.OTP.otpEmailAuthenticate("", "", "", null, 30)
            coVerify { mockB2BApiService.otpEmailAuthenticate(any()) }
        }

    @Test
    fun `StytchB2BApi OTP Email otpEmailDiscoverySend calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.otpEmailDiscoverySend(any()) } returns mockk(relaxed = true)
            StytchB2BApi.OTP.otpEmailDiscoverySend("", null, null)
            coVerify { mockB2BApiService.otpEmailDiscoverySend(any()) }
        }

    @Test
    fun `StytchB2BApi OTP Email otpEmailDiscoveryAuthenticate calls appropriate apiService method`() =
        runBlocking {
            coEvery { mockB2BApiService.otpEmailDiscoveryAuthenticate(any()) } returns mockk(relaxed = true)
            StytchB2BApi.OTP.otpEmailDiscoveryAuthenticate("", "")
            coVerify { mockB2BApiService.otpEmailDiscoveryAuthenticate(any()) }
        }

    @Test
    fun `safeApiCall returns success when call succeeds`() =
        runBlocking {
            fun mockApiCall(): StytchDataResponse<Boolean> = StytchDataResponse(true)
            val result = StytchB2BApi.safeB2BApiCall { mockApiCall() }
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
            val result = StytchB2BApi.safeB2BApiCall { mockApiCall() }
            assert(result is StytchResult.Error)
        }

    @Test
    fun `safeApiCall returns correct error for StytchErrors`() =
        runBlocking {
            fun mockApiCall(): StytchDataResponse<Boolean> =
                throw StytchAPIError(errorType = StytchAPIErrorType.UNKNOWN_ERROR, message = "", statusCode = 400)
            val result = StytchB2BApi.safeB2BApiCall { mockApiCall() }
            assert(result is StytchResult.Error)
        }

    @Test
    fun `safeApiCall returns correct error for other exceptions`() =
        runBlocking {
            fun mockApiCall(): StytchDataResponse<Boolean> {
                error("Test")
            }
            try {
                val result = StytchB2BApi.safeB2BApiCall { mockApiCall() }
                assert(result is StytchResult.Error)
            } catch (_: Exception) {
                fail("safeApiCall should not throw an exception")
            }
        }
}
