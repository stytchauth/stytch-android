package com.stytch.sdk.b2b.network

import android.app.Application
import android.content.Context
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.AllowedAuthMethods
import com.stytch.sdk.b2b.network.models.AuthMethods
import com.stytch.sdk.b2b.network.models.B2BRequests
import com.stytch.sdk.b2b.network.models.EmailInvites
import com.stytch.sdk.b2b.network.models.EmailJitProvisioning
import com.stytch.sdk.b2b.network.models.MfaMethod
import com.stytch.sdk.b2b.network.models.SsoJitProvisioning
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.AppLifecycleListener
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.EndpointOptions
import com.stytch.sdk.common.NetworkChangeListener
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchAPIError
import com.stytch.sdk.common.errors.StytchAPIErrorType
import com.stytch.sdk.common.errors.StytchSDKNotConfiguredError
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
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.security.KeyStore

internal class StytchB2BApiTest {
    var mContextMock = mockk<Context>(relaxed = true)

    @MockK
    private lateinit var mockB2BSessionStorage: B2BSessionStorage

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
        mockkObject(StytchB2BApi)
        mockkObject(NetworkChangeListener)
        every { NetworkChangeListener.configure(any(), any()) } just runs
        every { NetworkChangeListener.networkIsAvailable } returns true
        mockkObject(AppLifecycleListener)
        every { AppLifecycleListener.configure(any()) } just runs
        MockKAnnotations.init(this, true, true)
        every { EncryptionManager.createNewKeys(any()) } returns Unit
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        StytchB2BClient.sessionStorage = mockB2BSessionStorage
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `StytchB2BApi isInitialized returns correctly based on configuration state`() {
        StytchB2BApi.configure("publicToken", DeviceInfo(), EndpointOptions(), { null }, mockk())
        assert(StytchB2BApi.isInitialized)
    }

    @Test
    fun `StytchB2BApi apiService is available when configured`() {
        StytchB2BClient.configure(mContextMock, "")
        StytchB2BApi.apiService
    }

    // TODO every method calls safeApi

