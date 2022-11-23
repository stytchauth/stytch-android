package com.stytch.sdk

import android.content.Context
import android.util.Base64
import com.google.crypto.tink.Aead
import com.google.crypto.tink.CleartextKeysetHandle
import com.google.crypto.tink.JsonKeysetWriter
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.PublicKeySign
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.google.crypto.tink.proto.Ed25519PublicKey
import com.google.crypto.tink.shaded.protobuf.ByteString
import com.google.crypto.tink.signature.SignatureConfig
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import kotlin.random.Random
import org.json.JSONObject

private const val HEX_RADIX = 16

@Suppress("TooManyFunctions")
internal object EncryptionManager {

    private const val PREF_FILE_NAME = "stytch_secured_pref"
    private const val MASTER_KEY_URI = "android-keystore://stytch_master_key"
    private var aead: Aead? = null
    private var ed25519Manager: AndroidKeysetManager? = null

    init {
        AeadConfig.register()
        SignatureConfig.register()
    }

    private fun getOrGenerateNewAES256KeysetHandle(context: Context, keyAlias: String): KeysetHandle? {
        return AndroidKeysetManager.Builder()
            .withSharedPref(context, keyAlias, PREF_FILE_NAME)
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle
    }

    private fun getOrGenerateNewEd25519KeysetManager(context: Context, keyAlias: String): AndroidKeysetManager {
        return AndroidKeysetManager.Builder()
            .withSharedPref(context, keyAlias, PREF_FILE_NAME)
            .withKeyTemplate(KeyTemplates.get("ED25519WithRawOutput"))
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
        encodedString = Base64.encodeToString(cipherText, Base64.NO_WRAP)
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
        val ciphertext: ByteArray = Base64.decode(encryptedText, Base64.NO_WRAP)
        val plaintext: ByteArray = aead.decrypt(ciphertext, null)
        decryptedText = String(plaintext, Charsets.UTF_8)

        return decryptedText
    }

    /**
     * @throws Exception - if failed to generate keys
     */
    fun createNewKeys(context: Context, rsaKeyAlias: String, ed25519KeyAlias: String) {
        aead = getOrGenerateNewAES256KeysetHandle(context, rsaKeyAlias)?.getPrimitive(Aead::class.java)
        ed25519Manager = getOrGenerateNewEd25519KeysetManager(context, ed25519KeyAlias)
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
        val base64String = Base64.encodeToString(value.hexStringToByteArray(), Base64.NO_WRAP)
        return base64String
            .replace("+", "-")
            .replace("/", "_")
            .replace("=", "")
    }

    private fun String.hexStringToByteArray(): ByteArray {
        return chunked(2).map { it.toInt(HEX_RADIX).toByte() }.toByteArray()
    }

    private fun ByteArray.toHexString(): String {
        return joinToString(separator = "") { byte -> "%02x".format(byte) }
    }

    fun getOrGenerateEd25519PublicKey(): String? {
        val keysetHandle = ed25519Manager?.keysetHandle ?: return null
        val publicKeysetHandle = keysetHandle.publicKeysetHandle
        val publicKeyStream = ByteArrayOutputStream()
        CleartextKeysetHandle.write(publicKeysetHandle, JsonKeysetWriter.withOutputStream(publicKeyStream))
        val publicKeyJson = JSONObject(publicKeyStream.toString())
        val publicKeyString = publicKeyJson
            .getJSONArray("key")
            .getJSONObject(0)
            .getJSONObject("keyData")
            .getString("value")
        val publicKeyJsonBytes = Base64.decode(publicKeyString, Base64.DEFAULT)
        val publicKey = Ed25519PublicKey.parseFrom(ByteString.copyFrom(publicKeyJsonBytes))
        return Base64.encodeToString(publicKey.keyValue.toByteArray(), Base64.DEFAULT)
    }

    fun signEd25519CodeChallenge(challengeString: String): String? {
        val keysetHandle = ed25519Manager?.keysetHandle ?: return null
        val challenge = Base64.decode(challengeString, Base64.DEFAULT)
        val publicKeysetHandle = keysetHandle.publicKeysetHandle
        val signer = publicKeysetHandle.getPrimitive(PublicKeySign::class.java)
        val signature = signer.sign(challenge)
        return Base64.encodeToString(signature, Base64.DEFAULT)
    }

    fun deleteEd25519Key() {
        val keysetManager = ed25519Manager ?: return
        val keysetHandle = keysetManager.keysetHandle
        keysetManager.delete(keysetHandle.primary.id)
    }
}
