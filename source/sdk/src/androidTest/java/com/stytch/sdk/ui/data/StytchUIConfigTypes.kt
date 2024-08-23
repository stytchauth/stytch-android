package com.stytch.sdk.ui.data

import com.stytch.sdk.common.network.models.BootstrapData

internal val DEFAULT_STYTCH_UI_CONFIG =
    StytchUIConfig(
        productConfig = StytchProductConfig(),
        styles = StytchStyles(),
        bootstrapData = BootstrapData(),
    )

internal val REALISTIC_STYTCH_UI_CONFIG =
    StytchUIConfig(
        productConfig =
            StytchProductConfig(
                products =
                    listOf(
                        StytchProduct.OAUTH,
                        StytchProduct.EMAIL_MAGIC_LINKS,
                        StytchProduct.OTP,
                        StytchProduct.PASSWORDS,
                    ),
                emailMagicLinksOptions = EmailMagicLinksOptions(),
                passwordOptions = PasswordOptions(),
                googleOauthOptions = GoogleOAuthOptions(),
                oAuthOptions =
                    OAuthOptions(
                        providers = listOf(OAuthProvider.GOOGLE, OAuthProvider.APPLE, OAuthProvider.GITHUB),
                    ),
                otpOptions =
                    OTPOptions(
                        methods = listOf(OTPMethods.SMS, OTPMethods.WHATSAPP),
                    ),
            ),
        styles = StytchStyles(),
        bootstrapData = BootstrapData(disableSDKWatermark = true),
    )

internal val REALISTIC_STYTCH_UI_CONFIG_NO_PASSWORD =
    StytchUIConfig(
        productConfig =
            StytchProductConfig(
                products =
                    listOf(
                        StytchProduct.OAUTH,
                        StytchProduct.EMAIL_MAGIC_LINKS,
                        StytchProduct.OTP,
                    ),
                emailMagicLinksOptions = EmailMagicLinksOptions(),
                passwordOptions = PasswordOptions(),
                googleOauthOptions = GoogleOAuthOptions(),
                oAuthOptions =
                    OAuthOptions(
                        providers = listOf(OAuthProvider.GOOGLE, OAuthProvider.APPLE, OAuthProvider.GITHUB),
                    ),
                otpOptions =
                    OTPOptions(
                        methods = listOf(OTPMethods.SMS, OTPMethods.WHATSAPP),
                    ),
            ),
        styles = StytchStyles(),
        bootstrapData = BootstrapData(disableSDKWatermark = true),
    )

internal val REALISTIC_STYTCH_UI_CONFIG_EML =
    StytchUIConfig(
        productConfig =
            StytchProductConfig(
                products =
                    listOf(
                        StytchProduct.EMAIL_MAGIC_LINKS,
                        StytchProduct.PASSWORDS,
                    ),
                emailMagicLinksOptions = EmailMagicLinksOptions(),
                passwordOptions = PasswordOptions(),
                googleOauthOptions = GoogleOAuthOptions(),
                oAuthOptions = OAuthOptions(),
                otpOptions =
                    OTPOptions(
                        methods = listOf(OTPMethods.SMS, OTPMethods.WHATSAPP),
                    ),
            ),
        styles = StytchStyles(),
        bootstrapData = BootstrapData(disableSDKWatermark = true),
    )

internal val REALISTIC_STYTCH_UI_CONFIG_EOTP =
    StytchUIConfig(
        productConfig =
            StytchProductConfig(
                products =
                    listOf(
                        StytchProduct.OTP,
                        StytchProduct.PASSWORDS,
                    ),
                emailMagicLinksOptions = EmailMagicLinksOptions(),
                passwordOptions = PasswordOptions(),
                googleOauthOptions = GoogleOAuthOptions(),
                oAuthOptions = OAuthOptions(),
                otpOptions =
                    OTPOptions(
                        methods = listOf(OTPMethods.EMAIL),
                    ),
            ),
        styles = StytchStyles(),
        bootstrapData = BootstrapData(disableSDKWatermark = true),
    )

internal val REALISTIC_STYTCH_UI_CONFIG_PASSWORD_ONLY =
    StytchUIConfig(
        productConfig =
            StytchProductConfig(
                products =
                    listOf(
                        StytchProduct.PASSWORDS,
                    ),
                emailMagicLinksOptions = EmailMagicLinksOptions(),
                passwordOptions = PasswordOptions(),
                googleOauthOptions = GoogleOAuthOptions(),
                oAuthOptions = OAuthOptions(),
                otpOptions = OTPOptions(),
            ),
        styles = StytchStyles(),
        bootstrapData = BootstrapData(disableSDKWatermark = true),
    )

internal val EML_AND_OTP_ERROR_STYTCH_UI_CONFIG =
    StytchUIConfig(
        productConfig =
            StytchProductConfig(
                products = listOf(StytchProduct.EMAIL_MAGIC_LINKS),
                otpOptions =
                    OTPOptions(
                        methods = listOf(OTPMethods.EMAIL),
                    ),
            ),
        styles = StytchStyles(),
        bootstrapData = BootstrapData(),
    )

internal val NO_PASSWORD_EML_OR_OTP_STYTCH_UI_CONFIG =
    StytchUIConfig(
        productConfig =
            StytchProductConfig(
                products = emptyList(),
                otpOptions =
                    OTPOptions(
                        methods = emptyList(),
                    ),
            ),
        styles = StytchStyles(),
        bootstrapData = BootstrapData(),
    )
