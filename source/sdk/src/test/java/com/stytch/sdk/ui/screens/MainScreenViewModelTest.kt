package com.stytch.sdk.ui.screens

import androidx.lifecycle.SavedStateHandle
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchAPIError
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.network.models.UserType
import com.stytch.sdk.ui.b2c.screens.MainScreenViewModel
import com.stytch.sdk.ui.b2c.data.EventState
import com.stytch.sdk.ui.b2c.data.NavigationRoute
import com.stytch.sdk.ui.b2c.data.OAuthProvider
import com.stytch.sdk.ui.b2c.data.OTPDetails
import com.stytch.sdk.ui.b2c.data.OTPMethods
import com.stytch.sdk.ui.b2c.data.OTPOptions
import com.stytch.sdk.ui.b2c.data.PasswordResetType
import com.stytch.sdk.ui.b2c.data.StytchProductConfig
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.spyk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.security.KeyStore

@OptIn(ExperimentalCoroutinesApi::class)
internal class MainScreenViewModelTest {
    private val savedStateHandle = SavedStateHandle()
    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: MainScreenViewModel
    private lateinit var mockStytchClient: StytchClient

    @Before
    fun before() {
        mockkStatic(KeyStore::class)
        mockkStatic(StytchClient::class)
        every { KeyStore.getInstance(any()) } returns mockk(relaxed = true)
        MockKAnnotations.init(this, true, true, true)
        mockStytchClient =
            mockk {
                every { events } returns
                    mockk(relaxed = true) {
                        every { logEvent(any(), any()) } just runs
                    }
            }
        viewModel = spyk(MainScreenViewModel(savedStateHandle, mockStytchClient))
    }

    @After
    fun after() {
        unmockkAll()
    }

    @Test
    fun `onStartOAuthLogin delegates to OneTap if configured`() =
        runTest(dispatcher) {
            val mockProductConfig: StytchProductConfig =
                mockk {
                    every { googleOauthOptions } returns
                        mockk {
                            every { clientId } returns "google-client-id"
                        }
                }
            coEvery {
                mockStytchClient.oauth.googleOneTap.start(
                    any(),
                )
            } returns StytchResult.Success(mockk(relaxed = true))
            every { mockStytchClient.oauth.google.start(any()) } throws Exception("THIS SHOULD NOT BE CALLED")
            viewModel.onStartOAuthLogin(mockk(relaxed = true), OAuthProvider.GOOGLE, mockProductConfig, this)
            coVerify { mockStytchClient.oauth.googleOneTap.start(any()) }
        }

    @Test
    fun `onStartOAuthLogin delegates to third party if not configured`() =
        runTest(dispatcher) {
            val mockProductConfig: StytchProductConfig =
                mockk {
                    every { googleOauthOptions } returns
                        mockk {
                            every { clientId } returns null
                        }
                    every { oAuthOptions } returns mockk(relaxed = true)
                }
            coEvery { mockStytchClient.oauth.googleOneTap.start(any()) } throws Exception("THIS SHOULD NOT BE CALLED")
            every { mockStytchClient.oauth.google.start(any()) } just runs
            viewModel.onStartOAuthLogin(mockk(relaxed = true), OAuthProvider.GOOGLE, mockProductConfig, this)
            coVerify { mockStytchClient.oauth.google.start(any()) }
        }

    @Test
    fun `onStartOAuthLogin delegates to third party if not Google`() =
        runTest(dispatcher) {
            val mockProductConfig: StytchProductConfig =
                mockk {
                    every { googleOauthOptions } returns
                        mockk {
                            every { clientId } returns null
                        }
                    every { oAuthOptions } returns mockk(relaxed = true)
                }
            every { mockStytchClient.oauth.amazon.start(any()) } just runs
            viewModel.onStartOAuthLogin(mockk(relaxed = true), OAuthProvider.AMAZON, mockProductConfig, this)
            coVerify { mockStytchClient.oauth.amazon.start(any()) }
        }

