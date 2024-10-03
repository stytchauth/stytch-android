package com.stytch.sdk.consumer.sessions

import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchFailedToDecryptDataError
import com.stytch.sdk.common.errors.StytchInternalError
import com.stytch.sdk.common.sessions.SessionAutoUpdater
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.network.models.AuthData
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

internal class SessionsImplTest {
    @MockK
    private lateinit var mockApi: StytchApi.Sessions

    @MockK
    private lateinit var mockSessionStorage: ConsumerSessionStorage

    private lateinit var impl: SessionsImpl
    private val dispatcher = Dispatchers.Unconfined

    private val successfulAuthResponse = StytchResult.Success<AuthData>(mockk(relaxed = true))
    private val authParameters = Sessions.AuthParams(sessionDurationMinutes = 30)

    @Before
    fun before() {
        MockKAnnotations.init(this, true, true)
        mockkStatic(KeyStore::class)
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any()) } returns Unit
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(SessionAutoUpdater)
        mockkStatic("com.stytch.sdk.consumer.extensions.StytchResultExtKt")
        every { SessionAutoUpdater.startSessionUpdateJob(any(), any(), any()) } just runs
        every { mockSessionStorage.userFlow } returns mockk(relaxed = true)
        every { mockSessionStorage.sessionFlow } returns mockk(relaxed = true)
        impl =
            SessionsImpl(
                externalScope = TestScope(),
                dispatchers = StytchDispatchers(dispatcher, dispatcher),
                sessionStorage = mockSessionStorage,
                api = mockApi,
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
    fun `SessionsImpl authenticate delegates to api`() =
        runBlocking {
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
    fun `SessionsImpl revoke delegates to api`() =
        runBlocking {
            coEvery { mockApi.revoke() } returns mockk()
            impl.revoke()
            coVerify { mockApi.revoke() }
        }

    @Test
    fun `SessionsImpl revoke does not revoke a local session if a network error occurs and forceClear is not true`() =
        runBlocking {
            coEvery { mockApi.revoke() } returns StytchResult.Error(mockk(relaxed = true))
            impl.revoke()
            verify(exactly = 0) { mockSessionStorage.revoke() }
        }

    @Test
    fun `SessionsImpl revoke does revoke a local session if a network error occurs and forceClear is true`() =
        runBlocking {
            coEvery { mockApi.revoke() } returns StytchResult.Error(mockk(relaxed = true))
            impl.revoke(Sessions.RevokeParams(true))
            verify { mockSessionStorage.revoke() }
        }

    @Test
    fun `SessionsImpl revoke returns error if sessionStorage revoke fails`() =
        runBlocking {
            coEvery { mockApi.revoke() } returns StytchResult.Success(mockk(relaxed = true))
            every { mockSessionStorage.revoke() } throws RuntimeException("Test")
            val result = impl.revoke(Sessions.RevokeParams(true))
            assert(result is StytchResult.Error)
            verify(exactly = 1) { mockSessionStorage.revoke() }
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
    fun `SessionsImpl updateSession throws StytchInternalError exception when sessionstorage fails`() {
        every { mockSessionStorage.updateSession(any(), any()) } throws RuntimeException("Test")
        impl.updateSession("token", "jwt")
    }
}
