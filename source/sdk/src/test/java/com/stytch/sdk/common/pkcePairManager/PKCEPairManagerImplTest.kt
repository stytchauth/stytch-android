package com.stytch.sdk.common.pkcePairManager

import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.PKCECodePair
import com.stytch.sdk.common.StorageHelper
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.security.KeyStore

internal class PKCEPairManagerImplTest {
    private lateinit var impl: PKCEPairManager

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any()) } just runs
        mockkObject(StorageHelper)
        every { StorageHelper.initialize(any()) } returns mockk()

        impl = PKCEPairManagerImpl(StorageHelper, EncryptionManager)
    }

    @After
    fun after() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `generateAndReturnPKCECodePair delegates to objects as necessary and returns as expected`() {
        every { EncryptionManager.generateCodeVerifier() } returns "code-verifier"
        every { EncryptionManager.encryptCodeChallengeFromVerifier(any()) } returns "code-challenge"
        every { StorageHelper.saveValue(any(), any()) } just runs
        val pkceCodePair = impl.generateAndReturnPKCECodePair()
        val expected = PKCECodePair("code-challenge", "code-verifier")
        assert(pkceCodePair == expected)
    }

    @Test
    fun `getPKCECodePair returns as expected`() {
        // test missing both
        every { StorageHelper.loadValue(PREFERENCES_CODE_CHALLENGE) } returns null
        every { StorageHelper.loadValue(PREFERENCES_CODE_VERIFIER) } returns null
        var pkceCodePair = impl.getPKCECodePair()
        assert(pkceCodePair == null)

        // test missing code challenge
        every { StorageHelper.loadValue(PREFERENCES_CODE_CHALLENGE) } returns null
        every { StorageHelper.loadValue(PREFERENCES_CODE_VERIFIER) } returns "code-verifier"
        pkceCodePair = impl.getPKCECodePair()
        assert(pkceCodePair == null)

        // test missing verifier
        every { StorageHelper.loadValue(PREFERENCES_CODE_CHALLENGE) } returns "code-challenge"
        every { StorageHelper.loadValue(PREFERENCES_CODE_VERIFIER) } returns null
        pkceCodePair = impl.getPKCECodePair()
        assert(pkceCodePair == null)

        // test both present
        every { StorageHelper.loadValue(PREFERENCES_CODE_CHALLENGE) } returns "code-challenge"
        every { StorageHelper.loadValue(PREFERENCES_CODE_VERIFIER) } returns "code-verifier"
        pkceCodePair = impl.getPKCECodePair()
        assert(pkceCodePair == PKCECodePair("code-challenge", "code-verifier"))
    }

    @Test
    fun `clearPKCECodePair clears the PKCE code pair`() {
        every { StorageHelper.deletePreference(any()) } returns true
        impl.clearPKCECodePair()
        verify(exactly = 1) { StorageHelper.deletePreference(PREFERENCES_CODE_CHALLENGE) }
        verify(exactly = 1) { StorageHelper.deletePreference(PREFERENCES_CODE_VERIFIER) }
    }
}
