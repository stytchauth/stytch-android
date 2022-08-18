package com.stytch.sdk

import android.content.Context
import java.security.KeyStore

private const val KEY_ALIAS = "Stytch KeyStore Alias"
private const val PREFERENCES_FILE_NAME = "stytch_preferences"
private const val PREFERENCES_CODE_CHALLENGE = "code_challenge"

internal class StorageHelper(context: Context) {

    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore")
    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

    init {
        keyStore.load(null)
        EncryptionManager.createNewKeys(context, keyStore, KEY_ALIAS)
    }

    /**
     * Encrypt and save value to SharedPreferences
     */
    internal fun saveValue(name: String, value: String) {
        val encryptedData = EncryptionManager.encryptString(keyStore, KEY_ALIAS, value)
        with(sharedPreferences.edit()) {
            putString(name, encryptedData)
            apply()
        }
    }

    /**
     * Load and decrypt value from SharedPreferences
     */
    internal fun loadValue(name: String): String? {
        val encryptedString = sharedPreferences.getString(name, null)
        return EncryptionManager.decryptString(keyStore, KEY_ALIAS, encryptedString)
    }

    /**
     * @return Pair(codeChallengeMethod, codeChallenge)
     * @throws StytchExceptions.NoCodeChallengeFound
     */
    internal fun getHashedCodeChallenge(generateNew: Boolean = false): Pair<String, String> {
        val codeChallenge: String?

        if (generateNew) {
            codeChallenge = EncryptionManager.generateCodeChallenge()
            saveValue(PREFERENCES_CODE_CHALLENGE, codeChallenge)
        } else {
            codeChallenge = loadValue(PREFERENCES_CODE_CHALLENGE)
        }

        if (codeChallenge == null) {
            throw StytchExceptions.NoCodeChallengeFound
        }

        return "S256" to EncryptionManager.getSha256(codeChallenge)
    }
}