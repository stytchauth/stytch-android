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
     */
    internal fun getHashedCodeChallenge(generateNew: Boolean = false): Pair<String, String> {
        val codeChallenge = if (generateNew) {
            EncryptionManager.generateCodeChallenge()
        } else {
            loadValue(PREFERENCES_CODE_CHALLENGE)
        } ?: EncryptionManager.generateCodeChallenge() // generate new codeChallenge if no value found in preferences

        saveValue(PREFERENCES_CODE_CHALLENGE, codeChallenge)

        return "S256" to EncryptionManager.getSha256(codeChallenge)
    }
}