    @Test
    fun `onStartThirdPartyOAuth maps to the correct handler`() {
        coEvery { mockStytchClient.oauth.amazon.start(any()) } just runs
        coEvery { mockStytchClient.oauth.apple.start(any()) } just runs
        coEvery { mockStytchClient.oauth.bitbucket.start(any()) } just runs
        coEvery { mockStytchClient.oauth.coinbase.start(any()) } just runs
        coEvery { mockStytchClient.oauth.discord.start(any()) } just runs
        coEvery { mockStytchClient.oauth.facebook.start(any()) } just runs
        coEvery { mockStytchClient.oauth.figma.start(any()) } just runs
        coEvery { mockStytchClient.oauth.gitlab.start(any()) } just runs
        coEvery { mockStytchClient.oauth.github.start(any()) } just runs
        coEvery { mockStytchClient.oauth.google.start(any()) } just runs
        coEvery { mockStytchClient.oauth.linkedin.start(any()) } just runs
        coEvery { mockStytchClient.oauth.microsoft.start(any()) } just runs
        coEvery { mockStytchClient.oauth.salesforce.start(any()) } just runs
        coEvery { mockStytchClient.oauth.slack.start(any()) } just runs
        coEvery { mockStytchClient.oauth.snapchat.start(any()) } just runs
        coEvery { mockStytchClient.oauth.tiktok.start(any()) } just runs
        coEvery { mockStytchClient.oauth.twitch.start(any()) } just runs
        coEvery { mockStytchClient.oauth.twitter.start(any()) } just runs

        viewModel.onStartThirdPartyOAuth(mockk(relaxed = true), OAuthProvider.AMAZON, null)
        coVerify { mockStytchClient.oauth.amazon.start(any()) }
        viewModel.onStartThirdPartyOAuth(mockk(relaxed = true), OAuthProvider.APPLE, null)
        coVerify { mockStytchClient.oauth.apple.start(any()) }
        viewModel.onStartThirdPartyOAuth(mockk(relaxed = true), OAuthProvider.BITBUCKET, null)
        coVerify { mockStytchClient.oauth.bitbucket.start(any()) }
        viewModel.onStartThirdPartyOAuth(mockk(relaxed = true), OAuthProvider.COINBASE, null)
        coVerify { mockStytchClient.oauth.coinbase.start(any()) }
        viewModel.onStartThirdPartyOAuth(mockk(relaxed = true), OAuthProvider.DISCORD, null)
        coVerify { mockStytchClient.oauth.discord.start(any()) }
        viewModel.onStartThirdPartyOAuth(mockk(relaxed = true), OAuthProvider.FACEBOOK, null)
        coVerify { mockStytchClient.oauth.facebook.start(any()) }
        viewModel.onStartThirdPartyOAuth(mockk(relaxed = true), OAuthProvider.FIGMA, null)
        coVerify { mockStytchClient.oauth.figma.start(any()) }
        viewModel.onStartThirdPartyOAuth(mockk(relaxed = true), OAuthProvider.GITLAB, null)
        coVerify { mockStytchClient.oauth.gitlab.start(any()) }
        viewModel.onStartThirdPartyOAuth(mockk(relaxed = true), OAuthProvider.GITHUB, null)
        coVerify { mockStytchClient.oauth.github.start(any()) }
        viewModel.onStartThirdPartyOAuth(mockk(relaxed = true), OAuthProvider.GOOGLE, null)
        coVerify { mockStytchClient.oauth.google.start(any()) }
        viewModel.onStartThirdPartyOAuth(mockk(relaxed = true), OAuthProvider.LINKEDIN, null)
        coVerify { mockStytchClient.oauth.linkedin.start(any()) }
        viewModel.onStartThirdPartyOAuth(mockk(relaxed = true), OAuthProvider.MICROSOFT, null)
        coVerify { mockStytchClient.oauth.microsoft.start(any()) }
        viewModel.onStartThirdPartyOAuth(mockk(relaxed = true), OAuthProvider.SALESFORCE, null)
        coVerify { mockStytchClient.oauth.salesforce.start(any()) }
        viewModel.onStartThirdPartyOAuth(mockk(relaxed = true), OAuthProvider.SLACK, null)
        coVerify { mockStytchClient.oauth.slack.start(any()) }
        viewModel.onStartThirdPartyOAuth(mockk(relaxed = true), OAuthProvider.SNAPCHAT, null)
        coVerify { mockStytchClient.oauth.snapchat.start(any()) }
        viewModel.onStartThirdPartyOAuth(mockk(relaxed = true), OAuthProvider.TIKTOK, null)
        coVerify { mockStytchClient.oauth.tiktok.start(any()) }
        viewModel.onStartThirdPartyOAuth(mockk(relaxed = true), OAuthProvider.TWITCH, null)
        coVerify { mockStytchClient.oauth.twitch.start(any()) }
        viewModel.onStartThirdPartyOAuth(mockk(relaxed = true), OAuthProvider.TWITTER, null)
        coVerify { mockStytchClient.oauth.twitter.start(any()) }
    }

