package com.stytch.sdk.consumer.biometrics

import androidx.biometric.BiometricManager
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchAPIError
import com.stytch.sdk.common.errors.StytchBiometricAuthenticationFailed
import com.stytch.sdk.common.errors.StytchChallengeSigningFailed
import com.stytch.sdk.common.errors.StytchInternalError
import com.stytch.sdk.common.errors.StytchKeystoreUnavailableError
import com.stytch.sdk.common.errors.StytchMissingPublicKeyError
import com.stytch.sdk.common.errors.StytchNoBiometricsRegistrationError
import com.stytch.sdk.common.errors.StytchNoCurrentSessionError
import com.stytch.sdk.common.extensions.toBase64DecodedByteArray
import com.stytch.sdk.common.extensions.toBase64EncodedString
import com.stytch.sdk.common.sessions.SessionAutoUpdater
import com.stytch.sdk.consumer.BiometricsAuthResponse
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.network.models.BiometricsAuthData
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
import javax.crypto.Cipher
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
    private lateinit var mockSessionStorage: ConsumerSessionStorage

    @MockK
    private lateinit var mockStorageHelper: StorageHelper

    @MockK
    private lateinit var mockBiometricsProvider: BiometricsProvider

    @MockK
    private lateinit var mockUserManagerApi: StytchApi.UserManagement

    private lateinit var impl: BiometricsImpl
    private val dispatcher = Dispatchers.Unconfined
    private val base64DecodedByteArray = ByteArray(16)
    private val base64EncodedString = "cHJpdmF0ZWtleQ=="
    private val mockCipher: Cipher = mockk(relaxed = true) {
        every { doFinal(any()) } returns base64DecodedByteArray
    }
    private val deleteBiometricsSpy = spyk<suspend (String) -> Unit>()

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        mockkStatic(
            "com.stytch.sdk.common.extensions.StringExtKt",
            "com.stytch.sdk.common.extensions.ByteArrayExtKt",
            "com.stytch.sdk.consumer.extensions.StytchResultExtKt"
        )
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any()) } returns Unit
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(StorageHelper)
        MockKAnnotations.init(this, true, true)
        mockkObject(SessionAutoUpdater)
        every { SessionAutoUpdater.startSessionUpdateJob(any(), any(), any()) } just runs
        every { mockStorageHelper.loadValue(any()) } returns ""
        every { mockStorageHelper.getBoolean(any()) } returns true
        every { mockStorageHelper.saveValue(any(), any()) } just runs
        every { mockStorageHelper.saveBoolean(any(), any()) } just runs
        every { any<String>().toBase64DecodedByteArray() } returns base64DecodedByteArray
        every { any<ByteArray>().toBase64EncodedString() } returns base64EncodedString
        impl = BiometricsImpl(
            externalScope = TestScope(),
            dispatchers = StytchDispatchers(dispatcher, dispatcher),
            sessionStorage = mockSessionStorage,
            storageHelper = mockStorageHelper,
            api = mockApi,
            biometricsProvider = mockBiometricsProvider,
            deleteBiometricRegistration = deleteBiometricsSpy,
        )
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `register returns correct error if insecure keystore and allowFallbackToCleartext is false`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore() } returns false
        val mockRegisterParameters: Biometrics.RegisterParameters = mockk(relaxed = true) {
            every { allowFallbackToCleartext } returns false
        }
        val result = impl.register(mockRegisterParameters)
        require(result is StytchResult.Error)
        assert(result.exception is StytchKeystoreUnavailableError)
    }

    @Test
    fun `register returns correct error if no session is found`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore() } returns true
        every { mockStorageHelper.preferenceExists(any()) } returns false
        every {
            mockSessionStorage.ensureSessionIsValidOrThrow()
        } throws StytchNoCurrentSessionError
        val result = impl.register(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception is StytchNoCurrentSessionError)
    }

    @Test
    fun `register removes existing registration if found`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore() } returns true
        every { mockStorageHelper.loadValue(any()) } returns null
        every { mockStorageHelper.preferenceExists(any()) } returns true
        every { mockBiometricsProvider.ensureSecretKeyIsAvailable(any()) } just runs
        every {
            mockBiometricsProvider.areBiometricsAvailable(any(), any())
        } returns BiometricManager.BIOMETRIC_SUCCESS
        every { mockStorageHelper.deletePreference(any()) } returns true
        every { mockSessionStorage.ensureSessionIsValidOrThrow() } just runs
        coEvery {
            mockBiometricsProvider.showBiometricPromptForRegistration(any(), any(), any())
        } throws StytchBiometricAuthenticationFailed(AUTHENTICATION_FAILED)
        every { mockBiometricsProvider.deleteSecretKey() } just runs
        val result = impl.register(mockk(relaxed = true))
        require(result is StytchResult.Error)
        require(result.exception is StytchBiometricAuthenticationFailed)
        verify { mockStorageHelper.deletePreference(LAST_USED_BIOMETRIC_REGISTRATION_ID) }
        verify { mockStorageHelper.deletePreference(PRIVATE_KEY_KEY) }
        verify { mockStorageHelper.deletePreference(CIPHER_IV_KEY) }
    }

    @Test
    fun `register removes existing registration if unexpected exception occurs`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore() } returns true
        every { mockStorageHelper.loadValue(any()) } returns null
        every { mockStorageHelper.preferenceExists(any()) } returns true
        every { mockStorageHelper.deletePreference(any()) } returns true
        every { mockSessionStorage.ensureSessionIsValidOrThrow() } just runs
        coEvery {
            mockBiometricsProvider.showBiometricPromptForRegistration(any(), any(), any())
        } throws RuntimeException("Testing")
        every { mockBiometricsProvider.deleteSecretKey() } just runs
        val result = impl.register(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert((result.exception as StytchInternalError).exception is RuntimeException)
        verify { mockStorageHelper.deletePreference(LAST_USED_BIOMETRIC_REGISTRATION_ID) }
        verify { mockStorageHelper.deletePreference(PRIVATE_KEY_KEY) }
        verify { mockStorageHelper.deletePreference(CIPHER_IV_KEY) }
    }

    @Test
    fun `register returns correct error if biometrics fail`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore() } returns true
        every { mockStorageHelper.preferenceExists(any()) } returns false
        every { mockSessionStorage.ensureSessionIsValidOrThrow() } just runs
        coEvery {
            mockBiometricsProvider.showBiometricPromptForRegistration(any(), any(), any())
        } throws StytchBiometricAuthenticationFailed(AUTHENTICATION_FAILED)
        val result = impl.register(mockk(relaxed = true))
        require(result is StytchResult.Error)
        require(result.exception is StytchBiometricAuthenticationFailed)
    }

    @Test
    fun `register returns correct error if keys could not be generated`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore() } returns true
        every { mockStorageHelper.preferenceExists(any()) } returns false
        every { mockSessionStorage.ensureSessionIsValidOrThrow() } just runs
        coEvery {
            mockBiometricsProvider.showBiometricPromptForRegistration(any(), any(), any())
        } returns mockk(relaxed = true)
        every {
            EncryptionManager.generateEd25519KeyPair()
        } throws StytchMissingPublicKeyError(null)
        val result = impl.register(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception is StytchMissingPublicKeyError)
    }

    @Test
    fun `register returns correct error if registerStart fails`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore() } returns true
        every { mockStorageHelper.preferenceExists(any()) } returns false
        every { mockSessionStorage.ensureSessionIsValidOrThrow() } just runs
        coEvery {
            mockBiometricsProvider.showBiometricPromptForRegistration(any(), any(), any())
        } returns mockCipher
        every {
            EncryptionManager.generateEd25519KeyPair()
        } returns Pair(base64EncodedString, base64EncodedString)
        coEvery { mockApi.registerStart(base64EncodedString) } returns StytchResult.Error(
            StytchAPIError(errorType = "", message = "")
        )
        val result = impl.register(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception is StytchAPIError)
        coVerify { mockApi.registerStart(base64EncodedString) }
    }

    @Test
    fun `register returns correct error if challenge signing fails`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore() } returns true
        every { mockStorageHelper.preferenceExists(any()) } returns false
        every { mockSessionStorage.ensureSessionIsValidOrThrow() } just runs
        coEvery {
            mockBiometricsProvider.showBiometricPromptForRegistration(any(), any(), any())
        } returns mockCipher
        every {
            EncryptionManager.generateEd25519KeyPair()
        } returns Pair(base64EncodedString, base64EncodedString)
        coEvery { mockApi.registerStart(base64EncodedString) } returns StytchResult.Success(
            mockk {
                every { challenge } returns "challenge"
            }
        )
        every {
            EncryptionManager.signEd25519Challenge(any(), "challenge")
        } throws StytchChallengeSigningFailed(null)
        val result = impl.register(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception is StytchChallengeSigningFailed)
        coVerify { mockApi.registerStart(base64EncodedString) }
    }

    @Test
    fun `register returns success if everything succeeds and saves required preferences`() = runTest {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore() } returns true
        every { mockStorageHelper.preferenceExists(any()) } returns false
        every { mockSessionStorage.ensureSessionIsValidOrThrow() } just runs
        coEvery {
            mockBiometricsProvider.showBiometricPromptForRegistration(any(), any(), any())
        } returns mockCipher
        every {
            EncryptionManager.generateEd25519KeyPair()
        } returns Pair(base64EncodedString, base64EncodedString)
        coEvery { mockApi.registerStart(base64EncodedString) } returns StytchResult.Success(
            mockk {
                every { challenge } returns "challenge"
                every { biometricRegistrationId } returns "biometricRegistrationId"
            }
        )
        every { EncryptionManager.signEd25519Challenge(any(), any()) } returns "signature"
        val mockResponse = mockk<StytchResult.Success<BiometricsAuthData>>(relaxed = true)
        coEvery { mockApi.register("signature", "biometricRegistrationId", 30U) } returns mockResponse
        every { mockStorageHelper.saveValue(any(), any()) } just runs
        every { mockResponse.launchSessionUpdater(any(), any()) } just runs
        val result = impl.register(Biometrics.RegisterParameters(mockk(), 30U))
        assert(result is StytchResult.Success)
        coVerify { mockApi.register("signature", "biometricRegistrationId", 30U) }
        verify { mockResponse.launchSessionUpdater(any(), any()) }
        verify { mockStorageHelper.saveValue(LAST_USED_BIOMETRIC_REGISTRATION_ID, "biometricRegistrationId") }
        verify { mockStorageHelper.saveValue(PRIVATE_KEY_KEY, base64EncodedString) }
        verify { mockStorageHelper.saveValue(CIPHER_IV_KEY, any()) }
    }

    @Test
    fun `register with callback calls callback method`() {
        // short circuit on the first internal check, since we just care that the callback method is called
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore() } returns false
        every { mockStorageHelper.deletePreference(any()) } returns true
        val mockCallback = spyk<(BiometricsAuthResponse) -> Unit>()
        impl.register(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `authenticate wraps unexpected exceptions in StytchResult Error class`() = runTest {
        every { mockStorageHelper.preferenceExists(any()) } throws RuntimeException("Testing")
        val result = impl.authenticate(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception is StytchInternalError)
    }

    @Test
    fun `authenticate returns correct error if biometrics are not available`() = runTest {
        every { mockStorageHelper.preferenceExists(any()) } returns false
        every { mockStorageHelper.loadValue(any()) } returns null
        val result = impl.authenticate(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception is StytchNoBiometricsRegistrationError)
    }

    @Test
    fun `authenticate returns correct error if biometrics fails`() = runTest {
        every { mockStorageHelper.preferenceExists(any()) } returns true
        every { mockBiometricsProvider.ensureSecretKeyIsAvailable(any()) } just runs
        every {
            mockBiometricsProvider.areBiometricsAvailable(any(), any())
        } returns BiometricManager.BIOMETRIC_SUCCESS
        every { mockStorageHelper.loadValue(any()) } returns ""
        coEvery {
            mockBiometricsProvider.showBiometricPromptForAuthentication(any(), any(), any(), any())
        } throws StytchBiometricAuthenticationFailed(AUTHENTICATION_FAILED)
        val result = impl.authenticate(mockk(relaxed = true))
        require(result is StytchResult.Error)
        require(result.exception is StytchBiometricAuthenticationFailed)
    }

    @Test
    fun `authenticate returns correct error if public key cannot be derived from private key`() = runTest {
        every { mockStorageHelper.preferenceExists(any()) } returns true
        every { mockBiometricsProvider.ensureSecretKeyIsAvailable(any()) } just runs
        every {
            mockBiometricsProvider.areBiometricsAvailable(any(), any())
        } returns BiometricManager.BIOMETRIC_SUCCESS
        every { mockStorageHelper.loadValue(any()) } returns ""
        coEvery {
            mockBiometricsProvider.showBiometricPromptForAuthentication(any(), any(), any(), any())
        } returns mockCipher
        every {
            EncryptionManager.deriveEd25519PublicKeyFromPrivateKeyBytes(base64DecodedByteArray)
        } throws StytchMissingPublicKeyError(null)
        val result = impl.authenticate(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception is StytchMissingPublicKeyError)
    }

    @Test
    fun `authenticate returns correct error if authenticateStart fails`() = runTest {
        every { mockStorageHelper.preferenceExists(any()) } returns true
        every { mockBiometricsProvider.ensureSecretKeyIsAvailable(any()) } just runs
        every {
            mockBiometricsProvider.areBiometricsAvailable(any(), any())
        } returns BiometricManager.BIOMETRIC_SUCCESS
        every { mockStorageHelper.loadValue(any()) } returns ""
        coEvery {
            mockBiometricsProvider.showBiometricPromptForAuthentication(any(), any(), any(), any())
        } returns mockCipher
        every {
            EncryptionManager.deriveEd25519PublicKeyFromPrivateKeyBytes(base64DecodedByteArray)
        } returns "publicKey"
        coEvery { mockApi.authenticateStart("publicKey") } returns StytchResult.Error(
            StytchAPIError(errorType = "", message = "")
        )
        val result = impl.authenticate(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception is StytchAPIError)
        coVerify { mockApi.authenticateStart("publicKey") }
    }

    @Test
    fun `authenticate returns correct error if challenge signing fails`() = runTest {
        every { mockStorageHelper.preferenceExists(any()) } returns true
        every { mockBiometricsProvider.ensureSecretKeyIsAvailable(any()) } just runs
        every {
            mockBiometricsProvider.areBiometricsAvailable(any(), any())
        } returns BiometricManager.BIOMETRIC_SUCCESS
        every { mockStorageHelper.loadValue(any()) } returns ""
        coEvery {
            mockBiometricsProvider.showBiometricPromptForAuthentication(any(), any(), any(), any())
        } returns mockCipher
        every {
            EncryptionManager.deriveEd25519PublicKeyFromPrivateKeyBytes(base64DecodedByteArray)
        } returns "publicKey"
        coEvery { mockApi.authenticateStart("publicKey") } returns StytchResult.Success(
            mockk {
                every { challenge } returns "challenge"
            }
        )
        every {
            EncryptionManager.signEd25519Challenge(any(), "challenge")
        } throws StytchChallengeSigningFailed(null)
        val result = impl.authenticate(mockk(relaxed = true))
        require(result is StytchResult.Error)
        assert(result.exception is StytchChallengeSigningFailed)
        coVerify { mockApi.authenticateStart("publicKey") }
    }

    @Test
    fun `authenticate returns success if everything succeeds`() = runTest {
        every { mockStorageHelper.preferenceExists(any()) } returns true
        every { mockBiometricsProvider.ensureSecretKeyIsAvailable(any()) } just runs
        every {
            mockBiometricsProvider.areBiometricsAvailable(any(), any())
        } returns BiometricManager.BIOMETRIC_SUCCESS
        every { mockStorageHelper.loadValue(any()) } returns ""
        coEvery {
            mockBiometricsProvider.showBiometricPromptForAuthentication(any(), any(), any(), any())
        } returns mockCipher
        every {
            EncryptionManager.deriveEd25519PublicKeyFromPrivateKeyBytes(base64DecodedByteArray)
        } returns "publicKey"
        coEvery { mockApi.authenticateStart("publicKey") } returns StytchResult.Success(
            mockk {
                every { challenge } returns "challenge"
                every { biometricRegistrationId } returns "biometricRegistrationId"
            }
        )
        every { EncryptionManager.signEd25519Challenge(any(), any()) } returns "signature"
        val mockResponse = mockk<StytchResult.Success<BiometricsAuthData>>(relaxed = true)
        coEvery { mockApi.authenticate("signature", "biometricRegistrationId", 30U) } returns mockResponse
        every { mockResponse.launchSessionUpdater(any(), any()) } just runs
        val result = impl.authenticate(Biometrics.AuthenticateParameters(mockk(), 30U))
        assert(result is StytchResult.Success)
        coVerify { mockApi.authenticate("signature", "biometricRegistrationId", 30U) }
        verify { mockResponse.launchSessionUpdater(any(), any()) }
    }

    @Test
    fun `authenticate with callback calls callback method`() {
        // short circuit on the first internal check, since we just care that the callback method is called
        every { mockStorageHelper.preferenceExists(any()) } returns false
        val mockCallback = spyk<(BiometricsAuthResponse) -> Unit>()
        impl.authenticate(mockk(relaxed = true), mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `registrationAvailable delegates to storageHelper`() {
        every { mockStorageHelper.preferenceExists(any()) } returns true
        every { mockBiometricsProvider.ensureSecretKeyIsAvailable(any()) } just runs
        every {
            mockBiometricsProvider.areBiometricsAvailable(any(), any())
        } returns BiometricManager.BIOMETRIC_SUCCESS
        assert(impl.isRegistrationAvailable(mockk(relaxed = true)))
        verify { mockStorageHelper.preferenceExists(LAST_USED_BIOMETRIC_REGISTRATION_ID) }
        verify { mockStorageHelper.preferenceExists(PRIVATE_KEY_KEY) }
        verify { mockStorageHelper.preferenceExists(CIPHER_IV_KEY) }
    }

    @Test
    fun `removeRegistration delegates to storageHelper and deletes registration from user`() = runTest {
        every { mockStorageHelper.loadValue(any()) } returns "lastUsedRegistrationId"
        every { mockStorageHelper.deletePreference(any()) } returns true
        coEvery { mockUserManagerApi.deleteBiometricRegistrationById(any()) } returns mockk(relaxed = true)
        coEvery { deleteBiometricsSpy.invoke(any()) } just runs
        every { mockBiometricsProvider.deleteSecretKey() } just runs
        assert(impl.removeRegistration())
        verify { mockStorageHelper.deletePreference(LAST_USED_BIOMETRIC_REGISTRATION_ID) }
        verify { mockStorageHelper.deletePreference(PRIVATE_KEY_KEY) }
        verify { mockStorageHelper.deletePreference(CIPHER_IV_KEY) }
        coVerify { deleteBiometricsSpy.invoke(any()) }
    }

    @Test
    fun `removeRegistration with callback calls callback`() {
        every { mockStorageHelper.deletePreference(any()) } returns true
        every { mockBiometricsProvider.deleteSecretKey() } just runs
        val mockCallback = spyk<(Boolean) -> Unit>()
        impl.removeRegistration(mockCallback)
        verify { mockCallback.invoke(any()) }
    }

    @Test
    fun `isUsingKeystore delegates to storageHelper`() {
        every { mockStorageHelper.checkIfKeysetIsUsingKeystore() } returns true
        assert(impl.isUsingKeystore())
        verify { mockStorageHelper.checkIfKeysetIsUsingKeystore() }
    }

    @Test
    fun `areBiometricsAvailable delegates to BiometricsProvider and produces correct registration result`() {
        every { mockBiometricsProvider.ensureSecretKeyIsAvailable(any()) } just runs
        every {
            mockBiometricsProvider.areBiometricsAvailable(any(), any())
        } returns BiometricManager.BIOMETRIC_SUCCESS
        every { mockStorageHelper.preferenceExists(any()) } returns false // no registration data
        var available = impl.areBiometricsAvailable(mockk())
        assert(available == BiometricAvailability.AvailableNoRegistrations)
        verify { mockBiometricsProvider.areBiometricsAvailable(any(), any()) }

        every { mockStorageHelper.preferenceExists(any()) } returns true // assume registration data
        available = impl.areBiometricsAvailable(mockk())
        assert(available == BiometricAvailability.AvailableRegistered)
        verify { mockBiometricsProvider.areBiometricsAvailable(any(), any()) }
    }
}
