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
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchAPIError
import com.stytch.sdk.common.errors.StytchSDKNotConfiguredError
import com.stytch.sdk.common.network.InfoHeaderModel
import com.stytch.sdk.common.network.StytchDataResponse
import com.stytch.sdk.common.network.models.CommonRequests
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkAll
import java.security.KeyStore
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

internal class StytchB2BApiTest {
    var mContextMock = mockk<Context>(relaxed = true)

    private val mockDeviceInfo = DeviceInfo(
        applicationPackageName = "com.stytch.test",
        applicationVersion = "1.0.0",
        osName = "Android",
        osVersion = "14",
        deviceName = "Test Device",
        screenSize = ""
    )

    @Before
    fun before() {
        val mockApplication: Application = mockk {
            every { registerActivityLifecycleCallbacks(any()) } just runs
            every { packageName } returns "Stytch"
        }
        mContextMock = mockk(relaxed = true) {
            every { applicationContext } returns mockApplication
        }
        mockkStatic(KeyStore::class)
        mockkObject(EncryptionManager)
        mockkObject(StytchB2BApi)
        every { EncryptionManager.createNewKeys(any(), any()) } returns Unit
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(StorageHelper)
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `StytchB2BApi isInitialized returns correctly based on configuration state`() {
        StytchB2BApi.configure("publicToken", DeviceInfo())
        assert(StytchB2BApi.isInitialized)
    }

    @Test(expected = StytchSDKNotConfiguredError::class)
    fun `StytchB2BApi apiService throws exception when not configured`() {
        every { StytchB2BApi.isInitialized } returns false
        StytchB2BApi.apiService
    }

    @Test
    fun `StytchB2BApi apiService is available when configured`() {
        StytchB2BClient.configure(mContextMock, "")
        StytchB2BApi.apiService
    }

    // TODO every method calls safeApi

    @Test
    fun `StytchB2BApi MagicLinks Email loginOrCreate calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.loginOrSignupByEmail(any()) } returns mockk(relaxed = true)
        StytchB2BApi.MagicLinks.Email.loginOrSignupByEmail("", "", "", "", "", "", "")
        coVerify { StytchB2BApi.apiService.loginOrSignupByEmail(any()) }
    }

    @Test
    fun `StytchB2BApi MagicLinks Email authenticate calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.authenticate(any()) } returns mockk(relaxed = true)
        StytchB2BApi.MagicLinks.Email.authenticate("", 30U, "")
        coVerify { StytchB2BApi.apiService.authenticate(any()) }
    }

    @Test
    fun `StytchB2BApi MagicLinks Email invite calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.sendInviteMagicLink(any()) } returns mockk(relaxed = true)
        StytchB2BApi.MagicLinks.Email.invite("email@address.com")
        coVerify { StytchB2BApi.apiService.sendInviteMagicLink(any()) }
    }

    @Test
    fun `StytchB2BApi MagicLinks Discovery send calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.sendDiscoveryMagicLink(any()) } returns mockk(relaxed = true)
        StytchB2BApi.MagicLinks.Discovery.send("", "", "", "")
        coVerify { StytchB2BApi.apiService.sendDiscoveryMagicLink(any()) }
    }

    @Test
    fun `StytchB2BApi MagicLinks Discovery authenticate calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.authenticateDiscoveryMagicLink(any()) } returns mockk(relaxed = true)
        StytchB2BApi.MagicLinks.Discovery.authenticate("", "")
        coVerify { StytchB2BApi.apiService.authenticateDiscoveryMagicLink(any()) }
    }

    @Test
    fun `StytchB2BApi Sessions authenticate calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.authenticateSessions(any()) } returns mockk(relaxed = true)
        StytchB2BApi.Sessions.authenticate(30U)
        coVerify { StytchB2BApi.apiService.authenticateSessions(any()) }
    }

    @Test
    fun `StytchB2BApi Sessions revoke calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.revokeSessions() } returns mockk(relaxed = true)
        StytchB2BApi.Sessions.revoke()
        coVerify { StytchB2BApi.apiService.revokeSessions() }
    }

    @Test
    fun `StytchB2BApi Sessions exchange calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.exchangeSession(any()) } returns mockk(relaxed = true)
        StytchB2BApi.Sessions.exchange(organizationId = "test-123", sessionDurationMinutes = 30U)
        coVerify { StytchB2BApi.apiService.exchangeSession(any()) }
    }

    @Test
    fun `StytchB2BApi Organizations getOrganization calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.getOrganization() } returns mockk(relaxed = true)
        StytchB2BApi.Organization.getOrganization()
        coVerify { StytchB2BApi.apiService.getOrganization() }
    }

    @Test
    fun `StytchB2BApi Member get calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.getMember() } returns mockk(relaxed = true)
        StytchB2BApi.Member.getMember()
        coVerify { StytchB2BApi.apiService.getMember() }
    }

    @Test
    fun `StytchB2BApi Member update calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.updateMember(any()) } returns mockk(relaxed = true)
        StytchB2BApi.Member.updateUser("", emptyMap(), false, "", MfaMethod.SMS)
        coVerify { StytchB2BApi.apiService.updateMember(any()) }
    }

    @Test
    fun `StytchB2BApi Member deleteMFAPhoneNumber calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.deleteMFAPhoneNumber() } returns mockk(relaxed = true)
        StytchB2BApi.Member.deleteMFAPhoneNumber()
        coVerify { StytchB2BApi.apiService.deleteMFAPhoneNumber() }
    }

    @Test
    fun `StytchB2BApi Member deleteMFATOTP calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.deleteMFATOTP() } returns mockk(relaxed = true)
        StytchB2BApi.Member.deleteMFATOTP()
        coVerify { StytchB2BApi.apiService.deleteMFATOTP() }
    }

    @Test
    fun `StytchB2BApi Member deletePassword calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.deletePassword("passwordId") } returns mockk(relaxed = true)
        StytchB2BApi.Member.deletePassword("passwordId")
        coVerify { StytchB2BApi.apiService.deletePassword("passwordId") }
    }

    @Test
    fun `StytchB2BApi Passwords authenticate calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.authenticatePassword(any()) } returns mockk(relaxed = true)
        StytchB2BApi.Passwords.authenticate("", "", "")
        coVerify { StytchB2BApi.apiService.authenticatePassword(any()) }
    }

    @Test
    fun `StytchB2BApi Passwords resetByEmailStart calls appropriate apiService method`() = runTest {
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
        )
        coVerify { StytchB2BApi.apiService.resetPasswordByEmailStart(any()) }
    }

    @Test
    fun `StytchB2BApi Passwords resetByEmail calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.resetPasswordByEmail(any()) } returns mockk(relaxed = true)
        StytchB2BApi.Passwords.resetByEmail(passwordResetToken = "", password = "", codeVerifier = "")
        coVerify { StytchB2BApi.apiService.resetPasswordByEmail(any()) }
    }

    @Test
    fun `StytchB2BApi Passwords resetByExisting calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.resetPasswordByExisting(any()) } returns mockk(relaxed = true)
        StytchB2BApi.Passwords.resetByExisting(
            organizationId = "",
            emailAddress = "",
            existingPassword = "",
            newPassword = ""
        )
        coVerify { StytchB2BApi.apiService.resetPasswordByExisting(any()) }
    }

    @Test
    fun `StytchB2BApi Passwords resetBySession calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.resetPasswordBySession(any()) } returns mockk(relaxed = true)
        StytchB2BApi.Passwords.resetBySession(organizationId = "", password = "")
        coVerify { StytchB2BApi.apiService.resetPasswordBySession(any()) }
    }

    @Test
    fun `StytchB2BApi Passwords strengthCheck calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.passwordStrengthCheck(any()) } returns mockk(relaxed = true)
        StytchB2BApi.Passwords.strengthCheck(email = "", password = "")
        coVerify { StytchB2BApi.apiService.passwordStrengthCheck(any()) }
    }

    @Test
    fun `StytchB2BApi Discovery discoverOrganizations calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.discoverOrganizations(any()) } returns mockk(relaxed = true)
        StytchB2BApi.Discovery.discoverOrganizations(null)
        coVerify { StytchB2BApi.apiService.discoverOrganizations(any()) }
    }

    @Test
    fun `StytchB2BApi Discovery exchangeSession calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.intermediateSessionExchange(any()) } returns mockk(relaxed = true)
        StytchB2BApi.Discovery.exchangeSession(
            intermediateSessionToken = "",
            organizationId = "",
            sessionDurationMinutes = 30U
        )
        coVerify { StytchB2BApi.apiService.intermediateSessionExchange(any()) }
    }

    @Test
    fun `StytchB2BApi Discovery createOrganization calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.createOrganization(any()) } returns mockk(relaxed = true)
        StytchB2BApi.Discovery.createOrganization(
            intermediateSessionToken = "",
            organizationLogoUrl = "",
            organizationSlug = "",
            organizationName = "",
            sessionDurationMinutes = 30U,
            ssoJitProvisioning = SsoJitProvisioning.ALL_ALLOWED,
            emailJitProvisioning = EmailJitProvisioning.RESTRICTED,
            emailInvites = EmailInvites.ALL_ALLOWED,
            emailAllowedDomains = listOf("allowed-domain.com"),
            authMethods = AuthMethods.RESTRICTED,
            allowedAuthMethods = listOf(AllowedAuthMethods.PASSWORD, AllowedAuthMethods.MAGIC_LINK),
        )
        val expectedParams = B2BRequests.Discovery.CreateRequest(
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
    fun `StytchB2BApi SSO authenticate calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        coEvery { StytchB2BApi.apiService.ssoAuthenticate(any()) } returns mockk(relaxed = true)
        StytchB2BApi.SSO.authenticate(
            ssoToken = "",
            sessionDurationMinutes = 30U,
            codeVerifier = ""
        )
        coVerify { StytchB2BApi.apiService.ssoAuthenticate(any()) }
    }

    @Test
    fun `StytchB2BApi Bootstrap getBootstrapData calls appropriate apiService method`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        every { StytchB2BApi.publicToken } returns "mock-public-token"
        coEvery { StytchB2BApi.apiService.getBootstrapData("mock-public-token") } returns mockk(relaxed = true)
        StytchB2BApi.getBootstrapData()
        coVerify { StytchB2BApi.apiService.getBootstrapData("mock-public-token") }
    }

    @Test
    fun `StytchB2BApi Events logEvent calls appropriate apiService method`() = runTest {
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
                        telemetry = CommonRequests.Events.EventTelemetry(
                            eventId = "event-id",
                            appSessionId = "app-session-id",
                            persistentId = "persistent-id",
                            clientSentAt = "ISO date string",
                            timezone = "Timezone/Identifier",
                            app = CommonRequests.Events.VersionIdentifier(
                                identifier = header.app.identifier,
                                version = header.app.version
                            ),
                            os = CommonRequests.Events.VersionIdentifier(
                                identifier = header.os.identifier,
                                version = header.os.version
                            ),
                            sdk = CommonRequests.Events.VersionIdentifier(
                                identifier = header.sdk.identifier,
                                version = header.sdk.version
                            ),
                            device = CommonRequests.Events.DeviceIdentifier(
                                model = header.device.identifier,
                                screenSize = header.device.version
                            ),
                        ),
                        event = CommonRequests.Events.EventEvent(
                            publicToken = "mock-public-token",
                            eventName = "event-name",
                            details = details,
                        ),
                    )
                )
            )
        }
    }

    @Test(expected = StytchSDKNotConfiguredError::class)
    fun `safeApiCall throws exception when StytchB2BClient is not initialized`() = runTest {
        every { StytchB2BApi.isInitialized } returns false
        val mockApiCall: suspend () -> StytchDataResponse<Boolean> = mockk()
        StytchB2BApi.safeB2BApiCall { mockApiCall() }
    }

    @Test
    fun `safeApiCall returns success when call succeeds`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        fun mockApiCall(): StytchDataResponse<Boolean> {
            return StytchDataResponse(true)
        }
        val result = StytchB2BApi.safeB2BApiCall { mockApiCall() }
        assert(result is StytchResult.Success)
    }

    @Test
    fun `safeApiCall returns correct error for HttpException`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        fun mockApiCall(): StytchDataResponse<Boolean> {
            throw HttpException(
                mockk(relaxed = true) {
                    every { errorBody() } returns null
                }
            )
        }
        val result = StytchB2BApi.safeB2BApiCall { mockApiCall() }
        assert(result is StytchResult.Error)
    }

    @Test
    fun `safeApiCall returns correct error for StytchErrors`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        fun mockApiCall(): StytchDataResponse<Boolean> {
            throw StytchAPIError(errorType = "", message = "")
        }
        val result = StytchB2BApi.safeB2BApiCall { mockApiCall() }
        assert(result is StytchResult.Error)
    }

    @Test
    fun `safeApiCall returns correct error for other exceptions`() = runTest {
        every { StytchB2BApi.isInitialized } returns true
        fun mockApiCall(): StytchDataResponse<Boolean> {
            error("Test")
        }
        val result = StytchB2BApi.safeB2BApiCall { mockApiCall() }
        assert(result is StytchResult.Error)
    }
}
