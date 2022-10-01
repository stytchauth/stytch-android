package com.stytch.sdk

import android.content.Context
import java.security.KeyStore

private const val KEY_ALIAS = "Stytch RSA 2048"
private const val PREFERENCES_FILE_NAME = "stytch_preferences"
internal const val PREFERENCES_CODE_VERIFIER = "code_verifier"

internal class StorageHelper(context: Context) {

    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore")
    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)

    init {
        keyStore.load(null)
        EncryptionManager.createNewKeys(context, KEY_ALIAS)
    }

    /**
     * Encrypt and save value to SharedPreferences
     */
    internal fun saveValue(name: String, value: String?) {
        if (value == null) {
            with(sharedPreferences.edit()) {
                putString(name, value)
                apply()
            }
            return
        }

        val encryptedData = EncryptionManager.encryptString(value)
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
        return EncryptionManager.decryptString(encryptedString)
    }

    /**
     * @return Pair(codeChallengeMethod, codeChallenge)
     */
    internal fun generateHashedCodeChallenge(): Pair<String, String> {
        val codeVerifier: String?

        codeVerifier = EncryptionManager.generateCodeChallenge()
        saveValue(PREFERENCES_CODE_VERIFIER, codeVerifier)

        return "S256" to EncryptionManager.encryptCodeChallenge(codeVerifier)
    }
}
