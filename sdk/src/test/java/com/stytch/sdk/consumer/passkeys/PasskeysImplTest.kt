package com.stytch.sdk.consumer.passkeys

import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchPasskeysNotSupportedError
import com.stytch.sdk.common.sessions.SessionAutoUpdater
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.WebAuthnRegisterResponse
import com.stytch.sdk.consumer.WebAuthnUpdateResponse
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.network.StytchApi
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
import java.security.KeyStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class PasskeysImplTest {
    @MockK
    private lateinit var mockApi: StytchApi.WebAuthn

    @MockK
    private lateinit var mockSessionStorage: ConsumerSessionStorage

    @MockK
    private lateinit var mockPasskeysProvider: PasskeysProvider

    private lateinit var impl: PasskeysImpl
    private val dispatcher = Dispatchers.Unconfined

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        mockkStatic(
            "com.stytch.sdk.consumer.extensions.StytchResultExtKt"
        )
        MockKAnnotations.init(this, true, true)
        mockkObject(SessionAutoUpdater)
        every { SessionAutoUpdater.startSessionUpdateJob(any(), any(), any()) } just runs
        impl = spyk(
            PasskeysImpl(
                externalScope = TestScope(),
                dispatchers = StytchDispatchers(dispatcher, dispatcher),
                sessionStorage = mockSessionStorage,
                api = mockApi,
                provider = mockPasskeysProvider,
            ),
            recordPrivateCalls = true
        )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `register returns error if passkeys are unsupported`() = runTest {
        every { impl.isSupported } returns false
        val result = impl.register(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception is StytchPasskeysNotSupportedError)
    }

    @Test
    fun `register returns error if registerStart api call fails`() = runTest {
        every { impl.isSupported } returns true
        coEvery { mockApi.registerStart(any(), any(), any(), any()) } returns StytchResult.Error(mockk())
        val result = impl.register(mockk(relaxed = true))
        assert(result is StytchResult.Error)
        coVerify(exactly = 1) { mockApi.registerStart(any(), any(), any(), any()) }
        coVerify(exactly = 0) { mockPasskeysProvider.createPublicKeyCredential(any(), any(), any()) }
        coVerify(exactly = 0) { mockApi.register(any()) }
    }

    @Test
    fun `register returns error if createPublicKeyCredential call fails`() = runTest {
        every { impl.isSupported } returns true
        coEvery {
            mockApi.registerStart(any(), any(), any(), any())
        } returns StytchResult.Success(mockk(relaxed = true))
        coEvery { mockPasskeysProvider.createPublicKeyCredential(any(), any(), any()) } throws Exception()
        val result = impl.register(mockk(relaxed = true))
        assert(result is StytchResult.Error)
        coVerify(exactly = 1) { mockApi.registerStart(any(), any(), any(), any()) }
        coVerify(exactly = 1) { mockPasskeysProvider.createPublicKeyCredential(any(), any(), any()) }
        coVerify(exactly = 0) { mockApi.register(any()) }
    }

    @Test
    fun `register returns error if register api call fails`() = runTest {
        every { impl.isSupported } returns true
        coEvery {
            mockApi.registerStart(any(), any(), any(), any())
        } returns StytchResult.Success(mockk(relaxed = true))
        coEvery { mockPasskeysProvider.createPublicKeyCredential(any(), any(), any()) } returns mockk(relaxed = true)
        coEvery { mockApi.register(any()) } returns StytchResult.Error(mockk(relaxed = true))
        val result = impl.register(mockk(relaxed = true))
        assert(result is StytchResult.Error)
        coVerify(exactly = 1) { mockApi.registerStart(any(), any(), any(), any()) }
        coVerify(exactly = 1) { mockPasskeysProvider.createPublicKeyCredential(any(), any(), any()) }
        coVerify(exactly = 1) { mockApi.register(any()) }
    }

    @Test
    fun `register calls launchSessionUpdater and returns success if registration flow succeeds`() = runTest {
        every { impl.isSupported } returns true
        coEvery {
            mockApi.registerStart(any(), any(), any(), any())
        } returns StytchResult.Success(mockk(relaxed = true))
        coEvery { mockPasskeysProvider.createPublicKeyCredential(any(), any(), any()) } returns mockk(relaxed = true)
        val mockSuccessResponse = mockk<WebAuthnRegisterResponse>(relaxed = true)
        coEvery { mockApi.register(any()) } returns mockSuccessResponse
        every { mockSuccessResponse.launchSessionUpdater(any(), any()) } just runs
        impl.register(mockk(relaxed = true))
        coVerify(exactly = 1) { mockApi.registerStart(any(), any(), any(), any()) }
        coVerify(exactly = 1) { mockPasskeysProvider.createPublicKeyCredential(any(), any(), any()) }
        coVerify(exactly = 1) { mockApi.register(any()) }
        verify(exactly = 1) { mockSuccessResponse.launchSessionUpdater(any(), any()) }
    }

    @Test
    fun `register with callback calls callback method`() {
        // short circuit on the first internal check, since we just care that the callback method is called
        val mockCallback = spyk<(WebAuthnRegisterResponse) -> Unit>()
        impl.register(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `authenticate returns error if passkeys are unsupported`() = runTest {
        every { impl.isSupported } returns false
        val result = impl.authenticate(mockk())
        require(result is StytchResult.Error)
        assert(result.exception is StytchPasskeysNotSupportedError)
    }

    @Test
    fun `authenticate returns error if authenticateStartSecondary api call fails`() = runTest {
        every { impl.isSupported } returns true
        every { mockSessionStorage.persistedSessionIdentifiersExist } returns true
        coEvery { mockApi.authenticateStartSecondary(any(), any()) } returns StytchResult.Error(mockk())
        val result = impl.authenticate(mockk(relaxed = true))
        assert(result is StytchResult.Error)
        coVerify(exactly = 1) { mockApi.authenticateStartSecondary(any(), any()) }
        coVerify(exactly = 0) { mockApi.authenticateStartPrimary(any(), any()) }
        coVerify(exactly = 0) { mockPasskeysProvider.getPublicKeyCredential(any(), any(), any()) }
        coVerify(exactly = 0) { mockApi.authenticate(any(), any()) }
    }

    @Test
    fun `authenticate returns error if authenticateStartPrimary api call fails`() = runTest {
        every { impl.isSupported } returns true
        every { mockSessionStorage.persistedSessionIdentifiersExist } returns false
        coEvery { mockApi.authenticateStartPrimary(any(), any()) } returns StytchResult.Error(mockk())
        val result = impl.authenticate(mockk(relaxed = true))
        assert(result is StytchResult.Error)
        coVerify(exactly = 0) { mockApi.authenticateStartSecondary(any(), any()) }
        coVerify(exactly = 1) { mockApi.authenticateStartPrimary(any(), any()) }
        coVerify(exactly = 0) { mockPasskeysProvider.getPublicKeyCredential(any(), any(), any()) }
        coVerify(exactly = 0) { mockApi.authenticate(any(), any()) }
    }

    @Test
    fun `authenticate returns error if getPublicKeyCredential call fails`() = runTest {
        every { impl.isSupported } returns true
        every { mockSessionStorage.persistedSessionIdentifiersExist } returns false
        coEvery { mockApi.authenticateStartPrimary(any(), any()) } returns StytchResult.Success(mockk(relaxed = true))
        coEvery { mockPasskeysProvider.getPublicKeyCredential(any(), any(), any()) } throws Exception()
        val result = impl.authenticate(mockk(relaxed = true))
        assert(result is StytchResult.Error)
        coVerify(exactly = 0) { mockApi.authenticateStartSecondary(any(), any()) }
        coVerify(exactly = 1) { mockApi.authenticateStartPrimary(any(), any()) }
        coVerify(exactly = 1) { mockPasskeysProvider.getPublicKeyCredential(any(), any(), any()) }
        coVerify(exactly = 0) { mockApi.authenticate(any(), any()) }
    }

    @Test
    fun `authenticate returns error if authenticate api call fails`() = runTest {
        every { impl.isSupported } returns true
        every { mockSessionStorage.persistedSessionIdentifiersExist } returns false
        coEvery { mockApi.authenticateStartPrimary(any(), any()) } returns StytchResult.Success(mockk(relaxed = true))
        coEvery { mockPasskeysProvider.getPublicKeyCredential(any(), any(), any()) } returns mockk(relaxed = true)
        coEvery { mockApi.authenticate(any(), any()) } returns StytchResult.Error(mockk(relaxed = true))
        val result = impl.authenticate(mockk(relaxed = true))
        assert(result is StytchResult.Error)
        coVerify(exactly = 0) { mockApi.authenticateStartSecondary(any(), any()) }
        coVerify(exactly = 1) { mockApi.authenticateStartPrimary(any(), any()) }
        coVerify(exactly = 1) { mockPasskeysProvider.getPublicKeyCredential(any(), any(), any()) }
        coVerify(exactly = 1) { mockApi.authenticate(any(), any()) }
    }

    @Test
    fun `authenticate calls launchSessionUpdater and returns success if authentication flow succeeds`() = runTest {
        every { impl.isSupported } returns true
        every { mockSessionStorage.persistedSessionIdentifiersExist } returns false
        coEvery { mockApi.authenticateStartPrimary(any(), any()) } returns StytchResult.Success(mockk(relaxed = true))
        coEvery { mockPasskeysProvider.getPublicKeyCredential(any(), any(), any()) } returns mockk(relaxed = true)
        val mockSuccessResponse = mockk<AuthResponse>(relaxed = true)
        coEvery { mockApi.authenticate(any(), any()) } returns mockSuccessResponse
        every { mockSuccessResponse.launchSessionUpdater(any(), any()) } just runs
        impl.authenticate(mockk(relaxed = true))
        coVerify(exactly = 0) { mockApi.authenticateStartSecondary(any(), any()) }
        coVerify(exactly = 1) { mockApi.authenticateStartPrimary(any(), any()) }
        coVerify(exactly = 1) { mockPasskeysProvider.getPublicKeyCredential(any(), any(), any()) }
        coVerify(exactly = 1) { mockApi.authenticate(any(), any()) }
        verify(exactly = 1) { mockSuccessResponse.launchSessionUpdater(any(), any()) }
    }

    @Test
    fun `authenticate with callback calls callback method`() {
        // short circuit on the first internal check, since we just care that the callback method is called
        val mockCallback = spyk<(AuthResponse) -> Unit>()
        impl.authenticate(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `update delegates to api`() = runTest {
        coEvery { mockApi.update(any(), any()) } returns mockk(relaxed = true)
        impl.update(Passkeys.UpdateParameters(id = "registration-id", name = "new name"))
        coVerify { mockApi.update("registration-id", "new name") }
    }

    @Test
    fun `update with callback calls callback method`() {
        coEvery { mockApi.update(any(), any()) } returns mockk(relaxed = true)
        val mockCallback = spyk<(WebAuthnUpdateResponse) -> Unit>()
        impl.update(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }
}
