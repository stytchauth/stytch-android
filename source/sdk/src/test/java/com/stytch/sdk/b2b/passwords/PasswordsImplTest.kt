package com.stytch.sdk.b2b.passwords

import com.stytch.sdk.b2b.B2BPasswordDiscoveryAuthenticateResponse
import com.stytch.sdk.b2b.B2BPasswordDiscoveryResetByEmailResponse
import com.stytch.sdk.b2b.EmailResetResponse
import com.stytch.sdk.b2b.PasswordResetByExistingPasswordResponse
import com.stytch.sdk.b2b.PasswordStrengthCheckResponse
import com.stytch.sdk.b2b.PasswordsAuthenticateResponse
import com.stytch.sdk.b2b.SessionResetResponse
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.network.models.B2BPasswordDiscoveryAuthenticateResponseData
import com.stytch.sdk.b2b.network.models.B2BPasswordDiscoveryResetByEmailResponseData
import com.stytch.sdk.b2b.network.models.EmailResetResponseData
import com.stytch.sdk.b2b.network.models.PasswordResetByExistingPasswordResponseData
import com.stytch.sdk.b2b.network.models.PasswordsAuthenticateResponseData
import com.stytch.sdk.b2b.network.models.SessionResetResponseData
import com.stytch.sdk.b2b.network.models.StrengthCheckResponseData
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.PKCECodePair
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.models.BasicData
import com.stytch.sdk.common.pkcePairManager.PKCEPairManager
import com.stytch.sdk.common.sessions.SessionAutoUpdater
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
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.security.KeyStore

internal class PasswordsImplTest {
    @MockK
    private lateinit var mockApi: StytchB2BApi.Passwords

    @MockK
    private lateinit var mockDiscoveryApi: StytchB2BApi.Passwords.Discovery

    @MockK
    private lateinit var mockSessionStorage: B2BSessionStorage

    @MockK
    private lateinit var mockPKCEPairManager: PKCEPairManager

