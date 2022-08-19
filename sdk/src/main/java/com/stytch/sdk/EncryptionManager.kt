package com.stytch.sdk

import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.MessageDigest
import java.util.Calendar
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.security.auth.x500.X500Principal
import kotlin.random.Random

internal object EncryptionManager {
    fun encryptString(keyStore: KeyStore, alias: String, plainText: String): String? {
        var encodedString: String? = null
        try {
            val privateKeyEntry = keyStore.getEntry(alias, null) as KeyStore.PrivateKeyEntry
            val publicKey = privateKeyEntry.certificate.publicKey

            // Encrypt the text
            val input: Cipher = getCipher()
            input.init(Cipher.ENCRYPT_MODE, publicKey)
            val outputStream = ByteArrayOutputStream()
            val cipherOutputStream = CipherOutputStream(
                outputStream, input)
            cipherOutputStream.write(plainText.toByteArray(charset("UTF-8")))
            cipherOutputStream.close()
            val vals: ByteArray = outputStream.toByteArray()
            encodedString = Base64.encodeToString(vals, Base64.NO_WRAP)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return encodedString
    }

    fun decryptString(keyStore: KeyStore, alias: String, encryptedText: String?): String? {
        var decryptedText: String? = null
        try {
            val privateKeyEntry = keyStore.getEntry(alias, null) as KeyStore.PrivateKeyEntry
            val privateKey = privateKeyEntry.privateKey
            val output = getCipher()
            output.init(Cipher.DECRYPT_MODE, privateKey)
            val cipherInputStream = CipherInputStream(
                ByteArrayInputStream(Base64.decode(encryptedText, Base64.NO_WRAP)), output)
            val values: ArrayList<Byte> = ArrayList()
            var nextByte: Int
            while (cipherInputStream.read().also { nextByte = it } != -1) {
                values.add(nextByte.toByte())
            }
            val bytes = ByteArray(values.size)
            for (i in bytes.indices) {
                bytes[i] = values[i]
            }
            decryptedText = String(bytes, 0, bytes.size, Charset.forName("UTF-8"))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return decryptedText
    }

    fun createNewKeys(context: Context, keyStore: KeyStore, alias: String) {
        try {
            // Create new key if needed
            if (!keyStore.containsAlias(alias)) {
                    val start = Calendar.getInstance()
                    val end = Calendar.getInstance()
                    end.add(Calendar.YEAR, 1)
                    val spec = KeyPairGeneratorSpec.Builder(context)
                        .setAlias(alias)
                        .setSubject(X500Principal("CN=stytch CN, O=Android Authority"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.time)
                        .setEndDate(end.time)
                        .build()
                    val generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore")
                    generator.initialize(spec)
                    generator.generateKeyPair()
            }
        } catch (e: java.lang.Exception) {
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
        val randomBytes: ByteArray = Random.nextBytes(Constants.CODE_CHALLENGE_BYTE_COUNT)
        return randomBytes.joinToString(separator = "") { byte -> "%02x".format(byte) }
    }

    fun encryptCodeChallenge(codeChallenge: String): String{
        return convertToBase64UrlEncoded(getSha256(codeChallenge))
    }

    fun getSha256(value: String): String {
        val bytes = value.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val sha256 = digest.fold("") { str, byte -> str + "%02x".format(byte) }
        return sha256
    }

    fun convertToBase64UrlEncoded(value: String): String {
        val base64String = Base64.encode(value.toByteArray(Charsets.UTF_8), Base64.NO_WRAP).toString()
        return base64String
            .replace("+", "-")
            .replace("/", "_")
            .replace("=", "")
    }

}