    @Test
    fun `StytchB2BApi MagicLinks Email loginOrCreate calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.loginOrSignupByEmail(any()) } returns mockk(relaxed = true)
            StytchB2BApi.MagicLinks.Email.loginOrSignupByEmail("", "", "", "", "", "", "", null)
            coVerify { StytchB2BApi.apiService.loginOrSignupByEmail(any()) }
        }

    @Test
    fun `StytchB2BApi MagicLinks Email authenticate calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.authenticate(any()) } returns mockk(relaxed = true)
            StytchB2BApi.MagicLinks.Email.authenticate("", 30, "")
            coVerify { StytchB2BApi.apiService.authenticate(any()) }
        }

    @Test
    fun `StytchB2BApi MagicLinks Email invite calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.sendInviteMagicLink(any()) } returns mockk(relaxed = true)
            StytchB2BApi.MagicLinks.Email.invite("email@address.com")
            coVerify { StytchB2BApi.apiService.sendInviteMagicLink(any()) }
        }

    @Test
    fun `StytchB2BApi MagicLinks Discovery send calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.sendDiscoveryMagicLink(any()) } returns mockk(relaxed = true)
            StytchB2BApi.MagicLinks.Discovery.send("", "", "", "", Locale.EN)
            coVerify { StytchB2BApi.apiService.sendDiscoveryMagicLink(any()) }
        }

    @Test
    fun `StytchB2BApi MagicLinks Discovery authenticate calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.authenticateDiscoveryMagicLink(any()) } returns mockk(relaxed = true)
            StytchB2BApi.MagicLinks.Discovery.authenticate("", "")
            coVerify { StytchB2BApi.apiService.authenticateDiscoveryMagicLink(any()) }
        }

    @Test
    fun `StytchB2BApi Sessions authenticate calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.authenticateSessions(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Sessions.authenticate(30)
            coVerify { StytchB2BApi.apiService.authenticateSessions(any()) }
        }

    @Test
    fun `StytchB2BApi Sessions revoke calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.revokeSessions() } returns mockk(relaxed = true)
            StytchB2BApi.Sessions.revoke()
            coVerify { StytchB2BApi.apiService.revokeSessions() }
        }

    @Test
    fun `StytchB2BApi Sessions exchange calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.exchangeSession(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Sessions.exchange(organizationId = "test-123", sessionDurationMinutes = 30)
            coVerify { StytchB2BApi.apiService.exchangeSession(any()) }
        }

    @Test
    fun `StytchB2BApi Organizations getOrganization calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.getOrganization() } returns mockk(relaxed = true)
            StytchB2BApi.Organization.getOrganization()
            coVerify { StytchB2BApi.apiService.getOrganization() }
        }

    @Test
    fun `StytchB2BApi Organizations update calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.updateOrganization(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Organization.updateOrganization()
            coVerify { StytchB2BApi.apiService.updateOrganization(any()) }
        }

    @Test
    fun `StytchB2BApi Organizations delete calls appropriate apiService method()`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.deleteOrganization() } returns mockk(relaxed = true)
            StytchB2BApi.Organization.deleteOrganization()
            coVerify { StytchB2BApi.apiService.deleteOrganization() }
        }

    @Test
    fun `StytchB2BApi Organizations member delete calls appropriate apiService method()`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.deleteOrganizationMember(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Organization.deleteOrganizationMember("my-member-id")
            coVerify { StytchB2BApi.apiService.deleteOrganizationMember(any()) }
        }

    @Test
    fun `StytchB2BApi Organizations member reactivate calls appropriate apiService method()`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.reactivateOrganizationMember(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Organization.reactivateOrganizationMember("my-member-id")
            coVerify { StytchB2BApi.apiService.reactivateOrganizationMember(any()) }
        }

    @Test
    fun `StytchB2BApi Organization deleteOrganizationMemberMFAPhoneNumber calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery {
                StytchB2BApi.apiService.deleteOrganizationMemberMFAPhoneNumber(
                    any(),
                )
            } returns mockk(relaxed = true)
            StytchB2BApi.Organization.deleteOrganizationMemberMFAPhoneNumber("my-member-id")
            coVerify { StytchB2BApi.apiService.deleteOrganizationMemberMFAPhoneNumber("my-member-id") }
        }

    @Test
    fun `StytchB2BApi Organization deleteOrganizationMemberMFATOTP calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.deleteOrganizationMemberMFATOTP(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Organization.deleteOrganizationMemberMFATOTP("my-member-id")
            coVerify { StytchB2BApi.apiService.deleteOrganizationMemberMFATOTP("my-member-id") }
        }

    @Test
    fun `StytchB2BApi Organization deleteOrganizationMemberPassword calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery {
                StytchB2BApi.apiService.deleteOrganizationMemberPassword(
                    any(),
                )
            } returns mockk(relaxed = true)
            StytchB2BApi.Organization.deleteOrganizationMemberPassword("password-id")
            coVerify { StytchB2BApi.apiService.deleteOrganizationMemberPassword("password-id") }
        }

    @Test
    fun `StytchB2BApi Organization createOrganizationMember calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.createMember(any()) } returns mockk(relaxed = true)
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
            coVerify { StytchB2BApi.apiService.createMember(any()) }
        }

    @Test
    fun `StytchB2BApi Organization updateOrganizationMember calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
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
            coVerify { StytchB2BApi.apiService.updateOrganizationMember("my-member-id", any()) }
        }

    @Test
    fun `StytchB2BApi Organization searchMembers calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery {
                StytchB2BApi.apiService.searchMembers(any())
            } returns mockk(relaxed = true)
            StytchB2BApi.Organization.search()
            coVerify { StytchB2BApi.apiService.searchMembers(any()) }
        }

    @Test
    fun `StytchB2BApi Member get calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.getMember() } returns mockk(relaxed = true)
            StytchB2BApi.Member.getMember()
            coVerify { StytchB2BApi.apiService.getMember() }
        }

    @Test
    fun `StytchB2BApi Member update calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.updateMember(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Member.updateMember("", emptyMap(), false, "", MfaMethod.SMS)
            coVerify { StytchB2BApi.apiService.updateMember(any()) }
        }

    @Test
    fun `StytchB2BApi Member deleteMFAPhoneNumber calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.deleteMFAPhoneNumber() } returns mockk(relaxed = true)
            StytchB2BApi.Member.deleteMFAPhoneNumber()
            coVerify { StytchB2BApi.apiService.deleteMFAPhoneNumber() }
        }

    @Test
    fun `StytchB2BApi Member deleteMFATOTP calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.deleteMFATOTP() } returns mockk(relaxed = true)
            StytchB2BApi.Member.deleteMFATOTP()
            coVerify { StytchB2BApi.apiService.deleteMFATOTP() }
        }

    @Test
    fun `StytchB2BApi Member deletePassword calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.deletePassword("passwordId") } returns mockk(relaxed = true)
            StytchB2BApi.Member.deletePassword("passwordId")
            coVerify { StytchB2BApi.apiService.deletePassword("passwordId") }
        }

    @Test
    fun `StytchB2BApi Passwords authenticate calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.authenticatePassword(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Passwords.authenticate("", "", "")
            coVerify { StytchB2BApi.apiService.authenticatePassword(any()) }
        }

    @Test
    fun `StytchB2BApi Passwords resetByEmailStart calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.resetPasswordByEmailStart(any()) } returns mockk(relaxed = true)
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
            coVerify { StytchB2BApi.apiService.resetPasswordByEmailStart(any()) }
        }

    @Test
    fun `StytchB2BApi Passwords resetByEmail calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.resetPasswordByEmail(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Passwords.resetByEmail(passwordResetToken = "", password = "", codeVerifier = "")
            coVerify { StytchB2BApi.apiService.resetPasswordByEmail(any()) }
        }

    @Test
    fun `StytchB2BApi Passwords resetByExisting calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.resetPasswordByExisting(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Passwords.resetByExisting(
                organizationId = "",
                emailAddress = "",
                existingPassword = "",
                newPassword = "",
            )
            coVerify { StytchB2BApi.apiService.resetPasswordByExisting(any()) }
        }

    @Test
    fun `StytchB2BApi Passwords resetBySession calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.resetPasswordBySession(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Passwords.resetBySession(organizationId = "", password = "", locale = null)
            coVerify { StytchB2BApi.apiService.resetPasswordBySession(any()) }
        }

    @Test
    fun `StytchB2BApi Passwords strengthCheck calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.passwordStrengthCheck(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Passwords.strengthCheck(email = "", password = "")
            coVerify { StytchB2BApi.apiService.passwordStrengthCheck(any()) }
        }

    @Test
    fun `StytchB2BApi Passwords Discovery ResetByEmailStart calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.passwordDiscoveryResetByEmailStart(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Passwords.Discovery.resetByEmailStart("", null, null, null, null, "", null, null)
            coVerify { StytchB2BApi.apiService.passwordDiscoveryResetByEmailStart(any()) }
        }

    @Test
    fun `StytchB2BApi Passwords Discovery ResetByEmail calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.passwordDiscoveryResetByEmail(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Passwords.Discovery.resetByEmail("", "", null, "", null)
            coVerify { StytchB2BApi.apiService.passwordDiscoveryResetByEmail(any()) }
        }

    @Test
    fun `StytchB2BApi Passwords Discovery Authenticate calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.passwordDiscoveryAuthenticate(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Passwords.Discovery.authenticate("", "")
            coVerify { StytchB2BApi.apiService.passwordDiscoveryAuthenticate(any()) }
        }

    @Test
    fun `StytchB2BApi Discovery discoverOrganizations calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.discoverOrganizations(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Discovery.discoverOrganizations(null)
            coVerify { StytchB2BApi.apiService.discoverOrganizations(any()) }
        }

    @Test
    fun `StytchB2BApi Discovery exchangeSession calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.intermediateSessionExchange(any()) } returns mockk(relaxed = true)
            StytchB2BApi.Discovery.exchangeSession(
                intermediateSessionToken = "",
                organizationId = "",
                sessionDurationMinutes = 30,
            )
            coVerify { StytchB2BApi.apiService.intermediateSessionExchange(any()) }
        }

    @Test
    fun `StytchB2BApi Discovery createOrganization calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.createOrganization(any()) } returns mockk(relaxed = true)
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
            coVerify { StytchB2BApi.apiService.createOrganization(expectedParams) }
        }

    @Test
    fun `StytchB2BApi SSO authenticate calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.ssoAuthenticate(any()) } returns mockk(relaxed = true)
            StytchB2BApi.SSO.authenticate(
                ssoToken = "",
                sessionDurationMinutes = 30,
                codeVerifier = "",
            )
            coVerify { StytchB2BApi.apiService.ssoAuthenticate(any()) }
        }

    @Test
    fun `StytchB2BApi SSO getConnections calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.ssoGetConnections() } returns mockk(relaxed = true)
            StytchB2BApi.SSO.getConnections()
            coVerify { StytchB2BApi.apiService.ssoGetConnections() }
        }

    @Test
    fun `StytchB2BApi SSO deleteConnection calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.ssoDeleteConnection(any()) } returns mockk(relaxed = true)
            val connectionId = "my-connection-id"
            StytchB2BApi.SSO.deleteConnection(connectionId = connectionId)
            coVerify { StytchB2BApi.apiService.ssoDeleteConnection(connectionId) }
        }

    @Test
    fun `StytchB2BApi SSO SAML createConnection calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.ssoSamlCreate(any()) } returns mockk(relaxed = true)
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
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.ssoSamlUpdate(any(), any()) } returns mockk(relaxed = true)
            val connectionId = "my-connection-id"
            StytchB2BApi.SSO.samlUpdateConnection(connectionId = connectionId)
            coVerify {
                StytchB2BApi.apiService.ssoSamlUpdate(connectionId = connectionId, any())
            }
        }

    @Test
    fun `StytchB2BApi SSO SAML updateConnectionByUrl calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.ssoSamlUpdateByUrl(any(), any()) } returns mockk(relaxed = true)
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
            every { StytchB2BApi.isInitialized } returns true
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
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.ssoOidcCreate(any()) } returns mockk(relaxed = true)
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
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.ssoOidcUpdate(any(), any()) } returns mockk(relaxed = true)
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
            every { StytchB2BApi.isInitialized } returns true
            every { StytchB2BApi.publicToken } returns "mock-public-token"
            coEvery { StytchB2BApi.apiService.getBootstrapData("mock-public-token") } returns mockk(relaxed = true)
            StytchB2BApi.getBootstrapData()
            coVerify { StytchB2BApi.apiService.getBootstrapData("mock-public-token") }
        }

    @Test
    fun `StytchB2BApi Events logEvent calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            every { StytchB2BApi.publicToken } returns "mock-public-token"
            coEvery { StytchB2BApi.apiService.logEvent(any()) } returns mockk(relaxed = true)
            val details = mapOf("test-key" to "test value")
            val header = InfoHeaderModel.fromDeviceInfo(mockDeviceInfo)
            StytchB2BApi.Events.logEvent(
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
                StytchB2BApi.apiService.logEvent(
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

    @Test
    fun `StytchB2BApi OTP sendSMSOTP calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.sendSMSOTP(any()) } returns mockk(relaxed = true)
            StytchB2BApi.OTP.sendSMSOTP("", "")
            coVerify { StytchB2BApi.apiService.sendSMSOTP(any()) }
        }

    @Test
    fun `StytchB2BApi OTP authenticateSMSOTP calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.authenticateSMSOTP(any()) } returns mockk(relaxed = true)
            StytchB2BApi.OTP.authenticateSMSOTP("", "", "", null, 30)
            coVerify { StytchB2BApi.apiService.authenticateSMSOTP(any()) }
        }

    @Test
    fun `StytchB2BApi TOTP create calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.createTOTP(any()) } returns mockk(relaxed = true)
            StytchB2BApi.TOTP.create("", "")
            coVerify { StytchB2BApi.apiService.createTOTP(any()) }
        }

    @Test
    fun `StytchB2BApi TOTP authenticate calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.authenticateTOTP(any()) } returns mockk(relaxed = true)
            StytchB2BApi.TOTP.authenticate("", "", "", null, null, 30)
            coVerify { StytchB2BApi.apiService.authenticateTOTP(any()) }
        }

    @Test
    fun `StytchB2BApi RecoveryCodes get calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.getRecoveryCodes() } returns mockk(relaxed = true)
            StytchB2BApi.RecoveryCodes.get()
            coVerify { StytchB2BApi.apiService.getRecoveryCodes() }
        }

    @Test
    fun `StytchB2BApi RecoveryCodes rotate calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.rotateRecoveryCodes() } returns mockk(relaxed = true)
            StytchB2BApi.RecoveryCodes.rotate()
            coVerify { StytchB2BApi.apiService.rotateRecoveryCodes() }
        }

    @Test
    fun `StytchB2BApi RecoveryCodes recover calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.recoverRecoveryCodes(any()) } returns mockk(relaxed = true)
            StytchB2BApi.RecoveryCodes.recover("", "", 30, "")
            coVerify { StytchB2BApi.apiService.recoverRecoveryCodes(any()) }
        }

    @Test
    fun `StytchB2BApi OAuth authenticate calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.oauthAuthenticate(any()) } returns mockk(relaxed = true)
            StytchB2BApi.OAuth.authenticate("", Locale.EN, 30, "", "")
            coVerify { StytchB2BApi.apiService.oauthAuthenticate(any()) }
        }

    @Test
    fun `StytchB2BApi OAuth discoveryAuthenticate calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.oauthDiscoveryAuthenticate(any()) } returns mockk(relaxed = true)
            StytchB2BApi.OAuth.discoveryAuthenticate("", "")
            coVerify { StytchB2BApi.apiService.oauthDiscoveryAuthenticate(any()) }
        }

    @Test
    fun `StytchB2BApi SearchManager searchOrganizations calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.searchOrganizations(any()) } returns mockk(relaxed = true)
            StytchB2BApi.SearchManager.searchOrganizations("organization-slug")
            coVerify { StytchB2BApi.apiService.searchOrganizations(any()) }
        }

    @Test
    fun `StytchB2BApi SearchManager searchOrganizationMembers calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.searchOrganizationMembers(any()) } returns mockk(relaxed = true)
            StytchB2BApi.SearchManager.searchMembers("email@example.com", "organization-id")
            coVerify { StytchB2BApi.apiService.searchOrganizationMembers(any()) }
        }

    @Test
    fun `StytchB2BApi SCIM createConnection calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.scimCreateConnection(any()) } returns mockk(relaxed = true)
            StytchB2BApi.SCIM.createConnection("", "")
            coVerify { StytchB2BApi.apiService.scimCreateConnection(any()) }
        }

    @Test
    fun `StytchB2BApi SCIM updateConnection calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.scimUpdateConnection(any(), any()) } returns mockk(relaxed = true)
            StytchB2BApi.SCIM.updateConnection("connection-id", "", "", emptyList())
            coVerify { StytchB2BApi.apiService.scimUpdateConnection(eq("connection-id"), any()) }
        }

    @Test
    fun `StytchB2BApi SCIM deleteConection calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.scimDeleteConnection(any()) } returns mockk(relaxed = true)
            StytchB2BApi.SCIM.deleteConection("connection-id")
            coVerify { StytchB2BApi.apiService.scimDeleteConnection(eq("connection-id")) }
        }

    @Test
    fun `StytchB2BApi SCIM getConnection calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.scimGetConnection() } returns mockk(relaxed = true)
            StytchB2BApi.SCIM.getConnection()
            coVerify { StytchB2BApi.apiService.scimGetConnection() }
        }

    @Test
    fun `StytchB2BApi SCIM getConnectionGroups calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.scimGetConnectionGroups(any()) } returns mockk(relaxed = true)
            StytchB2BApi.SCIM.getConnectionGroups("", 1000)
            coVerify { StytchB2BApi.apiService.scimGetConnectionGroups(any()) }
        }

    @Test
    fun `StytchB2BApi SCIM rotateStart calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.scimRotateStart(any()) } returns mockk(relaxed = true)
            StytchB2BApi.SCIM.rotateStart("connection-id")
            coVerify { StytchB2BApi.apiService.scimRotateStart(any()) }
        }

    @Test
    fun `StytchB2BApi SCIM rotateCancel calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.scimRotateCancel(any()) } returns mockk(relaxed = true)
            StytchB2BApi.SCIM.rotateCancel("connection-id")
            coVerify { StytchB2BApi.apiService.scimRotateCancel(any()) }
        }

    @Test
    fun `StytchB2BApi SCIM rotateComplete calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.scimRotateComplete(any()) } returns mockk(relaxed = true)
            StytchB2BApi.SCIM.rotateComplete("connection-id")
            coVerify { StytchB2BApi.apiService.scimRotateComplete(any()) }
        }

    @Test
    fun `StytchB2BApi OTP Email otpEmailLoginOrSignup calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.otpEmailLoginOrSignup(any()) } returns mockk(relaxed = true)
            StytchB2BApi.OTP.otpEmailLoginOrSignup("", "", null, null, null)
            coVerify { StytchB2BApi.apiService.otpEmailLoginOrSignup(any()) }
        }

    @Test
    fun `StytchB2BApi OTP Email otpEmailAuthenticate calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.otpEmailAuthenticate(any()) } returns mockk(relaxed = true)
            StytchB2BApi.OTP.otpEmailAuthenticate("", "", "", null, 30)
            coVerify { StytchB2BApi.apiService.otpEmailAuthenticate(any()) }
        }

    @Test
    fun `StytchB2BApi OTP Email otpEmailDiscoverySend calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.otpEmailDiscoverySend(any()) } returns mockk(relaxed = true)
            StytchB2BApi.OTP.otpEmailDiscoverySend("", null, null)
            coVerify { StytchB2BApi.apiService.otpEmailDiscoverySend(any()) }
        }

    @Test
    fun `StytchB2BApi OTP Email otpEmailDiscoveryAuthenticate calls appropriate apiService method`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true
            coEvery { StytchB2BApi.apiService.otpEmailDiscoveryAuthenticate(any()) } returns mockk(relaxed = true)
            StytchB2BApi.OTP.otpEmailDiscoveryAuthenticate("", "")
            coVerify { StytchB2BApi.apiService.otpEmailDiscoveryAuthenticate(any()) }
        }

    @Test(expected = StytchSDKNotConfiguredError::class)
    fun `safeApiCall throws exception when StytchB2BClient is not initialized`(): Unit =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns false
            val mockApiCall: suspend () -> StytchDataResponse<Boolean> = mockk()
            StytchB2BApi.safeB2BApiCall { mockApiCall() }
        }

    @Test
    fun `safeApiCall returns success when call succeeds`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true

            fun mockApiCall(): StytchDataResponse<Boolean> = StytchDataResponse(true)
            val result = StytchB2BApi.safeB2BApiCall { mockApiCall() }
            assert(result is StytchResult.Success)
        }

    @Test
    fun `safeApiCall returns correct error for HttpException`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true

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
            every { StytchB2BApi.isInitialized } returns true

            fun mockApiCall(): StytchDataResponse<Boolean> =
                throw StytchAPIError(errorType = StytchAPIErrorType.UNKNOWN_ERROR, message = "", statusCode = 400)
            val result = StytchB2BApi.safeB2BApiCall { mockApiCall() }
            assert(result is StytchResult.Error)
        }

    @Test
    fun `safeApiCall returns correct error for other exceptions`() =
        runBlocking {
            every { StytchB2BApi.isInitialized } returns true

            fun mockApiCall(): StytchDataResponse<Boolean> {
                error("Test")
            }
            val result = StytchB2BApi.safeB2BApiCall { mockApiCall() }
            assert(result is StytchResult.Error)
        }
}
