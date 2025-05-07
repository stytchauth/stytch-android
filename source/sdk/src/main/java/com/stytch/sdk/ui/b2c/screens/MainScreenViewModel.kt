package com.stytch.sdk.ui.b2c.screens

import androidx.activity.ComponentActivity
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.models.Locale
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.network.models.UserType
import com.stytch.sdk.consumer.oauth.OAuth
import com.stytch.sdk.consumer.userManagement.UserManagement
import com.stytch.sdk.ui.b2c.AuthenticationActivity.Companion.STYTCH_THIRD_PARTY_OAUTH_REQUEST_ID
import com.stytch.sdk.ui.b2c.data.ApplicationUIState
import com.stytch.sdk.ui.b2c.data.EMLDetails
import com.stytch.sdk.ui.b2c.data.EmailMagicLinksOptions
import com.stytch.sdk.ui.b2c.data.EventState
import com.stytch.sdk.ui.b2c.data.NavigationRoute
import com.stytch.sdk.ui.b2c.data.OAuthOptions
import com.stytch.sdk.ui.b2c.data.OAuthProvider
import com.stytch.sdk.ui.b2c.data.OTPDetails
import com.stytch.sdk.ui.b2c.data.OTPMethods
import com.stytch.sdk.ui.b2c.data.OTPOptions
import com.stytch.sdk.ui.b2c.data.PasswordOptions
import com.stytch.sdk.ui.b2c.data.PasswordResetDetails
import com.stytch.sdk.ui.b2c.data.PasswordResetType
import com.stytch.sdk.ui.b2c.data.StytchProduct
import com.stytch.sdk.ui.b2c.data.StytchProductConfig
import com.stytch.sdk.ui.shared.data.EmailState
import com.stytch.sdk.ui.shared.data.EventTypes
import com.stytch.sdk.ui.shared.data.PhoneNumberState
import com.stytch.sdk.ui.shared.utils.isValidEmailAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal enum class ProductComponent {
    BUTTONS,
    BIOMETRICS,
    INPUTS,
    DIVIDER,
}

internal enum class TabTypes {
    EMAIL,
    SMS,
    WHATSAPP,
}