    @Test
    fun `onCountryCodeChanged updates the state`() {
        viewModel.onCountryCodeChanged("1")
        assert(viewModel.uiState.value.phoneNumberState.countryCode == "1")
        assert(viewModel.uiState.value.phoneNumberState.error == null)
        assert(viewModel.uiState.value.genericErrorMessage == null)
    }

    @Test
    fun `onPhoneNumberChanged updates the state`() {
        viewModel.onPhoneNumberChanged("1234567890")
        assert(viewModel.uiState.value.phoneNumberState.phoneNumber == "1234567890")
        assert(viewModel.uiState.value.phoneNumberState.error == null)
        assert(viewModel.uiState.value.genericErrorMessage == null)
    }

    @Test
    fun `onEmailAddressChanged updates the state`() {
        viewModel.onEmailAddressChanged("my@email.address")
        assert(viewModel.uiState.value.emailState.emailAddress == "my@email.address")
        assert(viewModel.uiState.value.emailState.validEmail == true)
        assert(viewModel.uiState.value.genericErrorMessage == null)
    }

    @Test
    fun `onEmailAddressSubmit delegates and sends events for new users`() =
        runTest(dispatcher) {
            coEvery { viewModel.getUserType(any()) } returns UserType.NEW
            val eventFlow =
                async {
                    viewModel.eventFlow.first()
                }
            viewModel.onEmailAddressSubmit(mockk(relaxed = true), this)
            assert(eventFlow.await() == EventState.NavigationRequested(NavigationRoute.NewUser))
        }

    @Test
    fun `onEmailAddressSubmit delegates and sends events for password users`() =
        runTest(dispatcher) {
            coEvery { viewModel.getUserType(any()) } returns UserType.PASSWORD
            val eventFlow =
                async {
                    viewModel.eventFlow.first()
                }
            viewModel.onEmailAddressSubmit(mockk(relaxed = true), this)
            assert(eventFlow.await() == EventState.NavigationRequested(NavigationRoute.ReturningUser))
        }

    @Test
    fun `onEmailAddressSubmit delegates and sends events for passwordless users`() =
        runTest(dispatcher) {
            coEvery { viewModel.getUserType(any()) } returns UserType.PASSWORDLESS
            coEvery {
                viewModel.sendEmailOTPForReturningUserAndGetNavigationRoute(any(), any())
            } returns NavigationRoute.OTPConfirmation(mockk(), true, "")
            coEvery {
                viewModel.sendEmailMagicLinkForReturningUserAndGetNavigationRoute(any(), any())
            } returns NavigationRoute.EMLConfirmation(mockk(), true)
            coEvery {
                viewModel.sendResetPasswordForReturningUserAndGetNavigationRoute(any(), any())
            } returns NavigationRoute.PasswordResetSent(mockk())

            // should send Email OTP
            var eventFlow =
                async {
                    viewModel.eventFlow.first()
                }
            val configShouldSendEmailOTP: StytchProductConfig =
                mockk {
                    every { products } returns listOf(com.stytch.sdk.ui.b2c.data.StytchProduct.OTP)
                    every { otpOptions } returns
                        mockk {
                            every { methods } returns listOf(OTPMethods.EMAIL)
                        }
                }
            viewModel.onEmailAddressSubmit(configShouldSendEmailOTP, this)
            var event = eventFlow.await()
            require(event is EventState.NavigationRequested)
            require(event.navigationRoute is NavigationRoute.OTPConfirmation)

            // should send EML
            eventFlow =
                async {
                    viewModel.eventFlow.first()
                }
            val configShouldSendEml: StytchProductConfig =
                mockk {
                    every { products } returns listOf(com.stytch.sdk.ui.b2c.data.StytchProduct.EMAIL_MAGIC_LINKS)
                    every { emailMagicLinksOptions } returns mockk(relaxed = true)
                }
            viewModel.onEmailAddressSubmit(configShouldSendEml, this)
            event = eventFlow.await()
            require(event is EventState.NavigationRequested)
            require(event.navigationRoute is NavigationRoute.EMLConfirmation)

            // should send password reset
            eventFlow =
                async {
                    viewModel.eventFlow.first()
                }
            val configShouldSendPasswordReset: StytchProductConfig =
                mockk {
                    every { products } returns listOf()
                    every { passwordOptions } returns mockk(relaxed = true)
                }
            viewModel.onEmailAddressSubmit(configShouldSendPasswordReset, this)
            event = eventFlow.await()
            require(event is EventState.NavigationRequested)
            require(event.navigationRoute is NavigationRoute.PasswordResetSent)
        }

