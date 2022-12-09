package com.stytch.sdk

import android.content.Context
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.google.crypto.tink.shaded.protobuf.ByteString
import com.google.crypto.tink.signature.SignatureConfig
import com.stytch.sdk.extensions.hexStringToByteArray
import com.stytch.sdk.extensions.toBase64DecodedByteArray
import com.stytch.sdk.extensions.toBase64EncodedString
import com.stytch.sdk.extensions.toHexString
import java.security.MessageDigest
import kotlin.random.Random

@Suppress("TooManyFunctions")
internal object EncryptionManager {

    private const val PREF_FILE_NAME = "stytch_secured_pref"
    private const val MASTER_KEY_URI = "android-keystore://stytch_master_key"
    private var keysetManager: AndroidKeysetManager? = null
    private var aead: Aead? = null

    init {
        AeadConfig.register()
        SignatureConfig.register()
    }

    private fun getOrGenerateNewAES256KeysetHandle(context: Context, keyAlias: String): AndroidKeysetManager {
        return AndroidKeysetManager.Builder()
            .withSharedPref(context, keyAlias, PREF_FILE_NAME)
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
    }

    /**
     * @throws Exception if failed to encrypt text
     */
    fun encryptString(plainText: String): String? {
        val encodedString: String?
        val aead = aead ?: throw Exception()
        val plainBytes: ByteArray = plainText.toByteArray(Charsets.UTF_8)
        // An artifical step to test whether Tink can co-exist with protobuf-lite.
        val pStr: ByteString = ByteString.copyFrom(plainBytes)
        val cipherText: ByteArray = aead.encrypt(pStr.toByteArray(), null)
        encodedString = cipherText.toBase64EncodedString()
        return encodedString
    }

    /**
     * @throws Exception if failed to decrypt text
     */
    @Suppress("ReturnCount")
    fun decryptString(encryptedText: String?): String? {
        // prevent decryption if value is null
        encryptedText ?: return null
        val aead = aead ?: return null

        val decryptedText: String?
        val ciphertext: ByteArray = encryptedText.toBase64DecodedByteArray()
        val plaintext: ByteArray = aead.decrypt(ciphertext, null)
        decryptedText = String(plaintext, Charsets.UTF_8)

        return decryptedText
    }

    /**
     * @throws Exception - if failed to generate keys
     */
    fun createNewKeys(context: Context, rsaKeyAlias: String) {
        val ksm = getOrGenerateNewAES256KeysetHandle(context, rsaKeyAlias)
        keysetManager = ksm
        aead = ksm.keysetHandle.getPrimitive(Aead::class.java)
    }

    fun generateCodeChallenge(): String {
        val randomGenerator = Random(System.currentTimeMillis())
        val randomBytes: ByteArray = randomGenerator.nextBytes(Constants.CODE_CHALLENGE_BYTE_COUNT)
        return randomBytes.toHexString()
    }

    fun encryptCodeChallenge(codeChallenge: String): String {
        return convertToBase64UrlEncoded(getSha256(codeChallenge))
    }

    private fun getSha256(hexString: String): String {
        // convert hexString to bytes
        val bytes = hexString.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val sha256 = digest.fold("") { str, byte -> str + "%02x".format(byte) }
        return sha256
    }

    private fun convertToBase64UrlEncoded(value: String): String {
        val base64String = value.hexStringToByteArray().toBase64EncodedString()
        return base64String
            .replace("+", "-")
            .replace("/", "_")
            .replace("=", "")
    }

    fun isKeysetUsingKeystore(): Boolean = keysetManager?.isUsingKeystore == true
}
