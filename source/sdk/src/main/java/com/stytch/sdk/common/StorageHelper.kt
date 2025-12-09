package com.stytch.sdk.common

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import com.stytch.sdk.common.EncryptionManager.KEY_PREFERENCES_FILE_NAME
import com.stytch.sdk.common.EncryptionManager.MASTER_KEY_ALIAS
import com.stytch.sdk.common.StorageHelper.sharedPreferences
import com.stytch.sdk.common.extensions.clearPreferences
import com.stytch.sdk.consumer.biometrics.BIOMETRIC_KEY_NAME
import com.stytch.sdk.consumer.biometrics.KEYS_REQUIRED_FOR_REGISTRATION
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.security.KeyStore

internal const val STYTCH_PREFERENCES_FILE_NAME = "stytch_preferences"

internal object StorageHelper {
    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore")

    @VisibleForTesting
    internal lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context): Job =
        CoroutineScope(SupervisorJob()).launch(Dispatchers.IO) {
            if (::sharedPreferences.isInitialized) return@launch
            keyStore.load(null)
            if (!keyStore.containsAlias(MASTER_KEY_ALIAS)) {
                // If an app is restored on device, the encrypted shared preference files will be restored, but the key
                // to decrypt them will NOT. If, on startup, we detect that there is no master key, we should clean
                // up any potentially restored preference files
                // NOTE: this is a slightly different flow than if the key is corrupted, which is checked in the
                // `getOrGenerateNewAES256KeysetManager` method
                context.clearPreferences(listOf(STYTCH_PREFERENCES_FILE_NAME, KEY_PREFERENCES_FILE_NAME))
            }
            EncryptionManager.createNewKeys(context, keyStore)
            sharedPreferences = context.getSharedPreferences(STYTCH_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
        }

    /**
     * Encrypt and save value to SharedPreferences
     * @throws Exception if failed to encrypt string
     */
    internal fun saveValue(
        name: String,
        value: String?,
    ) {
        if (value.isNullOrEmpty()) {
            with(sharedPreferences.edit()) {
                putString(name, null)
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

    internal fun saveBoolean(
        name: String,
        value: Boolean,
    ) {
        with(sharedPreferences.edit()) {
            putBoolean(name, value)
            apply()
        }
    }

    internal fun saveLong(
        name: String,
        value: Long,
    ) {
        with(sharedPreferences.edit()) {
            putLong(name, value)
            apply()
        }
    }

    internal fun getBoolean(
        name: String,
        default: Boolean = false,
    ): Boolean =
        try {
            sharedPreferences.getBoolean(name, default)
        } catch (ex: Exception) {
            default
        }

    internal fun getLong(
        name: String,
        default: Long = 0L,
    ): Long =
        try {
            sharedPreferences.getLong(name, default)
        } catch (ex: Exception) {
            default
        }

    /**
     * Load and decrypt value from SharedPreferences
     * @return null if failed to load data
     */
    internal fun loadValue(name: String): String? {
        return try {
            val encryptedString = sharedPreferences.getString(name, null)
            if (encryptedString.isNullOrEmpty()) {
                return null
            }
            EncryptionManager.decryptString(encryptedString)
        } catch (ex: Exception) {
            null
        }
    }

    /**
     * Delete an existing ED25519 key from shared preferences
     */
    internal fun deletePreference(keyAlias: String): Boolean =
        try {
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
    internal fun preferenceExists(keyAlias: String): Boolean =
        try {
            sharedPreferences.contains(keyAlias)
        } catch (e: Exception) {
            false
        }

    /**
     * Check if the keyset is using the Android KeyStore
     * @return Boolean
     */
    internal fun checkIfKeysetIsUsingKeystore(): Boolean =
        try {
            EncryptionManager.isKeysetUsingKeystore()
        } catch (e: Exception) {
            false
        }

    internal fun deleteAllBiometricsKeys() {
        KEYS_REQUIRED_FOR_REGISTRATION.forEach { deletePreference(it) }
        keyStore.deleteEntry(BIOMETRIC_KEY_NAME)
    }
}
