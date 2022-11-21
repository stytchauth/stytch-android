package com.stytch.sdk

import android.content.Context
import com.stytch.sdk.network.StytchApi
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.security.KeyStore

private const val PUBLIC_TOKEN = "12345asf"
private const val EMAIL = "email@email.com"
private const val LOGIN_MAGIC_LINK = "loginMagicLink://"
internal class RequestsUnitTest {

    private lateinit var mockWebServer: MockWebServer
    private val mockContext = mockk<Context>(relaxed = true)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    val dispatcher = Dispatchers.Unconfined

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start(12345)
        StytchApi.hostUrl = mockWebServer.url("/").toString()
        mockkConstructor(StorageHelper::class)
        mockkStatic(KeyStore::class)
        mockkObject(EncryptionManager)
        every { EncryptionManager.createNewKeys(any(), any()) } returns Unit
        Dispatchers.setMain(mainThreadSurrogate)
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        every { anyConstructed<StorageHelper>().loadValue(any()) } returns ""
        every { anyConstructed<StorageHelper>().generateHashedCodeChallenge() } returns Pair("", "")
        StytchClient.configure(mockContext, PUBLIC_TOKEN)
        StytchClient.setDispatchers(dispatcher, dispatcher)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    //region MagicLinks

    @Test
    fun `check magic links email loginOrCreate request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            StytchClient.magicLinks.email.loginOrCreate(MagicLinks.EmailMagicLinks.Parameters(EMAIL, LOGIN_MAGIC_LINK))
            val request =  mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/magic_links/email/login_or_create")
            assert(!request.getHeader("Authorization").isNullOrBlank())
            assert(body.contains("email\":\"${EMAIL}\""))
            assert(body.contains("login_magic_link_url\":\"${LOGIN_MAGIC_LINK}\""))
         }
    }

    @Test
    fun `check magic links authenticate request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            StytchClient.magicLinks.authenticate(MagicLinks.AuthParameters("token", 60u))
            val request =  mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/magic_links/authenticate")
            assert(!request.getHeader("Authorization").isNullOrBlank())
            assert(body.contains("token\":\"token\""))
            assert(body.contains("session_duration_minutes\":60"))
        }
    }

    //endregion MagicLinks

    //region OTP
    @Test
    fun `check OTP email loginOrCreate with default expiration request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            StytchClient.otps.email.loginOrCreate(OTP.EmailOTP.Parameters(EMAIL))
            val request =  mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/otps/email/login_or_create")
            assert(!request.getHeader("Authorization").isNullOrBlank())
            assert(body.contains("email\":\"${EMAIL}\""))
            assert(body.contains("expiration_minutes\":${Constants.DEFAULT_OTP_EXPIRATION_TIME_MINUTES}"))
        }
    }

    @Test
    fun `check OTP email loginOrCreate request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            StytchClient.otps.email.loginOrCreate(OTP.EmailOTP.Parameters(EMAIL, 24u))
            val request =  mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/otps/email/login_or_create")
            assert(!request.getHeader("Authorization").isNullOrBlank())
            assert(body.contains("email\":\"${EMAIL}\""))
            assert(body.contains("expiration_minutes\":24"))
        }
    }

    @Test
    fun `check OTP sms loginOrCreate request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            StytchClient.otps.sms.loginOrCreate(OTP.SmsOTP.Parameters("000", 24u))
            val request =  mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/otps/sms/login_or_create")
            assert(!request.getHeader("Authorization").isNullOrBlank())
            assert(body.contains("phone_number\":\"000\""))
            assert(body.contains("expiration_minutes\":24"))
        }
    }

    @Test
    fun `check OTP sms loginOrCreate with default expiration request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            StytchClient.otps.sms.loginOrCreate(OTP.SmsOTP.Parameters("000"))
            val request =  mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/otps/sms/login_or_create")
            assert(!request.getHeader("Authorization").isNullOrBlank())
            assert(body.contains("phone_number\":\"000\""))
            assert(body.contains("expiration_minutes\":${Constants.DEFAULT_OTP_EXPIRATION_TIME_MINUTES}"))
        }
    }

    @Test
    fun `check OTP whatsapp loginOrCreate with default expiration request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            StytchClient.otps.whatsapp.loginOrCreate(OTP.WhatsAppOTP.Parameters("000"))
            val request =  mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/otps/whatsapp/login_or_create")
            assert(!request.getHeader("Authorization").isNullOrBlank())
            assert(body.contains("phone_number\":\"000\""))
            assert(body.contains("expiration_minutes\":${Constants.DEFAULT_OTP_EXPIRATION_TIME_MINUTES}"))
        }
    }

    @Test
    fun `check OTP whatsapp loginOrCreate request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            StytchClient.otps.whatsapp.loginOrCreate(OTP.WhatsAppOTP.Parameters("000", 24u))
            val request =  mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/otps/whatsapp/login_or_create")
            assert(!request.getHeader("Authorization").isNullOrBlank())
            assert(body.contains("phone_number\":\"000\""))
            assert(body.contains("expiration_minutes\":24"))
        }
    }

    @Test
    fun `check OTP authenticate request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            StytchClient.otps.authenticate(OTP.AuthParameters("token", "methodId", 60u))
            val request =  mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/otps/authenticate")
            assert(!request.getHeader("Authorization").isNullOrBlank())
            assert(body.contains("token\":\"token\""))
            assert(body.contains("method_id\":\"methodId\""))
            assert(body.contains("session_duration_minutes\":60"))
        }
    }

    //endregion OTP

    //region Passwords

    @Test
    fun `check Passwords create request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            StytchClient.passwords.create(Passwords.CreateParameters(EMAIL,"123asd"))
            val request =  mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/passwords")
            assert(!request.getHeader("Authorization").isNullOrBlank())
            assert(body.contains("email\":\"${EMAIL}\""))
            assert(body.contains("password\":\"123asd\""))
            assert(body.contains("session_duration_minutes\":${Constants.DEFAULT_SESSION_TIME_MINUTES}"))

        }
    }

    @Test
    fun `check Passwords strenghtCheck request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            StytchClient.passwords.strengthCheck (Passwords.StrengthCheckParameters(EMAIL, "123asd"))
            val request =  mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/passwords/strength_check")
            assert(!request.getHeader("Authorization").isNullOrBlank())
            assert(body.contains("email\":\"${EMAIL}\""))
            assert(body.contains("password\":\"123asd\""))

        }
    }

    @Test
    fun `check Passwords resetbyEmail request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            StytchClient.passwords.resetByEmail (Passwords.ResetByEmailParameters("token", "123asd"))
            val request =  mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/passwords/email/reset")
            assert(!request.getHeader("Authorization").isNullOrBlank())
            assert(body.contains("token\":\"token\""))
            assert(body.contains("password\":\"123asd\""))
            assert(body.contains("session_duration_minutes\":${Constants.DEFAULT_SESSION_TIME_MINUTES}"))

        }
    }

    @Test
    fun `check Passwords resetbyEmailStart request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            StytchClient.passwords.resetByEmailStart(Passwords.ResetByEmailStartParameters(EMAIL))
            val request =  mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/passwords/email/reset/start")
            assert(!request.getHeader("Authorization").isNullOrBlank())
            assert(body.contains("email\":\"${EMAIL}\""))
        }
    }

    @Test
    fun `check Passwords authenticate request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            StytchClient.passwords.authenticate(Passwords.AuthParameters(EMAIL, "123asd"))
            val request =  mockWebServer.takeRequest()
            assert(request.method == "POST")
            val body = request.body.readUtf8()
            assert(request.path == "/passwords/authenticate")
            assert(!request.getHeader("Authorization").isNullOrBlank())
            assert(body.contains("email\":\"${EMAIL}\""))
            assert(body.contains("password\":\"123asd\""))
            assert(body.contains("session_duration_minutes\":${Constants.DEFAULT_SESSION_TIME_MINUTES}"))
        }
    }

    //endregion Passwords

    //region Sessions

    @Test
    fun `check Sessions authenticate request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            StytchClient.sessions.authenticate(Sessions.AuthParams())
            val request =  mockWebServer.takeRequest()
            assert(request.method == "POST")
            assert(request.path == "/sessions/authenticate")
            assert(!request.getHeader("Authorization").isNullOrBlank())
        }
    }

    @Test
    fun `check Sessions revoke request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        runBlocking {
            StytchClient.sessions.revoke()
            val request =  mockWebServer.takeRequest()
            assert(request.method == "POST")
            assert(request.path == "/sessions/revoke")
            assert(!request.getHeader("Authorization").isNullOrBlank())
        }
    }

    //endregion Sessions

}