    @Test
    fun `onEmailAddressSubmit delegates and updates state for unknown users`() =
        runTest(dispatcher) {
            coEvery { viewModel.getUserType(any()) } returns null
            viewModel.onEmailAddressSubmit(mockk(relaxed = true), this)
            assert(viewModel.uiState.value.genericErrorMessage == "Failed to get user type")
        }

    @Test
    fun `getUserType returns as expected`() =
        runTest(dispatcher) {
            coEvery { mockStytchClient.user.search(any()) } returns
                StytchResult.Success(
                    mockk {
                        every { userType } returns UserType.NEW
                    },
                )
            assert(viewModel.getUserType("") == UserType.NEW)
            coEvery { mockStytchClient.user.search(any()) } returns StytchResult.Error(mockk())
            assert(viewModel.getUserType("") == null)
        }

    @Test
    fun `sendEmailMagicLinkForReturningUserAndGetNavigationRoute returns nav route on success`() =
        runTest(dispatcher) {
            every { mockStytchClient.publicToken } returns "publicToken"
            coEvery { mockStytchClient.magicLinks.email.loginOrCreate(any()) } returns StytchResult.Success(mockk())
            val route = viewModel.sendEmailMagicLinkForReturningUserAndGetNavigationRoute("", mockk(relaxed = true))
            require(route is NavigationRoute.EMLConfirmation)
            require(route.isReturningUser)
        }

    @Test
    fun `sendEmailMagicLinkForReturningUserAndGetNavigationRoute updates state and returns null on error`() =
        runTest(dispatcher) {
            every { mockStytchClient.publicToken } returns "publicToken"
            coEvery { mockStytchClient.magicLinks.email.loginOrCreate(any()) } returns
                StytchResult.Error(
                    StytchAPIError(errorType = "", message = "Something went wrong", statusCode = 400),
                )
            val route = viewModel.sendEmailMagicLinkForReturningUserAndGetNavigationRoute("", mockk(relaxed = true))
            assert(route == null)
            assert(viewModel.uiState.value.genericErrorMessage == "Something went wrong")
        }

    @Test
    fun `sendEmailOTPForReturningUserAndGetNavigationRoute returns nav route on success`() =
        runTest(dispatcher) {
            coEvery {
                mockStytchClient.otps.email.loginOrCreate(
                    any(),
                )
            } returns StytchResult.Success(mockk(relaxed = true))
            val route =
                viewModel.sendEmailOTPForReturningUserAndGetNavigationRoute(
                    "my@email.com",
                    mockk(relaxed = true),
                )
            require(route is NavigationRoute.OTPConfirmation)
            require(route.isReturningUser)
            require(route.emailAddress == "my@email.com")
        }

    @Test
    fun `sendEmailOTPForReturningUserAndGetNavigationRoute updates state and returns null on error`() =
        runTest(dispatcher) {
            coEvery { mockStytchClient.otps.email.loginOrCreate(any()) } returns
                StytchResult.Error(
                    StytchAPIError(errorType = "", message = "Something went wrong", statusCode = 400),
                )
            val route = viewModel.sendEmailOTPForReturningUserAndGetNavigationRoute("", mockk(relaxed = true))
            assert(route == null)
            assert(viewModel.uiState.value.genericErrorMessage == "Something went wrong")
        }

