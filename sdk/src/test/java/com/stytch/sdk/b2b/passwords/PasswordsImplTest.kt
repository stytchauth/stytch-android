package com.stytch.sdk.b2b.passwords

import com.stytch.sdk.b2b.AuthResponse
import com.stytch.sdk.b2b.EmailResetResponse
import com.stytch.sdk.b2b.PasswordStrengthCheckResponse
import com.stytch.sdk.b2b.SessionResetResponse
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.network.models.B2BAuthData
import com.stytch.sdk.b2b.network.models.EmailResetResponseData
import com.stytch.sdk.b2b.network.models.PasswordsAuthenticateResponseData
import com.stytch.sdk.b2b.network.models.SessionResetResponseData
import com.stytch.sdk.b2b.network.models.StrengthCheckResponseData
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.models.BasicData
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.security.KeyStore

@OptIn(ExperimentalCoroutinesApi::class)
internal class PasswordsImplTest {
    @MockK
    private lateinit var mockApi: StytchB2BApi.Passwords

    @MockK
    private lateinit var mockSessionStorage: B2BSessionStorage

    @MockK
    private lateinit var mockStorageHelper: StorageHelper

    private lateinit var impl: PasswordsImpl
    private val dispatcher = Dispatchers.Unconfined

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any()) } returns Unit
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(StorageHelper)
        mockkObject(SessionAutoUpdater)
        mockkStatic("com.stytch.sdk.b2b.extensions.StytchResultExtKt")
        every { SessionAutoUpdater.startSessionUpdateJob(any(), any(), any()) } just runs
        MockKAnnotations.init(this, true, true)
        impl =
            PasswordsImpl(
                externalScope = TestScope(),
                dispatchers = StytchDispatchers(dispatcher, dispatcher),
                sessionStorage = mockSessionStorage,
                storageHelper = mockStorageHelper,
                api = mockApi,
            )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `PasswordsImpl authenticate delegates to api`() =
        runTest {
            val mockkResponse = StytchResult.Success<PasswordsAuthenticateResponseData>(mockk(relaxed = true))
            coEvery { mockApi.authenticate(any(), any(), any(), any()) } returns mockkResponse
            val response = impl.authenticate(mockk(relaxed = true))
            assert(response is StytchResult.Success)
            coVerify { mockApi.authenticate(any(), any(), any(), any()) }
            verify { mockkResponse.launchSessionUpdater(any(), any()) }
        }

    @Test
    fun `PasswordsImpl authenticate with callback calls callback method`() {
        val mockkResponse = StytchResult.Success<PasswordsAuthenticateResponseData>(mockk(relaxed = true))
        coEvery { mockApi.authenticate(any(), any(), any(), any()) } returns mockkResponse
        val mockCallback = spyk<(AuthResponse) -> Unit>()
        impl.authenticate(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(mockkResponse) }
    }

    @Test
    fun `PasswordsImpl resetByEmailStart returns error if generateHashedCodeChallenge fails`() =
        runTest {
            every { mockStorageHelper.generateHashedCodeChallenge() } throws RuntimeException("Test")
            val response = impl.resetByEmailStart(mockk(relaxed = true))
            assert(response is StytchResult.Error)
        }

    @Test
    fun `PasswordsImpl resetByEmailStart delegates to api`() =
        runTest {
            every { mockStorageHelper.generateHashedCodeChallenge() } returns Pair("", "")
            val mockkResponse = StytchResult.Success<BasicData>(mockk(relaxed = true))
            coEvery { mockApi.resetByEmailStart(any(), any(), any(), any(), any(), any(), any()) } returns mockkResponse
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
            coVerify { mockApi.resetByEmailStart(any(), any(), any(), any(), any(), any(), any()) }
        }

    @Test
    fun `PasswordsImpl resetByEmailStart with callback calls callback method`() {
        every { mockStorageHelper.generateHashedCodeChallenge() } returns Pair("", "")
        val mockkResponse = StytchResult.Success<BasicData>(mockk(relaxed = true))
        coEvery { mockApi.resetByEmailStart(any(), any(), any(), any(), any(), any(), any()) } returns mockkResponse
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
        runTest {
            every { mockStorageHelper.loadValue(any()) } returns null
            val response = impl.resetByEmail(mockk(relaxed = true))
            assert(response is StytchResult.Error)
        }

    @Test
    fun `PasswordsImpl resetByEmail delegates to api`() =
        runTest {
            every { mockStorageHelper.retrieveCodeVerifier() } returns ""
            val mockkResponse = StytchResult.Success<EmailResetResponseData>(mockk(relaxed = true))
            coEvery { mockApi.resetByEmail(any(), any(), any(), any()) } returns mockkResponse
            val response = impl.resetByEmail(mockk(relaxed = true))
            assert(response is StytchResult.Success)
            coVerify { mockApi.resetByEmail(any(), any(), any(), any()) }
            verify { mockkResponse.launchSessionUpdater(any(), any()) }
        }

    @Test
    fun `PasswordsImpl resetByEmail with callback calls callback method`() {
        every { mockStorageHelper.retrieveCodeVerifier() } returns ""
        val mockkResponse = StytchResult.Success<EmailResetResponseData>(mockk(relaxed = true))
        coEvery { mockApi.resetByEmail(any(), any(), any(), any()) } returns mockkResponse
        val mockCallback = spyk<(EmailResetResponse) -> Unit>()
        impl.resetByEmail(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(mockkResponse) }
        verify { mockkResponse.launchSessionUpdater(any(), any()) }
    }

    @Test
    fun `PasswordsImpl resetByExisting delegates to api`() =
        runTest {
            val mockkResponse = StytchResult.Success<B2BAuthData>(mockk(relaxed = true))
            coEvery { mockApi.resetByExisting(any(), any(), any(), any(), any()) } returns mockkResponse
            val response = impl.resetByExisting(mockk(relaxed = true))
            assert(response is StytchResult.Success)
            coVerify { mockApi.resetByExisting(any(), any(), any(), any(), any()) }
            verify { mockkResponse.launchSessionUpdater(any(), any()) }
        }

    @Test
    fun `PasswordsImpl resetByExisting with callback calls callback method`() {
        val mockkResponse = StytchResult.Success<B2BAuthData>(mockk(relaxed = true))
        coEvery { mockApi.resetByExisting(any(), any(), any(), any(), any()) } returns mockkResponse
        val mockCallback = spyk<(AuthResponse) -> Unit>()
        impl.resetByExisting(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(mockkResponse) }
    }

    @Test
    fun `PasswordsImpl resetBySession delegates to api`() =
        runTest {
            val mockkResponse = StytchResult.Success<SessionResetResponseData>(mockk(relaxed = true))
            coEvery { mockApi.resetBySession(any(), any()) } returns mockkResponse
            val response = impl.resetBySession(mockk(relaxed = true))
            assert(response is StytchResult.Success)
            coVerify { mockApi.resetBySession(any(), any()) }
        }

    @Test
    fun `PasswordsImpl resetBySession with callback calls callback method`() {
        val mockkResponse = StytchResult.Success<SessionResetResponseData>(mockk(relaxed = true))
        coEvery { mockApi.resetBySession(any(), any()) } returns mockkResponse
        val mockCallback = spyk<(SessionResetResponse) -> Unit>()
        impl.resetBySession(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(mockkResponse) }
    }

    @Test
    fun `PasswordsImpl strengthCheck delegates to api`() =
        runTest {
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
}
