package com.stytch.sdk

import android.content.Context
import android.content.SharedPreferences
import java.security.KeyStore

private const val KEY_ALIAS = "Stytch RSA 2048"
private const val ED25519_KEY_ALIAS = "Stytch Ed25519"
private const val PREFERENCES_FILE_NAME = "stytch_preferences"
internal const val PREFERENCES_CODE_VERIFIER = "code_verifier"

internal object StorageHelper {

    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore")
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var encryptedSharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        keyStore.load(null)
        sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        EncryptionManager.createNewKeys(context, KEY_ALIAS)
        encryptedSharedPreferences = context.getSharedPreferences(
            EncryptionManager.PREF_FILE_NAME,
            Context.MODE_PRIVATE
        )
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

    /**
     * Get or create a new ED25519 key
     * @return Base64 encoded publicKey or null on exception
     */
    internal fun getEd25519PublicKey(context: Context): String? = try {
        EncryptionManager.getOrGenerateEd25519PublicKey(context, ED25519_KEY_ALIAS)
    } catch (ex: Exception) {
        StytchLog.e(ex.message ?: "Failed to get ED25519 public key")
        null
    }

    /**
     * Sign the provided challenge
     * @return Base64 encoded signedChallenge or null on exception
     */
    internal fun signEd25519CodeChallenge(context: Context, challenge: String): String? = try {
        EncryptionManager.signEd25519CodeChallenge(context, ED25519_KEY_ALIAS, challenge)
    } catch (ex: Exception) {
        StytchLog.e(ex.message ?: "Failed to sign challenge")
        null
    }

    /**
     * Delete the existing ED25519 key from shared preferences
     */
    internal fun deleteEd25519Key(): Boolean = try {
        with(encryptedSharedPreferences.edit()) {
            remove(ED25519_KEY_ALIAS)
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
    internal fun ed25519KeyExists(): Boolean = try {
        encryptedSharedPreferences.contains(ED25519_KEY_ALIAS)
    } catch (e: Exception) {
        false
    }

    /**
     * Check if the keyset is using the Android KeyStore
     * @return Boolean
     */
    internal fun checkIfKeysetIsUsingKeystore(context: Context): Boolean = try {
        val isUsingKeystore = EncryptionManager.isKeysetUsingKeystore(context, ED25519_KEY_ALIAS)
        // clean up after checking to avoid false positives when checking if key exists
        deleteEd25519Key()
        isUsingKeystore
    } catch (e: Exception) {
        false
    }
}
