package com.stytch.sdk

import android.content.Context
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

    @MockK
    private lateinit var mockBiometricsProvider: BiometricsProvider

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
            api = mockApi,
            biometricsProvider = mockBiometricsProvider,
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
        every { mockStorageHelper.deleteEd25519Key(any()) } returns true
        val result = impl.register(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception.reason == StytchErrorType.NOT_USING_KEYSTORE.message)
        verify { mockStorageHelper.deleteEd25519Key(BIOMETRICS_REGISTRATION_KEY) }
    }

    @Test
    fun `register returns correct error if no session is found and removes pending registration`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore(any()) } returns true
        every { mockSessionStorage.sessionToken } returns null
        every { mockSessionStorage.sessionJwt } returns null
        every { mockStorageHelper.deleteEd25519Key(any()) } returns true
        val result = impl.register(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception.reason == StytchErrorType.NO_CURRENT_SESSION.message)
        verify { mockStorageHelper.deleteEd25519Key(BIOMETRICS_REGISTRATION_KEY) }
    }

    @Test
    fun `register returns correct error if biometrics are not available`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore(any()) } returns true
        every { mockSessionStorage.sessionToken } returns "sessionToken"
        every { mockSessionStorage.sessionJwt } returns "sessionJwt"
        coEvery {
            mockBiometricsProvider.showBiometricPrompt(any(), any())
        } throws StytchExceptions.Input("Authentication failed")
        val result = impl.register(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception.reason == "Authentication failed")
    }

    @Test
    fun `register returns correct error if no public key is found and removes pending registration`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore(any()) } returns true
        every { mockSessionStorage.sessionToken } returns "sessionToken"
        every { mockSessionStorage.sessionJwt } returns "sessionJwt"
        coEvery { mockBiometricsProvider.showBiometricPrompt(any(), any()) } just runs
        every { mockStorageHelper.getEd25519PublicKey(any(), BIOMETRICS_REGISTRATION_KEY) } returns null
        every { mockStorageHelper.deleteEd25519Key(any()) } returns true
        val result = impl.register(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception.reason == StytchErrorType.KEY_GENERATION_FAILED.message)
        verify { mockStorageHelper.deleteEd25519Key(BIOMETRICS_REGISTRATION_KEY) }
    }

    @Test
    fun `register returns correct error if registerStart fails and removes pending registration`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore(any()) } returns true
        every { mockSessionStorage.sessionToken } returns "sessionToken"
        every { mockSessionStorage.sessionJwt } returns "sessionJwt"
        coEvery { mockBiometricsProvider.showBiometricPrompt(any(), any()) } just runs
        every { mockStorageHelper.getEd25519PublicKey(any(), BIOMETRICS_REGISTRATION_KEY) } returns "publicKey"
        every { mockStorageHelper.deleteEd25519Key(any()) } returns true
        coEvery { mockApi.registerStart("publicKey") } returns StytchResult.Error(
            StytchExceptions.Response(mockk(relaxed = true))
        )
        val result = impl.register(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception is StytchExceptions.Response)
        coVerify { mockApi.registerStart("publicKey") }
        verify { mockStorageHelper.deleteEd25519Key(BIOMETRICS_REGISTRATION_KEY) }
    }

    @Test
    fun `register returns correct error if challenge signing fails and removes pending registration`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore(any()) } returns true
        every { mockSessionStorage.sessionToken } returns "sessionToken"
        every { mockSessionStorage.sessionJwt } returns "sessionJwt"
        coEvery { mockBiometricsProvider.showBiometricPrompt(any(), any()) } just runs
        every { mockStorageHelper.getEd25519PublicKey(any(), BIOMETRICS_REGISTRATION_KEY) } returns "publicKey"
        every { mockStorageHelper.deleteEd25519Key(any()) } returns true
        coEvery { mockApi.registerStart("publicKey") } returns StytchResult.Success(
            mockk {
                every { challenge } returns "challenge"
            }
        )
        every {
            mockStorageHelper.signEd25519CodeChallenge(any(), BIOMETRICS_REGISTRATION_KEY, "challenge")
        } returns null
        val result = impl.register(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception is StytchExceptions.Input)
        assert(result.exception.reason == StytchErrorType.ERROR_SIGNING_CHALLENGE.message)
        coVerify { mockApi.registerStart("publicKey") }
        verify { mockStorageHelper.deleteEd25519Key(BIOMETRICS_REGISTRATION_KEY) }
    }

    @Test
    fun `register returns success if everything succeeds`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore(any()) } returns true
        every { mockSessionStorage.sessionToken } returns "sessionToken"
        every { mockSessionStorage.sessionJwt } returns "sessionJwt"
        coEvery { mockBiometricsProvider.showBiometricPrompt(any(), any()) } just runs
        every { mockStorageHelper.getEd25519PublicKey(any(), BIOMETRICS_REGISTRATION_KEY) } returns "publicKey"
        coEvery { mockApi.registerStart("publicKey") } returns StytchResult.Success(
            mockk {
                every { challenge } returns "challenge"
                every { biometricRegistrationId } returns "biometricRegistrationId"
            }
        )
        every {
            mockStorageHelper.signEd25519CodeChallenge(any(), BIOMETRICS_REGISTRATION_KEY, "challenge")
        } returns "signature"
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
        every { mockStorageHelper.deleteEd25519Key(any()) } returns true
        val mockCallback = spyk<(BiometricsAuthResponse) -> Unit>()
        impl.register(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `authenticate returns correct error if no session is found, but registration is not removed`() = runTest {
        every { mockSessionStorage.sessionToken } returns null
        every { mockSessionStorage.sessionJwt } returns null
        val result = impl.authenticate(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception.reason == StytchErrorType.NO_CURRENT_SESSION.message)
        verify(exactly = 0) { mockStorageHelper.deleteEd25519Key(BIOMETRICS_REGISTRATION_KEY) }
    }

    @Test
    fun `authenticate returns correct error if biometrics are not available`() = runTest {
        coEvery {
            mockBiometricsProvider.showBiometricPrompt(any(), any())
        } throws StytchExceptions.Input("Authentication failed")
        val result = impl.authenticate(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception.reason == "Authentication failed")
    }

    @Test
    fun `authenticate returns correct error if no public key is found`() = runTest {
        coEvery { mockBiometricsProvider.showBiometricPrompt(any(), any()) } just runs
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore(any()) } returns true
        every { mockStorageHelper.getEd25519PublicKey(any(), BIOMETRICS_REGISTRATION_KEY) } returns null
        val result = impl.authenticate(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception.reason == StytchErrorType.KEY_GENERATION_FAILED.message)
    }

    @Test
    fun `authenticate returns correct error if authenticateStart fails`() = runTest {
        coEvery { mockBiometricsProvider.showBiometricPrompt(any(), any()) } just runs
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore(any()) } returns true
        every { mockStorageHelper.getEd25519PublicKey(any(), BIOMETRICS_REGISTRATION_KEY) } returns "publicKey"
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
        coEvery { mockBiometricsProvider.showBiometricPrompt(any(), any()) } just runs
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore(any()) } returns true
        every { mockStorageHelper.getEd25519PublicKey(any(), BIOMETRICS_REGISTRATION_KEY) } returns "publicKey"
        coEvery { mockApi.authenticateStart("publicKey") } returns StytchResult.Success(
            mockk {
                every { challenge } returns "challenge"
            }
        )
        every {
            mockStorageHelper.signEd25519CodeChallenge(any(), BIOMETRICS_REGISTRATION_KEY, "challenge")
        } returns null
        val result = impl.authenticate(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception is StytchExceptions.Input)
        assert(result.exception.reason == StytchErrorType.ERROR_SIGNING_CHALLENGE.message)
        coVerify { mockApi.authenticateStart("publicKey") }
    }

    @Test
    fun `authenticate returns success if everything succeeds`() = runTest {
        coEvery { mockBiometricsProvider.showBiometricPrompt(any(), any()) } just runs
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore(any()) } returns true
        every { mockStorageHelper.getEd25519PublicKey(any(), BIOMETRICS_REGISTRATION_KEY) } returns "publicKey"
        coEvery { mockApi.authenticateStart("publicKey") } returns StytchResult.Success(
            mockk {
                every { challenge } returns "challenge"
                every { biometricRegistrationId } returns "biometricRegistrationId"
            }
        )
        every {
            mockStorageHelper.signEd25519CodeChallenge(any(), BIOMETRICS_REGISTRATION_KEY, "challenge")
        } returns "signature"
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
        coEvery { mockBiometricsProvider.showBiometricPrompt(any(), any()) } just runs
        // short circuit on the first internal check, since we just care that the callback method is called
        every { mockStorageHelper.getEd25519PublicKey(any(), BIOMETRICS_REGISTRATION_KEY) } returns null
        val mockCallback = spyk<(BiometricsAuthResponse) -> Unit>()
        impl.authenticate(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `registrationAvailable delegates to storageHelper`() {
        every { mockStorageHelper.ed25519KeyExists(any()) } returns true
        assert(impl.registrationAvailable)
        verify { mockStorageHelper.ed25519KeyExists(BIOMETRICS_REGISTRATION_KEY) }
    }

    @Test
    fun `removeRegistration delegates to storageHelper`() {
        every { mockStorageHelper.deleteEd25519Key(any()) } returns true
        assert(impl.removeRegistration())
        verify { mockStorageHelper.deleteEd25519Key(BIOMETRICS_REGISTRATION_KEY) }
    }

    @Test
    fun `isUsingKeystore delegates to storageHelper`() {
        val mockContext = mockk<Context>()
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore(any()) } returns true
        assert(impl.isUsingKeystore(mockContext))
        verify { mockStorageHelper.checkIfKeysetIsUsingKeystore(mockContext) }
    }

    @Test
    fun `areBiometricsAvailable delegates to BiometricsProvider`() {
        every { mockBiometricsProvider.areBiometricsAvailable(any()) } returns Pair(true, "Yep")
        val (canShow, message) = impl.areBiometricsAvailable(mockk())
        assert(canShow)
        assert(message == "Yep")
        verify { mockBiometricsProvider.areBiometricsAvailable(any()) }
    }
}
