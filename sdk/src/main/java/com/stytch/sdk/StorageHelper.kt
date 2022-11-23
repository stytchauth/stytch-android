package com.stytch.sdk

import android.content.Context
import android.content.SharedPreferences
import java.security.KeyStore

private const val KEY_ALIAS = "Stytch RSA 2048"
private const val PREFERENCES_FILE_NAME = "stytch_preferences"
internal const val PREFERENCES_CODE_VERIFIER = "code_verifier"

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

    /**
     * Load and decrypt value from SharedPreferences
     * @throws Exception if failed to load data
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
     * Load and return a Map of all cookies in memory
     * @param Map<String, String> of cookie and preference names
     * @return Map<String?, String?> of names and values of cookies saved in memory or null if there are none
     * @throws Exception if failed to return data
     */
    internal fun getAllCookies(cookiePrefMap: Map<String, String>): Map<String?, String?>? {
        return try {
            cookiePrefMap.mapValues {
                val cookieValue = sharedPreferences.getString(it.value, "")
                EncryptionManager.decryptString(cookieValue)
            }
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
}
