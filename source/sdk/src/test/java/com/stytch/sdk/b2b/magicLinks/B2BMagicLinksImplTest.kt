package com.stytch.sdk.b2b.magicLinks

import com.stytch.sdk.b2b.DiscoveryEMLAuthResponse
import com.stytch.sdk.b2b.EMLAuthenticateResponse
import com.stytch.sdk.b2b.MemberResponse
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.network.models.B2BEMLAuthenticateData
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.PKCECodePair
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
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
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.security.KeyStore

internal class B2BMagicLinksImplTest {
    @MockK
    private lateinit var mockEmailApi: StytchB2BApi.MagicLinks.Email

    @MockK
    private lateinit var mockDiscoveryApi: StytchB2BApi.MagicLinks.Discovery

    @MockK
    private lateinit var mockB2BSessionStorage: B2BSessionStorage

    @MockK
    private lateinit var mockPKCEPairManager: PKCEPairManager

    private lateinit var impl: B2BMagicLinksImpl
    private val dispatcher = Dispatchers.Unconfined
    private val successfulAuthResponse = StytchResult.Success<B2BEMLAuthenticateData>(mockk(relaxed = true))
    private val authParameters = mockk<B2BMagicLinks.AuthParameters>(relaxed = true)
    private val emailMagicLinkParameters = mockk<B2BMagicLinks.EmailMagicLinks.Parameters>(relaxed = true)
    private val emailInviteParameters = mockk<B2BMagicLinks.EmailMagicLinks.InviteParameters>(relaxed = true)
    private val mockBaseResponse = mockk<BaseResponse>()
    private val mockMemberResponse = mockk<MemberResponse>()

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any()) } returns Unit
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        MockKAnnotations.init(this, true, true)
        mockkObject(SessionAutoUpdater)
        mockkStatic("com.stytch.sdk.b2b.extensions.StytchResultExtKt")
        every { SessionAutoUpdater.startSessionUpdateJob(any(), any(), any()) } just runs
        every { mockB2BSessionStorage.intermediateSessionToken } returns ""
        every { mockPKCEPairManager.clearPKCECodePair() } just runs
        impl =
            B2BMagicLinksImpl(
                externalScope = TestScope(),
                dispatchers = StytchDispatchers(dispatcher, dispatcher),
                sessionStorage = mockB2BSessionStorage,
                emailApi = mockEmailApi,
                discoveryApi = mockDiscoveryApi,
                pkcePairManager = mockPKCEPairManager,
            )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `MagicLinksImpl authenticate delegates to api when code verifier is found`() =
        runTest {
            every { mockPKCEPairManager.getPKCECodePair() } returns mockk(relaxed = true)
            coEvery { mockEmailApi.authenticate(any(), any(), any(), any()) } returns successfulAuthResponse
            val response = impl.authenticate(authParameters)
            assert(response is StytchResult.Success)
            coVerify { mockEmailApi.authenticate(any(), any(), any(), any()) }
            verify { successfulAuthResponse.launchSessionUpdater(any(), any()) }
            verify(exactly = 1) { mockPKCEPairManager.clearPKCECodePair() }
        }

    @Test
    fun `MagicLinksImpl authenticate delegates to api when code verifier is not found`() =
        runTest {
            every { mockPKCEPairManager.getPKCECodePair() } returns null
            coEvery { mockEmailApi.authenticate(any(), any(), any(), any()) } returns successfulAuthResponse
            val response = impl.authenticate(authParameters)
            assert(response is StytchResult.Success)
            coVerify { mockEmailApi.authenticate(any(), any(), any(), any()) }
            verify { successfulAuthResponse.launchSessionUpdater(any(), any()) }
            verify(exactly = 1) { mockPKCEPairManager.clearPKCECodePair() }
        }

    @Test
    fun `MagicLinksImpl authenticate with callback calls callback method`() {
        every { mockPKCEPairManager.getPKCECodePair() } returns null
        coEvery { mockEmailApi.authenticate(any(), any(), any(), any()) } returns successfulAuthResponse
        val mockCallback = spyk<(EMLAuthenticateResponse) -> Unit>()
        impl.authenticate(authParameters, mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `MagicLinksImpl email loginOrCreate returns error if generateCodeChallenge fails`() =
        runTest {
            every { mockPKCEPairManager.generateAndReturnPKCECodePair() } throws RuntimeException("Test")
            val response = impl.email.loginOrSignup(emailMagicLinkParameters)
            assert(response is StytchResult.Error)
        }

    @Test
    fun `MagicLinksImpl email loginOrCreate delegates to api`() =
        runTest {
            every { mockPKCEPairManager.generateAndReturnPKCECodePair() } returns PKCECodePair("", "")
            coEvery {
                mockEmailApi.loginOrSignupByEmail(any(), any(), any(), any(), any(), any(), any(), any())
            } returns mockBaseResponse
            impl.email.loginOrSignup(emailMagicLinkParameters)
            coVerify { mockEmailApi.loginOrSignupByEmail(any(), any(), any(), any(), any(), any(), any(), any()) }
        }

    @Test
    fun `MagicLinksImpl email loginOrCreate with callback calls callback method`() {
        val mockCallback = spyk<(BaseResponse) -> Unit>()
        impl.email.loginOrSignup(emailMagicLinkParameters, mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `MagicLinksImpl email invite delegates to api`() =
        runTest {
            coEvery {
                mockEmailApi.invite(any(), any(), any(), any(), any(), any(), any())
            } returns mockMemberResponse
            impl.email.invite(emailInviteParameters)
            coVerify { mockEmailApi.invite(any(), any(), any(), any(), any(), any(), any()) }
        }

    @Test
    fun `MagicLinksImpl email invite with callback calls callback method`() {
        coEvery {
            mockEmailApi.invite(any(), any(), any(), any(), any(), any(), any())
        } returns mockMemberResponse
        val mockCallback = spyk<(MemberResponse) -> Unit>()
        impl.email.invite(emailInviteParameters, mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `MagicLinksImpl email sendDiscovery returns error if generateCodeChallenge fails`() =
        runTest {
            every { mockPKCEPairManager.generateAndReturnPKCECodePair() } throws RuntimeException("Test")
            val response = impl.email.discoverySend(mockk(relaxed = true))
            assert(response is StytchResult.Error)
        }

    @Test
    fun `MagicLinksImpl discovery send delegates to api`() =
        runTest {
            every { mockPKCEPairManager.generateAndReturnPKCECodePair() } returns PKCECodePair("", "")
            coEvery { mockDiscoveryApi.send(any(), any(), any(), any(), any()) } returns mockBaseResponse
            impl.email.discoverySend(mockk(relaxed = true))
            coVerify { mockDiscoveryApi.send(any(), any(), any(), any(), any()) }
        }

    @Test
    fun `MagicLinksImpl discovery send with callback calls callback method`() {
        val mockCallback = spyk<(BaseResponse) -> Unit>()
        impl.email.discoverySend(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `MagicLinksImpl discovery authenticate returns error if retrieveCodeVerifier fails`() =
        runTest {
            every { mockPKCEPairManager.getPKCECodePair() } returns null
            val response = impl.discoveryAuthenticate(mockk(relaxed = true))
            assert(response is StytchResult.Error)
        }

    @Test
    fun `MagicLinksImpl discovery authenticate delegates to api`() =
        runTest {
            every { mockPKCEPairManager.getPKCECodePair() } returns mockk(relaxed = true)
            coEvery { mockDiscoveryApi.authenticate(any(), any()) } returns mockk(relaxed = true)
            impl.discoveryAuthenticate(mockk(relaxed = true))
            coVerify { mockDiscoveryApi.authenticate(any(), any()) }
            verify(exactly = 1) { mockPKCEPairManager.clearPKCECodePair() }
        }

    @Test
    fun `MagicLinksImpl discovery authenticate with callback calls callback method`() {
        every { mockPKCEPairManager.getPKCECodePair() } returns null
        val mockCallback = spyk<(DiscoveryEMLAuthResponse) -> Unit>()
        impl.discoveryAuthenticate(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }
}