internal class MainScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val stytchClient: StytchClient,
) : ViewModel() {
    val uiState = savedStateHandle.getStateFlow(ApplicationUIState.SAVED_STATE_KEY, ApplicationUIState())

    private val _eventFlow = MutableSharedFlow<EventState>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun getProductComponents(
        products: List<StytchProduct>,
        context: FragmentActivity,
    ): List<ProductComponent> {
        val hasButtons = products.contains(StytchProduct.OAUTH)
        val hasInput =
            products.any {
                listOf(StytchProduct.OTP, StytchProduct.PASSWORDS, StytchProduct.EMAIL_MAGIC_LINKS).contains(it)
            }
        val hasBiometrics = products.contains(StytchProduct.BIOMETRICS)
        val enrolledInBiometrics = StytchClient.biometrics.isRegistrationAvailable(context)
        val hasDivider = (hasButtons || (hasBiometrics && enrolledInBiometrics)) && hasInput
        return mutableListOf<ProductComponent>()
            .apply {
                products.forEachIndexed { index, product ->
                    if (hasDivider && index > 0) {
                        add(ProductComponent.DIVIDER)
                    }
                    if (product == StytchProduct.OAUTH) {
                        add(ProductComponent.BUTTONS)
                    }
                    if (product == StytchProduct.PASSWORDS ||
                        product == StytchProduct.EMAIL_MAGIC_LINKS ||
                        product == StytchProduct.OTP
                    ) {
                        add(ProductComponent.INPUTS)
                    }
                    if (product == StytchProduct.BIOMETRICS && enrolledInBiometrics) {
                        add(ProductComponent.BIOMETRICS)
                    }
                }
            }.toSet()
            .toList()
    }

    fun getTabTitleOrdering(
        products: List<StytchProduct>,
        otpMethods: List<OTPMethods>,
    ): List<TabTypes> {
        val hasEmail =
            products.any {
                listOf(StytchProduct.EMAIL_MAGIC_LINKS, StytchProduct.PASSWORDS).contains(it)
            }
        return mutableListOf<TabTypes>()
            .apply {
                if (hasEmail) {
                    add(TabTypes.EMAIL)
                }
                otpMethods.forEach { method ->
                    when (method) {
                        OTPMethods.SMS -> add(TabTypes.SMS)
                        OTPMethods.EMAIL -> add(TabTypes.EMAIL)
                        OTPMethods.WHATSAPP -> add(TabTypes.WHATSAPP)
                    }
                }
            }.toSet()
            .toList()
    }

    fun onStartOAuthLogin(
        context: ComponentActivity,
        provider: OAuthProvider,
        productConfig: StytchProductConfig,
        scope: CoroutineScope = viewModelScope,
    ) {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = true)
        if (provider == OAuthProvider.GOOGLE) {
            scope.launch {
                val clientId = productConfig.googleOauthOptions.clientId
                clientId?.let {
                    val result =
                        stytchClient.oauth.googleOneTap.start(
                            OAuth.GoogleOneTap.StartParameters(
                                context = context,
                                clientId = clientId,
                            ),
                        )
                    _eventFlow.emit(EventState.Authenticated(result))
                } ?: onStartThirdPartyOAuth(context, provider = provider, oAuthOptions = productConfig.oAuthOptions)
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
                loginRedirectUrl = "${StytchClient.configurationManager.publicToken}://oauth",
                signupRedirectUrl = "${StytchClient.configurationManager.publicToken}://oauth",
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

    private suspend fun handleEMLorEOTPSubmission(
        emailAddress: String,
        productConfig: StytchProductConfig,
    ): NavigationRoute? {
        val hasEML = productConfig.products.contains(StytchProduct.EMAIL_MAGIC_LINKS)
        val hasEOTP =
            productConfig.products.contains(StytchProduct.OTP) &&
                productConfig.otpOptions.methods.contains(OTPMethods.EMAIL)
        return if (hasEOTP) {
            // send Email OTP
            sendEmailOTPForReturningUserAndGetNavigationRoute(
                emailAddress = emailAddress,
                otpOptions = productConfig.otpOptions,
                locale = productConfig.locale,
            )
        } else if (hasEML) {
            // send EML
            sendEmailMagicLinkForReturningUserAndGetNavigationRoute(
                emailAddress = emailAddress,
                emailMagicLinksOptions = productConfig.emailMagicLinksOptions,
                locale = productConfig.locale,
            )
        } else {
            null
        }
    }

    fun onEmailAddressSubmit(
        productConfig: StytchProductConfig,
        scope: CoroutineScope = viewModelScope,
    ) {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = true)
        scope.launch {
            val emailAddress = uiState.value.emailState.emailAddress
            val hasPasswords = productConfig.products.contains(StytchProduct.PASSWORDS)
            val hasEML = productConfig.products.contains(StytchProduct.EMAIL_MAGIC_LINKS)
            val hasEOTP =
                productConfig.products.contains(StytchProduct.OTP) &&
                    productConfig.otpOptions.methods.contains(OTPMethods.EMAIL)
            val nextNavigationRoute =
                if (hasPasswords) {
                    when (getUserType(emailAddress)) {
                        UserType.NEW -> NavigationRoute.NewUser
                        UserType.PASSWORD -> NavigationRoute.ReturningUser
                        UserType.PASSWORDLESS -> {
                            if (hasEOTP || hasEML) {
                                handleEMLorEOTPSubmission(emailAddress, productConfig)
                            } else {
                                // no Email OTP or EML, so set password
                                sendResetPasswordForReturningUserAndGetNavigationRoute(
                                    emailAddress = emailAddress,
                                    passwordOptions = productConfig.passwordOptions,
                                    locale = productConfig.locale,
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
                    }
                } else {
                    handleEMLorEOTPSubmission(emailAddress, productConfig)
                }
            nextNavigationRoute?.let {
                _eventFlow.emit(EventState.NavigationRequested(it))
            }
            savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = false)
        }
    }

    suspend fun getUserType(emailAddress: String): UserType? =
        when (val result = stytchClient.user.search(UserManagement.SearchParams(emailAddress))) {
            is StytchResult.Success -> result.value.userType
            is StytchResult.Error -> null
        }

    suspend fun sendEmailMagicLinkForReturningUserAndGetNavigationRoute(
        emailAddress: String,
        emailMagicLinksOptions: EmailMagicLinksOptions,
        locale: Locale,
    ): NavigationRoute? {
        val parameters =
            emailMagicLinksOptions.toParameters(
                emailAddress = emailAddress,
                publicToken = stytchClient.configurationManager.publicToken,
                locale = locale,
            )
        return when (val result = stytchClient.magicLinks.email.loginOrCreate(parameters = parameters)) {
            is StytchResult.Success -> {
                stytchClient.events.logEvent(
                    eventName = EventTypes.EMAIL_SENT,
                    details =
                        mapOf(
                            "email" to parameters.email,
                            "type" to EventTypes.LOGIN_OR_CREATE_EML,
                        ),
                )
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
        locale: Locale,
    ): NavigationRoute? {
        val parameters = otpOptions.toEmailOtpParameters(emailAddress, locale)
        return when (val result = stytchClient.otps.email.loginOrCreate(parameters)) {
            is StytchResult.Success -> {
                stytchClient.events.logEvent(
                    eventName = EventTypes.EMAIL_SENT,
                    details =
                        mapOf(
                            "email" to parameters.email,
                            "type" to EventTypes.LOGIN_OR_CREATE_OTP,
                        ),
                )
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
        locale: Locale,
    ): NavigationRoute? {
        val parameters =
            passwordOptions.toResetByEmailStartParameters(
                emailAddress = emailAddress,
                publicToken = stytchClient.configurationManager.publicToken,
                locale = locale,
            )
        return when (val result = stytchClient.passwords.resetByEmailStart(parameters = parameters)) {
            is StytchResult.Success -> {
                stytchClient.events.logEvent(
                    eventName = EventTypes.EMAIL_SENT,
                    details =
                        mapOf(
                            "email" to parameters.email,
                            "type" to EventTypes.RESET_PASSWORD,
                        ),
                )
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
        locale: Locale,
        scope: CoroutineScope = viewModelScope,
    ) {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = true)
        scope.launch {
            val phoneNumberState = uiState.value.phoneNumberState
            val parameters = otpOptions.toSMSOtpParameters(phoneNumberState.toE164(), locale)
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
        locale: Locale,
        scope: CoroutineScope = viewModelScope,
    ) {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = true)
        scope.launch {
            val phoneNumberState = uiState.value.phoneNumberState
            val parameters = otpOptions.toWhatsAppOtpParameters(phoneNumberState.toE164(), locale)
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
