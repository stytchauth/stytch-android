package com.stytch.sdk.common

import android.content.Context
import android.content.SharedPreferences
import java.security.KeyStore

private const val KEY_ALIAS = "Stytch RSA 2048"
private const val PREFERENCES_FILE_NAME = "stytch_preferences"
private const val PREFERENCES_CODE_VERIFIER = "code_verifier"

internal object StorageHelper {

    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore")
    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        keyStore.load(null)
        sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        EncryptionManager.createNewKeys(context, KEY_ALIAS)
    }

    /**
     * Encrypt and save value to SharedPreferences
     * @throws Exception if failed to encrypt string
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

    internal fun saveBoolean(name: String, value: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(name, value)
            apply()
        }
    }

    internal fun getBoolean(name: String): Boolean = sharedPreferences.getBoolean(name, false)

    /**
     * Load and decrypt value from SharedPreferences
     * @return null if failed to load data
     */
    internal fun loadValue(name: String): String? {
        return try {
            val encryptedString = sharedPreferences.getString(name, null)
            EncryptionManager.decryptString(encryptedString)
        } catch (ex: Exception) {
            null
        }
    }

    /**
     * @return Pair(codeChallengeMethod, codeChallenge)
     * @throws Exception if failed to encrypt data
     */
    internal fun generateHashedCodeChallenge(): Pair<String, String> {
        val codeVerifier: String?

        codeVerifier = EncryptionManager.generateCodeChallenge()
        saveValue(PREFERENCES_CODE_VERIFIER, codeVerifier)

        return "S256" to EncryptionManager.encryptCodeChallenge(codeVerifier)
    }

    internal fun retrieveHashedCodeChallenge(): String? = loadValue(PREFERENCES_CODE_VERIFIER)

    /**
     * Delete an existing ED25519 key from shared preferences
     */
    internal fun deletePreference(keyAlias: String): Boolean = try {
        with(sharedPreferences.edit()) {
            remove(keyAlias)
            apply()
            true
        }
    } catch (e: Exception) {
        false
    }

    /**
     * Check if the ED25519 key exists in shared preferences
     * @return Boolean
     */
    internal fun preferenceExists(keyAlias: String): Boolean = try {
        sharedPreferences.contains(keyAlias)
    } catch (e: Exception) {
        false
    }

    /**
     * Check if the keyset is using the Android KeyStore
     * @return Boolean
     */
    internal fun checkIfKeysetIsUsingKeystore(): Boolean = try {
        EncryptionManager.isKeysetUsingKeystore()
    } catch (e: Exception) {
        false
    }
}
