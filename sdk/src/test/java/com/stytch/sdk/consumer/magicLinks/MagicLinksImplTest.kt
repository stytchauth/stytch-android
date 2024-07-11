package com.stytch.sdk.consumer.magicLinks

import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.PKCECodePair
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.models.BasicData
import com.stytch.sdk.common.pkcePairManager.PKCEPairManager
import com.stytch.sdk.common.sessions.SessionAutoUpdater
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.network.models.AuthData
import com.stytch.sdk.consumer.sessions.ConsumerSessionStorage
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

internal class MagicLinksImplTest {
    @MockK
    private lateinit var mockApi: StytchApi.MagicLinks.Email

    @MockK
    private lateinit var mockSessionStorage: ConsumerSessionStorage

    @MockK
    private lateinit var mockPKCEPairManager: PKCEPairManager

    private lateinit var impl: MagicLinksImpl
    private val dispatcher = Dispatchers.Unconfined
    private val successfulAuthResponse = StytchResult.Success<AuthData>(mockk(relaxed = true))
    private val successfulBaseResponse = StytchResult.Success<BasicData>(mockk(relaxed = true))
    private val authParameters = mockk<MagicLinks.AuthParameters>(relaxed = true)
    private val emailMagicLinkParameters = mockk<MagicLinks.EmailMagicLinks.Parameters>(relaxed = true)
    private val successfulLoginOrCreateResponse = mockk<BaseResponse>()

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any()) } returns Unit
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(StorageHelper)
        MockKAnnotations.init(this, true, true)
        mockkObject(SessionAutoUpdater)
        mockkStatic("com.stytch.sdk.consumer.extensions.StytchResultExtKt")
        every { SessionAutoUpdater.startSessionUpdateJob(any(), any(), any()) } just runs
        every { mockPKCEPairManager.clearPKCECodePair() } just runs
        impl =
            MagicLinksImpl(
                externalScope = TestScope(),
                dispatchers = StytchDispatchers(dispatcher, dispatcher),
                sessionStorage = mockSessionStorage,
                pkcePairManager = mockPKCEPairManager,
                api = mockApi,
            )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `MagicLinksImpl authenticate returns error if codeverifier fails`() =
        runTest {
            val response = impl.authenticate(authParameters)
            assert(response is StytchResult.Error)
        }

    @Test
    fun `MagicLinksImpl authenticate delegates to api`() =
        runTest {
            every { mockPKCEPairManager.getPKCECodePair() } returns PKCECodePair("", "")
            coEvery { mockApi.authenticate(any(), any(), any()) } returns successfulAuthResponse
            val response = impl.authenticate(authParameters)
            assert(response is StytchResult.Success)
            coVerify { mockApi.authenticate(any(), any(), any()) }
            verify { successfulAuthResponse.launchSessionUpdater(any(), any()) }
            verify(exactly = 1) { mockPKCEPairManager.clearPKCECodePair() }
        }

    @Test
    fun `MagicLinksImpl authenticate with callback calls callback method`() {
        val mockCallback = spyk<(AuthResponse) -> Unit>()
        impl.authenticate(authParameters, mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `MagicLinksImpl email loginOrCreate returns error if generateCodeChallenge fails`() =
        runTest {
            every { mockPKCEPairManager.generateAndReturnPKCECodePair() } throws RuntimeException("Test")
            val response = impl.email.loginOrCreate(emailMagicLinkParameters)
            assert(response is StytchResult.Error)
        }

    @Test
    fun `MagicLinksImpl email loginOrCreate delegates to api`() =
        runTest {
            every { mockPKCEPairManager.generateAndReturnPKCECodePair() } returns PKCECodePair("", "")
            coEvery {
                mockApi.loginOrCreate(any(), any(), any(), any(), any(), any())
            } returns successfulLoginOrCreateResponse
            impl.email.loginOrCreate(emailMagicLinkParameters)
            coVerify { mockApi.loginOrCreate(any(), any(), any(), any(), any(), any()) }
        }

    @Test
    fun `MagicLinksImpl email loginOrCreate with callback calls callback method`() {
        val mockCallback = spyk<(BaseResponse) -> Unit>()
        impl.email.loginOrCreate(emailMagicLinkParameters, mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `MagicLinksImpl email send with active session delegates to api`() =
        runTest {
            every { mockSessionStorage.persistedSessionIdentifiersExist } returns true
            coEvery {
                mockApi.sendSecondary(any(), any(), any(), any(), any(), any(), any(), any())
            } returns successfulBaseResponse
            every { mockPKCEPairManager.generateAndReturnPKCECodePair() } returns PKCECodePair("", "")
            val response =
                impl.email.send(
                    MagicLinks.EmailMagicLinks.Parameters(email = "emailAddress"),
                )
            assert(response is StytchResult.Success)
            coVerify {
                mockApi.sendSecondary(any(), any(), any(), any(), any(), any(), any(), any())
            }
        }

    @Test
    fun `MagicLinksImpl email send with no active session delegates to api`() =
        runTest {
            every { mockSessionStorage.persistedSessionIdentifiersExist } returns false
            coEvery {
                mockApi.sendPrimary(any(), any(), any(), any(), any(), any(), any(), any())
            } returns successfulBaseResponse
            every { mockPKCEPairManager.generateAndReturnPKCECodePair() } returns PKCECodePair("", "")
            val response =
                impl.email.send(
                    MagicLinks.EmailMagicLinks.Parameters(email = "emailAddress"),
                )
            assert(response is StytchResult.Success)
            coVerify {
                mockApi.sendPrimary(any(), any(), any(), any(), any(), any(), any(), any())
            }
        }

    @Test
    fun `MagicLinksImpl email send with callback calls callback method`() {
        every { mockSessionStorage.persistedSessionIdentifiersExist } returns false
        coEvery {
            mockApi.sendPrimary(any(), any(), any(), any(), any(), any(), any(), any())
        } returns successfulBaseResponse
        val mockCallback = spyk<(BaseResponse) -> Unit>()
        impl.email.send(MagicLinks.EmailMagicLinks.Parameters(email = "emailAddress"), mockCallback)
        verify { mockCallback.invoke(any()) }
    }
}
