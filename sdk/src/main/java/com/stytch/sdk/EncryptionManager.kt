package com.stytch.sdk

import android.content.Context
import android.os.Build
import android.util.Base64
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.google.crypto.tink.shaded.protobuf.ByteString
import java.security.GeneralSecurityException
import java.security.MessageDigest
import javax.crypto.Cipher
import kotlin.random.Random

internal object EncryptionManager {

    private const val PREF_FILE_NAME = "stytch_secured_pref"
    private const val MASTER_KEY_URI = "android-keystore://stytch_master_key"
    private var aead: Aead? = null

    init {
        AeadConfig.register();
    }

    private fun getOrGenerateNewKeysetHandle(context: Context, keyAlias: String): KeysetHandle? {
        return AndroidKeysetManager.Builder()
            .withSharedPref(context, keyAlias, PREF_FILE_NAME)
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle
    }

    fun encryptString(plainText: String): String? {
        var encodedString: String? = null
        try {
            val aead = aead ?: throw Exception()
            val plainBytes: ByteArray = plainText.toByteArray(Charsets.UTF_8)
            // An artifical step to test whether Tink can co-exist with protobuf-lite.
            val pStr: ByteString = ByteString.copyFrom(plainBytes)
            val cipherText: ByteArray = aead.encrypt(pStr.toByteArray(), null)
            encodedString = Base64.encodeToString(cipherText, Base64.NO_WRAP)
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        return encodedString
    }

    fun decryptString(encryptedText: String?): String? {
//       prevent decryption if value is null
        encryptedText ?: return null
        val aead = aead ?: return null

        var decryptedText: String? = null

        try {
            val ciphertext: ByteArray = Base64.decode(encryptedText, Base64.NO_WRAP)
            val plaintext: ByteArray = aead.decrypt(ciphertext, null)
            decryptedText = String(plaintext, Charsets.UTF_8)
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

        return decryptedText
    }

    fun createNewKeys(context: Context, keyAlias: String) {
        try {
            aead = getOrGenerateNewKeysetHandle(context, keyAlias)?.getPrimitive(Aead::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCipher(): Cipher {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { // below android m
            return Cipher.getInstance("RSA/ECB/PKCS1Padding",
                "AndroidOpenSSL") // error in android 6: InvalidKeyException: Need RSA private or public key
        } else { // android m and above
            return Cipher.getInstance("RSA/ECB/PKCS1Padding") // error in android 5: NoSuchProviderException: Provider not available: AndroidKeyStoreBCWorkaround
        }
    }

    fun generateCodeChallenge(): String {
        val randomGenerator = Random(System.currentTimeMillis())
        val randomBytes: ByteArray = randomGenerator.nextBytes(Constants.CODE_CHALLENGE_BYTE_COUNT)
        return randomBytes.toHexString()
    }

    fun encryptCodeChallenge(codeChallenge: String): String {
        return convertToBase64UrlEncoded(getSha256(codeChallenge))
    }

    fun getSha256(hexString: String): String {
//        convert hexString to bytes
        val bytes = hexString.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val sha256 = digest.fold("") { str, byte -> str + "%02x".format(byte) }
        return sha256
    }

    fun convertToBase64UrlEncoded(value: String): String {
        val base64String = Base64.encodeToString(value.hexStringToByteArray(), Base64.NO_WRAP)
        return base64String
            .replace("+", "-")
            .replace("/", "_")
            .replace("=", "")
    }

    fun String.hexStringToByteArray(): ByteArray {
        return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }

    fun ByteArray.toHexString(): String {
        return joinToString(separator = "") { byte -> "%02x".format(byte) }
    }

}