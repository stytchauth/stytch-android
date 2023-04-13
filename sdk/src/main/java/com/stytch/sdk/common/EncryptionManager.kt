package com.stytch.sdk.common

import android.content.Context
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.google.crypto.tink.shaded.protobuf.ByteString
import com.google.crypto.tink.signature.SignatureConfig
import com.stytch.sdk.common.extensions.hexStringToByteArray
import com.stytch.sdk.common.extensions.toBase64DecodedByteArray
import com.stytch.sdk.common.extensions.toBase64EncodedString
import com.stytch.sdk.common.extensions.toHexString
import com.stytch.sdk.common.network.StytchErrorType
import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.random.Random
import org.bouncycastle.crypto.Signer
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer

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

    private fun getOrGenerateNewAES256KeysetManager(context: Context, keyAlias: String): AndroidKeysetManager {
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
        val ksm = getOrGenerateNewAES256KeysetManager(context, rsaKeyAlias)
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

    fun generateEd25519KeyPair(): Pair<String, String> = try {
        val gen = Ed25519KeyPairGenerator()
        gen.init(Ed25519KeyGenerationParameters(SecureRandom()))
        val keyPair = gen.generateKeyPair()
        val publicKey = keyPair.public as Ed25519PublicKeyParameters
        val privateKey = keyPair.private as Ed25519PrivateKeyParameters
        Pair(publicKey.encoded.toBase64EncodedString(), privateKey.encoded.toBase64EncodedString())
    } catch (e: Exception) {
        throw StytchExceptions.Input(StytchErrorType.KEY_GENERATION_FAILED.message)
    }

    fun signEd25519Challenge(challengeString: String, privateKeyString: String): String = try {
        val signer: Signer = Ed25519Signer()
        val challenge = challengeString.toBase64DecodedByteArray()
        val privateKey = Ed25519PrivateKeyParameters(privateKeyString.toBase64DecodedByteArray())
        signer.init(true, privateKey)
        signer.update(challenge, 0, challenge.size)
        val signature: ByteArray = signer.generateSignature()
        signature.toBase64EncodedString()
    } catch (e: Exception) {
        throw StytchExceptions.Input(StytchErrorType.ERROR_SIGNING_CHALLENGE.message)
    }

    fun deriveEd25519PublicKeyFromPrivateKeyBytes(privateKeyBytes: ByteArray): String = try {
        val privateKeyRebuild = Ed25519PrivateKeyParameters(privateKeyBytes, 0)
        val publicKeyRebuild = privateKeyRebuild.generatePublicKey()
        publicKeyRebuild.encoded.toBase64EncodedString()
    } catch (e: Exception) {
        throw StytchExceptions.Input(StytchErrorType.ERROR_DERIVING_PUBLIC_KEY.message)
    }
}