    @Test
    fun `sendResetPasswordForReturningUserAndGetNavigationRoute returns nav route on success`() =
        runTest(dispatcher) {
            every { mockStytchClient.publicToken } returns "publicToken"
            coEvery {
                mockStytchClient.passwords.resetByEmailStart(any())
            } returns StytchResult.Success(mockk(relaxed = true))
            val route =
                viewModel.sendResetPasswordForReturningUserAndGetNavigationRoute(
                    "my@email.com",
                    mockk(relaxed = true),
                )
            require(route is NavigationRoute.PasswordResetSent)
            require(route.details.resetType == PasswordResetType.NO_PASSWORD_SET)
        }

    @Test
    fun `sendResetPasswordForReturningUserAndGetNavigationRoute updates state and returns null on error`() =
        runTest(dispatcher) {
            every { mockStytchClient.publicToken } returns "publicToken"
            coEvery { mockStytchClient.passwords.resetByEmailStart(any()) } returns
                StytchResult.Error(
                    StytchAPIError(errorType = "", message = "Something went wrong", statusCode = 400),
                )
            val route = viewModel.sendResetPasswordForReturningUserAndGetNavigationRoute("", mockk(relaxed = true))
            assert(route == null)
            assert(viewModel.uiState.value.genericErrorMessage == "Something went wrong")
        }

    @Test
    fun `sendSmsOTP updates state and emits events as expected`() =
        runTest(dispatcher) {
            // set a phone number first
            viewModel.onCountryCodeChanged("+1")
            viewModel.onPhoneNumberChanged("5555555555")
            val mockOptions: OTPOptions =
                mockk {
                    every { toSMSOtpParameters(any()) } returns mockk()
                }
            coEvery { mockStytchClient.otps.sms.loginOrCreate(any()) } returns
                StytchResult.Success(
                    mockk {
                        every { methodId } returns "my-method-id"
                    },
                )
            val eventFlow =
                async {
                    viewModel.eventFlow.first()
                }
            viewModel.sendSmsOTP(mockOptions, this)
            assert(!viewModel.uiState.value.showLoadingDialog)
            val event = eventFlow.await()
            require(event is EventState.NavigationRequested)
            require(event.navigationRoute is NavigationRoute.OTPConfirmation)
            require(event.navigationRoute.details is OTPDetails.SmsOTP)
            require(event.navigationRoute.details.methodId == "my-method-id")
            require(!event.navigationRoute.isReturningUser)

            // error state
            coEvery { mockStytchClient.otps.sms.loginOrCreate(any()) } returns
                StytchResult.Error(
                    StytchAPIError(errorType = "", message = "Something went wrong", statusCode = 400),
                )
            viewModel.sendSmsOTP(mockOptions, this)
            assert(!viewModel.uiState.value.showLoadingDialog)
            assert(viewModel.uiState.value.phoneNumberState.error == "Something went wrong")
        }

    @Test
    fun `sendWhatsAppOTP updates state and emits events as expected`() =
        runTest(dispatcher) {
            // set a phone number first
            viewModel.onCountryCodeChanged("+1")
            viewModel.onPhoneNumberChanged("5555555555")
            val mockOptions: OTPOptions =
                mockk {
                    every { toWhatsAppOtpParameters(any()) } returns mockk(relaxed = true)
                }
            coEvery { mockStytchClient.otps.whatsapp.loginOrCreate(any()) } returns
                StytchResult.Success(
                    mockk {
                        every { methodId } returns "my-method-id"
                    },
                )
            val eventFlow =
                async {
                    viewModel.eventFlow.first()
                }
            viewModel.sendWhatsAppOTP(mockOptions, this)
            assert(!viewModel.uiState.value.showLoadingDialog)
            val event = eventFlow.await()
            require(event is EventState.NavigationRequested)
            require(event.navigationRoute is NavigationRoute.OTPConfirmation)
            require(event.navigationRoute.details is OTPDetails.WhatsAppOTP)
            require(event.navigationRoute.details.methodId == "my-method-id")
            require(!event.navigationRoute.isReturningUser)

            // error state
            coEvery { mockStytchClient.otps.whatsapp.loginOrCreate(any()) } returns
                StytchResult.Error(
                    StytchAPIError(errorType = "", message = "Something went wrong", statusCode = 400),
                )
            viewModel.sendWhatsAppOTP(mockOptions, this)
            assert(!viewModel.uiState.value.showLoadingDialog)
            assert(viewModel.uiState.value.phoneNumberState.error == "Something went wrong")
        }
}
