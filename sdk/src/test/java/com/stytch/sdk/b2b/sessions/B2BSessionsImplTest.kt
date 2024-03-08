package com.stytch.sdk.b2b.sessions

import com.stytch.sdk.b2b.AuthResponse
import com.stytch.sdk.b2b.SessionExchangeResponse
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.network.models.B2BSessionData
import com.stytch.sdk.b2b.network.models.IB2BAuthData
import com.stytch.sdk.b2b.network.models.SessionExchangeResponseData
import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchFailedToDecryptDataError
import com.stytch.sdk.common.errors.StytchInternalError
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
import java.security.KeyStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class B2BSessionsImplTest {
    @MockK
    private lateinit var mockApi: StytchB2BApi.Sessions

    @MockK
    private lateinit var mockSessionStorage: B2BSessionStorage

    private lateinit var impl: B2BSessionsImpl
    private val dispatcher = Dispatchers.Unconfined

    private val successfulAuthResponse = StytchResult.Success<IB2BAuthData>(mockk(relaxed = true))
    private val authParameters = B2BSessions.AuthParams(sessionDurationMinutes = 30U)
    private val successfulExchangeResponse = StytchResult.Success<SessionExchangeResponseData>(mockk(relaxed = true))

    @Before
    fun before() {
        MockKAnnotations.init(this, true, true)
        mockkStatic(KeyStore::class)
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any()) } returns Unit
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(SessionAutoUpdater)
        mockkStatic("com.stytch.sdk.b2b.extensions.StytchResultExtKt")
        every { SessionAutoUpdater.startSessionUpdateJob(any(), any(), any()) } just runs
        impl = B2BSessionsImpl(
            externalScope = TestScope(),
            dispatchers = StytchDispatchers(dispatcher, dispatcher),
            sessionStorage = mockSessionStorage,
            api = mockApi
        )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test(expected = StytchFailedToDecryptDataError::class)
    fun `SessionsImpl sessionToken throws exception if fails to retrieve token`() {
        impl.sessionToken
    }

    @Test
    fun `SessionsImpl sessionToken returns expected token from storage`() {
        every { mockSessionStorage.sessionToken } returns "sessionToken"
        assert(impl.sessionToken == "sessionToken")
    }

    @Test(expected = StytchFailedToDecryptDataError::class)
    fun `SessionsImpl sessionJwt throws exception if fails to retrieve token`() {
        impl.sessionJwt
    }

    @Test
    fun `SessionsImpl sessionJwt returns expected token from storage`() {
        every { mockSessionStorage.sessionJwt } returns "sessionJwt"
        assert(impl.sessionJwt == "sessionJwt")
    }

    @Test
    fun `SessionsImpl authenticate delegates to api`() = runTest {
        coEvery { mockApi.authenticate(any()) } returns successfulAuthResponse
        val response = impl.authenticate(authParameters)
        assert(response is StytchResult.Success)
        coVerify { mockApi.authenticate(any()) }
        verify { successfulAuthResponse.launchSessionUpdater(any(), any()) }
    }

    @Test
    fun `SessionsImpl authenticate with callback calls callback method`() {
        coEvery { mockApi.authenticate(any()) } returns successfulAuthResponse
        val mockCallback = spyk<(AuthResponse) -> Unit>()
        impl.authenticate(authParameters, mockCallback)
        verify { mockCallback.invoke(eq(successfulAuthResponse)) }
    }

    @Test
    fun `SessionsImpl revoke delegates to api`() = runTest {
        coEvery { mockApi.revoke() } returns mockk()
        impl.revoke()
        coVerify { mockApi.revoke() }
    }

    @Test
    fun `SessionsImpl revoke does not revoke a local session if a network error occurs and forceClear is not true`() =
        runTest {
            coEvery { mockApi.revoke() } returns StytchResult.Error(mockk(relaxed = true))
            impl.revoke()
            verify(exactly = 0) { mockSessionStorage.revoke() }
        }

    @Test
    fun `SessionsImpl revoke does revoke a local session if a network error occurs and forceClear is true`() =
        runTest {
            coEvery { mockApi.revoke() } returns StytchResult.Error(mockk(relaxed = true))
            impl.revoke(B2BSessions.RevokeParams(true))
            verify { mockSessionStorage.revoke() }
        }

    @Test
    fun `SessionsImpl revoke returns error if sessionStorage revoke fails`() = runTest {
        coEvery { mockApi.revoke() } returns StytchResult.Success(mockk(relaxed = true))
        every { mockSessionStorage.revoke() } throws RuntimeException("Test")
        val result = impl.revoke(B2BSessions.RevokeParams(true))
        verify(exactly = 1) { mockSessionStorage.revoke() }
        assert(result is StytchResult.Error)
    }

    @Test
    fun `SessionsImpl revoke with callback calls callback method`() {
        coEvery { mockApi.revoke() } returns mockk()
        val mockCallback = spyk<(BaseResponse) -> Unit>()
        impl.revoke(callback = mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `SessionsImpl updateSession delegates to sessionStorage`() {
        every { mockSessionStorage.updateSession(any(), any()) } just runs
        impl.updateSession("token", "jwt")
        verify { mockSessionStorage.updateSession("token", "jwt") }
    }

    @Test(expected = StytchInternalError::class)
    fun `SessionsImpl updateSession throws StytchInternalError when sessionstorage fails`() {
        every { mockSessionStorage.updateSession(any(), any()) } throws RuntimeException("Test")
        impl.updateSession("token", "jwt")
    }

    @Test
    fun `SessionsImpl getSync delegates to sessionStorage`() {
        val mockSession: B2BSessionData = mockk()
        every { mockSessionStorage.memberSession } returns mockSession
        val session = impl.getSync()
        assert(session == mockSession)
        verify { mockSessionStorage.memberSession }
    }

    @Test
    fun `SessionsImpl exchange delegates to the api`() = runTest {
        coEvery { mockApi.exchange(any(), any()) } returns successfulExchangeResponse
        impl.exchange(B2BSessions.ExchangeParameters(organizationId = "test-123", sessionDurationMinutes = 30U))
        coVerify(exactly = 1) { mockApi.exchange(any(), any()) }
        verify { successfulExchangeResponse.launchSessionUpdater(any(), any()) }
    }

    @Test
    fun `SessionsImpl exchange with callback calls callback method`() {
        coEvery { mockApi.exchange(any(), any()) } returns successfulExchangeResponse
        val mockCallback = spyk<(SessionExchangeResponse) -> Unit>()
        impl.exchange(
            B2BSessions.ExchangeParameters(
                organizationId = "test-123",
                sessionDurationMinutes = 30U
            ),
            callback = mockCallback
        )
        verify { mockCallback.invoke(any()) }
    }
}
