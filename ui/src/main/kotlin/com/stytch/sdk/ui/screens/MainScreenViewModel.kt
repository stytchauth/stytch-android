package com.stytch.sdk.ui.screens

import androidx.activity.ComponentActivity
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.StytchClient.publicToken
import com.stytch.sdk.consumer.network.models.UserType
import com.stytch.sdk.consumer.oauth.OAuth
import com.stytch.sdk.consumer.userManagement.UserManagement
import com.stytch.sdk.ui.AuthenticationActivity.Companion.STYTCH_GOOGLE_OAUTH_REQUEST_ID
import com.stytch.sdk.ui.AuthenticationActivity.Companion.STYTCH_THIRD_PARTY_OAUTH_REQUEST_ID
import com.stytch.sdk.ui.data.ApplicationUIState
import com.stytch.sdk.ui.data.EMLDetails
import com.stytch.sdk.ui.data.EmailMagicLinksOptions
import com.stytch.sdk.ui.data.EmailState
import com.stytch.sdk.ui.data.EventState
import com.stytch.sdk.ui.data.NavigationRoute
import com.stytch.sdk.ui.data.OAuthOptions
import com.stytch.sdk.ui.data.OAuthProvider
import com.stytch.sdk.ui.data.OTPDetails
import com.stytch.sdk.ui.data.OTPMethods
import com.stytch.sdk.ui.data.OTPOptions
import com.stytch.sdk.ui.data.PasswordOptions
import com.stytch.sdk.ui.data.PasswordResetDetails
import com.stytch.sdk.ui.data.PasswordResetType
import com.stytch.sdk.ui.data.PhoneNumberState
import com.stytch.sdk.ui.data.StytchProduct
import com.stytch.sdk.ui.data.StytchProductConfig
import com.stytch.sdk.ui.utils.isValidEmailAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal class MainScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val stytchClient: StytchClient,
) : ViewModel() {
    val uiState = savedStateHandle.getStateFlow(ApplicationUIState.SAVED_STATE_KEY, ApplicationUIState())

    private val _eventFlow = MutableSharedFlow<EventState>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onStartOAuthLogin(
        context: ComponentActivity,
        provider: OAuthProvider,
        productConfig: StytchProductConfig,
        scope: CoroutineScope = viewModelScope,
    ) {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = true)
        if (provider == OAuthProvider.GOOGLE) {
            scope.launch {
                val didStartOneTap =
                    productConfig.googleOauthOptions.clientId?.let { clientId ->
                        stytchClient.oauth.googleOneTap.start(
                            OAuth.GoogleOneTap.StartParameters(
                                context = context,
                                clientId = clientId,
                                oAuthRequestIdentifier = STYTCH_GOOGLE_OAUTH_REQUEST_ID,
                            ),
                        )
                    } ?: false
                if (!didStartOneTap) {
                    // Google OneTap is unavailable, fallback to traditional OAuth
                    onStartThirdPartyOAuth(context, provider = provider, oAuthOptions = productConfig.oAuthOptions)
                }
            }
        } else {
            onStartThirdPartyOAuth(context, provider = provider, oAuthOptions = productConfig.oAuthOptions)
        }
    }

    @VisibleForTesting
    internal fun onStartThirdPartyOAuth(
        context: ComponentActivity,
        provider: OAuthProvider,
        oAuthOptions: OAuthOptions? = null,
    ) {
        val parameters =
            OAuth.ThirdParty.StartParameters(
                context = context,
                oAuthRequestIdentifier = STYTCH_THIRD_PARTY_OAUTH_REQUEST_ID,
                loginRedirectUrl = oAuthOptions?.loginRedirectURL,
                signupRedirectUrl = oAuthOptions?.signupRedirectURL,
            )
        when (provider) {
            OAuthProvider.AMAZON -> stytchClient.oauth.amazon.start(parameters)
            OAuthProvider.APPLE -> stytchClient.oauth.apple.start(parameters)
            OAuthProvider.BITBUCKET -> stytchClient.oauth.bitbucket.start(parameters)
            OAuthProvider.COINBASE -> stytchClient.oauth.coinbase.start(parameters)
            OAuthProvider.DISCORD -> stytchClient.oauth.discord.start(parameters)
            OAuthProvider.FACEBOOK -> stytchClient.oauth.facebook.start(parameters)
            OAuthProvider.FIGMA -> stytchClient.oauth.figma.start(parameters)
            OAuthProvider.GITLAB -> stytchClient.oauth.gitlab.start(parameters)
            OAuthProvider.GITHUB -> stytchClient.oauth.github.start(parameters)
            OAuthProvider.GOOGLE -> stytchClient.oauth.google.start(parameters)
            OAuthProvider.LINKEDIN -> stytchClient.oauth.linkedin.start(parameters)
            OAuthProvider.MICROSOFT -> stytchClient.oauth.microsoft.start(parameters)
            OAuthProvider.SALESFORCE -> stytchClient.oauth.salesforce.start(parameters)
            OAuthProvider.SLACK -> stytchClient.oauth.slack.start(parameters)
            OAuthProvider.SNAPCHAT -> stytchClient.oauth.snapchat.start(parameters)
            OAuthProvider.TIKTOK -> stytchClient.oauth.tiktok.start(parameters)
            OAuthProvider.TWITCH -> stytchClient.oauth.twitch.start(parameters)
            OAuthProvider.TWITTER -> stytchClient.oauth.twitter.start(parameters)
        }
    }

    fun onCountryCodeChanged(countryCode: String) {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
            uiState.value.copy(
                phoneNumberState =
                    PhoneNumberState(
                        countryCode = countryCode,
                        error = null,
                    ),
                genericErrorMessage = null,
            )
    }

    fun onPhoneNumberChanged(phoneNumber: String) {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
            uiState.value.copy(
                phoneNumberState =
                    PhoneNumberState(
                        phoneNumber = phoneNumber,
                        error = null,
                    ),
                genericErrorMessage = null,
            )
    }

    fun onEmailAddressChanged(emailAddress: String) {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
            uiState.value.copy(
                emailState =
                    EmailState(
                        emailAddress = emailAddress,
                        validEmail = emailAddress.isValidEmailAddress(),
                    ),
                genericErrorMessage = null,
            )
    }

    fun onEmailAddressSubmit(
        productConfig: StytchProductConfig,
        scope: CoroutineScope = viewModelScope,
    ) {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = true)
        scope.launch {
            val emailAddress = uiState.value.emailState.emailAddress
            when (getUserType(emailAddress)) {
                UserType.NEW -> NavigationRoute.NewUser
                UserType.PASSWORD -> NavigationRoute.ReturningUser
                UserType.PASSWORDLESS -> {
                    if (
                        productConfig.products.contains(StytchProduct.OTP) &&
                        productConfig.otpOptions.methods.contains(OTPMethods.EMAIL)
                    ) {
                        // send Email OTP
                        sendEmailOTPForReturningUserAndGetNavigationRoute(
                            emailAddress = emailAddress,
                            otpOptions = productConfig.otpOptions,
                        )
                    } else if (productConfig.products.contains(StytchProduct.EMAIL_MAGIC_LINKS)) {
                        // send EML
                        sendEmailMagicLinkForReturningUserAndGetNavigationRoute(
                            emailAddress = emailAddress,
                            emailMagicLinksOptions = productConfig.emailMagicLinksOptions,
                        )
                    } else {
                        // no Email OTP or EML, so set password
                        sendResetPasswordForReturningUserAndGetNavigationRoute(
                            emailAddress = emailAddress,
                            passwordOptions = productConfig.passwordOptions,
                        )
                    }
                }
                else -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                        uiState.value.copy(
                            showLoadingDialog = false,
                            genericErrorMessage = "Failed to get user type",
                        )
                    null
                }
            }?.let {
                _eventFlow.emit(EventState.NavigationRequested(it))
            }
            savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = false)
        }
    }

    suspend fun getUserType(emailAddress: String): UserType? {
        return when (val result = stytchClient.user.search(UserManagement.SearchParams(emailAddress))) {
            is StytchResult.Success -> result.value.userType
            is StytchResult.Error -> null
        }
    }

    suspend fun sendEmailMagicLinkForReturningUserAndGetNavigationRoute(
        emailAddress: String,
        emailMagicLinksOptions: EmailMagicLinksOptions,
    ): NavigationRoute? {
        val parameters =
            emailMagicLinksOptions.toParameters(
                emailAddress = emailAddress,
                publicToken = stytchClient.publicToken,
            )
        return when (val result = stytchClient.magicLinks.email.loginOrCreate(parameters = parameters)) {
            is StytchResult.Success -> {
                NavigationRoute.EMLConfirmation(details = EMLDetails(parameters), isReturningUser = true)
            }
            is StytchResult.Error -> {
                savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                    uiState.value.copy(
                        genericErrorMessage = result.exception.message,
                    ) // TODO
                null
            }
        }
    }

    suspend fun sendEmailOTPForReturningUserAndGetNavigationRoute(
        emailAddress: String,
        otpOptions: OTPOptions,
    ): NavigationRoute? {
        val parameters = otpOptions.toEmailOtpParameters(emailAddress)
        return when (val result = stytchClient.otps.email.loginOrCreate(parameters)) {
            is StytchResult.Success -> {
                NavigationRoute.OTPConfirmation(
                    OTPDetails.EmailOTP(
                        parameters = parameters,
                        methodId = result.value.methodId,
                    ),
                    isReturningUser = true,
                    emailAddress = emailAddress,
                )
            }
            is StytchResult.Error -> {
                savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                    uiState.value.copy(
                        genericErrorMessage = result.exception.message,
                    ) // TODO
                null
            }
        }
    }

    suspend fun sendResetPasswordForReturningUserAndGetNavigationRoute(
        emailAddress: String,
        passwordOptions: PasswordOptions,
    ): NavigationRoute? {
        val parameters =
            passwordOptions.toResetByEmailStartParameters(
                emailAddress = emailAddress,
                publicToken = publicToken,
            )
        return when (val result = stytchClient.passwords.resetByEmailStart(parameters = parameters)) {
            is StytchResult.Success -> {
                NavigationRoute.PasswordResetSent(PasswordResetDetails(parameters, PasswordResetType.NO_PASSWORD_SET))
            }
            is StytchResult.Error -> {
                savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                    uiState.value.copy(
                        genericErrorMessage = result.exception.message,
                    ) // TODO
                null
            }
        }
    }

    fun sendSmsOTP(
        otpOptions: OTPOptions,
        scope: CoroutineScope = viewModelScope,
    ) {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = true)
        scope.launch {
            val phoneNumberState = uiState.value.phoneNumberState
            val parameters = otpOptions.toSMSOtpParameters(phoneNumberState.toE164())
            when (val result = stytchClient.otps.sms.loginOrCreate(parameters)) {
                is StytchResult.Success -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(
                        EventState.NavigationRequested(
                            NavigationRoute.OTPConfirmation(
                                OTPDetails.SmsOTP(
                                    parameters = parameters,
                                    methodId = result.value.methodId,
                                ),
                                isReturningUser = false,
                            ),
                        ),
                    )
                }
                is StytchResult.Error -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                        uiState.value.copy(
                            phoneNumberState =
                                phoneNumberState.copy(
                                    error = result.exception.message,
                                ),
                            showLoadingDialog = false,
                        )
                }
            }
        }
    }

    fun sendWhatsAppOTP(
        otpOptions: OTPOptions,
        scope: CoroutineScope = viewModelScope,
    ) {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = true)
        scope.launch {
            val phoneNumberState = uiState.value.phoneNumberState
            val parameters = otpOptions.toWhatsAppOtpParameters(phoneNumberState.toE164())
            when (val result = stytchClient.otps.whatsapp.loginOrCreate(parameters)) {
                is StytchResult.Success -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(
                        EventState.NavigationRequested(
                            NavigationRoute.OTPConfirmation(
                                OTPDetails.WhatsAppOTP(
                                    parameters = parameters,
                                    methodId = result.value.methodId,
                                ),
                                isReturningUser = false,
                            ),
                        ),
                    )
                }
                is StytchResult.Error -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
                        uiState.value.copy(
                            phoneNumberState =
                                phoneNumberState.copy(
                                    error = result.exception.message,
                                ),
                            showLoadingDialog = false,
                        )
                }
            }
        }
    }

    companion object {
        fun factory(savedStateHandle: SavedStateHandle): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    MainScreenViewModel(
                        stytchClient = StytchClient,
                        savedStateHandle = savedStateHandle,
                    )
                }
            }
    }
}
