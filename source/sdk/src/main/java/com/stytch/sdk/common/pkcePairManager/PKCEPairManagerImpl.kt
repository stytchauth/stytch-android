package com.stytch.sdk.common.pkcePairManager

import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.PKCECodePair
import com.stytch.sdk.common.StorageHelper

internal const val PREFERENCES_CODE_VERIFIER = "code_verifier"
internal const val PREFERENCES_CODE_CHALLENGE = "code_challenge"

internal class PKCEPairManagerImpl(
    private val storageHelper: StorageHelper,
    private val encryptionManager: EncryptionManager,
) : PKCEPairManager {
    override fun generateAndReturnPKCECodePair(): PKCECodePair {
        val codeVerifier = encryptionManager.generateCodeVerifier()
        val codeChallenge = encryptionManager.encryptCodeChallengeFromVerifier(codeVerifier)
        storageHelper.saveValue(PREFERENCES_CODE_CHALLENGE, codeChallenge)
        storageHelper.saveValue(PREFERENCES_CODE_VERIFIER, codeVerifier)
        return PKCECodePair(
            codeChallenge = codeChallenge,
            codeVerifier = codeVerifier,
        )
    }

    override fun getPKCECodePair(): PKCECodePair? {
        val codeChallenge = storageHelper.loadValue(PREFERENCES_CODE_CHALLENGE)
        val codeVerifier = storageHelper.loadValue(PREFERENCES_CODE_VERIFIER)
        if (codeChallenge.isNullOrEmpty() || codeVerifier.isNullOrEmpty()) return null
        return PKCECodePair(
            codeChallenge = codeChallenge,
            codeVerifier = codeVerifier,
        )
    }

    override fun clearPKCECodePair() {
        storageHelper.deletePreference(PREFERENCES_CODE_CHALLENGE)
        storageHelper.deletePreference(PREFERENCES_CODE_VERIFIER)
    }
}
