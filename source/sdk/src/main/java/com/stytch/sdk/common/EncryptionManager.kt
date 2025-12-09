package com.stytch.sdk.common

import android.content.Context
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.google.crypto.tink.shaded.protobuf.ByteString
import com.google.crypto.tink.signature.SignatureConfig
import com.stytch.sdk.common.errors.StytchChallengeSigningFailed
import com.stytch.sdk.common.errors.StytchMissingPublicKeyError
import com.stytch.sdk.common.extensions.clearPreferences
import com.stytch.sdk.common.extensions.hexStringToByteArray
import com.stytch.sdk.common.extensions.toBase64DecodedByteArray
import com.stytch.sdk.common.extensions.toBase64EncodedString
import com.stytch.sdk.common.extensions.toHexString
import org.bouncycastle.crypto.Signer
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.MessageDigest
import java.security.SecureRandom

@Suppress("TooManyFunctions")
internal object EncryptionManager {
    private const val KEY_ALIAS = "Stytch RSA 2048"
    internal const val KEY_PREFERENCES_FILE_NAME = "stytch_secured_pref"
    internal const val MASTER_KEY_ALIAS = "stytch_master_key"
    private const val MASTER_KEY_URI = "android-keystore://$MASTER_KEY_ALIAS"
    private var keysetManager: AndroidKeysetManager? = null
    private var aead: Aead? = null
    private const val MAX_KEY_GEN_ATTEMPTS = 3

    init {
        AeadConfig.register()
        SignatureConfig.register()
    }

    private fun getOrGenerateNewAES256KeysetManager(
        context: Context,
        keyStore: KeyStore,
    ): AndroidKeysetManager? =
        try {
            AndroidKeysetManager
                .Builder()
                .withSharedPref(context, KEY_ALIAS, KEY_PREFERENCES_FILE_NAME)
                .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
                .withMasterKeyUri(MASTER_KEY_URI)
                .build()
        } catch (_: KeyStoreException) {
            // if a keystore exception occurred, destroy the key and the preference files
            keyStore.deleteEntry(MASTER_KEY_ALIAS)
            context.clearPreferences(listOf(KEY_PREFERENCES_FILE_NAME, STYTCH_PREFERENCES_FILE_NAME))
            null
        } catch (_: Exception) {
            // If a non-keystore exception occurred, we can keep the key, but should clear the preference files
            context.clearPreferences(listOf(KEY_PREFERENCES_FILE_NAME, STYTCH_PREFERENCES_FILE_NAME))
            null
        }

    /**
     * @throws Exception if failed to encrypt text
     */
    fun encryptString(plainText: String): String {
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
    fun createNewKeys(
        context: Context,
        keyStore: KeyStore,
    ) {
        var ksm: AndroidKeysetManager? = null
        var i = 0
        while (ksm == null && i < MAX_KEY_GEN_ATTEMPTS) {
            ksm = getOrGenerateNewAES256KeysetManager(context, keyStore)
            i++
        }
        keysetManager = ksm ?: error("Unrecoverable error attempting to retrieve keys")
        aead = ksm.keysetHandle.getPrimitive(Aead::class.java)
    }

    fun generateCodeVerifier(): String {
        val randomBytes = ByteArray(CODE_CHALLENGE_BYTE_COUNT)
        SecureRandom().nextBytes(randomBytes)
        return randomBytes.toHexString()
    }

    fun encryptCodeChallengeFromVerifier(codeVerifier: String): String =
        convertToBase64UrlEncoded(getSha256(codeVerifier))

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

    fun generateEd25519KeyPair(): Pair<String, String> =
        try {
            val gen = Ed25519KeyPairGenerator()
            gen.init(Ed25519KeyGenerationParameters(SecureRandom()))
            val keyPair = gen.generateKeyPair()
            val publicKey = keyPair.public as Ed25519PublicKeyParameters
            val privateKey = keyPair.private as Ed25519PrivateKeyParameters
            Pair(publicKey.encoded.toBase64EncodedString(), privateKey.encoded.toBase64EncodedString())
        } catch (e: Exception) {
            throw StytchMissingPublicKeyError(e)
        }

    fun signEd25519Challenge(
        challengeString: String,
        privateKeyString: String,
    ): String =
        try {
            val signer: Signer = Ed25519Signer()
            val challenge = challengeString.toBase64DecodedByteArray()
            val privateKey = Ed25519PrivateKeyParameters(privateKeyString.toBase64DecodedByteArray())
            signer.init(true, privateKey)
            signer.update(challenge, 0, challenge.size)
            val signature: ByteArray = signer.generateSignature()
            signature.toBase64EncodedString()
        } catch (e: Exception) {
            throw StytchChallengeSigningFailed(e)
        }

    fun deriveEd25519PublicKeyFromPrivateKeyBytes(privateKeyBytes: ByteArray): String =
        try {
            val privateKeyRebuild = Ed25519PrivateKeyParameters(privateKeyBytes, 0)
            val publicKeyRebuild = privateKeyRebuild.generatePublicKey()
            publicKeyRebuild.encoded.toBase64EncodedString()
        } catch (e: Exception) {
            throw StytchMissingPublicKeyError(e)
        }
}