    private lateinit var impl: PasswordsImpl
    private val dispatcher = Dispatchers.Unconfined

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any()) } returns Unit
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(SessionAutoUpdater)
        mockkStatic("com.stytch.sdk.b2b.extensions.StytchResultExtKt")
        every { SessionAutoUpdater.startSessionUpdateJob(any(), any(), any()) } just runs
        MockKAnnotations.init(this, true, true)
        every { mockSessionStorage.intermediateSessionToken } returns ""
        every { mockSessionStorage.intermediateSessionToken = any() } just runs
        every { mockSessionStorage.lastAuthMethodUsed = any() } just runs
        every { mockPKCEPairManager.clearPKCECodePair() } just runs
        impl =
            PasswordsImpl(
                externalScope = TestScope(),
                dispatchers = StytchDispatchers(dispatcher, dispatcher),
                sessionStorage = mockSessionStorage,
                pkcePairManager = mockPKCEPairManager,
                api = mockApi,
                discoveryApi = mockDiscoveryApi,
            )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `PasswordsImpl authenticate delegates to api`() =
        runBlocking {
            val mockkResponse = StytchResult.Success<PasswordsAuthenticateResponseData>(mockk(relaxed = true))
            coEvery { mockApi.authenticate(any(), any(), any(), any(), any(), any()) } returns mockkResponse
            val response = impl.authenticate(mockk(relaxed = true))
            assert(response is StytchResult.Success)
            coVerify { mockApi.authenticate(any(), any(), any(), any(), any(), any()) }
            verify { mockkResponse.launchSessionUpdater(any(), any()) }
        }

    @Test
    fun `PasswordsImpl authenticate with callback calls callback method`() {
        val mockkResponse = StytchResult.Success<PasswordsAuthenticateResponseData>(mockk(relaxed = true))
        coEvery { mockApi.authenticate(any(), any(), any(), any(), any(), any()) } returns mockkResponse
        val mockCallback = spyk<(PasswordsAuthenticateResponse) -> Unit>()
        impl.authenticate(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(mockkResponse) }
    }

    @Test
    fun `PasswordsImpl resetByEmailStart returns error if generateHashedCodeChallenge fails`() =
        runBlocking {
            every { mockPKCEPairManager.generateAndReturnPKCECodePair() } throws RuntimeException("Test")
            val response = impl.resetByEmailStart(mockk(relaxed = true))
            assert(response is StytchResult.Error)
        }

    @Test
    fun `PasswordsImpl resetByEmailStart delegates to api`() =
        runBlocking {
            every { mockPKCEPairManager.generateAndReturnPKCECodePair() } returns PKCECodePair("", "")
            val mockkResponse = StytchResult.Success<BasicData>(mockk(relaxed = true))
            coEvery { mockApi.resetByEmailStart(any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns
                mockkResponse
            val response =
                impl.resetByEmailStart(
                    Passwords.ResetByEmailStartParameters(
                        organizationId = "",
                        emailAddress = "",
                        loginRedirectUrl = null,
                        resetPasswordRedirectUrl = null,
                        resetPasswordTemplateId = null,
                        resetPasswordExpirationMinutes = null,
                    ),
                )
            assert(response is StytchResult.Success)
            coVerify { mockApi.resetByEmailStart(any(), any(), any(), any(), any(), any(), any(), any(), any()) }
        }

    @Test
    fun `PasswordsImpl resetByEmailStart with callback calls callback method`() {
        every { mockPKCEPairManager.generateAndReturnPKCECodePair() } returns PKCECodePair("", "")
        val mockkResponse = StytchResult.Success<BasicData>(mockk(relaxed = true))
        coEvery { mockApi.resetByEmailStart(any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns
            mockkResponse
        val mockCallback = spyk<(BaseResponse) -> Unit>()
        impl.resetByEmailStart(
            Passwords.ResetByEmailStartParameters(
                organizationId = "",
                emailAddress = "",
                loginRedirectUrl = null,
                resetPasswordRedirectUrl = null,
                resetPasswordTemplateId = null,
                resetPasswordExpirationMinutes = null,
            ),
            mockCallback,
        )
        verify { mockCallback.invoke(mockkResponse) }
    }

    @Test
    fun `PasswordsImpl resetByEmail returns error if codeVerifier fails`() =
        runBlocking {
            every { mockPKCEPairManager.getPKCECodePair() } returns null
            val response = impl.resetByEmail(mockk(relaxed = true))
            assert(response is StytchResult.Error)
        }

    @Test
    fun `PasswordsImpl resetByEmail delegates to api`() =
        runBlocking {
            every { mockPKCEPairManager.getPKCECodePair() } returns PKCECodePair("", "")
            val mockkResponse = StytchResult.Success<EmailResetResponseData>(mockk(relaxed = true))
            coEvery { mockApi.resetByEmail(any(), any(), any(), any(), any(), any()) } returns mockkResponse
            val response = impl.resetByEmail(mockk(relaxed = true))
            assert(response is StytchResult.Success)
            coVerify { mockApi.resetByEmail(any(), any(), any(), any(), any(), any()) }
            verify { mockkResponse.launchSessionUpdater(any(), any()) }
            verify(exactly = 1) { mockPKCEPairManager.clearPKCECodePair() }
        }

    @Test
    fun `PasswordsImpl resetByEmail with callback calls callback method`() {
        every { mockPKCEPairManager.getPKCECodePair() } returns PKCECodePair("", "")
        val mockkResponse = StytchResult.Success<EmailResetResponseData>(mockk(relaxed = true))
        coEvery { mockApi.resetByEmail(any(), any(), any(), any(), any(), any()) } returns mockkResponse
        val mockCallback = spyk<(EmailResetResponse) -> Unit>()
        impl.resetByEmail(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(mockkResponse) }
        verify { mockkResponse.launchSessionUpdater(any(), any()) }
        verify(exactly = 1) { mockPKCEPairManager.clearPKCECodePair() }
    }

    @Test
    fun `PasswordsImpl resetByExisting delegates to api`() =
        runBlocking {
            val mockkResponse = StytchResult.Success<PasswordResetByExistingPasswordResponseData>(mockk(relaxed = true))
            coEvery { mockApi.resetByExisting(any(), any(), any(), any(), any(), any()) } returns mockkResponse
            val response = impl.resetByExisting(mockk(relaxed = true))
            assert(response is StytchResult.Success)
            coVerify { mockApi.resetByExisting(any(), any(), any(), any(), any(), any()) }
            verify { mockkResponse.launchSessionUpdater(any(), any()) }
        }

    @Test
    fun `PasswordsImpl resetByExisting with callback calls callback method`() {
        val mockkResponse = StytchResult.Success<PasswordResetByExistingPasswordResponseData>(mockk(relaxed = true))
        coEvery { mockApi.resetByExisting(any(), any(), any(), any(), any(), any()) } returns mockkResponse
        val mockCallback = spyk<(PasswordResetByExistingPasswordResponse) -> Unit>()
        impl.resetByExisting(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(mockkResponse) }
    }

    @Test
    fun `PasswordsImpl resetBySession delegates to api`() =
        runBlocking {
            val mockkResponse = StytchResult.Success<SessionResetResponseData>(mockk(relaxed = true))
            coEvery { mockApi.resetBySession(any(), any(), any()) } returns mockkResponse
            val response = impl.resetBySession(mockk(relaxed = true))
            assert(response is StytchResult.Success)
            coVerify { mockApi.resetBySession(any(), any(), any()) }
        }

    @Test
    fun `PasswordsImpl resetBySession with callback calls callback method`() {
        val mockkResponse = StytchResult.Success<SessionResetResponseData>(mockk(relaxed = true))
        coEvery { mockApi.resetBySession(any(), any(), any()) } returns mockkResponse
        val mockCallback = spyk<(SessionResetResponse) -> Unit>()
        impl.resetBySession(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(mockkResponse) }
    }

    @Test
    fun `PasswordsImpl strengthCheck delegates to api`() =
        runBlocking {
            val mockkResponse = StytchResult.Success<StrengthCheckResponseData>(mockk(relaxed = true))
            coEvery { mockApi.strengthCheck(any(), any()) } returns mockkResponse
            val response = impl.strengthCheck(mockk(relaxed = true))
            assert(response is StytchResult.Success)
            coVerify { mockApi.strengthCheck(any(), any()) }
        }

    @Test
    fun `PasswordsImpl strengthCheck with callback calls callback method`() {
        val mockkResponse = StytchResult.Success<StrengthCheckResponseData>(mockk(relaxed = true))
        coEvery { mockApi.strengthCheck(any(), any()) } returns mockkResponse
        val mockCallback = spyk<(PasswordStrengthCheckResponse) -> Unit>()
        impl.strengthCheck(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(mockkResponse) }
    }

    @Test
    fun `PasswordsImpl DiscoveryImpl resetByEmailStart delegates to api`() =
        runBlocking {
            every { mockPKCEPairManager.generateAndReturnPKCECodePair() } returns PKCECodePair("", "")
            val mockkResponse = StytchResult.Success<BasicData>(mockk(relaxed = true))
            coEvery {
                mockDiscoveryApi.resetByEmailStart(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
            } returns
                mockkResponse
            val response = impl.discovery.resetByEmailStart(mockk(relaxed = true))
            assert(response is StytchResult.Success)
            coVerify { mockDiscoveryApi.resetByEmailStart(any(), any(), any(), any(), any(), any(), any(), any()) }
        }

    @Test
    fun `PasswordsImpl DiscoveryImpl resetByEmailStart with callback calls callback method`() {
        every { mockPKCEPairManager.generateAndReturnPKCECodePair() } returns PKCECodePair("", "")
        val mockkResponse = StytchResult.Success<BasicData>(mockk(relaxed = true))
        coEvery { mockDiscoveryApi.resetByEmailStart(any(), any(), any(), any(), any(), any(), any(), any()) } returns
            mockkResponse
        val mockCallback = spyk<(BaseResponse) -> Unit>()
        impl.discovery.resetByEmailStart(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(mockkResponse) }
    }

    @Test
    fun `PasswordsImpl DiscoveryImpl resetByEmail delegates to api`() =
        runBlocking {
            every { mockPKCEPairManager.getPKCECodePair() } returns PKCECodePair("", "")
            val mockkResponse =
                StytchResult.Success<B2BPasswordDiscoveryResetByEmailResponseData>(
                    mockk(relaxed = true),
                )
            coEvery { mockDiscoveryApi.resetByEmail(any(), any(), any(), any(), any()) } returns mockkResponse
            val response = impl.discovery.resetByEmail(mockk(relaxed = true))
            assert(response is StytchResult.Success)
            coVerify { mockDiscoveryApi.resetByEmail(any(), any(), any(), any(), any()) }
        }

    @Test
    fun `PasswordsImpl DiscoveryImpl resetByEmail with callback calls callback method`() {
        every { mockPKCEPairManager.getPKCECodePair() } returns PKCECodePair("", "")
        val mockkResponse = StytchResult.Success<B2BPasswordDiscoveryResetByEmailResponseData>(mockk(relaxed = true))
        coEvery { mockDiscoveryApi.resetByEmail(any(), any(), any(), any(), any()) } returns mockkResponse
        val mockCallback = spyk<(B2BPasswordDiscoveryResetByEmailResponse) -> Unit>()
        impl.discovery.resetByEmail(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(mockkResponse) }
    }

    @Test
    fun `PasswordsImpl DiscoveryImpl authenticate delegates to api`() =
        runBlocking {
            val mockkResponse =
                StytchResult.Success<B2BPasswordDiscoveryAuthenticateResponseData>(
                    mockk(relaxed = true),
                )
            every { mockSessionStorage.intermediateSessionToken } returns ""
            coEvery { mockDiscoveryApi.authenticate(any(), any()) } returns mockkResponse
            val response = impl.discovery.authenticate(mockk(relaxed = true))
            assert(response is StytchResult.Success)
            coVerify { mockDiscoveryApi.authenticate(any(), any()) }
        }

    @Test
    fun `PasswordsImpl DiscoveryImpl authenticate with callback calls callback method`() {
        val mockkResponse = StytchResult.Success<B2BPasswordDiscoveryAuthenticateResponseData>(mockk(relaxed = true))
        coEvery { mockDiscoveryApi.authenticate(any(), any()) } returns mockkResponse
        val mockCallback = spyk<(B2BPasswordDiscoveryAuthenticateResponse) -> Unit>()
        impl.discovery.authenticate(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(mockkResponse) }
    }
}
