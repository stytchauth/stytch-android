package com.stytch.sdk

import com.stytch.sdk.network.StytchApi
import com.stytch.sdk.network.StytchErrorType
import com.stytch.sdk.network.responseData.BiometricsAuthData
import com.stytch.sessions.SessionAutoUpdater
import com.stytch.sessions.SessionStorage
import com.stytch.sessions.launchSessionUpdater
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
internal class BiometricsImplTest {
    @MockK
    private lateinit var mockApi: StytchApi.Biometrics

    @MockK
    private lateinit var mockSessionStorage: SessionStorage

    @MockK
    private lateinit var mockStorageHelper: StorageHelper

    private lateinit var impl: BiometricsImpl
    private val dispatcher = Dispatchers.Unconfined

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any()) } returns Unit
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(StorageHelper)
        MockKAnnotations.init(this, true, true)
        mockkObject(SessionAutoUpdater)
        every { SessionAutoUpdater.startSessionUpdateJob(any(), any()) } just runs
        impl = BiometricsImpl(
            externalScope = TestScope(),
            dispatchers = StytchDispatchers(dispatcher, dispatcher),
            sessionStorage = mockSessionStorage,
            storageHelper = mockStorageHelper,
            api = mockApi
        )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `register returns correct error if insecure keystore and allowFallbackToCleartext is false`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore(any()) } returns false
        every { mockStorageHelper.deleteEd25519Key() } returns true
        val result = impl.register(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception.reason == StytchErrorType.NOT_USING_KEYSTORE.message)
        verify { mockStorageHelper.deleteEd25519Key() }
    }

    @Test
    fun `register returns correct error if no session is found and removes pending registration`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore(any()) } returns true
        every { mockSessionStorage.sessionToken } returns null
        every { mockSessionStorage.sessionJwt } returns null
        every { mockStorageHelper.deleteEd25519Key() } returns true
        val result = impl.register(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception.reason == StytchErrorType.NO_CURRENT_SESSION.message)
        verify { mockStorageHelper.deleteEd25519Key() }
    }

    @Test
    fun `register returns correct error if no public key is found and removes pending registration`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore(any()) } returns true
        every { mockSessionStorage.sessionToken } returns "sessionToken"
        every { mockSessionStorage.sessionJwt } returns "sessionJwt"
        every { mockStorageHelper.getEd25519PublicKey(any()) } returns null
        every { mockStorageHelper.deleteEd25519Key() } returns true
        val result = impl.register(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception.reason == StytchErrorType.KEY_GENERATION_FAILED.message)
        verify { mockStorageHelper.deleteEd25519Key() }
    }

    @Test
    fun `register returns correct error if registerStart fails and removes pending registration`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore(any()) } returns true
        every { mockSessionStorage.sessionToken } returns "sessionToken"
        every { mockSessionStorage.sessionJwt } returns "sessionJwt"
        every { mockStorageHelper.getEd25519PublicKey(any()) } returns "publicKey"
        every { mockStorageHelper.deleteEd25519Key() } returns true
        coEvery { mockApi.registerStart("publicKey") } returns StytchResult.Error(
            StytchExceptions.Response(mockk(relaxed = true))
        )
        val result = impl.register(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception is StytchExceptions.Response)
        coVerify { mockApi.registerStart("publicKey") }
        verify { mockStorageHelper.deleteEd25519Key() }
    }

    @Test
    fun `register returns correct error if challenge signing fails and removes pending registration`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore(any()) } returns true
        every { mockSessionStorage.sessionToken } returns "sessionToken"
        every { mockSessionStorage.sessionJwt } returns "sessionJwt"
        every { mockStorageHelper.getEd25519PublicKey(any()) } returns "publicKey"
        every { mockStorageHelper.deleteEd25519Key() } returns true
        coEvery { mockApi.registerStart("publicKey") } returns StytchResult.Success(
            mockk {
                every { challenge } returns "challenge"
            }
        )
        every { mockStorageHelper.signEd25519CodeChallenge(any(), "challenge") } returns null
        val result = impl.register(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception is StytchExceptions.Input)
        assert(result.exception.reason == StytchErrorType.ERROR_SIGNING_CHALLENGE.message)
        coVerify { mockApi.registerStart("publicKey") }
        verify { mockStorageHelper.deleteEd25519Key() }
    }

    @Test
    fun `register returns success if everything succeeds`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore(any()) } returns true
        every { mockSessionStorage.sessionToken } returns "sessionToken"
        every { mockSessionStorage.sessionJwt } returns "sessionJwt"
        every { mockStorageHelper.getEd25519PublicKey(any()) } returns "publicKey"
        coEvery { mockApi.registerStart("publicKey") } returns StytchResult.Success(
            mockk {
                every { challenge } returns "challenge"
                every { biometricRegistrationId } returns "biometricRegistrationId"
            }
        )
        every { mockStorageHelper.signEd25519CodeChallenge(any(), "challenge") } returns "signature"
        val mockResponse = mockk<StytchResult.Success<BiometricsAuthData>>(relaxed = true)
        coEvery { mockApi.register("signature", "biometricRegistrationId", 30U) } returns mockResponse
        every { mockResponse.launchSessionUpdater(any(), any()) } just runs
        val result = impl.register(Biometrics.StartParameters(mockk(), 30U))
        assert(result is StytchResult.Success)
        coVerify { mockApi.register("signature", "biometricRegistrationId", 30U) }
        verify { mockResponse.launchSessionUpdater(any(), any()) }
    }

    @Test
    fun `register with callback calls callback method`() {
        // short circuit on the first internal check, since we just care that the callback method is called
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore(any()) } returns false
        every { mockStorageHelper.deleteEd25519Key() } returns true
        val mockCallback = spyk<(BiometricsAuthResponse) -> Unit>()
        impl.register(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `authenticate returns correct error if no public key is found`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore(any()) } returns true
        every { mockStorageHelper.getEd25519PublicKey(any()) } returns null
        val result = impl.authenticate(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception.reason == StytchErrorType.KEY_GENERATION_FAILED.message)
    }

    @Test
    fun `authenticate returns correct error if authenticateStart fails`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore(any()) } returns true
        every { mockStorageHelper.getEd25519PublicKey(any()) } returns "publicKey"
        coEvery { mockApi.authenticateStart("publicKey") } returns StytchResult.Error(
            StytchExceptions.Response(mockk(relaxed = true))
        )
        val result = impl.authenticate(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception is StytchExceptions.Response)
        coVerify { mockApi.authenticateStart("publicKey") }
    }

    @Test
    fun `authenticate returns correct error if challenge signing fails`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore(any()) } returns true
        every { mockStorageHelper.getEd25519PublicKey(any()) } returns "publicKey"
        coEvery { mockApi.authenticateStart("publicKey") } returns StytchResult.Success(
            mockk {
                every { challenge } returns "challenge"
            }
        )
        every { mockStorageHelper.signEd25519CodeChallenge(any(), "challenge") } returns null
        val result = impl.authenticate(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception is StytchExceptions.Input)
        assert(result.exception.reason == StytchErrorType.ERROR_SIGNING_CHALLENGE.message)
        coVerify { mockApi.authenticateStart("publicKey") }
    }

    @Test
    fun `authenticate returns success if everything succeeds`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore(any()) } returns true
        every { mockStorageHelper.getEd25519PublicKey(any()) } returns "publicKey"
        coEvery { mockApi.authenticateStart("publicKey") } returns StytchResult.Success(
            mockk {
                every { challenge } returns "challenge"
                every { biometricRegistrationId } returns "biometricRegistrationId"
            }
        )
        every { mockStorageHelper.signEd25519CodeChallenge(any(), "challenge") } returns "signature"
        val mockResponse = mockk<StytchResult.Success<BiometricsAuthData>>(relaxed = true)
        coEvery { mockApi.authenticate("signature", "biometricRegistrationId", 30U) } returns mockResponse
        every { mockResponse.launchSessionUpdater(any(), any()) } just runs
        val result = impl.authenticate(Biometrics.StartParameters(mockk(), 30U))
        assert(result is StytchResult.Success)
        coVerify { mockApi.authenticate("signature", "biometricRegistrationId", 30U) }
        verify { mockResponse.launchSessionUpdater(any(), any()) }
    }

    @Test
    fun `authenticate with callback calls callback method`() {
        // short circuit on the first internal check, since we just care that the callback method is called
        every { mockStorageHelper.getEd25519PublicKey(any()) } returns null
        val mockCallback = spyk<(BiometricsAuthResponse) -> Unit>()
        impl.authenticate(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }
}